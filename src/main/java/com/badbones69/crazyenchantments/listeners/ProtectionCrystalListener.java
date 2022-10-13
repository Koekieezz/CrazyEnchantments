package com.badbones69.crazyenchantments.listeners;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.api.FileManager.Files;
import com.badbones69.crazyenchantments.api.enums.Messages;
import com.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ProtectionCrystalListener implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Methods methods = plugin.getStarter().getMethods();

    private final HashMap<UUID, List<ItemStack>> playersItems = new HashMap<>();
    private ItemBuilder crystal;
    private String protectionString;

    public void loadProtectionCrystal() {
        FileConfiguration config = Files.CONFIG.getFile();
        crystal = new ItemBuilder()
        .setMaterial(config.getString("Settings.ProtectionCrystal.Item"))
        .setName(config.getString("Settings.ProtectionCrystal.Name"))
        .setLore(config.getStringList("Settings.ProtectionCrystal.Lore"))
        .setGlow(config.getBoolean("Settings.ProtectionCrystal.Glowing"));
        protectionString = methods.color(Files.CONFIG.getFile().getString("Settings.ProtectionCrystal.Protected"));
    }

    public ItemStack getCrystals() {
        return getCrystals(1);
    }

    public ItemStack getCrystals(int amount) {
        return crystal.copy().setAmount(amount).build();
    }

    public boolean isProtected(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            for (String lore : item.getItemMeta().getLore()) {
                if (lore.contains(protectionString)) return true;
            }
        }

        return false;
    }

    /**
     * @deprecated use {@link ProtectionCrystalListener#isProtectionSuccessful}.
     */
    @Deprecated(forRemoval = true)
    public boolean isSuccessful(Player player) {
        return isProtectionSuccessful(player);
    }

    public boolean isProtectionSuccessful(Player player) {
        if (player.hasPermission("crazyenchantments.bypass.protectioncrystal")) return true;

        FileConfiguration config = Files.CONFIG.getFile();

        if (config.getBoolean("Settings.ProtectionCrystal.Chance.Toggle")) return methods.randomPicker(config.getInt("Settings.ProtectionCrystal.Chance.Success-Chance", 100), 100);

        return true;
    }

    public ItemStack removeProtection(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<>(itemMeta.getLore());
        lore.remove(protectionString);
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        if (e.getInventory() != null) {
            ItemStack crystalItem = e.getCursor() != null ? e.getCursor() : new ItemStack(Material.AIR); // The Crystal.
            ItemStack item = e.getCurrentItem() != null ? e.getCurrentItem() : new ItemStack(Material.AIR); // The item your adding the protection to.
            if (item.getType() != Material.AIR && crystalItem.getType() != Material.AIR &&
            // The item getting protected is not stacked.
            item.getAmount() == 1 &&
            // Making sure they are not dropping crystals on top of other crystals.
            !getCrystals().isSimilar(item) && crystalItem.isSimilar(getCrystals()) &&
            // The item does not have protection on it.
            !isProtected(item)) {
                // The crystal is not stacked.

                if (crystalItem.getAmount() > 1) {
                    player.sendMessage(Messages.NEED_TO_UNSTACK_ITEM.getMessage());
                    return;
                }

                e.setCancelled(true);
                player.setItemOnCursor(methods.removeItem(crystalItem));
                e.setCurrentItem(methods.addLore(item, protectionString));
                player.updateInventory();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent e) {

        if (e.getKeepInventory()) return;

        Player player = e.getEntity();
        List<ItemStack> savedItems = new ArrayList<>();
        List<ItemStack> droppedItems = new ArrayList<>();

        for (ItemStack item : e.getDrops()) {

            if (item != null) {

                if (isProtected(item) && isProtectionSuccessful(player)) {
                    savedItems.add(item);
                    continue;
                }

                droppedItems.add(item);
            }
        }

        e.getDrops().clear();
        e.getDrops().addAll(droppedItems);
        playersItems.put(player.getUniqueId(), savedItems);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();

        if (playersItems.containsKey(player.getUniqueId())) {

            // If the config does not have the option then it will lose the protection by default.
            if (Files.CONFIG.getFile().getBoolean("Settings.ProtectionCrystal.Lose-Protection-On-Death", true)) {
                for (ItemStack item : playersItems.get(player.getUniqueId())) {
                    player.getInventory().addItem(removeProtection(item));
                }
            } else {
                for (ItemStack item : playersItems.get(player.getUniqueId())) {
                    player.getInventory().addItem(item);
                }
            }

            playersItems.remove(player.getUniqueId());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCrystalClick(PlayerInteractEvent e) {
        ItemStack item = methods.getItemInHand(e.getPlayer());

        if (item.isSimilar(getCrystals())) e.setCancelled(true);
    }
}