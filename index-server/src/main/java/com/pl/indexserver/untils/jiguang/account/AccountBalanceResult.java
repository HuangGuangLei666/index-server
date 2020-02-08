package com.pl.indexserver.untils.jiguang.account;

import cn.jiguang.common.resp.BaseResult;
import com.google.gson.annotations.Expose;

public class AccountBalanceResult extends BaseResult {
    @Expose
    int dev_balance;

    public int getDevBalance() {
        return dev_balance;
    }
}
