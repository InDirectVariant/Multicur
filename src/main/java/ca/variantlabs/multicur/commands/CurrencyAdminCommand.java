package ca.variantlabs.multicur.commands;

import ca.variantlabs.multicur.Currency;
import ca.variantlabs.multicur.Multicur;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CurrencyAdminCommand implements CommandExecutor {
    private final Multicur plugin;

    public CurrencyAdminCommand(Multicur plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        // Possible sub commands.
        // Give <player> <amount>
        // Remove <player> <amount>
        // Balance <player>
        // Create <name> <symbol>
        if(!(commandSender instanceof Player sender)){plugin.getLogger().info("ERROR: You must be a player to run this command!"); return false;}

        String op = strings[0];

        // Currency Admin give command
        if(op.equalsIgnoreCase("give")){
            if(!sender.hasPermission("multicur.admin.give")){sender.sendMessage("No permission for that command!");return false;}
            Player reciever = Bukkit.getPlayer(strings[1]);
            double amntToSend = Double.parseDouble(strings[2]);

            try {
                Currency.addCurrency(reciever.getUniqueId(), amntToSend);
            } catch (Exception e){
                sender.sendMessage("Could not send credits to " + reciever.getDisplayName());
                plugin.getLogger().info(e.toString());
            }

            sender.sendMessage("You have sent " + strings[2] + " currency to " + reciever.getDisplayName());
            reciever.sendMessage("You have received " + strings[2] + " currency from " + sender.getDisplayName());
            return true;
        }
        // Currency Admin remove command
        else if(op.equalsIgnoreCase("remove")){
            if(!sender.hasPermission("multicur.admin.remove")){sender.sendMessage("No permission for that command!");return false;}
            Player reciever = Bukkit.getPlayer(strings[1]);
            double amntToRemove = Double.parseDouble(strings[2]);

            try {
                Currency.removeCurrency(reciever.getUniqueId(), amntToRemove);
            } catch(Exception e){
                sender.sendMessage("Could not remove credits to " + reciever.getDisplayName());
                plugin.getLogger().info(e.toString());
            }

            sender.sendMessage("You have removed " + strings[2] + " currency from " + reciever.getDisplayName());
            return true;
        }
        // Currency Admin balance command
        else if(op.equalsIgnoreCase("balance") || op.equalsIgnoreCase("bal")){
            if(!sender.hasPermission("multicur.admin.balance")){sender.sendMessage("No permission for that command!");return false;}
            Player reciever = Bukkit.getPlayer(strings[1]);

            sender.sendMessage("Balance of " + reciever.getDisplayName() + ": " + Currency.getCurrency(reciever.getUniqueId()));
        }
        return false;
    }
}
