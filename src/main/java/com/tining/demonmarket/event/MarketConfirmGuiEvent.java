package com.tining.demonmarket.event;

import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.gui.MarketConfirmGui;
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
 * 市场确认事件
 * @author tinga
 */
public class MarketConfirmGuiEvent implements Listener {

    /**
     * 关闭时取消注册并结算
     *
     * @param e 消息句柄
     */
    @EventHandler
    public void close(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player) {
            Player player = (Player) e.getPlayer();
            MarketConfirmGui.unRegisterMarketConfirmGui(player);
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
        MarketConfirmGui.unRegisterMarketConfirmGui(player);
    }

    /**
     * 防止物品被挪动
     * @param e
     */
    @EventHandler(priority = EventPriority.LOW)
    public void disableMove(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player && e.getClickedInventory() != null) {
            Player player = (Player) e.getWhoClicked();
            if (MarketConfirmGui.isMarketConfirmGui(player) || Objects.equals(LangUtil.get(MarketConfirmGui.GUI_NAME), e.getView().getTitle())) {
                MarketConfirmGui marketConfirmGui = MarketConfirmGui.getMyMarketConfirmGui(player);
                // 检查是否关闭了窗口
                if(Objects.nonNull(marketConfirmGui)) {
                    MarketItem marketItem = marketConfirmGui.getMarketItem();
                    if (Objects.nonNull(e.getCurrentItem()) && Objects.nonNull(e.getCurrentItem().getItemMeta())) {
                        MarketConfirmGui.makeDecision(player, marketItem,
                                e.getSlot(), e.getCurrentItem().getItemMeta().getDisplayName());

                    }
                }
                e.setCancelled(true);
            }
        }
    }

}
