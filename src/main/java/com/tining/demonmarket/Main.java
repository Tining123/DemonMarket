package com.tining.demonmarket;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tining.demonmarket.command.AdminCommand;
import com.tining.demonmarket.command.UserCommand;
import com.tining.demonmarket.common.ref.Metrics;
import com.tining.demonmarket.common.ref.Updater;
import com.tining.demonmarket.event.*;
import com.tining.demonmarket.common.ref.Vault;
import com.tining.demonmarket.gui.MarketConfirmGui;
import com.tining.demonmarket.storage.ConfigReader;
import com.tining.demonmarket.storage.LangReader;
import com.tining.demonmarket.task.ChestDrawTask;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author tinga
 */
public class Main extends JavaPlugin {
    private static Main instance;
    private static final Logger log = Logger.getLogger("Minecraft");

    /**
     * 线程上线
     */
    private static final int THREADS = Runtime.getRuntime().availableProcessors() + 1;

    /**
     * 线程池
     */
    private static ExecutorService executor = new ThreadPoolExecutor(THREADS, THREADS, 3, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat("DemonMarket-thread-%d").build(),
            (r, executor) -> log.info("DemonMarket process throw exception!"));

    @Override
    public void onDisable() {
        Bukkit.getGlobalRegionScheduler().cancelTasks(this);
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    @Override
    public void onEnable() {
        instance = this;

        // 释放配置文件
        saveDefaultConfig();
        ConfigReader.initRelease();
        ConfigReader.reloadConfig();
        // 如果有设置强制预言，加载强制语言
        if (!Objects.isNull(ConfigReader.getLanguage()) && !StringUtils.isEmpty(ConfigReader.getLanguage())) {
            LangReader.setLanguage(ConfigReader.getLanguage());
        }
        LangReader.initRelease();
        LangReader.reloadLang();
        setExecutor();

        //初始化NMS
        // JsonItemStack.reloadNMS();

        Vault.vaultSetup();
        int pluginId = 14142;
        Metrics metrics = new Metrics(this, pluginId);

        // 批量注册时间
        registerEvent();

        Updater.checkUpdate();
        ConfigReader.getDisablePayList();

        //注册
        if(ConfigReader.getEnableAutoRefresh()){
            long interval = (long) ConfigReader.getAutoRefreshInterval();
            if(interval < 20){
                interval = 20L;
            }
            Bukkit.getGlobalRegionScheduler().runAtFixedRate(this,new ChestDrawTask()
                    ,1, interval);
        }
        // Bukkit.getScheduler().runTaskTimerAsynchronously(this, new ChestDrawTask(), 0L, 1L);

    }


    /**
     * 注册事件
     */
    public static void registerEvent() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new ChestGuiEvent(), Main.getInstance());
        pm.registerEvents(new AcquireListGuiEvent(), Main.getInstance());
        pm.registerEvents(new UpdaterEvent(), Main.getInstance());
        pm.registerEvents(new CancelCommandEvent(), Main.getInstance());
        pm.registerEvents(new AdminShopGuiEvent(), Main.getInstance());
        pm.registerEvents(new ShopGuiEvent(), Main.getInstance());
        pm.registerEvents(new ShopConfirmGuiEvent(), Main.getInstance());
        pm.registerEvents(new MarketGuiEvent(), Main.getInstance());
        pm.registerEvents(new MarketConfirmGuiEvent(), Main.getInstance());

        pm.registerEvents(new AdminMarketGuiEvent(), Main.getInstance());
        pm.registerEvents(new AdminMarketConfirmGuiEvent(), Main.getInstance());
        pm.registerEvents(new PanelGuiEvent(), Main.getInstance());
        pm.registerEvents(new AdminGroupListGuiEvent(), Main.getInstance());
        pm.registerEvents(new AdminGroupSignSetGuiEvent(), Main.getInstance());
    }

    public static void setExecutor() {
        if (Bukkit.getPluginCommand("mt") != null) {
            Bukkit.getPluginCommand("mt").setExecutor(new UserCommand());
        }
        if (Bukkit.getPluginCommand("demonmarket") != null) {
            Bukkit.getPluginCommand("demonmarket").setExecutor(new UserCommand());
        }
        if (Bukkit.getPluginCommand("mtadmin") != null) {
            Bukkit.getPluginCommand("mtadmin").setExecutor(new AdminCommand());
        }
        if (Bukkit.getPluginCommand("demonmarketadmin") != null) {
            Bukkit.getPluginCommand("demonmarketadmin").setExecutor(new AdminCommand());
        }
    }

    /**
     * 获取线程池资源
     *
     * @return
     */
    public static ExecutorService getExecutor() {
        return executor;
    }

    public static Main getInstance() {
        return instance;
    }

}