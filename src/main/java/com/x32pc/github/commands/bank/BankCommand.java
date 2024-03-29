package com.x32pc.github.commands.bank;

import com.x32pc.github.GBank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BankCommand implements CommandExecutor {

    private final GBank gBank;

    public BankCommand(GBank main) {
        this.gBank = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this.");
            return true;
        }

        if(args.length == 0) {
            sendHelp(sender);
            return true;
        }

        if(args[0].equalsIgnoreCase("reload")) {
            gBank.reloadConfig();
            gBank.languageManager.saveLanguageFile();
            sender.sendMessage(ChatColor.GREEN + " Config has been reloaded and saved.");
            return true;
        }
       if(args[0].equalsIgnoreCase("give")) {
           if(!(sender.hasPermission(gBank.getConfig().getString("permissions.bank.give")))) {
               sender.sendMessage(ChatColor.RED + "No permission.");
               return true;
           }
            BankGive bankGive = new BankGive(gBank);
            bankGive.onCommand(sender, command, label, args);
        } else if(args[0].equalsIgnoreCase("set")) {
           if(!(sender.hasPermission(gBank.getConfig().getString("permissions.bank.set")))) {
               sender.sendMessage(ChatColor.RED + "No permission.");
               return true;
           }
            BankSet bankSet = new BankSet(gBank);
            bankSet.onCommand(sender, command, label, args);
        } else if(args[0].equalsIgnoreCase("take")) {
           if(!(sender.hasPermission(gBank.getConfig().getString("permissions.bank.take")))) {
               sender.sendMessage(ChatColor.RED + "No permission.");
               return true;
           }
            BankTake bankTake = new BankTake(gBank);
            bankTake.onCommand(sender, command, label, args);
        }
        return true;
    }

    public void sendHelp(CommandSender sender) {
        for (String message : gBank.languageManager.getLanguageConfig().getStringList("messages.help"))
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }



}
