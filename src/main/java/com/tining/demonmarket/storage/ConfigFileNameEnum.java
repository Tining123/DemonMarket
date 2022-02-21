package com.tining.demonmarket.storage;

import java.util.Objects;

/**
 * 标识价值文件数据库的枚举类
 * @author tinga
 */

public enum ConfigFileNameEnum {

    /**
     * 价值配置文件
     */
    WORTH_FILE_NAME("worth.yml"),
    /**
     * NBT价值配置文件
     */
    NBT_WORTH_FILE_NAME("nbtworth.yml");

    /**
     * 路径名称
     */
    private String name;

    ConfigFileNameEnum(String s) {
        this.name = s;
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
}
