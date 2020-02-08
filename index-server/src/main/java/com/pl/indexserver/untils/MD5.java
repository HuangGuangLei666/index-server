package com.pl.indexserver.untils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class MD5 {
    /**Determine encrypt algorithm MD5*/
    private static final String ALGORITHM_MD5 = "MD5";
    /**UTF-8 Encoding*/
    private static final String UTF_8 = "UTF-8";

    /**
     * MD5 32bit Encrypt Methods.
     * @param readyEncryptStr ready encrypt string
     * @return String encrypt result string
     * @throws NoSuchAlgorithmException
     * */
    public static  String MD5_32bit(String readyEncryptStr) throws NoSuchAlgorithmException {
        if(readyEncryptStr != null){
            //Get MD5 digest algorithm's MessageDigest's instance.
            MessageDigest md = MessageDigest.getInstance(ALGORITHM_MD5);
            //Use specified byte update digest.
            md.update(readyEncryptStr.getBytes());
            //Get cipher text
            byte [] b = md.digest();
            //The cipher text converted to hexadecimal string
            StringBuilder su = new StringBuilder();
            //byte array switch hexadecimal number.
            for(int offset = 0,bLen = b.length; offset < bLen; offset++){
                String haxHex = Integer.toHexString(b[offset] & 0xFF);
                if(haxHex.length() < 2){
                    su.append("0");
                }
                su.append(haxHex);
            }
            return su.toString();
        }else{
            return null;
        }
    }

    /**
     * MD5 32bit Encrypt Methods.
     * @param readyEncryptStr ready encrypt string
     * @return String encrypt result string
     * @throws NoSuchAlgorithmException
     * */
    public static String MD5_32bit1(String readyEncryptStr) throws NoSuchAlgorithmException {
        if (readyEncryptStr != null) {
            //The cipher text converted to hexadecimal string
            StringBuilder su = new StringBuilder();
            //Get MD5 digest algorithm's MessageDigest's instance.
            MessageDigest md = MessageDigest.getInstance(ALGORITHM_MD5);
            byte[] b = md.digest(readyEncryptStr.getBytes());
            int temp = 0;
            //byte array switch hexadecimal number.
            for (int offset = 0, bLen = b.length; offset < bLen; offset++) {
                temp = b[offset];
                if (temp < 0) {
                    temp += 256;
                }
                int d1 = temp / 16;
                int d2 = temp % 16;
                su.append(Integer.toHexString(d1) + Integer.toHexString(d2));
            }
            return su.toString();
        } else {
            return null;
        }
    }
}
