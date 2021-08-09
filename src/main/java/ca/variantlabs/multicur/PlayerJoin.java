package ca.variantlabs.multicur;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class PlayerJoin implements Listener {

    private final Multicur plugin;

    public PlayerJoin(Multicur plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        //Gets player information
        String uuid = event.getPlayer().getUniqueId().toString();

        //Begins event handling
        try {
            plugin.getLogger().info(String.format("Checking for %s in the database", event.getPlayer().getDisplayName()));
            //Prepares select SQL Statement
            String selectSQL = String.format("SELECT * FROM mcur_accounts WHERE uuid=\"%s\"", uuid);
            PreparedStatement selectStmt = Multicur.connection.prepareStatement(selectSQL);
            ResultSet results = selectStmt.executeQuery();

            //Checks to see if player already exists
            if(!results.next()){

                //Gets the name of the currency
                Set<String> set_currencies = Objects.requireNonNull(plugin.getConfig().getConfigurationSection("currency")).getKeys(false);
                List<String> currencies = new ArrayList<>(set_currencies);
                String currency = currencies.get(0);

                //Gets the configured starting balance for the currency
                double startingBalance = plugin.getConfig().getDouble("currency." + currency + ".starting");
                currency+="_balance";

                //Inserts player row
                try {
                    String insertSQL = String.format("INSERT INTO mcur_accounts (uuid, %s) VALUES (\"%s\", %f);", currency, uuid, startingBalance);
                    PreparedStatement insertStmt = Multicur.connection.prepareStatement(insertSQL);
                    insertStmt.executeUpdate();
                    plugin.getLogger().info(String.format("Created %s in the database...", event.getPlayer().getDisplayName()));
                } catch(SQLException e)  { e.printStackTrace(); }
            } else {
                plugin.getLogger().info(String.format("Player %s already exists in the database...", event.getPlayer().getDisplayName()));
            }
        } catch(SQLException e) { e.printStackTrace(); }
    }
}