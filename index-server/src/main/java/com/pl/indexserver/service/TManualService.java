package com.pl.indexserver.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.pl.model.TManual;

import java.util.Date;

public interface TManualService {

    Page<TManual> getTManualListOfPage(int pageIndex, int pageSize ,String phone, Long companyId, String uid, Integer status, Date beginDate, Date endDate);

    TManual getTmanualByPhone(Integer taskId, Long companyId, String phone);

    TManual getTmanualByPriKey(Integer id);

    int updateTmanual(TManual manual);

    int insertTmanual(TManual manual);

    int deleteTmanual(Integer id);

}
