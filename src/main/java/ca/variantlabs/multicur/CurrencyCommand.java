package ca.variantlabs.multicur;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CurrencyCommand implements CommandExecutor {
    private final Multicur plugin;

    public CurrencyCommand(Multicur plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {


        return false;
    }
}
