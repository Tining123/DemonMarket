package com.tining.demonmarket.command;

import com.tining.demonmarket.storage.ConfigReader;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;

public class AdminCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;

        if (args.length < 1){ return false;}

        switch (args[0]) {
            case "set": {
                //校验参数合法
                if(args.length < 2){
                    //应该有一个set和一个价格 两个参数
                    return false;
                }
                //获取物品名称
                Material itemToSell = player.getInventory().getItemInMainHand().getType();
                //校验物品是否合法
                if (Objects.isNull(itemToSell) || itemToSell.name().equals("AIR")) {
                    sender.sendMessage(ChatColor.YELLOW + "[DemonMarket]你手里的物品无法交易");
                    return true;
                }
                double price = 0.0;
                //校验价值是否合法
                try{
                    price = Double.parseDouble(args[1]);
                    if(price < 0){
                        sender.sendMessage(ChatColor.YELLOW + "[DemonMarket]你输入的价格不合法");
                        return true;
                    }
                }catch (Exception e){
                    sender.sendMessage(ChatColor.YELLOW + "[DemonMarket]你输入的价格不合法");
                    return true;
                }
                //修改数值
                Map<String,Double> map =  ConfigReader.getWorth();
                map.put(itemToSell.name(),price);
                //修改配置文件
                //保存
                ConfigReader.saveWorth(map);
                ConfigReader.reloadConfig();
                sender.sendMessage(ChatColor.YELLOW + "[DemonMarket]设置成功");
                return true;
            }
            case "name":{
                Material itemToSell = player.getInventory().getItemInMainHand().getType();
                sender.sendMessage(ChatColor.YELLOW + "[DemonMarket]你手中拿的是：" + itemToSell.name());
                return true;
            }
            case "reload":{
                ConfigReader.reloadConfig();
                sender.sendMessage(ChatColor.YELLOW + "[DemonMarket]重载成功");
                return true;
            }
            default: {
                return false;
            }
        }
    }
}
