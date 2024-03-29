package com.x32pc.github.manager;

import com.x32pc.github.GBank;
import com.x32pc.github.api.API;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CurrencyManager implements API {

    private final GBank gBank;
    private final ConfigurationSection currenciesConfig;

    public CurrencyManager(GBank main) {
        this.gBank = main;
        this.currenciesConfig = gBank.getConfig().getConfigurationSection("currencies");
        int intervalSeconds = gBank.getConfig().getInt("give-currency.seconds");
        if(gBank.getConfig().getBoolean("give-currency.enabled")) {
            Bukkit.getScheduler().runTaskTimer(main, this::addCoins, 0, intervalSeconds * 20L);
        }
    }

    private void addCoins() {
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            String uuid = player.getUniqueId().toString();
            List<String> currencies = gBank.currencyManager.getAllCurrencies();
            double amountToAdd = gBank.getConfig().getDouble("give-currency.amount");
            if(gBank.dataManager.getIsYML()) {
                File playerFile = new File(gBank.getDataFolder() + File.separator + "data", uuid + ".yml");
                YamlConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);
                for (String currency : currencies) {
                    double currentAmount = playerData.getDouble("currencies." + currency, 0);
                    ;
                    double newAmount = currentAmount + amountToAdd;
                    playerData.set("currencies." + currency, newAmount);
                }
                    try {
                        playerData.save(playerFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            } else {
                for (String currency : currencies) {
                    double currentAmount = gBank.dataManager.getCurrencyValue(uuid, currency);
                    double newAmount = currentAmount + amountToAdd;
                    gBank.dataManager.setCurrencyValue(uuid, currency, newAmount);
                }
            }
        }
    }

    public String getCurrencyName(String key) {
        return currenciesConfig.getString(key + ".name");
    }

    public String getCurrencyPrefix(String key) {
        return currenciesConfig.getString(key + ".prefix");
    }
    public String getCurrencyNameColor(String key) {
        return ChatColor.translateAlternateColorCodes('&', currenciesConfig.getString(key + ".name"));
    }

    public List<String> getAllCurrencies() {
        Set<String> currencyKeys = currenciesConfig.getKeys(false);
        return new ArrayList<>(currencyKeys);
    }

    @Override
    public double getAmountCurrency(String playeruuid, String currency) {
        if(gBank.dataManager.getIsYML()) {
            return gBank.dataCreateEvent.getCurrencyAmount(playeruuid, currency);
        } else {
            return gBank.dataManager.getCurrencyValue(playeruuid, currency);
        }
    }

    public void setAmountCurrency(String playeruuid, String currency, double amount) {
        if(gBank.dataManager.getIsYML()) {
            gBank.dataCreateEvent.setCurrencyAmount(playeruuid, currency, amount);
        } else {
            gBank.dataManager.setCurrencyValue(playeruuid, currency, amount);
        }
    }

    public void giveAmountCurrency(String playeruuid, String currency, double amount) {
        if(gBank.dataManager.getIsYML()) {
            gBank.dataCreateEvent.giveCurrencyAmount(playeruuid, currency, amount);
        } else {
            gBank.dataManager.giveCurrency(playeruuid, currency, amount);
        }
    }

    public void takeAmountCurrency(String playeruuid, String currency, double amount) {
        if(gBank.dataManager.getIsYML()) {
            double has = gBank.dataCreateEvent.getCurrencyAmount(playeruuid, currency);
            gBank.dataCreateEvent.setCurrencyAmount(playeruuid, currency, has-amount);
        } else {
            gBank.dataManager.takeCurrency(playeruuid, currency, amount);
        }
    }
}
