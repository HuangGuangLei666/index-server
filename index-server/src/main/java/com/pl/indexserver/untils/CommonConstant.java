package com.pl.indexserver.untils;

/**
 * @author liuqiongyu
 * @date 2018/04/27
 */
public interface CommonConstant {
    /**
     * token请求头名称
     */
    String REQ_TOKEN = "token";

    /**
     * GET请求标识
     */
    String GET = "GET";

    String PROJECT_FILE_DIR = "project/";

    /**
     * 编码
     */
    String UTF8 = "UTF-8";

    /**
     * JSON 资源
     */
    String CONTENT_TYPE = "application/json; charset=utf-8";

    String ZERO = "0";
    
    //*******************************   ResponseMode,CommonCraft相关常量   ************************************
    //状态
    public final static int STATUS_0 = 0;//正常
    public final static int STATUS_1 = 1;//删除
    //相关权限
    public final static int FLAG_0 = 0;//用户创建的并可自由操作的
    public final static int FLAG_1 = 1;//系统创建的不可操作的
    public final static int FLAG_2 = 2;//系统创建的不可删除但可编辑的

    public final static int TYPE_1 = 1;//问题类型1
    public final static int TYPE_2 = 2;//问题类型2

    //触发类型
    public final static int RULETYPE_1 = 1000;//关键字触发


    //***********************************   UserOperateRecord 相关常量   **************************************
    public final static int CALLTASK = 1;//外呼任务
    public final static int FLOWCONFIG = 2;//智能配置

    public final static String INITIAL_BUSINESSID = "0";//通用businessId
    //是否录音
    public final static int IS_RECORD = 1;//已录音
    public final static int NUT_RECORD = 0;//未录音

    //  操作结果
    public final static String OPERATION_SUCCEED = "===>result : true";
    public final static String OPERATION_DEFEATED = "===>result : false";

    public final static String CREATE_TASK = "申请任务";
    public final static String CREATION_TASK = "创建任务";
    public final static String PUBLISH_TASK = "发布任务";
    public final static String START_TASK = "开始任务";
    public final static String PAUSE_TASK = "暂停任务";
    public final static String FINISH_TASK = "结束任务";
    public final static String DELTEL_TASK = "删除任务";
    public final static String IMPORT_FILE = "导入文件";
    public final static String ADD_PHONE = "添加号码";

    public final static String CREATE_RESP = "新增响应方式";
    public final static String MODIFY_RESP = "修改响应方式";
    public final static String DELETE_RESP = "删除响应方式";

    public final static String CREATE_COMMONCRAFT = "新增通用话术";
    public final static String MODIFY_COMMONCRAFT = "修改通用话术";
    public final static String DELETE_COMMONCRAFT = "删除通用话术";

    public final static String CREATE_KNOWLEDGE = "新增知识库问答";
    public final static String MODIFY_KNOWLEDGE = "修改知识库问答";
    public final static String DELETE_KNOWLEDGE = "删除知识库问答";
    public final static String MODIFY_KNOWLEDGE_ANSWEr = "修改知识库问答答案";
    public final static String DELETE_KNOWLEDGE_ANSWER = "删除知识库问答答案";

    public final static String CREATE_SPEECHCRAFT = "新增专用话术";
    public final static String MODIFY_SPEECHCRAFT = "修改专用话术";
    public final static String DELETE_SPEECHCRAFT = "删除专用话术";
    public final static String DELETE_SPEECHCRAFT_CONETNT = "删除专用话术回答";

    public final static String UPLOADRECIRD = "上传录音文件";
    public final static String UPLOADRECIRD_COMMONCRAFT = "上传通用话术录音文件";
    public final static String UPLOADRECIRD_KNOWLEDGE = "上传知识库问答录音文件";
    public final static String UPLOADRECIRD_SPEECHCRAFT = "上传专有话术录音文件";

    public final static String CREATE_WORKFLOW = "新增工作流程";
    public final static String MODIFY_WORKFLOW = "修改工作流程";
    public final static String DELETE_WORKFLOW = "删除工作流程";

    public final static String CREATE_WORKFLOWUI = "新增工作流程UI参数";
    public final static String MODIFY_WORKFLOWUI = "修改工作流程UI参数";

    public final static String CREATE_WORKFLOWNODE = "新增工作节点";
    public final static String MODIFY_WORKFLOWNODE = "修改工作节点";
    public final static String DELETE_WORKFLOWNODE = "删除工作节点";

    public final static String CREATE_WORKFLOWLINK = "新增工作节点链接";
    public final static String MODIFY_WORKFLOWLINK = "修改工作节点链接";
    public final static String DELETE_WORKFLOWLINK = "删除工作节点链接";

    public final static String CREATE_RESPONSEMODE = "新增响应方式";

    public final static String CREATE_BUSINESS = "新增智库方案";
    public final static String MODIFY_BUSINESS = "修改智库方案";
    
    public final static int IS_VALIDATE = 1;
    public final static int NOT_VALIDATE = 0;


    //*******************************  返回状态码  *****************************************
    public final static int RETURN_SUCCEED = 0;
    public final static int RETURN_DEFEATED_1 = 1;
    public final static int RETURN_NOPERMISSION = 1;//无权限
    public final static int RETURN_DIALOGISNULL = 1001;//无通话记录


}
