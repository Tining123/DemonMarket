package com.tining.demonmarket.storage;

import com.tining.demonmarket.Main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogWriter {
    public static void appendToLog(String log) {
        if(!ConfigReader.getEnableTransactionLog()){
            return;
        }
        try {
            File logFile = new File(Main.getInstance().getDataFolder(), "log.txt");

            // 如果文件不存在，则创建文件
            if (!logFile.exists()) {
                logFile.createNewFile();
            }

            // 打开文件并追加日志内容
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                // 获取当前时间并格式化为
                SimpleDateFormat dateFormat = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss.SSS]");
                String timestamp = dateFormat.format(new Date());

                writer.write(timestamp + log);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
