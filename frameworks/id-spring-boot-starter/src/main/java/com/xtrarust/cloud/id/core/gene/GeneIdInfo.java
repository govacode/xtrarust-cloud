package com.xtrarust.cloud.id.core.gene;

/**
 * 基因法雪花 ID 组成部分
 *
 * @param timestamp    时间戳
 * @param dataCenterId 数据中心 ID
 * @param workerId     工作机器节点 ID
 * @param sequence     自增序号，当高频模式下时，同一毫秒内生成 N 个 ID，则这个序号在同一毫秒下，自增以避免 ID 重复
 * @param gene         基因
 * @author gova
 */
public record GeneIdInfo(long timestamp, long dataCenterId, long workerId, long sequence, long gene) {

}
