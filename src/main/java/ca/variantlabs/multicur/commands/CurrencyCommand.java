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
            // /currency send <player> <currency> <amount>
            /*------------------------------------------------------------------------------------------------------------*/
            if (args[0].equalsIgnoreCase("send")) {

                //Checks for valid permission
                if (!Validate.validateHasPermission(plugin, sender, player.getDisplayName(), "multicur.currency.send", "currency send"))
                    return false;

                //Checks for valid command format
                if (!Validate.validateSendGiveRemoveInputs(plugin, sender, player.getDisplayName(), args))
                    return false;

                //Initialize variables
                Player receiver = getPlayer(args[1]);
                String currency = args[2];

                // Check that the currency exists
                try {
                    if (!CurrencyOperations.validateCurrencyExists(currency)) {
                        sender.sendMessage(String.format("Currency with name %s does not exist!", currency));
                        return false;
                    }
                } catch (Exception e){
                    sender.sendMessage("Error occurred. Contact an admin");
                    e.printStackTrace();
                    return true;
                }

                // Check if sending the currency is allowed
                if(!plugin.getConfig().getBoolean(String.format("%s.pay", currency))){
                    sender.sendMessage("Sending that currency is not allowed!");
                }

                //Checks that receiver exists
                if (!Validate.validateReceiverExistence(plugin, player, receiver))
                    return false;

                // Get the balance of the sender and how much they want to send
                try {
                    double senderBalance = CurrencyOperations.getCurrencyBalance(plugin, player.getUniqueId().toString(), currency);
                    double amountToSend = Double.parseDouble(args[3]);

                    //Checks that player is sending a valid amount
                    if (!Validate.validateSendCurrencyAmount(sender, amountToSend, senderBalance))
                        return false;

                    //Performs the operation that transfers the currency
                    try {
                        CurrencyOperations.removeCurrency(plugin, player.getUniqueId().toString(), currency, amountToSend);
                        CurrencyOperations.addCurrency(plugin, receiver.getUniqueId().toString(), currency, amountToSend);
                    } catch (Exception e) {
                        sender.sendMessage("An error occurred, please contact an administrator!");
                        plugin.getLogger().info(MessageFormat.format("{0} - Error occurred with MySQL operation to send Currency: {1}", player.getDisplayName(), e));
                        return false;
                    }

                    //Send a message to both players that transaction was a success
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
            // /currency balance <currency>
            /*------------------------------------------------------------------------------------------------------------*/
            else if (args[0].equalsIgnoreCase("bal") || args[0].equalsIgnoreCase("balance")) {

                //Checks for valid permission
                if (!Validate.validateHasPermission(plugin, sender, player.getDisplayName(), "multicur.currency.balance", "currency balance"))
                    return false;

                //Check for currency
                if(args.length != 3 ){
                    sender.sendMessage("You must input a currency name to check!");
                    return false;
                }

                // Initialize variables
                String currency = args [1];

                // Check that the currency exists
                try {
                    if (!CurrencyOperations.validateCurrencyExists(currency)) {
                        sender.sendMessage(String.format("Currency with name %s does not exist!", currency));
                        return false;
                    }
                } catch (Exception e){
                    sender.sendMessage("Error occurred. Contact an admin");
                    e.printStackTrace();
                    return true;
                }

                //Gets balance and sends message to player
                try {
                    double balance = CurrencyOperations.getCurrencyBalance(plugin, player.getUniqueId().toString(), currency);
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
                sender.sendMessage("Available commands: /currency send <player> <currency> <amount> \n /currency <balance/bal> <currency>");
                return false;
            }
        }
        return false;
    }
}
