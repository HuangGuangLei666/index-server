package com.pl.indexserver.service.impl;

import com.pl.indexserver.model.DialogItemModelDto;
import com.pl.indexserver.model.DialogModelDto;
import com.pl.indexserver.service.*;
import com.pl.mapper.CallTaskMapper;
import com.pl.mapper.TBookMapper;
import com.pl.model.CallTask;
import com.pl.model.TDialog;
import com.pl.model.TDialogDetailExt;
import com.pl.model.TmCustomer;

import com.pl.model.wx.*;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class DialogDetailServiceImpl implements DialogDetailService {

    //录音文件路径格式化
    private static final String FILE_PATH_FORMAT = "%s/%s/TASK-%s/%s/%s";

    private static final Logger log = LoggerFactory.getLogger(DialogDetailServiceImpl.class);


    @Value("${recordings.address}")
    private String filePath;
    @Value("${recordings.ftpPath_local}")
    private String ftpPathLocal;

    @Autowired
    private TDialogDetailExtProxy tDialogDetailExtProxy;

    @Autowired
    private TmCustomerService tmCustomerService;

    @Autowired
    private CallTaskMapper callTaskMapper;

    @Autowired
    private TDialogService tDialogService;
    @Autowired
    private TUserTagService tUserTagService;
    @Autowired
    private TGroupTagService tGroupTagService;
    @Autowired
    private TUserinfoService tUserinfoService;
    @Autowired
    private TBookMapper tBookMapper;


    @Override
    public DialogModelDto queryDetail(String dialogId, Long taskId) throws Exception {
        DialogModelDto queryDetailDto = queryDetail(taskId, Long.parseLong(dialogId));
        String companyId = queryDetailDto.getCompanyId();
        String telephone = queryDetailDto.getTelephone();
        List<List<DialogItemModelDto>> contents = queryDetailDto.getContents();
        List<DialogItemModelDto> temp = queryDetailDto.getTemp();
        //用于中转数据
        List<DialogItemModelDto> dialogItemModelDtos = new ArrayList<>();
        String detailFilePath;
        String dingDingWavFileCreateDateString = "";
        String recordPath = queryDetailDto.getRecordPath();
        if (!StringUtils.isEmpty(recordPath)) {
            if (recordPath.endsWith(".wav")) {
                recordPath = recordPath.replace(ftpPathLocal, "");
                detailFilePath = filePath + recordPath;
                //https://ai.yousayido.net/recordManagement/recordings/2019-01-23/13686862953.8888.10-49-22.6a261ee9-70c0-45f8-870b-e87ee311658d
                dingDingWavFileCreateDateString = recordPath.substring(recordPath.indexOf("/20") + 1);
                dingDingWavFileCreateDateString = dingDingWavFileCreateDateString.substring(0, 10);
                log.info("=====dingDingWavFileCreateDateString=" + dingDingWavFileCreateDateString);
                detailFilePath = detailFilePath.substring(0, detailFilePath.length() - 4);
            } else {
                detailFilePath = String.format(FILE_PATH_FORMAT, filePath, queryDetailDto.getCompanyId(), queryDetailDto.getTaskId(), queryDetailDto.getTelephone(), recordPath);
                //detailFilePath = filePath + "/" + queryDetailDto.getCompanyId() + "/TASK-" + queryDetailDto.getTaskId() + "/" + queryDetailDto.getTelephone() + "/" + recordPath;
            }
        } else {
            detailFilePath = "";
        }
        log.info("detailFilePath=" + detailFilePath);
        //区分对话轮次
        for (int i = 0; i < temp.size(); i++) {
            DialogItemModelDto queryDetailItemDto = temp.get(i);
            String cycleId = queryDetailItemDto.getCycleId();
            String id = queryDetailItemDto.getId();
            //处理当前对象录音文件地址
            String itemFilePath;
            String file_path = queryDetailItemDto.getFile_path();
            if (!StringUtils.isEmpty(file_path)) {
                if (file_path.endsWith(".wav")) {
                    file_path = file_path.replace(ftpPathLocal, "");
                    itemFilePath = filePath + file_path;
                    itemFilePath = itemFilePath.substring(0, itemFilePath.length() - 4);
                } else {
                    itemFilePath = String.format(FILE_PATH_FORMAT, filePath, queryDetailDto.getCompanyId(), queryDetailDto.getTaskId(), queryDetailDto.getTelephone(), queryDetailItemDto.getFile_path());
                    //itemFilePath = filePath + "/" + queryDetailDto.getCompanyId() + "/TASK-" + queryDetailDto.getTaskId() + "/" + queryDetailDto.getTelephone() + "/" + queryDetailItemDto.getFile_path();
                }
            } else {
                itemFilePath = "";
            }
            log.info("=============itemFilePath=" + itemFilePath);

            queryDetailItemDto.setFile_path(itemFilePath);
            //添加到中转List
            dialogItemModelDtos.add(queryDetailItemDto);
            for (int j = 0; j < temp.size(); j++) {
                DialogItemModelDto dto = temp.get(j);
                String cycleId1 = dto.getCycleId();
                String id1 = dto.getId();
                //如果两者的轮标识不一致或者id值一致则跳过
                if (!cycleId.equals(cycleId1) || id.equals(id1)) {
                    continue;
                }
                //拼凑当前对象录音文件完整地址
                String itemFilePath1;
                String file_path1 = dto.getFile_path();
                if (!StringUtils.isEmpty(file_path1)) {
                    log.info("======  file_path1=" + file_path1);

                    itemFilePath1 = String.format(FILE_PATH_FORMAT, filePath, companyId, queryDetailDto.getTaskId(), telephone, dto.getFile_path());

                    if (file_path1.indexOf(".wav") == -1) {
                        log.info("======old plat=======itemFilePath1=" + itemFilePath1);

                    } else {
                        String dateString = dingDingWavFileCreateDateString;

                        String dateString1 = dateString.replaceAll("-", "");
                        log.info("=============queryDetailDto.getCreateDate()=" + queryDetailDto.getCreateDate()
                                + "=====dateString=" + dateString + "=====dateString1=" + dateString1);
                        String fullFilePath = filePath + "/recordings/" + dateString + "/";
                        ///var/www/html/recordManagement/recording_sections/20190119
                        String partFilePath = filePath + "/recording_sections/" + dateString1 + "/";
                        itemFilePath1 = dto.getFile_path();
                        if (itemFilePath1.indexOf(".wav") > 0) {
                            itemFilePath1 = itemFilePath1.substring(0, itemFilePath1.indexOf(".wav"));
                        }
                        if (itemFilePath1.indexOf(dateString) > 0) {
                            itemFilePath1 = itemFilePath1.substring(itemFilePath1.indexOf(dateString) + 12);
                        }
                        if (itemFilePath1.indexOf("_") > 0) {
                            itemFilePath1 = partFilePath + itemFilePath1;
                        } else {
                            itemFilePath1 = fullFilePath + itemFilePath1;
                        }
                    }
                } else {
                    itemFilePath1 = "";
                }
                log.info("=============itemFilePath1=" + itemFilePath1);
                dto.setFile_path(itemFilePath1);
                //添加到中转List
                dialogItemModelDtos.add(dto);
                //将当前数据从清除，避免重复数据
                temp.remove(j);
            }
            contents.add(dialogItemModelDtos);
            //将中转List重置准备下一轮
            dialogItemModelDtos = new ArrayList<>();
        }
        queryDetailDto.setDialogNum(queryDetailDto.getContents().size());

        String intentionLevel = queryDetailDto.getIntentionLevel();
        if ("E".equals(intentionLevel) || "e".equals(intentionLevel)) {
            queryDetailDto.setIntentionLevel("无意向");
        }
        queryDetailDto.setRecordPath(detailFilePath);
        queryDetailDto.setTemp(null);
        TmCustomer tmCustomer = tmCustomerService.selectByTelephoneAndCompanyId(telephone, companyId);
        if (null != tmCustomer) {
            queryDetailDto.setCtname(tmCustomer.getCtname());
        }
        //处理状态
        queryDetailDto.setStatusDetail();
        return queryDetailDto;
    }

    @Override
    public TDialog getDialogbyPhone(Long task_id, String telephone) {
        return tDialogService.getDialogbyPhone(task_id, telephone);
    }

    @Override
    public int updateDialogStatus(Long task_id, String telephone, Integer status, Long agent_id) {
        return tDialogService.updateDialogStatus(task_id, telephone, status, agent_id);
    }

    public int updateTDialog(TDialog tDialog, Long taskId) {
        return tDialogService.updateTDialog(tDialog, taskId);
    }

    @Override
    public void insertTdialogDetailrecords(Long taskId, TDialogDetailExt tDialogDetailExt) {
        tDialogDetailExtProxy.insertTdialogDetailRocords(taskId, tDialogDetailExt);
    }

    @Override
    public CallTask selectByPrimaryKey(Long id) {
        return callTaskMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateAgentStatus(Long id) {
        return callTaskMapper.updateAgentStatus(id);
    }


    private DialogModelDto queryDetail(Long taskId, Long dialogId) {
        DialogModelDto dialogModelDto = new DialogModelDto();
        TDialog tDialog = tDialogService.getDialogbyId(taskId, dialogId);
        dialogModelDto.setTaskName(tDialog.getTaskName());
        dialogModelDto.setTaskId(tDialog.getTaskId().toString());
        dialogModelDto.setStatus(tDialog.getStatus() == null ? "" : tDialog.getStatus().toString());
        dialogModelDto.setRecordPath(tDialog.getFile_path());
        dialogModelDto.setCompanyId(tDialog.getCompanyId() == null ? "" : tDialog.getCompanyId().toString());
        dialogModelDto.setTaskId(tDialog.getTaskId().toString());
        dialogModelDto.setTelephone(tDialog.getTelephone());
        dialogModelDto.setBeginDate(tDialog.getBeginDate());
        dialogModelDto.setIntentionLevel(tDialog.getIntentionLevel());
        dialogModelDto.setFocusLevel(tDialog.getFocusLevel());
        dialogModelDto.setIntentLevel(tDialog.getIntentLevel());
        String patten = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patten);
        dialogModelDto.setCreateDate(tDialog.getCreateDate() == null ? "" : simpleDateFormat.format(tDialog.getCreateDate()));
        dialogModelDto.setTotalSeconds(tDialog.getTotal_seconds() == null ? 0 : tDialog.getTotal_seconds());
        List<DialogItemModelDto> dialogDetailList = tDialogDetailExtProxy.selectByDialogId(taskId, dialogId);
        dialogModelDto.getTemp().addAll(dialogDetailList);
        return dialogModelDto;
    }

    @Override
    public DialogModelDto queryCallInRecordDetail(String dialogId) throws Exception {
        DialogModelDto queryDetailDto = queryCallInRecordDetail(Long.parseLong(dialogId));
        String companyId = queryDetailDto.getCompanyId();
        String telephone = queryDetailDto.getTelephone();
        List<List<DialogItemModelDto>> contents = queryDetailDto.getContents();
        List<DialogItemModelDto> temp = queryDetailDto.getTemp();
        //用于中转数据
        List<DialogItemModelDto> dialogItemModelDtos = new ArrayList<>();


        String detailFilePath;
        String dingDingWavFileCreateDateString = "";
        String recordPath = queryDetailDto.getRecordPath();
        if (!StringUtils.isEmpty(recordPath)) {
            if (recordPath.endsWith(".wav")) {
                recordPath = recordPath.replace(ftpPathLocal, "");
                detailFilePath = filePath + recordPath;
                //https://ai.yousayido.net/recordManagement/recordings/2019-01-23/13686862953.8888.10-49-22.6a261ee9-70c0-45f8-870b-e87ee311658d
                dingDingWavFileCreateDateString = recordPath.substring(recordPath.indexOf("/20") + 1);
                dingDingWavFileCreateDateString = dingDingWavFileCreateDateString.substring(0, 10);
                log.info("=====dingDingWavFileCreateDateString=" + dingDingWavFileCreateDateString);

                detailFilePath = detailFilePath.substring(0, detailFilePath.length() - 4);

            } else {
                detailFilePath = String.format(FILE_PATH_FORMAT, filePath, queryDetailDto.getCompanyId(), queryDetailDto.getTaskId(), queryDetailDto.getTelephone(), recordPath);
                //detailFilePath = filePath + "/" + queryDetailDto.getCompanyId() + "/TASK-" + queryDetailDto.getTaskId() + "/" + queryDetailDto.getTelephone() + "/" + recordPath;
            }
        } else {
            detailFilePath = "";
        }

        log.info("detailFilePath=" + detailFilePath);

        //区分对话轮次
        for (int i = 0; i < temp.size(); i++) {
            DialogItemModelDto queryDetailItemDto = temp.get(i);
            String cycleId = queryDetailItemDto.getCycleId();
            String id = queryDetailItemDto.getId();
            //处理当前对象录音文件地址
            String itemFilePath;
            String file_path = queryDetailItemDto.getFile_path();
            if (!StringUtils.isEmpty(file_path)) {
                if (file_path.endsWith(".wav")) {
                    file_path = file_path.replace(ftpPathLocal, "");
                    itemFilePath = filePath + file_path;
                    itemFilePath = itemFilePath.substring(0, itemFilePath.length() - 4);
                } else {
                    itemFilePath = String.format(FILE_PATH_FORMAT, filePath, queryDetailDto.getCompanyId(), queryDetailDto.getTaskId(), queryDetailDto.getTelephone(), queryDetailItemDto.getFile_path());
                    //itemFilePath = filePath + "/" + queryDetailDto.getCompanyId() + "/TASK-" + queryDetailDto.getTaskId() + "/" + queryDetailDto.getTelephone() + "/" + queryDetailItemDto.getFile_path();
                }
            } else {
                itemFilePath = "";
            }
            log.info("=============itemFilePath=" + itemFilePath);

            queryDetailItemDto.setFile_path(itemFilePath);
            //添加到中转List
            dialogItemModelDtos.add(queryDetailItemDto);
            for (int j = 0; j < temp.size(); j++) {
                DialogItemModelDto dto = temp.get(j);
                String cycleId1 = dto.getCycleId();
                String id1 = dto.getId();
                //如果两者的轮标识不一致或者id值一致则跳过
                if (!cycleId.equals(cycleId1) || id.equals(id1)) {
                    continue;
                }
                //拼凑当前对象录音文件完整地址
                String itemFilePath1;
                String file_path1 = dto.getFile_path();
                if (!StringUtils.isEmpty(file_path1)) {


                    log.info("======  file_path1=" + file_path1);


                    itemFilePath1 = String.format(FILE_PATH_FORMAT, filePath, companyId, queryDetailDto.getTaskId(), telephone, dto.getFile_path());


                    if (file_path1.indexOf(".wav") == -1) {


                        log.info("======old plat=======itemFilePath1=" + itemFilePath1);

                    } else {
                        String dateString = dingDingWavFileCreateDateString;

                        String dateString1 = "";
                        if (dateString != null) {
                            dateString1 = dateString.replaceAll("-", "");
                        }

                        log.info("=============queryDetailDto.getCreateDate()=" + queryDetailDto.getCreateDate()
                                + "=====dateString=" + dateString + "=====dateString1=" + dateString1);

                        String fullFilePath = filePath + "/recordings/" + dateString + "/";
                        ///var/www/html/recordManagement/recording_sections/20190119
                        String partFilePath = filePath + "/recording_sections/" + dateString1 + "/";

                        itemFilePath1 = dto.getFile_path();


                        if (itemFilePath1.indexOf(".wav") > 0) {
                            itemFilePath1 = itemFilePath1.substring(0, itemFilePath1.indexOf(".wav"));
                        }

                        if (itemFilePath1.indexOf(dateString) > 0) {
                            itemFilePath1 = itemFilePath1.substring(itemFilePath1.indexOf(dateString) + 12);
                        }

                        if (itemFilePath1.indexOf("_") > 0) {
                            itemFilePath1 = partFilePath + itemFilePath1;
                        } else {
                            itemFilePath1 = fullFilePath + itemFilePath1;
                        }

                    }

                } else {
                    itemFilePath1 = "";
                }
                log.info("=============itemFilePath1=" + itemFilePath1);
                dto.setFile_path(itemFilePath1);
                //添加到中转List
                dialogItemModelDtos.add(dto);
                //将当前数据从清除，避免重复数据
                temp.remove(j);
            }
            contents.add(dialogItemModelDtos);
            //将中转List重置准备下一轮
            dialogItemModelDtos = new ArrayList<>();
        }
        queryDetailDto.setDialogNum(queryDetailDto.getContents().size());

        String intentionLevel = queryDetailDto.getIntentionLevel();
        if ("E".equals(intentionLevel) || "e".equals(intentionLevel)) {
            queryDetailDto.setIntentionLevel("无意向");
        }
        queryDetailDto.setRecordPath(detailFilePath);
        queryDetailDto.setTemp(null);
        TmCustomer tmCustomer = tmCustomerService.selectByTelephoneAndCompanyId(telephone, companyId);
        if (null != tmCustomer) {
            queryDetailDto.setCtname(tmCustomer.getCtname());
        }
        //处理状态
        queryDetailDto.setStatusDetail();
        return queryDetailDto;

    }

    private DialogModelDto queryCallInRecordDetail(Long dialogId) {
        DialogModelDto dialogModelDto = new DialogModelDto();
        TDialog tDialog = tDialogService.getDialogInbyId(dialogId);

        //通话详情增加号码类型
        String phoneType = "";
        //初始化好友姓名
        String friendName = "";
        //通讯录分类
        String groupName = "";
        Long taskId = tDialog.getTaskId();
        TUserinfo tUserinfo = tUserinfoService.selectByPhone(taskId+"");
        if (!StringUtils.isEmpty(tUserinfo)){
            //通话详情显示好友姓名
            TBook tBook = tBookMapper.selectByPhoneAndOpenid(tDialog.getTelephone(), tUserinfo.getOpenid());
            if (!StringUtils.isEmpty(tBook)){
                friendName = tBook.getFriendName();
            }

            //通讯录分类
            TTagGroup tTagGroup = tGroupTagService.selectByOpenidPhone(tUserinfo.getOpenid(),tDialog.getTelephone());
            if (!StringUtils.isEmpty(tTagGroup)){
                groupName = tTagGroup.getName();
            }

            String telephone = tDialog.getTelephone();
            TUserTag tUserTag = tUserTagService.selectByPhone(tUserinfo.getOpenid(),telephone);
            //号码类型，先从t_user_tag表查询，没有在查t_group_tag表
            if (!StringUtils.isEmpty(tUserTag)){
                phoneType = tUserTag.getTagName();
            }else {
                TGroupTag tGroupTag = tGroupTagService.selectByOpenidAndPhone(tUserinfo.getOpenid(),telephone);
                if (!StringUtils.isEmpty(tGroupTag)){
                    phoneType = tGroupTag.getTagName();
                }
            }
        }

        dialogModelDto.setTaskName(tDialog.getTaskName());
        dialogModelDto.setTaskId(tDialog.getTaskId().toString());
        dialogModelDto.setStatus(tDialog.getStatus() == null ? "" : tDialog.getStatus().toString());
        dialogModelDto.setRecordPath(tDialog.getFile_path());
        dialogModelDto.setCompanyId(tDialog.getCompanyId() == null ? "" : tDialog.getCompanyId().toString());
        dialogModelDto.setTaskId(tDialog.getTaskId().toString());
        dialogModelDto.setTelephone(tDialog.getTelephone());
        dialogModelDto.setBeginDate(tDialog.getBeginDate());
        dialogModelDto.setIntentionLevel(tDialog.getIntentionLevel());
        dialogModelDto.setFocusLevel(tDialog.getFocusLevel());
        dialogModelDto.setIntentLevel(tDialog.getIntentLevel());
        dialogModelDto.setPhoneType(phoneType);
        dialogModelDto.setFriendName(friendName);
        //通话详情显示通讯录分类
        dialogModelDto.setGroupName(groupName);
        String patten = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patten);
        dialogModelDto.setCreateDate(tDialog.getCreateDate() == null ? "" : simpleDateFormat.format(tDialog.getCreateDate()));
        dialogModelDto.setTotalSeconds(tDialog.getTotal_seconds() == null ? 0 : tDialog.getTotal_seconds());
        List<DialogItemModelDto> dialogDetailList = tDialogDetailExtProxy.selectByDialogInId(dialogId);
        dialogModelDto.getTemp().addAll(dialogDetailList);
        return dialogModelDto;
    }
}
