package com.tining.demonmarket.data;

import org.bukkit.Material;


public class MarketEvent{


    public MarketEvent(String type, boolean function, double percent, String item_name, String ban, int weight) {
        this.type = MarketEvent.event_type.valueOf(type);
        this.function = function;
        this.percent = percent;
        this.material = Material.matchMaterial(item_name);
        this.ban = ban;
        this.weight = weight;
    }

    //事件类型
    public enum event_type {
        MARKET_X, //市场存量变动
        TAX_RATE, //税率变动
        BANK_INTEREST_RATE, //银行利率变动
    }
    public event_type type;
    //true是加，false是减
    public boolean function;
//    //受影响的数量
//    public int amount;
    //受影响的比率
    public double percent;
    //受影响的物品
    public Material material;
    //对该事件的解释
    public String ban;
    //该事件发生的权重
    public int weight;
}
