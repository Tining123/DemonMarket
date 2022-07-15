package com.tining.demonmarket;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tining.demonmarket.command.AdminCommand;
import com.tining.demonmarket.command.UserCommand;
import com.tining.demonmarket.common.ref.Metrics;
import com.tining.demonmarket.common.ref.Updater;
import com.tining.demonmarket.event.AcquireListGuiEvent;
import com.tining.demonmarket.event.CancelCommandEvent;
import com.tining.demonmarket.event.ChestGuiEvent;
import com.tining.demonmarket.common.ref.Vault;
import com.tining.demonmarket.common.ref.JsonItemStack;
import com.tining.demonmarket.event.UpdaterEvent;
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
        Bukkit.getScheduler().cancelTasks(this);
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    @Override
    public void onEnable() {
        instance = this;

        //释放配置文件
        saveDefaultConfig();
        ConfigReader.initRelease();
        ConfigReader.reloadConfig();
        //TODO: 如果有设置强制预言，加载强制语言
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

        registerEvent();

        Updater.checkUpdate();
        ConfigReader.getDisablePayList();

        //注册
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new ChestDrawTask(), 0L, 1L);

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