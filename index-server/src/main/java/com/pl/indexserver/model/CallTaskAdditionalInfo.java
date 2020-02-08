package com.pl.indexserver.model;

import java.util.ArrayList;
import java.util.List;

public class CallTaskAdditionalInfo {

    private List<String> customerLabels = new ArrayList<>();

    public List<String> getCustomerLabels() {
        return customerLabels;
    }

    public void setCustomerLabels(List<String> customerLabels) {
        this.customerLabels = customerLabels;
    }
}
