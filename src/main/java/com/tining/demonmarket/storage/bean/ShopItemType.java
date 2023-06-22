package com.tining.demonmarket.storage.bean;

import com.tining.demonmarket.storage.ConfigFileNameEnum;

import java.util.Objects;

/**
 * 商店物品类型
 * @author tinga
 */

public enum ShopItemType {

    /**
     * 普通类型
     */
    COMMON_ITEM("common"),

    /**
     * NBT类型
     */
    NBT_ITEM("nbt");

    /**
     * 类型名称
     */
    private String name;

    ShopItemType(String s) {
        this.name = s;
    }

    /**
     * 通过名称获得枚举
     * @param s
     * @return
     */
    public static ShopItemType getType(String s) {
        for(ShopItemType w: ShopItemType.values()){
            if(Objects.equals(s,w.getName())){
                return w;
            }
        }
        return null;
    }

    /**
     * 通过枚举获得名称
     * @return
     */
    public String getName(){
        return name;
    }
}
