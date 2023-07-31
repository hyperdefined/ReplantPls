package lol.hyper.rightgrow;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
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
        }
    }

    private void handleEvent(Block plant, Material seeds, Material farmLand, Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack heldItem = inventory.getItemInMainHand();

        // make sure we hold the right seeds
        if (heldItem.getType() != seeds) {
            return;
        }

        // make sure the plant is ready to harvest
        Ageable originalPlant = (Ageable) plant.getBlockData();
        if (originalPlant.getAge() != originalPlant.getMaximumAge()) {
            return;
        }

        // break the plant
        plant.breakNaturally();

        // make sure the block under the plant is farmland
        Location wheatLocation = plant.getLocation();
        Block dirt = player.getWorld().getBlockAt(wheatLocation.subtract(0, 1, 0));
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
    }
}
