package lol.hyper.replantpls.mcmmo;

import com.gmail.nossr50.api.ExperienceAPI;
import lol.hyper.replantpls.ReplantPls;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MCMMOHook {

    private final ReplantPls replantPls;
    private final Map<String, Integer> cropExperience = new HashMap<>();

    public MCMMOHook(ReplantPls replantPls) {
        this.replantPls = replantPls;
    }

    public void setup() {
        Plugin mcMMO = Bukkit.getPluginManager().getPlugin("mcMMO");
        if (mcMMO == null) {
            replantPls.getLogger().severe("No mcMMO plugin found after trying to load it.");
            replantPls.getLogger().severe("This is checked after the first check, so this message should never appear...");
            return;
        }

        // load the experience config to get the values for each crop
        File experienceConfigFile = new File(mcMMO.getDataFolder(), "experience.yml");
        YamlConfiguration experienceConfig = YamlConfiguration.loadConfiguration(experienceConfigFile);
        ConfigurationSection herbalismExperience = experienceConfig.getConfigurationSection("Experience_Values.Herbalism");
        if (herbalismExperience == null) {
            replantPls.getLogger().severe("Unable to find Experience_Values.Herbalism section in your mcMMO experience.yml configuration.");
            return;
        }

        // load the exp from the crops
        int wheatExp = herbalismExperience.getInt("Wheat");
        int beetRootsExp = herbalismExperience.getInt("Beetroot");
        int carrotsExp = herbalismExperience.getInt("Carrots");
        int potatoesExp = herbalismExperience.getInt("Potatoes");
        int netherWartExp = herbalismExperience.getInt("Nether_Wart");
        int torchFlowerExp = herbalismExperience.getInt("Torchflower");
        int pitcherExp = herbalismExperience.getInt("Pitcher_Plant");

        cropExperience.put("WHEAT", wheatExp);
        cropExperience.put("BEETROOTS", beetRootsExp);
        cropExperience.put("CARROTS", carrotsExp);
        cropExperience.put("POTATOES", potatoesExp);
        cropExperience.put("NETHER_WART", netherWartExp);
        cropExperience.put("TORCHFLOWER_CROP", torchFlowerExp);
        cropExperience.put("PITCHER_CROP", pitcherExp);
    }

    /**
     * Add Herbalism EXP to the given player.
     *
     * @param player The player.
     * @param crop   The crop the player broke.
     */
    public void addExp(Player player, String crop) {
        ExperienceAPI.addXP(player, "Herbalism", cropExperience.get(crop), "UNKNOWN");
    }
}
