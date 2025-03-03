package com.tining.demonmarket.gui;

import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.common.util.PluginUtil;
import com.tining.demonmarket.gui.bean.SignMaterialEnum;
import com.tining.demonmarket.gui.v1.AbstractGUIV1;
import com.tining.demonmarket.gui.v1.AbstractListGUIV1;
import com.tining.demonmarket.gui.v1.SignEnumInterfaceV1;
import com.tining.demonmarket.storage.bean.Classify;
import com.tining.demonmarket.storage.bean.Group;
import com.tining.demonmarket.storage.bean.ShopItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class AdminClassifyListAddGui extends AbstractListGUIV1<ShopItem> {

    Group group;

    public AdminClassifyListAddGui(Player player, List<ShopItem> dataList, Group group) {
        super(player, "添加物品到分组", dataList);
        this.group = group;
    }

    @Override
    protected void drawPage(Inventory inventory, int pageNum, Player player) {
    // 前画
        beforeDraw(inventory,pageNum,player);

        AdminClassifyListAddGui me = (AdminClassifyListAddGui) AbstractListGUIV1.getMe(player);
        int viewSize = me.viewSize;
        List<ShopItem> datalist = me.dataList;
        int move = 0;
        boolean set = false;

        List<ItemStack> list = new ArrayList<>();
        for (ShopItem data : datalist) {
            ItemStack sign = getSignItemStack(data);
            PluginUtil.setName(sign, data.getName());
            list.add(sign);
        }

        for (int i = pageNum * viewSize; i < list.size() && i < (pageNum + 1) * viewSize; i++) {
            if (!Objects.isNull(list.get(i))) {
                if (!set) {
                    inventory.clear();
                    set = true;
                }
                inventory.setItem(move % viewSize, list.get(i));
                move++;
            }
        }

        //设置翻页图标
        ItemStack left = new ItemStack(SignMaterialEnum.LEFT.getMaterial(), 1);
        ItemStack right = new ItemStack(SignMaterialEnum.RIGHT.getMaterial(), 1);
        ItemStack mid = new ItemStack(SignMaterialEnum.PAGE.getMaterial(), 1);

        ShopItem selectItem = (ShopItem) me.selectItem;
        if (Objects.nonNull(selectItem)) {
            mid = new ItemStack(SignMaterialEnum.GROUP.getMaterial(), 1);
        }

        ItemMeta leftItemMeta = left.getItemMeta();
        ItemMeta rightItemMeta = right.getItemMeta();
        ItemMeta midItemMeta = right.getItemMeta();

        if (!Objects.isNull(leftItemMeta) && pageNum != 0 && pageNum != 1) {
            leftItemMeta.setDisplayName(LangUtil.get("上一页"));
            left.setItemMeta(leftItemMeta);
            inventory.setItem(me.lastIndex, left);
        }
        if (!Objects.isNull(rightItemMeta) && move != 0 && (pageNum + 1) * me.viewSize < me.dataList.size()) {
            rightItemMeta.setDisplayName(LangUtil.get("下一页"));
            right.setItemMeta(rightItemMeta);
            inventory.setItem(me.nextIndex, right);
        }
        if (!Objects.isNull(midItemMeta)) {
            midItemMeta.setDisplayName("< " + (pageNum + 1) + " >");
            mid.setItemMeta(midItemMeta);
            if (Objects.nonNull(selectItem)) {
                PluginUtil.addLore(mid, Collections.singletonList(selectItem.getName()));
            }

            inventory.setItem(me.pageIndex, mid);
        }

        // 后画
        afterDraw(inventory,pageNum,player);
    }

    @Override
    protected void beforeDraw(Inventory inventory, int pageNum, Player player) {

    }

    @Override
    protected void afterDraw(Inventory inventory, int pageNum, Player player) {

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
        ADD(49,"新增"){
            @Override
            public void deal(Inventory inventory, Player player) {
                AdminClassifyListGui me = (AdminClassifyListGui) AbstractGUIV1.getMe(player);
            }
        }
        ;
        @Getter
        private int slot;
        @Getter
        private String label;
    }

}
