package com.tining.demonmarket.common.util;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.tining.demonmarket.storage.ConfigFileNameEnum;
import com.tining.demonmarket.storage.ConfigReader;
import com.tining.demonmarket.storage.bean.MarketItem;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * 市场接口
 * @author tinga
 */
public class MarketUtil {
    private static final Logger log = Logger.getLogger("Minecraft");

    /**
     * 第一位是玩家UUID，第二位是NBT，第三位是目标
     */
    private final static Table<String, String, MarketItem> MARKET_TABLE = HashBasedTable.create();

    public final static List<MarketItem> MARKET_LIST = new CopyOnWriteArrayList<>();

    /**
     * 获取物品价值，如果不存在，返回-1
     *
     * @param is 物品
     * @return
     */
    public static double getItemPrice(Player player, ItemStack is) {
        Material material = is.getType();

        String name = player.getName();
        String nbtName = PluginUtil.getKeyName(is);

        if(MARKET_TABLE.contains(name, nbtName)){
            return MARKET_TABLE.get(name, nbtName).getPrice();
        }else {
            return -1;
        }
    }

    /**
     * 判断是否存在列表当中
     *
     * @param is 物品类型
     * @return
     */
    public static boolean isMarketContain(Player player, ItemStack is) {
        String name = player.getName();
        String nbtName = PluginUtil.getKeyName(is);

        return MARKET_TABLE.contains(name, nbtName);
    }

    /**
     * 获取当前玩家售卖数量
     * @param player
     * @return
     */
    public static int userSellCount(Player player){
        return MARKET_TABLE.row(player.getName()).size();
    }

    /**
     * 添加到价格表
     * @param is
     * @param price
     * @return
     */
    public static boolean addToMarket(Player player, ItemStack is, Double price){
        // 设置物品细节
        MarketItem item = setMarketItemProperties(player, is);
        item.setPrice(price);

        // 加入
        synchronized (MARKET_TABLE){
            synchronized (MARKET_LIST) {
                MARKET_TABLE.put(player.getName(), item.getInfo(), item);
                // 添加到队首
                MARKET_LIST.add(0,item);
                // 将现状回装填到config
                saveAndReloadMarket();
            }
        }
        return true;
    }

    /**
     * 从列表中删除
     * @param is
     */
    public static boolean removeFromMarket(String playName, ItemStack is){
        // 设置物品细节
        MarketItem item = setMarketItemProperties(playName,is);

        // 删除
        synchronized (MARKET_TABLE){
            synchronized (MARKET_LIST) {
                if(MARKET_TABLE.contains(playName, item.getInfo())){
                    MarketItem newItem = MARKET_TABLE.get(playName, item.getInfo());
                    // 如果已经存在，循环并找到，修改
                    for(int i = 0 ;i < MARKET_LIST.size();i++){
                        MarketItem marketItem = MARKET_LIST.get(i);
                        if(marketItem.equals(newItem)){
                            MARKET_LIST.remove(i);
                            MARKET_TABLE.remove(playName, item.getInfo());
                            saveAndReloadMarket();
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
     * 设置 MarketItem 的属性
     * @param is 物品
     * @return 设置后的 MarketItem 对象
     */
    private static MarketItem setMarketItemProperties(Player player, ItemStack is) {
        Material material = is.getType();
        String name = material.name();
        String nbtName = PluginUtil.getKeyName(is);

        Date publishDate = new Date();
        String dateString = DateUtil.formatDateToString(publishDate);

        // 设置物品细节
        MarketItem item = new MarketItem();
        item.setInfo(nbtName);
        // 如果是通常物品，记录原名为nbt
        item.setItemStack(is);
        item.setName(name);
        item.setAmount(is.getAmount());
        item.setPublicDate(publishDate);
        item.setDateString(dateString);
        item.setOwnerName(player.getName());
        return item;
    }

    /**
     * 设置 MarketItem 的属性
     * @param is 物品
     * @return 设置后的 MarketItem 对象
     */
    private static MarketItem setMarketItemProperties(String player, ItemStack is) {
        Material material = is.getType();
        String name = material.name();
        String nbtName = PluginUtil.getKeyName(is);

        Date publishDate = new Date();
        String dateString = DateUtil.formatDateToString(publishDate);

        // 设置物品细节
        MarketItem item = new MarketItem();
        item.setInfo(nbtName);
        // 如果是通常物品，记录原名为nbt
        item.setItemStack(is);
        item.setName(name);
        item.setAmount(is.getAmount());
        item.setPublicDate(publishDate);
        item.setDateString(dateString);
        item.setOwnerName(player);
        return item;
    }

    /**
     * 保存并重装商店信息
     */
    private static void saveAndReloadMarket(){
        FileConfiguration config = getMarketConfig();
        String rootSection = ConfigFileNameEnum.MARKET_DB_NAME.getRootSection();
        config.addDefault(rootSection, formatMarketItemList(MARKET_LIST));
        config.set(rootSection, formatMarketItemList(MARKET_LIST));

        ConfigReader.saveConfig(ConfigFileNameEnum.MARKET_DB_NAME.getName(), config);
        reloadMarket();;
    }

    /**
     * 获取商店物品总价值表
     * @return 商店物品总价值表
     */
    public static void reloadMarket() {
        // log.info("【DemonMarket】Loading market price list");
        FileConfiguration config = getMarketConfig();
        List<MarketItem> marketItemList = new ArrayList<>();
        List<Map<?,?>> sourceList = config.getMapList(ConfigFileNameEnum.MARKET_DB_NAME.getRootSection());
        for (Map<?, ?> map : sourceList) {
            MarketItem marketItem = new MarketItem();
            marketItem.setInfo((String) map.get("info"));
            marketItem.setName((String) map.get("name"));
            marketItem.setAmount(Integer.parseInt((String) map.get("amount")));
            marketItem.setOwnerName((String) map.get("ownerName"));
            marketItem.setDateString((String) map.get("dateString"));
            marketItem.setPrice(Double.parseDouble((String)map.get("price")));
            try {
                marketItem.setPublicDate(DateUtil.parseStringToDate(marketItem.getDateString()));
            }catch (Exception ignore){}
            ItemStack itemStack = getItem(marketItem.getInfo());
            itemStack.setAmount(marketItem.getAmount());
            marketItem.setItemStack(itemStack);
            marketItemList.add(marketItem);
        }

        if(!CollectionUtils.isEmpty(marketItemList)){
            synchronized (MARKET_TABLE) {
                synchronized (MARKET_LIST) {
                    // 构建list
                    MARKET_LIST.clear();
                    MARKET_LIST.addAll(marketItemList);
                    // 补充信息
                    for (int i = 0; i < MARKET_LIST.size(); i++) {
                        MarketItem marketItem = MARKET_LIST.get(i);
                        // 过滤异常物品
                        // 同时构建table
                        MARKET_TABLE.put(marketItem.getOwnerName(), marketItem.getInfo(), marketItem);
                    }
                }
            }
        }
        // log.info("【DemonMarket】Market price list loaded");
    }

    /**
     * 根据下标获取物品
     * @param index
     * @return
     */
    public static MarketItem getMarketItem(int index){
        if(index < 0 || index >= MARKET_LIST.size()){
            return null;
        }
        return MARKET_LIST.get(index);
    }

    /**
     * 格式化商品列表
     * @param marketItemList
     * @return
     */
    private static List<Map<String,String>> formatMarketItemList(List<MarketItem> marketItemList){
        List<Map<String,String>> formatList = new ArrayList<>();

        for (int i = 0 ; i < marketItemList.size();i ++){
            formatList.add(convertClassToMap(marketItemList.get(i)));
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
     * @param nbtInfo
     * @return
     */
    private static ItemStack getItem(String nbtInfo){
        return PluginUtil.getItemStackFromSaveNBTString(nbtInfo);
    }

    private static FileConfiguration getMarketConfig() {
        return ConfigReader.getConfigMap().get(ConfigFileNameEnum.MARKET_DB_NAME.getName());
    }
}
