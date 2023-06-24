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

    /**
     * 防止物品被放入商店列表
     * @param e
     */
    @EventHandler(priority = EventPriority.LOW)
    public void disableMove(InventoryMoveItemEvent e) {
        Inventory source = e.getSource();
        Inventory destination = e.getDestination();

        // 获取触发移动事件的玩家
        Player player = (Player) e.getInitiator();
        AcquireListGui acquireListGui = AcquireListGui.getMyAcquireListGui(player);

        if(Objects.nonNull(acquireListGui) && Objects.nonNull(acquireListGui.getInventory())){
            Inventory shopInventory = acquireListGui.getInventory();
            // 如果目标活源头是商店背包，返回
            if(destination.equals(shopInventory) || source.equals(shopInventory)){
                e.setCancelled(true);
            }
        }
    }
}
