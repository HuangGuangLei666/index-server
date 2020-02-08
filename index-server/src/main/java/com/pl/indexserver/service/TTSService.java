package com.pl.indexserver.service;

import com.pl.indexserver.model.TtsProperty;

public interface TTSService {

    /**
     *
     * 在线合成音频文件并上传值FTP服务器
     *
     * @param content     文本内容
     * @param filePath    上传路径
     * @param fileName    上传文件名
     * @param ttsProperty tts属性信息
     * @return
     */
    long createRecordToFTP(String content, String filePath, String fileName, TtsProperty ttsProperty);


    long ssttsToFTP(String content, String filePath, String fileName, TtsProperty ttsProperty);
}
