package com.pl.indexserver.untils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    static final int BUFFER = 8192;

    public static OutputStream compress(OutputStream outputStream, boolean nameFlag, String... pathName) {
        ZipOutputStream out = null;
        try {
            if (null == outputStream) {
                outputStream = new ByteArrayOutputStream();
            }
            CheckedOutputStream cos = new CheckedOutputStream(outputStream, new CRC32());
            out = new ZipOutputStream(cos);
            String basedir = "";
            for (int i = 0; i < pathName.length; i++) {
                compress(new File(pathName[i]), out, basedir, nameFlag);
            }
            out.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return outputStream;
    }

    public static OutputStream compress(OutputStream outputStream, String srcPathName) {

        try {
            File file = new File(srcPathName);
            if (!file.exists())
                throw new RuntimeException(srcPathName + "不存在！");
            if (null == outputStream) {
                outputStream = new ByteArrayOutputStream();
            }
            CheckedOutputStream cos = new CheckedOutputStream(outputStream, new CRC32());
            ZipOutputStream out = new ZipOutputStream(cos);
            compress(file, out, "", false);
            out.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return outputStream;
    }

    private static void compress(File file, ZipOutputStream out, String basedir, boolean nameFlag) {
        /* 判断是目录还是文件 */
        logger.info("准备压缩--{},{}", file.getName(), file.isDirectory());
        if (file.isDirectory()) {
            compressDirectory(file, out, nameFlag);
        } else {
            compressFile(file, out, basedir, nameFlag);
        }
    }

    /**
     * 压缩一个目录
     */
    private static void compressDirectory(File dir, ZipOutputStream out, boolean nameFlag) {
        if (!dir.exists()) {
            return;
        }
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            /* 递归 */
            compress(files[i], out, dir.getName() + "/", nameFlag);
        }
    }

    /**
     * 压缩一个文件
     */
    private static void compressFile(File file, ZipOutputStream out, String basedir, boolean nameFlag) {
        if (!file.exists()) {
            return;
        }
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            String fileName = file.getName();
            if (nameFlag) {
                try {
                    String[] split1 = fileName.split("\\.");
                    String parent = file.getParent();
                    String[] split = parent.split("\\W");
                    fileName = split[split.length - 1] + "." + split1[split1.length - 1];
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //-----------
            ZipEntry entry = new ZipEntry(basedir + fileName);
            out.putNextEntry(entry);
            int count;
            byte data[] = new byte[BUFFER];
            long i = 0;
            while ((count = bis.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
                ++i;
            }
            logger.info("正在压缩文件【{}】,文件大小{},新文件名:{}", file.getName(), i, fileName);
            bis.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void copyDir(String srcDir, String desDir) {
        File files = new File(srcDir);
        File fileCopy = new File(desDir);
        logger.info("======srcDir======{}," +
                "======desDir======{}",srcDir,desDir);
        try {
            //判断是否是文件
            if (files.isDirectory()) {
                // 如果不存在，创建文件夹
                if (!fileCopy.exists()) {
                    fileCopy.mkdirs();
                }
                // 将文件夹下的文件存入文件数组
                String[] fs = files.list();
                for (String f : fs) {
                    //创建文件夹下的子目录
                    File srcFile = new File(files, f);
                    File destFile = new File(fileCopy, f);
                    // 将文件进行下一层循环
                    copyDir(srcFile.getAbsolutePath(), destFile.getAbsolutePath());
                }
            } else {
                // 创建文件输入的字节流用于读取文件内容，源文件
                FileInputStream fis = new FileInputStream(files);

                // 创建文件输出的字节流，用于将读取到的问件内容写到另一个磁盘文件中，目标文件
                FileOutputStream os = new FileOutputStream(fileCopy);
                logger.info("======files====={}," +
                        "======fileCopy====={}",files,fileCopy);

                // 创建字符串，用于缓冲
                int len = -1;
                byte[] b = new byte[1024];
                while (true) {
                    // 从文件输入流中读取数据。每执行一次,数据读到字节数组b中
                    len = fis.read(b);
                    if (len == -1) {
                        break;
                    }
                    os.write(b, 0, len);
                }
                fis.close();
                os.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        copyDir("C:\\Download", "C:\\Download1");

    }

}
