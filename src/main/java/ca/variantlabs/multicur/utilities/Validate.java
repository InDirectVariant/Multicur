package ca.variantlabs.multicur.utilities;

import ca.variantlabs.multicur.Multicur;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.text.MessageFormat;

public class Validate {

    public static boolean validateIsPlayer(Multicur plugin, CommandSender commandSender) {
        if(!(commandSender instanceof Player)) {
            plugin.getLogger().warning("You must be a player to run this command!");
            return false;
        }
        else
            return true;
    }

    public static boolean validateHasPermission(Multicur plugin, CommandSender sender, String displayName, String permission, String command) {
        if(!sender.hasPermission(permission)) {
            sender.sendMessage("No permission for that command!");
            plugin.getLogger().info(MessageFormat.format("{0} - No permission to run {1} command!", displayName, command));
            return false;
        }
        else
            return true;
    }

    public static boolean validateSendGiveRemoveInputs(Multicur plugin, CommandSender sender, String displayName, String[] args) {
        if(args[1].isBlank()) {
            plugin.getLogger().info(MessageFormat.format("INFO: {0} ran {1} command without a player name", displayName, args[0]));
            sender.sendMessage("You must input a player to {0} currency to/from!", args[0]);
            return false;
        } else if(args[2].isBlank()){
            plugin.getLogger().info(MessageFormat.format("INFO: {0} ran {1} command without a specified currency", displayName, args[0]));
            sender.sendMessage("You must input the currency you are {0} the player!", args[0]);
            return false;
        } else if(args[3].isBlank()){
            plugin.getLogger().info(MessageFormat.format("INFO: {0} ran {1] command without an amount", displayName, args[0]));
            sender.sendMessage("You must input an amount of currency to {0} to/from the player!", args[0]);
            return false;
        }
        else
            return true;
    }

    public static boolean validateAdminBalanceInputs(Multicur plugin, CommandSender sender, String displayName, String[] args){
        if(args[1].isBlank()){
            plugin.getLogger().info(MessageFormat.format("INFO: {0} ran balance command without a player name!", displayName));
            sender.sendMessage("You must input a player to check their balance!");
            return false;
        } else if(args[2].isBlank()){
            plugin.getLogger().info(MessageFormat.format("INFO: {0} ran balance command with a currency name!", displayName));
            sender.sendMessage("You must input a currency name to check a balance!");
            return false;
        }
        else
            return true;
    }

    public static boolean validateReceiverExistence(Multicur plugin, Player player, Player receiver) {
        if (receiver == null) {
            plugin.getLogger().info("Player does not exist");
            player.sendMessage("Specified player does not exist!");
            return false;
        }
        else
            return true;
    }

    public static boolean validateReceiverExistenceConsole(Multicur plugin, CommandSender sender, Player receiver) {
        if (receiver == null) {
            plugin.getLogger().info("Player does not exist");
            sender.sendMessage("Specified player does not exist!");
            return false;
        }
        else
            return true;
    }

    public static boolean validateSendCurrencyAmount(CommandSender sender, Double amountToSend, Double senderBalance) {

        //Checks that player is not sending 0 or less currency
        if(amountToSend <= 0) {
            sender.sendMessage("You can not send 0 or less currency!");
            return false;
        }

        //Checks that player has enough currency to send
        if(senderBalance < amountToSend) {
            sender.sendMessage("You can not send more currency than you currently have!");
            return false;
        }
        else
            return true;
    }

    public static boolean validatePlayerNotNull (Multicur plugin, CommandSender sender, String displayName, String[] args) {

        // Check to see if there was a player added
        if(args[1].isEmpty()) {
            sender.sendMessage("You must specify a player!");
            plugin.getLogger().info(MessageFormat.format("{0} - Did not specify a player!", displayName));
            return false;
        }
        else
            return true;
    }
}
