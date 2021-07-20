package ca.variantlabs.multicur.commands;

import ca.variantlabs.multicur.Multicur;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CurrencyCommand implements CommandExecutor {
    private final Multicur plugin;

    public CurrencyCommand(Multicur plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        
        return false;
    }
}
