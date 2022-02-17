package com.tining.demonmarket.player;

import com.tining.demonmarket.common.PluginUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

import static org.bukkit.Bukkit.getLogger;


public class Inventory {

    //计算玩家背包中某种物品的数量
    public static int calcInventory(Player player, ItemStack itemStack) {
        int amountInInventory = 0;
        ItemStack[] itemStacks = player.getInventory().getContents();
        for (ItemStack is : itemStacks) {
            if  (Objects.equals(PluginUtil.getKeyName(is), PluginUtil.getKeyName(itemStack))) {
                amountInInventory += is.getAmount();
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
        player.getInventory().getItemInMainHand().setAmount(0);
    }

    /**
     * 删除全身对应物品
     * @param player
     * @param itemStack
     */
    public static void subtractAll(Player player,ItemStack itemStack){
        ItemStack[] itemStacks = player.getInventory().getContents();
        for(ItemStack is : itemStacks){
            if  (Objects.equals(PluginUtil.getKeyName(is), PluginUtil.getKeyName(itemStack))) {
                is.setAmount(0);
            }
        }
    }

    //从玩家的背包移除物品
    public static void subtractInventory(Player player, ItemStack itemStack, int amount) {
        ItemStack[] itemStacks = player.getInventory().getContents();
        for (int i = 0; amount > 0 && i < itemStacks.length; i++) {
            if (Objects.equals(PluginUtil.getKeyName(itemStacks[i]), PluginUtil.getKeyName(itemStack))) {
                if (itemStacks[i].getAmount() < amount) {
                    amount -= itemStacks[i].getAmount();
                    itemStacks[i].setAmount(0);
                } else {
                    itemStacks[i].setAmount(itemStacks[i].getAmount() - amount);
                    amount = 0;
                }
                player.getInventory().setItem(i, itemStacks[i]);
            }
        }
    }


    //为玩家的背包增加物品
    public static void addInventory(Player player, Material material, int amount) {
        for (; amount > material.getMaxStackSize(); amount -= material.getMaxStackSize()) {
            ItemStack itemStack = new ItemStack(material, material.getMaxStackSize());
            player.getInventory().addItem(itemStack);
        }
        ItemStack itemStack = new ItemStack(material, amount);
        player.getInventory().addItem(itemStack);
    }

    //计算背包中的空格子数
    public static int calcEmpty(Player player) {
        ItemStack[] itemStacks = player.getInventory().getContents();
        int count = 0;
        for (ItemStack itemStack : itemStacks) {
            if (itemStack == null) {
                count++;
            }
        }
        return count;
    }
}
