package com.xtrarust.cloud.sharding.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xtrarust.cloud.db.mybatis.core.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单项实体
 *
 * @author gova
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_order_item")
public class OrderItem extends BaseDO {

    private static final long serialVersionUID = 263434701950670170L;

    @TableId(type = IdType.NONE)
    private Long orderItemId;

    private Long orderId;

    private Long userId;

    private Long skuId;

    private Integer quantity;

    private BigDecimal price;

    @Override
    public String toString() {
        return String.format("order_item_id:%s, order_id: %s, user_id: %s, sku_id: %s, quantity: %s, price: %s",
                orderItemId, orderId, userId, skuId, quantity, price);
    }
}
