package com.tining.demonmarket.common.util;

import com.tining.demonmarket.storage.LangReader;
import org.apache.commons.collections.CollectionUtils;
import org.bukkit.ChatColor;

import java.util.Map;

/**
 * 语言工具类
 * @author tinga
 */
public class LangUtil {
    /**
     * 获取译文
     * @param str
     * @return
     */
    public static String get(String str){
        if(LangReader.getDictionary().isEmpty()){
            return str;
        }

        Map<String,String> map = LangReader.getDictionary();

        if(map.containsKey(str)){
            return map.get(str);
        }

        return str;
    }


    public static String preColor(ChatColor color, String str){
        if(LangReader.getDictionary().isEmpty()){
            return str;
        }

        Map<String,String> map = LangReader.getDictionary();

        if(map.containsKey(str)){
            String convert = map.get(str);
            // 如果convert里面没有§，就用默认颜色
            if(!convert.contains("§")){
                return color + convert;
            }else{
                return convert;
            }
        }

        return str;
    }

    /**
     * 获取当前语言
     * @return
     */
    public static String getLang(){
        return LangReader.getLanguage();
    }
}
