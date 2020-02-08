package com.pl.indexserver.web;

import com.pl.indexserver.model.ReturnMsg;
import com.pl.indexserver.model.SpecialtyTalkDto;
import com.pl.indexserver.model.SpeechcraftModelDto;
import com.pl.indexserver.model.TDialogModelDto;
import com.pl.indexserver.service.SpeechcraftService;
import com.pl.indexserver.service.TBusinessFocusService;
import com.pl.indexserver.service.UserOperateRecordService;
import com.pl.indexserver.untils.*;
import com.pl.model.Speechcraft;
import com.pl.model.TmUser;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/busiManagement")
public class SpeechcraftContorller {

    private static final Logger logger = LoggerFactory.getLogger(SpeechcraftContorller.class);

    @Autowired
    private SpeechcraftService speechcraftService;
    @Value("${recordings.address}")
    private String filePath;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private UserOperateRecordService userOperateRecordService;
    @Autowired
    private TBusinessFocusService tBusinessFocusService;


    /**
     * @api {GET} /busiManagement/flowConfig/speechcraft/query 话术管理-查询单个知识库所有话术信息;
     * @apiName query
     * @apiGroup SpeechcraftContorller
     * @apiParam{Long} businessId 业务标识
     * @apiParam{String} name 话术名称或流程节点名称
     * @apiSuccess {String} code 0:成功 其他:错误码
     * @apiSuccess {String} content  返回专用话术列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": [
     * {
     * "craftId": "话术标识",
     * "name": "话术标题",
     * "flowName": "流程节点名称",
     * "modifyDate": "更新时间"
     * }
     * ],
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @GetMapping(value = "/flowConfig/speechcraft/query")
    public ReturnMsg querySpeechcraft(HttpServletRequest request,
                                      @RequestParam("businessId") Long businessId, @RequestParam(value = "name", required = false) String name) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            TmUser user = GetUid.getUID(request, redisClient);
            Map<String, Object> map = new HashMap<>();
            map.put("businessId", businessId);
            map.put("companyId",user.getCompanyId());
            if (null != name) {
                name = name.replace("\"", "");
            }
            map.put("name", name);
            List<SpecialtyTalkDto> dtoList = speechcraftService.selectSpeechcraftAllForName(map);
            returnMsg.setCode(0);
            returnMsg.setContent(dtoList);
        } catch (Exception e) {
            logger.error("专用话术列表查询异常:  ", e);
            returnMsg.setCode(1);
            returnMsg.setErrorMsg("请求异常！");

        }
        return returnMsg;

    }


    /**
     * @api {GET} /busiManagement/flowConfig/speechcraft/deleteContent 话术管理-删除单个知识库话术的单条回答;
     * @apiName deleteContent
     * @apiGroup SpeechcraftContorller
     * @apiParam{String} id 主键id
     * @apiParam{String} businessId 智库id
     * @apiSuccess {String} code 0:成功 其他:错误码
     * @apiSuccess {String} content  无
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": "",
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @RequestMapping(value = "/flowConfig/speechcraft/deleteContent")
    public ReturnMsg deleteSpeechcraft(HttpServletRequest request, @RequestParam("id") Long id, @RequestParam("businessId") Long businessId) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            boolean flag = false;
            TmUser user = GetUid.getUID(request, redisClient);
            Speechcraft oldObj = speechcraftService.selectByPrimaryKey(id);
            if (!StringUtils.isEmpty(user)) {

                flag = speechcraftService.deleteById(id);
            }
            if (flag) {
                returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            } else {
                throw new Exception();
            }

            StringBuilder remark = new StringBuilder(id.toString());
            if (flag) {
                remark.append(CommonConstant.OPERATION_SUCCEED);
            } else {
                remark.append(CommonConstant.OPERATION_DEFEATED);
            }
            // 操作日志处理
            String opInfo = String.format("话术标题:%s", oldObj.getName());
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.FLOWCONFIG, businessId.toString(), "speechcraft/deleteContent",
                    CommonConstant.DELETE_SPEECHCRAFT_CONETNT, remark.toString(), OperateType.DELETE, opInfo, null, null);
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            returnMsg.setErrorMsg("删除失败!");
        }
        return returnMsg;
    }

    /**
     * @api {GET} /busiManagement/flowConfig/speechcraft/delete 话术管理-删除单个知识库话术;
     * @apiName delete
     * @apiGroup SpeechcraftContorller
     * @apiParam{String} craftId 专用话术标识
     * @apiParam{String} businessId 智库id
     * @apiSuccess {String} code 0:成功 其他:错误码
     * @apiSuccess {String} content  无
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": "",
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @RequestMapping(value = "/flowConfig/speechcraft/delete")
    public ReturnMsg deleteSpeechcraft(HttpServletRequest request, @RequestParam("craftId") String craftId, @RequestParam("businessId") Long businessId) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            boolean flag = false;
            TmUser user = GetUid.getUID(request, redisClient);
            SpeechcraftModelDto oldObj = speechcraftService.selectDtoByCraftId(craftId.replace("\"", ""), false);
            if (!StringUtils.isEmpty(user)) {
                flag = speechcraftService.deleteByCraftId(craftId.replace("\"", ""));
            }
            if (flag) {
                returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            } else {
                throw new Exception();
            }

            StringBuilder remark = new StringBuilder(craftId.toString());
            if (flag) {
                remark.append(CommonConstant.OPERATION_SUCCEED);
            } else {
                remark.append(CommonConstant.OPERATION_DEFEATED);
            }
            // 操作日志处理
            String opInfo = String.format("话术标题:%s", oldObj.getName());
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.FLOWCONFIG, businessId.toString(), "speechcraft/delete",
                    CommonConstant.DELETE_SPEECHCRAFT, remark.toString(), OperateType.DELETE, opInfo, null, null);
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
        }
        return returnMsg;
    }

    /**
     * @api {POST} /busiManagement/flowConfig/speechcraft/createToContentRelevant 话术管理-新增专用话术回答相关信息
     * @apiName createToContentRelevant
     * @apiGroup SpeechcraftContorller
     * @apiParam {SpeechcraftDto} speechcraftDto  专用话术(不包括录音)，示例如下：
     * @apiParamExample {json} 专用话术输入
     * {
     * "craftId":"话术标识"，
     * "content":"话术内容"，
     * "businessId":"智库id"，
     * "recordDetail":[{
     * "content":"文本内容",
     * "recordName":录音文件id,
     * "recordDescribe":录音文件类型(0:tts,1:手动上传)
     * }]
     * }
     * @apiSuccess {String} code 0:新增成功 1:新增失败
     * @apiSuccess {String} content  无
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": 主键id,
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     **/
    @RequestMapping(value = "/flowConfig/speechcraft/createToContentRelevant")
    public ReturnMsg createSpeechcraftToContentRelevant(HttpServletRequest request,
                                                        @RequestBody SpeechcraftModelDto speechcraftModelDto) {
        ReturnMsg returnMsg = new ReturnMsg();
        TmUser user = GetUid.getUID(request, redisClient);
        Long companyId = user.getCompanyId();
        try {
            speechcraftModelDto.setCompanyId(companyId);
            Long businessId = speechcraftModelDto.getBusinessId();
            Boolean flag = speechcraftService.insertAndRecardLogToContentRelevant(speechcraftModelDto,user);
            //插入操作日志
//            UserOperateRecord userOperateRecord = new UserOperateRecord();
//            userOperateRecord.setUserId(user.getUserid());
//            userOperateRecord.setCompanyId(companyId);
//            userOperateRecord.setObjectType(CommonConstant.FLOWCONFIG);
//            userOperateRecord.setObject(businessId.toString());
//            userOperateRecord.setOperateId("");
//            userOperateRecord.setOperateName(CommonConstant.CREATE_SPEECHCRAFT);
//            StringBuilder remark = new StringBuilder("craftId=" + speechcraftModelDto.getCraftId());
//            if (flag) {
//                remark.append(CommonConstant.OPERATION_SUCCEED);
//            } else {
//                remark.append(CommonConstant.OPERATION_DEFEATED);
//            }
//            userOperateRecord.setRemark(remark.toString());
//            userOperateRecord.setCreateDate(new Date());
//            userOperateRecordService.insert(userOperateRecord);

            if (flag) {
                returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
                returnMsg.setContent(speechcraftModelDto.getId());
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            returnMsg.setErrorMsg("请求异常!");
            logger.error("新增专用话术(回答相关信息)异常:  ", e);
        }
        return returnMsg;

    }

    /**
     * @api {POST} /busiManagement/flowConfig/speechcraft/createToBasic 话术管理-新增专用话术(基础信息)
     * @apiName createToBasic
     * @apiGroup SpeechcraftContorller
     * @apiParam {SpeechcraftDto} speechcraftDto  专用话术(不包括录音)，示例如下：
     * @apiParamExample {json} 专用话术输入
     * {
     * "craftId":"话术标识"，
     * "name":"话术问题名"，
     * "businessId":"智库id"，
     * "msgtemplId":"短信模板id，当为0时表示不发送短信"，
     * "score":"得分",
     * "foucs":"关注点"
     * }
     * @apiSuccess {String} code 0:新增成功 1:新增失败
     * @apiSuccess {String} content  无
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": "话术标识craftId",
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     **/
    @RequestMapping(value = "/flowConfig/speechcraft/createToBasic")
    public ReturnMsg createSpeechcraftToBasic(HttpServletRequest request,
                                              @RequestBody SpeechcraftModelDto speechcraftModelDto) {
        ReturnMsg returnMsg = new ReturnMsg();
        TmUser user = GetUid.getUID(request, redisClient);
        Long companyId = user.getCompanyId();
        try {
            speechcraftModelDto.setCompanyId(companyId);
            Long businessId = speechcraftModelDto.getBusinessId();
            String craftId = UUID.randomUUID().toString().replace("-", "").toLowerCase();//uuid
            speechcraftModelDto.setCraftId(craftId);
            SpeechcraftModelDto operateObj = new SpeechcraftModelDto();
            BeanUtils.copyProperties(speechcraftModelDto, operateObj);
            operateObj.setFoucs(tBusinessFocusService.convertFocusNamesToFocusIds(user, businessId, operateObj.getFoucs()));
            Boolean flag = speechcraftService.insertBasicByDto(operateObj);
            StringBuilder remark = new StringBuilder("craftId=" + craftId);
            if (flag) {
                remark.append(CommonConstant.OPERATION_SUCCEED);
            } else {
                remark.append(CommonConstant.OPERATION_DEFEATED);
            }
            // 操作日志处理
            String opInfo = String.format("话术标题:%s", speechcraftModelDto.getName());
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.FLOWCONFIG, businessId.toString(), "speechcraft/createToBasic",
                    CommonConstant.CREATE_SPEECHCRAFT, remark.toString(), OperateType.CREATE, opInfo, null, null);

            if (flag) {
                Map<String,Object> map = new HashMap<>();
                map.put("craftId",craftId);
                map.put("businessId",businessId);
                returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
                returnMsg.setContent(map);
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            returnMsg.setErrorMsg("请求异常!");
            logger.error("新增专用话术(基础信息)异常:  ", e);
        }
        return returnMsg;

    }

    /**
     * @api {POST} /busiManagement/flowConfig/speechcraft/modifyToContentRelevant 话术管理-修改专用话术回答相关数据
     * @apiName modifyToContentRelevant
     * @apiGroup SpeechcraftContorller
     * @apiParam {SpeechcraftDto} speechcraftDto  专用话术(不包括录音)，示例如下：
     * @apiParamExample {json} 专用话术输入
     * {
     * "id":主键id,
     * "craftId":"话术标识",
     * "businessId":智库id,
     * "recordDetail":[
     * {
     * "content":"文本内容",
     * "recordName":录音文件,
     * "recordDescribe":录音文件描述(类型 0:tts,1:手动上传)
     * }
     * ]
     * }
     * @apiSuccess {String} code 0:新增成功 1:新增失败
     * @apiSuccess {String} content  无
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": 无,
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     **/
    @PostMapping(value = "/flowConfig/speechcraft/modifyToContentRelevant")
    public ReturnMsg modifySpeechcraftToContentRelevant(HttpServletRequest request, @RequestBody SpeechcraftModelDto speechcraftModelDto) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            TmUser user = GetUid.getUID(request, redisClient);
            Long companyId = user.getCompanyId();
            speechcraftModelDto.setCompanyId(companyId);
            Long businessId = speechcraftModelDto.getBusinessId();
            String craftId = speechcraftModelDto.getCraftId();
            Speechcraft oldObj = speechcraftService.selectByPrimaryKey(speechcraftModelDto.getId());
            boolean flag = speechcraftService.updateAndRecardLogToContentRelevant(speechcraftModelDto,user);
            StringBuilder remark = new StringBuilder("craftId=" + craftId);
            if (flag) {
                remark.append(CommonConstant.OPERATION_SUCCEED);
            } else {
                remark.append(CommonConstant.OPERATION_DEFEATED);
            }
            String opInfo = String.format("话术标题:%s", oldObj.getName());
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.FLOWCONFIG, businessId.toString(), "speechcraft/modifyToContentRelevant",
                    CommonConstant.MODIFY_SPEECHCRAFT, remark.toString(), OperateType.MODIFY, opInfo, oldObj, speechcraftModelDto);
            if (flag) {
                returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            logger.error("修改专用话术(回答相关数据)出错:  ", e);
        }
        return returnMsg;
    }

    /**
     * @api {POST} /busiManagement/flowConfig/speechcraft/modifyToBasic 话术管理-修改专用话术基础信息
     * @apiName modifyToBasic
     * @apiGroup SpeechcraftContorller
     * @apiParam {SpeechcraftDto} speechcraftDto  专用话术(不包括录音)，示例如下：
     * @apiParamExample {json} 专用话术输入
     * {
     * "id":主键id,
     * "craftId":"话术标识"，
     * "name":"话术问题名"，
     * "businessId":"智库id"，
     * "score":"得分",
     * "msgtemplId":"短信模板id，当为0时表示不发送短信"，
     * "foucs":"关注点"
     * }
     * @apiSuccess {String} code 0:新增成功 1:新增失败
     * @apiSuccess {String} content  无
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": 无,
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     **/
    @PostMapping(value = "/flowConfig/speechcraft/modifyToBasic")
    public ReturnMsg modifySpeechcraftToBasic(HttpServletRequest request, @RequestBody SpeechcraftModelDto speechcraftModelDto) {
        ReturnMsg returnMsg = new ReturnMsg();
        TmUser user = GetUid.getUID(request, redisClient);
        Long companyId = user.getCompanyId();
        try {
            speechcraftModelDto.setCompanyId(companyId);
            Long businessId = speechcraftModelDto.getBusinessId();
            String craftId = speechcraftModelDto.getCraftId();
            SpeechcraftModelDto oldObj = speechcraftService.selectDtoByCraftId(speechcraftModelDto.getCraftId(), false);

            SpeechcraftModelDto operateObj = new SpeechcraftModelDto();
            BeanUtils.copyProperties(speechcraftModelDto, operateObj);
            operateObj.setFoucs(tBusinessFocusService.convertFocusNamesToFocusIds(user, businessId, operateObj.getFoucs()));
            Boolean flag = speechcraftService.updateBasicByDto(operateObj);
            //插入操作日志
            StringBuilder remark = new StringBuilder("craftId=" + craftId);
            if (flag) {
                remark.append(CommonConstant.OPERATION_SUCCEED);
            } else {
                remark.append(CommonConstant.OPERATION_DEFEATED);
            }
            String opInfo = String.format("话术标题:%s", oldObj.getName());
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.FLOWCONFIG, businessId.toString(), "speechcraft/modifyToBasic",
                    CommonConstant.MODIFY_SPEECHCRAFT, remark.toString(), OperateType.MODIFY, opInfo, oldObj, speechcraftModelDto);
            if (flag) {
                returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            logger.error("修改专用话术(基础信息)出错:  ", e);
        }
        return returnMsg;
    }

    /**
     * @api {POST} /busiManagement/flowConfig/speechcraft/queryDetail 话术管理-专用话术详情查询。
     * @apiName queryDetail
     * @apiGroup SpeechcraftContorller
     * @apiParam {String} craftId  专用话术标识
     * @apiSuccess {String} code 0:新增成功 其他:错误码
     * @apiSuccess {String} content  专用话术详情
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content":
     * {
     * "name":"话术名"，
     * "businessId":"智库id"，
     * "craftId":"话术标识"，
     * "msgtemplId":"短信模板"，
     * "speechcrafts":[
     * {
     * "id":"主键id",
     * "content":"回答内容",
     * "recordName":"录音文件名称"
     * }
     * ]
     * }
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     **/
    @RequestMapping(value = "/flowConfig/speechcraft/queryDetail")
    public ReturnMsg queryDetailSpeechcraft(HttpServletRequest request, @RequestParam("craftId") String craftId) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            TmUser user = GetUid.getUID(request, redisClient);
            if("0".equals(craftId)|| StringUtils.isEmpty(craftId)){
                returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
                returnMsg.setContent(new SpeechcraftModelDto());
                return returnMsg;
            }
            SpeechcraftModelDto speechcraftModelDto = speechcraftService.selectDtoByCraftId(craftId, true);
            speechcraftModelDto.setFoucs(tBusinessFocusService.convertFocusIdsToFocusNames(user.getCompanyId(), speechcraftModelDto.getBusinessId(), speechcraftModelDto.getFoucs()));
            returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            returnMsg.setContent(null == speechcraftModelDto ? new Object[0] : speechcraftModelDto);
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            logger.error("查询指定专用话术异常:", e);
        }
        return returnMsg;
    }

    /**
     * hgl
     * 导话术专用
     * @param businessId
     * @param response
     * @throws IOException
     */
    @GetMapping(value = "/node")
    public void node(Long businessId, HttpServletResponse response) throws IOException {
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("businessId",Long.valueOf(businessId));
        speechcraftService.node(map, response);
    }
}
