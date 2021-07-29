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

public class CurrencyAdminCommandExecutor implements CommandExecutor {

    private final Multicur plugin;

    public CurrencyAdminCommandExecutor(Multicur plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {

        //Checks that command was sent by player
        if (!Validate.validateIsPlayer(plugin, sender))
            return false;

        Player player = (Player) sender;

        //Checks that valid command was sent
        if (command.getName().equalsIgnoreCase("currencyadmin")) {

            /*------------------------------------------------------------------------------------------------------------*/
            // /currencyadmin give
            /*------------------------------------------------------------------------------------------------------------*/
            if (args[0].equalsIgnoreCase("give")) {

                //Checks for valid permission
                if (!Validate.validateHasPermission(plugin, sender, player.getDisplayName(), "multicur.admin.give", "currencyadmin give"))
                    return false;

                //Checks for valid command format
                if (!Validate.validateSendGiveRemoveInputs(plugin, sender, player.getDisplayName(), args))
                    return false;

                //Initialize variables
                Player receiver = getPlayer(args[1]);
                double amountToGive = Double.parseDouble(args[2]);

                //Checks that receiver exists
                if (!Validate.validateReceiverExistence(plugin, player, receiver))
                    return false;

                //Give currency to player
                try {
                    CurrencyOperations.addCurrency(plugin, receiver.getUniqueId().toString(), amountToGive);
                } catch (Exception e) {
                    sender.sendMessage("An error occurred, please contact an administrator!");
                    plugin.getLogger().info(MessageFormat.format("{0} - Error occurred with MySQL operation to give Currency: {1}", player.getDisplayName(), e));
                    return false;
                }

                //Sends messages to both players that transaction was a success
                sender.sendMessage(MessageFormat.format("You have given {0} {1} currency!", receiver.getDisplayName(), amountToGive));
                receiver.sendMessage(MessageFormat.format("You were given {0} currency!", amountToGive));

                return true;
            }

            /*------------------------------------------------------------------------------------------------------------*/
            // /currencyadmin remove
            /*------------------------------------------------------------------------------------------------------------*/
            if (args[0].equalsIgnoreCase("remove")) {

                //Checks for valid permission
                if (!Validate.validateHasPermission(plugin, sender, player.getDisplayName(), "multicur.admin.remove", "currencyadmin remove"))
                    return false;

                //Checks for valid command format
                if (!Validate.validateSendGiveRemoveInputs(plugin, sender, player.getDisplayName(), args))
                    return false;

                //Initialize variables
                Player victim = getPlayer(args[1]);
                double amountToRemove = Double.parseDouble(args[2]);

                //Checks that victim exists
                if (!Validate.validateReceiverExistence(plugin, player, victim))
                    return false;

                //Remove currency from victim
                try {
                    CurrencyOperations.removeCurrency(plugin, victim.getUniqueId().toString(), amountToRemove);
                } catch (Exception e) {
                    sender.sendMessage("An error occurred, please contact an administrator!");
                    plugin.getLogger().info(MessageFormat.format("{0} - Error occurred with MySQL operation to remove Currency: {1}", player.getDisplayName(), e));
                    return false;
                }

                //Sends messages to both players that transaction was a success
                sender.sendMessage(MessageFormat.format("You have removed {0} currency from {1}!", amountToRemove, victim.getDisplayName()));
                victim.sendMessage(MessageFormat.format("You had {0} currency removed!", amountToRemove));

                return true;
            }

            /*------------------------------------------------------------------------------------------------------------*/
            // /currencyadmin balance
            /*------------------------------------------------------------------------------------------------------------*/
            else if (args[0].equalsIgnoreCase("bal") || args[0].equalsIgnoreCase("balance")) {

                //Checks for valid permission
                if (!Validate.validateHasPermission(plugin, sender, player.getDisplayName(), "multicur.admin.balance", "currencyadmin balance"))
                    return false;

                //Checks for valid player to find
                if (!Validate.validatePlayerNotNull(plugin, sender, player.getDisplayName(), args))
                    return false;
                Player playerToFind = getPlayer(args[1]);
                if (!Validate.validateReceiverExistence(plugin, player, playerToFind))
                    return false;

                //Sends message with balance
                try {
                    sender.sendMessage("Balance of " + playerToFind.getDisplayName() + ": " + CurrencyOperations.getCurrency(plugin, playerToFind.getUniqueId().toString()));
                    plugin.getLogger().info(MessageFormat.format("{0} - Viewed {1}'s balance!", player.getDisplayName(), playerToFind.getDisplayName()));
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
                sender.sendMessage("Available commands: /currencyadmin [give <player> <amount>]/[balance <player>]/[bal <player>]");
                return false;
            }
        }
        return false;
    }
}
