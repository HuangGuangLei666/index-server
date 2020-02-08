package com.pl.indexserver.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pl.indexserver.model.IntelligentJumpDto;
import com.pl.indexserver.model.IntelligentJumpMap;
import com.pl.indexserver.model.IntelligentJumpMapDto;
import com.pl.indexserver.service.IntelligentJumpService;
import com.pl.mapper.DialogWorkflowMapper;
import com.pl.mapper.IntelligentJumpMapper;
import com.pl.model.DialogWorkflow;

@Service
public class IntelligentJumpServiceImpl implements IntelligentJumpService {

    private static final Logger logger = LoggerFactory.getLogger(IntelligentJumpServiceImpl.class);

  
    @Autowired
    private IntelligentJumpMapper intelligentJumpMapper;
   
    @Autowired
    private DialogWorkflowMapper dialogWorkflowMapper;
    
    
	public IntelligentJumpDto selectByPrimaryKey(Long id ,Long companyId,  Long businessId ) 
	  
	{
		IntelligentJumpDto intelligentJumpDto;
		if (id > 0)
		{
			intelligentJumpDto = intelligentJumpMapper.selectByPrimaryKey(id);
		
			if (intelligentJumpDto!=null)
			{
				intelligentJumpDto.setResponseModeList(intelligentJumpMapper.selectResponseModeByBusinessId(id,businessId) );
			
				intelligentJumpDto.setCommList(intelligentJumpMapper.selectCommByBusinessId(id,businessId) );
				
				intelligentJumpDto.setKnowledgeQuestionList(intelligentJumpMapper.selectKnowledgeByBusinessId(id,businessId) );
				
				intelligentJumpDto.setCraftList(intelligentJumpMapper.selectcraftByBusinessId(id,businessId) );
				
				
				intelligentJumpDto.setResponseModeCheckedString(getAndString(intelligentJumpDto.getResponseModeList()));
				
				intelligentJumpDto.setCommCheckedString(getAndString(intelligentJumpDto.getCommList()));
				
				intelligentJumpDto.setKnowledgeQuestionCheckedString(getAndString(intelligentJumpDto.getKnowledgeQuestionList()));
			
				intelligentJumpDto.setCraftCheckedString(getAndString(intelligentJumpDto.getCraftList()));
				
						}
		}
		else
		{
			intelligentJumpDto = new IntelligentJumpDto ();
			
			intelligentJumpDto.setResponseModeList(intelligentJumpMapper.selectResponseModeByBusinessId(id,businessId) );
			
			intelligentJumpDto.setCommList(intelligentJumpMapper.selectCommByBusinessId(id,businessId) );
			
			intelligentJumpDto.setKnowledgeQuestionList(intelligentJumpMapper.selectKnowledgeByBusinessId(id,businessId) );
			 
			intelligentJumpDto.setCraftList(intelligentJumpMapper.selectcraftByBusinessId(id,businessId) );
			
			
			
		}
		List<DialogWorkflow> dialogWorkflowList =  dialogWorkflowMapper.getDialogWorkFlowList(companyId , businessId);
		 
		DialogWorkflow d = new DialogWorkflow();
		d.setId(1L);
		d.setName( "挂&nbsp;机");
		dialogWorkflowList.add(0,d);
//		d = new DialogWorkflow();
//		d.setId(3L);
//		d.setName( "跳至原主流程");
//		dialogWorkflowList.add(0,d);
		
		
		intelligentJumpDto.setDialogWorkflowList(dialogWorkflowList);

		
		
		return intelligentJumpDto;
		
	}
	
	
	 
	
	
	private String getAndString(List<IntelligentJumpMapDto>  list  )
	{
		StringBuffer buf=new StringBuffer("");
		
		for (IntelligentJumpMapDto l : list)
		{
			//if ( l.getIsChecked()!=null )
			{
				buf.append(l.getName()+"&");
			}
		}
		String ret = buf.toString();
		if(ret.endsWith("&"))
		{
			ret = ret.substring(0,ret.length()-1);
		}
		
		return ret;
	}
	 
	   
	public List<IntelligentJumpDto> selectByBusinessId( Long businessId) 
	{
		return intelligentJumpMapper.selectByBusinessId(businessId);
	}
	
	
	public boolean deleteById (Long id) 
	{
		intelligentJumpMapper.deleteMapById(id);
		return intelligentJumpMapper.deleteById(id);
	}
	
	public boolean deleteMapById (Long id) 
	{
		return intelligentJumpMapper.deleteMapById(id);
	}

   
	public boolean insert(IntelligentJumpDto intelligentJumpDto)
	{
		
		boolean ret = intelligentJumpMapper.insert(intelligentJumpDto);
		
		logger.info("==============" + intelligentJumpDto.getId());
		
		List<IntelligentJumpMap> list = new ArrayList<IntelligentJumpMap>();
		
		String s = intelligentJumpDto.getResponseModeCheckedString();
		if (s!=null)
		{
			String tmp[] = s.split("&");
			for (String id : tmp)
			{
				if (id.length() > 0)
				{
					IntelligentJumpMap o = new IntelligentJumpMap();
					o.setType(1L);
					o.setIntelligentJumpId(intelligentJumpDto.getId());
					o.setMapId(  id );
					list.add(o);
				}
			}
		}
		
		
		s = intelligentJumpDto.getCommCheckedString();
		if (s!=null)
		{
			String tmp[] = s.split("&");
			for (String id : tmp)
			{
				if (id.length() > 0)
				{
					IntelligentJumpMap o = new IntelligentJumpMap();
					o.setType(2L);
					o.setIntelligentJumpId(intelligentJumpDto.getId());
					o.setMapId( id );
					list.add(o);
				}
			}
		}
		
		
		
		s = intelligentJumpDto.getKnowledgeQuestionCheckedString();
		if (s!=null)
		{
			String tmp[] = s.split("&");
			for (String id : tmp)
			{
				if (id.length() > 0)
				{
					IntelligentJumpMap o = new IntelligentJumpMap();
					o.setType(3L);
					o.setIntelligentJumpId(intelligentJumpDto.getId());
					o.setMapId(  id  );
					list.add(o);
				}
			}
		}
		
		s = intelligentJumpDto.getCraftCheckedString();
		if (s!=null)
		{
			String tmp[] = s.split("&");
			for (String id : tmp)
			{
				if (id.length() > 0)
				{
					IntelligentJumpMap o = new IntelligentJumpMap();
					o.setType(4L);
					o.setIntelligentJumpId(intelligentJumpDto.getId());
					o.setMapId(  id  );
					list.add(o);
				}
			}
		}
	 
		if (list.size()>0)
		{
			ret &= intelligentJumpMapper.insertMapByBatch(list);
	 
		} 
		return  ret;
	}

   
	public boolean update(IntelligentJumpDto intelligentJumpDto) 
	{
		
		
		List<IntelligentJumpMap> list = new ArrayList<IntelligentJumpMap>();
		
		String s = intelligentJumpDto.getResponseModeCheckedString();
		if (s!=null)
		{
			String tmp[] = s.split("&");
			for (String id : tmp)
			{
				if (id.length() > 0)
				{
					IntelligentJumpMap o = new IntelligentJumpMap();
					o.setType(1L);
					o.setIntelligentJumpId(intelligentJumpDto.getId());
					o.setMapId( id  );
					list.add(o);
				}
			}
		}
		
		
		s = intelligentJumpDto.getCommCheckedString();
		if (s!=null)
		{
			String tmp[] = s.split("&");
			for (String id : tmp)
			{
				if (id.length() > 0)
				{
					IntelligentJumpMap o = new IntelligentJumpMap();
					o.setType(2L);
					o.setIntelligentJumpId(intelligentJumpDto.getId());
					o.setMapId(  id  );
					list.add(o);
				}
			}
		}
		
		
		
		s = intelligentJumpDto.getKnowledgeQuestionCheckedString();
		if (s!=null)
		{
			String tmp[] = s.split("&");
			for (String id : tmp)
			{
				if (id.length() > 0)
				{
					IntelligentJumpMap o = new IntelligentJumpMap();
					o.setType(3L);
					o.setIntelligentJumpId(intelligentJumpDto.getId());
					o.setMapId(  id  );
					list.add(o);
				}
			}
		}
		s = intelligentJumpDto.getCraftCheckedString();
		if (s!=null)
		{
			String tmp[] = s.split("&");
			for (String id : tmp)
			{
				if (id.length() > 0)
				{
					IntelligentJumpMap o = new IntelligentJumpMap();
					o.setType(4L);
					o.setIntelligentJumpId(intelligentJumpDto.getId());
					o.setMapId(  id  );
					list.add(o);
				}
			}
		}
		intelligentJumpMapper.deleteMapById(intelligentJumpDto.getId());
		
		if (list.size()>0)
		{
			intelligentJumpMapper.insertMapByBatch(list);
	 
		}
		
		return intelligentJumpMapper.update(intelligentJumpDto);
	}

    
	
	public boolean insertMap(String id,Long type,String mapId) 
	{
		return intelligentJumpMapper.insertMap(id, type, mapId);
	}

	   
	 
	   
	public boolean deleteMap(String id,Long type,String mapId) 
	{
		return intelligentJumpMapper.deleteMap(id, type, mapId) ;
	}

    
    
}
