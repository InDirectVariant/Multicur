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
        return "1.1.0";
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
                String currency = "";
                for(int i = 0; i < und; i++){
                   currency += identifier.charAt(i);
                }
                return Double.toString(CurrencyOperations.getCurrency(plugin, player.getUniqueId().toString(), currency));
            }
        } catch(Exception e){
            return "-1";
        }
        return null;
    }
}
