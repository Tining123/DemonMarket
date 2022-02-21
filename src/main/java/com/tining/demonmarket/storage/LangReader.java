package com.tining.demonmarket.storage;

import com.tining.demonmarket.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.HashMap;
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
    private static final String LOCALE = LangEnum.ENGLISH.getLanguage(Locale.getDefault());

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
            File configFile = new File(ROOT_FOLDER, configName);
            if (!configFile.exists()) {
                MAIN.saveResource(SUB_FOLDER_NAME + "/" + configName, false);
            }
        } catch (Exception e) {
            MAIN.getLogger().info(e.toString());
        }
    }

}
