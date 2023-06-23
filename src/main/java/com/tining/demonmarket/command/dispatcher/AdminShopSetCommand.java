package com.tining.demonmarket.command.dispatcher;

import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.common.util.ShopUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AdminShopSetCommand extends AbstractCommander{
    @Override
    protected boolean solve(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            //应该有一个set和一个价格 两个参数
            return false;
        }
        Player player = (Player) sender;
        //获取物品名称
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        Material itemToSell = player.getInventory().getItemInMainHand().getType();
        double price = 0.0;
        //校验价值是否合法
        try {
            price = Double.parseDouble(args[1]);
            if (price <= 0) {
                sender.sendMessage(ChatColor.YELLOW + LangUtil.get("[DemonMarket]你输入的价格不合法"));
                return true;
            }
        } catch (Exception e) {
            sender.sendMessage(ChatColor.YELLOW + LangUtil.get("[DemonMarket]你输入的价格不合法"));
            return true;
        }

        ShopUtil.addToCommonPrice(itemStack,price);
        sender.sendMessage(ChatColor.YELLOW + LangUtil.get("[DemonMarket]设置成功"));

        return true;
    }
}
