package com.x32pc.github.commands.bank;

import com.x32pc.github.GBank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import java.util.UUID;

public class BankTake implements CommandExecutor {

    private final GBank gBank;

    public BankTake(GBank main) {
        this.gBank = main;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(gBank.bankGive.testNumber(args, sender) && gBank.bankGive.testCommand(args, sender))) {
            return true;
        }
        String uuid = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
        double toTake = Double.parseDouble(args[3]);
        double has = gBank.currencyManager.getAmountCurrency(uuid, args[2]);

        if(has-toTake < 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', gBank.languageManager.getLanguageConfig().getString("messages.bank.take-not-enough").replace("%player%", args[1]).replace("%amount%", String.valueOf(toTake)).replace("%currency_amount%", String.valueOf(has)).replace("%currency_symbol%", gBank.currencyManager.getCurrencyPrefix(args[2]))));
            return true;
        }
        gBank.currencyManager.takeAmountCurrency(uuid, args[2], toTake);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', gBank.languageManager.getLanguageConfig().getString("messages.bank.take")).replace("%player%", args[1]).replace("%currency_amount%", Double.toString(has-toTake)).replace("%currency_symbol%", gBank.currencyManager.getCurrencyPrefix(args[2])).replace("%amount%", String.valueOf(toTake)));
        String message = ChatColor.translateAlternateColorCodes('&', gBank.languageManager.getLanguageConfig().getString("messages.bank.take-receiver")).replace("%executor%", sender.getName()).replace("%player%", args[1]).replace("%currency_symbol%", gBank.currencyManager.getCurrencyPrefix(args[2])).replace("%amount%", String.valueOf(toTake));
        if(Bukkit.getPlayer(args[1]) != null) {
            Bukkit.getPlayer(args[1]).sendMessage(message);
        } else {
            gBank.offlineManager.addOfflineMessage(UUID.fromString(uuid), message);
        }
        return true;
    }


}
