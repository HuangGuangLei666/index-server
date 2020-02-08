package com.pl.indexserver.service;

import java.util.List;

import com.pl.indexserver.model.IntelligentJumpDto;

/**
 * 问答服务类
 */
public interface IntelligentJumpService {

	public IntelligentJumpDto selectByPrimaryKey(Long id ,Long companyId,  Long businessId );
	 
   
	public List<IntelligentJumpDto> selectByBusinessId( Long businessId)  ;

   
	public boolean insert(IntelligentJumpDto intelligentJumpDto)  ;

   
	public boolean update(IntelligentJumpDto intelligentJumpDto)  ;
	
	public boolean deleteById (Long id)  ;
	public boolean deleteMapById (Long id)  ;
	

    
	
	public boolean insertMap(String id,Long type,String mapId)  ;

	   
	 
   
	public boolean deleteMap(String id,Long type,String mapId)  ;
		
	
	

    
   
   }
