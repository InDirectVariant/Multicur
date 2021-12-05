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

        //Checks that valid command was sent
        if (command.getName().equalsIgnoreCase("curadmin")) {

            /*------------------------------------------------------------------------------------------------------------*/
            // /curadmin give <player> <currency> <amount>
            /*------------------------------------------------------------------------------------------------------------*/
            if (args[0].equalsIgnoreCase("give")) {

                //Checks for valid permission for players
                if (sender instanceof Player && !Validate.validateHasPermission(plugin, sender, ((Player) sender).getDisplayName(), "multicur.admin.give", "curadmin give"))
                    return false;

                //Checks for valid command format
                if (sender instanceof Player && !Validate.validateSendGiveRemoveInputs(plugin, sender, ((Player) sender).getDisplayName(), args)) {
                    return false;
                }
                else if (!Validate.validateSendGiveRemoveInputs(plugin, sender, "Console", args)){
                    return false;
                }

                //Initialize variables
                Player receiver = getPlayer(args[1]);
                String currency = args[2];
                double amountToGive = Double.parseDouble(args[3]);

                // Check that the currency exists
                try {
                    if (!CurrencyOperations.validateCurrencyExists(currency)) {
                        sender.sendMessage(MessageFormat.format("{0} That currency does not exist", plugin.getMessagePrefix()));
                        return false;
                    }
                } catch (Exception e){
                    sender.sendMessage(MessageFormat.format("{0} An error occurred, see console for details!", plugin.getMessagePrefix()));
                    e.printStackTrace();
                    return true;
                }

                //Checks that receiver exists
                if (sender instanceof Player) {
                    if (!Validate.validateReceiverExistence(plugin, (Player) sender, receiver)) {
                        sender.sendMessage(MessageFormat.format("{0} That receiver does not exist!", plugin.getMessagePrefix()));
                        return false;
                    }
                } else {
                    if(!Validate.validateReceiverExistenceConsole(plugin, sender, receiver)) {
                        sender.sendMessage(MessageFormat.format("{0} That receiver does not exist!", plugin.getMessagePrefix()));
                        return false;
                    }
                }

                //Checks that player is sending a valid amount
                if (!Validate.validateGiveCurrencyAmount(sender, amountToGive)) {
                    sender.sendMessage(MessageFormat.format("{0} Please enter a valid currency amount!", plugin.getMessagePrefix()));
                    return false;
                }

                //Give currency to player
                try {
                    assert receiver != null;
                    CurrencyOperations.addCurrency(plugin, receiver.getUniqueId().toString(), currency, amountToGive);
                } catch (Exception e) {
                    sender.sendMessage(MessageFormat.format("{0} An error occurred, see console for details!", plugin.getMessagePrefix()));
                    plugin.getLogger().info(MessageFormat.format("{0} - Error occurred with MySQL operation to give Currency: {1}", sender.getName(), e));
                    return false;
                }

                //Send a message to both players that transaction was a success
                sender.sendMessage(MessageFormat.format("{0} You have sent {1} {2} to {3}!", plugin.getMessagePrefix(), amountToGive, currency, receiver.getDisplayName()));
                String senderName = "";
                if(sender instanceof Player) {
                    senderName = ((Player) sender).getDisplayName();
                } else {
                    senderName = "Console";
                }
                receiver.sendMessage(MessageFormat.format("{0} You have received {1} {2} from {3}", plugin.getMessagePrefix(), amountToGive, currency, senderName));
                return true;
            }

            /*------------------------------------------------------------------------------------------------------------*/
            // /currencyadmin remove <player> <currency> <amount>
            /*------------------------------------------------------------------------------------------------------------*/
            if (args[0].equalsIgnoreCase("remove")) {

                //Checks for valid permission for players
                if (sender instanceof Player && !Validate.validateHasPermission(plugin, sender, ((Player) sender).getDisplayName(), "multicur.admin.remove", "curadmin remove"))
                    return false;

                //Checks for valid command format
                if (sender instanceof Player && !Validate.validateSendGiveRemoveInputs(plugin, sender, ((Player) sender).getDisplayName(), args)) {
                    return false;
                }
                else if (!Validate.validateSendGiveRemoveInputs(plugin, sender, "Console", args)){
                    return false;
                }

                //Initialize variables
                Player victim = getPlayer(args[1]);
                String currency = args[2];
                double amountToRemove = Double.parseDouble(args[3]);

                // Check that the currency exists
                try {
                    if (!CurrencyOperations.validateCurrencyExists(currency)) {
                        sender.sendMessage(MessageFormat.format("{0} That currency does not exist!", plugin.getMessagePrefix()));
                        return false;
                    }
                } catch (Exception e){
                    sender.sendMessage(MessageFormat.format("{0} An error occurred, see console for details!", plugin.getMessagePrefix()));
                    e.printStackTrace();
                    return true;
                }

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
                    assert victim != null;
                    if(!CurrencyOperations.removeCurrency(plugin, victim.getUniqueId().toString(), currency, amountToRemove)){
                        sender.sendMessage(MessageFormat.format("{0} Please input a valid currency amount!", plugin.getMessagePrefix()));
                        plugin.getLogger().info("Cannot remove more than the player's balance!");
                        return true;
                    }
                } catch (Exception e) {
                    sender.sendMessage(MessageFormat.format("{0} &cAn error occurred, please contact an administrator!", plugin.getMessagePrefix()));
                    plugin.getLogger().info(MessageFormat.format("{0} - Error occurred with MySQL operation to remove Currency: {1}", "Console", e));
                    return true;
                }

                //Sends messages to both players that transaction was a success
                sender.sendMessage(MessageFormat.format("{0} &cYou have removed {1} currency from {2}!", plugin.getMessagePrefix(), amountToRemove, victim.getDisplayName()));
                victim.sendMessage(MessageFormat.format("{0} &cYou had {1} currency removed!", plugin.getMessagePrefix(), amountToRemove));

                return true;
            }

            /*------------------------------------------------------------------------------------------------------------*/
            // /currencyadmin balance <player> <currency>
            /*------------------------------------------------------------------------------------------------------------*/
            else if (args[0].equalsIgnoreCase("bal") || args[0].equalsIgnoreCase("balance")) {

                //Checks for valid permission
                if (!Validate.validateHasPermission(plugin, sender, "Console", "multicur.admin.balance", "curadmin balance"))
                    return false;

                //Check inputs
                if(sender instanceof Player && !Validate.validateAdminBalanceInputs(plugin, sender, ((Player) sender).getDisplayName(), args)){
                    return false;
                } else{
                    if (!Validate.validateAdminBalanceInputs(plugin, sender, "Console", args)){
                        return false;
                    }
                }

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

                //Check for currency
                if(args.length != 3 ){
                    sender.sendMessage(MessageFormat.format("{0} &cYou must input a currency name to check!", plugin.getMessagePrefix()));
                    return false;
                }

                //Initialize variables
                String currency = args[2];

                // Check that the currency exists
                try {
                    if (!CurrencyOperations.validateCurrencyExists(currency)) {
                        sender.sendMessage(MessageFormat.format("{0} &cCurrency with name {1} does not exist!", plugin.getMessagePrefix(), currency));
                        return false;
                    }
                } catch (Exception e){
                    sender.sendMessage(MessageFormat.format("{0} &cError occurred. Contact an admin", plugin.getMessagePrefix()));
                    e.printStackTrace();
                    return true;
                }

                //Sends message with balance
                try {
                    assert playerToFind != null;
                    sender.sendMessage(
                            MessageFormat.format(
                                    "{0} &cBalance of {1}: {2}",
                                    plugin.getMessagePrefix(),
                                    playerToFind.getDisplayName(),
                                    CurrencyOperations.getCurrencyBalance(plugin, playerToFind.getUniqueId().toString(), currency)
                            )
                    );
                    plugin.getLogger().info(MessageFormat.format("{0} - Viewed {1}'s balance!", "Console", playerToFind.getDisplayName()));
                } catch (Exception e) {
                    sender.sendMessage(MessageFormat.format("{0} &cCould not get the balance of {1} due to error: {2}", plugin.getMessagePrefix(), playerToFind.getDisplayName(), e));
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
