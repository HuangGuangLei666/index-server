package com.pl.model.wx;

import com.pl.thirdparty.dto.BaseModel;
import com.pl.thirdparty.dto.request.TemplateParamRequest;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @author HuangGuangLei
 * @Date 2019/10/29
 */
public class TemplateSendRequestDto extends BaseModel {
    @NotNull(message = "touser is null")
    private String touser;
    /**
     * 模板ID
     */
    @NotNull(message = "template_id is null")
    private String template_id;
    /**
     * 模板跳转链接
     */
    private String url;
    /**
     * 模板数据
     */
    @NotNull(message = "data is null")
    private Map<String, TemplateParamRequest> data;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, TemplateParamRequest> getData() {
        return data;
    }

    public void setData(Map<String, TemplateParamRequest> data) {
        this.data = data;
    }
}
