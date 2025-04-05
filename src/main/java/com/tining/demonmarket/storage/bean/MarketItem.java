package com.tining.demonmarket.storage.bean;

import com.tining.demonmarket.Main;
import com.tining.demonmarket.common.util.PluginUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
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
    @EqualsAndHashCode.Exclude
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
    @EqualsAndHashCode.Exclude
    private String dateString;
    /**
     * 返回物品信息字符串
     */
    public String toInfo() {
        DecimalFormat df = new DecimalFormat("#.00");
        String formattedPrice = df.format(price);
        String str = name + "-" + ownerName + "-" + amount + "-" + formattedPrice + "-" + dateString;
        str += "-" + PluginUtil.getKeyName(itemStack);
        return str;
    }

}
