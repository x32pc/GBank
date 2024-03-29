package com.x32pc.github.manager;

import com.x32pc.github.GBank;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private Connection connection;
    private final GBank gBank;
    boolean isYML;


    public DataManager(GBank main, String host, String database, String username, String password) throws SQLException {
        this.gBank = main;
        isYML = !gBank.getConfig().getString("database.type").equalsIgnoreCase("mysql");

        if (isYML) {
            gBank.getLogger().info("Plugin is using YML database.");
        } else {
            gBank.getLogger().info("Plugin is using MySQL database.");
            connect(host, database, username, password);
            createOrUpdateCurrencyTable();
        }
    }

    public boolean getIsYML() {
        return isYML;
    }

    private void connect(String host, String database, String username, String password) throws SQLException {
        String url = "jdbc:mysql://" + host + "/" + database;

        connection = DriverManager.getConnection(url, username, password);
        gBank.getLogger().info("Connected to MySQL database!");
    }

    private void createOrUpdateCurrencyTable() {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "currency_database", null);

            if (!tables.next()) {
                createCurrencyTable();
            } else {
                checkForNewCurrencies();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createCurrencyTable() {
        List<String> currencies = gBank.currencyManager.getAllCurrencies();

        StringBuilder createTableSQL = new StringBuilder("CREATE TABLE currency_database (player_uuid VARCHAR(36) PRIMARY KEY");

        for (String currency : currencies) {
            createTableSQL.append(", ").append(currency).append(" DOUBLE DEFAULT 0.0");
        }
        createTableSQL.append(")");

        try (PreparedStatement statement = connection.prepareStatement(createTableSQL.toString())) {
            statement.executeUpdate();
            gBank.getLogger().info("Created currency table!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void checkForNewCurrencies() throws SQLException {
        List<String> existingCurrencies = getCurrenciesInTable();
        List<String> allCurrencies = gBank.currencyManager.getAllCurrencies();

        for (String currency : allCurrencies) {
            if (!existingCurrencies.contains(currency)) {
                addCurrencyColumn(currency);
            }
        }
    }

    private List<String> getCurrenciesInTable() throws SQLException {
        List<String> currencies = new ArrayList<>();

        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet columns = metaData.getColumns(null, null, "currency_database", null);

        while (columns.next()) {
            String columnName = columns.getString("COLUMN_NAME");
            if (!columnName.equals("player_uuid")) {
                currencies.add(columnName);
            }
        }

        return currencies;
    }

    private void addCurrencyColumn(String currencyName) {
        String alterTableSQL = "ALTER TABLE currency_database ADD COLUMN " + currencyName + " DOUBLE DEFAULT 0.0";

        try (PreparedStatement statement = connection.prepareStatement(alterTableSQL)) {
            statement.executeUpdate();
            gBank.getLogger().info("Added column for currency: " + currencyName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            gBank.getLogger().info("Disconnected from MySQL database!");
        }
    }

    public double getCurrencyValue(String playerUUID, String currencyName) {
        double value = 0;

        String selectQuery = "SELECT " + currencyName + " FROM currency_database WHERE player_uuid = ?";
        try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
            statement.setString(1, playerUUID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                value = resultSet.getDouble(currencyName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return value;
    }

    public void setCurrencyValue(String playerUUID, String currencyName, double value) {
        String updateQuery = "UPDATE currency_database SET " + currencyName + " = ? WHERE player_uuid = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setDouble(1, value);
            statement.setString(2, playerUUID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void giveCurrency(String playerUUID, String currencyName, double amount) {
        double currentValue = getCurrencyValue(playerUUID, currencyName);
        double newValue = currentValue + amount;
        setCurrencyValue(playerUUID, currencyName, newValue);
    }

    public void takeCurrency(String playerUUID, String currencyName, double amount) {
        double currentValue = getCurrencyValue(playerUUID, currencyName);
        setCurrencyValue(playerUUID, currencyName, currentValue - amount);
    }

    public void insertInitialCurrencies(String playerUUID) {
        if (playerExists(playerUUID)) {
            return;
        }

        List<String> currencies = gBank.currencyManager.getAllCurrencies();
        String insertQuery = "INSERT INTO currency_database (player_uuid";

        for (String currency : currencies) {
            insertQuery += ", " + currency;
        }

        insertQuery += ") VALUES (?";
        for (int i = 0; i < currencies.size(); i++) {
            insertQuery += ", ?";
        }

        insertQuery += ")";

        try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setString(1, playerUUID);

            for (int i = 0; i < currencies.size(); i++) {
                statement.setDouble(i + 2, 0.0);
            }

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean playerExists(String playerUUID) {
        String selectQuery = "SELECT player_uuid FROM currency_database WHERE player_uuid = ?";
        try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
            statement.setString(1, playerUUID);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
