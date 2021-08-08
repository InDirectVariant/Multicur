package ca.variantlabs.multicur;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CurrencyOperations {

    public static boolean validateCurrencyExists(String currency) throws Exception{
        String sql = String.format("SHOW COLUMNS FROM `mcur_accounts` LIKE '%s';", currency + "_balance");
        System.out.println(sql);
        try {
            PreparedStatement stmt = Multicur.connection.prepareStatement(sql);
            ResultSet results = stmt.executeQuery();
            return results.next();
        } catch (SQLException e){
            e.printStackTrace();
            throw new Exception("SQL Exception, see console for details:");
        }
    }

    //Gets currency balance of player
    public static double getCurrencyBalance(JavaPlugin plugin, String UUID, String currency) throws Exception {

        //Gets currency name
        //Set<String> set_currencies = Objects.requireNonNull(plugin.getConfig().getConfigurationSection("currency")).getKeys(false);
        //List<String> currencies = new ArrayList<>(set_currencies);
        //String currencyName = currencies.get(0)+"_balance";

        currency += "_balance";

        try {

            //Creates and executes select statement
            String selectSQL = String.format("SELECT %s FROM mcur_accounts WHERE uuid=\"%s\";", currency, UUID);
            PreparedStatement stmt = Multicur.connection.prepareStatement(selectSQL);
            ResultSet results = stmt.executeQuery();
            results.next();
            plugin.getLogger().info(String.valueOf(results.getDouble(currency)));
            return results.getDouble(currency);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("SQL Exception, see console for details");
        }
    }

    public static void addCurrency(JavaPlugin plugin, String UUID, String currency, double amount) throws Exception {

        //Gets currency name
        //Set<String> set_currencies = Objects.requireNonNull(plugin.getConfig().getConfigurationSection("currency")).getKeys(false);
        //List<String> currencies = new ArrayList<>(set_currencies);
        //String currencyName = currencies.get(0)+"_balance";

        //Gets current balance
        double current = getCurrencyBalance(plugin, UUID, currency);
        double newBalance = current + amount;

        currency += "_balance";

        //Updates database
        try {
            String updateSQL = String.format("UPDATE mcur_accounts SET %s=%f WHERE uuid=\"%s\"", currency, newBalance, UUID);
            PreparedStatement stmt = Multicur.connection.prepareStatement(updateSQL);
            stmt.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
            throw new Exception("SQL Exception, see console for details");
        }
    }

    public static boolean removeCurrency(JavaPlugin plugin, String UUID, String currency, double amount) throws Exception {

        //Gets currency name
        //Set<String> set_currencies = Objects.requireNonNull(plugin.getConfig().getConfigurationSection("currency")).getKeys(false);
        //List<String> currencies = new ArrayList<>(set_currencies);
        //String currencyName = currencies.get(0)+"_balance";

        //Gets current balance
        double current = getCurrencyBalance(plugin, UUID, currency);
        double newBalance = current - amount;

        currency += "_balance";

        //If balance is less than 0, set to 0
        if(current<0)
            return false;

        //Updates database
        try {
            String updateSQL = String.format("UPDATE mcur_accounts SET %s=%f WHERE uuid=\"%s\"", currency, newBalance, UUID);
            PreparedStatement stmt = Multicur.connection.prepareStatement(updateSQL);
            stmt.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
            throw new Exception("SQL Exception, see console for details");
        }
        return true;
    }
}
