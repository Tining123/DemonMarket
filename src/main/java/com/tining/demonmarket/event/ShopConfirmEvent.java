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

import java.util.Objects;

/**
 * 购买确认事件
 * @author tinga
 */
public class ShopConfirmEvent implements Listener {

    /**
     * 关闭时取消注册并结算
     *
     * @param e 消息句柄
     */
    @EventHandler
    public void close(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player) {
            Player player = (Player) e.getPlayer();
            ShopConfirmGui.unRegisterShopConfirmGui(player);
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
        ShopConfirmGui.unRegisterShopConfirmGui(player);
    }

    /**
     * 防止物品被挪动
     * @param e
     */
    @EventHandler(priority = EventPriority.LOW)
    public void disableMove(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player && e.getClickedInventory() != null) {
            Player player = (Player) e.getWhoClicked();
            if (ShopConfirmGui.isShopConfirmGui(player) || Objects.equals(LangUtil.get(ShopConfirmGui.GUI_NAME), e.getView().getTitle())) {
                ShopItem shopItem = ShopConfirmGui.getMyShopConfirmGui(player).getShopItem();
                int amount = ShopConfirmGui.getMyShopConfirmGui(player).getStackNum();

                if(Objects.nonNull(e.getCurrentItem()) && Objects.nonNull(e.getCurrentItem().getItemMeta())) {
                    ShopConfirmGui.makeDecesion(player, shopItem, amount,
                            e.getSlot(), e.getCurrentItem().getItemMeta().getDisplayName());

                }
                e.setCancelled(true);
            }
        }
    }

}
