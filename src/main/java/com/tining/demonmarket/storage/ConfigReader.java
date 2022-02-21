package com.tining.demonmarket.storage;

import com.google.common.collect.Lists;
import com.tining.demonmarket.Main;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class ConfigReader {


    /**
     * main函数实体
     */
    private static Main main = Main.getInstance();

    /**
     * 插件目录
     */
    private static final File ROOT_FOLDER = main.getDataFolder();

    /**
     * 直接复制的配置文件列表
     */
    private static final String[] COPY_FILE_LIST = new String[]{
            "nbtworth.yml"
            , "worth.yml"
    };

    /**
     * 主配置文件
     */
    private static FileConfiguration config = Main.getInstance().getConfig();

    /**
     * 表配置文件
     */
    private static Map<String,FileConfiguration> configMap = new HashMap<>();

    /**
     * 返回配置表，不包含config主配置
     * @return
     */
    public static Map<String,FileConfiguration> getConfigMap(){
        return configMap;
    }

    /**
     * 重载所有配置文件
     */
    public static void reloadConfig() {
        //重载主配置文件
        Main.getInstance().reloadConfig();
        config = Main.getInstance().getConfig();
        //重载配置表中的文件
        for(String configName : COPY_FILE_LIST){
            if(!Objects.isNull(configMap.get(configName))) {
                FileConfiguration configuration = YamlConfiguration.loadConfiguration(new File(configName));
                configMap.put(configName, configuration);
            }

        }
    }

    /**
     * 保存并重载插件
     */
    public static void saveConfig(){
        Main.getInstance().saveConfig();
        //重载配置表中的文件
        for(String configName : COPY_FILE_LIST){
            if(!Objects.isNull(configMap.get(configName))) {
                try {
                    configMap.get(configName).save(configName);
                }catch (Exception e){
                    Main.getInstance().getLogger().info(e.toString());
                }
            }
        }
        reloadConfig();
    }

    /**
     * 初次释放配置文件
     */
    public static void initRelease() {
        for (String fileName : COPY_FILE_LIST) {
            try {
                File configFile = new File(ROOT_FOLDER, fileName);
                if (!configFile.exists()) {
                    main.saveResource(fileName, false);
                }
            } catch (Exception e) {
                main.getLogger().info(e.toString());
            }
        }
    }

    /**
     * 获取op名称
     *
     * @return
     */
    public static String getOP() {
        return config.getString("OP");
    }

    /**
     * 获取是否开启小数点近似
     *
     * @return
     */
    public static String getRoundSetting() {
        return config.getString("Round");
    }

    /**
     * 获取是否开启屏蔽
     *
     * @return
     */
    public static String getFilterSetting() {
        return config.getString("Filter");
    }

    /**
     * 获取税率
     *
     * @return
     */
    public static double getTaxRate() {
        return config.getDouble("TaxRate");
    }

    /**
     * 获取资产基线
     *
     * @return
     */
    public static double getBasicProperty() {
        return ConfigReader.config.getDouble("BasicProperty");
    }


}