package lol.hyper.replantpls;

import lol.hyper.hyperlib.HyperLib;
import lol.hyper.hyperlib.bstats.bStats;
import lol.hyper.hyperlib.releases.modrinth.ModrinthPlugin;
import lol.hyper.hyperlib.releases.modrinth.ModrinthRelease;
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

        bStats bstats = new bStats(hyperLib, 24167);
        bstats.setup();

        if (Bukkit.getPluginManager().isPluginEnabled("mcMMO")) {
            logger.info("mcMMO is detected, enabling support for it!");
            hook = new MCMMOHook(this);
            hook.setup();
        } else {
            hook = null;
        }

        Bukkit.getPluginManager().registerEvents(new PlayerInteract(this), this);

        Bukkit.getAsyncScheduler().runNow(this, scheduledTask -> {
            ModrinthPlugin modrinthPlugin = new ModrinthPlugin("qw91hiLm");
            modrinthPlugin.get();

            ModrinthRelease release = modrinthPlugin.getReleaseByVersion(this.getPluginMeta().getVersion());
            if (release == null) {
                logger.warning("You are running a version not published.");
            } else {
                int buildsBehind = modrinthPlugin.buildsVersionsBehind(release);
                if (buildsBehind > 0) {
                    ModrinthRelease latest = modrinthPlugin.getLatestRelease();
                    if (latest != null) {
                        logger.info("You are " + buildsBehind + " versions behind. Please update!");
                        logger.info("The latest version is " + latest.getVersion());
                        logger.info(latest.getVersionPage());
                    }
                } else {
                    logger.info("Yay! You are running the latest version.");
                }
            }
        });
    }

    public MCMMOHook getMCMMOHook() {
        return hook;
    }
}
