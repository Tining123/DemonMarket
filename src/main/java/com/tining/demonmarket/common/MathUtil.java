package com.tining.demonmarket.common;

import java.math.BigDecimal;

public class MathUtil {
    /**
     * 基线资产50万
     */
    public static double basicProperty = 500000;

    /**
     * 根据现有资产下调物价，使用默认基线
     * @param price 当前物价
     * @param property 当前资产
     * @return
     */
    public static double priceDownByProperty(double price, double property){
        return priceDownByProperty(price,property,basicProperty);
    }

    /**
     * 根据现有资产下调物价
     * @param price 当前物价
     * @param property 当前资产
     * @param basicProperty 基线资产
     * @return
     */
    public static double priceDownByProperty(double price, double property, double basicProperty){
        return ((price) /Math.pow(Math.exp((property/basicProperty)),0.5)
                + (price/(1 + property/basicProperty)))/2;
    }
}
