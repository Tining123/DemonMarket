package com.tining.demonmarket.common.util;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tining.demonmarket.storage.ConfigFileNameEnum;
import com.tining.demonmarket.storage.ConfigReader;
import com.tining.demonmarket.storage.bean.ShopItem;
import com.tining.demonmarket.storage.bean.ShopItemType;
import org.apache.commons.collections.CollectionUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * @author tinga
 */
public class ShopUtil {

    private static final Logger log = Logger.getLogger("Minecraft");

    /**
     * 出售价格地图，第一位是名称，第二位是info
     */
    private final static Table<String, String, ShopItem> PRICE_TABLE = HashBasedTable.create();

    public final static List<ShopItem> PRICE_LIST = new CopyOnWriteArrayList<>();

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

        if(PRICE_TABLE.contains(name, nbtName)){
            return PRICE_TABLE.get(name, nbtName).getPrice();
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

        return PRICE_TABLE.contains(name, nbtName);
    }

    /**
     * 以非NBT的正常加入价格表
     * @param is
     * @return
     */
    public static boolean addToCommonPrice(ItemStack is, Double price){
        return addToPrice(is,price,ShopItemType.COMMON_ITEM);
    }

    /**
     * 以非NBT的正常加入价格表
     * @param is
     * @return
     */
    public static boolean addToNBTPrice(ItemStack is, Double price){
        return addToPrice(is,price,ShopItemType.NBT_ITEM);
    }

    /**
     * 添加到价格表
     * @param is
     * @param price
     * @param itemType
     * @return
     */
    private static boolean addToPrice(ItemStack is, Double price, ShopItemType itemType){
        // 设置物品细节
        ShopItem item = setShopItemProperties(is, itemType);
        item.setPrice(price);

        // 加入
        synchronized (PRICE_TABLE){
            synchronized (PRICE_LIST) {
                if(PRICE_TABLE.contains(item.getName(), item.getInfo())){
                    ShopItem newItem = PRICE_TABLE.get(item.getName(), item.getInfo());
                    newItem.setPrice(price);
                    // 如果已经存在，循环并找到，修改
                    for(ShopItem shopItem : PRICE_LIST){
                        if(shopItem.equals(newItem)){
                            shopItem.setPrice(newItem.getPrice());
                            break;
                        }
                    }
                }else{
                    PRICE_TABLE.put(item.getName(), item.getInfo(), item);
                    // 添加到队尾
                    PRICE_LIST.add(item);
                }
                // 将现状回装填到config
                saveAndReloadShop();
            }
        }
        return true;
    }

    /**
     * 物品在列表中向前移动
     * @param is
     * @param itemType
     */
    public static boolean moveUpItem(ItemStack is, ShopItemType itemType){
        // 设置物品细节
        ShopItem item = setShopItemProperties(is, itemType);

        // 删除
        synchronized (PRICE_TABLE){
            synchronized (PRICE_LIST) {
                if(PRICE_TABLE.contains(item.getName(), item.getInfo())){
                    ShopItem newItem = PRICE_TABLE.get(item.getName(), item.getInfo());
                    // 如果已经存在，循环并找到，修改
                    for(int i = 0 ;i < PRICE_LIST.size();i++){
                        ShopItem shopItem = PRICE_LIST.get(i);
                        if (shopItem.equals(newItem)) {
                            // 如果已经是第一个，返回
                            if (i <= 0) {
                                return false;
                            }
                            Collections.swap(PRICE_LIST, i, i - 1);
                            saveAndReloadShop();
                            return true;
                        }
                    }
                }else{
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 物品在列表中向后移动
     * @param is
     * @param itemType
     */
    public static boolean moveDownItem(ItemStack is, ShopItemType itemType){
        // 设置物品细节
        ShopItem item = setShopItemProperties(is, itemType);

        // 删除
        synchronized (PRICE_TABLE){
            synchronized (PRICE_LIST) {
                if(PRICE_TABLE.contains(item.getName(), item.getInfo())){
                    ShopItem newItem = PRICE_TABLE.get(item.getName(), item.getInfo());
                    // 如果已经存在，循环并找到，修改
                    for(int i = 0 ;i < PRICE_LIST.size();i++){
                        ShopItem shopItem = PRICE_LIST.get(i);
                        if (shopItem.equals(newItem)) {
                            // 如果已经是第一个，返回
                            if (i >= PRICE_LIST.size() - 1) {
                                return false;
                            }
                            Collections.swap(PRICE_LIST, i, i + 1);
                            saveAndReloadShop();
                            return true;
                        }
                    }
                }else{
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 从列表中删除
     * @param is
     * @param itemType
     */
    public static boolean removeFromShop(ItemStack is, ShopItemType itemType){
        // 设置物品细节
        ShopItem item = setShopItemProperties(is, itemType);

        // 删除
        synchronized (PRICE_TABLE){
            synchronized (PRICE_LIST) {
                if(PRICE_TABLE.contains(item.getName(), item.getInfo())){
                    ShopItem newItem = PRICE_TABLE.get(item.getName(), item.getInfo());
                    // 如果已经存在，循环并找到，修改
                    for(int i = 0 ;i < PRICE_LIST.size();i++){
                        ShopItem shopItem = PRICE_LIST.get(i);
                        if(shopItem.equals(newItem)){
                            PRICE_LIST.remove(i);
                            saveAndReloadShop();
                            return true;
                        }
                    }
                }else{
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 设置 ShopItem 的属性
     * @param is 物品
     * @param itemType 物品类型
     * @return 设置后的 ShopItem 对象
     */
    private static ShopItem setShopItemProperties(ItemStack is, ShopItemType itemType) {
        Material material = is.getType();
        // 调整数量为1
        is.setAmount(1);
        String name = material.name();
        String nbtName = PluginUtil.getKeyName(is);

        // 设置物品细节
        ShopItem item = new ShopItem();
        item.setInfo(nbtName);
        // 如果是通常物品，记录原名为nbt
        if (itemType.equals(ShopItemType.COMMON_ITEM)) {
            item.setInfo(name);
        }
        item.setItemStack(is);
        item.setName(name);
        item.setItemType(itemType);
        item.setType(itemType.getName());
        return item;
    }

    /**
     * 保存并重装商店信息
     */
    private static void saveAndReloadShop(){
        FileConfiguration config = getShopConfig();
        String rootSection = ConfigFileNameEnum.SHOP_PRICE_NAME.getRootSection();
        config.addDefault(rootSection, formatShopItemList(PRICE_LIST));
        config.set(rootSection, formatShopItemList(PRICE_LIST));

        ConfigReader.saveConfig(ConfigFileNameEnum.SHOP_PRICE_NAME.getName(), config);
        reloadShop();;
    }

    /**
     * 获取商店物品总价值表
     * @return 商店物品总价值表
     */
    public static void reloadShop() {
        // log.info("【DemonMarket】Loading shop price list");
        FileConfiguration config = getShopConfig();
        List<ShopItem> shopItemList = new ArrayList<>();
        List<Map<?,?>> sourceList = config.getMapList(ConfigFileNameEnum.SHOP_PRICE_NAME.getRootSection());
        for (Map<?, ?> map : sourceList) {
            ShopItem shopItem = new ShopItem();
            shopItem.setType((String) map.get("type"));
            shopItem.setInfo((String) map.get("info"));
            shopItem.setName((String) map.get("name"));
            shopItem.setPrice(Double.parseDouble((String)map.get("price")));
            shopItemList.add(shopItem);
        }

        if(!CollectionUtils.isEmpty(shopItemList)){
            // 构建list
            PRICE_LIST.clear();
            PRICE_LIST.addAll(shopItemList);
            // 补充信息
            for(int i = 0 ; i < PRICE_LIST.size(); i ++){
                ShopItem shopItem  = PRICE_LIST.get(i);
                shopItem.setItemType(ShopItemType.getType(shopItem.getType()));
                shopItem.setItemStack(getItem(shopItem.getName(), shopItem.getInfo(), shopItem.getItemType()));
                // 过滤异常物品
                if(Objects.isNull(shopItem.getItemStack())){
                    PRICE_LIST.remove(i);
                    i--;
                    continue;
                }
                // 同时构建table
                PRICE_TABLE.put(shopItem.getName(),shopItem.getInfo(), shopItem);
            }
        }
        // log.info("【DemonMarket】Shop price list loaded");
    }

    /**
     * 根据下标获取物品
     * @param index
     * @return
     */
    public static ShopItem getShopItem(int index){
        if(index < 0 || index >= PRICE_LIST.size()){
            return null;
        }
        return PRICE_LIST.get(index);
    }

    /**
     * 格式化商品列表
     * @param shopItemList
     * @return
     */
    private static List<Map<String,String>> formatShopItemList(List<ShopItem> shopItemList){
        List<Map<String,String>> formatList = new ArrayList<>();

        for (int i = 0 ; i < shopItemList.size();i ++){
            formatList.add(convertClassToMap(shopItemList.get(i)));
        }
        return formatList;
    }

    /**
     * 类转map
     * @param obj
     * @return
     */
    private static Map<String, String> convertClassToMap(Object obj) {
        Map<String, String> resultMap = new HashMap<>();

        try {
            // 获取类的所有属性
            Field[] fields = obj.getClass().getDeclaredFields();

            for (Field field : fields) {
                // 检查属性是否被标记为 transient
                if (Modifier.isTransient(field.getModifiers())) {
                    continue; // 跳过 transient 属性
                }

                field.setAccessible(true);

                // 获取属性名和属性值
                String fieldName = field.getName();
                String fieldValue = String.valueOf(field.get(obj));

                // 将属性名和属性值添加到 Map 中
                resultMap.put(fieldName, fieldValue);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return resultMap;
    }



    /**
     * 加载物品内容
     * @param name
     * @param nbtInfo
     * @param shopItemType
     * @return
     */
    private static ItemStack getItem(String name, String nbtInfo, ShopItemType shopItemType){
        if(shopItemType.equals(ShopItemType.COMMON_ITEM)){
            return PluginUtil.getItem(name);
        }else if(shopItemType.equals(ShopItemType.NBT_ITEM)){
            return PluginUtil.getItemStackFromSaveNBTString(nbtInfo);
        }

        return PluginUtil.getItem(name);
    }

    private static FileConfiguration getShopConfig() {
        return ConfigReader.getConfigMap().get(ConfigFileNameEnum.SHOP_PRICE_NAME.getName());
    }

}
