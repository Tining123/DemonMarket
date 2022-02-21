package com.tining.demonmarket.common.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;


public class InventoryUtil {

    /**
     * 计算玩家背包中某种物品的数量
     */
    public static int calcInventory(Player player, ItemStack itemStack) {
        int amountInInventory = 0;
        ItemStack[] itemStacks = player.getInventory().getContents();
        for (ItemStack is : itemStacks) {
            if(!Objects.isNull(is)) {
                if (Objects.equals(PluginUtil.getKeyName(is), PluginUtil.getKeyName(itemStack))) {
                    amountInInventory += is.getAmount();
                }
            }
        }
        return amountInInventory;
    }

    /**
     * 删除手上对应物品
     * @param player
     * @param itemStack
     */
    public static void subtractHand(Player player,ItemStack itemStack){
        if(!Objects.isNull(itemStack)) {
            player.getInventory().getItemInMainHand().setAmount(0);
        }
    }

    /**
     * 删除全身对应物品
     * @param player
     * @param itemStack
     */
    public static void subtractAll(Player player,ItemStack itemStack){
        ItemStack[] itemStacks = player.getInventory().getContents();
        for(ItemStack is : itemStacks){
            if(!Objects.isNull(itemStack)) {
                if (Objects.equals(PluginUtil.getKeyName(is), PluginUtil.getKeyName(itemStack))) {
                    is.setAmount(0);
                }
            }
        }
    }

}
