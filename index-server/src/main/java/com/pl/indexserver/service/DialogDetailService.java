package com.pl.indexserver.service;


import com.pl.indexserver.model.DialogModelDto;
import com.pl.model.CallTask;
import com.pl.model.TDialog;
import com.pl.model.TDialogDetail;
import com.pl.model.TDialogDetailExt;
import org.apache.ibatis.annotations.Param;

public interface DialogDetailService {


    DialogModelDto queryDetail(String dialogId,Long taskId) throws Exception;

    DialogModelDto queryCallInRecordDetail(String dialogId) throws Exception;




    TDialog getDialogbyPhone(@Param("task_id") Long task_id,
                             @Param("telephone") String telephone);

    int updateDialogStatus(@Param("task_id") Long task_id,
                           @Param("telephone") String telephone,
                           @Param("status") Integer status,
                           @Param("agent_id") Long agent_id);

    int updateTDialog(TDialog tDialog,Long taskId);

    void insertTdialogDetailrecords(Long taskId, TDialogDetailExt tDialogDetailExt);

    CallTask selectByPrimaryKey(Long id);

    int updateAgentStatus(@Param("id") Long id);
}
