package com.tining.demonmarket.gui;

import com.tining.demonmarket.common.util.PluginUtil;
import com.tining.demonmarket.gui.bean.SignMaterialEnum;
import com.tining.demonmarket.gui.v1.AbstractGUIV1;
import com.tining.demonmarket.gui.v1.SignEnumInterfaceV1;
import com.tining.demonmarket.storage.ClassifyReader;
import com.tining.demonmarket.storage.bean.Group;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AdminGroupDeleteGui extends AbstractGUIV1 {

    Group group;

    public AdminGroupDeleteGui(Player player, Group group) {
        super(player, "确认删除分组");
        this.group = group;
        loadGui();
    }

    /**
     * 渲染画面
     *
     * @param inventory
     * @param pageNum
     * @param player
     */
    @Override
    protected void drawPage(Inventory inventory, int pageNum, Player player) {
        setSign(inventory,SignEnum.CANCEL, SignMaterialEnum.OFF,"取消",new ArrayList<>());
        setSign(inventory,SignEnum.CONFIRM, SignMaterialEnum.ON,"确认",new ArrayList<>());

        ItemStack itemStack = PluginUtil.getItemStackFromSaveNBTString(group.getInfo());
        inventory.setItem(SignEnum.ITEM.getSlot(), itemStack);
    }

    @Override
    public List<SignEnumInterfaceV1> getEnumList(Player player) {
        return Arrays.asList(SignEnum.values());
    }

    @AllArgsConstructor
    public static enum SignEnum implements SignEnumInterfaceV1{
        //---事件区---//
        CANCEL(39,"取消"){
            @Override
            public void deal(Inventory inventory, Player player) {
                AdminGroupDeleteGui me = (AdminGroupDeleteGui) AbstractGUIV1.getMe(player);
                me.quit(player,inventory);
            }
        },
        CONFIRM(41,"确认"){
            @Override
            public void deal(Inventory inventory, Player player) {
                AdminGroupDeleteGui me = (AdminGroupDeleteGui) AbstractGUIV1.getMe(player);
                ClassifyReader.getInstance().removeGroup(me.group);
                me.quit(player,inventory);
                player.sendMessage("删除成功");
            }
        },
        ITEM(22,"物品"){@Override public void deal(Inventory inventory, Player player) { } },
        ;
        @Getter
        private int slot;
        @Getter
        private String label;
    }
}
