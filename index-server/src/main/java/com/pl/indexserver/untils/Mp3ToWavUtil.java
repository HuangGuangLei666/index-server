package com.pl.indexserver.untils;

import com.pl.indexserver.web.WxController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2020/1/7 16:59
 * @Description 把深声tts生成的mp3格式的录音转化为wav
 */
public class Mp3ToWavUtil {

    private static final Logger logger = LoggerFactory.getLogger(Mp3ToWavUtil.class);

    public static int Mp3ToWav(String param){
        int retCode = 9999;
        String path = "/mnt/tm/2107/BUSINESS-134680606/mp32wav.sh";
        String params = "/mnt/tm/" + param;
//        String cmd = "/bin/sh " + path + " " + params;
        try {
            //解决脚本没有执行权限
            ProcessBuilder builder = new ProcessBuilder("/bin/chmod", "755",path);
            Process p = builder.start();
//            try {
//                p.waitFor();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", path + " " + params});
            try {
                retCode = process.waitFor();
            } catch (InterruptedException e) {
                logger.error("mp3转wav出异常了");
            }
//            logger.info("=======cmd={}==========",cmd);
            logger.info("=========retCode={}===========",retCode);
        } catch (IOException e) {
            logger.error("mp3转wav出异常了");
        }
        return retCode;
    }
}
