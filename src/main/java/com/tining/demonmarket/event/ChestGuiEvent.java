package com.tining.demonmarket.event;

import com.tining.demonmarket.gui.ChestGui;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
/**
 * @author tinga
 */
public class ChestGuiEvent implements Listener {

    /**
     * 关闭时取消注册并结算
     *
     * @param e 消息句柄
     */
    @EventHandler
    public void close(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player) {
            Player player = (Player) e.getPlayer();
            ChestGui.checkOutPlayer(player);
            ChestGui.unRegisterChestGui(player);
        }

    }


    /**
     * 关闭时取消注册并结算
     *
     * @param e 消息句柄
     */
    @EventHandler
    public void close(PlayerQuitEvent e) {
        if (e.getPlayer() instanceof Player) {
            Player player = (Player) e.getPlayer();
            ChestGui.checkOutPlayer(player);
            ChestGui.unRegisterChestGui(player);
        }

    }
}
