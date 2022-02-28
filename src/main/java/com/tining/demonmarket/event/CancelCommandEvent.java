package com.tining.demonmarket.event;

import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.storage.ConfigReader;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class CancelCommandEvent implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void cancelPay(PlayerCommandPreprocessEvent e){
        String message = e.getMessage();
        if(message.startsWith("/mt pay")){
            return;
        }
        if(message.startsWith("/mt sell")){
            return;
        }
        //禁止pay
        if(ConfigReader.getDisablePay() && message.contains("pay")){
            List<String> commands = ConfigReader.getDisablePayList();
            for (String s : commands) {
                if (message.startsWith(s)) {
                    e.setCancelled(true);
                    Player player = e.getPlayer();
                    player.sendMessage(LangUtil.get("该命令已经停用，请使用/mt pay"));
                    return;
                }
            }
        }

        if(ConfigReader.getDisablePay() && message.contains("sell")){
            List<String> commands = ConfigReader.getDisableSellList();
            for (String s : commands) {
                if (message.startsWith(s)) {
                    e.setCancelled(true);
                    Player player = e.getPlayer();
                    player.sendMessage(LangUtil.get("该命令已经停用，请使用/mt sell"));
                    return;
                }
            }
        }
    }
}
