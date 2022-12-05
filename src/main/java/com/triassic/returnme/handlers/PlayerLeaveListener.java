package com.triassic.returnme.handlers;

import com.triassic.returnme.storage.LocationData;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerLeaveListener implements Listener {
    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        if(!(event.getPlayer().getServer() == null)) {
            if (LocationData.exists(event.getPlayer())) {
                LocationData.writeData(event.getPlayer(), event.getPlayer().getServer().getInfo().getName());
            } else {
                LocationData.createData(event.getPlayer(), event.getPlayer().getServer().getInfo().getName());
            }
        }
    }

    @EventHandler
    public void onPlayerServerSwitch(ServerSwitchEvent event) {
        if(!(event.getPlayer().getServer() == null)) {
            if (LocationData.exists(event.getPlayer())) {
                LocationData.writeData(event.getPlayer(), event.getPlayer().getServer().getInfo().getName());
            } else {
                LocationData.createData(event.getPlayer(), event.getPlayer().getServer().getInfo().getName());
            }
        }
    }
}
