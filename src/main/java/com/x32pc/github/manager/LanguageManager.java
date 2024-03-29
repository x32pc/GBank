package com.x32pc.github.manager;

import com.x32pc.github.GBank;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class LanguageManager {

    public File languageFile ;
    public FileConfiguration languageConfig;

    private final GBank gBank;

    public LanguageManager(GBank main) {
        this.gBank = main;
    }

    public void createLanguageFile() {
        languageFile = new File(gBank.getDataFolder(), "language.yml");
        if (!languageFile.exists()) {
            languageFile.getParentFile().mkdirs();
            gBank.saveResource("language.yml", false);
        }
    }

    public void saveLanguageFile() {
        languageConfig = YamlConfiguration.loadConfiguration(languageFile);
        try {
            languageConfig.save(languageFile);
        } catch (IOException e) {
        }
    }

    public FileConfiguration getLanguageConfig() {
        languageConfig = YamlConfiguration.loadConfiguration(languageFile);
        return languageConfig;
    }
}
