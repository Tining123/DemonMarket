package com.tining.demonmarket.gui;

import com.tining.demonmarket.common.util.BukkitUtil;
import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.common.util.PluginUtil;
import com.tining.demonmarket.common.util.WorthUtil;
import com.tining.demonmarket.economy.MarketEconomy;
import com.tining.demonmarket.economy.MarketTrade;
import com.tining.demonmarket.common.ref.Vault;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

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
     * 单页大小
     */
    private static final Integer PAGE_SIZE = 54;

    /**
     * 最右下角角落价格图标
     */
    private static final Material PRICE_SIGN = Material.PAPER;

    /**
     * 价格占位坐标
     */
    private static final Integer PRICE_INDEX = 53;

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
        chestGui.inventory = Bukkit.createInventory(player, PAGE_SIZE, LangUtil.get("收购箱"));
        chestGui.player = player;

        chestGui.registerChestGui();
        chestGui.openChestGui();

        //设置最后一个paper
        ItemStack priceToken = new ItemStack(PRICE_SIGN, 1);
        //更新paper
        ItemMeta itemMeta = priceToken.getItemMeta();
        itemMeta.setDisplayName(LangUtil.get("合计："));
        priceToken.setItemMeta(itemMeta);
        PluginUtil.addLore(priceToken, Collections.singletonList(ChatColor.YELLOW + LangUtil.get("总价：$") + 0));
        chestGui.inventory.setItem(PRICE_INDEX, priceToken);

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
     * 验证当前用户打开的箱子是否是在注册的收购箱
     *
     * @param player
     * @param inventory
     */
    public static boolean verifyChest(Player player, Inventory inventory) {
        return Objects.equals(MENU_OPENING.get(player.getUniqueId()), inventory);
    }

    /**
     * 获取当前所有在册UUID
     * @return
     */
    public static Set<UUID> getPlayerSet(){
        return MENU_OPENING.keySet();
    }

    /**
     * 是否是价格坐标处
     * @param slot
     * @return
     */
    public static boolean isPriceIndex(int slot){
        return slot == PRICE_INDEX;
    }

    /**
     * 判断是否在册
     *
     * @param player
     * @return
     */
    public static boolean isChestGui(Player player) {
        if (MENU_OPENING.containsKey(player.getUniqueId())) {
            return true;
        }
        return false;
    }


    /**
     * 结算当前收益
     *
     * @return
     */
    public static double checkOut(Inventory inventory, Player player) {
        ItemStack[] slot = inventory.getContents();

        double money = Vault.checkCurrency(player.getUniqueId());
        double count = 0;

        for (int i = 0; i < slot.length; i++) {
            //如果是占位符就跳过
            if (i == PRICE_INDEX) {
                continue;
            }

            ItemStack is = slot[i];
            //如果不可出售，继续
            if (!MarketEconomy.isIllegalItem(is)) {
                //如果存在物品，返还
                if (!Objects.isNull(is) && is.getAmount() != 0) {
                    BukkitUtil.returnItem(player, is);
                    inventory.setItem(i,new ItemStack(Material.AIR));
                    //TODO 这里需要一个消灭箱子里的安全措施
                    //TODO 否则这个函数注定只能在关闭时候触发
                }
                continue;
            }
            //进行循环计算
            double value = WorthUtil.getItemWorth(is);
            double price = MarketEconomy.getSellingPrice(value, is.getAmount(), money);
            count = count + price;
            money = money + price;
        }
        return count;
    }

    /**
     * 模拟结算当前收益
     *
     * @return
     */
    public static double preCheckOut(Inventory inventory, Player player) {
        ItemStack[] slot = inventory.getContents();

        double money = Vault.checkCurrency(player.getUniqueId());
        double count = 0;

        for (int i = 0; i < slot.length; i++) {
            //如果是占位符就跳过
            if (i == PRICE_INDEX) {
                continue;
            }

            ItemStack is = slot[i];
            //如果不可出售，继续
            if (!MarketEconomy.isIllegalItem(is)) {
                continue;
            }
            //进行循环计算
            double value = WorthUtil.getItemWorth(is);
            double price = MarketEconomy.getSellingPrice(value, is.getAmount(), money);
            count = count + price;
            money = money + price;
        }
        return count;
    }


    /**
     * 结算当前
     *
     * @return
     */
    private String checkOut() {
        ItemStack[] slot = inventory.getContents();
        double count = 0;
        count = ChestGui.checkOut(inventory, player);

        if (count != 0) {
            MarketTrade.trade(player, count);
        } else {
            player.sendMessage(ChatColor.YELLOW + LangUtil.get("[DemonMarket]你手里的物品无法交易"));
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

    /**
     * 绘制当前列表
     */
    public static void drawPage(Player player) {
        if (ChestGui.isChestGui(player)) {
            Inventory inventory = MENU_OPENING.get(player.getUniqueId()).inventory;
            drawPage(inventory,player);
        }
    }

    /**
     * 绘制当前列表
     */
    private static void drawPage(Inventory inventory, Player player) {
        //设置最后一个paper
        ItemStack priceToken = inventory.getItem(PRICE_INDEX);
        if(Objects.isNull(priceToken)){
            return;
        }
        //更新paper
        double count = ChestGui.preCheckOut(inventory,player);
        ItemMeta itemMeta = priceToken.getItemMeta();
        itemMeta.setDisplayName(LangUtil.get("合计："));
        itemMeta.setLore(Collections.singletonList(ChatColor.YELLOW + LangUtil.get("总价：$") + count));
        priceToken.setItemMeta(itemMeta);

    }

}
