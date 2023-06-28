package com.tining.demonmarket.gui;

import com.tining.demonmarket.common.ref.Vault;
import com.tining.demonmarket.common.util.BukkitUtil;
import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.common.util.PluginUtil;
import com.tining.demonmarket.storage.bean.ShopItem;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * 确认购买界面
 */
@Data
public class ShopConfirmGui {
    private static final Logger log = Logger.getLogger("Minecraft");
    /**
     * 当前开启的菜单
     */
    private static final Map<UUID, ShopConfirmGui> MENU_OPENING = new ConcurrentHashMap<>();

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
     * +1 坐标
     */
    private static final Integer PLUS_1_INDEX = 23;

    /**
     * -1 坐标
     */
    private static final Integer MINUS_1_INDEX = 21;

    /**
     * +8 坐标
     */
    private static final Integer PLUS_8_INDEX = 24;

    /**
     * -8 坐标
     */
    private static final Integer MINUS_8_INDEX = 20;

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
     * 增加数量图标
     */
    private static final Material PLUS_SIGN = Material.WHITE_WOOL;

    /**
     * 减少熟料图标
     */
    private static final Material MINUS_SIGN = Material.BLACK_WOOL;

    /**
     * 确认图标
     */
    private static final Material CONFIRM_SIGN = Material.GREEN_WOOL;

    /**
     * 取消图标
     */
    private static final Material CANCEL_SIGN = Material.RED_WOOL;

    /// 菜单字符常量 ///
    private static final String PLUS_1_TEXT = "+1";
    private static final String MINUS_1_TEXT = "-1";
    private static final String PLUS_8_TEXT = "+8";
    private static final String MINUS_8_TEXT = "-8";
    private static final String CONFIRM_TEXT = "确认购买";
    private static final String CANCEL_TEXT = "取消购买";

    /**
     * 确认购买的内容
     */
    private ShopItem shopItem;

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
    public static final String GUI_NAME = "确认购买页面";

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
    public static ShopConfirmGui getShopConfirmGui(Player player, ShopItem shopItem){
        return getShopConfirmGui(player,shopItem,1);
    }

    /**
     * 获取一个界面
     *
     * @param player 玩家
     * @return 箱子对象
     */
    public static ShopConfirmGui getShopConfirmGui(Player player, ShopItem shopItem, Integer stackNum) {
        ShopConfirmGui shopConfirmGui = new ShopConfirmGui();
        shopConfirmGui.inventory = Bukkit.createInventory(player, PAGE_SIZE, LangUtil.get(GUI_NAME));
        shopConfirmGui.player = player;
        shopConfirmGui.setShopItem(shopItem);
        shopConfirmGui.setStackNum(stackNum);

        shopConfirmGui.registerShopConfirmGui();
        shopConfirmGui.openShopConfirmGui();

        drawPage(shopConfirmGui.getInventory(), stackNum, player);
        return shopConfirmGui;
    }

    /**
     * 执行操作决定
     * @param shopItem
     * @param slot
     */
    public static void makeDecision(Player player, ShopItem shopItem, int amount, int slot, String displayName){
        if(!isDisplayNameValid(displayName)){
            return;
        }
        if (slot == PLUS_1_INDEX) {
            amount += 1;
        } else if (slot == PLUS_8_INDEX) {
            amount += 8;
        } else if (slot == MINUS_1_INDEX) {
            amount -= 1;
        } else if (slot == MINUS_8_INDEX) {
            amount -= 8;
        } else {
            if(slot == CANCEL_SIGN_INDEX){
                player.closeInventory();
                ShopConfirmGui.unRegisterShopConfirmGui(player);
                ShopGui.getShopGui(player);
            }else if(slot == CONFIRM_SIGN_INDEX){
                double totalPrice = amount * shopItem.getPrice();
                // 检测余额
                if (Vault.checkCurrency(player.getUniqueId()) < totalPrice) {

                    player.sendMessage(ChatColor.YELLOW + LangUtil.get("你没有足够的余额") + String.format("%.2f", totalPrice));
                    return;
                }
                // 发货
                for(int i = 0 ; i < amount;i++){
                    if (Vault.checkCurrency(player.getUniqueId()) < shopItem.getPrice()) {
                        player.sendMessage(ChatColor.YELLOW + LangUtil.get("你没有足够的余额") + String.format("%.2f", shopItem.getPrice()));
                        return;
                    }
                    // 扣费
                    Vault.subtractCurrency(player.getUniqueId(), shopItem.getPrice());
                    // 发送物品
                    BukkitUtil.returnItem(player, shopItem.getItemStack().clone());
                }
                player.sendMessage(ChatColor.YELLOW + LangUtil.get("交易成功，花费：") + totalPrice);
            }
            return;
        }

        if(amount < 1){
            amount = 1;
        }else if(amount > 64){
            amount = 64;
        }

        player.closeInventory();
        ShopConfirmGui.unRegisterShopConfirmGui(player);
        ShopConfirmGui.getShopConfirmGui(player, shopItem,amount);
        return;
    }

    /**
     * 校验是否菜单物品
     * @param displayName
     * @return
     */
    private static boolean isDisplayNameValid(String displayName) {
        return displayName.equals(LangUtil.get(PLUS_1_TEXT)) ||
                displayName.equals(LangUtil.get(MINUS_1_TEXT)) ||
                displayName.equals(LangUtil.get(PLUS_8_TEXT)) ||
                displayName.equals(LangUtil.get(MINUS_8_TEXT)) ||
                displayName.equals(LangUtil.get(CONFIRM_TEXT)) ||
                displayName.equals(LangUtil.get(CANCEL_TEXT));
    }

    /**
     * 绘制第N页的列表
     *
     */
    private static void drawPage(Inventory inventory, int amount, Player player) {

        // 设置购买物品
        ShopConfirmGui shopConfirmGui = ShopConfirmGui.getMyShopConfirmGui(player);
        ShopItem shopItem = shopConfirmGui.getShopItem();
        ItemStack itemStack = shopItem.getItemStack().clone();
        itemStack.setAmount(amount);
        PluginUtil.addLore(itemStack, Collections.singletonList(ChatColor.YELLOW + LangUtil.get("总价：$") + (shopItem.getPrice() * amount)));
        inventory.setItem(ITEM_SIGN_INDEX, itemStack);

        setSign(inventory, PLUS_SIGN, PLUS_1_INDEX, LangUtil.get(PLUS_1_TEXT));
        setSign(inventory, PLUS_SIGN, PLUS_8_INDEX, LangUtil.get(PLUS_8_TEXT));
        setSign(inventory, MINUS_SIGN, MINUS_1_INDEX, LangUtil.get(MINUS_1_TEXT));
        setSign(inventory, MINUS_SIGN, MINUS_8_INDEX, LangUtil.get(MINUS_8_TEXT));
        setSign(inventory, CONFIRM_SIGN, CONFIRM_SIGN_INDEX, LangUtil.get(CONFIRM_TEXT));
        setSign(inventory, CANCEL_SIGN, CANCEL_SIGN_INDEX, LangUtil.get(CANCEL_TEXT));
    }

    /**
     * 设置坐标
     * @param inventory
     * @param material
     * @param lore
     */
    private static void setSign(Inventory inventory, Material material, Integer index, String lore){
        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(lore);
        itemStack.setItemMeta(itemMeta);
        inventory.setItem(index, itemStack);
    }

    /**
     * 取消打开界面的注册
     * @param player
     */
    public static void unRegisterShopConfirmGui(Player player) {
        MENU_OPENING.remove(player.getUniqueId());
    }

    /**
     * 查看是否在册
     * @param player
     * @return
     */
    public static boolean isShopConfirmGui(Player player) {
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
    public static ShopConfirmGui getMyShopConfirmGui(Player player){
        return MENU_OPENING.getOrDefault(player.getUniqueId(), null);
    }

    /**
     * 打开这个收购箱子
     */
    private void openShopConfirmGui() {
        player.openInventory(inventory);
    }

    /**
     * 把当前对象加入全局表
     */
    private void registerShopConfirmGui() {
        MENU_OPENING.put(player.getUniqueId(), this);
    }
}
