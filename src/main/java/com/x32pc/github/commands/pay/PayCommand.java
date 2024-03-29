package com.x32pc.github.commands.pay;

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

        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this.");
            return true;
        }

        if(!(sender.hasPermission(gBank.getConfig().getString("permissions.pay")))) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }

        if(args.length < 3) {
            sendHelp(sender);
            return true;
        }

        if(gBank.currencyManager.getCurrencyName(args[1]) == null) {
            sender.sendMessage(ChatColor.RED + "There isn't currency with name: " + ChatColor.UNDERLINE + args[1]);
            return true;
        }

        if(!testNumber(args, sender)) {
            return true;
        }

        Player has = (Player) sender;

        String to_pay_uuid = Bukkit.getOfflinePlayer(args[0]).getUniqueId().toString();
        String currency = args[1];
        double amount = Double.parseDouble(args[2]);
        String checkIfHas_uuid = has.getUniqueId().toString();

        if(gBank.currencyManager.getAmountCurrency(checkIfHas_uuid, currency) < amount) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', gBank.languageManager.getLanguageConfig().getString("messages.pay.not-enough")).replace("%currency_amount%", args[2]).replace("%currency_symbol%", gBank.currencyManager.getCurrencyPrefix(currency)));
        } else {
            double old_sender = gBank.currencyManager.getAmountCurrency(checkIfHas_uuid, currency);
            double new_amount_sender = old_sender - amount;
            double tax = gBank.getConfig().getDouble("tax.tax");
            String tax_text = String.valueOf(tax*100);
            amount = amount * (1 - tax);
            gBank.currencyManager.giveAmountCurrency(to_pay_uuid, currency, amount);
            gBank.currencyManager.setAmountCurrency(checkIfHas_uuid, currency, new_amount_sender);
            if(Bukkit.getPlayer(args[0]) != null) {
                Bukkit.getPlayer(args[0]).sendMessage(ChatColor.translateAlternateColorCodes('&', gBank.languageManager.getLanguageConfig().getString("messages.pay.received")).replace("%currency_amount%", String.valueOf(amount)).replace("%currency_symbol%", gBank.currencyManager.getCurrencyPrefix(currency)).replace("%receiver%", args[0]).replace("%tax%", tax_text).replace("%sender%", sender.getName()));
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', gBank.languageManager.getLanguageConfig().getString("messages.pay.send")).replace("%currency_amount%", String.valueOf(amount)).replace("%currency_symbol%", gBank.currencyManager.getCurrencyPrefix(currency)).replace("%receiver%", args[0]).replace("%tax%", tax_text).replace("%sender%", sender.getName()));
        }

        return true;
    }



    public void sendHelp(CommandSender sender) {
        for (String message : gBank.languageManager.getLanguageConfig().getStringList("messages.help"))
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public boolean testNumber(String[] args, CommandSender sender) {
        try {
            double value = Double.parseDouble(args[2]);
        } catch (NumberFormatException ex) {
            sender.sendMessage(ChatColor.RED + "Last argument isn't number. (Example of number: 10.4)");
            return false;
        }
        return true;
    }
}
