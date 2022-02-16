package com.tining.demonmarket.command;

import com.google.common.base.Strings;
import com.tining.demonmarket.economy.MarketEconomy;
import com.tining.demonmarket.economy.MarketTrade;
import com.tining.demonmarket.player.Inventory;
import com.tining.demonmarket.storage.ConfigReader;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Logger;

//import javax.annotation.ParametersAreNonnullByDefault;

public class UserCommand implements CommandExecutor {

    Logger logger = Logger.getLogger("command");


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /**
         * 获取价格
         */
        Map<String, Double> worth = ConfigReader.getWorth();

        if (!(sender instanceof Player)) {
            return false;
        }
        if (args.length == 0) {
            // 参数数量太少，拒绝处理
            return false;
        }

        Player player = (Player) sender;
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "sell": {
                Material itemToSell = player.getInventory().getItemInMainHand().getType();
                //合法性校验
                if(!isIllegalItem(itemToSell,player,sender,worth)){
                    return true;
                }

                double value = worth.get(itemToSell.name());
                if (value == 0) {
                    sender.sendMessage(ChatColor.YELLOW + "[DemonMarket]你输入的物品当前一文不值");
                    return true;
                }
                int amountInInventory = Inventory.calcInventory(player, itemToSell);
                int sellAmount = 0;
                if (args.length == 1) {
                    sellAmount = player.getInventory().getItemInMainHand().getAmount();
                    MarketTrade.trade(player, itemToSell, value, sellAmount, MarketTrade.type.SELL);
                    return true;
                }
                if (!"all".equals(args[1].toLowerCase(Locale.ROOT))) {
                    return false;
                }

                sellAmount = amountInInventory;
                MarketTrade.trade(player, itemToSell, value, sellAmount, MarketTrade.type.SELL);
                return true;

            }
            case "price": {
                Material itemToSell = player.getInventory().getItemInMainHand().getType();
                //合法性校验
                if(!isIllegalItem(itemToSell,player,sender,worth)){
                    return true;
                }
                double value = worth.get(itemToSell.name());
                if (value == 0) {
                    sender.sendMessage(ChatColor.YELLOW + "[DemonMarket]你输入的物品当前一文不值");
                    return true;
                }
                int amountInInventory = Inventory.calcInventory(player, itemToSell);
                int sellAmount = 0;
                sellAmount = player.getInventory().getItemInMainHand().getAmount();
                double hand = MarketTrade.preTrade(player, itemToSell, value, sellAmount, MarketTrade.type.SELL);
                double all = MarketTrade.preTrade(player, itemToSell, value, amountInInventory, MarketTrade.type.SELL);
                sender.sendMessage(ChatColor.YELLOW + "[DemonMarket]物品单价：" + MarketEconomy.formatMoney(value) + "，你手里的物品总价："
                        + MarketEconomy.formatMoney(hand) + "，如果出售背包中所有物品可得：" + MarketEconomy.formatMoney(all));
                return true;

            }
            case "help": {
                return false;
            }
            default:
                break;
        }
        return false;
    }

    public boolean isIllegalItem(Material itemToSell, Player player, CommandSender sender, Map<String, Double> worth) {
        //检测是否屏蔽非原版物品
        if (ConfigReader.getFilterSetting().toLowerCase(Locale.ROOT).equals("true")) {
            ItemStack is = player.getInventory().getItemInMainHand();
            //测试
            String name = is.getItemMeta().getDisplayName();
            if (!Strings.isNullOrEmpty(name)) {
                sender.sendMessage(ChatColor.YELLOW + "[DemonMarket]你手里的物品无法交易");
                return false;
            }
        }

        if (Objects.isNull(itemToSell) || itemToSell.name().equals("AIR")) {
            sender.sendMessage(ChatColor.YELLOW + "[DemonMarket]你手里的物品无法交易");
            return false;
        }
        //检测此种物品是否可交易
        if (!worth.containsKey(itemToSell.name())) {
            sender.sendMessage(ChatColor.YELLOW + "[DemonMarket]你手里的物品无法交易");
            return false;
        }

        return true;
    }

}