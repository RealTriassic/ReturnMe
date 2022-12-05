package com.triassic.returnme.storage;

import com.triassic.returnme.ReturnMe;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.util.Objects;

public class LocationData {
    static File locationsFolder = new File(ReturnMe.getPlugin().getDataFolder(), "locations");
    static Configuration data;
    static File locationsFile = null;

    public static void createData(ProxiedPlayer p, String s) {
        locationsFile = new File(locationsFolder, p.getUniqueId() + ".yml");

        if (!locationsFolder.exists()) {
            try {
                locationsFolder.mkdirs();
            } catch (Exception err) {
                ReturnMe.getPlugin().getLogger().severe("Failed to create locations folder");
            }
        }

        try {
            locationsFile.createNewFile();
            writeData(p, s);
        } catch (Exception err) {
            ReturnMe.getPlugin().getLogger().severe("Failed to create " + locationsFile.getName() + " for " + p.getName());
        }
    }

    public static void writeData(ProxiedPlayer p, String s) {
        locationsFile = new File(locationsFolder, p.getUniqueId() + ".yml");

        try {
            data = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(locationsFile.toURI()));
        } catch (Exception err) {
            ReturnMe.getPlugin().getLogger().severe("Failed to load " + locationsFile.getName() + " for " + p.getName());
        }

        ServerInfo loginServer = ProxyServer.getInstance().getServerInfo("login");

        if(!(Objects.equals(s, loginServer.getName())) && !(Objects.equals(s, getLastLocation(p)))) {
            data.set("server", s);
        }

        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(data, new File(locationsFile.toURI()));
        } catch (Exception err) {
            ReturnMe.getPlugin().getLogger().severe("Failed to save " + locationsFile.getName() + " for " + p.getName());
        }
    }

    public static String getLastLocation(ProxiedPlayer p) {
        locationsFile = new File(locationsFolder, p.getUniqueId() + ".yml");
        try {
            data = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(locationsFile.toURI()));
        } catch (Exception err) {
            ReturnMe.getPlugin().getLogger().severe("Failed to load " + locationsFile.getName() + " for " + p.getName());
        }

        String server;
        server = (String) data.get("server");

        return server;
    }

    public static boolean exists(ProxiedPlayer p) {
        locationsFile = new File(locationsFolder, p.getUniqueId() + ".yml");
        return locationsFolder.exists() && locationsFile.exists();
    }
}
