package com.triassic.returnme;

import com.triassic.returnme.handlers.PlayerLeaveListener;
import com.triassic.returnme.handlers.PluginMessageReceiver;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public final class ReturnMe extends Plugin {
    private static ReturnMe plugin;

    @Override
    public void onEnable() {
        plugin = this;

        ProxyServer.getInstance().getPluginManager().registerListener(this, new PluginMessageReceiver());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerLeaveListener());

        getProxy().getLogger().info("ReturnMe was enabled.");
    }

    @Override
    public void onDisable() {
        getProxy().getLogger().info("ReturnMe was disabled.");
    }

    public static ReturnMe getPlugin() {
        return plugin;
    }
}
