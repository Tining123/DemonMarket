package com.tining.demonmarket.command.dispatcher;

import com.tining.demonmarket.gui.AdminShopGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 管理界面
 * @author tinga
 */
public class AdminShopCommand extends AbstractCommander{
    @Override
    protected boolean solve(CommandSender sender, Command command, String label, String[] args) {
        AdminShopGui.getAdminShopGui((Player)sender);
        return true;
    }
}
