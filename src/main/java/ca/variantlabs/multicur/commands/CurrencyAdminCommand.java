package ca.variantlabs.multicur.commands;

import ca.variantlabs.multicur.CurrencyOperations;
import ca.variantlabs.multicur.Multicur;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
            Player receiver = Bukkit.getPlayer(strings[1]);
            double amntToSend = Double.parseDouble(strings[2]);

            // Give currency to player
            try {
                assert receiver != null;
                CurrencyOperations.addCurrency(plugin, receiver.getUniqueId().toString(), amntToSend);
            } catch (Exception e){
                sender.sendMessage("Could not send credits to " + receiver.getDisplayName());
                plugin.getLogger().info(MessageFormat.format("ERROR: Could not add currency to {0}", receiver.getDisplayName()));
                plugin.getLogger().info(e.toString());
            }

            // Send confirmation messages
            sender.sendMessage("You have sent " + strings[2] + " currency to " + receiver.getDisplayName() + "!");
            receiver.sendMessage("You have received " + strings[2] + " currency!");
            plugin.getLogger().info(MessageFormat.format("{0} - Added {1} currency to {2}'s balance!", sender.getDisplayName(), amntToSend, receiver.getDisplayName()));
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
            Player receiver = Bukkit.getPlayer(strings[1]);
            double amntToRemove = Double.parseDouble(strings[2]);

            // Remove the currency
            try {
                assert receiver != null;
                CurrencyOperations.removeCurrency(plugin, receiver.getUniqueId().toString(), amntToRemove);
            } catch(Exception e){
                sender.sendMessage("Could not remove credits to " + receiver.getDisplayName());
                plugin.getLogger().info(MessageFormat.format("ERROR: Could not remove currency from {0}!", receiver.getDisplayName()));
                plugin.getLogger().info(e.toString());
            }

            // Send confirmation messages
            sender.sendMessage("You have removed " + strings[2] + " currency from " + receiver.getDisplayName());
            receiver.sendMessage( strings[2] + " currency has been removed from your account!");
            plugin.getLogger().info(MessageFormat.format("{0} - Removed {1} currency from {0}'s balance!", sender.getDisplayName(), amntToRemove, receiver.getDisplayName()));
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
            Player receiver = Bukkit.getPlayer(strings[1]);

            // Send a message with the balance
            try {
                assert receiver != null;
                sender.sendMessage("Balance of " + receiver.getDisplayName() + ": " + CurrencyOperations.getCurrency(plugin, receiver.getUniqueId().toString()));
                plugin.getLogger().info(MessageFormat.format("{0} - Viewed {1}'s balance!", sender.getDisplayName(), receiver.getDisplayName()));
            } catch (Exception e){
                sender.sendMessage(MessageFormat.format("Could not get the balance of {0} due to error: {1}", receiver.getDisplayName(), e));
                plugin.getLogger().info(MessageFormat.format("{0} - Could not get balance due to error: {1}", receiver.getDisplayName(), e));
            }
        }
        return false;
    }
}
