package ca.variantlabs.multicur;

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
        // Create the query
        String sql = "SELECT * FROM mcur_accounts WHERE uuid=?;";
        // Get the players UUID
        String uuid = event.getPlayer().getUniqueId().toString();

        // Check to see if the player is already in the DB
        try {
            // Prepare the statement and name it stmt
            PreparedStatement stmt = Multicur.connection.prepareStatement(sql);
            // Insert the players UUID into the query
            stmt.setString(1, uuid);
            // Execute the query and get the results
            ResultSet results = stmt.executeQuery();
            // Check the results, if there are none then we need to create the player in the DB
            if(!results.next()){
                // Get the name of the custom currency
                Set<String> set_currencies = Objects.requireNonNull(plugin.getConfig().getConfigurationSection("currency")).getKeys(false);
                List<String> currencies = new ArrayList<>(set_currencies);
                String currency = currencies.get(0);

                // Get the configured starting balance for the currency
                double start_balance = plugin.getConfig().getDouble("currency." + currency + ".starting");
                // Create the player insert
                String nsql = "INSERT INTO mcur_accounts (uuid, ?) VALUES (?, ?);";
                try {
                    // Prepare the statement and name it nstmt (new statement)
                    PreparedStatement nstmt = Multicur.connection.prepareStatement(nsql);
                    // Insert the currency name, uuid, and starting balance
                    nstmt.setString(1, currency + "_balance");
                    nstmt.setString(2, uuid);
                    nstmt.setDouble(3, start_balance);
                    // Execute the update
                    nstmt.executeUpdate();
                } catch(SQLException e){
                    // Catch and print any errors
                    e.printStackTrace();
                }
            }
        } catch(SQLException e){
            // Catch and print any errors
            e.printStackTrace();
        }
    }
}
