package com.tining.demonmarket.common.util;

import java.math.BigDecimal;

public class MathUtil {
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
