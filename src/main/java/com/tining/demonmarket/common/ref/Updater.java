package com.tining.demonmarket.common.ref;

import com.tining.demonmarket.Main;
import com.tining.demonmarket.common.util.LangUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.xml.transform.dom.DOMSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 更新检测
 */
public class Updater {
    private static boolean foundANewVersion = false;
    private static String newVersion;
    private static String link;
    private static String description;
    private static Date date = new Date();
    private static boolean checkDone = false;

    /**
     * 更新信息表
     */
    private static final HashMap<String,String> updateInfo = new HashMap<>();


    private static String info;

    /**
     * 开始进行版本检查
     */
    public static void initialize() {
        try {
            URL url = new URL("https://tining123.github.io/demonmarket.io/");
            String context = "";
            try (Reader reader = new InputStreamReader(url.openStream(), "UTF-8")) {
                BufferedReader htmlReader = new BufferedReader(reader);
                //跳过
                String text = htmlReader.readLine();
                while (!Objects.isNull(text) && !text.contains("Welcome to GitHub Pages")) {
                    text = htmlReader.readLine();
                }
                while (!Objects.isNull(text)) {
                    text = htmlReader.readLine();
                    if (!Objects.isNull(text) && text.contains("Welcome to GitHub Pages")) {
                        break;
                    }
                    if (!StringUtils.isEmpty(text)) {
                        context += text + "\n";
                    }
                }
                context = extractInfo(context);
                //System.out.println(context);
            } catch (IOException ex) {
                //Bukkit.getConsoleSender().sendMessage("Updater.Error");
            }
        } catch (MalformedURLException ex) {
            //Bukkit.getConsoleSender().sendMessage("Updater.Error");
        }
        date = new Date();
    }

    /**
     * 提取更新信息
     *
     * @param text
     * @return
     */
    public static String extractInfo(String text) {
        text = text.replace("<p>", "").replace("</p>", "");
        text = text.replace("”", "").replace("“", "").replace("\"", "");
        text = text.replace("&amp;","&");
        String[] lines = text.split("\n");
        for(String s : lines){
            //设置版本
            if(s.startsWith("latest-version")){
                newVersion = s.split(":")[1].trim();
            }else if(s.startsWith("link")){
                link = s.replace("lin:","").trim();
            }else{
                updateInfo.put(s.split(":")[0].trim(),s.split(":")[1].trim());
            }
        }
        if(!StringUtils.isEmpty(newVersion) && !StringUtils.isEmpty(link) && updateInfo.size() != 0){
            checkDone = true;
        }
        Main.getInstance().getLogger().info("Update check done");
        return text;
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
     *
     * @return
     */
    public static boolean isFoundANewVersion() {
        return foundANewVersion;
    }

    /**
     * Get new version.
     *
     * @return
     */
    public static String getNewVersion() {
        return newVersion;
    }

    /**
     * Get download link.
     *
     * @return
     */
    public static String getLink() {
        return link;
    }

    /**
     * Get new version's update description.
     *
     * @return
     */
    public static String getDescription(String local) {
        return updateInfo.get(local);
    }

    /**
     * 更新检查是否完成
     *
     * @return
     */
    public static boolean isUpdateCheckDone() {
        return checkDone;
    }

    /**
     * Get the time of last check update.
     *
     * @return
     */
    public static Date getTimeOfLastCheckUpdate() {
        return date;
    }
}
