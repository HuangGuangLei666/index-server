package com.pl.indexserver.untils;

import sun.rmi.server.Activation;

import java.util.Random;

/**
 * @author HuangGuangLei
 * @Date 2019/11/10
 */
public class RandomUtils {
    public static String genActivationCode(){
        int  maxNum = 36;
        int i;
        int count = 0;
        char[] str = { '0', '1', '2', '3', '4', '5',
                '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
                'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
                'W', 'X', 'Y', 'Z' };
        StringBuffer pwd = new StringBuffer("");
        Random r = new Random();
        while(count < 8){
            i = Math.abs(r.nextInt(maxNum));
            if (i >= 0 && i < str.length) {
                pwd.append(str[i]);
                count ++;
            }
        }
        return pwd.toString();
    }


    public static String genSixcode(){
        int  maxNum = 36;
        int i;
        int count = 0;
        char[] str = { '0', '1', '2', '3', '4', '5',
                '6', '7', '8', '9' };
        StringBuffer pwd = new StringBuffer("");
        Random r = new Random();
        while(count < 6){
            i = Math.abs(r.nextInt(maxNum));
            if (i >= 0 && i < str.length) {
                pwd.append(str[i]);
                count ++;
            }
        }
        return pwd.toString();
    }


    public static String genEightOrgnum(){
        int  maxNum = 36;
        int i;
        int count = 0;
        char[] str = { '0', '1', '2', '3', '4', '5',
                '6', '7', '8', '9' };
        StringBuffer pwd = new StringBuffer("");
        Random r = new Random();
        while(count < 8){
            i = Math.abs(r.nextInt(maxNum));
            if (i >= 0 && i < str.length) {
                pwd.append(str[i]);
                count ++;
            }
        }
        return pwd.toString();
    }


    public static String generateOpenid(){
        int  maxNum = 36;
        int i;
        int count = 0;
        char[] str = { '0', '1', '2', '3', '4', '5',
                '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
                'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
                'W', 'X', 'Y', 'Z' };
        StringBuffer pwd = new StringBuffer("");
        Random r = new Random();
        while(count < 21){
            i = Math.abs(r.nextInt(maxNum));
            if (i >= 0 && i < str.length) {
                pwd.append(str[i]);
                count ++;
            }
        }
        return "Android"+pwd.toString();
    }

    public static void main(String[] args) {
        /*String randomNum = genOrgNum();
        System.out.println(randomNum);*/

        String randomNum = generateOpenid();
        System.out.println(randomNum);
    }
}
