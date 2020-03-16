package com.jakub.example;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class CmdAndList implements Listener, CommandExecutor {
    @EventHandler
    public void onDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        User user = User.get(player.getName());
        user.increaseKills();
        JDatabase.updateStats(player.getName(), user.getKills(), user.getDeaths());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        User user = User.get(commandSender.getName());
        commandSender.sendMessage("Your kills: " + user.getKills());
        commandSender.sendMessage("Your deaths: " + user.getDeaths());
        return false;
    }
}
