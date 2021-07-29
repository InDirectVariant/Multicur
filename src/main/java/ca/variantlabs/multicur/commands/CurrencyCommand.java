package ca.variantlabs.multicur.commands;

import ca.variantlabs.multicur.Multicur;
import ca.variantlabs.multicur.CurrencyOperations;
import ca.variantlabs.multicur.utilities.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import static org.bukkit.Bukkit.getPlayer;

public class CurrencyCommand implements CommandExecutor {

    private final Multicur plugin;

    public CurrencyCommand(Multicur plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {

        //Checks that command was sent by player
        if (!Validate.validateIsPlayer(plugin, sender))
            return false;

        Player player = (Player) sender;

        //Checks that valid command was sent
        if (command.getName().equalsIgnoreCase("currency")) {

            /*------------------------------------------------------------------------------------------------------------*/
            // /currency send
            /*------------------------------------------------------------------------------------------------------------*/
            if (args[0].equalsIgnoreCase("send")) {

                //Checks for valid permission
                if (!Validate.validateHasPermission(plugin, sender, player.getDisplayName(), "multicur.currency.send", "currency send"))
                    return false;

                //Checks for valid command format
                if (!Validate.validateSendGiveRemoveInputs(plugin, sender, player.getDisplayName(), args))
                    return false;

                Player receiver = getPlayer(args[1]);

                //Checks that receiver exists
                if (!Validate.validateReceiverExistence(plugin, player, receiver))
                    return false;

                // Get the balance of the sender and how much they want to send
                try {
                    double senderBalance = CurrencyOperations.getCurrency(plugin, player.getUniqueId().toString());
                    double amountToSend = Double.parseDouble(args[2]);

                    //Checks that player is sending a valid amount
                    if (!Validate.validateSendCurrencyAmount(sender, amountToSend, senderBalance))
                        return false;

                    //Performs the operation that transfers the currency
                    try {
                        CurrencyOperations.removeCurrency(plugin, player.getUniqueId().toString(), amountToSend);
                        CurrencyOperations.addCurrency(plugin, receiver.getUniqueId().toString(), amountToSend);
                    } catch (Exception e) {
                        sender.sendMessage("An error occurred, please contact an administrator!");
                        plugin.getLogger().info(MessageFormat.format("{0} - Error occurred with MySQL operation to send Currency: {1}", player.getDisplayName(), e));
                        return false;
                    }

                    //Sends messages to both players that transaction was a success
                    sender.sendMessage(MessageFormat.format("You have sent {0} {1} currency!", receiver.getDisplayName(), amountToSend));
                    receiver.sendMessage(MessageFormat.format("You have received {0} currency from {1}!", amountToSend, player.getDisplayName()));

                    return true;
                } catch (Exception e) {
                    sender.sendMessage("An error occurred, please contact an administrator!");
                    plugin.getLogger().info(MessageFormat.format("{0} - Error occurred with MySQL operation to get Currency: {1}", player.getDisplayName(), e));
                    return false;
                }
            }

            /*------------------------------------------------------------------------------------------------------------*/
            // /currency balance
            /*------------------------------------------------------------------------------------------------------------*/
            else if (args[0].equalsIgnoreCase("bal") || args[0].equalsIgnoreCase("balance")) {

                //Checks for valid permission
                if (!Validate.validateHasPermission(plugin, sender, player.getDisplayName(), "multicur.currency.balance", "currency balance"))
                    return false;

                //Gets balance and sends message to player
                try {
                    double balance = CurrencyOperations.getCurrency(plugin, player.getUniqueId().toString());
                    sender.sendMessage(MessageFormat.format("Your balance is {0}!", balance));
                } catch (Exception e) {
                    sender.sendMessage("An error occurred, please contact an administrator!");
                    plugin.getLogger().info(MessageFormat.format("{0} - Error occurred with MySQL operation to get Currency: {1}", player.getDisplayName(), e));
                    return false;
                }

                return true;
            }

            /*------------------------------------------------------------------------------------------------------------*/
            // No command entered
            /*------------------------------------------------------------------------------------------------------------*/
            else {
                sender.sendMessage("Available commands: /currency [send <player> <amount>]/[balance]/[bal]");
                return false;
            }
        }
        return false;
    }
}
