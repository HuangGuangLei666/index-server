package com.pl.indexserver.untils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class ExportUtil {

    private static Logger logger = LoggerFactory.getLogger(ExportUtil.class);

    public static HSSFSheet createSheetWithHeads(HSSFWorkbook wb, String sheetName, String[] heads) {
        HSSFSheet sheet = wb.createSheet(sheetName);
        CellStyle style = getCellStyle(wb);
        ExportUtil.setAllHeader(wb, sheet, heads, style);
        return sheet;
    }

    public static HSSFSheet createSheetNoHeads(HSSFWorkbook wb, String sheetName) {
        return wb.createSheet(sheetName);
    }

    public static void exportXls(HSSFWorkbook wb, String downNamePre, HttpServletResponse response) {
        try {
            String fileNameDisplay = new String(downNamePre.getBytes("GB2312"), "ISO_8859_1") ;
            response.reset();
//            
            response.setHeader("Cache-Control", "min-fresh");
            response.setHeader("Access-Control-Allow-Origin", "*");
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            
             
            int fileSize = wb.getBytes().length;
            logger.info("fileSize===" + fileSize);
            if (fileSize > 2048000)
            {
            	logger.info("application/x-zip-compressed" );
            	response.addHeader("Content-Disposition", "attachment;filename=" + fileNameDisplay+ ".zip");
            	
            	response.setContentType("application/x-zip-compressed");  
                ZipOutputStream zip = new ZipOutputStream( toClient );
                
                ZipEntry entry = new ZipEntry(downNamePre+".xls");
                zip.putNextEntry(entry);
                
                wb.write(zip);
                zip.flush();
                zip.close();
            }
            else
            {
            	response.addHeader("Content-Disposition", "attachment;filename=" + fileNameDisplay+ ".xls");
            	response.setContentType("application/vnd.ms-excel");            
            	 wb.write(toClient);
            }
           

            toClient.flush();
            toClient.close();

        } catch (Exception e) {
            logger.error("exportXls error.", logger, e);
        }
    }

    private static void exportXls(HSSFWorkbook wb, String downNamePre, FileOutputStream os) {
        try {
        	logger.error("exportXls error.", logger );
        	
        	
           


            FileOutputStream target = new FileOutputStream("f:/t.zip");
            ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(target));
            
            ZipEntry entry = new ZipEntry("t.xls");
            zip.putNextEntry(entry);
            
            
            wb.write(zip);
            zip.flush();
            zip.close();
            
            
            os.flush();
            os.close();
        } catch (Exception e) {
            logger.error("exportXls error.", logger, e);
        }
    }

    public static CellStyle getCellStyle(HSSFWorkbook wb) {
        HSSFCellStyle style = wb.createCellStyle();
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        return style;
    }

    public static void setAllHeader(HSSFWorkbook wb, HSSFSheet sheet, String[] cells, CellStyle style) {
        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i < cells.length; i++) {
            HSSFCell cell = row.createCell(i);
            sheet.setColumnWidth(i, cells[i].getBytes().length * 256);
            setCellValue(wb, cell, cells[i], style);
        }
    }


    public static void setAllCell(HSSFWorkbook wb, HSSFSheet sheet, int n, String[] cells, CellStyle style) {
        HSSFRow row = sheet.createRow(n);
        if (row == null) {
            return;
        }
        for (int i = 0; i < cells.length; i++) {
            HSSFCell cell = row.createCell(i);
            setCellValue(wb, cell, cells[i], style);
        }
    }

    public static void setCellValue(HSSFWorkbook wb, HSSFCell cell, Object obj) {
        if (cell != null) {
            cell.setCellValue(obj == null ? "" : obj.toString());
        }
    }

    public static void setCellValue(HSSFWorkbook wb, HSSFCell cell, Object obj, CellStyle style) {
        if (cell != null) {
            cell.setCellStyle(style);
            cell.setCellValue(obj == null ? "" : obj.toString());
        }
    }

    public static void setCellValue(HSSFWorkbook wb, HSSFRow row, int n, Object obj) {
        if (row != null) {
            HSSFCell cell = row.createCell(n);
            setCellValue(wb, cell, obj);
        }
    }

    public static void setCellPercent(HSSFWorkbook wb, HSSFCell cell, Float value) {
        if (cell != null && value != null) {
            HSSFCellStyle cellStyle = wb.createCellStyle();
            HSSFDataFormat dataFormat = wb.createDataFormat();
            cellStyle.setDataFormat(dataFormat.getFormat("0%"));
            cell.setCellStyle(cellStyle);
            if (value.isNaN()) {
                value = 0F;
            }
            setCellValue(wb, cell, percent(value));
        }
    }

    private static String percent(Float rate) {
        if (rate == null) {
            return "-";
        }
        DecimalFormat df = new DecimalFormat("0.00%");
        return df.format(rate);
    }

    public static void setCellAsTitle(HSSFWorkbook wb, HSSFCell cell, Object obj) {
        if (cell != null && obj != null) {
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            HSSFFont font = wb.createFont();
            font.setFontName("黑体");
            font.setFontHeightInPoints((short) 16);
            style.setFont(font);
            cell.setCellStyle(style);
            cell.setCellValue(obj == null ? "" : obj.toString());

        }
    }

    public static String objectToString(Object object) {
        if (!StringUtils.isEmpty(object)) {
            return String.valueOf(object);
        }
        return "";
    }

    @SuppressWarnings("unused")
    public static void main(String[] args) throws Exception {
        //例子1，没有模板文件的
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = ExportUtil.createSheetWithHeads(wb, "激活数",
                new String[]{"日期", "应用代码", "广告id", "应用名字", "渠道名字", "send_ret", "激活数"});
        CellStyle style = ExportUtil.getCellStyle(wb);
        List<String> strList = new ArrayList<String>();
       for(int i=0;i<60000;i++)
       {
        strList.add(i+"  11aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
        		+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
        		+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
        		+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
        		+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
        		+ "aaaaaaaaaaaaaaaa" );
//        strList.add("22");
//        strList.add("33");
       }
        for (int j = 0; j < strList.size(); j++) {
            String stat = strList.get(j);
            String[] cells = new String[]
                    {stat};
            ExportUtil.setAllCell(wb, sheet, j + 1, cells, style);
        }
        
        HttpServletResponse p = null;
        
        ExportUtil.exportXls(wb, "激活数", p);
        
     //   ExportUtil.exportXls(wb, "激活数", new FileOutputStream(new File("f:/t.xls.zip")));

        //例子2，使用模板文件。
//        String templateName = "apply.xls";
//        String templateFilePath = "page/export/" + templateName;
//        HSSFWorkbook wb2 = null;
//        try {
//            wb2 = new HSSFWorkbook(new FileInputStream(templateFilePath));
//        } catch (Exception e) {
//
//        }
//        HSSFSheet sheet2 = wb2.getSheetAt(0);
//
//        //....
//        wb2.close();

    }

}
