package com.tining.demonmarket.storage.bean;

import com.tining.demonmarket.gui.v1.DataV1;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public class Group extends DataV1 {

    /**
     * 商店物品存储标识串，带nbt
     */
    private String info;

    /**
     * 分组名称
     */
    private String name;
}
