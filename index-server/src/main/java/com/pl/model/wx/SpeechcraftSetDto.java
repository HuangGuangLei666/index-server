package com.pl.model.wx;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/9/25
 */
public class SpeechcraftSetDto {
    private List<GroupSetDto> tGroups;
    private List<BookSetDto> tBooks;
    private List<VoiceSetDto> tVoices;
    private List<BusinessSetDto> tContents;
    private List<SysLabelDto> sysLabelDtos;

    public List<SysLabelDto> getSysLabelDtos() {
        return sysLabelDtos;
    }

    public void setSysLabelDtos(List<SysLabelDto> sysLabelDtos) {
        this.sysLabelDtos = sysLabelDtos;
    }

    public List<GroupSetDto> gettGroups() {
        return tGroups;
    }

    public void settGroups(List<GroupSetDto> tGroups) {
        this.tGroups = tGroups;
    }

    public List<BookSetDto> gettBooks() {
        return tBooks;
    }

    public void settBooks(List<BookSetDto> tBooks) {
        this.tBooks = tBooks;
    }

    public List<VoiceSetDto> gettVoices() {
        return tVoices;
    }

    public void settVoices(List<VoiceSetDto> tVoices) {
        this.tVoices = tVoices;
    }

    public List<BusinessSetDto> gettContents() {
        return tContents;
    }

    public void settContents(List<BusinessSetDto> tContents) {
        this.tContents = tContents;
    }
}
