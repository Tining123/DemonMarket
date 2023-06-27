package com.tining.demonmarket.event;

import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.gui.ShopConfirmGui;
import com.tining.demonmarket.gui.ShopGui;
import com.tining.demonmarket.storage.bean.ShopItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.Objects;

/**
 * 商店页面
 * @author tinga
 */
public class ShopGuiEvent implements Listener {
    /**
     * 关闭时取消注册并结算
     *
     * @param e 消息句柄
     */
    @EventHandler
    public void close(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player) {
            Player player = (Player) e.getPlayer();
            ShopGui.unRegisterShopGui(player);
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
        ShopGui.unRegisterShopGui(player);
    }

    /**
     * 防止物品被挪动
     * @param e
     */
    @EventHandler(priority = EventPriority.LOW)
    public void disableMove(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player && e.getClickedInventory() != null) {
            Player player = (Player) e.getWhoClicked();

            if (ShopGui.isShopGui(player) || Objects.equals(LangUtil.get(ShopGui.GUI_NAME), e.getView().getTitle())) {
                if(e.getSlot() < ShopGui.VIEW_SIZE && !Objects.equals(player.getInventory(),e.getClickedInventory())) {
                    ShopItem shopItem = ShopGui.getConfirmItem(e.getInventory(), e.getSlot(), player);
                    player.closeInventory();
                    ShopGui.unRegisterShopGui(player);
                    ShopConfirmGui.getShopConfirmGui(player, shopItem,1);
                }
                else{
                    ShopGui.turnPage(e.getInventory(), e.getSlot(), player);
                }
                e.setCancelled(true);
            }
        }
    }

}
