package com.tining.demonmarket.gui;

import com.tining.demonmarket.common.ref.Vault;
import com.tining.demonmarket.common.util.*;
import com.tining.demonmarket.common.util.bean.Lore;
import com.tining.demonmarket.economy.MarketEconomy;
import com.tining.demonmarket.storage.bean.MarketItem;
import com.tining.demonmarket.storage.bean.ShopItem;
import lombok.Data;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * @author tinga
 */
@Data
public class MarketGui {
    private static final Logger log = Logger.getLogger("Minecraft");
    /**
     * 当前开启的菜单
     */
    private static final Map<UUID, MarketGui> MENU_OPENING = new ConcurrentHashMap();

    /**
     * 单页大小
     */
    private static final Integer PAGE_SIZE = 54;

    /**
     * 可视区域大小
     */
    public static final Integer VIEW_SIZE = 45;

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
    public static final String GUI_NAME = "市场列表";

    /**
     * 持有者
     */
    Player player;

    /**
     * 获取一个界面
     *
     * @param player 玩家
     * @return 箱子对象
     */
    public static MarketGui getMarketGui(Player player) {
        MarketGui marketGui = new MarketGui();
        marketGui.inventory = Bukkit.createInventory(player, PAGE_SIZE, LangUtil.get(GUI_NAME));
        marketGui.player = player;

        drawPage(marketGui.inventory, 0, player);


        marketGui.registerMarketGui();
        marketGui.openMarketGui();

        return marketGui;
    }

    /**
     * 绘制第N页的列表
     *
     * @param pageNum
     */
    private static void drawPage(Inventory inventory, int pageNum, Player player) {
        List<MarketItem> marketItemList = MarketUtil.MARKET_LIST;
        int move = 0;
        boolean set = false;

        List<ItemStack> list = new ArrayList<>();
        for(MarketItem marketItem : marketItemList){
            if(Objects.isNull(marketItem.getItemStack())){
                log.info("【DemonMarket】Null item stack warn: " + marketItem.toString());
                continue;
            }
            ItemStack itemStack = marketItem.getItemStack().clone();
            List<String> lores = new ArrayList<>();
            lores.add(LangUtil.preColor(ChatColor.YELLOW , LangUtil.get("售价：") + marketItem.getPrice()));
            lores.add((LangUtil.preColor(ChatColor.WHITE , LangUtil.get("卖家：") + marketItem.getOwnerName())));
            lores.add((LangUtil.preColor(ChatColor.WHITE , LangUtil.get("上架时间：") + DateUtil.formatDateToViewString(marketItem.getPublicDate()))));
            PluginUtil.addLore(itemStack, lores);
            list.add(itemStack);
        }

        for (int i = pageNum * VIEW_SIZE; i < list.size() && i < (pageNum + 1) * VIEW_SIZE; i++) {
            if (!Objects.isNull(list.get(i))) {
                if (!set) {
                    inventory.clear();
                    set = true;
                }
                inventory.setItem(move % VIEW_SIZE, list.get(i));
                move++;
            }
        }
        //设置翻页图标
        ItemStack left = new ItemStack(PAGE_ARROW, 1);
        ItemStack right = new ItemStack(PAGE_ARROW, 1);
        ItemStack mid = new ItemStack(PAGE_SIGN, 1);
        ItemMeta leftItemMeta = left.getItemMeta();
        ItemMeta rightItemMeta = right.getItemMeta();
        ItemMeta midItemMeta = right.getItemMeta();

        if (!Objects.isNull(leftItemMeta) && pageNum != 0 && pageNum != 1) {
            leftItemMeta.setDisplayName(LangUtil.get("上一页"));
            left.setItemMeta(leftItemMeta);
            inventory.setItem(LEFT_ARROW_INDEX, left);
        }
        if (!Objects.isNull(rightItemMeta) && move != 0 && (pageNum + 1) * VIEW_SIZE < list.size()) {
            rightItemMeta.setDisplayName(LangUtil.get("下一页"));
            right.setItemMeta(rightItemMeta);
            inventory.setItem(RIGHT_ARROW_INDEX, right);
        }
        if (!Objects.isNull(midItemMeta) && move != 0) {
            midItemMeta.setDisplayName("< " + (pageNum + 1) + " >");
            mid.setItemMeta(midItemMeta);
            inventory.setItem(PAGE_SIGN_INDEX, mid);
        }
    }

    /**
     * 绘制第N页的列表
     */
    public static void turnPage(Inventory inventory, int slot, Player player) {
        try {
            ItemStack itemStack = inventory.getItem(PAGE_SIGN_INDEX);
            String name = itemStack.getItemMeta().getDisplayName();
            int page = Integer.parseInt(name.replace("<", "").replace(">", "").trim());

            if (Objects.equals(slot, LEFT_ARROW_INDEX)) {
                if (page < 2) {
                    return;
                }
                drawPage(inventory, page - 2, player);
                return;
            }

            if (Objects.equals(slot, RIGHT_ARROW_INDEX)) {
                drawPage(inventory, page, player);
                return;
            }
        } catch (Exception e) {
        }
    }


    /**
     * 判断是否在册
     *
     * @param player
     * @return
     */
    public static boolean isMarketGui(Player player) {
        if (MENU_OPENING.containsKey(player.getUniqueId())) {
            return true;
        }
        return false;
    }

    /**
     * 获取玩家打开的界面
     * @param player
     * @return
     */
    public static MarketGui getMyMarketGui(Player player){
        return MENU_OPENING.getOrDefault(player.getUniqueId(), null);
    }

    /**
     * 根据名字是不是收购列表
     *
     * @param name
     * @return
     */
    public static boolean isMarketGui(String name) {
        if (StringUtils.equals(name, LangUtil.get(GUI_NAME))) {
            return true;
        }
        return false;
    }

    /**
     * 设置正在编辑的物品
     * @param inventory
     * @param slot
     * @param player
     */
    public static MarketItem getConfirmItem(Inventory inventory, int slot, Player player){
        if(slot >= VIEW_SIZE ){
            return null;
        }

        try {
            ItemStack itemStack = inventory.getItem(PAGE_SIGN_INDEX);
            String name = itemStack.getItemMeta().getDisplayName();
            int page = Integer.parseInt(name.replace("<", "").replace(">", "").trim());

            int index = (page - 1) * VIEW_SIZE + slot;
            return MarketUtil.getMarketItem(index);
        } catch (Exception e) {
        }
        return null;
    }


    /**
     * 打开这个收购箱子
     */
    private void openMarketGui() {
        player.openInventory(inventory);
    }

    /**
     * 卸载当前对象
     */
    public static void unRegisterMarketGui(Player player) {
        MENU_OPENING.remove(player.getUniqueId());
    }


    /**
     * 把当前对象加入全局表
     */
    private void registerMarketGui() {
        MENU_OPENING.put(player.getUniqueId(), this);
    }
}
