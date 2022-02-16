package com.tining.demonmarket.player;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.Bukkit.getLogger;


public class Inventory {

    //计算玩家背包中某种物品的数量
    public static int calcInventory(Player player, Material material) {
        int amountInInventory = 0;
        ItemStack[] itemStacks = player.getInventory().getContents();
        for (ItemStack itemStack : itemStacks) {
            if (itemStack != null && itemStack.getType().equals(material)) amountInInventory += itemStack.getAmount();
        }
        return amountInInventory;
    }

    //从玩家的背包移除物品
    public static void subtractInventory(Player player, Material material, int amount) {
        ItemStack[] itemStacks = player.getInventory().getContents();
        for (int i = 0; amount > 0 && i < itemStacks.length; i++) {
            if (itemStacks[i] != null && itemStacks[i].getType().equals(material)) {
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
        for (;amount > material.getMaxStackSize(); amount -= material.getMaxStackSize()) {
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
