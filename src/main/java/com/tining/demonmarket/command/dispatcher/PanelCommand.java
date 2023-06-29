package com.tining.demonmarket.command.dispatcher;

import com.tining.demonmarket.gui.PanelGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PanelCommand extends AbstractCommander{
    @Override
    protected boolean solve(CommandSender sender, Command command, String label, String[] args) {
        PanelGui.getPanelGui((Player)sender);
        return true;
    }
}
