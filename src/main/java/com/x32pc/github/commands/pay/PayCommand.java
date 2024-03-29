package com.x32pc.github.commands;

import com.x32pc.github.GBank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {

    private final GBank gBank;

    public PayCommand(GBank main) {
        this.gBank = main;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(!(sender.hasPermission("gbank.pay"))) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this.");
            return true;
        }

        if(args.length < 3) {
            sendHelp(sender);
            return true;
        }

        Player has = (Player) sender;

        String to_pay_uuid = Bukkit.getOfflinePlayer(args[0]).getUniqueId().toString();
        String currency = args[1];
        double amount = Double.parseDouble(args[2]);
        String checkIfHas_uuid = has.getUniqueId().toString();

        if(gBank.dataCreateEvent.getCurrencyAmount(checkIfHas_uuid, currency) < amount) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', gBank.languageManager.getLanguageConfig().getString("messages.pay.not-enough")).replace("%currency_amount%", args[2]).replace("%currency_symbol%", gBank.currencyManager.getCurrencyPrefix(currency)));
        } else {
            double old_sender = gBank.dataCreateEvent.getCurrencyAmount(checkIfHas_uuid, currency);
            double new_amount_sender = old_sender - amount;
            double tax = gBank.getConfig().getDouble("tax.tax");
            String tax_text = String.valueOf(tax*100);
            amount = amount * (1 - tax);
            gBank.dataCreateEvent.giveCurrencyAmount(to_pay_uuid, currency, amount);
            gBank.dataCreateEvent.setCurrencyAmount(checkIfHas_uuid, currency, new_amount_sender);
            if(Bukkit.getPlayer(to_pay_uuid) != null) {
                Bukkit.getPlayer(to_pay_uuid).sendMessage(ChatColor.translateAlternateColorCodes('&', gBank.languageManager.getLanguageConfig().getString("messages.pay.received")).replace("%currency_amount%", args[2]).replace("%currency_symbol%", gBank.currencyManager.getCurrencyPrefix(currency)).replace("%receiver%", args[0]).replace("%tax%", tax_text));
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', gBank.languageManager.getLanguageConfig().getString("messages.pay.send")).replace("%currency_amount%", args[2]).replace("%currency_symbol%", gBank.currencyManager.getCurrencyPrefix(currency)).replace("%receiver%", args[0]).replace("%tax%", tax_text));
        }

        return true;
    }



    public void sendHelp(CommandSender sender) {
        for (String message : gBank.languageManager.getLanguageConfig().getStringList("messages.help"))
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
