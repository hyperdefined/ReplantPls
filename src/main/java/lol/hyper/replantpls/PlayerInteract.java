package lol.hyper.replantpls;

import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.PitcherCrop;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerInteract implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        Material blockType = block.getType();
        switch (blockType) {
            case WHEAT: {
                handleEvent(block, Material.WHEAT_SEEDS, Material.FARMLAND, player);
                break;
            }
            case BEETROOTS: {
                handleEvent(block, Material.BEETROOT_SEEDS, Material.FARMLAND, player);
                break;
            }
            case CARROTS: {
                handleEvent(block, Material.CARROT, Material.FARMLAND, player);
                break;
            }
            case POTATOES: {
                handleEvent(block, Material.POTATO, Material.FARMLAND, player);
                break;
            }
            case NETHER_WART: {
                handleEvent(block, Material.NETHER_WART, Material.SOUL_SAND, player);
                break;
            }
            case TORCHFLOWER_CROP: {
                handleEvent(block, Material.TORCHFLOWER_SEEDS, Material.FARMLAND, player);
                break;
            }
            case PITCHER_CROP: {
                handleEvent(block, Material.PITCHER_POD, Material.FARMLAND, player);
                break;
            }
        }
    }

    /**
     * Handle PlayerInteractEvent event.
     *
     * @param plant    The plant block.
     * @param seeds    The type of seeds the player is holding.
     * @param farmLand The block below the plant.
     * @param player   The player activating this event.
     */
    private void handleEvent(Block plant, Material seeds, Material farmLand, Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack heldItem = inventory.getItemInMainHand();
        Location plantLocation = plant.getLocation();

        // make sure we hold the right seeds
        if (heldItem.getType() != seeds) {
            return;
        }

        // make sure the plant is ready to harvest
        Ageable originalPlant = (Ageable) plant.getBlockData();
        if (originalPlant.getAge() != originalPlant.getMaximumAge()) {
            return;
        }

        switch (plant.getType()) {
            case WHEAT:
            case BEETROOTS:
            case CARROTS:
            case POTATOES:
            case NETHER_WART:
            case TORCHFLOWER_CROP: {
                // break the plant
                plant.breakNaturally();

                // make sure the block under the plant is farmland
                Block dirt = player.getWorld().getBlockAt(plantLocation.subtract(0, 1, 0));
                if (dirt.getType() != farmLand) {
                    return;
                }

                // clone the block data, set the age to 0
                // set the new block to the new plant
                Ageable newPlant = (Ageable) originalPlant.clone();
                newPlant.setAge(0);
                plant.setType(plant.getType());
                plant.setBlockData(newPlant);

                // subtract the seeds in hand
                if (player.getGameMode() != GameMode.CREATIVE) {
                    inventory.getItemInMainHand().setAmount(heldItem.getAmount() - 1);
                }
                break;
            }
            case PITCHER_CROP: {
                PitcherCrop pitcherCrop = (PitcherCrop) plant.getBlockData();
                if (pitcherCrop.getHalf() == Bisected.Half.TOP) {
                    // set the block to replant/break to be the lower part
                    // if the player right-clicks the top, it will not replant
                    plant = player.getWorld().getBlockAt(plantLocation.subtract(0, 1, 0));
                }

                plant.breakNaturally();

                // make sure the block under the plant is farmland
                Block dirt = player.getWorld().getBlockAt(plantLocation.subtract(0, 1, 0));
                if (dirt.getType() != farmLand) {
                    return;
                }

                PitcherCrop newPlant = (PitcherCrop) pitcherCrop.clone();
                newPlant.setAge(0);
                newPlant.setHalf(Bisected.Half.BOTTOM);
                plant.setType(plant.getType());
                plant.setBlockData(newPlant);

                // subtract the seeds in hand
                if (player.getGameMode() != GameMode.CREATIVE) {
                    inventory.getItemInMainHand().setAmount(heldItem.getAmount() - 1);
                }
                break;
            }
        }
    }
}
