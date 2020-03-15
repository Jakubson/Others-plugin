package com.jakub.example;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDatabase {
    private static String databaseURL;
    private static final List<String> QUERY_QUEUE = new ArrayList<>();

    public static void updateBalance(String nickname, BigDecimal balance) {
        synchronized (QUERY_QUEUE) {
            QUERY_QUEUE.add("UPDATE `accounts` SET `balance` = " + balance + " WHERE `nickname` = '" + nickname + "'");
        }
    }

    public static void createBalance(String nickname) {
        synchronized (QUERY_QUEUE) {
            QUERY_QUEUE.add("INSERT INTO `accounts` VALUES ('" + nickname + "', 0)");
        }
    }

    public static void loadDatabaseURL() throws ClassNotFoundException {
        FileConfiguration fileConfiguration = JEconomy.getConfiguration();
        StringBuilder stringBuilder = new StringBuilder();

        if (fileConfiguration.getBoolean("database.sqlite")) {
            Class.forName("org.sqlite.JDBC");
            stringBuilder.append("jdbc:sqlite://");
            stringBuilder.append(JEconomy.getPluginFolder());
            stringBuilder.append("/database.db");
        } else {
            ConfigurationSection remote = fileConfiguration.getConfigurationSection("database.remote");
            stringBuilder.append("jdbc:mysql://");
            stringBuilder.append(remote.getString("host"));
            stringBuilder.append(":");
            stringBuilder.append(remote.getInt("port"));
            stringBuilder.append("/");
            stringBuilder.append(remote.getString("database"));
            stringBuilder.append("?user=");
            stringBuilder.append(remote.getString("user"));
            stringBuilder.append("&password=");
            stringBuilder.append(remote.getString("password"));
        }

        databaseURL = stringBuilder.toString();
    }

    public static void prepareDatabase() throws SQLException {
        Connection connection = DriverManager.getConnection(databaseURL);
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS `accounts` (`nickname` VARCHAR(16), `balance` INT(255), PRIMARY KEY (`nickname`))");
        ResultSet resultSet = statement.executeQuery("SELECT * FROM `accounts`");

        while (resultSet.next()) {
            String nickname = resultSet.getString("nickname");
            double balance = resultSet.getDouble("balance");
            //tutaj dodajesz pobrane dane od jakiegos cache'a
        }

        resultSet.close();
        statement.close();
        connection.close();
    }

    public static void runDatabaseService() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(JEconomy.getInstance(), JDatabase::executeQueryQueue, 200, 200);
    }

    public static void executeQueryQueue() {
        synchronized (QUERY_QUEUE) {
            if (QUERY_QUEUE.isEmpty()) {
                return;
            }

            try {
                Connection connection = DriverManager.getConnection(databaseURL);
                Statement statement = connection.createStatement();

                for (String query : QUERY_QUEUE) {
                    statement.executeUpdate(query);
                }

                statement.close();
                connection.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

            QUERY_QUEUE.clear();
        }
    }
}
