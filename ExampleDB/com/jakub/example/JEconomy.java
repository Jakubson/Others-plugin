package com.jakub.example;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class JEconomy extends JavaPlugin {
    private static JEconomy instance;

    public static FileConfiguration getConfiguration() {
        return instance.getConfig();
    }

    public static String getPluginFolder() {
        return instance.getDataFolder().getAbsolutePath();
    }

    public static JEconomy getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        try {
            saveDefaultConfig();
            getConfig().options().copyDefaults(true);

            JDatabase.loadDatabaseURL();
            JDatabase.prepareDatabase();
            JDatabase.runDatabaseService();
        } catch (SQLException | ClassNotFoundException exception) {
            exception.printStackTrace();
            Bukkit.shutdown();
            return;
        }
    }

    @Override
    public void onDisable() {
        JDatabase.executeQueryQueue();
    }
}