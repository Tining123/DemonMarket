package com.tining.demonmarket.command.dispatcher;

import com.tining.demonmarket.command.UserCommand;
import com.tining.demonmarket.gui.AdminShopGui;
import com.tining.demonmarket.gui.ShopGui;
import com.tining.demonmarket.storage.ConfigReader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 商店相关指令
 * @author tinga
 */
public class ShopCommand extends AbstractCommander{
    @Override
    protected boolean solve(CommandSender sender, Command command, String label, String[] args) {
        if(ConfigReader.getDisableShop()){
            sender.sendMessage("商店已经被禁用");
            return true;
        }
        ShopGui.getShopGui((Player)sender);
        return true;
    }
}
