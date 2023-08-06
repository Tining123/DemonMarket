package com.tining.demonmarket.gui.v1;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.common.util.PluginUtil;
import com.tining.demonmarket.gui.bean.SignMaterialEnum;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

public abstract class AbstractGUIV1{

    protected static final Logger log = Logger.getLogger("Minecraft");

    /**
     * 总map
     */
    protected static Table<UUID,String,AbstractGUIV1> map = HashBasedTable.create();

    /**
     * 获取当前实例
     * @param player
     * @return
     */
    public static AbstractGUIV1 getMe(Player player){
        return map.get(player.getUniqueId(),getClazzType());
    }

    /**
     * 默认单页大小
     */
    protected Integer pageSize = 54;

    /**
     * 默认视区域大小
     */
    @Getter
    protected Integer viewSize = 45;

    /**
     * 窗口名称
     */
    protected String guiName;

    protected Inventory inventory;

    Player player;

    public AbstractGUIV1(Player player,String guiName) {
        this.guiName = guiName;
        this.player = player;
//        this.inventory = Bukkit.createInventory(player, pageSize, LangUtil.get(guiName));
//        map.put(player.getUniqueId(), getClazzType(),this);
//        player.openInventory(inventory);
//        drawPage(inventory, 0, player);
    }

    /**
     * 获取当前实际类的名称
     * @return
     */
    protected static String getClazzType() {
        Class<?> clazz = new Object() {}.getClass().getEnclosingClass();
        return clazz.getSimpleName();
    }

    /**
     * 判断是否应该响应
     *
     * @param player
     * @param title
     * @return
     */
    public static boolean shouldEffective(Player player, String title) {
        AbstractGUIV1 gui = AbstractGUIV1.getMe(player);
        return Objects.nonNull(gui);
    }

    /**
     * 取消注册
     *
     * @param player
     */
    @Deprecated
    public static void unRegister(Player player) {
        map.remove(player.getUniqueId(),getClazzType());
    }

    /**
     * 退出页面
     * @param player
     */
    protected void quit(Player player, Inventory inventory){
        player.closeInventory();
        unRegister(player);
    }

    /**
     * 查找与给定的整数匹配的枚举
     *
     * @param checkSlot 要检查的整数
     * @return 如果找到匹配的枚举，则返回该枚举，否则返回 null
     */
    public SignEnumInterfaceV1 findMatchedSign(int checkSlot, List<SignEnumInterfaceV1> signList) {
        for (SignEnumInterfaceV1 sign : signList) {
            if (sign.getSlot() == checkSlot) {
                return sign;
            }
        }
        return null;
    }

    /**
     * 加载画面
     */
    protected void loadGui(){
        this.inventory = Bukkit.createInventory(player, pageSize, LangUtil.get(guiName));
        map.put(player.getUniqueId(), getClazzType(),this);
        player.openInventory(inventory);
        drawPage(inventory, 0, player);
    }

    /**
     * 设置图案
     * @param inventory
     * @param signEnum
     * @param signMaterialEnum
     * @param label
     * @param lore
     */
    protected void setSign(Inventory inventory, SignEnumInterfaceV1 signEnum, SignMaterialEnum signMaterialEnum
            , String label, List<String> lore) {
        Material material = signMaterialEnum.getMaterial();
        if (Objects.isNull(material)) {
            return;
        }
        ItemStack itemStack = new ItemStack(material);
        if (!CollectionUtils.isEmpty(lore)) {
            PluginUtil.addLore(itemStack, lore);
        }
        PluginUtil.setName(itemStack, label);
        inventory.setItem(signEnum.getSlot(), itemStack);
    }

    /**
     * 渲染画面
     * @param inventory
     * @param pageNum
     * @param player
     */
    protected abstract void drawPage(Inventory inventory, int pageNum, Player player);

    /**
     * 获取内部枚举列表
     * @param player
     * @return
     */
    public abstract List<SignEnumInterfaceV1> getEnumList(Player player);


    /**
     * 检查给定的整数是否匹配任何坐标
     *
     * @param checkSlot 要检查的整数
     * @return 如果整数匹配任何坐标，则返回 true，否则返回 false
     */
    public boolean isMatchedSlot(int checkSlot, List<SignEnumInterfaceV1> signList) {
        return findMatchedSign(checkSlot, signList) != null;
    }
}
