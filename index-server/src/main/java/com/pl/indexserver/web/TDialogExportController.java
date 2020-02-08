package com.pl.indexserver.web;


import com.pl.indexserver.model.TDialogModelDto;
import com.pl.indexserver.service.BusinessReportService;
import com.pl.indexserver.service.TDialogService;
import com.pl.indexserver.untils.GetUid;
import com.pl.indexserver.untils.RedisClient;
import com.pl.model.TmUser;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RestController
@RequestMapping(value = "/busiManagement")
public class TDialogExportController {


    @Autowired
    private RedisClient redisClient;
    @Autowired //对话详情
    private TDialogService tDialogService;
    @Autowired
    private BusinessReportService businessReportService;

    /**
     * **
     * @api {POST} /busiManagement/callTask/task/exportDetail 导出任务明细信息
     * @apiName 任务明细信息
     * @apiGroup OutboundController
     * @apiParam {Long} id  任务ID
     * @apiParam {String} beginDate  开始时间
     * @apiParam {String} endDate  结束时间
     * @apiParam {String} intentionLevel  意向等级
     * @apiParam {String} ctAddress  城市查找
     * @apiParam {String} ctPosition  档位查找
     * @apiParam {String} status  工作状态
     * @apiParam {String} queryJson  编码
     *
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {

     * "code": 0,
     * "content": {
     * "pageIndex": 1,
     * "totalPage": 0,
     *" tdialogModelDtoList": [
     *
     * "id": 111271,
     * "ctName": "啊啊",
     * "ctPhone": "13392195945",
     *" isIntention": 0,
     * "isIntentionInfo": "未拨打",
     * "beginDate": null,
     * "duration": 0,
     * "agentNum": 0,
     * "intentionLevel": null,
     * "ctAddress": ""
     * }
     * ],
     * "pageSize": 4
     * },
     * "token": null,
     * "errorMsg": null
     * }
     *
     */
    @GetMapping (value = "/callTask/task/exportDetail")
    public void export(HttpServletRequest request, HttpServletResponse response,
                       String queryJson,
                       @RequestParam("id") String id,
                       String beginDate,
                       String endDate,
                       String type,
                       String intentionLevel,
                       String ctAddress,String telephone,
                       String ctPosition,  String status)
    {

        TmUser user = GetUid.getUID(request, redisClient);
        //如果出现中文乱码请添加下面这句
        if(queryJson!=null) {
            try {
                queryJson = URLDecoder.decode(queryJson, "UTF-8");
            } catch (Exception E) {
                E.printStackTrace();
            }
        }
        //需要导入alibaba的fastjson包
       TDialogModelDto dto = com.alibaba.fastjson.JSON.parseObject(queryJson, TDialogModelDto.class);

        Map<String,Object> map=new HashMap<String,Object>();
        map.put("id",Long.valueOf(id));
        if (!StringUtils.isEmpty(type)) {
            map.put("type",type);
        }
        if (!StringUtils.isEmpty(telephone)) {
            telephone=telephone.replaceAll("\\D", "");
            map.put("telephone",telephone);
        }
        if(beginDate!=null&beginDate!=""){
            map.put("beginDate",beginDate);
        }
        if(endDate!=null&endDate!=""){
            map.put("endDate",endDate);
        }
        if(intentionLevel!=null&intentionLevel!=""){
            map.put("intentionLevel",intentionLevel);
        }
        if(ctAddress!=null&ctAddress!=""){
            map.put("ctAddress",ctAddress);
        }
        if(ctPosition!=null&ctPosition!="") {
            map.put("ctPosition", ctPosition);
        }
        if (status != null && !"".equals(status) && Integer.valueOf(status) == 50) {
            map.put("tableName", businessReportService.getReportTableName(Long.valueOf(id)));
        }else {
            if (status != null && !"".equals(status) && Integer.valueOf(status) < 20) {
                map.put("status", status);
            } else if (status != null && !"".equals(status) && Integer.valueOf(status) > 20) {
                map.put("isIntention", status);
            } else if (status != null && !"".equals(status) && Integer.valueOf(status) == 20) {//----
                map.put("allIsIntention", "true");
            }
        }
       tDialogService.exportAllTDialogTDialogModelDto(map, response);
    }

    /**
     * **
     * @api {POST} /busiManagement/callTask/task/exportDetail 导出呼入通话详情数据
     * @apiName 导出呼入通话详情数据
     * @apiGroup TDialogExportController
     * @apiParam {Long} id  任务ID
     * @apiParam {String} beginDate  开始时间
     * @apiParam {String} endDate  结束时间
     * @apiParam {String} intentionLevel  意向等级
     * @apiParam {String} ctAddress  城市查找
     * @apiParam {String} ctPosition  档位查找
     * @apiParam {String} status  工作状态
     * @apiParam {String} queryJson  编码
     *
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {

     * "code": 0,
     * "content": {
     * "pageIndex": 1,
     * "totalPage": 0,
     *" tdialogModelDtoList": [
     *
     * "id": 111271,
     * "ctName": "啊啊",
     * "ctPhone": "13392195945",
     *" isIntention": 0,
     * "isIntentionInfo": "未拨打",
     * "beginDate": null,
     * "duration": 0,
     * "agentNum": 0,
     * "intentionLevel": null,
     * "ctAddress": ""
     * }
     * ],
     * "pageSize": 4
     * },
     * "token": null,
     * "errorMsg": null
     * }
     *
     */
    @GetMapping (value = "/callIn/exportDetail")
    public void exportDetail(HttpServletRequest request, HttpServletResponse response,
                       String queryJson,
                       @RequestParam("id") String id,
                       String beginDate,
                       String endDate,
                       String type,
                       String intentionLevel,
                       String ctAddress,String telephone,
                       String ctPosition,  String status)
    {

        TmUser user = GetUid.getUID(request, redisClient);
        //如果出现中文乱码请添加下面这句
        if(queryJson!=null) {
            try {
                queryJson = URLDecoder.decode(queryJson, "UTF-8");
            } catch (Exception E) {
                E.printStackTrace();
            }
        }
        //需要导入alibaba的fastjson包
        TDialogModelDto dto = com.alibaba.fastjson.JSON.parseObject(queryJson, TDialogModelDto.class);

        Map<String,Object> map=new HashMap<String,Object>();
        map.put("id",Long.valueOf(id));
        if (!StringUtils.isEmpty(type)) {
            map.put("type",type);
        }
        if (!StringUtils.isEmpty(telephone)) {
            telephone=telephone.replaceAll("\\D", "");
            map.put("telephone",telephone);
        }
        if(beginDate!=null&beginDate!=""){
            map.put("beginDate",beginDate);
        }
        if(endDate!=null&endDate!=""){
            map.put("endDate",endDate);
        }
        if(intentionLevel!=null&intentionLevel!=""){
            map.put("intentionLevel",intentionLevel);
        }
        if(ctAddress!=null&ctAddress!=""){
            map.put("ctAddress",ctAddress);
        }
        if(ctPosition!=null&ctPosition!="") {
            map.put("ctPosition", ctPosition);
        }
        if (status != null && !"".equals(status) && Integer.valueOf(status) == 50) {
            map.put("tableName", businessReportService.getReportTableName(Long.valueOf(id)));
        }else {
            if (status != null && !"".equals(status) && Integer.valueOf(status) < 20) {
                map.put("status", status);
            } else if (status != null && !"".equals(status) && Integer.valueOf(status) > 20) {
                map.put("isIntention", status);
            } else if (status != null && !"".equals(status) && Integer.valueOf(status) == 20) {//----
                map.put("allIsIntention", "true");
            }
        }
        tDialogService.exportAllTDialogInTDialogModelDto(map, response);
    }

    @GetMapping(value = "/callTask/task/exportReport")
    public void exportReport(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam("id") String id,
                             String beginDate,
                             String endDate,
                             String intentionLevel,
                             String status) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", Long.valueOf(id));
        if (beginDate != null & beginDate != "") {
            map.put("beginDate", beginDate);
        }
        if (endDate != null & endDate != "") {
            map.put("endDate", endDate);
        }
        if (intentionLevel != null & intentionLevel != "") {
            map.put("intentionLevel", intentionLevel);
        }
        if (status != null && !"".equals(status) && Integer.valueOf(status) < 20) {
            map.put("status", status);
        } else if (status != null && !"".equals(status) && Integer.valueOf(status) > 20) {
            map.put("isIntention", status);
        } else if (status != null && !"".equals(status) && Integer.valueOf(status) == 20) {//----
            map.put("allIsIntention", "true");
        }
        businessReportService.exportBusinessReport(Long.valueOf(id) , map, response);
    }

    public class ExportExcel<T> {
        public void exportExcel(String[] headers,Collection<T> dataset, String fileName,HttpServletResponse response) {
            // 声明一个工作薄
            XSSFWorkbook  workbook = new XSSFWorkbook();
            // 生成一个表格
            XSSFSheet sheet = workbook.createSheet(fileName);
            // 设置表格默认列宽度为15个字节
            sheet.setDefaultColumnWidth((short) 20);
            // 产生表格标题行
            XSSFRow row = sheet.createRow(0);
            for (short i = 0; i < headers.length; i++) {
                XSSFCell  cell = row.createCell(i);
                XSSFRichTextString text = new XSSFRichTextString(headers[i]);
                cell.setCellValue(text);
            }
            try {
                // 遍历集合数据，产生数据行
                Iterator<T> it = dataset.iterator();
                int index = 0;
                while (it.hasNext()) {
                    index++;
                    row = sheet.createRow(index);
                    T t = (T) it.next();
                    // 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
                    Field[] fields = t.getClass().getDeclaredFields();
                    for (short i = 0; i < headers.length; i++) {
                        XSSFCell cell = row.createCell(i);
                        Field field = fields[i];
                        String fieldName = field.getName();
                        String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        Class tCls = t.getClass();
                        Method getMethod = tCls.getMethod(getMethodName, new Class[] {});
                        Object value = getMethod.invoke(t, new Object[] {});
                        // 判断值的类型后进行强制类型转换
                        String textValue = null;
                        // 其它数据类型都当作字符串简单处理
                        if(value != null && value != ""){
                            textValue = value+"";
                        }
                        if (textValue != null) {
                            XSSFRichTextString richString = new XSSFRichTextString(textValue);
                            cell.setCellValue(richString);
                        }
                    }
                }
                getExportedFile(workbook, fileName,response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         *
         * 方法说明: 指定路径下生成EXCEL文件
         * @return
         */
        public void getExportedFile(XSSFWorkbook workbook, String name,HttpServletResponse response) throws Exception {
            BufferedOutputStream fos = null;
            try {
                String fileName = name + ".xlsx";
                response.setContentType("application/x-msdownload");
                response.setHeader("Content-Disposition", "attachment;filename=" + new String( fileName.getBytes("gb2312"), "ISO8859-1" ));
                fos = new BufferedOutputStream(response.getOutputStream());
                workbook.write(fos);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    fos.close();
                }
            }
        }

    }
}

