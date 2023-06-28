package com.tining.demonmarket.storage.bean;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.Date;

/**
 * 市场物品
 * @author tinga
 */
@Data
public class MarketItem {

    /**
     * 物品本体
     */
    private transient ItemStack itemStack;

    /**
     * 发布日期
     */
    private transient Date publicDate;

    /**
     * 物品名称
     */
    private String name;

    /**
     * 卖家名称
     */
    private String ownerName;

    /**
     * NBT串
     */
    private String info;

    /**
     * 物品数量
     */
    private int amount;

    /**
     * 价格
     */
    private Double price;

    /**
     * 发布日期字符串
     */
    private String dateString;
}
