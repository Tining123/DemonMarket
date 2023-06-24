package com.tining.demonmarket.event;

import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.gui.AdminShopGui;
import com.tining.demonmarket.gui.ShopGui;
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
 * 商店管理员事件
 * @author tinga
 */
public class AdminShopGuiEvent implements Listener {
    /**
     * 关闭时取消注册并结算
     *
     * @param e 消息句柄
     */
    @EventHandler
    public void close(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player) {
            Player player = (Player) e.getPlayer();
            AdminShopGui.unRegisterAdminShopGui(player);
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
        AdminShopGui.unRegisterAdminShopGui(player);
    }

    /**
     * 防止物品被挪动
     * @param e
     */
    @EventHandler(priority = EventPriority.LOW)
    public void disableMove(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player && e.getClickedInventory() != null) {
            Player player = (Player) e.getWhoClicked();

            if (AdminShopGui.isAdminShopGui(player) || Objects.equals(LangUtil.get(AdminShopGui.GUI_NAME), e.getView().getTitle())) {
                if(Objects.equals(e.getSlot(),AdminShopGui.PAGE_SIGN_INDEX) && !Objects.equals(player.getInventory(),e.getClickedInventory())){
                    AdminShopGui.removeEditingItem(e.getInventory(), e.getSlot(), player);
                }
                else if(e.getSlot() < AdminShopGui.VIEW_SIZE && !Objects.equals(player.getInventory(),e.getClickedInventory())){
                    AdminShopGui.setEditingItem(e.getInventory(), e.getSlot(), player);
                }else if(e.getSlot() == AdminShopGui.LEFT_MOVE_ARROW_INDEX || e.getSlot() == AdminShopGui.RIGHT_MOVE_ARROW_INDEX
                        && !Objects.equals(player.getInventory(),e.getClickedInventory())){
                    AdminShopGui.moveEditingItem(e.getInventory(), e.getSlot(), player);
                }else{
                    AdminShopGui.turnPage(e.getInventory(), e.getSlot(), player);
                }
                e.setCancelled(true);
            }
        }
    }


}
