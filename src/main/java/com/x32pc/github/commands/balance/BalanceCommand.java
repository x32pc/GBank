package com.x32pc.github.commands.balance;

import com.x32pc.github.GBank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class BalanceCommand implements CommandExecutor {

    private final GBank gBank;

    public BalanceCommand(GBank main) {
        this.gBank = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this.");
            return true;
        }

        if(!(sender.hasPermission(gBank.getConfig().getString("permissions.balance")))) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }

        Player p = (Player) sender;

        if(args.length > 2) {
            for (String message : gBank.languageManager.getLanguageConfig().getStringList("messages.help"))
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            return true;
        }

        if(args.length == 2) {
            if(gBank.currencyManager.getCurrencyName(args[0]) == null) {
                sender.sendMessage(ChatColor.RED + "There isn't currency with name: " + ChatColor.UNDERLINE + args[0]);
                return true;
            }
            String fromArgument = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
            String currency = args[0];
            String currency_symbol = gBank.currencyManager.getCurrencyPrefix(args[0]);
            String currency_name = gBank.currencyManager.getCurrencyNameColor(args[0]);
            String amount = String.valueOf(gBank.currencyManager.getAmountCurrency(fromArgument, currency));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', gBank.languageManager.getLanguageConfig().getString("messages.currency.specific-currency-amount-other")).replace("%currency_symbol%", currency_symbol).replace("%currency_name%", currency_name).replace("%currency_amount%", amount).replace("%player%", Bukkit.getPlayer(args[1]).getName()));
        } else if(args.length == 1) {
            if(gBank.currencyManager.getCurrencyName(args[0]) == null) {
                sender.sendMessage(ChatColor.RED + "There isn't currency with name: " + ChatColor.UNDERLINE + args[0]);
                return true;
            }
            String currency = args[0];
            String currency_symbol = gBank.currencyManager.getCurrencyPrefix(args[0]);
            String currency_name = gBank.currencyManager.getCurrencyNameColor(args[0]);
            String amount = String.valueOf(gBank.currencyManager.getAmountCurrency(p.getUniqueId().toString(), currency));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',gBank.languageManager.getLanguageConfig().getString("messages.currency.specific-currency-amount")).replace("%currency%", currency).replace("%currency_symbol%", currency_symbol).replace("%currency_name%", currency_name).replace("%currency_amount%", amount));
        } else {
            gBank.balanceGUI.openCurrencyGUI(p, 0);
        }

        return true;
    }


}
