package com.tining.demonmarket.gui;

import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.common.util.PluginUtil;
import com.tining.demonmarket.common.util.ShopUtil;
import com.tining.demonmarket.common.util.bean.Lore;
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
import java.util.logging.Logger;

/**
 * 商店管理列表
 * @author tinga
 */
@Data
public class AdminShopGui {

    private static final Logger log = Logger.getLogger("Minecraft");
    /**
     * 当前开启的菜单
     */
    private static final Map<UUID, AdminShopGui> MENU_OPENING = new HashMap();

    /**
     * 管理员当前编辑对象
     */
    private static final Map<UUID, Integer> EDITING_ITEM = new HashMap<>();

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
     * 向右翻页占位坐标
     */
    private static final Integer LEFT_MOVE_ARROW_INDEX = 48;

    /**
     * 向左翻页占位坐标
     */
    private static final Integer RIGHT_MOVE_ARROW_INDEX = 50;

    /**
     * 页码占位坐标
     */
    private static final Integer PAGE_SIGN_INDEX = 49;

    /**
     * 占位符图标
     */
    private static final Material PAGE_ARROW = Material.PAPER;

    /**
     * 移动位置占位符图标
     */
    private static final Material MOVE_ARROW = Material.ARROW;


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
    public static final String GUI_NAME = "商店管理列表";

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
    public static AdminShopGui getAdminShopGui(Player player) {
        AdminShopGui adminShopGui = new AdminShopGui();
        adminShopGui.inventory = Bukkit.createInventory(player, PAGE_SIZE, LangUtil.get(GUI_NAME));
        adminShopGui.player = player;

        drawPage(adminShopGui.getInventory(), 0, player);


        adminShopGui.registerAdminShopGui();
        adminShopGui.openAdminShopGui();

        return adminShopGui;
    }

    /**
     * 绘制第N页的列表
     *
     * @param pageNum
     */
    private static void drawPage(Inventory inventory, int pageNum, Player player) {
        List<ShopItem> shopItemList = ShopUtil.PRICE_LIST;
        int move = 0;
        boolean set = false;

        List<ItemStack> list = new ArrayList<>();
        for(ShopItem shopItem : shopItemList){
            if(Objects.isNull(shopItem.getItemStack())){
                log.info("【DemonMarket】Null item stack warn: " + shopItem.toString());
                continue;
            }
            ItemStack itemStack = shopItem.getItemStack().clone();
            Lore lore = Lore.builder()
                    .lore(LangUtil.get("售价：") + shopItem.getPrice())
                    .chatColor(ChatColor.YELLOW)
                    .build();
            Lore constLore = Lore.builder()
                    .lore(LangUtil.get("点击以编辑顺序") + shopItem.getPrice())
                    .chatColor(ChatColor.WHITE)
                    .build();
            PluginUtil.addColorLore(itemStack, Collections.singletonList(lore));
            PluginUtil.addColorLore(itemStack, Collections.singletonList(constLore));
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

        if (!Objects.isNull(leftItemMeta) && pageNum != 0) {
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
        // 如果正在编辑，则渲染选项
        if(EDITING_ITEM.containsKey(player.getUniqueId())){
            // 渲染中标
            int index = EDITING_ITEM.get(player.getUniqueId());
            ShopItem shopItem = ShopUtil.getShopItem(index);
            if(Objects.nonNull(shopItem)){
                ItemStack editingItem = shopItem.getItemStack().clone();
                midItemMeta.setDisplayName("< " + (pageNum + 1) + " >");
                editingItem.setItemMeta(midItemMeta);

                Lore lore = Lore.builder()
                        .lore(LangUtil.get("编辑中"))
                        .chatColor(ChatColor.YELLOW)
                        .build();
                PluginUtil.addColorLore(editingItem, Collections.singletonList(lore));

                inventory.setItem(PAGE_SIGN_INDEX, editingItem);
            }
            // 渲染两项操作标
            ItemStack leftMove = new ItemStack(MOVE_ARROW, 1);
            ItemStack rightMove = new ItemStack(MOVE_ARROW, 1);

            leftItemMeta.setDisplayName(LangUtil.get("向左移"));
            leftMove.setItemMeta(leftItemMeta);
            inventory.setItem(LEFT_MOVE_ARROW_INDEX, leftMove);

            rightItemMeta.setDisplayName(LangUtil.get("向右移"));
            rightMove.setItemMeta(rightItemMeta);
            inventory.setItem(RIGHT_MOVE_ARROW_INDEX, rightMove);
        }
    }

    /**
     * 取消打开界面的注册
     * @param player
     */
    public static void unRegisterAdminShopGui(Player player) {
        MENU_OPENING.remove(player.getUniqueId());
        EDITING_ITEM.remove(player.getUniqueId());
    }

    /**
     * 查看是否在册
     * @param player
     * @return
     */
    public static boolean isAdminShopGui(Player player) {
        if (MENU_OPENING.containsKey(player.getUniqueId())) {
            return true;
        }
        return false;
    }

    /**
     * 翻页
     * @param inventory
     * @param slot
     * @param player
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
     * 设置正在编辑的物品
     * @param inventory
     * @param slot
     * @param player
     */
    public static void setEditingItem(Inventory inventory, int slot, Player player){
        if(slot >= VIEW_SIZE ){
            return;
        }

        try {
            ItemStack itemStack = inventory.getItem(PAGE_SIGN_INDEX);
            String name = itemStack.getItemMeta().getDisplayName();
            int page = Integer.parseInt(name.replace("<", "").replace(">", "").trim());

            int index = (page - 1) * VIEW_SIZE + slot;
            EDITING_ITEM.put(player.getUniqueId(), index);

            drawPage(inventory, page - 1, player);
        } catch (Exception e) {
        }
    }

    /**
     * 移动正在编辑的物品
     * @param inventory
     * @param slot
     * @param player
     */
    public static void moveEditingItem(Inventory inventory, int slot, Player player){
        try {
            ItemStack itemStack = inventory.getItem(PAGE_SIGN_INDEX);
            String name = itemStack.getItemMeta().getDisplayName();
            int page = Integer.parseInt(name.replace("<", "").replace(">", "").trim());

            int index = EDITING_ITEM.get(player.getUniqueId());
            ShopItem shopItem = ShopUtil.getShopItem(index);
            if(slot == LEFT_MOVE_ARROW_INDEX && Objects.nonNull(shopItem)){
                ShopUtil.moveUpItem(shopItem.getItemStack(), shopItem.getItemType());
                EDITING_ITEM.put(player.getUniqueId(), index - 1);
            }else if(slot == RIGHT_MOVE_ARROW_INDEX && Objects.nonNull(shopItem)){
                ShopUtil.moveDownItem(shopItem.getItemStack(), shopItem.getItemType());
                EDITING_ITEM.put(player.getUniqueId(), index + 1);
            }else{
                return;
            }

            drawPage(inventory, page - 1, player);
        } catch (Exception e) {
        }
    }

    /**
     * 打开这个收购箱子
     */
    private void openAdminShopGui() {
        player.openInventory(inventory);
    }

    /**
     * 把当前对象加入全局表
     */
    private void registerAdminShopGui() {
        MENU_OPENING.put(player.getUniqueId(), this);
    }
}
