package com.tining.demonmarket.command.dispatcher;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * 商店相关指令
 * @author tinga
 */
public class ShopCommand extends AbstractCommander{
    @Override
    protected boolean solve(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }
}
