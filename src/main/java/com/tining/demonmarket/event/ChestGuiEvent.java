package com.tining.demonmarket.event;

import com.tining.demonmarket.Main;
import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.gui.AcquireListGui;
import com.tining.demonmarket.gui.ChestGui;
import org.apache.commons.collections.CollectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.Objects;

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
            if(!Objects.equals(LangUtil.get("收购箱"),e.getView().getTitle())){
                return;
            }
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

    /**
     * 防止物品被挪动
     * @param e
     */
    @EventHandler(priority = EventPriority.LOW)
    public void disableMove(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player && e.getClickedInventory() != null) {
            Player player = (Player) e.getWhoClicked();
            if (ChestGui.isChestGui(player)) {
                if(ChestGui.isPriceIndex(e.getSlot()))
                    e.setCancelled(true);{
                    ChestGui.drawPage(player);
                }
            }
        }
    }

    /**
     * 刷新价格
     * @param e
     */
    public void flushInventory(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player && e.getClickedInventory() != null) {
            Player player = (Player) e.getWhoClicked();
            if (ChestGui.isChestGui(player)) {
                ChestGui.drawPage(player);
            }
        }
    }

    /**
     * 刷新价格
     * @param e
     */
    public void flushInventory(InventoryMoveItemEvent e) {
        Inventory source = e.getSource();
        if( !CollectionUtils.isEmpty(e.getSource().getViewers()) && e.getSource().getViewers().get(0) instanceof Player) {
            Player sourcePlayer = (Player) source.getViewers().get(0);
            if (ChestGui.isChestGui(sourcePlayer)) {
                ChestGui.drawPage(sourcePlayer);
            }
        }
        Inventory destination = e.getDestination();
        if( !CollectionUtils.isEmpty(e.getDestination().getViewers()) && e.getDestination().getViewers().get(0) instanceof Player) {
            Player destinationPlayer = (Player) destination.getViewers().get(0);
            if (ChestGui.isChestGui(destinationPlayer)) {
                ChestGui.drawPage(destinationPlayer);
            }
        }
    }

    /**
     * 刷新价格
     * @param e
     */
    public void flushInventory(InventoryDragEvent e) {
        if (e.getWhoClicked() instanceof Player && e.getInventory() != null) {
            Player player = (Player) e.getWhoClicked();
            if (ChestGui.isChestGui(player)) {
                ChestGui.drawPage(player);
            }
        }
    }
}
