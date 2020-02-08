package com.pl.indexserver.service.impl;

import com.pl.indexserver.service.FileTransferService;
import com.pl.indexserver.untils.DateUtils;
import com.pl.indexserver.untils.FTPUtils;
import com.pl.indexserver.untils.FileUtil;
import com.pl.model.TDialog;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class FileTransferServiceImpl implements FileTransferService {

    private static final Logger logger = LoggerFactory.getLogger(FileTransferServiceImpl.class);


    // ************************ FTP相关属性 **************************
    @Value("${ftp.address}")
    private String ftpAddress; // 地址
    @Value("${ftp.port}")
    private int ftpPort; // 端口号
    @Value("${ftp.name}")
    private String ftpName; // 用户名
    @Value("${ftp.password}")
    private String ftpPassword; // 密码

    @Value("${recordings.ftpPath}")
    private String ftpPath;
    @Value("${recordings.ftpPath_local}")
    private String ftpPathLocal;

    @Override
    public boolean uploadFileToFTP(String filePath, String fileName, InputStream inputStream)
            throws Exception {
        if (null == fileName || "".equals(fileName)) {
            return true;
        }
        FTPClient ftpClient = getFTPClient();
        filePath = ftpPath + "/" + filePath;
        boolean flag = FTPUtils.uploadFile(ftpClient, filePath, fileName, inputStream);
        ftpClient.logout();
        try {
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public boolean uploadFilesToFTP(String filePath, Map<String, InputStream> files) throws Exception {
        FTPClient ftpClient = getFTPClient();
        filePath = ftpPath + "/" + filePath;
        boolean flag = FTPUtils.uploadFiles(ftpClient, filePath, files);
        ftpClient.logout();
        return flag;
    }

    @Override
    public OutputStream downloadFileByFTP(String filePath, String fileName, OutputStream outputStream)
            throws Exception {
        if (null == fileName || "".equals(fileName)) {
            return outputStream;
        }
        FTPClient ftpClient = getFTPClient();
        filePath = ftpPath + "/" + filePath;
        FTPUtils.downloadFile(ftpClient, filePath, fileName, outputStream);
        ftpClient.logout();
        return outputStream;
    }

    @Override
    public boolean deleteFilesByFTP(String filePath, String[] fileNames) throws Exception {
        FTPClient ftpClient = getFTPClient();
        filePath = ftpPath + "/" + filePath;
        boolean flag = FTPUtils.deleteFiles(ftpClient, filePath, fileNames);
        ftpClient.logout();
        return flag;
    }

    @Override
    public boolean rnameFilesByFTP(String filePath, Map<String, String> fileNames) throws Exception {
        FTPClient ftpClient = getFTPClient();
        filePath = ftpPath + "/" + filePath;
        boolean flag = FTPUtils.rnameFiles(ftpClient, filePath, fileNames);
        ftpClient.logout();
        return flag;
    }

    @Override
    public boolean copyFile(String filePath, String fileName, String newFileName) throws Exception {
        FTPClient ftpClient = getFTPClient();
        filePath = ftpPath + "/" + filePath;
        boolean flag = FTPUtils.copyFile(ftpClient, filePath, null, fileName, newFileName);
        ftpClient.logout();
        return flag;
    }

    @Override
    public long countForFtp(String filePath) throws Exception {
        FTPClient ftpClient = getFTPClient();
        filePath = ftpPath + "/" + filePath;
        long count = FTPUtils.countForFtp(ftpClient, filePath);
        ftpClient.logout();
        return count;
    }

    //---------------------------------------------------------------------------------------------------------------------
    @Override
    public FTPClient getFTPClient() throws Exception {
        return FTPUtils.getFTPClient(ftpAddress, ftpPort, ftpName, ftpPassword, null);
    }

    @Override
    public boolean uploadFileToFTP(FTPClient ftpClient, String filePath, String fileName, InputStream inputStream) throws Exception {
        if (null == fileName || "".equals(fileName)) {
            return true;
        }
        filePath = ftpPath + "/" + filePath;
        return FTPUtils.uploadFile(ftpClient, filePath, fileName, inputStream);
    }

    @Override
    public boolean uploadFilesToFTP(FTPClient ftpClient, String filePath, Map<String, InputStream> files) throws Exception {
        filePath = ftpPath + "/" + filePath;
        return FTPUtils.uploadFiles(ftpClient, filePath, files);
    }

    @Override
    public OutputStream downloadFileByFTP(FTPClient ftpClient, String filePath, String fileName, OutputStream outputStream) throws Exception {
        if (null == fileName || "".equals(fileName)) {
            return outputStream;
        }
        filePath = ftpPath + "/" + filePath;
        return FTPUtils.downloadFile(ftpClient, filePath, fileName, outputStream);
    }

    @Override
    public boolean deleteFilesByFTP(FTPClient ftpClient, String filePath, String[] fileNames) throws Exception {
        filePath = ftpPath + "/" + filePath;
        return FTPUtils.deleteFiles(ftpClient, filePath, fileNames);
    }

    @Override
    public boolean rnameFilesByFTP(FTPClient ftpClient, String filePath, Map<String, String> fileNames) throws Exception {
        filePath = ftpPath + "/" + filePath;
        return FTPUtils.rnameFiles(ftpClient, filePath, fileNames);
    }

    @Override
    public boolean copyFile(FTPClient ftpClient, String filePath, String fileName, String newFileName) throws Exception {
        filePath = ftpPath + "/" + filePath;
        return FTPUtils.copyFile(ftpClient, filePath, null, fileName, newFileName);
    }

    @Override
    public long countForFtp(FTPClient ftpClient, String filePath) throws Exception {
        filePath = ftpPath + "/" + filePath;
        return FTPUtils.countForFtp(ftpClient, filePath);
    }

    @Override
    public boolean copyFolder(FTPClient ftpClient, String oldFtpPath, String newFtpPath) throws Exception {
        oldFtpPath = ftpPath + "/" + oldFtpPath;
        newFtpPath = ftpPath + "/" + newFtpPath;
        return FTPUtils.copyFolder(ftpClient, oldFtpPath, newFtpPath);
    }

    @Override
    public boolean copyFolder(String oldFtpPath, String newFtpPath) throws Exception {
        FTPClient ftpClient = getFTPClient();
        oldFtpPath = ftpPath + "/" + oldFtpPath;
        newFtpPath = ftpPath + "/" + newFtpPath;
        boolean flag = FTPUtils.copyFolder(ftpClient, oldFtpPath, newFtpPath);
        return flag;
    }

    @Override
    public OutputStream downloadResourcesToCompressFile(OutputStream outputStream, Long taskId, Long companyId, List<TDialog> tDialogs) throws Exception {
//        String pathPrefix = String.format("%s/%s/TASK-%s", ftpPathLocal, companyId, taskId);
               // ftpPathLocal + "/" + companyId + "/TASK-" + taskId;
        List<String> paths = new ArrayList<>();
        for (int i = 0; i < tDialogs.size(); i++) {
            TDialog tDialog = tDialogs.get(i);
            if (StringUtils.isEmpty(tDialog.getFile_path())) {
                continue;
            }
           // pathPrefix + "/" + tDialog.getTelephone() + "/" + tDialog.getFile_path() + ".wav"
            
            logger.info("===getFile_path" +tDialog.getFile_path());
            paths.add( tDialog.getFile_path() );
        }
        FileUtil.compress(outputStream, false, paths.toArray(new String[]{}));
        return outputStream;
    }

    @Override
    public String downloadResourcesToLocalCompressFile(Long taskId, Long companyId, String username, List<TDialog> tDialogs) throws Exception {
        String pathPrefix = ftpPathLocal;
        String date = DateUtils.getStringForDate(new Date(), DateUtils.DATEFORMAT_1);
        String localFileNname = String.format("TASK%d_%s_%s_%d%s", taskId, username, date.replaceAll("\\D", ""), System.nanoTime(), ".zip");
        String localFileParentPath = pathPrefix + "/export";
        File localPath = new File(localFileParentPath);
        if (!localPath.exists() || !localPath.isDirectory()) {
            localPath.mkdirs();
        }
        File locaFile = new File(localPath, localFileNname);
        if (!localPath.exists() || !localPath.isFile()) {
            locaFile.createNewFile();
        }
        OutputStream os = new FileOutputStream(locaFile);
        downloadResourcesToCompressFile(os, taskId, companyId, tDialogs);
        try {
            if (null != os) {
                os.flush();
                os.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("导出到本地文件成功~~~~~~~~~~文件位置【{}】", locaFile.getPath());
        return locaFile.getPath();
    }
}
