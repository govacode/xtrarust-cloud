package com.xtrarust.cloud.oss.entity;

import lombok.Builder;
import lombok.Data;

/**
 * 文件上传结果
 *
 * @author gova
 */
@Data
@Builder
public class UploadResult {

    /**
     * 文件路径
     */
    private String url;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 已上传对象的实体标记（用来校验文件）
     */
    private String eTag;

}
