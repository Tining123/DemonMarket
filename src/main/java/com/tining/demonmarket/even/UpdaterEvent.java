package com.tining.demonmarket.even;

import com.tining.demonmarket.common.ref.Updater;
import com.tining.demonmarket.common.util.BukkitUtil;
import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.storage.ConfigReader;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * 更新信息时间
 */
public class UpdaterEvent implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void opJoin(PlayerJoinEvent e){
        if(BukkitUtil.getVersion().equals(Updater.getNewVersion())){
            return;
        }
        if(!ConfigReader.getVersionCheck()){
            return;
        }
        if(!Updater.isUpdateCheckDone()){
            return;
        }
        Player player = e.getPlayer();
        player.sendMessage(LangUtil.get("[DemonMarket]有新版本可用：")+ Updater.getNewVersion());
        player.sendMessage(LangUtil.get("[DemonMarket]更新内容：") + Updater.getDescription(LangUtil.getLang()));
        player.sendMessage(LangUtil.get("[DemonMarket]链接：") + Updater.getLink());
    }
}
