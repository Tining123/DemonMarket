package com.tining.demonmarket.economy;

import com.google.common.base.Strings;
import com.tining.demonmarket.common.util.MathUtil;
import com.tining.demonmarket.common.util.WorthUtil;
import com.tining.demonmarket.storage.ConfigReader;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;


public class MarketEconomy {

    /**
     * 规范化数字，目前不做处理
     *
     * @param money
     * @return
     */
    public static String formatMoney(double money) {
        if (ConfigReader.getRoundSetting().toLowerCase(Locale.ROOT).equals("true")) {
            return String.format("%.2f", money);
        }
        return money + "";
    }

    /**
     * 计算出售价格
     *
     * @param count 数量
     * @param money 玩家资产
     * @return 结算价格
     */
    public static double getSellingPrice(double value, int count, double money) {
        double price = 0.0;
        double onePrice = 0.0;

        while (count > 0) {
            count--;
            onePrice = MathUtil.priceDownByProperty(value, money, ConfigReader.getBasicProperty());
            price += onePrice;
            money += onePrice;
        }
        return price;
    }

    /**
     * 判断物品是否可以交易
     *
     * @param itemStack
     * @return
     */
    public static boolean isIllegalItem(ItemStack itemStack) {
        if (Objects.isNull(itemStack)) {
            return false;
        }
        //检测是否屏蔽非原版物品
        if (ConfigReader.getFilterSetting().toLowerCase(Locale.ROOT).equals("true")) {
            //测试
            String name = itemStack.getItemMeta().getDisplayName();
            if (!Strings.isNullOrEmpty(name)) {
                return false;
            }
        }
        if (Objects.isNull(itemStack) || itemStack.getType().name().equals("AIR")) {
            return false;
        }
        if (!WorthUtil.isWorthContain(itemStack)) {
            return false;
        }
        if (WorthUtil.getItemWorth(itemStack) <= 0) {
            return false;
        }
        return true;
    }

    public static double getTax(double price) {
        return price * ConfigReader.getTaxRate();
    }


}
