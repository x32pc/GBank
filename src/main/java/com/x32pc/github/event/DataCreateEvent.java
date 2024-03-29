package com.x32pc.github.event;

import com.x32pc.github.GBank;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DataCreateEvent implements Listener {

    private final GBank gBank;

    public DataCreateEvent(GBank main) {
        this.gBank = main;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        if(gBank.dataManager.getIsYML()) {
            String uuid = event.getPlayer().getUniqueId().toString();

            File dataFolder = new File(gBank.getDataFolder(), "data");
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
            File playerFile = new File(gBank.getDataFolder() + File.separator + "data", uuid + ".yml");
            if (!playerFile.exists()) {
                try {
                    playerFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
            YamlConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);

            List<String> currencies = gBank.currencyManager.getAllCurrencies();
            for (String currency : currencies) {
                if (!playerData.contains("currencies." + currency)) {
                    playerData.set("currencies." + currency, 0);
                }
            }

            try {
                playerData.save(playerFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            gBank.dataManager.insertInitialCurrencies(event.getPlayer().getUniqueId().toString());
        }

    }

    public void setCurrencyAmount(String playerUUID, String currency, double amount) {
        File playerFile = new File(gBank.getDataFolder() + File.separator + "data", playerUUID + ".yml");
        if (playerFile.exists()) {
            YamlConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);
            playerData.set("currencies." + currency, amount);
            try {
                playerData.save(playerFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void giveCurrencyAmount(String playerUUID, String currency, double toAdd) {
            double old = gBank.dataCreateEvent.getCurrencyAmount(playerUUID, currency);
            double newAmount = old+toAdd;
            setCurrencyAmount(playerUUID, currency, newAmount);
    }

    public double getCurrencyAmount(String playerUUID, String currency) {
        File playerFile = new File(gBank.getDataFolder() + File.separator + "data", playerUUID + ".yml");
        if (playerFile.exists()) {
            YamlConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);
            return playerData.getDouble("currencies." + currency);
        }
        return 0;
    }
}