package com.tining.demonmarket.common.util;

import com.google.gson.Gson;
import com.tining.demonmarket.Main;
import com.tining.demonmarket.common.ref.Updater;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
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

    /**
     * 获取插件当前版本
     * @return
     */
    public static String getVersion(){
        return Main.getInstance().getDescription().getVersion();
    }

    /**
     * 检查一名玩家是否为op
     * @param player
     * @return
     */
    public static boolean isOp(Player player){
        List<String> opUUIDs = new ArrayList<>();
        Set<OfflinePlayer> ops = Bukkit.getOperators();
        ops.forEach(e->opUUIDs.add(e.getUniqueId().toString()));
        return opUUIDs.contains(player.getUniqueId().toString());
    }

    /**
     * 获取一名玩家
     * @param name
     * @return
     */
    public static Player getPlayer(String name){
        return (Player) Bukkit.getPlayer(name);
    }

}
