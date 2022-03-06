package com.tining.demonmarket.task;

import com.tining.demonmarket.Main;
import com.tining.demonmarket.gui.ChestGui;
import org.apache.commons.collections.CollectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.UUID;

public class ChestDrawTask implements Runnable{
    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try{
            Set<UUID> playerUUIDs = ChestGui.getPlayerSet();
            if(CollectionUtils.isEmpty(playerUUIDs)){
                return;
            }
            for(UUID uuid: playerUUIDs){
                Player player = Bukkit.getPlayer(uuid);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ChestGui.drawPage(player);
                    }
                }.runTask(Main.getInstance());
            }
        }catch (Exception e){}
    }
}
