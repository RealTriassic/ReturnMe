package com.triassic.returnme;

import com.google.common.io.ByteStreams;
import com.triassic.returnme.listeners.AuthMePluginMessageListener;
import com.triassic.returnme.listeners.PlayerServerChangeListener;
import com.triassic.returnme.userstorage.UserStorageManager;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.logging.Logger;

public final class ReturnMe extends Plugin {
    private static ReturnMe plugin;

    @Override
    public void onEnable() {
        plugin = this;
        Logger logger = getLogger();
        this.saveDefaultConfig();

        // Load Configuration
        Configuration config;
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Initialize bStats metrics
        final boolean metricsEnabled = config.getBoolean("toggle-metrics", true);

        if (metricsEnabled) {
            new Metrics(this, 19332);
        }

        // Initialize the UserStorageManager
        UserStorageManager storage = new UserStorageManager(logger, config);

        // Start listeners
        getProxy().getPluginManager().registerListener(this, new AuthMePluginMessageListener(config, storage));
        getProxy().getPluginManager().registerListener(this, new PlayerServerChangeListener(storage));
    }

    public void saveDefaultConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                try (InputStream is = getResourceAsStream("config.yml");
                     OutputStream os = Files.newOutputStream(configFile.toPath())) {
                    ByteStreams.copy(is, os);
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to create configuration file", e);
            }
        }
    }

    public static ReturnMe getPlugin() {
        return plugin;
    }
}
