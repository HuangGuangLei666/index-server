package com.pl.indexserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.pl.indexserver.model.KnowledgeAnswerDto;
import com.pl.indexserver.model.TtsProperty;
import com.pl.indexserver.service.FileTransferService;
import com.pl.indexserver.service.KnowledgeAnswerService;
import com.pl.indexserver.service.TTSService;
import com.pl.indexserver.service.UserOperateRecordService;
import com.pl.indexserver.untils.BusinessPropertyType;
import com.pl.indexserver.untils.MD5;
import com.pl.indexserver.untils.TagUtils;
import com.pl.indexserver.untils.RedisClient;
import com.pl.mapper.*;
import com.pl.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class KnowledgeAnswerServiceImpl implements KnowledgeAnswerService {

    @Autowired
    private KnowledgeAnswerMapper knowledgeAnswerMapper;
    @Autowired
    private KnowledgeQuestionMapper knowledgeQuestionMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private UploadFileMapper uploadFileMapper;
    @Autowired
    private TBusinessPropertyMapper tBusinessPropertyMapper;
    @Autowired
    private FileTransferService fileTransferService;
    @Value("${redis.recordUpdateForSIP}")
    private String recordUpdateForSIP;
    @Value("${redis.recordSynthesis}")
    private String recordSynthesis;
    @Autowired
    private TTSService ttsService;
    @Autowired
    private UserOperateRecordService userOperateRecordService;
    @Autowired
    private TBusinessMapper tBusinessMapper;


    @Override
    public KnowledgeAnswer selectByPrimaryKey(Long id) throws Exception {
        return knowledgeAnswerMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public boolean insertAndRecardLog(KnowledgeAnswerDto knowledgeAnswerDto, TmUser user) throws Exception {
        if (StringUtils.isEmpty(knowledgeAnswerDto.getKnowledgeId())) {
            throw new NullPointerException("话术标识不能为null");
        }
        List<Integer> list = disposeParam(knowledgeAnswerDto,user);
        knowledgeAnswerDto.setStatus(0);
        knowledgeAnswerDto.setCreateDate(new Date());
        int flag = knowledgeAnswerMapper.insertSelective(knowledgeAnswerDto);
        tBusinessMapper.updateModifyDateById(knowledgeAnswerDto.getBusinessId());
        if (flag > 0 && list.size() > 0) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", knowledgeAnswerDto.getId());
            jsonObject.put("type", 2);
            jsonObject.put("index", list);
            redisClient.lpush(recordSynthesis, jsonObject.toJSONString());
        }
        return flag > 0;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public boolean updateAndRecardLogByPrimaryKey(KnowledgeAnswerDto knowledgeAnswerDto, TmUser user) throws Exception {
        Long id = knowledgeAnswerDto.getId();
        if (StringUtils.isEmpty(id)) {
            throw new NullPointerException("主键不能为null");
        }
        KnowledgeAnswer old_knowledgeAnswerDto = selectByPrimaryKey(id);
        knowledgeAnswerDto.setFileSizeCount(old_knowledgeAnswerDto.getFileSizeCount());
        List<Integer> list = disposeParam(knowledgeAnswerDto,user);
        knowledgeAnswerDto.setModifyDate(new Date());
        int flag = knowledgeAnswerMapper.updateByPrimaryKeySelective(knowledgeAnswerDto);
        tBusinessMapper.updateModifyDateById(knowledgeAnswerDto.getBusinessId());
        if (flag > 0 && list.size() > 0) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", knowledgeAnswerDto.getId());
            jsonObject.put("type", 2);
            jsonObject.put("index", list);
            redisClient.lpush(recordSynthesis, jsonObject.toJSONString());
        }
        return flag > 0;
    }

    private List<Integer> disposeParam(KnowledgeAnswerDto knowledgeAnswerDto, TmUser user) throws Exception {
        List<Integer> list = new ArrayList<>();
        //初始化数据
        Long companyId = knowledgeAnswerDto.getCompanyId();
        KnowledgeQuestion knowledgeQuestion = knowledgeQuestionMapper.selectByCompanyIdAndKnowledgeId(companyId, knowledgeAnswerDto.getKnowledgeId());
        String title = knowledgeQuestion.getName();
        if(StringUtils.isEmpty(companyId)){
            throw new Exception("无法获取到当前用户的公司编号!");
        }
        StringBuilder content = new StringBuilder();
        StringBuilder recordFile = new StringBuilder();
        StringBuilder recordDescribe = new StringBuilder();
        StringBuilder fileSizeCount = new StringBuilder();
        //辅助处理状态
        StringBuilder record_state = new StringBuilder();
        Long fileSize = 0L;
        int notRecordingNum = 0;
        int recordState ;
        Long businessId = knowledgeAnswerDto.getBusinessId();
        List<Map<String, String>> recordDetail = knowledgeAnswerDto.getRecordDetail();
        String filePath = companyId + "/BUSINESS-" + businessId;
        if (recordDetail.size()>0) {
            Map<String, String> itemMap;
            for (int j = 0; j < recordDetail.size(); j++) {
                itemMap = recordDetail.get(j);
                String contentItem = itemMap.get("content").trim().replaceAll("&","");//分段文本内容
                if(StringUtils.isEmpty(contentItem)){
                    continue;
                }
                //分段文本对应的录音
                String fileIdItem = itemMap.get("recordFile");
                //分段文本对应的录音类型
                String recordDescribeItem = itemMap.get("recordDescribe");
                content.append(contentItem).append("&");
                if (TagUtils.isLabel(contentItem)) {
                    recordFile.append(contentItem).append("&");
                    recordDescribe.append("0&");
                    fileSizeCount.append("0&");
                    record_state.append("2");
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
                        long recordSize = ttsService.createRecordToFTP(contentItem, filePath, fileName, ttsPropertyDto);
                        //如果成功则通知sip端，失败则设置成指定标示符
                        if (recordSize > 0) {
                            //累加文件大小
                            fileSize += recordSize;
                            recordFile.append(fileName).append("&");
                        } else {
                            recordFile.append("*&");
                            list.add(j);
                        }
                        notRecordingNum += 1;
                        fileSizeCount.append(recordSize).append("&");
                        recordDescribe.append("0&");
                        record_state.append("0");
                        //处理相关日志---
                        logContent = "话术标题【"+title+"】【"+contentItem+"】的录音文件更新为TTS合成";
                    } else {
                        if(fileIdItem.contains(".wav")){//处理未修改
                            //获取录音文件名
                            String[] temp_str = fileIdItem.split("/");
                            String[] temp_str1 = temp_str[temp_str.length - 1].split("\\?");
                            String old_fileSizeCount = knowledgeAnswerDto.getFileSizeCount();
                            String old_itemFileSizeCount = old_fileSizeCount.split("&")[j];

                            fileSize+=Long.valueOf(old_itemFileSizeCount);
                            fileSizeCount.append(old_itemFileSizeCount).append("&");
                            recordFile.append(temp_str1[0]).append("&");
                            recordDescribe.append(recordDescribeItem).append("&");
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
                            //累加文件大小
                            fileSize += uploadFile.getFileSize();
                            fileSizeCount.append(uploadFile.getFileSize()).append("&");
                            recordFile.append(fileName).append("&");
                            recordDescribe.append("1&");
                            record_state.append("1");
                            logContent = "话术标题【"+title+"】【"+contentItem+"】的录音文件重新上传了";
                        }
                    }
                    userOperateRecordService.insertForRecordFile(logContent,businessId,user);
                }
            }
            if (content.length() <= 0) {
                throw new NullPointerException("文本内容为null!");
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
        knowledgeAnswerDto.setNotRecordingNum(notRecordingNum);
        knowledgeAnswerDto.setIsRecord(temp_recordDescribe.contains("1") ? 0 : 1);
        knowledgeAnswerDto.setAnswer(content.toString());
        knowledgeAnswerDto.setRecordFile(recordFile.toString());
        knowledgeAnswerDto.setRecordDescribe(recordDescribe.toString());
        knowledgeAnswerDto.setFileSize(fileSize);
        knowledgeAnswerDto.setFileSizeCount(fileSizeCount.toString());
        knowledgeAnswerDto.setRecordState(recordState);
        knowledgeAnswerDto.setIsRecord(1);
        return list;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public boolean delete(Long id) throws Exception {
        KnowledgeAnswer knowledgeAnswer = selectByPrimaryKey(id);
        int flag = knowledgeAnswerMapper.deleteByPrimaryKey(id);
        tBusinessMapper.updateModifyDateById(knowledgeAnswer.getBusinessId());
        return flag>0;
    }

    @Override
    public List<KnowledgeAnswer> selectByKnowledgeId(String knowledgeId) {
        return knowledgeAnswerMapper.selectAnswerByKnowledgeId(knowledgeId);
    }

    @Override
    public KnowledgeAnswer selectByAnswerIdAndKnowledgeId(Long answerId, String knowledgeId) {
        return knowledgeAnswerMapper.selectByAnswerIdAndKnowledgeId(answerId,knowledgeId);
    }
}
