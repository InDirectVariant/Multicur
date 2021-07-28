package ca.variantlabs.multicur.commands;

import ca.variantlabs.multicur.Multicur;
import ca.variantlabs.multicur.CurrencyOperations;
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
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        // Check to make sure the command sender is a player
        if(!(commandSender instanceof Player sender)){plugin.getLogger().info("ERROR: You must be a player to run this command!"); return false;}

        /*------------------------------------------------------------------------------------------------------------*/
        // Currency Send operations
        /*------------------------------------------------------------------------------------------------------------*/
        if(strings[0].equalsIgnoreCase("send")){
            // Check to see if they have the permission to run the command
            if(!sender.hasPermission("multicur.currency.send")){
                sender.sendMessage("No permission for that command!");
                plugin.getLogger().info(MessageFormat.format("{0} - No permission to run Currency Send command!", sender.getDisplayName()));
                return true;
            }
            // Check to make sure they put in a player to send currency too and an amount to send
            if(strings[1].isBlank()) {
                plugin.getLogger().info(
                        MessageFormat.format("INFO: {0} ran Currency Send command without a player name",
                                sender.getDisplayName()
                        )
                );
                commandSender.sendMessage("You must input a player to send currency too!"); return false;
            } else if(strings[2].isBlank()){
                plugin.getLogger().info(
                        MessageFormat.format("INFO: {0} ran Currency Send command without an amount",
                                sender.getDisplayName()
                        )
                );
                commandSender.sendMessage("You must input an amount of currency to send to the player!"); return false;
            }

            // Initialize player variable
            Player receiver = getPlayer(strings[0]);

            // Ensure receiver is not null
            if(receiver==null){
                plugin.getLogger().info("Receiver is Null");
                sender.sendMessage("Cannot send currency to that player");
                return false;
            }

            // Get the balance of the sender and how much they want to send
            try {
                double senderCurrency = Double.parseDouble(CurrencyOperations.getCurrency(plugin, sender.getUniqueId().toString()));
                double amntToSend = Double.parseDouble(strings[1]);
                // Check if the sender is sending more currency than they have available
                if(senderCurrency < amntToSend){sender.sendMessage("You cannot send more currency than you currently have!"); return true;}

                // Perform the operations to transfer currency
                try {
                    CurrencyOperations.removeCurrency(plugin, sender.getUniqueId().toString(), amntToSend);
                    CurrencyOperations.addCurrency(plugin, receiver.getUniqueId().toString(), amntToSend);
                } catch (Exception e){
                    plugin.getLogger().info(e.toString());
                    return true;
                }

                // Send messages to the players for the transaction
                sender.sendMessage(MessageFormat.format("You have sent {0} {1} currency!", receiver.getDisplayName(), amntToSend));
                receiver.sendMessage(MessageFormat.format("You have received {0} currency from {1}!", amntToSend, sender.getDisplayName()));

                return true;
            } catch(Exception e){
                sender.sendMessage("An error occurred, please contact an administrator!");
                plugin.getLogger().info(MessageFormat.format("{0} - Error occurred with MySQL operation to get Currency: {1}", sender.getDisplayName(), e));
                return true;
            }
        }

        /*------------------------------------------------------------------------------------------------------------*/
        // Currency balance commands
        /*------------------------------------------------------------------------------------------------------------*/
        else if (strings[0].equalsIgnoreCase("balance") || strings[0].equalsIgnoreCase("bal")){
            // Check to see if they have the permissions
            if(!sender.hasPermission("multicur.currency.balance")){
                sender.sendMessage("No permission for that command!");
                plugin.getLogger().info(MessageFormat.format("{0} - No permission to run Currency Send command!", sender.getDisplayName()));
                return true;
            }

            try {
                String balance = CurrencyOperations.getCurrency(plugin, sender.getUniqueId().toString());

                sender.sendMessage(MessageFormat.format("Your balance is {0}!", balance));
                plugin.getLogger().info(MessageFormat.format("{0}'s balance is {1}", sender.getDisplayName(), balance));
                return true;
            } catch(Exception e){
                sender.sendMessage("An error occurred, please contact an administrator!");
                plugin.getLogger().info(MessageFormat.format("{0} - Could not get the balance of {0} due to an error: {1}", sender.getDisplayName(), e));
                return true;
            }
        }

        // No input
        return false;
    }

}
