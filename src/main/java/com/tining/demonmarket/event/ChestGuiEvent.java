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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.UUID;

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
    @EventHandler(priority = EventPriority.LOWEST)
    public void disableMove(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player && e.getClickedInventory() != null) {
            Player player = (Player) e.getWhoClicked();
            if (ChestGui.isChestGui(player)) {
                if(ChestGui.isPriceIndex(e.getSlot())) {
                    e.setCancelled(true);
                }
            }
            else if(Objects.equals(LangUtil.get("收购箱"),e.getView().getTitle())){
                if(ChestGui.isPriceIndex(e.getSlot())) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void refresh(InventoryClickEvent e){
        if (e.getWhoClicked() instanceof Player && e.getClickedInventory() != null) {
            Player player = (Player) e.getWhoClicked();
            if (ChestGui.isChestGui(player)) {
                Bukkit.getRegionScheduler().run(Main.getInstance(), player.getLocation(), scheduledTask -> ChestGui.drawPage(player));
            }
        }
    }

}
