package com.pl.indexserver.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pl.model.TBusinessConfig;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * 智库配置dto
 *
 * @Author bei.zhang
 * @Date 2018/8/8 18:12
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusinessConfigDto implements Serializable {

    private static final String DEFAULT_INTENT_A_JSON = "{\"condition_config\":\"or\",\"call_time\":75,\"call_wheel_num\":6,\"intent_num\":5}";
    private static final String DEFAULT_INTENT_B_JSON = "{\"condition_config\":\"or\",\"call_time\":60,\"call_wheel_num\":5,\"intent_num\":4}";
    private static final String DEFAULT_INTENT_C_JSON = "{\"condition_config\":\"or\",\"call_time\":40,\"call_wheel_num\":4,\"intent_num\":3}";
    private static final String DEFAULT_INTENT_D_JSON = "{\"condition_config\":\"or\",\"call_time\":30,\"call_wheel_num\":3,\"intent_num\":2}";

    private String configType;
    private String intentA;
    private String intentB;
    private String intentC;
    private String intentD;
    private String status;


    public BusinessConfigDto() {
    }

    public BusinessConfigDto(String configType) {
        this.configType = configType;
    }

    public String getIntentA() {
        return getDefaultIntentAValue(intentA);
    }

    public void setIntentA(String intentA) {
        this.intentA = intentA;
    }

    public String getIntentB() {
        return getDefaultIntentBValue(intentB);
    }

    public void setIntentB(String intentB) {
        this.intentB = intentB;
    }

    public String getIntentC() {
        return getDefaultIntentCValue(intentC);
    }

    public void setIntentC(String intentC) {
        this.intentC = intentC;
    }

    public String getIntentD() {
        return getDefaultIntentDValue(intentD);
    }

    public void setIntentD(String intentD) {
        this.intentD = intentD;
    }

    public String getStatus() {
        return getDefaultStatus(status);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    /**
     * 获取意向默认值
     *
     * @param status
     * @return
     */
    private String getDefaultStatus(String status) {
        if (status == null && !StringUtils.isEmpty(configType)) {
            if (TBusinessConfig.ConfigType.FOCUS.getCode().equals(configType)) {
                return TBusinessConfig.Status.UNUSED.getCode();
            } else if (TBusinessConfig.ConfigType.SCORE.getCode().equals(configType)) {
                return TBusinessConfig.Status.USED.getCode();
            } else if (TBusinessConfig.ConfigType.INTENT.getCode().equals(configType)) {
                return TBusinessConfig.Status.USED.getCode();
            } else if (TBusinessConfig.ConfigType.PUSH.getCode().equals(configType)) {
                return TBusinessConfig.Status.USED.getCode();
            }
        }
        return status;
    }

    /**
     * 获取意向默认值
     *
     * @param intentValue
     * @return
     */
    private String getDefaultIntentAValue(String intentValue) {
        if (intentValue == null && !StringUtils.isEmpty(configType)) {
            if (TBusinessConfig.ConfigType.FOCUS.getCode().equals(configType)) {
                return "";
            } else if (TBusinessConfig.ConfigType.SCORE.getCode().equals(configType)) {
                return "76";
            } else if (TBusinessConfig.ConfigType.INTENT.getCode().equals(configType)) {
                return DEFAULT_INTENT_A_JSON;
            } else if (TBusinessConfig.ConfigType.PUSH.getCode().equals(configType)) {
                return "1";
            }
        }
        return intentValue;
    }

    /**
     * 获取意向默认值
     *
     * @param intentValue
     * @return
     */
    private String getDefaultIntentBValue(String intentValue) {
        if (intentValue == null && !StringUtils.isEmpty(configType)) {
            if (TBusinessConfig.ConfigType.FOCUS.getCode().equals(configType)) {
                return "";
            } else if (TBusinessConfig.ConfigType.SCORE.getCode().equals(configType)) {
                return "51";
            } else if (TBusinessConfig.ConfigType.INTENT.getCode().equals(configType)) {
                return DEFAULT_INTENT_B_JSON;
            } else if (TBusinessConfig.ConfigType.PUSH.getCode().equals(configType)) {
                return "1";
            }
        }
        return intentValue;
    }

    /**
     * 获取意向默认值
     *
     * @param intentValue
     * @return
     */
    private String getDefaultIntentCValue(String intentValue) {
        if (intentValue == null && !StringUtils.isEmpty(configType)) {
            if (TBusinessConfig.ConfigType.FOCUS.getCode().equals(configType)) {
                return "";
            } else if (TBusinessConfig.ConfigType.SCORE.getCode().equals(configType)) {
                return "26";
            } else if (TBusinessConfig.ConfigType.INTENT.getCode().equals(configType)) {
                return DEFAULT_INTENT_C_JSON;
            } else if (TBusinessConfig.ConfigType.PUSH.getCode().equals(configType)) {
                return "1";
            }
        }
        return intentValue;
    }

    /**
     * 获取意向默认值
     *
     * @param intentValue
     * @return
     */
    private String getDefaultIntentDValue(String intentValue) {
        if (intentValue == null && !StringUtils.isEmpty(configType)) {
            if (TBusinessConfig.ConfigType.FOCUS.getCode().equals(configType)) {
                return "";
            } else if (TBusinessConfig.ConfigType.SCORE.getCode().equals(configType)) {
                return "1";
            } else if (TBusinessConfig.ConfigType.INTENT.getCode().equals(configType)) {
                return DEFAULT_INTENT_D_JSON;
            } else if (TBusinessConfig.ConfigType.PUSH.getCode().equals(configType)) {
                return "1";
            }
        }
        return intentValue;
    }
}
