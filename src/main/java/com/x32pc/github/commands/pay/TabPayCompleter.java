package com.x32pc.github.commands.pay;

import com.x32pc.github.GBank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class TabPayCompleter implements TabCompleter{

    private final GBank gBank;

    public TabPayCompleter(GBank main) {
        this.gBank = main;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> playerNames = getOnlinePlayerNames();
            for (String playerName : playerNames) {
                if (playerName.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(playerName);
                }
            }
        } else if (args.length == 2) {
            List<String> currencies = gBank.currencyManager.getAllCurrencies();
            for (String currency : currencies) {
                if (currency.toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(currency);
                }
            }
        }

        return completions;
    }

    private List<String> getOnlinePlayerNames() {
        List<String> playerNames = new ArrayList<>();
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            playerNames.add(player.getName());
        }
        return playerNames;
    }
}
