package com.tining.demonmarket.gui.v1;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

public abstract class AbstractListGuiEventV1<T extends AbstractListGUIV1>{
    /**
     * 关闭时取消注册并结算
     *
     * @param e 消息句柄
     */
    @EventHandler
    public void close(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player) {
            Player player = (Player) e.getPlayer();
            T me = null;
            try{
                me = (T)T.getMe(player);
            }catch (Exception ignore){}
            if(Objects.isNull(me)){
                return;
            }
            T.unRegister(player);
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
        T me = null;
        try{
            me = (T)T.getMe(player);
        }catch (Exception ignore){}
        if(Objects.isNull(me)){
            return;
        }
        T.unRegister(player);
    }

    /**
     * 防止物品被挪动
     *
     * @param e
     */
    @EventHandler(priority = EventPriority.LOW)
    public void disableMove(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player && e.getClickedInventory() != null) {
            Player player = (Player) e.getWhoClicked();
            // 是否应该响应
            if (T.shouldEffective(player, e.getView().getTitle())) {
                T me = null;
                try{
                    me = (T)T.getMe(player);
                }catch (Exception ignore){}
                if(Objects.isNull(me)){
                    return;
                }
                e.setCancelled(true);
                SignEnumInterfaceV1 signEnum = me.findMatchedSign(e.getSlot(),me.getEnumList(player));
                if (Objects.nonNull(signEnum)) {
                    signEnum.deal(e.getClickedInventory(), player);
                }else if(e.getSlot() < me.getViewSize()){
                    me.setSelectItem(e.getClickedInventory(), e.getSlot(), player);
                }
            }
        }
    }
}
