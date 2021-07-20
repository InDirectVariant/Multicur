package ca.variantlabs.multicur.commands;

import ca.variantlabs.multicur.Multicur;
import ca.variantlabs.multicur.Currency;
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

        // Initialize player variables
        Player receiver = getPlayer(strings[0]);

        // Ensure receiver is not null
        if(receiver==null){
            plugin.getLogger().info("Receiver is Null");
            sender.sendMessage("Cannot send currency to that player");
            return false;
        }

        // Currency Send operations
        if(command.getName().equalsIgnoreCase("currency send")){
            // Check to make sure they put in a player to send currency too and an amount to send
            if(strings[0].isBlank()) {
                plugin.getLogger().info(
                        MessageFormat.format("INFO: {0} ran Currency Send command without a player name",
                                sender.getDisplayName()
                        )
                );
                commandSender.sendMessage("You must input a player to send currency too!"); return false;
            } else if(strings[1].isBlank()){
                plugin.getLogger().info(
                        MessageFormat.format("INFO: {0} ran Currency Send command without an amount",
                                sender.getDisplayName()
                        )
                );
                commandSender.sendMessage("You must input an amount of currency to send to the player!"); return false;
            }

            // Get the balance of the sender and how much they want to send
            double senderCurrency = Currency.getCurrency(sender.getUniqueId());
            double amntToSend = Double.parseDouble(strings[1]);

            // Check if the sender is sending more currency than they have available
            if(senderCurrency < amntToSend){sender.sendMessage("You cannot send more currency than you currently have!"); return false;}

            // Perform the operations to transfer currency
            try {
                Currency.removeCurrency(sender.getUniqueId(), amntToSend);
                Currency.addCurrency(receiver.getUniqueId(), amntToSend);
            } catch (Exception e){
                plugin.getLogger().info(e.toString());
                return false;
            }

            // Send messages to the players for the transaction
            sender.sendMessage(MessageFormat.format("You have sent {0} {1} currency!", receiver.getDisplayName(), amntToSend));
            receiver.sendMessage(MessageFormat.format("You have received {0} currency from {1}!", amntToSend, sender.getDisplayName()));

            return true;
        } else if (command.getName().equalsIgnoreCase("currency balance") || command.getName().equalsIgnoreCase("currency bal")){
            sender.sendMessage(MessageFormat.format("Your balance is {0}!", Currency.getCurrency(sender.getUniqueId())));
            plugin.getLogger().info(MessageFormat.format("{0}'s balance is {1}", sender.getDisplayName(), Currency.getCurrency(sender.getUniqueId())));
            return true;
        }

        return false;
    }

}
