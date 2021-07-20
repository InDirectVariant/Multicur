package ca.variantlabs.multicur.commands;

import org.mineacademy.fo.command.SimpleCommandGroup;

public class CurrencyCommand extends SimpleCommandGroup {

    @Override
    protected void registerSubcommands() {
        registerSubcommand(new AdminCommand(this));
        registerSubcommand(new CurrencyOperationCommand(this));
    }

    @Override
    protected String getCredits(){
        return "Multicur developed by Variant__";
    }
}
