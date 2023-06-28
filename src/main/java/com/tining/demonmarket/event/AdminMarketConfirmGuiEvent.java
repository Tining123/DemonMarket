package com.tining.demonmarket.event;

import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.gui.MarketConfirmGui;
import com.tining.demonmarket.gui.AdminMarketConfirmGui;
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

public class AdminMarketConfirmGuiEvent implements Listener {
    /**
     * 关闭时取消注册并结算
     *
     * @param e 消息句柄
     */
    @EventHandler
    public void close(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player) {
            Player player = (Player) e.getPlayer();
            AdminMarketConfirmGui.unRegisterAdminMarketConfirmGui(player);
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
        AdminMarketConfirmGui.unRegisterAdminMarketConfirmGui(player);
    }

    /**
     * 防止物品被挪动
     * @param e
     */
    @EventHandler(priority = EventPriority.LOW)
    public void disableMove(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player && e.getClickedInventory() != null) {
            Player player = (Player) e.getWhoClicked();
            if (AdminMarketConfirmGui.isAdminMarketConfirmGui(player) || Objects.equals(LangUtil.get(AdminMarketConfirmGui.GUI_NAME), e.getView().getTitle())) {

                AdminMarketConfirmGui adminMarketConfirmGui = AdminMarketConfirmGui.getMyAdminMarketConfirmGui(player);

                if(Objects.nonNull(adminMarketConfirmGui)) {
                    MarketItem marketItem = adminMarketConfirmGui.getMarketItem();

                    if (Objects.nonNull(e.getCurrentItem()) && Objects.nonNull(e.getCurrentItem().getItemMeta())) {
                        AdminMarketConfirmGui.makeDecision(player, marketItem,
                                e.getSlot(), e.getCurrentItem().getItemMeta().getDisplayName());

                    }
                }
                e.setCancelled(true);
            }
        }
    }

}
