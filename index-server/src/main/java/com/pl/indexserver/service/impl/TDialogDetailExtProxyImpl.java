package com.pl.indexserver.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pl.indexserver.model.DialogItemModelDto;
import com.pl.indexserver.service.TDialogDetailExtProxy;
import com.pl.indexserver.untils.CustomRunTimeException;
import com.pl.indexserver.untils.ExceptionContant;
import com.pl.mapper.CallTaskMapper;
import com.pl.mapper.TDialogDetailExtMapper;
import com.pl.model.CallTask;
import com.pl.model.TDialogDetail;
import com.pl.model.TDialogDetailExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TDialogDetailExtProxyImpl implements TDialogDetailExtProxy {

    private static final ThreadLocal<Map<String, String>> THREAD_LOCAL = new ThreadLocal<>();

    @Autowired
    private TDialogDetailExtMapper tDialogDetailExtMapper;

    @Autowired
    private CallTaskMapper callTaskMapper;


    private static void put(String key, String val) {
        Map<String, String> currentThreadMap = THREAD_LOCAL.get();
        if (currentThreadMap == null) {
            currentThreadMap = new HashMap<>();
            THREAD_LOCAL.set(currentThreadMap);
        }
        currentThreadMap.put(key, val);
    }

    private static String get(String key) {
        String value = null;
        Map<String, String> currentThreadMap = THREAD_LOCAL.get();
        if (currentThreadMap != null)
            value = currentThreadMap.get(key);
        return value;
    }

    public String getPostfix() {
        return get("postfix");
    }

    public void setPostfix(String postfix) {
        put("postfix", postfix);
    }

    public void setCallTaskId(long callTaskId) {
        String postfix = getTablePostfix(callTaskId);
        setPostfix(postfix);
    }


    private String getTablePostfix(long taskId) {
        CallTask callTask = callTaskMapper.selectByPrimaryKey(taskId);
        if (null == callTask || callTask.getCreateDate() == null) {
            throw new CustomRunTimeException(ExceptionContant.CallTaskException.CALLTASK_NOTEXIST_ERROR, "外呼任务不存在");
        }
        String patten = "_yyyyMM";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patten);
        return simpleDateFormat.format(callTask.getCreateDate());
    }

    public List<DialogItemModelDto> selectByDialogId(Long taskId, Long dialogId) {
        String postfix = getTablePostfix(taskId);
        TDialogDetailExt tDialogDetailExt = tDialogDetailExtMapper.selectByPrimaryKey(dialogId, postfix);
        ArrayList<DialogItemModelDto> dialogItemModelDtoArrayList = new ArrayList<>();
        if (null != tDialogDetailExt) {
            JSONArray detailRecords = JSON.parseArray(tDialogDetailExt.getDetailRecords());
            DialogItemModelDto dialogItemModelDtoA = null;
            DialogItemModelDto dialogItemModelDtoQ = null;
            for (int i = 0; i < detailRecords.size(); i++) {
                dialogItemModelDtoQ = new DialogItemModelDto();
                dialogItemModelDtoQ.setId(String.valueOf(2 * i));
                String content_machine = detailRecords.getJSONObject(i).getString("content_machine");
//                if(StringUtils.isEmpty(content_machine)){
//                dialogItemModelDtoQ.setContent(content_machine);
//                }else {
//                    dialogItemModelDtoQ.setContent(content_machine.split("\",")[0].replace("\"","").replace("[","").replace("]",""));
//                }
                Object object =new ArrayList<>();
                if(!StringUtils.isEmpty(content_machine)){
                    try{
                        object = JSONObject.parseArray(content_machine);
                    }catch (Exception e){
                        object=content_machine.split("\";");
                    }
                }
                dialogItemModelDtoQ.setContentDetail(object);

                dialogItemModelDtoQ.setParticipant(1);
                dialogItemModelDtoQ.setFile_path(null);
                dialogItemModelDtoQ.setCycleId(detailRecords.getJSONObject(i).getString("cycle_id"));
                dialogItemModelDtoArrayList.add(dialogItemModelDtoQ);

                dialogItemModelDtoA = new DialogItemModelDto();
                dialogItemModelDtoA.setId(String.valueOf(2 * i + 1));
                String content_customer = detailRecords.getJSONObject(i).getString("content_customer");
//                if (StringUtils.isEmpty(content_customer)) {
//                dialogItemModelDtoA.setContent(content_customer);
//                } else {
//                    dialogItemModelDtoA.setContent(content_customer.split("\",")[0].replace("\"", "").replace("[", "").replace("]", ""));
//                }
                Object object1 =new ArrayList<>();
                if(!StringUtils.isEmpty(content_customer)){
                    try{
                        object1 = JSONObject.parseArray(content_customer);
                    }catch (Exception e){
                        object1=content_customer.split("\";");
                    }
                }
                dialogItemModelDtoA.setContentDetail(object1);

                dialogItemModelDtoA.setParticipant(2);
                dialogItemModelDtoA.setFile_path(detailRecords.getJSONObject(i).getString("file_path"));
                dialogItemModelDtoA.setCycleId(detailRecords.getJSONObject(i).getString("cycle_id"));

                JSONObject infoMap = detailRecords.getJSONObject(i).getJSONObject("info_map");
                if (infoMap != null) {
                    dialogItemModelDtoA.setWorkNodeName(infoMap.getString("workNodeName"));
                    dialogItemModelDtoA.setSpeachCraftName(infoMap.getString("speachCraftName"));
                }
                dialogItemModelDtoArrayList.add(dialogItemModelDtoA);
            }
        }
        return dialogItemModelDtoArrayList;
    }

    @Override
    public List<DialogItemModelDto> selectByDialogInId(Long dialogId) {
        TDialogDetailExt tDialogDetailExt = tDialogDetailExtMapper.selectCallInByPrimaryKey(dialogId);
        ArrayList<DialogItemModelDto> dialogItemModelDtoArrayList = new ArrayList<>();
        if (null != tDialogDetailExt) {
            JSONArray detailRecords = JSON.parseArray(tDialogDetailExt.getDetailRecords());
            DialogItemModelDto dialogItemModelDtoA = null;
            DialogItemModelDto dialogItemModelDtoQ = null;
            for (int i = 0; i < detailRecords.size(); i++) {
                dialogItemModelDtoQ = new DialogItemModelDto();
                dialogItemModelDtoQ.setId(String.valueOf(2 * i));
                String content_machine = detailRecords.getJSONObject(i).getString("content_machine");
//                if(StringUtils.isEmpty(content_machine)){
//                dialogItemModelDtoQ.setContent(content_machine);
//                }else {
//                    dialogItemModelDtoQ.setContent(content_machine.split("\",")[0].replace("\"","").replace("[","").replace("]",""));
//                }
                Object object =new ArrayList<>();
                if(!StringUtils.isEmpty(content_machine)){
                    try{
                        object = JSONObject.parseArray(content_machine);
                    }catch (Exception e){
                        object=content_machine.split("\";");
                    }
                }
                dialogItemModelDtoQ.setContentDetail(object);

                dialogItemModelDtoQ.setParticipant(1);
                dialogItemModelDtoQ.setFile_path(null);
                dialogItemModelDtoQ.setCycleId(detailRecords.getJSONObject(i).getString("cycle_id"));
                dialogItemModelDtoArrayList.add(dialogItemModelDtoQ);

                dialogItemModelDtoA = new DialogItemModelDto();
                dialogItemModelDtoA.setId(String.valueOf(2 * i + 1));
                String content_customer = detailRecords.getJSONObject(i).getString("content_customer");
//                if (StringUtils.isEmpty(content_customer)) {
//                dialogItemModelDtoA.setContent(content_customer);
//                } else {
//                    dialogItemModelDtoA.setContent(content_customer.split("\",")[0].replace("\"", "").replace("[", "").replace("]", ""));
//                }
                Object object1 =new ArrayList<>();
                if(!StringUtils.isEmpty(content_customer)){
                    try{
                        object1 = JSONObject.parseArray(content_customer);
                    }catch (Exception e){
                        object1=content_customer.split("\";");
                    }
                }
                dialogItemModelDtoA.setContentDetail(object1);

                dialogItemModelDtoA.setParticipant(2);
                dialogItemModelDtoA.setFile_path(detailRecords.getJSONObject(i).getString("file_path"));
                dialogItemModelDtoA.setCycleId(detailRecords.getJSONObject(i).getString("cycle_id"));

                JSONObject infoMap = detailRecords.getJSONObject(i).getJSONObject("info_map");
                if (infoMap != null) {
                    dialogItemModelDtoA.setWorkNodeName(infoMap.getString("workNodeName"));
                    dialogItemModelDtoA.setSpeachCraftName(infoMap.getString("speachCraftName"));
                }
                dialogItemModelDtoArrayList.add(dialogItemModelDtoA);
            }
        }
        return dialogItemModelDtoArrayList;
    }

    @Override
    public void insertTdialogDetailRocords(Long taskId, TDialogDetailExt tDialogDetailExt) {
        String postfix = getTablePostfix(taskId);
        tDialogDetailExt.setTablePostfix(postfix);
        tDialogDetailExtMapper.insert(tDialogDetailExt);
    }

    @Override
    public TDialogDetailExt selectByDialoginId(Long id) {
        return tDialogDetailExtMapper.selectByDialoginId(id);
    }

    @Override
    public void delDetailById(Long dialogId) {
        tDialogDetailExtMapper.delDetailById(dialogId);
    }

}