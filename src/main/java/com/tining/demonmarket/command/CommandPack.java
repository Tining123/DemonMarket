package com.tining.demonmarket.command;

import lombok.Data;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * 消息包
 * @author tinga
 */
@Data
public class CommandPack {
    CommandSender sender;
    Command command;
    String label;
    String[] args;
}
