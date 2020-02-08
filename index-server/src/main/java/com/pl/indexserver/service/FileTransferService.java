package com.pl.indexserver.service;

import com.pl.model.TDialog;
import org.apache.commons.net.ftp.FTPClient;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * 文件传输的服务类
 */
public interface FileTransferService {

	/**
	 * 将指定资源上传到ftp服务器
	 *
	 * @param filePath    指定服务器上的文件目录(若为null则默认为根目录下)
	 * @param fileName    指定文件名
	 * @param inputStream 上传的文件流
	 * @return
	 */
	boolean uploadFileToFTP( String filePath, String fileName, InputStream inputStream) throws Exception;

	/**
	 * 将指定资源上传到ftp服务器
	 *
	 * @param filePath 指定服务器上的文件目录(若为null则默认为根目录下)
	 * @param files    文件名(K)和文件流(V)以Map结构传入
	 * @return
	 */
	boolean uploadFilesToFTP(String filePath, Map<String, InputStream> files) throws Exception;

	/**
	 * 获取ftp服务器上指定的资源
	 *
	 * @param filePath     服务器上的文件目录(若为null则默认为根目录下)
	 * @param fileName     具体文件名
	 * @param outputStream 指定输出流
	 * @return 返回资源文件的输出流
	 */
	OutputStream downloadFileByFTP(String filePath, String fileName, OutputStream outputStream)
			throws Exception;

	/**
	 * 删除ftp服务器上指定的资源
	 *
	 * @param filePath  服务器上的文件目录(若为null则默认为根目录下)
	 * @param fileNames 具体文件名数组
	 * @return 返回操作结果
	 */
	boolean deleteFilesByFTP(String filePath, String[] fileNames) throws Exception;

	/**
	 * 重命名ftp服务器上指定的文件
	 *
	 * @param filePath  文件路径
	 * @param fileNames 包含文件名的map集合（原文件名为K，新文件名为V）
	 * @return 返回操作结果
	 * @throws Exception
	 */
	boolean rnameFilesByFTP(String filePath, Map<String, String> fileNames) throws Exception;

	/**
	 * 将ftp服务器上指定路径下的指定文件拷贝到当前目录下并重命名
	 *
	 * @param filePath    指定拷贝文件的文件目录(若为null则默认为根目录下)
	 * @param fileName    指定拷贝的文件
	 * @param newFileName 粘贴后的文件名(若为null则默认为原文件名)
	 * @return 返回操作结果
	 */
	boolean copyFile(String filePath, String fileName, String newFileName) throws Exception;

	/**
	 * 查看ftp服务器上指定路径的文件夹所占用的磁盘空间
	 *
	 * @param filePath 指定文件目录
	 * @return 返回结果值
	 */
	long countForFtp(String filePath) throws Exception;

	/**
	 * 获取ftp连接
	 *
	 * @throws Exception
	 * @return 返回ftp连接
	 */
	FTPClient getFTPClient() throws Exception;

	/**
	 * 将指定资源上传到ftp服务器
	 *
	 * @param ftpClient   ftp连接
	 * @param filePath    指定服务器上的文件目录(若为null则默认为根目录下)
	 * @param fileName    指定文件名
	 * @param inputStream 上传的文件流
	 * @return
	 */
	boolean uploadFileToFTP(FTPClient ftpClient, String filePath, String fileName, InputStream inputStream) throws Exception;

	/**
	 * 将指定资源上传到ftp服务器
	 *
	 * @param ftpClient ftp连接
	 * @param files     文件名(K)和文件流(V)以Map结构传入
	 * @return
	 */
	boolean uploadFilesToFTP(FTPClient ftpClient,String filePath, Map<String, InputStream> files) throws Exception;

	/**
	 * 获取ftp服务器上指定的资源
	 *
	 * @param ftpClient    ftp连接
	 * @param filePath     服务器上的文件目录(若为null则默认为根目录下)
	 * @param fileName     具体文件名
	 * @param outputStream 指定输出流
	 * @return 返回资源文件的输出流
	 */
	OutputStream downloadFileByFTP(FTPClient ftpClient, String filePath, String fileName, OutputStream outputStream)
			throws Exception;

	/**
	 * 删除ftp服务器上指定的资源
	 *
	 * @param ftpClient ftp连接
	 * @param filePath  服务器上的文件目录(若为null则默认为根目录下)
	 * @param fileNames 具体文件名数组
	 * @return 返回操作结果
	 */
	boolean deleteFilesByFTP(FTPClient ftpClient, String filePath, String[] fileNames) throws Exception;

	/**
	 * 重命名ftp服务器上指定的文件
	 *
	 * @param ftpClient ftp连接
	 * @param filePath  文件路径
	 * @param fileNames 包含文件名的map集合（原文件名为K，新文件名为V）
	 * @return 返回操作结果
	 * @throws Exception
	 */
	boolean rnameFilesByFTP(FTPClient ftpClient, String filePath, Map<String, String> fileNames) throws Exception;

	/**
	 * 将ftp服务器上指定路径下的指定文件拷贝到当前目录下并重命名
	 *
	 * @param ftpClient ftp连接
	 * @param filePath    指定拷贝文件的文件目录(若为null则默认为根目录下)
	 * @param fileName    指定拷贝的文件
	 * @param newFileName 粘贴后的文件名(若为null则默认为原文件名)
	 * @return 返回操作结果
	 */
	boolean copyFile(FTPClient ftpClient,String filePath, String fileName, String newFileName) throws Exception;

	/**
	 * 查看ftp服务器上指定路径的文件夹所占用的磁盘空间
	 *
	 * @param ftpClient ftp连接
	 * @param filePath 指定文件目录
	 * @return 返回结果值
	 */
	long countForFtp(FTPClient ftpClient,String filePath) throws Exception;

	/**
	 * 拷贝文件夹下的文件（不包含子文件夹）
	 * @param ftpClient     ftp连接
	 * @param oldFtpPath    被拷贝的目录
	 * @param newFtpPath    拷贝至指定目录
	 * @return
	 * @throws Exception
	 */
	boolean copyFolder(FTPClient ftpClient, String oldFtpPath,String newFtpPath)throws Exception;
	/**
	 * 拷贝文件夹下的文件（不包含子文件夹）
	 * @param oldFtpPath    被拷贝的目录
	 * @param newFtpPath    拷贝至指定目录
	 * @return
	 * @throws Exception
	 */
	boolean copyFolder(String oldFtpPath,String newFtpPath)throws Exception;

	OutputStream downloadResourcesToCompressFile(OutputStream outputStream,Long taskId,Long companyId,List<TDialog> telephones) throws Exception;

	String downloadResourcesToLocalCompressFile(Long taskId,Long companyId,String username,List<TDialog> telephones) throws Exception;

}
