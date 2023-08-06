package com.tining.demonmarket.gui;

import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.common.util.PluginUtil;
import com.tining.demonmarket.gui.bean.SignMaterialEnum;
import com.tining.demonmarket.gui.v1.AbstractGUIV1;
import com.tining.demonmarket.gui.v1.SignEnumInterfaceV1;
import com.tining.demonmarket.storage.ClassifyReader;
import com.tining.demonmarket.storage.bean.Group;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AdminGroupSignSetGui extends AbstractGUIV1 {

    @Getter
    String groupName;

    public AdminGroupSignSetGui(Player player, String groupName) {
        super(player, LangUtil.get("设置分组图标"));
        this.groupName = groupName;
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
        setSign(inventory, SignEnum.WALL1,SignMaterialEnum.WALL,LangUtil.get("请将物品放入中间"),new ArrayList<>());
        setSign(inventory, SignEnum.WALL2,SignMaterialEnum.WALL,LangUtil.get("请将物品放入中间"),new ArrayList<>());
        setSign(inventory, SignEnum.WALL3,SignMaterialEnum.WALL,LangUtil.get("请将物品放入中间"),new ArrayList<>());
        setSign(inventory, SignEnum.WALL4,SignMaterialEnum.WALL,LangUtil.get("请将物品放入中间"),new ArrayList<>());
        setSign(inventory, SignEnum.WALL5,SignMaterialEnum.WALL,LangUtil.get("请将物品放入中间"),new ArrayList<>());
        setSign(inventory, SignEnum.WALL6,SignMaterialEnum.WALL,LangUtil.get("请将物品放入中间"),new ArrayList<>());
        setSign(inventory, SignEnum.WALL7,SignMaterialEnum.WALL,LangUtil.get("请将物品放入中间"),new ArrayList<>());
        setSign(inventory, SignEnum.WALL8,SignMaterialEnum.WALL,LangUtil.get("请将物品放入中间"),new ArrayList<>());

        setSign(inventory, SignEnum.CANCEL, SignMaterialEnum.OFF, LangUtil.get("取消"), new ArrayList<>());
        setSign(inventory, SignEnum.CONFIRM, SignMaterialEnum.ON, LangUtil.get("确认"), new ArrayList<>());
    }

    /**
     * 获取内部枚举列表
     *
     * @param player
     * @return
     */
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
                AdminGroupSignSetGui me = (AdminGroupSignSetGui) AbstractGUIV1.getMe(player);
                me.quit(player,inventory);
            }
        },
        CONFIRM(41,"确认"){
            @Override
            public void deal(Inventory inventory, Player player) {
                AdminGroupSignSetGui me = (AdminGroupSignSetGui) AbstractGUIV1.getMe(player);
                ItemStack itemStack = inventory.getItem(22);
                if(Objects.isNull(itemStack)){
                    return;
                }
                me.quit(player,inventory);
                Group group = new Group();
                group.setName(me.getGroupName());
                ItemStack copy = itemStack.clone();
                if (copy.getType().name().equals("AIR")) {
                    return;
                }
                group.setInfo(PluginUtil.getKeyName(itemStack));
                ClassifyReader.getInstance().addToGroupList(group);
            }
        },
        WALL1(12,"墙"){@Override public void deal(Inventory inventory, Player player) { }},
        WALL2(13,"墙"){@Override public void deal(Inventory inventory, Player player) { }},
        WALL3(14,"墙"){@Override public void deal(Inventory inventory, Player player) { }},
        WALL4(21,"墙"){@Override public void deal(Inventory inventory, Player player) { }},
        WALL5(23,"墙"){@Override public void deal(Inventory inventory, Player player) { }},
        WALL6(30,"墙"){@Override public void deal(Inventory inventory, Player player) { }},
        WALL7(31,"墙"){@Override public void deal(Inventory inventory, Player player) { }},
        WALL8(32,"墙"){@Override public void deal(Inventory inventory, Player player) { }},
        ;
        @Getter
        private int slot;
        @Getter
        private String label;
    }
}
