package com.tining.demonmarket.economy;

import com.tining.demonmarket.common.MathUtil;
import com.tining.demonmarket.storage.ConfigReader;

import java.util.Locale;
import java.util.logging.Logger;


public class MarketEconomy {

    public static double basicProperty = ConfigReader.config.getInt("BasicProperty");
    public static Logger logger = Logger.getLogger("money");

    /**
     * 规范化数字，目前不做处理
     * @param money
     * @return
     */
    public static double formatMoney(double money) {
        if(ConfigReader.getRoundSetting().toLowerCase(Locale.ROOT).equals("true")){
            return Double.parseDouble(String.format("%.2f", money));
        }
        return money;
    }

    /**
     * 计算出售价格
     * @param count 数量
     * @param money 玩家资产
     * @return 结算价格
     */
    public static double getSellingPrice(double value, int count,double money) {
        double price = 0.0;
        double onePrice = 0.0;

        while (count > 0) {
            count--;
            onePrice = MathUtil.priceDownByProperty(value,money,basicProperty);
            price += onePrice;
            money += onePrice;
        }
        return formatMoney(price);
    }

    public static double getTax(double price) {
        return formatMoney(price * ConfigReader.getTaxRate());
    }



}
