package ca.variantlabs.multicur;

import ca.variantlabs.multicur.commands.CurrencyAdminCommand;
import ca.variantlabs.multicur.commands.CurrencyCommand;
import ca.variantlabs.multicur.utilities.MulticurPAPIExpansion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Multicur extends JavaPlugin {

    static Connection connection;

    @Override
    public void onEnable() {
        this.getLogger().info("""

                    __  __       _ _   _          \s
                   |  \\/  |     | | | (_)               \s
                   | \\  / |_   _| | |_ _  ___ _   _ _ __\s
                   | |\\/| | | | | | __| |/ __| | | | '__|
                   | |  | | |_| | | |_| | (__| |_| | |  \s
                   |_|  |_|\\__,_|_|\\__|_|\\___|\\__,_|_|  \s

                Version 1.3.0""");

        //Checks for plugin folder
        if (!this.getDataFolder().exists()) {
            this.getLogger().info("No plugin folder detected...");
            try {
                if(this.getDataFolder().mkdir())
                    this.getLogger().info("Made a new plugin folder!");
            } catch (Exception e) {
                this.getLogger().info(e.toString());
                this.getLogger().info("Could not make a new plugin folder!");
            }
        }

        //Checks for config and messages files
        final File configFile = new File(this.getDataFolder() + "/config.yml");
        if (!configFile.exists()) {
            this.getLogger().info("No config.yml file detected...");
            this.saveDefaultConfig();
            this.getLogger().info("Made a new config file!");
        }

        // Check for PAPI
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new MulticurPAPIExpansion(this).register();
        }

        FileConfiguration config = getConfig();

        // Check to see if MySQL has been enabled
        if(config.getBoolean("mysql.use")) {
            final String mysqlConnectionString = config.getString("mysql.connection_string");


            // MySQL create table if it doesn't exist
            try {
                assert mysqlConnectionString != null;
                connection = DriverManager.getConnection(mysqlConnectionString);

                // Create userdata table
                this.getLogger().info("Creating MySQL table if not exists...");
                String createSQL = "CREATE TABLE IF NOT EXISTS mcur_accounts(id bigint NOT NULL AUTO_INCREMENT, uuid varchar(36), PRIMARY KEY(id));";
                PreparedStatement createStmt = connection.prepareStatement(createSQL);
                createStmt.executeUpdate();
                this.getLogger().info("MySQL table created successfully or detected...");
            } catch(SQLException e) {
                this.getLogger().info("Error with MySQL:");
                e.printStackTrace();
            }

            // MySQL create currency columns if they don't exist
                // Get the name of currencies
                Set<String> set_currencies = config.getConfigurationSection("currency").getKeys(false);
                List<String> currencies = new ArrayList<>(set_currencies);
            for (String s : currencies) {
                String currency = s + "_balance";

                //Create currency columns
                this.getLogger().info("Create MySQL table columns for currencies...");
                String currencySQL = String.format("ALTER TABLE mcur_accounts ADD %s double;", currency);
                try {
                    PreparedStatement currencyStmt = connection.prepareStatement(currencySQL);
                    currencyStmt.executeUpdate();
                    this.getLogger().info(String.format("Currency column for %s created in MySQL table...", currency));
                } catch (SQLException e) {
                    if (e.getErrorCode() == 1060)
                        this.getLogger().info("Currency column already exists, skipping creation...");
                    else {
                        this.getLogger().info("Error with MySQL:");
                        e.printStackTrace();
                    }
                }
            }
        }
        else {
            this.getLogger().info("MYSQL has not been enabled, disabling Multicur!\nPlease set MySQL to 'true' in config.yml and configure MySQL settings.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        //Register Commands
        this.getCommand("currency").setExecutor(new CurrencyCommand(this));
        this.getCommand("curadmin").setExecutor(new CurrencyAdminCommand(this));

        // Register Events
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(this), this);
    }

    @Override
    public void onDisable(){
        this.getLogger().info("Shutting down Multicur...");

        // MySQL
        try {
            if (connection!=null && !connection.isClosed())
                connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String getMessagePrefix(){
        return getConfig().getString("Messages.MessagePrefix");
    }
}
