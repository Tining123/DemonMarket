package com.tining.demonmarket.gui.v1;

import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.common.util.PluginUtil;
import com.tining.demonmarket.gui.bean.SignMaterialEnum;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * V1版本列表GUI抽象类
 * @author tinga
 */
public abstract class AbstractListGUIV1<T extends DataV1> extends AbstractGUIV1{

    protected Integer lastIndex = 45;

    protected Integer pageIndex = 46;

    protected Integer nextIndex = 47;

    List<T> dataList;

    T selectItem;

    public AbstractListGUIV1(Player player,String guiName,List<T> dataList) {
        super(player,guiName);
        this.dataList = dataList;
        loadGui();
    }

    /**
     * 是否应该翻页
     *
     * @param slot
     * @return
     */
    public static boolean shouldTurnPage(int slot, Player player) {
        AbstractListGUIV1 gui = (AbstractListGUIV1) AbstractListGUIV1.getMe(player);
        return slot >= gui.viewSize;
    }


    /**
     * 获取选中的内容
     *
     * @param inventory
     * @param slot
     * @return
     */
    public void setSelectItem(Inventory inventory, int slot, Player player) {
        AbstractListGUIV1 me = (AbstractListGUIV1) map.get(player.getUniqueId(),getClazzType());
        if (slot >= me.getViewSize()) {
            me.selectItem = null;
        }

        try {
            me.decideSelectItem(inventory,slot,player);
        } catch (Exception e) {
        }
        me.selectItem = null;
    }

    @Override
    protected void drawPage(Inventory inventory, int pageNum, Player player){
        // 前画
        beforeDraw(inventory,pageNum,player);

        AbstractListGUIV1 me = (AbstractListGUIV1) AbstractListGUIV1.getMe(player);
        int viewSize = me.viewSize;
        List<T> datalist = me.dataList;
        int move = 0;
        boolean set = false;

        List<ItemStack> list = new ArrayList<>();
        for (T data : datalist) {
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

        T selectItem = (T) me.selectItem;
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

    /**
     * 绘制第N页的列表，子类翻页要用
     */
    protected void turnPage(Inventory inventory, int slot, Player player) {
        AbstractListGUIV1 me = (AbstractListGUIV1) AbstractListGUIV1.getMe(player);
        try {
            ItemStack itemStack = inventory.getItem(me.pageIndex);
            String name = itemStack.getItemMeta().getDisplayName();
            int page = Integer.parseInt(name.replace("<", "").replace(">", "").trim());

            if (Objects.equals(slot, me.lastIndex)) {
                if (page < 2) {
                    return;
                }
                drawPage(inventory, page - 2, player);
                return;
            }

            if (Objects.equals(slot, me.nextIndex)) {
                drawPage(inventory, page, player);
                return;
            }
        } catch (Exception ignore) {
        }
    }

    /**
     * 获取物品材质
     * @param item
     * @return
     */
    protected ItemStack getSignItemStack(T item){
        return new ItemStack(SignMaterialEnum.CONFIG.getMaterial());
    }

    /**
     * 决定选中的你内容
     * @param inventory
     * @param slot
     * @param player
     */
    public abstract void decideSelectItem(Inventory inventory, int slot, Player player);

    protected abstract void beforeDraw(Inventory inventory, int pageNum, Player player);

    protected abstract void afterDraw(Inventory inventory, int pageNum, Player player);

}
