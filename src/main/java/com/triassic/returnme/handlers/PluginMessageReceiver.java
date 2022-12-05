package com.triassic.returnme.handlers;

import com.triassic.returnme.storage.LocationData;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PluginMessageReceiver implements Listener {
    @EventHandler
    public void onMessageReceive(PluginMessageEvent event) {
        if (!event.getTag().equals("BungeeCord")) {
            return;
        }

        if (!(event.getSender() instanceof Server)) {
            return;
        }

        final ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());

        if(!input.readUTF().equals("Forward")) {
            return;
        }

        input.readUTF();

        if (!input.readUTF().equals("AuthMe.v2.Broadcast")) {
            return;
        }

        final short dataLength = input.readShort();
        final byte[] dataBytes = new byte[dataLength];
        input.readFully(dataBytes);
        final ByteArrayDataInput data = ByteStreams.newDataInput(dataBytes);

        final String type = data.readUTF();
        ProxiedPlayer receiver = (ProxiedPlayer) event.getReceiver();

        ServerInfo lastServer = ProxyServer.getInstance().getServerInfo(LocationData.getLastLocation((ProxiedPlayer) event.getReceiver()));
        ServerInfo loginServer = ProxyServer.getInstance().getServerInfo("login");
        ServerInfo spawnServer = ProxyServer.getInstance().getServerInfo("spawn");

        if ("login".equals(type)) {
            if (receiver.getServer().getInfo() == loginServer) {
                if (lastServer == null) {
                    if (receiver.hasPermission("group.player")) {
                        receiver.connect(spawnServer);
                    }
                } else if (!(receiver.hasPermission("returnme.bypass"))){
                    lastServer.ping((result, error) -> {
                        if (error == null) {
                            receiver.connect(lastServer);
                        } else {
                            receiver.connect(spawnServer);
                        }
                    });
                }
            }
        }
    }
}