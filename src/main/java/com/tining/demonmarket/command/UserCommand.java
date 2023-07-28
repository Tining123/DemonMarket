package com.tining.demonmarket.command;

import com.google.common.base.Strings;
import com.tining.demonmarket.command.dispatcher.MarketCommand;
import com.tining.demonmarket.command.dispatcher.PanelCommand;
import com.tining.demonmarket.command.dispatcher.ShopCommand;
import com.tining.demonmarket.common.ref.Vault;
import com.tining.demonmarket.common.util.BukkitUtil;
import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.common.util.WorthUtil;
import com.tining.demonmarket.economy.MarketEconomy;
import com.tining.demonmarket.economy.MarketTrade;
import com.tining.demonmarket.common.util.InventoryUtil;
import com.tining.demonmarket.gui.AcquireListGui;
import com.tining.demonmarket.gui.ChestGui;
import com.tining.demonmarket.storage.ConfigReader;
import com.tining.demonmarket.storage.LogWriter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
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
        //Map<String, Double> worth = ConfigReader.getWorth();

        if (!(sender instanceof Player)) {
            return false;
        }

        // 构造参数包
        CommandPack commandPack = new CommandPack();
        commandPack.sender = sender;
        commandPack.command = command;
        commandPack.label = label;
        commandPack.args = args;

        // 打开默认面板
        if (args.length == 0) {
            new PanelCommand().deal(commandPack);
            return true;
        }

        Player player = (Player) sender;
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "sell": {
                ItemStack copy = itemStack.clone();
                //合法性校验
                if (!isIllegalItem(itemStack, player, sender)) {
                    return true;
                }

                double value = WorthUtil.getItemWorth(itemStack);
                if (value == 0) {
                    sender.sendMessage(LangUtil.preColor(ChatColor.YELLOW , LangUtil.get("[DemonMarket]你手里的物品无法交易")));
                    return true;
                }
                int sellAmount = 0;
                if (args.length == 1) {
                    sellAmount = player.getInventory().getItemInMainHand().getAmount();
                    MarketTrade.trade(player, itemStack, value, sellAmount);
                    //去除物品
                    InventoryUtil.subtractHand(player, itemStack);
                    return true;
                }
                if (!"all".equals(args[1].toLowerCase(Locale.ROOT))) {
                    return false;
                }
                itemStack = copy;
                if (WorthUtil.isWorthNBTContain(itemStack)) {
                    sellAmount = InventoryUtil.calcInventoryNBT(player, itemStack);
                    MarketTrade.trade(player, itemStack, value, sellAmount);
                    InventoryUtil.subtractAllNBT(player, itemStack);
                } else {
                    sellAmount = InventoryUtil.calcInventoryNBT(player, itemStack);
                    MarketTrade.trade(player, itemStack, value, sellAmount);
                    InventoryUtil.subtractAll(player, itemStack);
                }
                return true;

            }
            case "price": {
                //合法性校验
                if (!isIllegalItem(itemStack, player, sender)) {
                    return true;
                }
                double value = WorthUtil.getItemWorth(itemStack);
                if (value == 0) {
                    sender.sendMessage(LangUtil.preColor(ChatColor.YELLOW , LangUtil.get("[DemonMarket]你手里的物品无法交易")));
                    return true;
                }
                int amountInInventory = InventoryUtil.calcInventory(player, itemStack);
                int sellAmount = 0;
                sellAmount = player.getInventory().getItemInMainHand().getAmount();
                double hand = MarketTrade.preTrade(player, itemStack, value, sellAmount);
                double all = MarketTrade.preTrade(player, itemStack, value, amountInInventory);
                sender.sendMessage(LangUtil.preColor(ChatColor.YELLOW , LangUtil.get("[DemonMarket]物品单价：") + MarketEconomy.formatMoney(value) + LangUtil.get("，你手里的物品总价：")
                        + MarketEconomy.formatMoney(hand) + LangUtil.get("，如果出售背包中所有物品可得：") + MarketEconomy.formatMoney(all)));
                return true;

            }
            case "pay": {
                //合法性校验
                if (args.length < 3) {
                    player.sendMessage(LangUtil.preColor(ChatColor.YELLOW , LangUtil.get("/mt pay [玩家] [金额]")));
                    return true;
                }
                String valueString = args[2];
                double value = 0;
                try {
                    value = Double.parseDouble(valueString);
                } catch (Exception e) {
                    player.sendMessage(LangUtil.preColor(ChatColor.YELLOW , LangUtil.get("/mt pay [玩家] [金额]")));
                    return true;
                }
                if(value < 0){
                    player.sendMessage(LangUtil.preColor(ChatColor.YELLOW , LangUtil.get("/mt pay [玩家] [金额]")));
                    return true;
                }
                if (Vault.checkCurrency(player.getUniqueId()) < value) {
                    player.sendMessage(LangUtil.preColor(ChatColor.YELLOW , LangUtil.get("你没有足够的余额")));
                    return true;
                }
                //转账上线校验
                double maxPay = ConfigReader.getMaxPay();
                if (maxPay != -1) {
                    if (maxPay == 0) {
                        player.sendMessage(LangUtil.preColor(ChatColor.YELLOW , LangUtil.get("转账功能已关闭")));
                        return true;
                    } else if (maxPay < value) {
                        player.sendMessage(LangUtil.preColor(ChatColor.YELLOW , String.format(LangUtil.get("转账金额超过上限，当前上限%s"), maxPay)));
                        return true;
                    }
                }


                OfflinePlayer reciever = Bukkit.getOfflinePlayer(args[1]);


                // totalValue为转账金额
                // totalPrice 为最终成功转账数量
                double totalPrice = 0;
                double totalValue = value;
                int time = (int)(totalValue / ConfigReader.getPayUnit());
                double res = totalValue % ConfigReader.getPayUnit();

                double price = MarketEconomy.getSellingPrice(ConfigReader.getPayUnit(), time, Vault.checkCurrency(reciever.getUniqueId()));
                totalPrice += price;

                price = MarketEconomy.getSellingPrice(res, 1, Vault.checkCurrency(reciever.getUniqueId()));
                totalPrice += price;


                // 如果不是接受者接受税金，那么发送者将支付税金
                if(ConfigReader.getPayerTax()){
                    double tempValue = totalValue;
                    totalValue = totalValue + (totalValue - totalPrice);
                    totalPrice = tempValue;

                    // 重新判断是否余额充足
                    if (Vault.checkCurrency(player.getUniqueId()) < totalValue) {
                        player.sendMessage(LangUtil.preColor(ChatColor.YELLOW , LangUtil.get("你没有足够的余额") + totalValue));
                        return true;
                    }
                }

                Vault.subtractCurrency(player.getUniqueId(), totalValue);
                Vault.addVaultCurrency(reciever, totalPrice);
                // 给收款人发消息
                try{
                    Player onlineReceiver = Bukkit.getPlayer(args[1]);
                    onlineReceiver.sendMessage(LangUtil.preColor(ChatColor.YELLOW , String.format(LangUtil.get("收款成功，从%s收到%s"), player.getName(), MarketEconomy.formatMoney(totalPrice))));
                }catch (Exception ignore){}

                player.sendMessage(LangUtil.preColor(ChatColor.YELLOW , String.format(LangUtil.get("转账成功，花费%S，转账%s"),
                        totalValue, MarketEconomy.formatMoney(totalPrice))));
                LogWriter.appendToLog(player.getName() + "->" + reciever.getName() + ":" + String.format(LangUtil.get("转账成功，花费%S，转账%s"),
                        totalValue, MarketEconomy.formatMoney(totalPrice)));
                return true;

            }
            case "gui": {
                ChestGui.getChestGui(player);
                return true;
            }
            case "list": {
                AcquireListGui.getAcquireListGui(player);
                return true;
            }
            case "shop": {
                new ShopCommand().deal(commandPack);
                return true;
            }
            case "mt":
            case "market": {
                new MarketCommand().deal(commandPack);
                return true;
            }
            case "panel":{
                new PanelCommand().deal(commandPack);
                return true;
            }
            default: {
                sender.sendMessage(getHelp());
                return true;
            }
        }
    }

    /**
     * 判断物品是否可以交易
     *
     * @param itemStack
     * @param player
     * @param sender
     * @return
     */
    @Deprecated
    public boolean isIllegalItem(ItemStack itemStack, Player player, CommandSender sender) {
        //检测是否屏蔽非原版物品
        if (ConfigReader.getFilterSetting().toLowerCase(Locale.ROOT).equals("true")) {
            ItemStack is = player.getInventory().getItemInMainHand();
            //测试
            String name = is.getItemMeta().getDisplayName();
            if (!Strings.isNullOrEmpty(name)) {
                sender.sendMessage(LangUtil.preColor(ChatColor.YELLOW , LangUtil.get("[DemonMarket]你手里的物品无法交易")));
                return false;
            }
        }

        if (Objects.isNull(itemStack) || itemStack.getType().name().equals("AIR")) {
            sender.sendMessage(LangUtil.preColor(ChatColor.YELLOW , LangUtil.get("[DemonMarket]你手里的物品无法交易")));
            return false;
        }
        //检测此种物品是否可交易
        if (!WorthUtil.isWorthContain(itemStack)) {
            sender.sendMessage(LangUtil.preColor(ChatColor.YELLOW , LangUtil.get("[DemonMarket]你手里的物品无法交易")));
            return false;
        }

        return true;
    }

    /**
     * 获取帮助提示
     *
     * @return
     */
    public static String getHelp() {
        String help = LangUtil.get("/mt gui 打开收购箱界面\n") +
                LangUtil.get("/mt list 查看所有可出售物品") + "\n" +
                LangUtil.get("/mt pay [玩家] [金额]") + "\n" +
                LangUtil.get("/mt sell 出售手里的物品\n") +
                LangUtil.get("/mt sell all 出售背包里当前所有与手中相同的物品\n") +
                LangUtil.get("/mt price 查询物品当前价格") + "\n" +
                LangUtil.get("/mt shop 打开物品商店") + "\n" +
                LangUtil.get("/mt market 打开交易市场") + "\n" +
                LangUtil.get("/mt market sell [金额] 售卖手中物品至市场");
                ;
        return help;
    }
}