package com.tining.demonmarket.storage;

import com.tining.demonmarket.Main;
import com.tining.demonmarket.common.util.MarketUtil;
import com.tining.demonmarket.common.util.ShopUtil;
import com.tining.demonmarket.common.util.WorthUtil;
import com.tining.demonmarket.storage.bean.ShopItem;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

/**
 * 配置文件管理
 */
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
     * 主配置文件
     */
    private static FileConfiguration config = Main.getInstance().getConfig();

    /**
     * 表配置文件
     */
    private static final Map<String, FileConfiguration> configMap = new HashMap<>();

    /**
     * 返回配置表，不包含config主配置
     *
     * @return
     */
    public static Map<String, FileConfiguration> getConfigMap() {
        return configMap;
    }

    /**
     * 重载所有配置文件
     */
    public static void reloadConfig() {
        //重载主配置文件
        Main.getInstance().reloadConfig();
        config = Main.getInstance().getConfig();
        //加载语言文件
        try {
            LangReader.reloadLang();
        }catch (Exception ignore){}
        //重载配置表中的文件到内存缓存中
        for (ConfigFileNameEnum w : ConfigFileNameEnum.values()) {
            String configName = w.getName();
            FileConfiguration configuration = YamlConfiguration.loadConfiguration(new File(ROOT_FOLDER, configName));
            configMap.put(configName, configuration);
        }
        // 重载价格
        WorthUtil.reloadWorth();
        WorthUtil.reloadNBTWorth();
        // 重载商店
        ShopUtil.reloadShop();
        // 重载市场
        MarketUtil.reloadMarket();
    }

    /**
     * 保存并重载插件
     */
    public static void saveConfig(String fileName, FileConfiguration fileConfiguration) {
        //重载配置表中的文件
        try {
            fileConfiguration.save(new File(ROOT_FOLDER, fileName));
        } catch (Exception e) {
            main.getLogger().info(e.toString());
        }
    }

    /**
     * 保存并重载插件
     */
    public static void saveConfig() {
        Main.getInstance().saveConfig();
        //重载配置表中的文件
        for (ConfigFileNameEnum w : ConfigFileNameEnum.values()) {
            String configName = w.getName();
            if (!Objects.isNull(configMap.get(configName))) {
                try {
                    configMap.get(configName).save(configName);
                } catch (Exception e) {
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
        for (ConfigFileNameEnum w : ConfigFileNameEnum.values()) {
            String configName = w.getName();
            try {
                File configFile = new File(ROOT_FOLDER, configName);
                if (!configFile.exists()) {
                    main.saveResource(configName, false);
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

    /**
     * 获取语言设定
     *
     * @return
     */
    public static String getLanguage() {
        return ConfigReader.config.getString("lang");
    }

    /**
     * 获取是否进行版本检查
     *
     * @return
     */
    public static Boolean getVersionCheck() {
        return ConfigReader.config.getBoolean("version-check");
    }

    /**
     * 获取是否要阻止pay命令
     * @return
     */
    public static boolean getDisablePay() {
        return ConfigReader.config.getBoolean("disable-pay");
    }

    /**
     * 获取是否要阻止的pay命令
     * @return
     */
    public static List<String> getDisablePayList() {
        List<String> commands = new ArrayList<>();
        Map<String, Object> data = new HashMap<>();
        if (!Objects.isNull(config.getStringList("disable-pay-list"))) {
            return config.getStringList("disable-pay-list");
        }
        return commands;
    }

    /**
     * 获取是否要阻止pay命令
     * @return
     */
    public static boolean getDisableSell() {
        return ConfigReader.config.getBoolean("disable-sell");
    }

    /**
     * 获取是否要阻止的pay命令
     * @return
     */
    public static List<String> getDisableSellList() {
        List<String> commands = new ArrayList<>();
        Map<String, Object> data = new HashMap<>();
        if (!Objects.isNull(config.getStringList("disable-sell-list"))) {
            return config.getStringList("disable-sell-list");
        }
        return commands;
    }

    /**
     * 获取支付限额
     * @return
     */
    public static double getMaxPay(){
        return ConfigReader.config.getDouble("max-pay");
    }


    /**
     * 支付单元金额
     * @return
     */
    public static double getPayUnit(){
        return ConfigReader.config.getDouble("pay-unit");
    }

    /**
     * 获取是否付款人支付税金
     * @return
     */
    public static boolean getPayerTax(){return ConfigReader.config.getBoolean("payer-tax");}

    /**
     * 获取是否自动刷新
     * @return
     */
    public static boolean getEnableAutoRefresh(){return ConfigReader.config.getBoolean("auto-refresh");}

    /**
     * 自动刷新间隔
     * @return
     */
    public static int getAutoRefreshInterval(){return ConfigReader.config.getInt("auto-refresh-gap") * 20;}

    /**
     * 获取是否禁用商品
     * @return
     */
    public static boolean getDisableShop(){return ConfigReader.config.getBoolean("disable-shop");}


    /**
     * 获取是否启用恶魔税率
     * @return
     */
    public static boolean getEnableDemonTax(){return ConfigReader.config.getBoolean("enable-demon-tax");}

    /**
     * 获取用户最大上架数量
     * @return
     */
    public static int getMaxUserSell(){

        if(ConfigReader.config.contains("market.enable-demon-tax")) {
            return ConfigReader.config.getInt("market.enable-demon-tax");
        }else{
            return 30;
        }
    }

    /**
     * 获取是否启用市场
     * @return
     */
    public static boolean getDisableMarket(){return ConfigReader.config.getBoolean("market.disable-market");}

    /**
     * 获取卖家税
     * @return
     */
    public static double getSellTaxRate(){return ConfigReader.config.getDouble("market.sell-tax-rate");}

    /**
     * 获取是否禁用上架物品的恶魔税
     * @return
     */
    public static boolean getDisableMarketDemonMarket(){return ConfigReader.config.getBoolean("market.diasble-demon-tax");}

}