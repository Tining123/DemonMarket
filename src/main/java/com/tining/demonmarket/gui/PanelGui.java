package com.tining.demonmarket.gui;

import com.tining.demonmarket.common.ref.Vault;
import com.tining.demonmarket.common.util.BukkitUtil;
import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.common.util.MarketUtil;
import com.tining.demonmarket.common.util.PluginUtil;
import com.tining.demonmarket.economy.MarketEconomy;
import com.tining.demonmarket.gui.bean.PanelButtonEnum;
import com.tining.demonmarket.storage.ConfigReader;
import com.tining.demonmarket.storage.bean.MarketItem;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * 菜单界面
 */
@Data
public class PanelGui {
    private static final Logger log = Logger.getLogger("Minecraft");
    /**
     * 当前开启的菜单
     */
    private static final Map<UUID, PanelGui> MENU_OPENING = new ConcurrentHashMap<>();

    /**
     * 单页大小
     */
    private static final Integer PAGE_SIZE = 54;

    /**
     * 可视区域大小
     */
    public static final Integer VIEW_SIZE = 45;

    /**
     * 所在的箱子的实体
     */
    public Inventory inventory;

    /**
     * 收购列表名称
     */
    public static final String GUI_NAME = "菜单面板";

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
    public static PanelGui getPanelGui(Player player) {
        PanelGui panelGui = new PanelGui();
        panelGui.inventory = Bukkit.createInventory(player, PAGE_SIZE, LangUtil.get(GUI_NAME));
        panelGui.player = player;

        panelGui.registerPanelGui();
        panelGui.openPanelGui();

        drawPage(panelGui.getInventory());
        return panelGui;
    }

    /**
     * 执行操作决定
     * @param slot
     */
    public static void makeDecision(Player player, int slot, String displayName){
        if(!isDisplayNameValid(displayName)){
            return;
        }

        if(slot == PanelButtonEnum.GUI.getIndex()){
            player.closeInventory();
            PanelGui.unRegisterPanelGui(player);
            player.performCommand(PanelButtonEnum.GUI.getCommand());
        }else if(slot == PanelButtonEnum.SHOP.getIndex()){
            player.closeInventory();
            PanelGui.unRegisterPanelGui(player);
            player.performCommand(PanelButtonEnum.SHOP.getCommand());
        }else if(slot == PanelButtonEnum.MARKET.getIndex()){
            player.closeInventory();
            PanelGui.unRegisterPanelGui(player);
            player.performCommand(PanelButtonEnum.MARKET.getCommand());
        }else if(slot == PanelButtonEnum.LIST.getIndex()){
            player.closeInventory();
            PanelGui.unRegisterPanelGui(player);
            player.performCommand(PanelButtonEnum.LIST.getCommand());
        }else if(slot == PanelButtonEnum.HELP.getIndex()) {
            player.closeInventory();
            PanelGui.unRegisterPanelGui(player);
            player.performCommand(PanelButtonEnum.HELP.getCommand());
        }
    }

    /**
     * 校验是否菜单物品
     * @param displayName
     * @return
     */
    private static boolean isDisplayNameValid(String displayName) {
        return displayName.equals(LangUtil.get(PanelButtonEnum.GUI.getText())) ||
                displayName.equals(LangUtil.get(PanelButtonEnum.SHOP.getText()))||
                displayName.equals(LangUtil.get(PanelButtonEnum.MARKET.getText()))||
                displayName.equals(LangUtil.get(PanelButtonEnum.LIST.getText()))||
                displayName.equals(LangUtil.get(PanelButtonEnum.HELP.getText()));
    }

    /**
     * 绘制第N页的列表
     *
     */
    private static void drawPage(Inventory inventory) {
        setSign(inventory, PanelButtonEnum.GUI.getMaterial(), PanelButtonEnum.GUI.getIndex(), LangUtil.get(PanelButtonEnum.GUI.getText()));
        setSign(inventory, PanelButtonEnum.SHOP.getMaterial(), PanelButtonEnum.SHOP.getIndex(), LangUtil.get(PanelButtonEnum.SHOP.getText()));
        setSign(inventory, PanelButtonEnum.MARKET.getMaterial(), PanelButtonEnum.MARKET.getIndex(), LangUtil.get(PanelButtonEnum.MARKET.getText()));
        setSign(inventory, PanelButtonEnum.LIST.getMaterial(), PanelButtonEnum.LIST.getIndex(), LangUtil.get(PanelButtonEnum.LIST.getText()));
        setSign(inventory, PanelButtonEnum.HELP.getMaterial(), PanelButtonEnum.HELP.getIndex(), LangUtil.get(PanelButtonEnum.HELP.getText()));
    }

    /**
     * 设置坐标
     * @param inventory
     * @param material
     * @param name
     */
    private static ItemStack setSign(Inventory inventory, Material material, Integer index, String name){
        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);
        inventory.setItem(index, itemStack);
        return itemStack;
    }

    /**
     * 取消打开界面的注册
     * @param player
     */
    public static void unRegisterPanelGui(Player player) {
        MENU_OPENING.remove(player.getUniqueId());
    }

    /**
     * 查看是否在册
     * @param player
     * @return
     */
    public static boolean isPanelGui(Player player) {
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
    public static PanelGui getMyPanelGui(Player player){
        return MENU_OPENING.getOrDefault(player.getUniqueId(), null);
    }

    /**
     * 打开这个收购箱子
     */
    private void openPanelGui() {
        player.openInventory(inventory);
    }

    /**
     * 把当前对象加入全局表
     */
    private void registerPanelGui() {
        MENU_OPENING.put(player.getUniqueId(), this);
    }
}
