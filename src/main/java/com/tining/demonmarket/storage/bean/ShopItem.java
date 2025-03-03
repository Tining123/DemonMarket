package com.tining.demonmarket.storage.bean;

import com.tining.demonmarket.gui.v1.DataV1;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.inventory.ItemStack;

/**
 * @author tinga
 */
@Data
@EqualsAndHashCode(of = {"type", "info", "name"}, callSuper = false)
public class ShopItem extends DataV1 {

    /**
     * 出售物品类型，不进行序列化
     */
    private transient ShopItemType itemType;

    /**
     * 物品本体
     */
    private transient ItemStack itemStack;

    /**
     * 出售物品类型的名称
     */
    private String type;

    /**
     * 物品存储标识串，如果是物品则和name相同，如果是nbt物品则是压缩串，需要反解析
     */
    private String info;

    /**
     *
     */
    private String name;

    /**
     * 价格
     */
    private Double price;

}
