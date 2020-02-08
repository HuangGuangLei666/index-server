package com.pl.model.wx;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/9/20
 */
public class GroupDto {
    private Integer id;
    private String groupName;
    private List<TBook> groupMemberPhones;
    private Long bussnessId;
    private String bussnessName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<TBook> getGroupMemberPhones() {
        return groupMemberPhones;
    }

    public void setGroupMemberPhones(List<TBook> groupMemberPhones) {
        this.groupMemberPhones = groupMemberPhones;
    }

    public Long getBussnessId() {
        return bussnessId;
    }

    public void setBussnessId(Long bussnessId) {
        this.bussnessId = bussnessId;
    }

    public String getBussnessName() {
        return bussnessName;
    }

    public void setBussnessName(String bussnessName) {
        this.bussnessName = bussnessName;
    }
}
