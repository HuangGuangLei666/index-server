package com.pl.indexserver.untils;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2020/1/9 14:58
 * @Description 因为不能有小数点，微信支付的金额先把传进来的金额*100
 */
public class BigIntegerUtil {
    public static BigInteger changeFee(String totalFee){
        //把金额先转化为BigDecimal
        BigDecimal decimal = new BigDecimal(totalFee);
        BigDecimal yibai = new BigDecimal(100);
        //乘以100
        BigDecimal bigDecimal = decimal.multiply(yibai);
        //再转化为BigInteger
        BigInteger bigInteger = bigDecimal.toBigInteger();
        return bigInteger;
    }
}
