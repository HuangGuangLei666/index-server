package com.pl.indexserver.model.algorithm;

import java.io.Serializable;

/**
 * 算法加密dto
 *
 * @Author bei.zhang
 * @Date 2018/11/20 16:34
 */
public class AlgorithmSecretDto implements Serializable {

    private static final long serialVersionUID = 6585230057650766021L;
    private String secret;
    private Long timestamp;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
