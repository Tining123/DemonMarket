package com.tining.demonmarket.common.util;

import com.tining.demonmarket.Main;
import com.tining.demonmarket.storage.ConfigReader;
import com.tining.demonmarket.storage.ConfigFileNameEnum;
import org.apache.commons.collections.CollectionUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author tinga
 */
public class WorthUtil {

    private static Map<String, Double> nbtWorth;

    private static Map<String, Double> worth;

    /**
     * 获取物品价值
     *
     * @param is 物品
     * @return
     */
    public static double getItemWorth(ItemStack is) {
        Material material = is.getType();
        //先检测nbtworth
        Map<String, Double> nbtWorth = getNBTWorth();
        String name = PluginUtil.getKeyName(is);

        if (nbtWorth.containsKey(name)) {
            return nbtWorth.get(name);
        }
        //检测普通worth
        Map<String, Double> worth = getWorth();
        if (worth.containsKey(material.name())) {
            return worth.get(material.name());
        }
        return 0;
    }


    /**
     * 获取NBT物品价值
     *
     * @param is 物品
     * @return
     */
    public static double getItemWorthWithoutNBT(ItemStack is) {
        Material material = is.getType();
        //先检测nbtworth
        //检测普通worth
        Map<String, Double> worth = getWorth();
        if (worth.containsKey(material.name())) {
            return worth.get(material.name());
        }
        return 0;
    }

    /**
     * 获取NBT物品价值
     *
     * @param is 物品
     * @return
     */
    public static double getItemWorthWithNBT(ItemStack is) {
        Material material = is.getType();
        //先检测nbtworth
        Map<String, Double> nbtWorth = getNBTWorth();
        String name = PluginUtil.getKeyName(is);

        if (nbtWorth.containsKey(name)) {
            return nbtWorth.get(name);
        }
        return 0;
    }

    /**
     * 判断是否存在worth当中
     *
     * @param is 物品类型
     * @return
     */
    public static boolean isWorthContain(ItemStack is) {
        Material material = is.getType();
        //先检测nbtworth
        Map<String, Double> nbtWorth = getNBTWorth();
        String name = PluginUtil.getKeyName(is);
        if (nbtWorth.containsKey(name)) {
            return true;
        }
        //检测普通worth
        Map<String, Double> worth = getWorth();
        if (worth.containsKey(material.name())) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否存在worth当中
     *
     * @param is 物品类型
     * @return
     */
    public static boolean isWorthNameContain(ItemStack is) {
        //先检测nbtworth
        Material material = is.getType();
        //检测普通worth
        Map<String, Double> worth = getWorth();
        if (worth.containsKey(material.name())) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否存在worthNBT当中
     *
     * @param is 物品类型
     * @return
     */
    public static boolean isWorthNBTContain(ItemStack is) {

        Map<String, Double> nbtWorth = getNBTWorth();
        String name = PluginUtil.getKeyName(is);
        if (nbtWorth.containsKey(name)) {
            return true;
        }
        return false;
    }

    /**
     * 获取价值表
     * @return
     */
    public static Map<String, Double> getWorth() {
        if(Objects.isNull(worth)){
            worth = loadWorth();
        }
        return worth;
    }

    /**
     * 重载价值表
     */
    public static void reloadWorth(){
        worth = loadWorth();
    }


    /**
     * 获取物品总价值表
     * @return 物品总价值表
     */
    public static Map<String, Double> loadWorth() {
        FileConfiguration config = getWorthConfig();
        Map<String, Double> value = new HashMap<>();
        Map<String, Object> data = config.getConfigurationSection("worth").getValues(false);
        for (String obj : data.keySet()) {
            value.put(obj, Double.parseDouble(data.get(obj).toString()));
        }
        return value;
    }

    /**
     * 获取NBT价值表
     * @return
     */
    public static Map<String, Double> getNBTWorth() {
        if(Objects.isNull(nbtWorth)){
            nbtWorth = loadNBTWorth();
        }
        return nbtWorth;
    }

    /**
     * 重载NBT价值表
     */
    public static void reloadNBTWorth(){
        nbtWorth = loadNBTWorth();
    }

    /**
     * 获取NBT物品总价值表
     * @return NBT物品总价值表
     */
    public static Map<String, Double> loadNBTWorth() {
        FileConfiguration config = getNBTWorthConfig();
        Map<String, Double> value = new HashMap<>();
        if (Objects.isNull(config.getConfigurationSection("nbtworth"))) {
            config.addDefault("nbtworth", value);
            config.set("nbtworth", value);
            ConfigReader.saveConfig(ConfigFileNameEnum.NBT_WORTH_FILE_NAME.getName(),config);
        }
        if (!Objects.isNull(config.getConfigurationSection("nbtworth"))) {
            Map<String, Object> data = config.getConfigurationSection("nbtworth").getValues(false);
            for (String obj : data.keySet()) {
                value.put(obj, Double.parseDouble(data.get(obj).toString()));
            }
        }
        return value;
    }

    /**
     * 添加nbt价值到配置文件
     */
    public static void addToNBTWorth(String name, double value) {

        FileConfiguration config = getNBTWorthConfig();

        Map<String, Double> worth = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        if (!Objects.isNull(config.getConfigurationSection("nbtworth"))) {
            data = config.getConfigurationSection("nbtworth").getValues(false);
        }
        for (String obj : data.keySet()) {
            worth.put(obj, Double.parseDouble(data.get(obj).toString()));
        }
        worth.put(name, value);
        config.addDefault("nbtworth", worth);
        config.set("nbtworth", worth);

        ConfigReader.saveConfig(ConfigFileNameEnum.NBT_WORTH_FILE_NAME.getName(),config);
    }

    /**
     * 添加nbt价值到配置文件
     */
    public static void addToWorth(String name, double value) {

        FileConfiguration config = getWorthConfig();

        Map<String, Double> worth = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        if (!Objects.isNull(config.getConfigurationSection("worth"))) {
            data = config.getConfigurationSection("worth").getValues(false);
        }
        for (String obj : data.keySet()) {
            worth.put(obj, Double.parseDouble(data.get(obj).toString()));
        }
        worth.put(name, value);
        config.addDefault("worth", worth);
        config.set("worth", worth);

        ConfigReader.saveConfig(ConfigFileNameEnum.WORTH_FILE_NAME.getName(),config);
    }

    /**
     * 获取worth配置文件
     *
     * @return worth配置文件
     */
    public static FileConfiguration getWorthConfig() {
        return ConfigReader.getConfigMap().get(ConfigFileNameEnum.WORTH_FILE_NAME.getName());
    }

    /**
     * 获取nbtworth配置文件
     *
     * @return nbtworth配置文件
     */
    public static FileConfiguration getNBTWorthConfig() {
        return ConfigReader.getConfigMap().get(ConfigFileNameEnum.NBT_WORTH_FILE_NAME.getName());
    }
}
