package com.pl.indexserver.service;

import com.pl.model.TBusiness;

/**
 * 智库检查Service
 */
public interface CheckBusinessService {

    TBusiness getCheckBusiness(Long businessId, Long companyId);

    //流程节点核查
    String workNodeCheck(TBusiness tBusiness);
    //流程图核查
    String workLinkCheck(TBusiness tBusiness);
    //问答核查
    String knowledgeQACheck(TBusiness tBusiness);
    //系统通用核查
    String systemCommonCheck(TBusiness tBusiness);

}
