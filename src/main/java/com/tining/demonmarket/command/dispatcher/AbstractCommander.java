package com.tining.demonmarket.command.dispatcher;

import com.tining.demonmarket.command.CommandPack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * 用于统一命令处理的抽象类
 * @author tinga
 */
public abstract class AbstractCommander {

    public boolean deal(CommandPack commandPack){
        CommandSender sender = commandPack.getSender();
        Command command = commandPack.getCommand();
        String label = commandPack.getLabel();
        String[] args = commandPack.getArgs();

        return solve(sender,command,label,args);
    }

    protected abstract boolean solve(CommandSender sender, Command command, String label, String[] args);
}
