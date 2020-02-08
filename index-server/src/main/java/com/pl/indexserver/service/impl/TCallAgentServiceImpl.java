package com.pl.indexserver.service.impl;

import com.pl.indexserver.model.TCallAgentSelectDto;
import com.pl.indexserver.service.TCallAgentService;
import com.pl.indexserver.service.TDialogService;
import com.pl.mapper.TCallAgentMapper;
import com.pl.model.TCallAgent;
import com.pl.model.TCallAgentSelect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TCallAgentServiceImpl implements TCallAgentService {

    @Autowired
    private TCallAgentMapper tCallAgentMapper;

    @Autowired
    private TDialogService tDialogService;

    @Override
    public int deleteByPrimaryKey(Long id) {
        return tCallAgentMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(TCallAgent record) {
        return tCallAgentMapper.insert(record);
    }

    @Override
    public int insertSelective(TCallAgent record) {
        return tCallAgentMapper.insertSelective(record);
    }

    @Override
    public TCallAgent selectByPrimaryKey(Long id) {
        return tCallAgentMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(TCallAgent record) {
        return tCallAgentMapper.updateByPrimaryKeySelective(record);
    }
    
    @Override
	public int batchUpdateCallAgentUsed(List<TCallAgent> callAgentList) {
		return tCallAgentMapper.batchUpdateCallAgentUsed(callAgentList);
	}

    @Override
    public int updateCallAgentUsed(TCallAgent callAgent) {
        return tCallAgentMapper.updateCallAgentUsed(callAgent);
    }

    @Override
    public int updateByPrimaryKey(TCallAgent record) {
        return tCallAgentMapper.updateByPrimaryKey(record);
    }

    @Override
    public List<TCallAgent> getTCallAgentListByCompany_id(Long company_id) {
        return tCallAgentMapper.getTCallAgentListByCompany_id(company_id);
    }

    @Override
    public List<TCallAgent> getTCAllAgentListByupdate(Long company_id, Integer autoAgentType, Integer autoAgentNum) {
        return tCallAgentMapper.getTCAllAgentListByupdate(company_id, autoAgentType, autoAgentNum);
    }
    @Override
    public List<TCallAgentSelectDto> selectTCallAgentSelect(Map<String,Object> map) {
        Long taskId = (Long) map.get("usedtaskid");
        ArrayList<Long> taskIdList = new ArrayList<>();
        ArrayList<Long> agentIdList = new ArrayList<>();
        taskIdList.add(taskId);
        List<TCallAgent> tCallAgentList = tCallAgentMapper.selectByTaskIds(taskIdList);
        HashMap<Long,TCallAgent> tCallAgentMap = new HashMap<>();
        for(TCallAgent tCallAgent:tCallAgentList){
            String agentSatus=(String)map.get("agentSatus");
            if(!StringUtils.isEmpty(agentSatus) && !agentSatus.equals(tCallAgent.getStatus().toString())){
                continue;
            }
            String agentNum = (String)map.get("agentNum");
            if(!StringUtils.isEmpty(agentNum)){
                if( tCallAgent.getAgentNum() == null) {
                    continue;
                }
                if( !tCallAgent.getAgentNum().contains(agentNum)){
                    continue;
                }
            }
            tCallAgentMap.put(tCallAgent.getId(),tCallAgent);
            agentIdList.add(tCallAgent.getId());
        }
        map.put("agentIdList",agentIdList);

        HashMap<Long,TCallAgentSelect> tCallAgentSelectHashMap = new HashMap<>();
        List<TCallAgentSelectDto> dtoList=new ArrayList<>();
        List<TCallAgentSelect> List = new ArrayList<>();
        if (!CollectionUtils.isEmpty(agentIdList)) {
            List=tDialogService.selectTCallAgentSelect(map);
        }
        for (int i=0;i<List.size();i++) {
            tCallAgentSelectHashMap.put(List.get(i).getAgentId(),List.get(i));
        }
		for(Long agentId:tCallAgentMap.keySet()){
            TCallAgent tCallAgent = tCallAgentMap.get(agentId);
            TCallAgentSelect agentSelect = tCallAgentSelectHashMap.get(agentId);
            TCallAgentSelectDto dto=new TCallAgentSelectDto();

             if(tCallAgent.getStatus()==1) {
                dto.setAgentSatus(1);
                dto.setId(agentId);
                dto.setAgentNum(tCallAgent.getAgentNum());
                dto.setCtName("");
            }
            else if(tCallAgent.getStatus()==2) {
                dto.setAgentSatus(2);
                dto.setId(agentId);
                dto.setAgentNum(tCallAgent.getAgentNum());
                dto.setCtName("");
            }else if(tCallAgent.getStatus()==3){
                 dto.setAgentSatus(3);
                 dto.setId(agentId);
                 dto.setAgentNum(tCallAgent.getAgentNum());
                 if(agentSelect ==null || agentSelect.getCtName().equals("")){
                     dto.setCtName("暂无");
                 }else {
                     dto.setCtName(agentSelect.getCtName());
                 }
             } else if(tCallAgent.getStatus()==0){
                     dto.setAgentSatus(0);
                     dto.setId(agentId);
                     dto.setAgentNum(tCallAgent.getAgentNum());
                     dto.setCtName("");
                 }


            dtoList.add(dto);
        }

        return dtoList;
    }

    @Override
    public int countByCompanyId(Long companyId) throws Exception {
        Integer count = tCallAgentMapper.countByCompanyId(companyId);
        return count==null?0:count;
    }

    @Override
    public List<TCallAgent> selectByIdArray(String[] ids) {
        return tCallAgentMapper.selectByIdArray(ids);
    }

    @Override
    public int countByTaskIdAndStatus(Long taskId, Long Status) {
        return tCallAgentMapper.countByTaskIdAndStatus(taskId,Status);
    }

    @Override
    public List<TCallAgent> queryCallinList(Long companyId) {
        return tCallAgentMapper.queryCallinList(companyId);
    }

    @Override
    public List<TCallAgent> queryCallInListByCallInId(Long callInId) {
        return tCallAgentMapper.queryCallInListByCallInId(callInId);
    }

    @Override
    public int selectCountOutnumber(String outNumber) {
        return tCallAgentMapper.selectCountOutnumber(outNumber);
    }

    @Override
    public TCallAgent getCallInNumber() {
        return tCallAgentMapper.getCallInNumber();
    }

    @Override
    public int updateByCompanyAndUsedtaskId(TCallAgent tCallAgent) {
        return tCallAgentMapper.updateByCompanyAndUsedtaskId(tCallAgent);
    }
}
