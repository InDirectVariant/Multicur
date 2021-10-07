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
                    sender.sendMessage(MessageFormat.format("{0} {1}", plugin.getMessagePrefix(), plugin.getMessage("NoPermission")));
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
                        String msg = plugin.getMessage("CurrencyDoesNotExist");
                        msg = msg.replace("[currencyName]", currency);
                        sender.sendMessage(MessageFormat.format("{0} {1}", plugin.getMessagePrefix(), msg));
                        return false;
                    }
                } catch (Exception e){
                    sender.sendMessage(MessageFormat.format("{0} {1}", plugin.getMessagePrefix(), plugin.getMessage("GenericError")));
                    e.printStackTrace();
                    return true;
                }

                // Check if sending the currency is allowed
                if(!plugin.getConfig().getBoolean(String.format("%s.pay", currency))){
                    String msg = plugin.getMessage("CurrencyNotAllowed");
                    msg = msg.replace("[currencyName]", currency);
                    sender.sendMessage(MessageFormat.format("{0} {1}", plugin.getMessagePrefix(), msg));
                }

                //Checks that receiver exists
                if (!Validate.validateReceiverExistence(plugin, player, receiver)) {
                    String msg = plugin.getMessage("ReceiverDoesNotExist");
                    msg = msg.replace("[currencyName]", currency);
                    msg = msg.replace("[targetName]", args[1]);
                    sender.sendMessage(MessageFormat.format("{0} {1}", plugin.getMessagePrefix(), msg));
                    return false;
                }

                // Get the balance of the sender and how much they want to send
                try {
                    double senderBalance = CurrencyOperations.getCurrencyBalance(plugin, player.getUniqueId().toString(), currency);
                    double amountToSend = Double.parseDouble(args[3]);

                    //Checks that player is sending a valid amount
                    if (!Validate.validateSendCurrencyAmount(sender, amountToSend, senderBalance)) {
                        String msg = plugin.getMessage("InvalidCurrencyAmount");
                        msg = msg.replace("[currencyName]", currency);
                        msg = msg.replace("[targetName]", args[1]);
                        msg = msg.replace("[sendAmount]", Double.toString(amountToSend));
                        sender.sendMessage(MessageFormat.format("{0} {1}", plugin.getMessagePrefix(), msg));
                        return false;
                    }

                    //Performs the operation that transfers the currency
                    try {
                        CurrencyOperations.removeCurrency(plugin, player.getUniqueId().toString(), currency, amountToSend);
                        assert receiver != null;
                        CurrencyOperations.addCurrency(plugin, receiver.getUniqueId().toString(), currency, amountToSend);
                    } catch (Exception e) {
                        sender.sendMessage(MessageFormat.format("{0} {1}", plugin.getMessagePrefix(), plugin.getMessage("GenericError")));
                        plugin.getLogger().info(MessageFormat.format("{0} - Error occurred with MySQL operation to send Currency: {1}", player.getDisplayName(), e));
                        return false;
                    }

                    //Send a message to both players that transaction was a success
                    String senderMsg = plugin.getMessage("SendCurrencySuccessForSender");
                    senderMsg = senderMsg.replace("[currencyName]", currency);
                    senderMsg = senderMsg.replace("[targetName]", args[1]);
                    senderMsg = senderMsg.replace("[sendAmount]", Double.toString(amountToSend));
                    sender.sendMessage(MessageFormat.format("{0} {1}", plugin.getMessagePrefix(), senderMsg));

                    String receiverMsg = plugin.getMessage("SendCurrencySuccessForReceiver");
                    receiverMsg = receiverMsg.replace("[currencyName]", currency);
                    receiverMsg = receiverMsg.replace("[senderName]", player.getDisplayName());
                    receiverMsg = receiverMsg.replace("[sendAmount]", Double.toString(amountToSend));
                    receiver.sendMessage(MessageFormat.format("{0} {1}", plugin.getMessagePrefix(), receiverMsg));
                    return true;
                } catch (Exception e) {
                    sender.sendMessage(MessageFormat.format("{0} {1}", plugin.getMessagePrefix(), plugin.getMessage("GenericError")));
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
                    sender.sendMessage(MessageFormat.format("{0} {1}", plugin.getMessagePrefix(), plugin.getMessage("MissingCurrencyInput")));
                    return false;
                }

                // Initialize variables
                String currency = args [1];

                // Check that the currency exists
                try {
                    if (!CurrencyOperations.validateCurrencyExists(currency)) {
                        String msg = plugin.getMessage("CurrencyDoesNotExist");
                        msg = msg.replace("[currencyName]", currency);
                        sender.sendMessage(MessageFormat.format("{0} {1}", plugin.getMessagePrefix(), msg));
                        return false;
                    }
                } catch (Exception e){
                    sender.sendMessage(MessageFormat.format("{0} {1}", plugin.getMessagePrefix(), plugin.getMessage("GenericError")));
                    e.printStackTrace();
                    return true;
                }

                //Gets balance and sends message to player
                try {
                    double balance = CurrencyOperations.getCurrencyBalance(plugin, player.getUniqueId().toString(), currency);
                    String msg = plugin.getMessage("Balance");
                    msg = msg.replace("[currencyName]", currency);
                    msg = msg.replace("[balance]", Double.toString(balance));
                    if(Objects.requireNonNull(plugin.getConfig().getString("Currency." + currency + ".symbol")).isBlank()){
                        msg = msg.replace("[currencySymbol]", "");
                    } else {
                        msg = msg.replace("[currencySymbol]", Objects.requireNonNull(plugin.getConfig().getString("Currency." + currency + ".symbol")));
                    }
                    sender.sendMessage(MessageFormat.format("{0} {1}", plugin.getMessagePrefix(), msg));
                } catch (Exception e) {
                    sender.sendMessage(MessageFormat.format("{0} {1}", plugin.getMessagePrefix(), plugin.getMessage("GenericError")));
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
