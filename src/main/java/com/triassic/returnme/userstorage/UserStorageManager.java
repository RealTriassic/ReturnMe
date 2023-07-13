package com.triassic.returnme.userstorage;

import com.triassic.returnme.ReturnMe;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

public class UserStorageManager {
    private final Logger logger;
    private final ConfigurationProvider configProvider;
    private final Configuration config;

    public UserStorageManager(Logger logger, Configuration config) {
        this.logger = logger;
        this.config = config;
        this.configProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);
    }

    private static final File LOCATIONS_FOLDER = new File(ReturnMe.getPlugin().getDataFolder(), "locations");

    public void savePlayerLocation(ProxiedPlayer player, String serverName) {
        File playerFile = getPlayerFile(player);

        if (!playerFile.exists()) {
            if (!LOCATIONS_FOLDER.exists() && !LOCATIONS_FOLDER.mkdirs()) {
                logger.severe("Failed to create locations folder");
                return;
            }

            try {
                playerFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Failed to create " + playerFile.getName() + " for " + player.getName(), e);
            }
        }

        try {
            Configuration data = configProvider.load(playerFile);
            String loginServer = config.getString("login-server");

            if (!Objects.equals(serverName, loginServer) && !Objects.equals(serverName, data.getString("server"))) {
                data.set("server", serverName);
                configProvider.save(data, playerFile);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load or save " + playerFile.getName() + " for " + player.getName(), e);
        }
    }

    public ServerInfo getLastLocation(ProxiedPlayer player) {
        File playerFile = getPlayerFile(player);

        if (playerFile.exists()) {
            try {
                Configuration data = configProvider.load(playerFile);
                return ProxyServer.getInstance().getServerInfo(data.getString("server"));
            } catch (IOException e) {
                throw new RuntimeException("Failed to load " + playerFile.getName() + " for " + player.getName(), e);
            }
        }

        return null;
    }

    private File getPlayerFile(ProxiedPlayer player) {
        return new File(LOCATIONS_FOLDER, player.getUniqueId() + ".yml");
    }
}