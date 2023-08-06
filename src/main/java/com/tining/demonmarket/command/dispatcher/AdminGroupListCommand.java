package com.tining.demonmarket.command.dispatcher;

import com.tining.demonmarket.gui.AdminGroupListGui;
import com.tining.demonmarket.storage.ClassifyReader;
import com.tining.demonmarket.storage.bean.Group;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class AdminGroupListCommand extends AbstractCommander{
    @Override
    protected boolean solve(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        // 构造group列表
        List groupList = ClassifyReader.getInstance().getForGroupList();
        new AdminGroupListGui(player, groupList);
        return true;
    }
}
