package com.pl.indexserver.service;

import com.pl.indexserver.model.CallTaskIndexDto;
import com.pl.indexserver.model.ReturnMsg;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IndexService {

   ReturnMsg getIndexPageData(Long companyId, String uid);

   ReturnMsg getIndexPageData2(Integer type, Long companyId, String uid);
}
