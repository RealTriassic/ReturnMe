package com.triassic.returnme.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.triassic.returnme.userstorage.UserStorageManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import java.util.List;
import java.util.Objects;

public class AuthMePluginMessageListener implements Listener {
    private final Configuration config;
    private final UserStorageManager storage;
    private final Boolean isPermissionRequired;
    private final List<String> exemptPlayers;

    public AuthMePluginMessageListener(Configuration config, UserStorageManager storage) {
        this.config = config;
        this.storage = storage;
        isPermissionRequired = config.getBoolean("require-permission");
        exemptPlayers = config.getStringList("exempted-players");
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.isCancelled() || !event.getTag().equals("BungeeCord") || !(event.getSender() instanceof Server)) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());

        if (!in.readUTF().equals("Forward")) {
            return;
        }

        in.readUTF();

        if (!in.readUTF().equals("AuthMe.v2.Broadcast")) {
            return;
        }

        short dataLength = in.readShort();
        byte[] dataBytes = new byte[dataLength];
        in.readFully(dataBytes);

        ByteArrayDataInput dataIn = ByteStreams.newDataInput(dataBytes);
        String type = dataIn.readUTF();

        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(dataIn.readUTF());

        if (type.equals("login")) {
            if (isPermissionRequired && !player.hasPermission("returnme.use")) {
                return;
            }

            if (exemptPlayers.contains(player.getName())) {
                return;
            }

            handleLoginEvent(player);
        }
    }

    private void handleLoginEvent(ProxiedPlayer player) {
        if (!(player == null)) {
            ServerInfo lastServer = storage.getLastLocation(player);
            String defaultServer = config.getString("default-server");
            String loginServer = config.getString("login-server");

            if (Objects.equals(player.getServer().getInfo().getName(), loginServer)) {
                if (lastServer == null) {
                    player.connect(ProxyServer.getInstance().getServerInfo(defaultServer));
                    return;
                }

                lastServer.ping((result, error) -> {
                    if (error == null) {
                        player.connect(lastServer);
                    } else {
                        player.connect(ProxyServer.getInstance().getServerInfo(defaultServer));
                    }
                });
            }
        }
    }
}
