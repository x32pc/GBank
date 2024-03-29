package com.x32pc.github.commands.bank;

import com.x32pc.github.GBank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import java.text.DecimalFormat;
import java.util.UUID;

public class BankSet implements CommandExecutor {

    private final GBank gBank;

    public BankSet(GBank main) {
        this.gBank = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(gBank.bankGive.testNumber(args, sender) && gBank.bankGive.testCommand(args, sender))) {
            return true;
        }
        DecimalFormat format = new DecimalFormat("0.00");
        double d = Double.parseDouble((format.format(Double.parseDouble(args[3]))));
        String uuid = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
        gBank.currencyManager.setAmountCurrency(uuid, args[2], d);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', gBank.languageManager.getLanguageConfig().getString("messages.bank.set")).replace("%player%", args[1]).replace("%currency_symbol%", gBank.currencyManager.getCurrencyPrefix(args[2])).replace("%amount%", String.valueOf(d)));
        String message = ChatColor.translateAlternateColorCodes('&', gBank.languageManager.getLanguageConfig().getString("messages.bank.set-receiver")).replace("%executor%", sender.getName()).replace("%player%", args[1]).replace("%currency_symbol%", gBank.currencyManager.getCurrencyPrefix(args[2])).replace("%amount%", String.valueOf(d));
        if(Bukkit.getPlayer(args[1]) != null) {
            Bukkit.getPlayer(args[1]).sendMessage(message);
        } else {
            gBank.offlineManager.addOfflineMessage(UUID.fromString(uuid), message);
        }
        return true;
    }
}
