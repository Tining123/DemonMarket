package com.tining.demonmarket.gui;

import com.tining.demonmarket.Main;
import com.tining.demonmarket.common.BukkitUtil;
import com.tining.demonmarket.common.WorthUtil;
import com.tining.demonmarket.economy.MarketEconomy;
import com.tining.demonmarket.economy.MarketTrade;
import com.tining.demonmarket.money.Vault;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ChestGui {
    /**
     * 当前开启的菜单
     */
    private static final Map<UUID, ChestGui> MENU_OPENING = new HashMap();

    /**
     * 所在的箱子的实体
     */
    public Inventory inventory;

    /**
     * 持有者
     */
    Player player;

    /**
     * 使用构造器构造
     */
    private ChestGui() {
    }

    /**
     * 获取一个界面
     *
     * @param player 玩家
     * @return 箱子对象
     */
    public static ChestGui getChestGui(Player player) {
        ChestGui chestGui = new ChestGui();
        chestGui.inventory = Bukkit.createInventory(player, 54, "收购箱");
        chestGui.player = player;

        chestGui.registerChestGui();
        chestGui.openChestGui();

        return chestGui;
    }

    /**
     * 结算出售
     *
     * @param player 玩家
     */
    public static void checkOutPlayer(Player player) {
        if (MENU_OPENING.containsKey(player.getUniqueId())) {
            ChestGui chestGui = MENU_OPENING.get(player.getUniqueId());
            chestGui.checkOut();
        }
    }

    /**
     * 卸载当前对象
     */
    public static void unRegisterChestGui(Player player) {
        MENU_OPENING.remove(player.getUniqueId());
    }

    /**
     * 结算当前
     *
     * @return
     */
    private String checkOut() {
        ItemStack[] slot = inventory.getContents();

        double money = Vault.checkCurrency(player.getUniqueId());
        double count = 0;

        for (ItemStack is : slot) {
            //如果不可出售，继续
            if (!MarketEconomy.isIllegalItem(is)) {
                //如果存在物品，返还
                if (!Objects.isNull(is) && is.getAmount() != 0) {
                    BukkitUtil.returnItem(player,is);
                }
                continue;
            }
            //进行循环出售
            double value = WorthUtil.getWorth(is);
            double price = MarketEconomy.getSellingPrice(value, is.getAmount(), money);
            count = count + price;
            money = money + price;
        }

        if(count != 0)
        {
            MarketTrade.trade(player,count);
        }

        return "";
    }

    /**
     * 打开这个收购箱子
     */
    private void openChestGui() {
        player.openInventory(inventory);
    }

    /**
     * 把当前对象加入全局表
     */
    private void registerChestGui() {
        MENU_OPENING.put(player.getUniqueId(), this);
    }

}
