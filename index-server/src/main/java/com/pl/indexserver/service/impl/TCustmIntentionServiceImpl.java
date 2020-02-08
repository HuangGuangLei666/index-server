package com.pl.indexserver.service.impl;

import com.pl.indexserver.model.TCustmIntentionDto;
import com.pl.indexserver.query.BaseQuery;
import com.pl.indexserver.query.Page;
import com.pl.indexserver.query.TCustmIntentionQuery;
import com.pl.indexserver.service.TCustmIntentionService;
import com.pl.mapper.TCustmIntentionMapper;
import com.pl.model.TCustmIntention;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TCustmIntentionServiceImpl implements TCustmIntentionService{

    @Autowired
    private TCustmIntentionMapper tCustmIntentionMapper;

    @Override
    public List<TCustmIntention> selectByQuery(BaseQuery baseQuery) throws Exception {
        return tCustmIntentionMapper.selectByQuery(baseQuery);
    }

    @Override
    public Long countByQuery(BaseQuery baseQuery) throws Exception {
        return tCustmIntentionMapper.countByQuery(baseQuery);
    }

    @Override
    public Page<TCustmIntentionDto> selectByBusinessIdAndContent(TCustmIntentionQuery tCustmIntentionQuery) throws Exception {
        Page<TCustmIntentionDto> tCustmIntentionDtoPage = new Page<>();
        Long businessId = tCustmIntentionQuery.getBusinessId();
        if(null==businessId){
            return tCustmIntentionDtoPage;
        }
        int count = tCustmIntentionMapper.countByTaskIdAndContent(tCustmIntentionQuery);
        List<TCustmIntentionDto> tCustmIntentionDtos = tCustmIntentionMapper.selectByTaskIdAndContent(tCustmIntentionQuery);
        tCustmIntentionDtoPage.setTotal(count);
        tCustmIntentionDtoPage.setRecords(tCustmIntentionDtos);
        return tCustmIntentionDtoPage;
    }
}
