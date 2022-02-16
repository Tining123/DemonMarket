package com.tining.demonmarket.economy;

import com.tining.demonmarket.data.MarketItem;
import com.tining.demonmarket.money.Vault;
import com.tining.demonmarket.player.Inventory;
import com.tining.demonmarket.player.PlayerRegData;
import com.tining.demonmarket.storage.ConfigReader;
import com.tining.demonmarket.storage.Mysql;
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
     * 随机收购物品
     */
    public static MarketItem randomAcquire;

    /**
     * 剩余收购数量
     */
    public static AtomicInteger randomRestCount;

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


    //计算税后购买价格
    public static double getBuyPriceWithTax(Player player, MarketItem marketItem, int amount) {
        double price = 0.0;
        double tax = 0.0;
        price = MarketEconomy.getBuyingPrice(marketItem, amount);
        tax = (PlayerRegData.isVIP(player)) ? 0.0 : MarketEconomy.getTax(price);
        return price + tax;
    }
    //获取用户贸易税
    public static double getTax(Player player, double price) {
        return (PlayerRegData.isVIP(player)) ? 0.0 : MarketEconomy.getTax(price);
    }
    //检查用户是否有足够的钱
    public static boolean isAffordFor(Player player, MarketItem marketItem, int amount) {
        return Vault.checkCurrency(player.getUniqueId()) >= getBuyPriceWithTax(player, marketItem, amount);
    }
    //检查玩家是否有足够的背包空格
    public static boolean isEnoughSlot(Player player, int amount, Material material) {
        return  Inventory.calcEmpty(player) >= amount / material.getMaxStackSize() + ((amount % material.getMaxStackSize() > 0) ? 1 : 0);
    }
    //检测物品数量合法性
    public static boolean isAmountLegal(String amount) {
        try {
            int i = Integer.parseInt(amount);
            return i > 0;
        } catch (Exception e) {
            return false;
        }
    }

    //检测市场库存是否充足

    /**
     * 监测库存，目前设定永远充足
     * @param marketItem
     * @param amount
     * @return
     */
    public static boolean isMarketXEnough(MarketItem marketItem, int amount) {
        return true;
    }
    //检测玩家背包中是否有足够的物品
    public static boolean isPlayerItemEnough(Player player, Material material, int amount) {
        return Inventory.calcInventory(player, material) >= amount;
    }
    //记录交易
    public static void log(Player player, Material material, type type, double price, double tax ,int amount) {
        Mysql m = new Mysql();
        m.prepareSql("INSERT INTO market_log (username, type, time, item, amount, price, tax) VALUES (?, ?, ?, ?, ?, ?, ?)");
        m.setData(1, player.getName());
        m.setData(2, String.valueOf(type));
        m.setData(3, String.valueOf(new Timestamp(System.currentTimeMillis()).getTime() /1000));
        m.setData(4, material.name());
        m.setData(5, String.valueOf(amount));
        m.setData(6, String.valueOf(price));
        m.setData(7, String.valueOf(tax));
        m.execute();
        m.close();
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
