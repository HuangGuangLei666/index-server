package com.pl.indexserver.model;

import java.util.List;

import com.pl.model.DialogWorkflow;
import com.pl.model.IntelligentJump;
import com.pl.model.WorkflowNode;

public class IntelligentJumpDto extends IntelligentJump{

	private Long responseModeCount;
	
	private Long knowledgeQuestionCount;
  
	private Long commCount;
	
	private String workflowNodeName;
	
	
	private String knowledgeQuestionString;
	  
	private String commString;
	
	private String responseModeString;
	
	
	
	
	private String knowledgeQuestionCheckedString;
	  
	private String commCheckedString;
	
	private String responseModeCheckedString;

	private String craftCheckedString;
	
	
	private List<IntelligentJumpMapDto> responseModeList;
	
	private List<IntelligentJumpMapDto> knowledgeQuestionList;
	
	
	private List<IntelligentJumpMapDto> commList;
	
	private List<IntelligentJumpMapDto> craftList;
	
	
	private  List<DialogWorkflow> dialogWorkflowList;
	

	public Long getResponseModeCount() {
		return responseModeCount;
	}

	public void setResponseModeCount(Long responseModeCount) {
		this.responseModeCount = responseModeCount;
	}

	public Long getKnowledgeQuestionCount() {
		return knowledgeQuestionCount;
	}

	public void setKnowledgeQuestionCount(Long knowledgeQuestionCount) {
		this.knowledgeQuestionCount = knowledgeQuestionCount;
	}

	public Long getCommCount() {
		return commCount;
	}

	public void setCommCount(Long commCount) {
		this.commCount = commCount;
	}

	public String getWorkflowNodeName() {
		return workflowNodeName;
	}

	public void setWorkflowNodeName(String workflowNodeName) {
		this.workflowNodeName = workflowNodeName;
	}

 

	public String getKnowledgeQuestionString() {
		return knowledgeQuestionString;
	}

	public void setKnowledgeQuestionString(String knowledgeQuestionString) {
		this.knowledgeQuestionString = knowledgeQuestionString;
	}

	public String getCommString() {
		return commString;
	}

	public void setCommString(String commString) {
		this.commString = commString;
	}

 

	public String getResponseModeString() {
		return responseModeString;
	}

	public void setResponseModeString(String responseModeString) {
		this.responseModeString = responseModeString;
	}

 
 

	public String getKnowledgeQuestionCheckedString() {
		return knowledgeQuestionCheckedString;
	}

	public void setKnowledgeQuestionCheckedString(
			String knowledgeQuestionCheckedString) {
		this.knowledgeQuestionCheckedString = knowledgeQuestionCheckedString;
	}

	public String getCommCheckedString() {
		return commCheckedString;
	}

	public void setCommCheckedString(String commCheckedString) {
		this.commCheckedString = commCheckedString;
	}

	public String getResponseModeCheckedString() {
		return responseModeCheckedString;
	}

	public void setResponseModeCheckedString(String responseModeCheckedString) {
		this.responseModeCheckedString = responseModeCheckedString;
	}

	public List<IntelligentJumpMapDto> getResponseModeList() {
		return responseModeList;
	}

	public void setResponseModeList(List<IntelligentJumpMapDto> responseModeList) {
		this.responseModeList = responseModeList;
	}

	public List<IntelligentJumpMapDto> getKnowledgeQuestionList() {
		return knowledgeQuestionList;
	}

	public void setKnowledgeQuestionList(
			List<IntelligentJumpMapDto> knowledgeQuestionList) {
		this.knowledgeQuestionList = knowledgeQuestionList;
	}

	public List<IntelligentJumpMapDto> getCommList() {
		return commList;
	}

	public void setCommList(List<IntelligentJumpMapDto> commList) {
		this.commList = commList;
	}

	public List<DialogWorkflow> getDialogWorkflowList() {
		return dialogWorkflowList;
	}

	public void setDialogWorkflowList(List<DialogWorkflow> dialogWorkflowList) {
		this.dialogWorkflowList = dialogWorkflowList;
	}

	public String getCraftCheckedString() {
		return craftCheckedString;
	}

	public void setCraftCheckedString(String craftCheckedString) {
		this.craftCheckedString = craftCheckedString;
	}

	public List<IntelligentJumpMapDto> getCraftList() {
		return craftList;
	}

	public void setCraftList(List<IntelligentJumpMapDto> craftList) {
		this.craftList = craftList;
	}

 
	
    
   
}
