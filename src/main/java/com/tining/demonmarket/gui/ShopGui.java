package com.tining.demonmarket.gui;

import com.tining.demonmarket.common.ref.Vault;
import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.common.util.PluginUtil;
import com.tining.demonmarket.common.util.WorthUtil;
import com.tining.demonmarket.economy.MarketEconomy;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * 商店界面
 */
public class ShopGui {
    /**
     * 当前开启的菜单
     */
    private static final Map<UUID, AcquireListGui> MENU_OPENING = new HashMap();

    /**
     * 单页大小
     */
    private static final Integer PAGE_SIZE = 54;

    /**
     * 可视区域大小
     */
    private static final Integer VIEW_SIZE = 45;

    /**
     * 向右翻页占位坐标
     */
    private static final Integer LEFT_ARROW_INDEX = 45;

    /**
     * 向左翻页占位坐标
     */
    private static final Integer RIGHT_ARROW_INDEX = 53;

    /**
     * 页码占位坐标
     */
    private static final Integer PAGE_SIGN_INDEX = 49;

    /**
     * 占位符图标
     */
    private static final Material PAGE_ARROW = Material.PAPER;

    /**
     * 页码图标
     */
    private static final Material PAGE_SIGN = Material.BOOK;

    /**
     * 所在的箱子的实体
     */
    public Inventory inventory;

    /**
     * 收购列表名称
     */
    private static final String GUI_NAME = "出售列表";

    /**
     * 持有者
     */
    Player player;



}
