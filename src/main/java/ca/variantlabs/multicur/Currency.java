package ca.variantlabs.multicur;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Currency {
    // Method to get the currency balance of a player
    public static String getCurrency(JavaPlugin plugin, UUID player){
        Set<String> set_currencies = plugin.getConfig().getConfigurationSection("currency").getKeys(false);
        List<String> currencies = new ArrayList<>(set_currencies);
        String currency_name = currencies.get(0);
        String sql = "SELECT ? FROM mcur_accounts WHERE uuid=?;";
        try {
            PreparedStatement stmt = Multicur.connection.prepareStatement(sql);
            stmt.setString(1, currency_name);
            stmt.setString(2, player.toString());
            ResultSet results = stmt.executeQuery();
            return results.getNString(currency_name);
        } catch (SQLException e){
            e.printStackTrace();
            return "Error";
        }
    }

    public static void addCurrency(UUID player, double amount){
        // If there's a failure, throw an exception
    }

    public static void removeCurrency(UUID player, double amount){
        // If there's a failure, throw an exception
    }
}
