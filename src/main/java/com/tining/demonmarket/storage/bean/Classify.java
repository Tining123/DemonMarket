package com.tining.demonmarket.storage.bean;

import lombok.Data;

@Data
public class Classify {
    /**
     * 出售物品类型的名称
     */
    private String type;

    /**
     * 物品存储标识串，如果是物品则和name相同，如果是nbt物品则是压缩串，需要反解析
     */
    private String info;

    /**
     * 名称
     */
    private String name;

    /**
     * 分组名称
     */
    private String group;
}
