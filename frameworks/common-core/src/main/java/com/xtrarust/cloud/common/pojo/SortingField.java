package com.xtrarust.cloud.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 排序字段 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortingField implements Serializable {

    /**
     * 升序
     */
    public static final String ORDER_ASC = "asc";

    /**
     * 降序
     */
    public static final String ORDER_DESC = "desc";

    /**
     * 字段
     */
    private String field;

    /**
     * 顺序
     */
    private String order;

}
