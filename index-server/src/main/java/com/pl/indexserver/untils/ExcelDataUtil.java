package com.pl.indexserver.untils;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.pl.indexserver.model.PersonDto;
import com.pl.indexserver.service.SpeechcraftTagService;
@Component
public class ExcelDataUtil {

    private static final Logger log = Logger.getLogger(ExcelDataUtil.class);
    private static ExcelDataUtil excelDataUtil;
    @Autowired
    private SpeechcraftTagService speechcraftTagService;

    @PostConstruct
    public void init() {
        excelDataUtil = this;
        excelDataUtil.speechcraftTagService = this.speechcraftTagService;
    }

    public static List<String[]> getExcelData(MultipartFile file) throws IOException {
        checkFile(file);
        //获得Workbook工作薄对象
        Workbook workbook = getWorkBook(file);
        //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
        List<String[]> list = new ArrayList<>();
        if(workbook != null){
            //sheet
            for(int sheetNum = 0;sheetNum < 1;sheetNum++){
                //获得当前sheet工作表
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if(sheet == null){
                    continue;
                }
                //获得当前sheet的开始行
                int firstRowNum  = sheet.getFirstRowNum();
                //除标题外的第一行
                int turefirstrownum = firstRowNum+1;
                //获得当前sheet的结束行
                int lastRowNum = sheet.getLastRowNum();
                //循环除了第一行的所有行
                for(int rowNum = turefirstrownum;rowNum <= lastRowNum;rowNum++){
                    //获得当前行
                    Row row = sheet.getRow(rowNum);
                    if(row == null){
                        continue;
                    }
                    //获得当前行的开始列
                    int firstCellNum = row.getFirstCellNum();
                    //获得当前行的列数
                    int lastCellNum = row.getLastCellNum();
                    String[] cells = new String[row.getLastCellNum()];
                    //循环当前行
                    for(int cellNum = firstCellNum; cellNum < lastCellNum;cellNum++){
                        Cell cell = row.getCell(cellNum);
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        cells[cellNum] = getCellValue(cell);
                    }
                    list.add(cells);
                }
            }
        }
        return list;
    }

    public static List<PersonDto> getExcelDatas(MultipartFile file,long companyid){
        checkFile(file);
        //获得Workbook工作薄对象
        Workbook workbook = getWorkBook(file);
        //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
        List<PersonDto> list = new ArrayList<>();
        if(workbook != null){
            //sheet
            for(int sheetNum = 0;sheetNum < 1;sheetNum++){
                //获得当前sheet工作表
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if(sheet == null){
                    continue;
                }
                //获得当前sheet的开始行
                int firstRowNum  = sheet.getFirstRowNum();
                //除标题外的第一行
                int turefirstrownum = firstRowNum+1;
                List<String> keys = new ArrayList<>();
                if(1005 != companyid){
                    Row rowq = sheet.getRow(0);
                    int lastCellNumq = rowq.getLastCellNum();
                    if (lastCellNumq>2) {
                        for (int i=2;i<lastCellNumq;i++){
                            Cell cellt = rowq.getCell(i);
                            if (null != cellt) {
                                cellt.setCellType(Cell.CELL_TYPE_STRING);
                                String name = getCellValue(cellt);
                                keys.add(excelDataUtil.speechcraftTagService.getSpeechcraftTag(companyid,name));
                            }
                        }
                    }
                }
                //获得当前sheet的结束行
                int lastRowNum = sheet.getLastRowNum();
                //循环除了第一行的所有行
                for(int rowNum = turefirstrownum;rowNum <= lastRowNum;rowNum++){
                    //获得当前行
                    Row row = sheet.getRow(rowNum);
                    if(row == null){
                        continue;
                    }
                    //获得当前行的列数
                    int lastCellNum = row.getLastCellNum();
                    PersonDto cells = new PersonDto();
                    //循环当前行
                    Cell cell = row.getCell(0);
                    // 用户名可能为空
                    if (null != cell) {
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        cells.setName(getCellValue(cell));
                    }

                    Cell cell2 = row.getCell(1);
                    if(null==cell2){
                        continue;
                    }
                    cell2.setCellType(Cell.CELL_TYPE_STRING);
                    String cellValue2 = getCellValue(cell2);
                    cells.setPhone(cellValue2.replaceAll("\\D",""));
                    if(1005 == companyid){
                        //如果用户是华胜
                        Cell cell3 = row.getCell(2);
                        if (null != cell3) {
                            cell3.setCellType(Cell.CELL_TYPE_STRING);
                            cells.setCar_numbers(getCellValue(cell3));
                        }
                    } else {
                        //处理TTS的多余信息
                        if (lastCellNum>=2) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("name",cells.getName());
                            jsonObject.put("phone",cells.getPhone());
                            for (int i=2;i<=lastCellNum;i++){
                                Cell cellt = row.getCell(i);
                                if (null != cellt && !StringUtils.isEmpty(getCellValue(cellt))) {
                                    cellt.setCellType(Cell.CELL_TYPE_STRING);
                                    jsonObject.put(keys.get(i-2),getCellValue(cellt));
                                }
                            }
                            cells.setExtra(jsonObject.toString());
                        }
                    }
                    if (!StringUtils.isEmpty(cells.getPhone())) {
                        list.add(cells);
                    }
                }
            }
        }
        return list;
    }

    /**
     * 检查文件
     * @param file
     */
    private static  void checkFile(MultipartFile file){
        //判断文件是否存在
        if(null == file){
            log.error("文件不存在！");
        } else {
            //获得文件名
            String fileName = file.getOriginalFilename();
            //判断文件是否是excel文件
            if(!fileName.endsWith("xls") && !fileName.endsWith("xlsx")){
                log.error(fileName + "不是excel文件");
            }
        }

    }

    private static  Workbook getWorkBook(MultipartFile file) {
        //获得文件名
        String fileName = file.getOriginalFilename();
        //创建Workbook工作薄对象，表示整个excel
        Workbook workbook = null;
        try {
            //获取excel文件的io流
            InputStream is = file.getInputStream();
            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
            if(fileName.endsWith("xls")){
                //2003
                workbook = new HSSFWorkbook(is);
            }else if(fileName.endsWith("xlsx")){
                //2007 及2007以上
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return workbook;
    }

    private static String getCellValue(Cell cell) {
        String cellValue = "";
        if (cell == null) {
            return cellValue;
        }
        //判断数据的类型
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC: //数字
                cellValue = cell.getNumericCellValue() + "";
                break;
            case Cell.CELL_TYPE_STRING: //字符串
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN: //Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA: //公式
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case Cell.CELL_TYPE_BLANK: //空值
                cellValue = "";
                break;
            case Cell.CELL_TYPE_ERROR: //故障
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }


    /**
     * 时间格式处理
     * @return
     * @author Liu Xin Nan
     * @data 2017年11月27日
     */
    public static String stringDateProcess(Cell cell){
        String result;
        if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
            SimpleDateFormat sdf = null;
            if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
                sdf = new SimpleDateFormat("HH:mm");
            } else {// 日期
                sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            }
            Date date = cell.getDateCellValue();
            result = sdf.format(date);
        } else if (cell.getCellStyle().getDataFormat() == 58) {
            // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            double value = cell.getNumericCellValue();
            Date date = DateUtil
                    .getJavaDate(value);
            result = sdf.format(date);
        } else {
            double value = cell.getNumericCellValue();
            CellStyle style = cell.getCellStyle();
            DecimalFormat format = new DecimalFormat();
            String temp = style.getDataFormatString();
            // 单元格设置成常规
            if (temp.equals("General")) {
                format.applyPattern("#");
            }
            result = format.format(value);
        }

        return result;
    }

    /**
     * 创建导入模板
     * @param sheet_name  工作表名称
     * @param excelHeader 表头
     * @return
     */
    public static HSSFWorkbook createExcel(String  sheet_name,List<String> excelHeader){
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(sheet_name);
        try {
            HSSFRow row = sheet.createRow(0);
            //创建表头
            for (int i = 0; i < excelHeader.size(); i++) {
                HSSFCell cell = row.createCell(i);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellValue(new HSSFRichTextString(excelHeader.get(i)));
                sheet.autoSizeColumn(i);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return wb;
    }




    public static HSSFWorkbook exportFileModel(String titles){
        List<String> excelHeader = new ArrayList<String>();
        excelHeader.add("客户姓名");
        excelHeader.add("客户手机");
        if (!StringUtils.isEmpty(titles)) {
            String[] titlearr = titles.split(",");
            for (int i=0;i<titlearr.length;i++){
                excelHeader.add(titlearr[i]);
            }
        }
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("sheet1");
        HSSFRow row = sheet.createRow(0);
        //创建表头
        CellStyle textStyle = wb.createCellStyle();
        DataFormat  format = wb.createDataFormat();
        textStyle.setDataFormat(format.getFormat("@"));
        for (int i = 0; i < excelHeader.size(); i++) {
            HSSFCell cell = row.createCell(i);
            sheet.setDefaultColumnStyle(i, textStyle);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellValue(new HSSFRichTextString(excelHeader.get(i)));
            sheet.setColumnWidth(i, excelHeader.get(i).getBytes().length * 256);
        }
        return  wb;
    }

    public static HSSFWorkbook exportFileModelWithCarnumber(){
        List<String> excelHeader = new ArrayList<String>();
        excelHeader.add("客户名称");
        excelHeader.add("客户手机");
        excelHeader.add("车牌号");
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("sheet1");
        HSSFRow row = sheet.createRow(0);
        //创建表头
        for (int i = 0; i < excelHeader.size(); i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(new HSSFRichTextString(excelHeader.get(i)));
            sheet.autoSizeColumn(i);
        }
        return  wb;
    }

    private void setSizeColumn(HSSFSheet sheet) {
        for (int columnNum = 0; columnNum <= 8; columnNum++) {
            int columnWidth = sheet.getColumnWidth(columnNum) / 256;
            for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
                HSSFRow currentRow;
                //当前行未被使用过
                if (sheet.getRow(rowNum) == null) {
                    currentRow = sheet.createRow(rowNum);
                } else {
                    currentRow = sheet.getRow(rowNum);
                }

                if (currentRow.getCell(columnNum) != null) {
                    HSSFCell currentCell = currentRow.getCell(columnNum);
                    if (currentCell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        int length = currentCell.getStringCellValue().getBytes().length;
                        if (columnWidth < length) {
                            columnWidth = length;
                        }
                    }
                }
            }
            sheet.setColumnWidth(columnNum, columnWidth * 256);
        }
    }

    
    public static void main(String a[])
    { 
    	       
    	        //创建Workbook工作薄对象，表示整个excel
    	        Workbook workbook = null;
    	        try {
    	        	String fileName = "F:\\WeChat Files\\lsb13613004847\\FileStorage\\File\\2019-10\\111.xlsx";
    	            //获取excel文件的io流
    	            InputStream is = new FileInputStream(fileName)  ;
    	            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
    	            if(fileName.endsWith("xls")){
    	                //2003
    	                workbook = new HSSFWorkbook(is);
    	            }else if(fileName.endsWith("xlsx")){
    	                //2007 及2007以上
    	                workbook = new XSSFWorkbook(is);
    	            }
    	        } catch (Exception e) {
    	            log.error(e.getMessage());
    	        }
    	        
    }
}
