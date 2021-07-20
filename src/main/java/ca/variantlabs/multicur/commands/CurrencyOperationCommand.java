package ca.variantlabs.multicur.commands;

import org.mineacademy.fo.command.SimpleCommandGroup;

public class CurrencyOperationCommand {
    protected CurrencyOperationCommand(final SimpleCommandGroup parent) {
        super(parent, "send|bal|balance");

        setDescription("Hide the target player from you.");
    }
}
