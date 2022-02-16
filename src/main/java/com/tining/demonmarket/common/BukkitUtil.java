package com.tining.demonmarket.common;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
}
