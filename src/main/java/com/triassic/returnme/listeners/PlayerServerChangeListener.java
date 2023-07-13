package com.triassic.returnme.listeners;

import com.triassic.returnme.userstorage.UserStorageManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerServerChangeListener implements Listener {
    private final UserStorageManager storage;

    public PlayerServerChangeListener(UserStorageManager storage) {
        this.storage = storage;
    }

    @EventHandler
    public void onPlayerLeave(ServerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        storage.savePlayerLocation(player, player.getServer().getInfo().getName());
    }

    @EventHandler
    public void onPlayerServerSwitch(ServerSwitchEvent event) {
        ProxiedPlayer player = event.getPlayer();
        storage.savePlayerLocation(player, player.getServer().getInfo().getName());
    }
}
