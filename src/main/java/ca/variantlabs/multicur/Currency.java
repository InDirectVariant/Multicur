package ca.variantlabs.multicur;

import org.bukkit.configuration.file.FileConfiguration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Currency {

    public boolean currencyCreate(String[] s){

        return true;
    }

    public static String getCurrency(UUID player){






        String sql = "SELECT bal_currency1 FROM mcur_accounts WHERE uuid=?;";
        try {
            PreparedStatement stmt = Multicur.connection.prepareStatement(sql);
            stmt.setString(1, player.toString());
            ResultSet results = stmt.executeQuery();
            return results.getNString("bal_currency1");
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
