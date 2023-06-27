package com.tining.demonmarket.event;

import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.gui.AcquireListGui;
import com.tining.demonmarket.gui.AdminShopGui;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.Objects;

/**
 * @author tinga
 */
public class AcquireListGuiEvent implements Listener {
    /**
     * 关闭时取消注册并结算
     *
     * @param e 消息句柄
     */
    @EventHandler
    public void close(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player) {
            Player player = (Player) e.getPlayer();
            AcquireListGui.unRegisterAcquireListGui(player);
        }

    }


    /**
     * 关闭时取消注册并结算
     *
     * @param e 消息句柄
     */
    @EventHandler
    public void close(PlayerQuitEvent e) {
        Player player = (Player) e.getPlayer();
        AcquireListGui.unRegisterAcquireListGui(player);
    }

    /**
     * 防止物品被挪动
     * @param e
     */
    @EventHandler(priority = EventPriority.LOW)
    public void disableMove(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player && e.getClickedInventory() != null) {
            Player player = (Player) e.getWhoClicked();
            if (AcquireListGui.isAcquireListGui(player)) {
                AcquireListGui.turnPage(e.getInventory(), e.getSlot() ,player);
                e.setCancelled(true);
            }
            else if(Objects.equals(LangUtil.get(AcquireListGui.GUI_NAME),e.getView().getTitle())){
                AcquireListGui.turnPage(e.getInventory(), e.getSlot() ,player);
                e.setCancelled(true);
            }
        }
    }
}
