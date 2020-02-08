package com.pl.indexserver.untils;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.pl.indexserver.web.FileTransferContorller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class FTPUtils {

    public static final Logger logger = LoggerFactory.getLogger(FTPUtils.class);
    


    /**
     * 获取ftp服务器的连接
     *
     * @param ftpAddress  ftp服务器的地址
     * @param ftpPort     ftp服务器的端口号
     * @param ftpName     ftp用户名
     * @param ftpPassword ftp密码
     * @param conding     设置编码格式
     * @return
     */
    public static FTPClient getFTPClient(String ftpAddress, int ftpPort, String ftpName, String ftpPassword, String conding) throws Exception {
        FTPClient ftpClient = null;
        try {
            ftpClient = new FTPClient();
            if (null == conding || "".equals(conding)) {
                conding = "UTF-8";
            }
            ftpClient.setControlEncoding(conding);
            ftpClient.connect(ftpAddress, ftpPort);
            ftpClient.login(ftpName, ftpPassword);
            // 设置文件类型为二进制传输
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_UNIX);
            ftpClient.configure(conf);
            ftpClient.setRemoteVerificationEnabled(false);
            ftpClient.enterLocalPassiveMode();
        } catch (Exception e) {
            throw e;
        }
        return ftpClient;
    }

    /**
     * 获取ftp服务器上指定的资源
     *
     * @param ftpClient    ftp连接
     * @param ftpPath      ftp服务器上的文件目录(若为null则默认为根目录下)
     * @param fileName     具体文件名
     * @param outputStream 指定输出流
     * @return 返回资源文件的输出流
     */
    public static OutputStream downloadFile(FTPClient ftpClient, String ftpPath, String fileName, OutputStream outputStream) throws Exception {
        try {
            if (null == ftpPath || "".equals(ftpPath)) {
                ftpPath = "/";
            }
            ftpClient.changeWorkingDirectory(ftpPath);
            ftpClient.retrieveFile(ftpPath + "/" + fileName, outputStream);
        } catch (Exception e) {
            logger.info("获取ftp资源出现异常 :  ", e);
        }
        return outputStream;
    }

    /**
     * 将指定资源上传到ftp服务器
     *
     * @param ftpClient   ftp连接
     * @param ftpPath     指定ftp服务器上的文件目录(若为null则默认为根目录下)
     * @param fileName    指定文件名
     * @param inputStream
     * @return
     */
    public static boolean uploadFile(FTPClient ftpClient, String ftpPath, String fileName, InputStream inputStream) throws Exception {
        boolean isSuccess;
        try {
        	
        	 
        	 
            boolean flag = ftpClient.changeWorkingDirectory("/mnt/" + ftpPath);
            if (!flag) {
                ftpClient.changeWorkingDirectory("/");
                String[] paths = ftpPath.split("/");
                for (int i = 0; i < paths.length; i++) {
                    if (ftpClient.changeWorkingDirectory(paths[i]) || "".equals(paths[i])) {
                        continue;
                    }
                    ftpClient.makeDirectory(paths[i]);
                    ftpClient.changeWorkingDirectory(paths[i]);
                }
                boolean b = ftpClient.changeWorkingDirectory(ftpPath);
                logger.info(b + " ftpClient.changeWorkingDirectory  fileName=" +  ftpPath );
            }
            
            isSuccess = ftpClient.storeFile(fileName, inputStream);
            logger.info(isSuccess + "ftpPath=" + ftpPath+  " fileName=" +  fileName );
        } catch (Exception e) {
            isSuccess = false;
            logger.info("上传资源到ftp出现异常 :  ", e);
        }
        return isSuccess;

    }

    /**
     * 将指定资源上传到ftp服务器(多文件上传)
     *
     * @param ftpClient ftp连接
     * @param ftpPath   指定ftp服务器上的文件目录(若为null则默认为根目录下)
     * @param files     指定文件名和文件流
     * @return
     */
    public static boolean uploadFiles(FTPClient ftpClient, String ftpPath, Map<String, InputStream> files) throws Exception {
        boolean isSuccess = true;
        try {
            // 检查文件检查文件路径是否存在,如果不存在则创建
            boolean flag = ftpClient.changeWorkingDirectory(null == ftpPath || "".equals(ftpPath) ? "/" : ftpPath);
            if (!flag) {
                ftpClient.changeWorkingDirectory("/");
                String[] paths = ftpPath.split("/");
                for (int i = 0; i < paths.length; i++) {
                    if (ftpClient.changeWorkingDirectory(paths[i]) || "".equals(paths[i])) {
                        continue;
                    }
                    ftpClient.makeDirectory(paths[i]);
                    ftpClient.changeWorkingDirectory(paths[i]);
                }
                ftpClient.changeWorkingDirectory(ftpPath);
                logger.info("ftpPath="+ftpPath);
            }
            // 遍历map集合上传文件
            for (Map.Entry<String, InputStream> entry : files.entrySet()) {
                InputStream file = entry.getValue();
                
               
                boolean b = ftpClient.storeFile(entry.getKey(), file);
                logger.info(b + " ftp info = " +entry);
                if (!b) {
                    isSuccess = false;
                } else {
                    file.close();
                }
            }
        } catch (Exception e) {
            isSuccess = false;
            logger.info("上传资源到ftp出现异常 :  ", e);
        }
        return isSuccess;

    }

    /**
     * 删除ftp服务器上指定的资源
     *
     * @param ftpClient ftp连接
     * @param ftpPath   ftp服务器上的文件目录
     * @param fileNames 具体文件名
     * @return 返回操作结果
     */
    public static boolean deleteFiles(FTPClient ftpClient, String ftpPath, String[] fileNames) throws Exception {
        boolean result = true;
        try {
            boolean flag = ftpClient.changeWorkingDirectory(ftpPath);
            if (flag) {
                for (String filName:fileNames) {
                    if (StringUtils.isEmpty(filName)) {
                        continue;
                    }
                    ftpClient.deleteFile(filName);
                }
            }
        } catch (Exception e) {
            result = false;
            logger.info("删除ftp资源出现异常 :  ", e);
        }
        return result;
    }

    /**
     * 修改ftp服务器上指定的资源文件名
     *
     * @param ftpClient ftp连接
     * @param ftpPath   ftp服务器上的文件目录(若为null则默认为根目录下)
     * @param fileNames 包含文件名的map集合（原文件名为K，新文件名为V）
     * @return 返回操作结果
     */
    public static boolean rnameFiles(FTPClient ftpClient, String ftpPath,
                                     Map<String, String> fileNames) throws Exception {
        boolean result = true;
        try {
            boolean flag = ftpClient.changeWorkingDirectory(ftpPath);
            if (flag) {
                // 遍历map集合修改文件名
                for (Map.Entry<String, String> entry : fileNames.entrySet()) {
                    boolean rename = ftpClient.rename(entry.getKey(), entry.getValue());
                    if (!rename) {
                        result = false;
                    }
                }
            }
        } catch (Exception e) {
            result = false;
            logger.info("重命名ftp资源出现异常 :  ", e);
        }
        return result;
    }

    /**
     * 检查ftp服务器上指定路径下是否存在指定文件
     *
     * @param ftpClient ftp连接
     * @param ftpPath   ftp服务器上的文件目录(若为null则默认为根目录下)
     * @param fileName  需检查的文件名
     * @return 返回操作结果
     */
    public static boolean fileExist(FTPClient ftpClient, String ftpPath, String fileName) throws Exception {
        boolean result = true;
        try {
            boolean flag = ftpClient.changeWorkingDirectory(ftpPath);
            if (flag) {
                // 检查指定文件是否存在
                InputStream is = ftpClient
                        .retrieveFileStream(new String(fileName.getBytes("UTF-8"), FTPClient.DEFAULT_CONTROL_ENCODING));
                if (null == is || ftpClient.getReplyCode() == FTPReply.FILE_UNAVAILABLE) {
                    result = false;
                }
            } else {
                result = false;
            }
        } catch (Exception e) {
            result = false;
            logger.info("检查ftp资源文件出现异常 :  ", e);
        }
        return result;
    }

    /**
     * 将ftp服务器上指定路径下的指定文件拷贝到另一目录下并指定文件名
     *
     * @param ftpClient   ftp连接
     * @param ftpPath     拷贝文件的文件目录(若为null则默认为根目录下)
     * @param newFtpPath  新目录(若为null则默认为原目录下)
     * @param fileName    拷贝的文件名
     * @param newFileName 新的文件名(若为null则默认为原文件名)
     * @return 返回操作结果
     */
    public static boolean copyFile(FTPClient ftpClient, String ftpPath,
                                   String newFtpPath, String fileName, String newFileName) throws Exception {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        boolean result = false;
        try {
            ftpClient.changeWorkingDirectory("/");
            if (null == newFtpPath || "".equals(newFtpPath)) {
                newFtpPath = ftpPath;
            }
            if (null == newFileName || "".equals(newFileName)) {
                newFileName = fileName;
            }
            if (fileName.equals(newFileName) && ftpPath.equals(newFtpPath)) {
                throw new Exception("请重新输入新文件路径或者新文件名！");
            }
            os = new ByteArrayOutputStream();
            downloadFile(ftpClient, ftpPath, fileName, os);
            is = new ByteArrayInputStream(os.toByteArray());
            result = uploadFile(ftpClient, newFtpPath, newFileName, is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
                if (null != os) {
                    os.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 查看ftp服务器上指定路径的文件夹所占用的磁盘空间
     *
     * @param ftpClient ftp连接
     * @param ftpPath   ftp服务器上的文件目录
     * @return 返回结果值
     */
    public static long countForFtp(FTPClient ftpClient, String ftpPath) throws Exception {
        if (null == ftpPath || "".equals(ftpPath)) {
            throw new NullPointerException("请输入正确的文件路径!");
        }
        return count(ftpClient, ftpPath);
    }


    /**
     * 统计FTP上指定文件夹所占用的磁盘大小
     *
     * @param ftpClient FTP连接
     * @param filePath  指定文件夹
     * @return 返回统计结果
     * @throws Exception
     */
    public static long count(FTPClient ftpClient, String filePath) throws Exception {
        //定义一个初始值
        long size = 0;
        //判断路径是否存在
        boolean flag = ftpClient.changeWorkingDirectory(filePath);
        if (flag) {
            //获取该目录下的文件列表
            FTPFile[] ftpFiles = ftpClient.listFiles();
            if (ftpFiles.length > 0){
                for (FTPFile ftpFile: ftpFiles) {
                    String temp_filePath = filePath + "/" + ftpFile.getName();
                    //判断是否是文件夹
                    boolean flag_1 = ftpClient.changeWorkingDirectory(temp_filePath);
                    if (flag_1) {
                        //如果是文件夹则递归统计
                        size += count(ftpClient, temp_filePath);
                    } else {
                        //如果是文件则累加
                        size += ftpFile.getSize();
                    }
                }
            } else {
                logger.error("该文件路径下没用文件！");
            }
        } else {
            logger.error("文件路径不正确！");
        }
        return size;
    }

    /**
     * 拷贝文件夹下的文件（不包含子文件夹）
     *
     * @param ftpClient  ftp连接
     * @param oldFtpPath 被拷贝的目录
     * @param newFtpPath 拷贝至指定目录
     * @return
     * @throws Exception
     */
    public static boolean copyFolder(FTPClient ftpClient, String oldFtpPath, String newFtpPath) throws Exception {
        boolean flag = true;
        boolean b = ftpClient.changeWorkingDirectory(oldFtpPath);
        if (!b || StringUtils.isEmpty(newFtpPath)) {
            return false;
        }
        //获取文件列表（不包含文件夹）
        FTPFile[] ftpFiles = ftpClient.listFiles();
        for (FTPFile ftpFile : ftpFiles) {
            String fileName = ftpFile.getName();
            String temp_oldFilePath = oldFtpPath + "/" + ftpFile.getName();
            //判断是否是文件夹
            boolean b1 = ftpClient.changeWorkingDirectory(temp_oldFilePath);
            if (b1) {
                String temp_newFilePath = newFtpPath + "/" + ftpFile.getName();
                copyFolder(ftpClient, temp_oldFilePath, temp_newFilePath);
            } else {
                copyFile(ftpClient, oldFtpPath, newFtpPath, fileName, fileName);
            }
        }
        return flag;
    }

    /**
     * 退出关闭服务器链接
     */
    public static void logOut(FTPClient ftpClient) {
        if (null != ftpClient && ftpClient.isConnected()) {
            try {
                ftpClient.logout();// 退出FTP服务器
            } catch (Exception e) {
                logger.info("退出FTP服务器异常！", e);
            } finally {
                try {
                    ftpClient.disconnect();// 关闭FTP服务器的连接
                } catch (Exception e) {
                    logger.info("关闭FTP服务器的连接异常！", e);
                }
            }
        }
    }

}
