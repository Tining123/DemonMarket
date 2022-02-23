package com.tining.demonmarket.common.util;

import com.tining.demonmarket.storage.LangReader;
import org.apache.commons.collections.CollectionUtils;

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

    public static String getLang(){
        return LangReader.getLanguage();
    }
}
