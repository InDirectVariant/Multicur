package ca.variantlabs.multicur;

import ca.variantlabs.multicur.commands.CurrencyAdminCommand;
import ca.variantlabs.multicur.commands.CurrencyCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Multicur extends JavaPlugin {

    @Override
    public void onEnable() {

        //Checks for plugin folder
        if (!this.getDataFolder().exists()) {
            this.getLogger().info("No plugin folder detected...");
            this.getDataFolder().mkdir();
            this.getLogger().info("Made a new plugin folder!");
        }

        //Checks for users folder
        final File userFolder = new File(this.getDataFolder() + "/userdata");
        if (!userFolder.exists()) {
            this.getLogger().info("No users folder detected...");
            userFolder.mkdir();
            this.getLogger().info("Made a new users folder!");
        }

        //Checks for config
        final File usersFile = new File(this.getDataFolder() + "/config.yml");
        if (!usersFile.exists()) {
            this.getLogger().info("No config.yml file detected...");
            this.saveDefaultConfig();
            this.getLogger().info("Made a new config file!");
        }

        //Register Commands
        this.getCommand("currency send").setExecutor(new CurrencyCommand(this));
        this.getCommand("currency balance").setExecutor(new CurrencyCommand(this));
        this.getCommand("currency admin").setExecutor(new CurrencyAdminCommand(this));
    }
}
