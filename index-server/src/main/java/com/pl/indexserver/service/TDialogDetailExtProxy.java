package com.pl.indexserver.service;

import com.pl.indexserver.model.DialogItemModelDto;
import com.pl.model.TDialogDetail;
import com.pl.model.TDialogDetailExt;

import java.util.List;

public interface TDialogDetailExtProxy {

    List<DialogItemModelDto>  selectByDialogId(Long taskId,Long dialogId);

    List<DialogItemModelDto>  selectByDialogInId(Long dialogId);

    void insertTdialogDetailRocords(Long taskId,TDialogDetailExt tDialogDetailExt);

    TDialogDetailExt selectByDialoginId(Long id);

    void delDetailById(Long dialogId);
}