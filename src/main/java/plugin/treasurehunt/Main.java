package plugin.treasurehunt;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.treasurehunt.command.TreasureCommand;

public final class Main extends JavaPlugin  {

    @Override
    public void onEnable() {
        TreasureCommand treasureCommand = new TreasureCommand(this);
        Bukkit.getPluginManager().registerEvents(treasureCommand, this);
        getCommand("treasure").setExecutor(treasureCommand);
    }

}
