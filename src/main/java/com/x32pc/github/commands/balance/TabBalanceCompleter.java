package com.x32pc.github.commands.balance;

import com.x32pc.github.GBank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class TabBalanceCompleter implements TabCompleter {

    private final GBank gBank;

    public TabBalanceCompleter(GBank main) {
        this.gBank = main;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && "balance".equalsIgnoreCase(command.getName())) {
            return gBank.currencyManager.getAllCurrencies();
        } else if (args.length == 2 && "balance".equalsIgnoreCase(command.getName())) {
            String currencyArg = args[0];
            if (gBank.currencyManager.getAllCurrencies().contains(currencyArg)) {
                return getOnlinePlayerNames();
            }
        }
        return new ArrayList<>();
    }

    private List<String> getOnlinePlayerNames() {
        List<String> playerNames = new ArrayList<>();
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            playerNames.add(player.getName());
        }
        return playerNames;
    }
}
