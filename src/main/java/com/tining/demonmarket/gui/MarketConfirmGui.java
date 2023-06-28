package com.tining.demonmarket.gui;

import com.tining.demonmarket.common.ref.Vault;
import com.tining.demonmarket.common.util.BukkitUtil;
import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.common.util.MarketUtil;
import com.tining.demonmarket.common.util.PluginUtil;
import com.tining.demonmarket.economy.MarketEconomy;
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
 * 确认购买界面
 */
@Data
public class MarketConfirmGui {
    private static final Logger log = Logger.getLogger("Minecraft");
    /**
     * 当前开启的菜单
     */
    private static final Map<UUID, MarketConfirmGui> MENU_OPENING = new ConcurrentHashMap<>();

    /**
     * 单页大小
     */
    private static final Integer PAGE_SIZE = 54;

    /**
     * 可视区域大小
     */
    public static final Integer VIEW_SIZE = 45;

    /**
     * 最大堆叠数量
     */
    public static final Integer MAX_STACK_NUM = 64;

    /**
     * 物品坐标
     */
    private static final Integer ITEM_SIGN_INDEX = 13;


    /**
     * 确认按钮坐标
     */
    private static final Integer CANCEL_SIGN_INDEX = 39;

    /**
     * 确认按钮坐标
     */
    private static final Integer CONFIRM_SIGN_INDEX = 41;

    /**
     * 确认图标
     */
    private static final Material CONFIRM_SIGN = Material.GREEN_WOOL;

    /**
     * 取消图标
     */
    private static final Material CANCEL_SIGN = Material.RED_WOOL;

    /**
     * 确认购买文本
     */
    private static final String CONFIRM_TEXT = "确认购买";

    /**
     * 取消购买文本
     */
    private static final String CANCEL_TEXT = "取消购买";

    /**
     * 确认购买的内容
     */
    private MarketItem marketItem;

    /**
     * 堆叠数量
     */
    private Integer stackNum;

    /**
     * 所在的箱子的实体
     */
    public Inventory inventory;

    /**
     * 收购列表名称
     */
    public static final String GUI_NAME = "市场确认购买页面";

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
    public static MarketConfirmGui getMarketConfirmGui(Player player, MarketItem marketItem){
        return getMarketConfirmGui(player,marketItem,1);
    }

    /**
     * 获取一个界面
     *
     * @param player 玩家
     * @return 箱子对象
     */
    public static MarketConfirmGui getMarketConfirmGui(Player player, MarketItem marketItem, Integer stackNum) {
        MarketConfirmGui marketConfirmGui = new MarketConfirmGui();
        marketConfirmGui.inventory = Bukkit.createInventory(player, PAGE_SIZE, LangUtil.get(GUI_NAME));
        marketConfirmGui.player = player;
        marketConfirmGui.setMarketItem(marketItem);
        marketConfirmGui.setStackNum(stackNum);

        marketConfirmGui.registerMarketConfirmGui();
        marketConfirmGui.openMarketConfirmGui();

        drawPage(marketConfirmGui.getInventory(), stackNum, player);
        return marketConfirmGui;
    }

    /**
     * 执行操作决定
     * @param marketItem
     * @param slot
     */
    public static void makeDecision(Player player, MarketItem marketItem, int slot, String displayName){
        if(!isDisplayNameValid(displayName)){
            return;
        }

        if(slot == CANCEL_SIGN_INDEX){
            player.closeInventory();
            MarketConfirmGui.unRegisterMarketConfirmGui(player);
            MarketGui.getMarketGui(player);
        }else if(slot == CONFIRM_SIGN_INDEX){
            double totalPrice = marketItem.getPrice();
            if(player.getName().equals(marketItem.getOwnerName())){
                totalPrice = 0;
            }
            // 检测余额
            if (Vault.checkCurrency(player.getUniqueId()) < totalPrice) {

                player.sendMessage(ChatColor.YELLOW + LangUtil.get("你没有足够的余额") + String.format("%.2f", totalPrice));
                return;
            }

            // 扣费
            Vault.subtractCurrency(player.getUniqueId(), marketItem.getPrice());
            // 给卖家转账
            OfflinePlayer reciever = Bukkit.getOfflinePlayer(marketItem.getOwnerName());
            double recieve = marketItem.getPrice();
            if(!ConfigReader.getDisableMarketDemonMarket()){
                double totalValue = recieve;
                int time = (int)(totalValue / ConfigReader.getPayUnit());
                double res = totalValue % ConfigReader.getPayUnit();

                double price = MarketEconomy.getSellingPrice(ConfigReader.getPayUnit(), time, Vault.checkCurrency(reciever.getUniqueId()));
                totalPrice += price;

                price = MarketEconomy.getSellingPrice(res, 1, Vault.checkCurrency(reciever.getUniqueId()));
                totalPrice += price;

                recieve = totalPrice;
            }
            Vault.addVaultCurrency(reciever, recieve);
            // 给收款人发消息
            try{
                Player onlineReceiver = Bukkit.getPlayer(marketItem.getOwnerName());
                onlineReceiver.sendMessage(ChatColor.YELLOW +
                        String.format(LangUtil.get("物品%s出售成功，从%s收到%s"),
                                marketItem.getName(), player.getName(), MarketEconomy.formatMoney(recieve)));
            }catch (Exception ignore){}



            // 发送物品
            BukkitUtil.returnItem(player, marketItem.getItemStack().clone());
            player.sendMessage(ChatColor.YELLOW + LangUtil.get("交易成功，花费：") + totalPrice);

            MarketUtil.removeFromMarket(marketItem.getOwnerName(), marketItem.getItemStack());

            player.closeInventory();
            MarketConfirmGui.unRegisterMarketConfirmGui(player);
        }
        return;
    }

    /**
     * 校验是否菜单物品
     * @param displayName
     * @return
     */
    private static boolean isDisplayNameValid(String displayName) {
        return displayName.equals(LangUtil.get(CONFIRM_TEXT)) ||
                displayName.equals(LangUtil.get(CANCEL_TEXT));
    }

    /**
     * 绘制第N页的列表
     *
     */
    private static void drawPage(Inventory inventory, int amount, Player player) {

        // 设置购买物品
        MarketConfirmGui marketConfirmGui = MarketConfirmGui.getMyMarketConfirmGui(player);
        MarketItem marketItem = marketConfirmGui.getMarketItem();
        ItemStack itemStack = marketItem.getItemStack().clone();
        itemStack.setAmount(amount);
        double price = marketItem.getPrice();
        if(player.getName().equals(marketItem.getOwnerName())) {
            price = 0;
        }
        PluginUtil.addLore(itemStack, Collections.singletonList(ChatColor.YELLOW + LangUtil.get("总价：$") + (price * amount)));
        if(player.getName().equals(marketItem.getOwnerName())) {
            PluginUtil.addLore(itemStack, Collections.singletonList(ChatColor.YELLOW +  LangUtil.get("你是物品拥有者，可以免费取回")));
        }
        inventory.setItem(ITEM_SIGN_INDEX, itemStack);

        setSign(inventory, CONFIRM_SIGN, CONFIRM_SIGN_INDEX, LangUtil.get(CONFIRM_TEXT));
        setSign(inventory, CANCEL_SIGN, CANCEL_SIGN_INDEX, LangUtil.get(CANCEL_TEXT));

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
    public static void unRegisterMarketConfirmGui(Player player) {
        MENU_OPENING.remove(player.getUniqueId());
    }

    /**
     * 查看是否在册
     * @param player
     * @return
     */
    public static boolean isMarketConfirmGui(Player player) {
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
    public static MarketConfirmGui getMyMarketConfirmGui(Player player){
        return MENU_OPENING.getOrDefault(player.getUniqueId(), null);
    }

    /**
     * 打开这个收购箱子
     */
    private void openMarketConfirmGui() {
        player.openInventory(inventory);
    }

    /**
     * 把当前对象加入全局表
     */
    private void registerMarketConfirmGui() {
        MENU_OPENING.put(player.getUniqueId(), this);
    }
}
