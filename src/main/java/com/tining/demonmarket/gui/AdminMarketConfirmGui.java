package com.tining.demonmarket.gui;

import com.tining.demonmarket.common.ref.Vault;
import com.tining.demonmarket.common.util.BukkitUtil;
import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.common.util.MarketUtil;
import com.tining.demonmarket.common.util.PluginUtil;
import com.tining.demonmarket.storage.LogWriter;
import com.tining.demonmarket.storage.bean.MarketItem;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
/**
 * 管理员确认界面
 * @author tinga
 */
@Data
public class AdminMarketConfirmGui {
    private static final Logger log = Logger.getLogger("Minecraft");
    /**
     * 当前开启的菜单
     */
    private static final Map<UUID, AdminMarketConfirmGui> MENU_OPENING = new ConcurrentHashMap<>();

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
    private static Material CONFIRM_SIGN;
    static {
        try {
            CONFIRM_SIGN = Material.getMaterial("GREEN_WOOL");
            if(Objects.isNull(CONFIRM_SIGN)){
                CONFIRM_SIGN = Material.getMaterial("WOOL");
            }
        } catch (Exception e) {
            // 如果出现异常，将 CONFIRM_SIGN 赋值为 Material.WOOL
            CONFIRM_SIGN = Material.getMaterial("WOOL");
        }
    }

    /**
     * 取消图标
     */
    private static Material CANCEL_SIGN;
    static {
        try {
            CANCEL_SIGN = Material.getMaterial("RED_WOOL");
            if(Objects.isNull(CANCEL_SIGN)){
                CANCEL_SIGN = Material.getMaterial("WOOL");
            }
        } catch (Exception e) {
            // 如果出现异常，将 CONFIRM_SIGN 赋值为 Material.WOOL
            CANCEL_SIGN = Material.getMaterial("WOOL");
        }
    }

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
    public static final String GUI_NAME = "管理员市场确认购买页面";

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
    public static AdminMarketConfirmGui getAdminMarketConfirmGui(Player player, MarketItem marketItem){
        return getAdminMarketConfirmGui(player,marketItem,1);
    }

    /**
     * 获取一个界面
     *
     * @param player 玩家
     * @return 箱子对象
     */
    public static AdminMarketConfirmGui getAdminMarketConfirmGui(Player player, MarketItem marketItem, Integer stackNum) {
        AdminMarketConfirmGui adminMarketConfirmGui = new AdminMarketConfirmGui();
        adminMarketConfirmGui.inventory = Bukkit.createInventory(player, PAGE_SIZE, LangUtil.get(GUI_NAME));
        adminMarketConfirmGui.player = player;
        adminMarketConfirmGui.setMarketItem(marketItem);
        adminMarketConfirmGui.setStackNum(stackNum);

        adminMarketConfirmGui.registerAdminMarketConfirmGui();
        adminMarketConfirmGui.openAdminMarketConfirmGui();

        drawPage(adminMarketConfirmGui.getInventory(), stackNum, player);
        return adminMarketConfirmGui;
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
            AdminMarketConfirmGui.unRegisterAdminMarketConfirmGui(player);
            AdminMarketGui.getAdminMarketGui(player);
        }else if(slot == CONFIRM_SIGN_INDEX){
            // 管理员不用花钱
            double totalPrice = 0;
            if(player.getName().equals(marketItem.getOwnerName())){
                totalPrice = 0;
            }

            // 扣费
            Vault.subtractCurrency(player.getUniqueId(), totalPrice);
            // 发送物品
            BukkitUtil.returnItem(player, marketItem.getItemStack().clone());
            player.sendMessage(LangUtil.preColor(ChatColor.YELLOW , LangUtil.get("交易成功，花费：") + String.format("%.2f", totalPrice)));

            MarketUtil.removeFromMarket(marketItem.getOwnerName(), marketItem.getItemStack());

            player.closeInventory();
            AdminMarketConfirmGui.unRegisterAdminMarketConfirmGui(player);

        }
        return;
    }

    /**
     * 校验是否菜单物品
     * @param displayName
     * @return
     */
    private static boolean isDisplayNameValid(String displayName) {
        if(Objects.isNull(displayName)){
            return false;
        }

        return displayName.equals(LangUtil.get(CONFIRM_TEXT)) ||
                displayName.equals(LangUtil.get(CANCEL_TEXT));
    }

    /**
     * 绘制第N页的列表
     *
     */
    private static void drawPage(Inventory inventory, int amount, Player player) {

        // 设置购买物品
        AdminMarketConfirmGui adminMarketConfirmGui = AdminMarketConfirmGui.getMyAdminMarketConfirmGui(player);
        MarketItem marketItem = adminMarketConfirmGui.getMarketItem();
        ItemStack itemStack = marketItem.getItemStack().clone();
        itemStack.setAmount(amount);
        double price = marketItem.getPrice();
        if(player.getName().equals(marketItem.getOwnerName())) {
            price = 0;
        }
        PluginUtil.addLore(itemStack, Collections.singletonList(LangUtil.preColor(ChatColor.YELLOW , LangUtil.get("总价：$") + (price * amount))));
        if(player.getName().equals(marketItem.getOwnerName())) {
            PluginUtil.addLore(itemStack, Collections.singletonList(LangUtil.preColor(ChatColor.YELLOW ,  LangUtil.get("你是物品拥有者，可以免费取回"))));
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
    public static void unRegisterAdminMarketConfirmGui(Player player) {
        MENU_OPENING.remove(player.getUniqueId());
    }

    /**
     * 查看是否在册
     * @param player
     * @return
     */
    public static boolean isAdminMarketConfirmGui(Player player) {
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
    public static AdminMarketConfirmGui getMyAdminMarketConfirmGui(Player player){
        return MENU_OPENING.getOrDefault(player.getUniqueId(), null);
    }

    /**
     * 打开这个收购箱子
     */
    private void openAdminMarketConfirmGui() {
        player.openInventory(inventory);
    }

    /**
     * 把当前对象加入全局表
     */
    private void registerAdminMarketConfirmGui() {
        MENU_OPENING.put(player.getUniqueId(), this);
    }
}
