package com.pl.indexserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.pl.indexserver.model.ACommonCraftDto;
import com.pl.indexserver.model.TtsProperty;
import com.pl.indexserver.service.ACommonCraftService;
import com.pl.indexserver.service.FileTransferService;
import com.pl.indexserver.service.TTSService;
import com.pl.indexserver.service.UserOperateRecordService;
import com.pl.indexserver.untils.BusinessPropertyType;
import com.pl.indexserver.untils.MD5;
import com.pl.indexserver.untils.RedisClient;
import com.pl.indexserver.untils.TagUtils;
import com.pl.mapper.*;
import com.pl.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class ACommonCraftServiceImpl implements ACommonCraftService {

    @Autowired
    private QCommonCraftMapper qCommonCraftMapper;
    @Autowired
    private ACommonCraftMapper aCommonCraftMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private UploadFileMapper uploadFileMapper;
    @Autowired
    private FileTransferService fileTransferService;
    @Value("${redis.recordUpdateForSIP}")
    private String recordUpdateForSIP;
    @Value("${redis.recordSynthesis}")
    private String recordSynthesis;
    @Autowired
    private TBusinessPropertyMapper tBusinessPropertyMapper;
    @Autowired
    private TTSService ttsService;
    @Autowired
    private UserOperateRecordService userOperateRecordService;


    @Override
    public ACommonCraft selectByPrimaryKey(Long id) {
        return aCommonCraftMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public void updateByPrimaryKeySelective(List<ACommonCraftDto> aCommonCraftDtos) throws Exception {
        if (!CollectionUtils.isEmpty(aCommonCraftDtos)) {
            //转换对象并操作
            for (ACommonCraftDto aCommonCraftDto:aCommonCraftDtos) {
                ACommonCraft aCommonCraft = new ACommonCraft();
                aCommonCraft.setId(aCommonCraftDto.getId());
                aCommonCraft.setRecordFile(aCommonCraftDto.getRecordName());
                aCommonCraftMapper.updateByPrimaryKeySelective(aCommonCraft);
            }
        }
    }

    @Override
    public long countFileSizeByCompanyId(Long companyId) throws Exception {
        Long size = aCommonCraftMapper.countFileSizeByCompanyId(companyId);
        return size == null ? 0 : size;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public boolean insertAndRecardLog(ACommonCraftDto aCommonCraftDto,TmUser user) throws Exception {
        if (StringUtils.isEmpty(aCommonCraftDto.getCraftId())) {
            throw new NullPointerException("话术标识不能为null");
        }
        List<Integer> list = disposeParam(aCommonCraftDto,user);
        aCommonCraftDto.setStatus(0);
        aCommonCraftDto.setCreateDate(new Date());
        int flag = aCommonCraftMapper.insertSelective(aCommonCraftDto);
        if(flag>0&&list.size()>0){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id",aCommonCraftDto.getId());
            jsonObject.put("type",0);
            jsonObject.put("index",list);
            redisClient.lpush(recordSynthesis,jsonObject.toJSONString());
        }
        return flag>0;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public boolean updateAndRecardLogByPrimaryKey(ACommonCraftDto aCommonCraftDto, TmUser user) throws Exception {
        Long id = aCommonCraftDto.getId();
        if (StringUtils.isEmpty(id)) {
            throw new NullPointerException("主键标识不能为null");
        }
        ACommonCraft aCommonCraft = selectByPrimaryKey(id);
        aCommonCraftDto.setFileSizeCount(aCommonCraft.getFileSizeCount());
        List<Integer> list = disposeParam(aCommonCraftDto,user);
        if(list.size()>0){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id",id);
            jsonObject.put("type",0);
            jsonObject.put("index",list);
            redisClient.lpush(recordSynthesis,jsonObject.toJSONString());
        }
        aCommonCraftDto.setModifyDate(new Date());
        int flag = aCommonCraftMapper.updateByPrimaryKeySelective(aCommonCraftDto);
        return flag > 0;
    }

    private List<Integer> disposeParam(ACommonCraftDto aCommonCraftDto,TmUser user) throws Exception {
        List<Integer> list = new ArrayList<>();
        //初始化数据
        Long companyId = aCommonCraftDto.getCompanyId();
        Long businessId = aCommonCraftDto.getBusinessId();
        QCommonCraft qCommonCraft = qCommonCraftMapper.selectByCompanyIdAndCraftId(companyId, aCommonCraftDto.getCraftId(),businessId);
        String title = qCommonCraft.getName();
        if(StringUtils.isEmpty(companyId)){
            throw new Exception("无法获取到当前用户的公司编号!");
        }
        StringBuilder content = new StringBuilder("");
        StringBuilder recordFile = new StringBuilder("");
        StringBuilder recordDescribe = new StringBuilder("");
        StringBuilder record_state = new StringBuilder("");
        StringBuilder fileSizeCount = new StringBuilder("");
        Long fileSize = 0L;
        Integer notRecordingNum = 0;
        Integer recordState = null;
        if (aCommonCraftDto.getBusinessId() == null){
            aCommonCraftDto.setBusinessId(0L);
        }
        List<Map<String, String>> recordDetail = aCommonCraftDto.getRecordDetail();
        String filePath = companyId + "/BUSINESS-"+businessId;
        if (null != recordDetail) {
            Map<String, String> itemMap = null;
            for (int j = 0; j < recordDetail.size(); j++) {
                itemMap = recordDetail.get(j);
                String contentItem = itemMap.get("content").trim().replaceAll("&","");//分段文本内容
                if(StringUtils.isEmpty(contentItem)){
                    continue;
                }
                String fileIdItem = itemMap.get("recordFile");//分段文本对应的录音
                String recordDescribeItem = itemMap.get("recordDescribe");//分段文本对应的录音类型
                content.append(contentItem + "&");

                if (TagUtils.isLabel(contentItem)) {
                    recordFile.append(contentItem + "&");
                    recordDescribe.append("0&");
                    fileSizeCount.append("0&");
                } else {
                    String logContent = null;
                    String fileName = MD5.MD5_32bit(contentItem) + ".wav";// 生成录音文件名
                    if (StringUtils.isEmpty(fileIdItem) ||"0".equals(recordDescribeItem)) {
                        //tts合成音频并上传到ftp
                        TtsProperty ttsPropertyDto = null;
                        TBusinessProperty tBusinessProperty = tBusinessPropertyMapper.selectByBusinessIdAndType(businessId, BusinessPropertyType.BUSINESS_TTS.getCode());
                        if(null!=tBusinessProperty && !StringUtils.isEmpty(tBusinessProperty.getPropertyValue())){
                            ttsPropertyDto = JSONObject.parseObject(tBusinessProperty.getPropertyValue(),TtsProperty.class);
                        }
                        long recordSize = ttsService.createRecordToFTP(contentItem, filePath, fileName,ttsPropertyDto);
                        //如果成功则通知sip端，失败则设置成指定标示符
                        if (recordSize > 0) {
                            recordFile.append(fileName + "&");
                            fileSize += recordSize;
                        } else {
                            recordFile.append("*&");
                            list.add(j);
                        }
                        notRecordingNum+=1;
                        fileSizeCount.append(recordSize+"&");
                        record_state.append("0");
                        recordDescribe.append("0&");
                        //处理相关日志---
                        logContent = "话术标题【"+title+"】【"+contentItem+"】的录音文件更新为TTS合成";
                    } else {
                        if(fileIdItem.contains(".wav")){//处理未修改
                            //获取录音文件名
                            String[] temp_str = fileIdItem.split("/");
                            String[] temp_str1 = temp_str[temp_str.length - 1].split("\\?");
                            String oldFileSizeCount = aCommonCraftDto.getFileSizeCount();
                            String old_itemFileSizeCount = oldFileSizeCount.split("&")[j];

                            fileSize+=Long.valueOf(old_itemFileSizeCount);
                            fileSizeCount.append(old_itemFileSizeCount+"&");
                            recordFile.append(temp_str1[0] + "&");
                            recordDescribe.append(recordDescribeItem+"&");
                            record_state.append("1");
                        }else{
                            // 修改录音文件状态
                            UploadFile uploadFile = uploadFileMapper.selectByPrimaryKey(Long.valueOf(fileIdItem));
                            // 重命名文件
                            Map<String, String> map = new HashMap<>();
                            map.put(uploadFile.getFileName(), fileName);
                            fileTransferService.rnameFilesByFTP(filePath, map);
                            uploadFile.setFileName(fileName);
                            uploadFile.setStatus(1);
                            uploadFile.setModifyDate(new Date());
                            uploadFileMapper.updateByPrimaryKeySelective(uploadFile);
                            recordFile.append(fileName + "&");
                            //累加文件大小
                            fileSize += uploadFile.getFileSize();
                            recordDescribe.append("1&");
                            record_state.append("1");
                            fileSizeCount.append(uploadFile.getFileSize()+"&");
                            logContent = "话术标题【"+title+"】【"+contentItem+"】的录音文件重新上传了";
                        }
                    }
                    userOperateRecordService.insertForRecordFile(logContent,businessId,user);
                }
            }

            if (content.length() <= 0) {
                throw new NullPointerException("文本内容为空!");
            }
        }

        //数据处理
        recordFile.deleteCharAt(recordFile.length() - 1);
        content.deleteCharAt(content.length() - 1);
        recordDescribe.deleteCharAt(recordDescribe.length() - 1);
        fileSizeCount.deleteCharAt(fileSizeCount.length() - 1);
        String temp_recordDescribe = recordDescribe.toString();
        String temp_record_state = record_state.toString();
        if (temp_record_state.contains("0")) {
            if (temp_record_state.contains("1")) {
                recordState = 1;
            } else {
                recordState = 0;
            }
        } else {
            if (temp_record_state.contains("1")) {
                recordState = 2;
            } else {
                recordState = 0;
            }
        }
        aCommonCraftDto.setNotRecordingNum(notRecordingNum);
        aCommonCraftDto.setIsRecord(temp_recordDescribe.contains("1") ? 0 : 1);
        aCommonCraftDto.setContent(content.toString());
        aCommonCraftDto.setRecordFile(recordFile.toString());
        aCommonCraftDto.setRecordDescribe(recordDescribe.toString());
        aCommonCraftDto.setFileSize(fileSize);
        aCommonCraftDto.setFileSizeCount(fileSizeCount.toString());
        aCommonCraftDto.setRecordState(recordState);
        aCommonCraftDto.setIsRecord(1);
        return list;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public boolean deleteByPrimaryKey(Long id, Long companyId) throws Exception {
        int flag = aCommonCraftMapper.deleteByPrimaryKey(id, companyId);
        return flag > 0;
    }

}
