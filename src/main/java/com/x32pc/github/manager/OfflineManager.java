package com.x32pc.github.manager;

import com.x32pc.github.GBank;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.*;
import java.util.*;

public class OfflineManager implements Listener {

    private Map<UUID, String> offlineMessages = new HashMap<>();

    private final GBank gBank;
    private File dataFile;

    public OfflineManager(GBank main) {
        this.gBank = main;
        dataFile = new File(gBank.getDataFolder(), "offline_messages.dat");
        loadData();
    }

    private void loadData() {
        if (!dataFile.exists()) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataFile))) {
            offlineMessages = (Map<UUID, String>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataFile))) {
            oos.writeObject(offlineMessages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addOfflineMessage(UUID playerId, String message) {
        String existingMessages = offlineMessages.get(playerId);

        if (existingMessages == null) {
            offlineMessages.put(playerId, message);
        } else {
            offlineMessages.put(playerId, existingMessages + "\n" + message);
        }
        saveData();
    }

    public List<String> loadOfflineMessages(UUID playerId) {
        String concatenatedMessages = offlineMessages.get(playerId);

        List<String> playerMessages = new ArrayList<>();
        if (concatenatedMessages != null) {
            playerMessages.addAll(Arrays.asList(concatenatedMessages.split("\n")));
        }

        return playerMessages;
    }

    public void clearOfflineMessages(UUID playerId) {
        offlineMessages.remove(playerId);
        saveData();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();

        for (String message : gBank.offlineManager.loadOfflineMessages(playerId)) {
            event.getPlayer().sendMessage(message);
        }
        gBank.offlineManager.clearOfflineMessages(playerId);
    }
}
