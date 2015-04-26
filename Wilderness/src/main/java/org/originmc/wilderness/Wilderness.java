package org.originmc.wilderness;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.originmc.wilderness.factions.api.FactionsHelper;
import org.originmc.wilderness.factions.api.FactionsHelperImpl;

import java.util.Random;

public final class Wilderness extends JavaPlugin {

    private FactionsManager factionsManager;

    private Random random = new Random();

    private Settings settings;

    public FactionsManager getFactionsManager() {
        return factionsManager;
    }

    public Settings getSettings() {
        return settings;
    }

    @Override
    public void onEnable() {
        // Load settings
        saveDefaultConfig();

        settings = new Settings(this);
        if (settings.isOutdated()) {
            getLogger().warning("**WARNING**");
            getLogger().warning("Your configuration file is outdated.");
            getLogger().warning("Backup your old file and then delete it to generate a new copy.");
        }

        // Load factions support
        integrateFactions();

        getLogger().info(getName() + " has been enabled!");
    }

    private void integrateFactions() {
        // Use a dummy implementation if Factions is disabled
        if (!getSettings().useFactions()) {
            factionsManager = new FactionsManager(new FactionsHelperImpl());
            return;
        }

        // Determine if Factions is loaded
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Factions");
        if (plugin == null) {
            getLogger().info("Factions integration is disabled because it is not loaded.");

            // Use the dummy helper implementation if Factions isn't loaded
            factionsManager = new FactionsManager(new FactionsHelperImpl());
            return;
        }

        // Determine which helper class implementation to use
        FactionsHelper helper;
        String[] v = plugin.getDescription().getVersion().split("\\.");
        String version = v[0] + "_" + v[1];
        String className = "org.originmc.wilderness.factions.v" + version + ".FactionsHelperImpl";

        try {
            // Try to create a new helper instance
            helper = (FactionsHelper) Class.forName(className).newInstance();

            // Create the manager which is what the plugin will interact with
            factionsManager = new FactionsManager(helper);
        } catch (Exception e) {
            // Something went wrong, chances are it's a newer, incompatible WorldGuard
            getLogger().warning("**WARNING**");
            getLogger().warning("Failed to enable Factions integration due to errors.");
            getLogger().warning("This is most likely due to a newer Factions.");

            // Use the dummy helper implementation since WG isn't supported
            factionsManager = new FactionsManager(new FactionsHelperImpl());

            // Let's leave a stack trace in console for reporting
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equals("wildreload")) {
            reloadConfig();
            getSettings().load();
            sender.sendMessage(ChatColor.GREEN + getName() + " config reloaded.");
            return true;
        }

        // Do nothing if sender is console
        if (!(sender instanceof Player)) {
            sender.sendMessage("Console is unable to execute the wilderness command.");
            return true;
        }

        // Set the destination world
        Player player = (Player) sender;
        World world = player.getWorld();
        if (!getSettings().getWorlds().contains(world.getName())) {
            world = Bukkit.getWorld(getSettings().getDefaultWorld());
        }

        // Do nothing if the world is null
        if (world == null) return true;

        // Get worlds settings
        int centerX = getSettings().getCenterX(world.getName());
        int centerZ = getSettings().getCenterZ(world.getName());
        int range = getSettings().getRange(world.getName());

        // Attempt to find a safe teleportation location
        for (int counter = 0; counter < getSettings().getAttempts(); counter++) {
            // Generate random set of coordinates
            int x = centerX + (random.nextInt(range * 2) - range);
            int z = centerZ + (random.nextInt(range * 2) - range);
            int y = world.getHighestBlockYAt(x, z);

            // Find a different location if the destination is not safe
            Block block = world.getBlockAt(x, y - 1, z);
            if (getSettings().getDeniedBlocks().contains(block.getType().toString())) continue;

            // Find a different location if the destination is inside claimed factions territory
            if (getSettings().useFactions() && getFactionsManager().isInTerritory(block.getLocation())) continue;

            // Teleport player to the destination
            player.teleport(new Location(world, x + 0.5D, y, z + 0.5D));

            // Tell the player the teleportation was a success
            if (getSettings().getSuccessMessage() != null && !getSettings().getSuccessMessage().isEmpty()) {
                player.sendMessage(getSettings().getSuccessMessage()
                        .replace("{NAME}", player.getName())
                        .replace("{X}", "" + x)
                        .replace("{Y}", "" + y)
                        .replace("{Z}", "" + z)
                        .replace("{WORLD}", world.getName()));
            }
            return true;
        }

        // Tell player the teleportation has failed
        if (getSettings().getFailedMessage() != null && !getSettings().getFailedMessage().isEmpty()) {
            player.sendMessage(getSettings().getFailedMessage().replace("{ATTEMPTS}", "" + getSettings().getAttempts()));
        }
        return true;
    }

}
