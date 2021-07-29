package ca.variantlabs.multicur;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CurrencyOperations {

    //Gets currency balance of player
    public static String getCurrency(JavaPlugin plugin, String UUID) throws Exception {

        //Gets currency name
        Set<String> set_currencies = Objects.requireNonNull(plugin.getConfig().getConfigurationSection("currency")).getKeys(false);
        List<String> currencies = new ArrayList<>(set_currencies);
        String currencyName = currencies.get(0)+"_balance";

        try {

            //Creates and executes select statement
            String selectSQL = String.format("SELECT %s FROM mcur_accounts WHERE uuid=\"%s\";", currencyName, UUID);
            PreparedStatement stmt = Multicur.connection.prepareStatement(selectSQL);
            ResultSet results = stmt.executeQuery();
            return results.getNString(currencyName);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("SQL Exception, see console for details");
        }
    }

    public static void addCurrency(JavaPlugin plugin, String UUID, double amount) throws Exception {

        //Gets currency name
        Set<String> set_currencies = Objects.requireNonNull(plugin.getConfig().getConfigurationSection("currency")).getKeys(false);
        List<String> currencies = new ArrayList<>(set_currencies);
        String currencyName = currencies.get(0)+"_balance";

        //Gets current balance
        double current = Double.parseDouble(getCurrency(plugin, UUID));
        current += amount;

        //Updates database
        try {
            String updateSQL = String.format("UPDATE mcur_accounts SET %s=%f WHERE uuid=\"%s\"", currencyName, current, UUID);
            PreparedStatement stmt = Multicur.connection.prepareStatement(updateSQL);
            stmt.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
            throw new Exception("SQL Exception, see console for details");
        }
    }

    public static void removeCurrency(JavaPlugin plugin, String UUID, double amount) throws Exception {

        //Gets currency name
        Set<String> set_currencies = Objects.requireNonNull(plugin.getConfig().getConfigurationSection("currency")).getKeys(false);
        List<String> currencies = new ArrayList<>(set_currencies);
        String currencyName = currencies.get(0)+"_balance";

        //Gets current balance
        double current = Double.parseDouble(getCurrency(plugin, UUID));
        current -= amount;

        //If balance is less than 0, set to 0
        if(current<0)
            current=0;

        //Updates database
        try {
            String updateSQL = String.format("UPDATE mcur_accounts SET %s=%f WHERE uuid=\"%s\"", currencyName, current, UUID);
            PreparedStatement stmt = Multicur.connection.prepareStatement(updateSQL);
            stmt.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
            throw new Exception("SQL Exception, see console for details");
        }
    }
}
