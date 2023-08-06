package com.tining.demonmarket.common.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BeanUtils {
    private BeanUtils() {
    }


    /**
     * 类转map
     * @param obj
     * @return
     */
    public static Map<String, String> convertClassToMap(Object obj) {
        Map<String, String> resultMap = new HashMap<>();

        try {
            // 获取类的所有属性
            Field[] fields = obj.getClass().getDeclaredFields();

            for (Field field : fields) {
                // 检查属性是否被标记为 transient
                if (Modifier.isTransient(field.getModifiers())) {
                    continue; // 跳过 transient 属性
                }

                field.setAccessible(true);

                // 获取属性名和属性值
                String fieldName = field.getName();
                String fieldValue = String.valueOf(field.get(obj));

                // 将属性名和属性值添加到 Map 中
                resultMap.put(fieldName, fieldValue);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return resultMap;
    }
}