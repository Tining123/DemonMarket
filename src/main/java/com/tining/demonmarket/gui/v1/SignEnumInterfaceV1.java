package com.tining.demonmarket.gui.v1;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * 标志枚举接口V1版本
 * @author tinga
 */
public interface SignEnumInterfaceV1 {
    int getSlot();
    String getLabel();
    void deal(Inventory inventory, Player player);
}
