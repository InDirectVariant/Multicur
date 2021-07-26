package ca.variantlabs.multicur;

import ca.variantlabs.multicur.commands.CurrencyAdminCommand;
import ca.variantlabs.multicur.commands.CurrencyCommand;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Multicur extends JavaPlugin {

    static Connection connection;

    @Override
    public void onEnable() {
        this.getLogger().info("Starting up Multicur...");

        //Checks for plugin folder
        if (!this.getDataFolder().exists()) {
            this.getLogger().info("No plugin folder detected...");
            try {
                if(this.getDataFolder().mkdir()){
                    this.getLogger().info("Made a new plugin folder!");
                }
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

        FileConfiguration config = getConfig();

        // Check to see if MySQL has been enabled
        if(config.getBoolean("mysql.use")) {
            final String username = config.getString("mysql.username");
            final String password = config.getString("mysql.password");
            final String url = config.getString("mysql.address");

            // MySQL
            try {
                connection = DriverManager.getConnection("jdbc:mysql://" + url, username, password);
                // Create userdata table
                String sql = "CREATE TABLE IF NOT EXISTS mcur_accounts(id bigint NOT NULL AUTO_INCREMENT, uuid varchar(36), PRIMARY KEY(id));";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.executeUpdate();

                // Create currency columns
                Set<String> set_currencies = Objects.requireNonNull(config.getConfigurationSection("currency")).getKeys(false);
                List<String> currencies = new ArrayList<>(set_currencies);
                String currency = currencies.get(0);

                String nsql = "ALTER TABLE mcur_accounts ADD ? double;";
                PreparedStatement nstmt = connection.prepareStatement(nsql);
                nstmt.setString(1, currency + "_balance");
                nstmt.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
                if(e.getErrorCode() == 1060){
                    this.getLogger().info("Currency column already exists, skipping creation.");
                } else {
                    this.getLogger().info("Error with MySQL, see above Stack Trace for debugging.");
                }
            }
        } else {
            this.getLogger().info("MYSQL has not been enabled, disabling Multicur!");
            Bukkit.getPluginManager().disablePlugin(this);
        }


        //Register Commands
        this.getCommand("currency send").setExecutor(new CurrencyCommand(this));
        this.getCommand("currency balance").setExecutor(new CurrencyCommand(this));
        this.getCommand("currency bal").setExecutor(new CurrencyCommand(this));
        this.getCommand("currency admin").setExecutor(new CurrencyAdminCommand(this));

        // Register Events
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(this), this);
    }

    @Override
    public void onDisable(){
        this.getLogger().info("Shutting down Multicur...");

        // MySQL
        try { // using a try catch to catch connection errors (like wrong sql password...)
            if (connection!=null && !connection.isClosed()){ // checking if connection isn't null to
                // avoid receiving a null pointer
                connection.close(); // closing the connection field variable.
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
