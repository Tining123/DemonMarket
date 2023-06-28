package com.tining.demonmarket.event;

import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.gui.AdminMarketConfirmGui;
import com.tining.demonmarket.gui.AdminMarketGui;
import com.tining.demonmarket.storage.bean.MarketItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

/**
 * @author tinga
 */
public class AdminMarketGuiEvent implements Listener {

    /**
     * 关闭时取消注册并结算
     *
     * @param e 消息句柄
     */
    @EventHandler
    public void close(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player) {
            Player player = (Player) e.getPlayer();
            AdminMarketGui.unRegisterAdminMarketGui(player);
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
        AdminMarketGui.unRegisterAdminMarketGui(player);
    }

    /**
     * 防止物品被挪动
     * @param e
     */
    @EventHandler(priority = EventPriority.LOW)
    public void disableMove(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player && e.getClickedInventory() != null) {
            Player player = (Player) e.getWhoClicked();

            if (AdminMarketGui.isAdminMarketGui(player) || Objects.equals(LangUtil.get(AdminMarketGui.GUI_NAME), e.getView().getTitle())) {
                if(e.getSlot() < AdminMarketGui.VIEW_SIZE && !Objects.equals(player.getInventory(),e.getClickedInventory())) {
                    MarketItem marketItem = AdminMarketGui.getConfirmItem(e.getInventory(), e.getSlot(), player);
                    if(Objects.nonNull(marketItem)) {
                        player.closeInventory();
                        AdminMarketGui.unRegisterAdminMarketGui(player);
                        AdminMarketConfirmGui.getAdminMarketConfirmGui(player, marketItem, 1);
                    }
                }
                else{
                    AdminMarketGui.turnPage(e.getInventory(), e.getSlot(), player);
                }
                e.setCancelled(true);
            }
        }
    }
}
