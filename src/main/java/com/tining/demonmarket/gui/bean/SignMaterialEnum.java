package com.tining.demonmarket.gui.bean;

import org.bukkit.Material;

import java.util.Objects;

/**
 * 按钮合集
 * @author tinga
 */
public enum SignMaterialEnum {
    /**
     * 按钮集合
     */
    LABEL("PAPER","提示"),
    LEFT("ARROW", "上一页"),
    RIGHT("ARROW", "下一页"),
    ON("GREEN_WOOL", "开启"),
    OFF("RED_WOOL", "关闭"),
    CONFIG("BOOK","配置"),
    PAGE("BOOK","页码"),
    ANVIL("ANVIL","铁砧"),
    PAINTING("PAINTING","图像"),
    GROUP("WRITABLE_BOOK", "名单"),
    PLAYER("PLAYER_HEAD","玩家"),
    WALL("WHITE_WOOL","墙"),

    ;

    private final String material;
    private final String name;

    SignMaterialEnum(String material, String name) {
        this.material = material;
        this.name = name;
    }

    /**
     * 获取材料
     * @return
     */
    public Material getMaterial() {
        Material get = null;
        /**
         * 最终默认按钮，适用于低版本
         */
        String defaultSign = "WOOL";
        try {
            get = Material.getMaterial(material);
            if(Objects.isNull(get)){
                get = Material.getMaterial(defaultSign);
            }
        } catch (Exception e) {
            // 如果出现异常，将赋值为 Material.WOOL
            get = Material.getMaterial(defaultSign);
        }
        return get;
    }

    /**
     * 获取名称
     * @return
     */
    public String getName() {
        return name;
    }
}
