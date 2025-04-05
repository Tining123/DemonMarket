package com.tining.demonmarket.gui;

import com.tining.demonmarket.common.ref.Vault;
import com.tining.demonmarket.common.util.BukkitUtil;
import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.common.util.MarketUtil;
import com.tining.demonmarket.common.util.PluginUtil;
import com.tining.demonmarket.economy.MarketEconomy;
import com.tining.demonmarket.gui.bean.LockManager;
import com.tining.demonmarket.storage.ConfigReader;
import com.tining.demonmarket.storage.LogWriter;
import com.tining.demonmarket.storage.bean.MarketItem;
import lombok.Data;
import lombok.Getter;
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
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
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
    public static void makeDecision(Player player, MarketItem marketItem, int slot, String displayName) {
        if (!isDisplayNameValid(displayName)) {
            return;
        }
        // 检查是否取消位置
        if(CANCEL_SIGN_INDEX == slot){
            // 关闭交易界面和注销GUI
            player.closeInventory();
            unRegisterMarketConfirmGui(player);
            return;
        }
        // 1. 交易开始前第一次确认物品是否存在
        if (!MarketUtil.checkFromMarket(marketItem.getOwnerName(), marketItem.getItemStack(), marketItem.getPrice())) {
            player.sendMessage(LangUtil.preColor(ChatColor.YELLOW, LangUtil.get("该物品已不存在于市场中")));
            player.closeInventory();
            MarketConfirmGui.unRegisterMarketConfirmGui(player);
            return;
        }

        // 使用物品名称、NBT、数量、价格和发布日期构建唯一锁 key
        String lockKey = marketItem.getName() + "_" +
                marketItem.getInfo() + "_" +
                marketItem.getAmount() + "_" +
                marketItem.getPrice() + "_" +
                marketItem.getDateString();
        ReentrantLock lock = LockManager.getLock(lockKey);

        boolean firstLockAcquired = false;
        try {
            // 尝试获取第一层锁，超时1秒
            firstLockAcquired = lock.tryLock(1, TimeUnit.SECONDS);
            if (!firstLockAcquired) {
                player.sendMessage(LangUtil.preColor(ChatColor.YELLOW, "交易繁忙，请稍后再试。"));
                return;
            }

            // 2. 第一层锁内，再次确认物品存在（第二次确认）
            if (!MarketUtil.checkFromMarket(marketItem.getOwnerName(), marketItem.getItemStack(), marketItem.getPrice())) {
                player.sendMessage(LangUtil.preColor(ChatColor.YELLOW, LangUtil.get("该物品已不存在于市场中")));
                return;
            }

            double totalPrice = marketItem.getPrice();
            if (player.getName().equals(marketItem.getOwnerName())) {
                totalPrice = 0;
            }
            if (Vault.checkCurrency(player.getUniqueId()) < totalPrice) {
                player.sendMessage(LangUtil.preColor(ChatColor.YELLOW,
                        LangUtil.get("你没有足够的余额") + String.format("%.2f", totalPrice)));
                return;
            }

            boolean secondLockAcquired = false;
            try {
                // 尝试获取第二层锁，超时1秒
                secondLockAcquired = lock.tryLock(1, TimeUnit.SECONDS);
                if (!secondLockAcquired) {
                    player.sendMessage(LangUtil.preColor(ChatColor.YELLOW, "交易繁忙，请稍后再试。"));
                    return;
                }

                // 3. 第二层锁内，再次确认物品存在（第三次确认）
                if (!MarketUtil.checkFromMarket(marketItem.getOwnerName(), marketItem.getItemStack(), marketItem.getPrice())) {
                    player.sendMessage(LangUtil.preColor(ChatColor.YELLOW, LangUtil.get("该物品已不存在于市场中")));
                    return;
                }

                // 4. 开始原子操作前，再确认一次（第四次确认）
                if (!MarketUtil.checkFromMarket(marketItem.getOwnerName(), marketItem.getItemStack(), marketItem.getPrice())) {
                    player.sendMessage(LangUtil.preColor(ChatColor.YELLOW, LangUtil.get("该物品已不存在于市场中")));
                    return;
                }

                // 原子操作步骤：
                // 4.1 扣费
                Vault.subtractCurrency(player.getUniqueId(), totalPrice);

                // 4.2 给予物品（克隆后发放，避免原对象修改）
                BukkitUtil.returnItem(player, marketItem.getItemStack().clone());

                // 4.3 物品下架处理
                MarketUtil.removeFromMarket(marketItem.getOwnerName(), marketItem.getItemStack(), marketItem.getPrice());

                // 4.4 给卖家转账（如果不是原主）
                if (!player.getName().equals(marketItem.getOwnerName())) {
                    OfflinePlayer receiver = Bukkit.getOfflinePlayer(marketItem.getOwnerName());
                    double receive = totalPrice;
                    Vault.addVaultCurrency(receiver, receive);
                    try {
                        Player onlineReceiver = Bukkit.getPlayer(marketItem.getOwnerName());
                        if (onlineReceiver != null) {
                            onlineReceiver.sendMessage(LangUtil.preColor(ChatColor.YELLOW,
                                    String.format(LangUtil.get("物品%s出售成功，从%s收到%s"),
                                            marketItem.getName(), player.getName(), MarketEconomy.formatMoney(receive))));
                        }
                    } catch (Exception ignore) { }
                }

                player.sendMessage(LangUtil.preColor(ChatColor.YELLOW,
                        LangUtil.get("交易成功，花费：") + String.format("%.2f", totalPrice)));
                LogWriter.appendToLog(player.getName() + "->" + marketItem.getOwnerName() + "[" +
                        marketItem.getName() + "]:" + LangUtil.get("交易成功，花费：") + totalPrice);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (secondLockAcquired) {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (firstLockAcquired) {
                lock.unlock();
            }
        }

        // 关闭交易界面和注销GUI
        player.closeInventory();
        MarketConfirmGui.unRegisterMarketConfirmGui(player);
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
        MarketConfirmGui marketConfirmGui = MarketConfirmGui.getMyMarketConfirmGui(player);
        MarketItem marketItem = marketConfirmGui.getMarketItem();
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
