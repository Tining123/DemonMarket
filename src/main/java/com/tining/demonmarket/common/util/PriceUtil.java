package com.tining.demonmarket.common.util;

import com.google.common.collect.Table;
import com.tining.demonmarket.storage.bean.ShopItem;
import com.tining.demonmarket.storage.bean.ShopItemType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * @author tinga
 */
public class PriceUtil {
    /**
     * 出售价格地图
     */
    private static Table<String, String, ShopItem> priceTable;

    private static List<ShopItem> 

    /**
     * 获取物品价值，如果不存在，返回-1
     *
     * @param is 物品
     * @return
     */
    public static double getItemPrice(ItemStack is) {
        Material material = is.getType();

        String name = material.name();
        String nbtName = PluginUtil.getKeyName(is);

        if(priceTable.contains(name, nbtName)){
            return priceTable.get(name, nbtName).getPrice();
        }else {
            return -1;
        }
    }

    /**
     * 判断是否存在price当中
     *
     * @param is 物品类型
     * @return
     */
    public static boolean isPriceContain(ItemStack is) {
        Material material = is.getType();

        String name = material.name();
        String nbtName = PluginUtil.getKeyName(is);

        return priceTable.contains(name, nbtName);
    }

    /**
     * 以非NBT的正常加入
     * @param is
     * @return
     */
    public static boolean addToPrice(ItemStack is, Double price){
        Material material = is.getType();
        is.setAmount(1);
        String name = material.name();
        String nbtName = PluginUtil.getKeyName(is);

        ShopItem item = new ShopItem();
        item.setInfo(nbtName);
        item.setIs(is);
        item.setName(name);
        item.setItemType(ShopItemType.COMMON_ITEM);
        item.setType(ShopItemType.COMMON_ITEM.getName());
        item.setPrice(price);

        synchronized (priceTable){
            int size = priceTable.size();
            item.setRank(size + 1);


        }
    }
}
