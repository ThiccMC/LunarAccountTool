package com.lunaraccounttool.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lunaraccounttool.Main;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AccountManager {

    private static JsonObject json;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Color SUCCESS_COLOR = new Color(135, 145, 216);
    private static final Color ERROR_COLOR = new Color(224, 17, 95);
    private static final Color NOTICE_COLOR = new Color(242, 140, 40);

    public static void createAccount(String username, String uuid) {
        JsonObject newAccount = new JsonObject();
        newAccount.addProperty("accessToken", uuid);
        newAccount.addProperty("accessTokenExpiresAt", "2050-07-02T10:56:30.717167800Z");
        newAccount.addProperty("eligibleForMigration", false);
        newAccount.addProperty("hasMultipleProfiles", false);
        newAccount.addProperty("legacy", true);
        newAccount.addProperty("persistent", true);
        newAccount.add("userProperites", new JsonArray());
        newAccount.addProperty("localId", uuid);

        JsonObject minecraftProfile = new JsonObject();
        minecraftProfile.addProperty("id", uuid);
        minecraftProfile.addProperty("name", username);
        newAccount.add("minecraftProfile", minecraftProfile);

        newAccount.addProperty("remoteId", uuid);
        newAccount.addProperty("type", "Xbox");
        newAccount.addProperty("username", username);

        JsonObject accounts = json.getAsJsonObject("accounts");
        accounts.add(uuid, newAccount);

        ConsoleHelper.printLine("SUCCESS", "Your account has successfully been created.", SUCCESS_COLOR);
    }

    public static void removeAllAccounts() {
        json.add("accounts", new JsonObject());
        ConsoleHelper.printLine("SUCCESS", "All accounts have been successfully removed.", SUCCESS_COLOR);
    }

    public static void removePremiumAccounts() {
        removeAccountsByUuidValidation(false);
        ConsoleHelper.printLine("SUCCESS", "Premium accounts have been successfully removed.", SUCCESS_COLOR);
    }

    public static void removeCrackedAccounts() {
        removeAccountsByUuidValidation(true);
        ConsoleHelper.printLine("SUCCESS", "Cracked accounts have been successfully removed.", SUCCESS_COLOR);
    }

    public static void removeByUUID(String uuid) {
        removeAccountsByUuid(uuid);
        ConsoleHelper.printLine("SUCCESS", "Accounts associated with the UUID have been successfully removed.", SUCCESS_COLOR);
    }

    private static void removeAccountsByUuidValidation(boolean shouldBeValid) {
        Set<String> accountsToRemove = new HashSet<>();
        JsonObject accounts = json.getAsJsonObject("accounts");

        for (Map.Entry<String, com.google.gson.JsonElement> entry : accounts.entrySet()) {
            JsonObject accountObj = entry.getValue().getAsJsonObject();
            String accessToken = accountObj.get("accessToken").getAsString();
            if (Validate.isValidUUID(accessToken) == shouldBeValid) {
                accountsToRemove.add(entry.getKey());
            }
        }

        for (String key : accountsToRemove) {
            accounts.remove(key);
        }
    }

    private static void removeAccountsByUuid(String uuid) {
        Set<String> accountsToRemove = new HashSet<>();
        JsonObject accounts = json.getAsJsonObject("accounts");

        for (Map.Entry<String, com.google.gson.JsonElement> entry : accounts.entrySet()) {
            JsonObject accountObj = entry.getValue().getAsJsonObject();
            String accessToken = accountObj.get("accessToken").getAsString();
            if (accessToken.equals(uuid)) {
                accountsToRemove.add(entry.getKey());
            }
        }

        for (String key : accountsToRemove) {
            accounts.remove(key);
        }
    }

    public static void viewInstalledAccounts() {
        ConsoleHelper.printLine("INFO", "Installed Accounts:", SUCCESS_COLOR);
        JsonObject accounts = json.getAsJsonObject("accounts");
        if (accounts.size() == 0) {
            ConsoleHelper.printLine("NOTICE", "No accounts are installed.", NOTICE_COLOR);
            return;
        }

        for (Map.Entry<String, com.google.gson.JsonElement> entry : accounts.entrySet()) {
            String username = entry.getValue().getAsJsonObject().get("username").getAsString();
            ConsoleHelper.printLine("ACCOUNT", entry.getKey() + ": " + username, SUCCESS_COLOR);
        }
    }

    public static void loadJson() {
        try {
            String content = Files.readString(Main.lunarAccountsPath);
            json = GSON.fromJson(content, JsonObject.class);
            if (json == null || !json.has("accounts")) {
                initializeEmptyJson();
            }
        } catch (NoSuchFileException e) {
            initializeEmptyJson();
        } catch (IOException e) {
            ConsoleHelper.printLine("ERROR", "Failed to load accounts file: " + e.getMessage(), ERROR_COLOR);
            ConsoleHelper.printLine("NOTICE", "Please check that you have Lunar Client installed.", NOTICE_COLOR);
            ConsoleHelper.printLine("NOTICE", "Exiting in 3 seconds...", NOTICE_COLOR);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
            }
            System.exit(1);
        }
    }

    private static void initializeEmptyJson() {
        json = new JsonObject();
        json.add("accounts", new JsonObject());
    }

    public static void saveJson() {
        try {
            Files.writeString(Main.lunarAccountsPath, GSON.toJson(json));
        } catch (IOException e) {
            ConsoleHelper.printLine("ERROR", "Failed to save accounts file: " + e.getMessage(), ERROR_COLOR);
        }
    }
}