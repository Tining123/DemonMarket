package com.tining.demonmarket.gui.bean;

import org.bukkit.Material;

/**
 * 面板按钮枚举
 * @author tinga
 */
public enum PanelButtonEnum {

    /**
     * 收购箱
     */
    GUI(11, "收购箱", "mt gui", Material.CHEST),

    /**
     * 商店
     */
    SHOP(13,"商店列表", "mt shop", Material.APPLE),

    /**
     * 市场
     */
    MARKET(15, "市场列表", "mt market", Material.BREAD),

    /**
     * 收购列表
     */
    LIST(29, "收购列表", "mt list", Material.PAPER),

    /**
     * 帮助
     */
    HELP(33, "帮助", "mt help", Material.BOOK),
    ;


    /**
     * 按钮坐标
     */
    final int index;

    /**
     * 按钮名称
     */
    final String text;

    /**
     * 按钮指令
     */
    final String command;

    /**
     * 按钮材料
     */
    final Material material;

    /**
     * 构造函数
     * @param index
     * @param text
     * @param command
     */
    PanelButtonEnum(int index, String text, String command, Material material){
        this.index = index;
        this.text = text;
        this.command = command;
        this.material = material;
    }

    public int getIndex() {
        return index;
    }

    public String getText() {
        return text;
    }

    public String getCommand() {
        return command;
    }

    public Material getMaterial() {
        return material;
    }
}
