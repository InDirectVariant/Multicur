package ca.variantlabs.multicur.commands;

import ca.variantlabs.multicur.CurrencyOperations;
import ca.variantlabs.multicur.Multicur;
import ca.variantlabs.multicur.utilities.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;

import static org.bukkit.Bukkit.getPlayer;

public class CurrencyAdminCommand implements CommandExecutor {

    private final Multicur plugin;

    public CurrencyAdminCommand(Multicur plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {

        //Checks that command was sent by player
        //if (!Validate.validateIsPlayer(plugin, sender))
            //return false;

        //Player player = (Player) sender;

        //Checks that valid command was sent
        if (command.getName().equalsIgnoreCase("curadmin")) {

            /*------------------------------------------------------------------------------------------------------------*/
            // /curadmin give <player> <currency> <amount>
            /*------------------------------------------------------------------------------------------------------------*/
            if (args[0].equalsIgnoreCase("give")) {

                //Checks for valid permission
                //if (!Validate.validateHasPermission(plugin, sender, "Console", "multicur.admin.give", "curadmin give"))
                    //return false;

                //Checks for valid command format
                if (!Validate.validateSendGiveRemoveInputs(plugin, sender, "Console", args))
                    return false;

                //Initialize variables
                Player receiver = getPlayer(args[1]);
                String currency = args[2];
                double amountToGive = Double.parseDouble(args[3]);

                //Checks that receiver exists
                if (sender instanceof Player) {
                    if (!Validate.validateReceiverExistence(plugin, (Player) sender, receiver))
                        return false;
                } else {
                    if(!Validate.validateReceiverExistenceConsole(plugin, sender, receiver))
                        return false;
                }


                //Give currency to player
                try {
                    CurrencyOperations.addCurrency(plugin, receiver.getUniqueId().toString(), currency, amountToGive);
                } catch (Exception e) {
                    sender.sendMessage("An error occurred, please contact an administrator!");
                    plugin.getLogger().info(MessageFormat.format("{0} - Error occurred with MySQL operation to give Currency: {1}", "Console", e));
                    return false;
                }

                //Sends messages to both players that transaction was a success
                sender.sendMessage(MessageFormat.format("You have given {0} {1} currency!", receiver.getDisplayName(), amountToGive));
                receiver.sendMessage(MessageFormat.format("You were given {0} currency!", amountToGive));

                return true;
            }

            /*------------------------------------------------------------------------------------------------------------*/
            // /currencyadmin remove <player> <currency> <amount>
            /*------------------------------------------------------------------------------------------------------------*/
            if (args[0].equalsIgnoreCase("remove")) {

                //Checks for valid permission
                //if (!Validate.validateHasPermission(plugin, sender, "Console", "multicur.admin.remove", "curadmin remove"))
                    //return false;

                //Checks for valid command format
                if (!Validate.validateSendGiveRemoveInputs(plugin, sender, "Console", args))
                    return false;

                //Initialize variables
                Player victim = getPlayer(args[1]);
                String currency = args[2];
                double amountToRemove = Double.parseDouble(args[3]);

                //Checks that victim exists
                //Checks that receiver exists
                if (sender instanceof Player) {
                    if (!Validate.validateReceiverExistence(plugin, (Player) sender, victim))
                        return false;
                } else {
                    if(!Validate.validateReceiverExistenceConsole(plugin, sender, victim))
                        return false;
                }

                //Remove currency from victim
                try {
                    if(!CurrencyOperations.removeCurrency(plugin, victim.getUniqueId().toString(), currency, amountToRemove)){
                     sender.sendMessage("You cannot remove more than the player's balance!");
                     plugin.getLogger().info("Cannot remove more than the player's balance!");
                     return true;
                    }
                } catch (Exception e) {
                    sender.sendMessage("An error occurred, please contact an administrator!");
                    plugin.getLogger().info(MessageFormat.format("{0} - Error occurred with MySQL operation to remove Currency: {1}", "Console", e));
                    return true;
                }

                //Sends messages to both players that transaction was a success
                sender.sendMessage(MessageFormat.format("You have removed {0} currency from {1}!", amountToRemove, victim.getDisplayName()));
                victim.sendMessage(MessageFormat.format("You had {0} currency removed!", amountToRemove));

                return true;
            }

            /*------------------------------------------------------------------------------------------------------------*/
            // /currencyadmin balance <player> <currency>
            /*------------------------------------------------------------------------------------------------------------*/
            else if (args[0].equalsIgnoreCase("bal") || args[0].equalsIgnoreCase("balance")) {

                //Checks for valid permission
                if (!Validate.validateHasPermission(plugin, sender, "Console", "multicur.admin.balance", "curadmin balance"))
                    return false;

                //Checks for valid player to find
                if (!Validate.validatePlayerNotNull(plugin, sender, "Console", args))
                    return false;
                Player playerToFind = getPlayer(args[1]);
                if (sender instanceof Player) {
                    if (!Validate.validateReceiverExistence(plugin, (Player) sender, playerToFind))
                        return false;
                } else {
                    if (!Validate.validateReceiverExistenceConsole(plugin, sender, playerToFind))
                        return false;
                }

                //Initialize variables
                String currency = args[2];

                //Sends message with balance
                try {
                    sender.sendMessage("Balance of " + playerToFind.getDisplayName() + ": " + CurrencyOperations.getCurrency(plugin, playerToFind.getUniqueId().toString(), currency));
                    plugin.getLogger().info(MessageFormat.format("{0} - Viewed {1}'s balance!", "Console", playerToFind.getDisplayName()));
                } catch (Exception e) {
                    sender.sendMessage(MessageFormat.format("Could not get the balance of {0} due to error: {1}", playerToFind.getDisplayName(), e));
                    plugin.getLogger().info(MessageFormat.format("{0} - Could not get balance due to error: {1}", playerToFind.getDisplayName(), e));
                    return false;
                }

                return true;
            }

            /*------------------------------------------------------------------------------------------------------------*/
            // No command entered
            /*------------------------------------------------------------------------------------------------------------*/
            else {
                return false;
            }
        }
        return false;
    }
}
