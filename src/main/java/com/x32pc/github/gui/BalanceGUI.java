package com.x32pc.github.gui;

import com.x32pc.github.GBank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BalanceGUI implements Listener {

    private final int pageSize = 7;

    private final GBank gBank;

    public BalanceGUI(GBank main) {
        this.gBank = main;
    }

    public void openCurrencyGUI(Player player, int page) {

        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, gBank.currencyManager.getAllCurrencies().size());

        Inventory gui = Bukkit.createInventory(player, 27, "Currency GUI - Page " + (page + 1));

        for (int i = startIndex; i < endIndex; i++) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta itemMeta = item.getItemMeta();

            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&'," &l" + gBank.currencyManager.getAllCurrencies().get(i)));
            String lore = ChatColor.translateAlternateColorCodes('&',("&fBalance of " + gBank.currencyManager.getAllCurrencies().get(i) + ": " + gBank.currencyManager.getAmountCurrency(player.getUniqueId().toString(), gBank.currencyManager.getAllCurrencies().get(i))));
                    itemMeta.setLore(Collections.singletonList(lore));
            item.setItemMeta(itemMeta);
            gui.setItem(i - startIndex + 10, item);
        }

        if (page > 0) {
            ItemStack backButton = new ItemStack(Material.ARROW);
            ItemMeta backButtonMeta = backButton.getItemMeta();
            backButtonMeta.setDisplayName(ChatColor.YELLOW + "Back");
            backButton.setItemMeta(backButtonMeta);
            gui.setItem(18, backButton);
        }

        if (endIndex < gBank.currencyManager.getAllCurrencies().size()) {
            ItemStack nextButton = new ItemStack(Material.ARROW);
            ItemMeta nextButtonMeta = nextButton.getItemMeta();
            nextButtonMeta.setDisplayName(ChatColor.GREEN + "Next");
            nextButton.setItemMeta(nextButtonMeta);
            gui.setItem(26, nextButton);
        }

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
            Player player = (Player) event.getWhoClicked();
            int currentPage = Integer.parseInt(event.getView().getTitle().split(" ")[4]) - 1;
            if(event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.PAPER) {
                event.setCancelled(true);
            }

            if (event.getRawSlot() == 18 && currentPage > 0) {
                openCurrencyGUI(player, currentPage - 1);
            } else if (event.getRawSlot() == 26 && currentPage < (gBank.currencyManager.getAllCurrencies().size() - 1) / pageSize) {
                openCurrencyGUI(player, currentPage + 1);
            }
    }
}
