package com.tining.demonmarket.storage;

import com.tining.demonmarket.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 语言加载文件
 *
 * @author tinga
 */
public class LangReader {

    /**
     * 本地区域名称
     */
    private static String LOCALE = LangEnum.ENGLISH.getLanguage(Locale.getDefault());

    /**
     * main函数实体
     */
    private static final Main MAIN = Main.getInstance();

    /**
     * 子目录
     */
    private static final String SUB_FOLDER_NAME = "lang";

    /**
     * 文件目录
     */
    private static final File ROOT_FOLDER = new File(MAIN.getDataFolder(), SUB_FOLDER_NAME);

    /**
     * 翻译词典
     */
    private static final Map<String, String> DICTIONARY = new HashMap<>();

    /**
     * 初次释放配置文件
     */
    public static void initRelease() {
        String configName = LOCALE + ".yml";
        if (!ROOT_FOLDER.exists()) {
            ROOT_FOLDER.mkdir();
        }
        try {
            MAIN.saveResource(SUB_FOLDER_NAME + "/" + configName, true);
        } catch (Exception e) {
            MAIN.getLogger().info(e.toString());
        }
    }

    /**
     * 重载语言
     */
    public static void reloadLang(){
        String configName = LOCALE + ".yml";
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(new File(ROOT_FOLDER,configName));
        Map<String, Object> map = configuration.getConfigurationSection("lang").getValues(false);

        map.entrySet().stream().forEach((e)->{
            DICTIONARY.put(e.getKey(),(String)e.getValue());
        });
    }

    /**
     * 强制设定预言
     */
    public static void setLanguage(String language){
        LOCALE = LangEnum.ENGLISH.getLanguage(language);
    }

    /**
     * 获取字典
     * @return
     */
    public static Map<String,String> getDictionary(){
        return DICTIONARY;
    }

}
