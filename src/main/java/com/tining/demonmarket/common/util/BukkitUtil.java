package com.tining.demonmarket.common.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * bukkit的工具类
 */
public class BukkitUtil {


    /**
     * 获取所有在线玩家
     * @return 玩家列表
     */
    public static List<Player> getAllOnlineUser(){
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    /**
     * 返还物品到玩家背包里，无法返还就掉落地上
     * @param player 玩家
     * @ItemStack 物品
     * @return 是否返还到背包中
     */
    public static boolean returnItem(Player player, ItemStack itemStack){
        if(player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(itemStack);
            return true;
        }
        else{
            player.getLocation().getWorld().dropItemNaturally(player.getLocation(),itemStack);
            return false;
        }
    }
}
