package com.tining.demonmarket.storage;

import java.util.Objects;

/**
 * 标识价值文件数据库的枚举类
 * 用于释放文件使用
 * @author tinga
 */

public enum ConfigFileNameEnum {

    /**
     * 价值配置文件
     */
    WORTH_FILE_NAME("worth.yml", "worth"),
    /**
     * NBT价值配置文件
     */
    NBT_WORTH_FILE_NAME("nbtworth.yml","nbtworth"),
    /**
     * 商店价值配置文件
     */
    SHOP_PRICE_NAME("shop.yml","shop"),
    /**
     * 市场存储
     */
    MARKET_DB_NAME("market.yml","market"),
    /**
     * 物品分类
     */
    CLASSIFY_DB_NAME("classify.yml","classify"),
    /**
     * 分类后的物品
     */
    GROUP_DB_NAME("group.yml","group"),

        ;

    /**
     * 路径名称
     */
    private String name;

    private String rootSection;


    ConfigFileNameEnum(String s, String rootSection) {
        this.name = s;
        this.rootSection = rootSection;
    }

    /**
     * 通过名称获得枚举
     * @param s
     * @return
     */
    public ConfigFileNameEnum getType(String s) {
        for(ConfigFileNameEnum w: ConfigFileNameEnum.values()){
            if(Objects.equals(s,w)){
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

    /**
     * 通过枚举获得根节点
     * @return
     */
    public String getRootSection(){
        return rootSection;
    }

}
