package com.tining.demonmarket.event;

import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.gui.MarketConfirmGui;
import com.tining.demonmarket.gui.MarketGui;
import com.tining.demonmarket.gui.ShopConfirmGui;
import com.tining.demonmarket.gui.ShopGui;
import com.tining.demonmarket.storage.bean.MarketItem;
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
 * 市场事件
 * @author tinga
 */
public class MarketGuiEvent implements Listener {
    /**
     * 关闭时取消注册并结算
     *
     * @param e 消息句柄
     */
    @EventHandler
    public void close(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player) {
            Player player = (Player) e.getPlayer();
            MarketGui.unRegisterMarketGui(player);
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
        MarketGui.unRegisterMarketGui(player);
    }

    /**
     * 防止物品被挪动
     * @param e
     */
    @EventHandler(priority = EventPriority.LOW)
    public void disableMove(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player && e.getClickedInventory() != null) {
            Player player = (Player) e.getWhoClicked();

            if (MarketGui.isMarketGui(player) || Objects.equals(LangUtil.get(MarketGui.GUI_NAME), e.getView().getTitle())) {
                if(e.getSlot() < MarketGui.VIEW_SIZE && !Objects.equals(player.getInventory(),e.getClickedInventory())) {
                    MarketItem marketItem = MarketGui.getConfirmItem(e.getInventory(), e.getSlot(), player);
                    if(Objects.nonNull(marketItem)) {
                        player.closeInventory();
                        MarketGui.unRegisterMarketGui(player);
                        MarketConfirmGui.getMarketConfirmGui(player, marketItem, 1);
                    }
                }
                else{
                    MarketGui.turnPage(e.getInventory(), e.getSlot(), player);
                }
                e.setCancelled(true);
            }
        }
    }
}
