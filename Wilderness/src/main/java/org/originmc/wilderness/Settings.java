package org.originmc.wilderness;

import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class Settings {

    private final Wilderness plugin;

    Settings(Wilderness plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        Configuration defaults = plugin.getConfig().getDefaults();
        defaults.set("denied-blocks", new ArrayList<>());
    }

    public int getConfigVersion() {
        return plugin.getConfig().getInt("config-version", 0);
    }

    public int getLatestConfigVersion() {
        return plugin.getConfig().getDefaults().getInt("config-version", 0);
    }

    public boolean isOutdated() {
        return getConfigVersion() < getLatestConfigVersion();
    }

    public int getAttempts() {
        return plugin.getConfig().getInt("attempts", 10);
    }

    public boolean useFactions() {
        return plugin.getConfig().getBoolean("factions");
    }

    public String getDefaultWorld() {
        return plugin.getConfig().getString("default-world", "world");
    }

    public String getSuccessMessage() {
        String message = plugin.getConfig().getString("success-message", null);
        return message != null ? ChatColor.translateAlternateColorCodes('&', message) : null;
    }

    public String getFailedMessage() {
        String message = plugin.getConfig().getString("failed-message", null);
        return message != null ? ChatColor.translateAlternateColorCodes('&', message) : null;
    }

    public List<String> getDeniedBlocks() {
        return plugin.getConfig().getStringList("denied-blocks");
    }

    public Set<String> getWorlds() {
        return plugin.getConfig().getConfigurationSection("worlds").getKeys(false);
    }

    public int getCenterX(String world) {
        return plugin.getConfig().getInt("worlds." + world + ".center-x");
    }

    public int getCenterZ(String world) {
        return plugin.getConfig().getInt("worlds." + world + ".center-z");
    }

    public int getRange(String world) {
        return plugin.getConfig().getInt("worlds." + world + ".range");
    }

}
