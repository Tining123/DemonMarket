package com.tining.demonmarket.command.dispatcher;

import com.tining.demonmarket.command.UserCommand;
import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.common.util.MarketUtil;
import com.tining.demonmarket.gui.MarketGui;
import com.tining.demonmarket.storage.ConfigReader;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;

/**
 * @author tinga
 */
public class AdminMarketCommand extends AbstractCommander{
    @Override
    protected boolean solve(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if(args.length == 1){
            MarketGui.getMarketGui((Player)sender);
            return true;
        }
        sender.sendMessage(UserCommand.getHelp());
        return true;
    }
}
