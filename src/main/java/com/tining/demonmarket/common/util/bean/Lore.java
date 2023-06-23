package com.tining.demonmarket.common.util.bean;

import lombok.Builder;
import lombok.Data;
import org.bukkit.ChatColor;

/**
 * 补充物品lore用的bean
 * @author tinga
 */
@Data
@Builder
public class Lore {

    /**
     * 默认白色
     */
    ChatColor chatColor = ChatColor.WHITE;

    /**
     * lore内容
     */
    String lore;
}
