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
import java.util.Objects;

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
                if (!Validate.validateHasPermission(plugin, sender, player.getDisplayName(), "multicur.currency.send", "currency send")) {
                    sender.sendMessage(MessageFormat.format("{0} You do not have permission for that!", plugin.getMessagePrefix()));
                    return false;
                }

                //Checks for valid command format
                if (!Validate.validateSendGiveRemoveInputs(plugin, sender, player.getDisplayName(), args))
                    return false;

                //Initialize variables
                Player receiver = getPlayer(args[1]);
                String currency = args[2];

                // Check that the currency exists
                try {
                    if (!CurrencyOperations.validateCurrencyExists(currency)) {
                        sender.sendMessage(MessageFormat.format("{0} That currency does not exist!", plugin.getMessagePrefix()));
                        return false;
                    }
                } catch (Exception e){
                    sender.sendMessage(MessageFormat.format("{0} An error occurred, please contact an admin!", plugin.getMessagePrefix()));
                    e.printStackTrace();
                    return true;
                }

                // Check if sending the currency is allowed
                if(!plugin.getConfig().getBoolean(String.format("%s.pay", currency))){
                    sender.sendMessage(MessageFormat.format("{0} You cannot send that currency!", plugin.getMessagePrefix()));
                }

                //Checks that receiver exists
                if (!Validate.validateReceiverExistence(plugin, player, receiver)) {
                    sender.sendMessage(MessageFormat.format("{0} That receiver does not exist!", plugin.getMessagePrefix()));
                    return false;
                }

                // Get the balance of the sender and how much they want to send
                try {
                    double senderBalance = CurrencyOperations.getCurrencyBalance(plugin, player.getUniqueId().toString(), currency);
                    double amountToSend = Double.parseDouble(args[3]);

                    //Checks that player is sending a valid amount
                    if (!Validate.validateSendCurrencyAmount(sender, amountToSend, senderBalance)) {
                        sender.sendMessage(MessageFormat.format("{0} Please enter a valid amount!", plugin.getMessagePrefix()));
                        return false;
                    }

                    //Performs the operation that transfers the currency
                    try {
                        CurrencyOperations.removeCurrency(plugin, player.getUniqueId().toString(), currency, amountToSend);
                        assert receiver != null;
                        CurrencyOperations.addCurrency(plugin, receiver.getUniqueId().toString(), currency, amountToSend);
                    } catch (Exception e) {
                        sender.sendMessage(MessageFormat.format("{0} An error occurred, please contact an admin!", plugin.getMessagePrefix()));
                        plugin.getLogger().info(MessageFormat.format("{0} - Error occurred with MySQL operation to send Currency: {1}", player.getDisplayName(), e));
                        return false;
                    }

                    //Send a message to both players that transaction was a success
                    sender.sendMessage(MessageFormat.format("{0} You have sent {1} {2} to {3}!", plugin.getMessagePrefix(), amountToSend, currency, receiver.getDisplayName()));

                    receiver.sendMessage(MessageFormat.format("{0} You have received {1} {2} from {3}!", plugin.getMessagePrefix(), amountToSend, currency, ((Player) sender).getDisplayName()));
                    return true;
                } catch (Exception e) {
                    sender.sendMessage(MessageFormat.format("{0} An error occurred, please contact an admin!", plugin.getMessagePrefix()));
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
                if(args.length != 2 ){
                    sender.sendMessage(MessageFormat.format("{0} You must enter a currency!", plugin.getMessagePrefix()));
                    return false;
                }

                // Initialize variables
                String currency = args [1];

                // Check that the currency exists
                try {
                    if (!CurrencyOperations.validateCurrencyExists(currency)) {
                        sender.sendMessage(MessageFormat.format("{0} That currency does not exist!", plugin.getMessagePrefix()));
                        return false;
                    }
                } catch (Exception e){
                    sender.sendMessage(MessageFormat.format("{0} An error occurred, please contact an admin!", plugin.getMessagePrefix()));
                    e.printStackTrace();
                    return true;
                }

                //Gets balance and sends message to player
                try {
                    double balance = CurrencyOperations.getCurrencyBalance(plugin, player.getUniqueId().toString(), currency);
                    sender.sendMessage(MessageFormat.format("{0} You have {1} {2}", plugin.getMessagePrefix(), balance, currency));
                } catch (Exception e) {
                    sender.sendMessage(MessageFormat.format("{0} An error occurred, please contact an admin!", plugin.getMessagePrefix()));
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
                return true;
            }
        }
        return false;
    }
}
