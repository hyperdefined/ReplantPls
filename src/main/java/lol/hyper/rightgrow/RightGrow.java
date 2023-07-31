package lol.hyper.rightgrow;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class RightGrow extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new PlayerInteract(), this);
    }
}
