package com.tining.demonmarket.event;

import com.tining.demonmarket.gui.AdminGroupSignSetGui;
import com.tining.demonmarket.gui.v1.AbstractGuiEventV1;
import com.tining.demonmarket.gui.v1.AbstractListGuiEventV1;
import com.tining.demonmarket.gui.v1.SignEnumInterfaceV1;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

public class AdminGroupSignSetGuiEvent extends AbstractGuiEventV1<AdminGroupSignSetGui> implements Listener {


    /**
     * 防止物品被挪动
     *
     * @param e
     */
    @Override
    @EventHandler(priority = EventPriority.LOW)
    public void disableMove(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player && e.getClickedInventory() != null) {
            Player player = (Player) e.getWhoClicked();
            // 是否应该响应
            if (AdminGroupSignSetGui.shouldEffective(player, e.getView().getTitle())) {
                int rowSlot = e.getRawSlot();
                AdminGroupSignSetGui me = null;
                try{
                    me = (AdminGroupSignSetGui)AdminGroupSignSetGui.getMe(player);
                }catch (Exception ignore){}
                if(Objects.isNull(me)){
                    return;
                }
                if(rowSlot >= me.getViewSize() || rowSlot == 22){
                    return;
                }
                e.setCancelled(true);
                SignEnumInterfaceV1 signEnum = me.findMatchedSign(e.getSlot(),me.getEnumList(player));
                if (Objects.nonNull(signEnum)) {
                    signEnum.deal(e.getClickedInventory(), player);
                }
            }
        }
    }
}
