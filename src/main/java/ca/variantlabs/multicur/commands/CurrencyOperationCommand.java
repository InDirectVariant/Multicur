package ca.variantlabs.multicur.commands;

import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

public class CurrencyOperationCommand extends SimpleSubCommand {
    protected CurrencyOperationCommand(final SimpleCommandGroup parent) {
        super(parent, "send|bal|balance");

        setDescription("Commands for players to send and check their balance of a currency");
    }

    @Override
    protected void onCommand() {

    }
}
