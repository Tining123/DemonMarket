package com.tining.demonmarket.storage;


import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;

import static org.bukkit.Bukkit.getLogger;

public class Mysql {


    private static Connection connection;
    private PreparedStatement statement;

    public boolean mysqlInit() {


        String JDBC_DRIVER = "org.sqlite.JDBC";


        // 连接参数的固定格式
        String DB_URL ="demonmarket.db";
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }



    public void prepareSql(String sql) {
        try {
            statement = getConnection().prepareStatement(sql);
        } catch (SQLException e) {
            getLogger().warning(ChatColor.RED + "An error in mysql occurred while preparing sql.");
            e.printStackTrace();
        }
    }

    public void setData(Integer number,String data) {
        try {
            statement.setString(number, data);
        } catch (SQLException e) {
            getLogger().warning(ChatColor.RED + "An error in mysql occurred while binding data for sql.");
            e.printStackTrace();
        }
    }

    public void execute() {
        try {
            statement.execute();
        } catch (SQLException e) {
            getLogger().warning(ChatColor.RED + "An error in mysql occurred while executing sql.");
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getResult() {
        try {
            return statement.executeQuery();
        } catch (SQLException e) {
            getLogger().warning(ChatColor.RED + "An error in mysql occurred while getting sql result.");
            e.printStackTrace();
            return null;
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || !connection.isValid(1000)) {
                mysqlInit();
            }
            return connection;

        } catch (SQLException e) {
            return null;
        }
    }
}

