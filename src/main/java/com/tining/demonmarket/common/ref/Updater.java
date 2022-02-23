package com.tining.demonmarket.common.ref;

import com.tining.demonmarket.Main;
import com.tining.demonmarket.common.util.LangUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Updater
{
    private static boolean foundANewVersion = false;
    private static String newVersion;
    private static String link;
    private static String description;
    private static Date date = new Date();

    private static String info;

    /**
     * Initialize programs.
     */
    public static void initialize() {
            try {
                URL url = new URL("https://raw.githubusercontent.com/Tining123/DemonMarket/main/src/main/resources/updater.yml");
                try (Reader reader = new InputStreamReader(url.openStream(), "UTF-8")) {
                    YamlConfiguration yaml = new YamlConfiguration();
                    yaml.load(reader);
                    String version = yaml.getString("latest-version");
                    String downloadLink = yaml.getString("link");
                    String description_ = "description.Default";
                    if (yaml.get("description." + LangUtil.getLang()) != null) {
                        description_ = yaml.getString("description." + LangUtil.getLang());
                    }
                    String nowVersion = Bukkit.getPluginManager().getPlugin("LiteSignIn").getDescription().getVersion();
                    if (!nowVersion.equalsIgnoreCase(version)) {
                        newVersion = version;
                        foundANewVersion = true;
                        link = downloadLink;
                        description = description_;
                        Main.getInstance().getLogger().info(description);
                        //MessageUtil.sendMessage(Bukkit.getConsoleSender(), "Updater.Checked");
                    }
                } catch (InvalidConfigurationException | IOException ex) {
                    Bukkit.getConsoleSender().sendMessage("Updater.Error");
                }
            } catch (MalformedURLException ex) {
                Bukkit.getConsoleSender().sendMessage("Updater.Error");
            }
            date = new Date();
    }
    
    /**
     * Start check updater.
     */
    public static void checkUpdate() {
        Main.getExecutor().execute(Updater::initialize);
        //checkUpdateThread.start();
    }
    
    /**
     * Return whether found a new version.
     * @return 
     */
    public static boolean isFoundANewVersion() {
        return foundANewVersion;
    }
    
    /**
     * Get new version.
     * @return 
     */
    public static String getNewVersion() {
        return newVersion;
    }
    
    /**
     * Get download link.
     * @return 
     */
    public static String getLink() {
        return link;
    }
    
    /**
     * Get new version's update description.
     * @return 
     */
    public static String getDescription() {
        return description;
    }
    
    /**
     * Get the time of last check update.
     * @return 
     */
    public static Date getTimeOfLastCheckUpdate() {
        return date;
    }
}
