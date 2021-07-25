package ca.variantlabs.multicur;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Currency {
    // Method to get the currency balance of a player
    public static String getCurrency(JavaPlugin plugin, UUID player) throws Exception{
        Set<String> set_currencies = Objects.requireNonNull(plugin.getConfig().getConfigurationSection("currency")).getKeys(false);
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
            throw new Exception("SQL Exception, see console for details");
        }
    }

    public static void  addCurrency(JavaPlugin plugin, UUID player, double amount) throws Exception {
        // If there's a failure, throw an exception
        Set<String> set_currencies = Objects.requireNonNull(plugin.getConfig().getConfigurationSection("currency")).getKeys(false);
        List<String> currencies = new ArrayList<>(set_currencies);
        String currency_name = currencies.get(0);

        // Get the current balance to determine what to set the new balance to
        double current = Double.parseDouble(getCurrency(plugin, player));
        amount += current;

        String sql = "UPDATE mcur_accounts SET ?=? WHERE uuid=?";
        try {
            PreparedStatement stmt = Multicur.connection.prepareStatement(sql);
            stmt.setString(1, currency_name);
            stmt.setDouble(2, amount);
            stmt.setString(3, player.toString());
            stmt.executeUpdate();
        } catch(SQLException e){
            e.printStackTrace();
            throw new Exception("Error with the Database, see console for error log");
        }

    }

    public static void removeCurrency(JavaPlugin plugin, UUID player, double amount) throws Exception{
        // If there's a failure, throw an exception
        Set<String> set_currencies = Objects.requireNonNull(plugin.getConfig().getConfigurationSection("currency")).getKeys(false);
        List<String> currencies = new ArrayList<>(set_currencies);
        String currency_name = currencies.get(0);

        // Get the current balance to determine what to set the new balance to
        double current = Double.parseDouble(getCurrency(plugin, player));
        amount -= current;

        String sql = "UPDATE mcur_accounts SET ?=? WHERE uuid=?";
        try {
            PreparedStatement stmt = Multicur.connection.prepareStatement(sql);
            stmt.setString(1, currency_name);
            stmt.setDouble(2, amount);
            stmt.setString(3, player.toString());
            stmt.executeUpdate();
        } catch(SQLException e){
            e.printStackTrace();
            throw new Exception("Error with the Database, see console for error log");
        }
    }
}
