package com.tining.demonmarket.command.dispatcher;

import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.common.util.ShopUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

/**
 * 以NBT设置商店价格
 * @author tinga
 */
public class AdminShopNbtSetCommand extends AbstractCommander{
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
        //校验物品是否合法
        if (Objects.isNull(itemToSell) || itemToSell.name().equals("AIR")) {
            sender.sendMessage(LangUtil.preColor(ChatColor.YELLOW , LangUtil.get("[DemonMarket]你手里的物品无法交易")));
            return true;
        }
        double price = 0.0;
        //校验价值是否合法
        try {
            price = Double.parseDouble(args[1]);
            if (price <= 0) {
                sender.sendMessage(LangUtil.preColor(ChatColor.YELLOW , LangUtil.get("[DemonMarket]你输入的价格不合法")));
                return true;
            }
        } catch (Exception e) {
            sender.sendMessage(LangUtil.preColor(ChatColor.YELLOW , LangUtil.get("[DemonMarket]你输入的价格不合法")));
            return true;
        }

        ShopUtil.addToNBTPrice(itemStack,price);
        sender.sendMessage(LangUtil.preColor(ChatColor.YELLOW , LangUtil.get("[DemonMarket]设置成功")));

        return true;
    }
}
