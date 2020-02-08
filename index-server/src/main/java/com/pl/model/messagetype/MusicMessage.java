package com.pl.model.messagetype;



/**
 * @author zhoumin
 * @create 2018-07-11 10:19
 */

public class MusicMessage extends BaseMessage {
    /**
     * 音乐
     */
    private Music Music;

    public com.pl.model.messagetype.Music getMusic() {
        return Music;
    }

    public void setMusic(com.pl.model.messagetype.Music music) {
        Music = music;
    }
}
