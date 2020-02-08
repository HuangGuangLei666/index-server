package com.pl.indexserver.model.redisdto;

import java.io.Serializable;

/**
 * 算法数据同步redis对象
 *
 * @Author bei.zhang
 * @Date 2018/11/23 11:16
 */
public class AlgorithmDataSynDto implements Serializable {

    /**
     * 类型
     */
    private TypeEnum type;
    /**
     * 操作
     */
    private OperationEnum operation;
    /**
     * 主键
     */
    private Long id;
    /**
     * 公司id
     */
    private Long companyId;
    /**
     * 智库id
     */
    private Long businessId;


    public AlgorithmDataSynDto() {
    }

    public AlgorithmDataSynDto(TypeEnum type, OperationEnum operation, Long id) {
        this.type = type;
        this.operation = operation;
        this.id = id;
    }

    public AlgorithmDataSynDto(TypeEnum type, OperationEnum operation, Long id, Long companyId, Long businessId) {
        this.type = type;
        this.operation = operation;
        this.id = id;
        this.companyId = companyId;
        this.businessId = businessId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public TypeEnum getType() {
        return type;
    }

    public void setType(TypeEnum type) {
        this.type = type;
    }

    public OperationEnum getOperation() {
        return operation;
    }

    public void setOperation(OperationEnum operation) {
        this.operation = operation;
    }

    public enum TypeEnum {
        KNOWLEDGE_QUESTION,
        Q_COMMON_CRAFT,
        RESPONSE_MODE
    }

    public enum OperationEnum {
        INSERT,
        UPDATE,
        DELETE
    }
}
