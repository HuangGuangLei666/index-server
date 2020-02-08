package com.pl.indexserver.untils.jiguang.account;

import cn.jiguang.common.resp.BaseResult;
import com.google.gson.annotations.Expose;

public class AppBalanceResult extends BaseResult {

    @Expose
    int app_balance;

    public int getAppBalance() {
        return app_balance;
    }
}
