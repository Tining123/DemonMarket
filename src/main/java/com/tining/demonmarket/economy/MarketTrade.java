package com.tining.demonmarket.economy;

import com.tining.demonmarket.money.Vault;
import com.tining.demonmarket.storage.ConfigReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class MarketTrade {

    /**
     * 服主
     */
    public static String op = ConfigReader.config.getString("OP");

    /**
     * 模拟贸易
     * @param player
     * @param value
     * @param amount
     */
    public static double preTrade(Player player, ItemStack itemStack, double value, int amount) {
        double price = 0.0;
        double tax = 0.0;
        //玩家存款
        double money = Vault.checkCurrency(player.getUniqueId());
        //计算价格
        price = MarketEconomy.getSellingPrice(value, amount,money);
        return price;
    }

    /**
     * 进行贸易
     * @param player 玩家
     * @param profit 利润
     */
    public static void trade(Player player, double profit) {
        OfflinePlayer op = null;
        //服主
        try {
            op = Bukkit.getOfflinePlayer(MarketTrade.op);
        }catch (Exception e){};
        double tax = 0.0;

        //计算贸易税
        tax = MarketEconomy.getTax(profit);
        //更新玩家货币数据
        Vault.addVaultCurrency(player.getUniqueId(), profit - tax);
        //给服主上税
        try {
            Vault.addVaultCurrency(op, tax);
        }catch (Exception e){}
        //记录贸易
        message(player, profit, tax);
    }

    /**
     * 进行贸易
     * @param player
     * @param value
     * @param amount
     */
    public static void trade(Player player, ItemStack itemStack, double value, int amount) {
        OfflinePlayer op = null;
        //服主
        try {
            op = Bukkit.getOfflinePlayer(MarketTrade.op);
        }catch (Exception e){};
        double price = 0.0;
        double tax = 0.0;
        //玩家存款
        double money = Vault.checkCurrency(player.getUniqueId());

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
                //Inventory.subtractInventory(player, itemStack, amount);
        //记录贸易
        message(player, itemStack, amount, price, tax);
    }


    //交易提示信息
    public static void message(Player player, ItemStack itemStack, int amount, double price, double tax) {

        player.sendMessage(ChatColor.GREEN + String.format("[DemonMarket]你成功%s了%s个%s，%s$%s，其中贸易税为$%s",
                "出售",
                amount,
                itemStack.getType().name(),
                "所得",
                MarketEconomy.formatMoney(price - tax),
                MarketEconomy.formatMoney(tax)
        ));
    }

    //交易提示信息
    public static void message(Player player, double price, double tax) {

        player.sendMessage(ChatColor.GREEN + String.format("[DemonMarket]你成功%s了收购箱中的物品，%s$%s，其中贸易税为$%s",
                "出售",
                "所得",
                MarketEconomy.formatMoney(price - tax),
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
