package ca.variantlabs.multicur.utilities;

import ca.variantlabs.multicur.CurrencyOperations;
import ca.variantlabs.multicur.Multicur;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class MulticurPAPIExpansion extends PlaceholderExpansion {
    final Multicur plugin;

    public MulticurPAPIExpansion(Multicur plugin){
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "multicur";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Variant";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.3.0";
    }

    @Override
    public boolean persist() {
        return true; // Required to prevent disabling this expansion on PAPI reload
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier){
        try {
            if (identifier.endsWith("balance")) {
                int und = identifier.indexOf("_") + 1;
                String currency = identifier.substring(0, und-1);

                // Check that the currency exists
                try {if (!CurrencyOperations.validateCurrencyExists(currency)) {return null;}} catch (Exception e){e.printStackTrace();return null;}

                return Double.toString(CurrencyOperations.getCurrencyBalance(plugin, player.getUniqueId().toString(), currency));
            }
        } catch(Exception e){
            return "-1";
        }
        return null;
    }
}
