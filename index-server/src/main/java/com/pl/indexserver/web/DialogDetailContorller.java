package com.pl.indexserver.web;
import com.alibaba.fastjson.JSONObject;
import com.pl.indexserver.model.DialogModelDto;
import com.pl.indexserver.model.ReturnMsg;
import com.pl.indexserver.service.DialogDetailService;
import com.pl.indexserver.service.TDialogService;
import com.pl.indexserver.untils.CommonConstant;
import com.pl.indexserver.untils.MD5;
import com.pl.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;

@RestController
public class DialogDetailContorller {

    private static final Logger logger = LoggerFactory.getLogger(DialogDetailContorller.class);

    @Autowired
    private DialogDetailService dialogDetailService;
    @Autowired
    private TDialogService tDialogService;



    /**
     * @api {POST} /busiManagement/callIn/queryRecordDetail 查询呼入通话的详情信息
     * @apiName 查询呼入通话的详情信息
     * @apiGroup DialogDetailContorller
     * @apiParam {int} dialogId 通话dialogId
     * @apiSuccessExample {json}  成功返回示例
     * {
     *   "code": 0,
        "content": {
        "status": "已接通",
        "dialogNum": 2,
        "recordPath": "",
        "companyId": "13",
        "taskId": "999918938044329",
        "telephone": "075585264759",
        "totalSeconds": 24056,
        "createDate": "2018-11-12 09:42:44",
        "beginDate": "2018-11-11 11:16:51",
        "ctname": "",
        "contents": [
        [
        {
        "id": "0",
        "file_path": "",
        "content": null,
        "participant": 1,
        "cycleId": "0",
        "workNodeName": null,
        "speachCraftName": null,
        "contentDetail": [
        "喂，你好！"
        ]
        },
        {
        "id": "1",
        "file_path": null,
        "content": null,
        "participant": 2,
        "cycleId": "0",
        "workNodeName": "喂你好",
        "speachCraftName": "喂你好",
        "contentDetail": []
        }
        ],
        [
        {
        "id": "2",
        "file_path": "",
        "content": null,
        "participant": 1,
        "cycleId": "1",
        "workNodeName": null,
        "speachCraftName": null,
        "contentDetail": [
        "您好，我们是小兵电话机器人，机器人可代替人工自动拨打电话并记录筛选意向客户，您有兴趣了解下吗"
        ]
        },
        {
        "id": "3",
        "file_path": "http://pulanbd.iok.la:8800/recordManagement/13/TASK-999918938044329/075585264759/075585264759_9fb85d99-3ba9-4c27-aa8c-43121cd72794_1.wav;",
        "content": null,
        "participant": 2,
        "cycleId": "1",
        "workNodeName": "开场白",
        "speachCraftName": "开场白",
        "contentDetail": [
        "小。"
        ]
        }
        ]
        ]
        },
        "token": null,
        "errorMsg": null
        }
     */
    @RequestMapping("/busiManagement/callIn/queryCallInRecordDetail")
    public Object queryCallInRecordDetail(@RequestParam("dialogId") String dialogId) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            DialogModelDto queryDetailDto = dialogDetailService.queryCallInRecordDetail(dialogId);
            
            /*if ("2122".equals(queryDetailDto.getCompanyId()))
            {
            	String path = queryDetailDto.getRecordPath();
            	path =path.replaceFirst(queryDetailDto.getTaskId()+"", "18948174400");
            	queryDetailDto.setRecordPath(path);
            }*/
            //调过此接口之后，把数据库intent_level定义为是否已读状态，isRead为已读，null为未读
            tDialogService.updateDialogIsReadStatus(Long.parseLong(dialogId));

            returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            returnMsg.setContent(queryDetailDto);
            return returnMsg;
        } catch (NullPointerException e) {
            logger.error("查询通话详情异常(无通话详情):", e);
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            returnMsg.setContent("此通话无通话记录");
            return returnMsg;
        } catch (Exception e) {
            logger.error("查询通话详情异常:", e);
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            return returnMsg;
        }
    }

    /**
     * @api {POST} /busiManagement/callTask/dialog/queryDetail 查询呼出通话的信息
     * @apiName 查询呼出通话的信息
     * @apiGroup DialogDetailContorller
     * @apiParam {int} dialogId 通话id
     * @apiParam {int} taskId 任务ID
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": {
     * "status": "未接",
     * "dialogNum": 0,
     * "recordPath": "",
     * "companyId": "2069",
     * "taskId": "11164",
     * "taskName": "teset12312",
     * "telephone": "15270230603",
     * "totalSeconds": 0,
     * "intentionLevel": "",
     * "focusLevel": "",
     * "intentLevel": "",
     * "createDate": "2018-12-24 14:28:06",
     * "beginDate": "2018-12-24 14:33:00",
     * "contents": []
     * },
     * "token": null,
     * "errorMsg": null
     * }
     */
    /*@RequestMapping("/busiManagement/callTask/dialog/queryDetail")
    public Object queryDetail(@RequestParam("dialogId") String dialogId,
                              @RequestParam("taskId") Long taskId) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            DialogModelDto queryDetailDto = dialogDetailService.queryDetail(dialogId, taskId);
            returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            returnMsg.setContent(queryDetailDto);
            return returnMsg;
        } catch (NullPointerException e) {
            logger.error("查询通话详情异常(无通话详情):", e);
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            returnMsg.setContent("此通话无通话记录");
            return returnMsg;
        } catch (Exception e) {
            logger.error("查询通话详情异常:", e);
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            return returnMsg;
        }
    }*/
    @RequestMapping("/busiManagement/callTask/dialog/queryDetail")
    public Object queryDetail(@RequestParam("dialogId") String dialogId,
                              @RequestParam("taskId") Long taskId) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            DialogModelDto queryDetailDto = dialogDetailService.queryDetail(dialogId, taskId);
            String recordPath = queryDetailDto.getRecordPath();
            if (recordPath == null ||recordPath.length() < 10)
            {
                try {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String beginDate = df.format(queryDetailDto.getBeginDate());
                    String fileDir = "/mnt/tm/recordings/"+beginDate;
                    String phone = queryDetailDto.getTelephone();
                    File[] fileNames = new File(fileDir).listFiles(
                            (file) -> file.getName().startsWith(phone));

                    long callTime = queryDetailDto.getBeginDate().getTime();
                    long minTime = 0;

                    for (File fileName : fileNames) {
                        long fileTime =  fileName.lastModified();
                        long  diffTime = Math.abs(fileTime - callTime);
                        logger.debug( fileName+"=========diffTime=" +diffTime );
                        if ( diffTime < minTime || minTime == 0 )
                        {
                            recordPath = "/recordManagement/recordings/"+beginDate+"/"+ fileName.getName();
                            if (recordPath.endsWith("wav")){
                                recordPath = recordPath.substring(0,recordPath.length()-4);
                            }
                            minTime = fileTime - callTime;
                        }
                    }

                    queryDetailDto.setRecordPath(recordPath);
                } catch (Exception e) {

                }
            }
            returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            returnMsg.setContent(queryDetailDto);
            return returnMsg;
        } catch (NullPointerException e) {
            logger.error("查询通话详情异常(无通话详情):", e);
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            returnMsg.setContent("此通话无通话记录");
            return returnMsg;
        } catch (Exception e) {
            logger.error("查询通话详情异常:", e);
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            return returnMsg;
        }
    }


    /**
     * @api {GET} /busiManagement/callTask/dialog/queryDetailByCallTask    查询通话记录详情（用于客户中心）
     * @apiName queryDetailByCallTask
     * @apiGroup DialogDetailContorller
     * @apiParam {String} telephone 电话号码
     * @apiParam {Long} taskId 任务id
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回数据
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": "通话记录id",
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @RequestMapping("/busiManagement/callTask/dialog/queryDetailByCallTask")
    public ReturnMsg queryDetailByCallTask(Long taskId, String telephone) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            if (null != telephone && !"".equals(telephone)) {
                telephone = telephone.replace("\"", "");
            }
            TDialog tDialog = tDialogService.selectByTaskIdAndTelephone(taskId, telephone);
            if (null == tDialog) {
                throw new NullPointerException();
            } else {
                DialogModelDto queryDetailDto = dialogDetailService.queryDetail(tDialog.getId().toString(), taskId);
                returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
                returnMsg.setContent(queryDetailDto);
            }
            return returnMsg;
        } catch (NullPointerException e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            returnMsg.setErrorMsg("无通话记录");
            return returnMsg;
        } catch (Exception e) {
            logger.error("查询通话详情异常:", e);
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            return returnMsg;
        }

    }

    /**
     * hgl
     * 呼叫结果接口，群控调此接口加微信
     * @param qryCallResultRequest
     * @return
     * @throws NoSuchAlgorithmException
     */
    @RequestMapping("/busiManagement/qryCallResult")
    public ChatPhone getphonenumber(@RequestBody QryCallResultRequest qryCallResultRequest) throws NoSuchAlgorithmException {
        /*SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date now = new Date();
        String format = dateFormat.format(now);*/
        ChatPhone cp = new ChatPhone();
        String userName = qryCallResultRequest.getUserName();
        String timeStamp = qryCallResultRequest.getTimeStamp();
        String checkCode = qryCallResultRequest.getCheckCode();
        String oldStr = userName + timeStamp + "xiaobingMd5Password";
        String md532bit = MD5.MD5_32bit(oldStr);
        logger.info("========qryCallResult=======oldStr={},timeStamp={},md532bit={}",oldStr,timeStamp,md532bit);
        if (!checkCode.equalsIgnoreCase(md532bit)){
            cp.setCode(-1);
            cp.setBussinessName("鉴权失败！！！");
            return cp;
        }
        ChatPhone chatPhone = tDialogService.getPhoneByUserName(userName);
        if (chatPhone == null){
            chatPhone.setCode(-1);
            chatPhone.setBussinessName("鉴权失败！！！");
            return chatPhone;
        }
        logger.info("%%%%%%%%%%%%%%%chatPhone={},%%%%%%%%%%%%%%%qryCallResultRequest={}",
                JSONObject.toJSONString(chatPhone),JSONObject.toJSONString(qryCallResultRequest));
        return chatPhone;
    }

//    /**
//     * 查询具体人员的对话信息
//     *
//     * @param dialogId   对应会话的id
//     */
//    @RequestMapping("/wx/busiManagement/callTask/dialog/queryDetail")
//    public ReturnMsg queryDetailByWeiXin(@RequestParam("dialogId")String dialogId, @RequestParam("taskId")Long taskId) {
//        ReturnMsg returnMsg = new ReturnMsg();
//        try {
//            DialogModelDto queryDetailDto = dialogDetailService.queryDetail(dialogId,taskId);
//            if(StringUtils.isEmpty(queryDetailDto.getFocusLevel())){
//                queryDetailDto.setFocusLevel("未开启");
//            }
//            if(StringUtils.isEmpty(queryDetailDto.getIntentionLevel())){
//                queryDetailDto.setIntentionLevel("未开启");
//            }
//            if(StringUtils.isEmpty(queryDetailDto.getIntentLevel())){
//                queryDetailDto.setIntentLevel("未开启");
//            }
//            returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
//            returnMsg.setContent(queryDetailDto);
//            return returnMsg;
//        } catch(NullPointerException e){
//            logger.error("查询通话详情异常(无通话详情):",e);
//            returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
//            returnMsg.setErrorMsg("此通话无通话记录");
//            return returnMsg;
//        }catch (Exception e) {
//            logger.error("查询通话详情异常:",e);
//            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
//            return returnMsg;
//        }
//    }

}
