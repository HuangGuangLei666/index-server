package com.pl.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.pl.indexserver.model.IntelligentJumpDto;
import com.pl.indexserver.model.IntelligentJumpMap;
import com.pl.indexserver.model.IntelligentJumpMapDto;

public interface IntelligentJumpMapper {

	public IntelligentJumpDto selectByPrimaryKey(Long id);
	 
	   
	public List<IntelligentJumpDto> selectByBusinessId( Long businessId) ;
   
	public boolean insert(IntelligentJumpDto intelligentJumpDto);
	
	public boolean insertMapByBatch(List<IntelligentJumpMap> list);
   
	public boolean update(IntelligentJumpDto intelligentJumpDto) ;

   
 	public boolean deleteById(Long id);  
 	
 	public boolean deleteMapById(Long id);  
 
	
	public boolean insertMap(@Param("id") String id,@Param("type") Long type,@Param("mapId") String mapId)  ;
	 
	   
	public boolean deleteMap(@Param("id") String id,@Param("type") Long type,@Param("mapId") String mapId)  ;
	 
	
 
	
	
	
	
	public List<IntelligentJumpMapDto> selectResponseModeByBusinessId(@Param("id") Long id, @Param("businessId") Long businessId  ) ;
	
	public List<IntelligentJumpMapDto> selectCommByBusinessId( @Param("id") Long id, @Param("businessId") Long businessId  ) ;
	
	public List<IntelligentJumpMapDto> selectKnowledgeByBusinessId(@Param("id") Long id, @Param("businessId") Long businessId ) ;
	
	
	public List<IntelligentJumpMapDto> selectcraftByBusinessId(@Param("id") Long id, @Param("businessId") Long businessId ) ;
	

    
}