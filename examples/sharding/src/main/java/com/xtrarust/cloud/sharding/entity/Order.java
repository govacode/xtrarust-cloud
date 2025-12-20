package com.xtrarust.cloud.sharding.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xtrarust.cloud.db.mybatis.core.entity.BaseDO;
import com.xtrarust.cloud.sharding.enums.OrderStatusEnum;
import lombok.*;

import java.math.BigDecimal;

/**
 * 订单实体
 *
 * @author gova
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_order")
public class Order extends BaseDO {

    private static final long serialVersionUID = 661434701950670670L;

    @TableId(type = IdType.NONE)
    private Long orderId;

    private Long shopId;

    private Long userId;

    private String mobile;

    // private String encryptedMobile;

    private Long addressId;

    private BigDecimal totalPrice;

    private OrderStatusEnum status;

    @Override
    public String toString() {
        return String.format("order_id: %s, shop_id: %s, user_id: %s, mobile: %s, address_id: %s, total_price: %s, status: %s",
                orderId, shopId, userId, mobile, addressId, totalPrice, status);
    }
}
