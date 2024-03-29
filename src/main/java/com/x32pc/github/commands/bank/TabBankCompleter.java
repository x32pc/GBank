package com.x32pc.github.commands.bank;

import com.x32pc.github.GBank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class TabBankCompleter implements TabCompleter {

    private final GBank gBank;

    public TabBankCompleter(GBank main) {
        this.gBank = main;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();


        if (args.length == 1) {
            String[] subCommands = {"give", "set", "take"};
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2) {
            List<String> playerNames = getOnlinePlayerNames();
            for (String playerName : playerNames) {
                if (playerName.toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(playerName);
                }
            }
        } else if (args.length == 3) {
            List<String> currencies = gBank.currencyManager.getAllCurrencies();
            for (String currency : currencies) {
                if (currency.toLowerCase().startsWith(args[2].toLowerCase())) {
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
