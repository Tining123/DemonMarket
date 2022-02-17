package com.tining.demonmarket.storage;

import com.tining.demonmarket.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public final class ConfigReader {

    public static FileConfiguration config = Main.instance.getConfig();

    public static String getMysqlConfig(String mysqlConfigTag) {
        return config.getString(mysqlConfigTag);
    }

    public static double getRate() {
        return config.getDouble("TaxRate");
    }

    public static int getTimeOut(String timeOutTag) {
        return config.getInt("TimeOut." + timeOutTag);
    }

    public static boolean getEnable(String enableTag) {
        return config.getBoolean("Enable." + enableTag);
    }

    public static void reloadConfig(){
        Main.instance.reloadConfig();
        config = Main.instance.getConfig();
        Main.instance.saveConfig();
        Main.instance.reloadConfig();
        config = Main.instance.getConfig();
    }


    /**
     * 获取是否开启屏蔽
     * @return
     */
    public static String getRoundSetting(){return config.getString("Round");}

    /**
     * 获取是否开启屏蔽
     * @return
     */
    public static String getFilterSetting(){return config.getString("Filter");}

    /**
     * 获取税率
     * @return
     */
    public static double getTaxRate() {
        return config.getDouble("TaxRate");
    }

    /**
     * 获取物品总价值
     * @return
     */
    public static Map<String, Double> getWorth(){

        Map<String, Double> value = new HashMap<>();
        Map<String, Object> data = config.getConfigurationSection("worth").getValues(false);
        for (String obj  : data.keySet()) {
            value.put(obj, Double.parseDouble(data.get(obj).toString()));
        }
        return value;
    }

    /**
     * 获取NBT物品总价值
     * @return
     */
    public static Map<String, Double> getNBTWorth(){

        Map<String, Double> value = new HashMap<>();
        if(Objects.isNull(config.getConfigurationSection("nbtworth"))){
            config.addDefault("nbtworth",value);
            config.set("nbtworth",value);
            reloadConfig();
        }
        if(!Objects.isNull(config.getConfigurationSection("nbtworth"))) {
            Map<String, Object> data = config.getConfigurationSection("nbtworth").getValues(false);
            for (String obj : data.keySet()) {
                value.put(obj, Double.parseDouble(data.get(obj).toString()));
            }
        }
        return value;
    }

    /**
     * 添加nbt价值到配置文件
     * @return
     */
    public static void addToNBTWorth(String name,double value){

        Map<String, Double> worth = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        if(!Objects.isNull(config.getConfigurationSection("nbtworth"))){
            data = config.getConfigurationSection("nbtworth").getValues(false);
        }
        for (String obj  : data.keySet()) {
            worth.put(obj, Double.parseDouble(data.get(obj).toString()));
        }
        worth.put(name,value);
        config.addDefault("nbtworth",worth);
        config.set("nbtworth",worth);
        Main.instance.saveConfig();
        Main.instance.reloadConfig();
        config = Main.instance.getConfig();
    }

    /**
     * 保存物品总价值
     * @return
     */
    public static void saveWorth(Map<String, Double> map){
        config.addDefault("worth",map);
        config.set("worth",map);
        Main.instance.saveConfig();
        Main.instance.reloadConfig();
        config = Main.instance.getConfig();
    }

}