package com.tining.demonmarket;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tining.demonmarket.command.AdminCommand;
import com.tining.demonmarket.command.UserCommand;
import com.tining.demonmarket.even.ChestGuiEvent;
import com.tining.demonmarket.money.Vault;
import com.tining.demonmarket.nms.JsonItemStack;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author tinga
 */
public class Main extends JavaPlugin {
    public static Main instance;
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

        saveDefaultConfig();


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
        //初始化NMS
        JsonItemStack.reloadNMS();

        Vault.vaultSetup();
        // <-- Replace with the id of your plugin!
        int pluginId = 14142;
        Metrics metrics = new Metrics(this, pluginId);

        registerEvent();
    }


    /**
     * 注册事件
     */
    public static void registerEvent() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new ChestGuiEvent(), Main.getInstance());
    }

    /**
     * 获取线程池资源
     * @return
     */
    public static ExecutorService getExecutor() {
        return executor;
    }

    public static Main getInstance() {
        return instance;
    }

}