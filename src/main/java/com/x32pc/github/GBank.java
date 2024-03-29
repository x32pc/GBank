package com.x32pc.github;

import com.x32pc.github.api.API;
import com.x32pc.github.api.APIImplementation;
import com.x32pc.github.commands.pay.PayCommand;
import com.x32pc.github.commands.balance.BalanceCommand;
import com.x32pc.github.commands.balance.TabBalanceCompleter;
import com.x32pc.github.commands.bank.TabBankCompleter;
import com.x32pc.github.commands.bank.BankCommand;
import com.x32pc.github.commands.bank.BankGive;
import com.x32pc.github.commands.bank.BankSet;
import com.x32pc.github.commands.bank.BankTake;
import com.x32pc.github.commands.pay.TabPayCompleter;
import com.x32pc.github.event.DataCreateEvent;
import com.x32pc.github.gui.BalanceGUI;
import com.x32pc.github.manager.*;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class GBank extends JavaPlugin{

    public BalanceCommand balanceCommand;
    public BankCommand bankCommand;
    public PayCommand payCommand;
    public LanguageManager languageManager;
    public CurrencyManager currencyManager;
    public TabBalanceCompleter tabBalanceCompleter;
    public DataCreateEvent dataCreateEvent;
    public BalanceGUI balanceGUI;
    public BankGive bankGive;
    public BankSet bankSet;
    public BankTake bankTake;
    public TabBankCompleter tabBankCompleter;
    public OfflineManager offlineManager;
    public TabPayCompleter tabPayCompleter;
    public DataManager dataManager;
    public APIImplementation apiImplementation;
    @Override
    public void onEnable() {
        saveDefaultConfig();
        try {
            registerFiles();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        registerEvents();
        registerCommands();
        languageManager.createLanguageFile();

        getServer().getServicesManager().register(API.class, new APIImplementation(this), this, ServicePriority.Normal);
        getLogger().info("GBank API registered successfully.");
    }

    @Override
    public void onDisable() {
        try {
            dataManager.disconnect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    String host = getConfig().getString("database.host");
    String database = getConfig().getString("database.database");
    String username = getConfig().getString("database.username");
    String password = getConfig().getString("database.password");


    public void registerFiles() throws SQLException {
        balanceCommand = new BalanceCommand(this);
        bankCommand = new BankCommand(this);
        payCommand = new PayCommand(this);
        languageManager = new LanguageManager(this);
        currencyManager = new CurrencyManager(this);
        tabBalanceCompleter = new TabBalanceCompleter(this);
        dataCreateEvent = new DataCreateEvent(this);
        balanceGUI = new BalanceGUI(this);
        bankGive = new BankGive(this);
        bankSet = new BankSet(this);
        bankTake = new BankTake(this);
        tabBankCompleter = new TabBankCompleter(this);
        offlineManager = new OfflineManager(this);
        tabPayCompleter = new TabPayCompleter(this);
        dataManager = new DataManager(this, host, database, username, password);
        apiImplementation = new APIImplementation(this);
    }

    public void registerEvents() {
        getServer().getPluginManager().registerEvents(new DataCreateEvent(this), this);
        getServer().getPluginManager().registerEvents(new BalanceGUI(this), this);
        getServer().getPluginManager().registerEvents(new OfflineManager(this), this);
    }

    public void registerCommands() {
        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("bank").setExecutor(new BankCommand(this));
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("balance").setTabCompleter(new TabBalanceCompleter(this));
        getCommand("bank").setTabCompleter(new TabBankCompleter(this));
        getCommand("pay").setTabCompleter(new TabPayCompleter(this));
    }


}
