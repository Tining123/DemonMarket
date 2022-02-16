package com.tining.demonmarket.economy;

import com.tining.demonmarket.data.MarketItem;
import com.tining.demonmarket.storage.ConfigReader;
import com.tining.demonmarket.storage.Mysql;
import com.google.common.collect.Lists;
import org.bukkit.Material;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.bukkit.Bukkit.getLogger;

public class MarketData {
    /**
     * 所有市场物品
     */
    private static List<MarketItem> markeList;

    private static Map<String,String> worth;

    /**
     * 获取市场物品
     * @return 市场物品列表
     */
    public static List<MarketItem> getMarketItem() {
        return Lists.newArrayList(getAllMarketItems());
    }


    /**
     * 获取所有市场物品
     * @return
     */
    private static List<MarketItem> getAllMarketItems(){
        if(!Objects.isNull(markeList) && !markeList.isEmpty()){
            return markeList;
        }

        Mysql m = new Mysql();

        List<MarketItem> list = new ArrayList<>();


        m.prepareSql("SELECT * FROM market_item_data");
        m.execute();
        ResultSet resultSet = m.getResult();

        try {
            while (resultSet.next()) {
                MarketItem marketItem = new MarketItem();
                marketItem.item = Material.matchMaterial(resultSet.getString("item_name"));
                marketItem.x = resultSet.getInt("x");
                marketItem.k = resultSet.getInt("k");
                marketItem.b = resultSet.getInt("b");
                if (marketItem.item != null) {
                    list.add(marketItem);
                } else {
                    getLogger().warning(String.format("[DemonMarket]We found an illegal item in your database which names '%s'. Please check if it's a bug.", resultSet.getString("item_name")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        m.close();
        markeList = list;
        return list;
    }


    /**
     * 获取市场物品
     * 如果找不到则返回空
     * @param item 玩家发送的物品
     * @return 市场物品，如果找不到则返回空
     */
    public static MarketItem getMarketItem(Material item) {
        List<MarketItem> marketItems = MarketData.getAllMarketItems();
        for(MarketItem mi : marketItems){
            if(mi.item.name().equals(item.name())){
                return mi;
            }
        }
        return  null;
    }

    public static void putMarketItem(MarketItem item) {
        Mysql m = new Mysql();
        m.prepareSql("INSERT INTO market_item_data (item_name, x, k, b) values (?, ?, ?, ?)");
        m.setData(1, item.item.name());
        m.setData(2, String.valueOf(item.x));
        m.setData(3, String.valueOf(item.k));
        m.setData(4, String.valueOf(item.b));
        m.execute();
        m.close();
    }

    public static void updateMarketItem(MarketItem item) {
        Mysql m = new Mysql();
        m.prepareSql("UPDATE market_item_data SET x = ?, b = ?, k = ? WHERE item_name = ?");
        m.setData(1, String.valueOf(item.x));
        m.setData(2, String.valueOf(item.b));
        m.setData(3, String.valueOf(item.k));
        m.setData(4, item.item.name());
        m.execute();
        m.close();
    }

    public static void removeMarketItem(MarketItem item) {
        Mysql m = new Mysql();
        m.prepareSql("DELETE FROM market_item_data WHERE item_name = ?");
        m.setData(1, item.item.name());
        m.execute();
        m.close();
    }

    public static void removeMarketItem(String itemName) {
        Mysql m = new Mysql();
        m.prepareSql("DELETE FROM market_item_data WHERE item_name = ?");
        m.setData(1, itemName);
        m.execute();
        m.close();
    }
}
