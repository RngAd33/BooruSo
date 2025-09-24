package com.rngad33.booruso.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 通用分页请求体
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageRequest {

    /**
     * 当前页号
     */
    private int current = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认升序）
     */
    private String sortOrder = "descend";

}