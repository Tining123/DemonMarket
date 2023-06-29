package com.tining.demonmarket.event;

import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.gui.PanelGui;
import com.tining.demonmarket.storage.bean.ShopItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

/**
 * 面板监听时间
 * @author tinga
 */
public class PanelGuiEvent implements Listener {

    /**
     * 关闭时取消注册并结算
     *
     * @param e 消息句柄
     */
    @EventHandler
    public void close(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player) {
            Player player = (Player) e.getPlayer();
            PanelGui.unRegisterPanelGui(player);
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
        PanelGui.unRegisterPanelGui(player);
    }

    /**
     * 防止物品被挪动
     * @param e
     */
    @EventHandler(priority = EventPriority.LOW)
    public void disableMove(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player && e.getClickedInventory() != null) {
            Player player = (Player) e.getWhoClicked();
            if (PanelGui.isPanelGui(player) || Objects.equals(LangUtil.get(PanelGui.GUI_NAME), e.getView().getTitle())) {
                if(Objects.nonNull(e.getCurrentItem()) && Objects.nonNull(e.getCurrentItem().getItemMeta())) {
                    PanelGui.makeDecision(player, e.getSlot(), e.getCurrentItem().getItemMeta().getDisplayName());
                }
                e.setCancelled(true);
            }
        }
    }
}
