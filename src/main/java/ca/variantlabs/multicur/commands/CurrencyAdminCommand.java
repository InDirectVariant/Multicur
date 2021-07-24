package ca.variantlabs.multicur.commands;

import ca.variantlabs.multicur.Currency;
import ca.variantlabs.multicur.Multicur;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.mail.Message;
import java.text.MessageFormat;

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
        if(!(commandSender instanceof Player sender)){plugin.getLogger().info("ERROR: You must be a player to run this command!"); return false;}

        String op = strings[0];

        // Currency Admin give command
        if(op.equalsIgnoreCase("give")){
            if(!sender.hasPermission("multicur.admin.give")){
                sender.sendMessage("No permission for that command!");
                plugin.getLogger().info(MessageFormat.format("{0} - No permission to run Admin Give command!", sender.getDisplayName()));
                return false;
            }

            // Check to see if there was a player added and currency specified
            if(strings[1].isEmpty()){
                sender.sendMessage("You must specify a player!");
                plugin.getLogger().info(MessageFormat.format("{0} - Did not specify a player!", sender.getDisplayName()));
                return false;
            }
            if(strings[2].isEmpty()){
                sender.sendMessage("You much specify an amount of currency to give!");
                plugin.getLogger().info(MessageFormat.format("{0} - Did not specify an amount of currency!", sender.getDisplayName()));
                return false;
            }

            // Assign variables
            Player reciever = Bukkit.getPlayer(strings[1]);
            double amntToSend = Double.parseDouble(strings[2]);

            // Give currency to player
            try {
                Currency.addCurrency(reciever.getUniqueId(), amntToSend);
            } catch (Exception e){
                sender.sendMessage("Could not send credits to " + reciever.getDisplayName());
                plugin.getLogger().info(MessageFormat.format("ERROR: Could not add currency to {0}", reciever.getDisplayName()));
                plugin.getLogger().info(e.toString());
            }

            // Send confirmation messages
            sender.sendMessage("You have sent " + strings[2] + " currency to " + reciever.getDisplayName() + "!");
            reciever.sendMessage("You have received " + strings[2] + " currency!");
            plugin.getLogger().info(MessageFormat.format("{0} - Added {1} currency to {2}'s balance!", sender.getDisplayName(), amntToSend, reciever.getDisplayName()));
            return true;
        }
        // Currency Admin remove command
        else if(op.equalsIgnoreCase("remove")){
            if(!sender.hasPermission("multicur.admin.remove")){
                sender.sendMessage("No permission for that command!");
                plugin.getLogger().info(MessageFormat.format("{0} - No permission to run Admin Remove command!", sender.getDisplayName()));
                return false;
            }

            // Check to see if there was a player added and currency specified
            if(strings[1].isEmpty()){
                sender.sendMessage("You must specify a player!");
                plugin.getLogger().info(MessageFormat.format("{0} - Did not specify a player!", sender.getDisplayName()));
                return false;
            }
            if(strings[2].isEmpty()){
                sender.sendMessage("You much specify an amount of currency to remove!");
                plugin.getLogger().info(MessageFormat.format("{0} - Did not specify an amount of currency!", sender.getDisplayName()));
                return false;
            }

            // Assign variables
            Player reciever = Bukkit.getPlayer(strings[1]);
            double amntToRemove = Double.parseDouble(strings[2]);

            // Remove the currency
            try {
                Currency.removeCurrency(reciever.getUniqueId(), amntToRemove);
            } catch(Exception e){
                sender.sendMessage("Could not remove credits to " + reciever.getDisplayName());
                plugin.getLogger().info(MessageFormat.format("ERROR: Could not remove currency from {0}!", reciever.getDisplayName()));
                plugin.getLogger().info(e.toString());
            }

            // Send confirmation messages
            sender.sendMessage("You have removed " + strings[2] + " currency from " + reciever.getDisplayName());
            reciever.sendMessage( strings[2] + " currency has been removed from your account!");
            plugin.getLogger().info(MessageFormat.format("{0} - Removed {1} currency from {0}'s balance!", sender.getDisplayName(), amntToRemove, reciever.getDisplayName()));
            return true;
        }
        // Currency Admin balance command
        else if(op.equalsIgnoreCase("balance") || op.equalsIgnoreCase("bal")){
            if(!sender.hasPermission("multicur.admin.balance")){
                sender.sendMessage("No permission for that command!");
                plugin.getLogger().info(MessageFormat.format("{0} - No permission to run Admin Balance command!", sender.getDisplayName()));
                return false;
            }

            // Check to see if there was a player added
            if(strings[1].isEmpty()){
                sender.sendMessage("You must specify a player!");
                plugin.getLogger().info(MessageFormat.format("{0} - Did not specify a player!", sender.getDisplayName()));
                return false;
            }

            // Get the player
            Player reciever = Bukkit.getPlayer(strings[1]);

            // Send a message with the balance
            sender.sendMessage("Balance of " + reciever.getDisplayName() + ": " + Currency.getCurrency(plugin, reciever.getUniqueId()));
            plugin.getLogger().info(MessageFormat.format("{0} - Viewed {1}'s balance!", sender.getDisplayName(), reciever.getDisplayName()));
        }
        return false;
    }
}
