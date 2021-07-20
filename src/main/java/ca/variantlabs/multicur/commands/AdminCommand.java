package ca.variantlabs.multicur.commands;

import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

public class AdminCommand extends SimpleSubCommand {

    protected AdminCommand(final SimpleCommandGroup parent) {
        super(parent, "admin");

        setDescription("Administration commands for currencies and Multicur");
    }

    @Override
    protected void onCommand() {

    }
}
