package ca.variantlabs.multicur;

import ca.variantlabs.multicur.commands.CurrencyAdminCommand;
import ca.variantlabs.multicur.commands.CurrencyCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Multicur extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getLogger().info("Starting up Multicur...");

        //Checks for plugin folder
        if (!this.getDataFolder().exists()) {
            this.getLogger().info("No plugin folder detected...");
            try {
                if(this.getDataFolder().mkdir()){
                    this.getLogger().info("Made a new plugin folder!");
                };
            } catch (Exception e){
                this.getLogger().info(e.toString());
                this.getLogger().info("Could not make a new plugin folder");
            }

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

    @Override
    public void onDisable(){
        this.getLogger().info("Shutting down Multicur...");
    }
}
