package com.tining.demonmarket.economy;

import com.tining.demonmarket.data.MarketItem;
import com.tining.demonmarket.money.Vault;
import com.tining.demonmarket.player.Inventory;
import com.tining.demonmarket.storage.ConfigReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class MarketTrade {

    /**
     * 服主
     */
    public static String op = ConfigReader.config.getString("OP");

    /**
     * 交易类型
     */
    public enum type{
        /**
         * 出售
         */
        SELL,
        /**
         * 购买
         */
        BUY
    }

    /**
     * 模拟贸易
     * @param player
     * @param material
     * @param value
     * @param amount
     * @param type
     */
    public static double preTrade(Player player, Material material, double value, int amount, type type) {
        OfflinePlayer op = null;
        //服主
        try {
            op = Bukkit.getOfflinePlayer(MarketTrade.op);
        }catch (Exception e){};
        double price = 0.0;
        double tax = 0.0;
        //玩家存款
        double money = Vault.checkCurrency(player.getUniqueId());
        switch (type) {
            case SELL: {
                //计算价格
                price = MarketEconomy.getSellingPrice(value, amount,money);
                return price;
            }
            default:break;
        }
        return value;
    }

    /**
     * 进行贸易
     * @param player
     * @param material
     * @param value
     * @param amount
     * @param type
     */
    public static void trade(Player player, Material material, double value, int amount, type type) {
        OfflinePlayer op = null;
        //服主
        try {
            op = Bukkit.getOfflinePlayer(MarketTrade.op);
        }catch (Exception e){};
        double price = 0.0;
        double tax = 0.0;
        //玩家存款
        double money = Vault.checkCurrency(player.getUniqueId());
        switch (type) {
            case SELL: {
                //计算价格
                price = MarketEconomy.getSellingPrice(value, amount,money);
                //计算贸易税
                tax = MarketEconomy.getTax(price);
                //更新玩家货币数据
                Vault.addVaultCurrency(player.getUniqueId(), price - tax);
                //给服主上税
                try {
                    Vault.addVaultCurrency(op, tax);
                }catch (Exception e){}
                //更新玩家储存
                Inventory.subtractInventory(player, material, amount);
                break;
            }
            default:break;
        }
        //记录贸易
        message(player, type, material, amount, price, tax);
    }


    //交易提示信息
    public static void message(Player player, type type, Material material, int amount, double price, double tax) {


        player.sendMessage(ChatColor.GREEN + String.format("[DemonMarket]你成功%s了%s个%s，%s$%s，其中贸易税为$%s",
                (type == MarketTrade.type.BUY) ? "购买" : "出售",
                amount,
                material.name(),
                (type == MarketTrade.type.BUY) ? "花费" : "所得",
                (type == MarketTrade.type.BUY) ? MarketEconomy.formatMoney(price + tax) : MarketEconomy.formatMoney(price - tax),
                MarketEconomy.formatMoney(tax)
        ));
    }

    /**
     * 近似数字
     * @param money
     * @return
     */
    public static double formatMoney(double money) {
        return Double.parseDouble(String.format("%.2f", money));
    }
}
