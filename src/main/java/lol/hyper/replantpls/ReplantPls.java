package lol.hyper.replantpls;

import lol.hyper.hyperlib.HyperLib;
import lol.hyper.hyperlib.bstats.HyperStats;
import lol.hyper.hyperlib.releases.HyperUpdater;
import lol.hyper.replantpls.mcmmo.MCMMOHook;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class ReplantPls extends JavaPlugin {

    private final Logger logger = this.getLogger();
    private MCMMOHook hook;

    @Override
    public void onEnable() {
        HyperLib hyperLib = new HyperLib(this);
        hyperLib.setup();

        HyperStats stats = new HyperStats(hyperLib, 24167);
        stats.setup();

        if (Bukkit.getPluginManager().isPluginEnabled("mcMMO")) {
            logger.info("mcMMO is detected, enabling support for it!");
            hook = new MCMMOHook(this);
            hook.setup();
        } else {
            hook = null;
        }

        Bukkit.getPluginManager().registerEvents(new PlayerInteract(this), this);

        HyperUpdater updater = new HyperUpdater(hyperLib);
        updater.setGitHub("hyperdefined", "ReplantPls");
        updater.setModrinth("qw91hiLm");
        updater.setHangar("ReplantPls", "paper");
        updater.check();
    }

    public MCMMOHook getMCMMOHook() {
        return hook;
    }
}
