package com.pl.indexserver.model;

import java.util.Map;

public class PageFlowModel {
    public String title;

    public Map<String,PageFlowNodeModel> nodes;

    public Map<String,PageFlowLineModel> lines;

    public Object areas;

    public Integer initNum;
}
