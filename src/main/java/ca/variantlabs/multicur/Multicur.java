package ca.variantlabs.multicur;

import ca.variantlabs.multicur.commands.CurrencyCommand;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.plugin.SimplePlugin;

public class Multicur extends SimplePlugin {

    @Override
    protected void onPluginStart() {
        Common.log("Multicur starting up...");

        //Register Commands
        registerCommands("currency|curr", new CurrencyCommand());
    }
}
