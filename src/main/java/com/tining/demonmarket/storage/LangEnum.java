package com.tining.demonmarket.storage;

import java.util.Locale;
import java.util.Objects;

/**
 * @author tinga
 */

public enum LangEnum {
    /**
     * 简体中文
     */
    SIMPLIFIED_CHINESE("zh_cn"),

    /**
     * 繁体中文
     */
    TRADITIONAL_CHINESE("zh_tw"),

    /**
     * 英文
     */
    ENGLISH("en");

    /**
     * 名称
     */
    private String name;

    LangEnum(String name) {
        this.name = name;
    }

    /**
     * 获取语言文件前名称
     * @param locale 当前地域
     * @return
     */
    public String getLanguage(Locale locale){
        for(LangEnum l : LangEnum.values()){
            if(Objects.equals(locale.toString().toLowerCase(Locale.ROOT),l.getName())){
                return l.getName();
            }
        }
        return LangEnum.ENGLISH.getName();
    }

    /**
     * 获取枚举名称
     * @return
     */
    public String getName(){
        return name;
    }
}
