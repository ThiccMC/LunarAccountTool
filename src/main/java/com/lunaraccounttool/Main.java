package com.lunaraccounttool;

import com.lunaraccounttool.helpers.AccountManager;
import com.lunaraccounttool.helpers.ConsoleHelper;
import com.lunaraccounttool.helpers.Validate;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    public static final String userFolder = System.getProperty("user.home");
    public static Path lunarAccountsPath = Paths.get(userFolder, ".lunarclient", "settings", "game", "accounts.json");

    private static final Color INFO_COLOR = new Color(135, 145, 216);
    private static final Color ERROR_COLOR = new Color(224, 17, 95);
    private static final Color WARN_COLOR = new Color(242, 140, 40);

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        AccountManager.loadJson();

        boolean continueProgram = true;
        while (continueProgram) {
            ConsoleHelper.clearConsole();
            printMenu();

            String choice = scanner.nextLine();
            try {
                switch (Integer.parseInt(choice)) {
                    case 1:
                        createAccountPrompt();
                        break;
                    case 2:
                        removeAccountsMenu();
                        break;
                    case 3:
                        AccountManager.viewInstalledAccounts();
                        break;
                    case 4:
                        ConsoleHelper.printLine("INFO", "Exiting the program.", INFO_COLOR);
                        continueProgram = false;
                        break;
                    default:
                        ConsoleHelper.printLine("ERROR", "Your choice is invalid. Please pick an option (1-4).", ERROR_COLOR);
                        break;
                }
            } catch (NumberFormatException e) {
                 ConsoleHelper.printLine("ERROR", "Invalid input. Please enter a number.", ERROR_COLOR);
            } catch (Exception e) {
                ConsoleHelper.printLine("ERROR", "An error occurred: " + e.getMessage(), ERROR_COLOR);
                e.printStackTrace(); // Good for debugging
            }

            if (continueProgram) {
                ConsoleHelper.printLine("INFO", "Press any key to return to the main menu...", INFO_COLOR);
                scanner.nextLine(); // Waits for user to press Enter
            }
        }

        AccountManager.saveJson();
        scanner.close();
    }

    private static void printMenu() {
        ConsoleHelper.printLine("QUERY", "What would you like to do:", INFO_COLOR);
        ConsoleHelper.printLine("OPTION", "1. Create Account", INFO_COLOR);
        ConsoleHelper.printLine("OPTION", "2. Remove Accounts", INFO_COLOR);
        ConsoleHelper.printLine("OPTION", "3. View Installed Accounts", INFO_COLOR);
        ConsoleHelper.printLine("OPTION", "4. Exit the program", INFO_COLOR);
        ConsoleHelper.print("INPUT", "Please type your option (1-4) here: ", INFO_COLOR);
    }

    public static void removeAccountsMenu() {
        ConsoleHelper.clearConsole();
        ConsoleHelper.printLine("QUERY", "Choose an option to remove accounts:", INFO_COLOR);
        ConsoleHelper.printLine("OPTION", "1. Remove All Accounts", INFO_COLOR);
        ConsoleHelper.printLine("OPTION", "2. Remove Cracked Accounts (accessToken is not a UUID)", INFO_COLOR);
        ConsoleHelper.printLine("OPTION", "3. Remove Premium Accounts (accessToken is a UUID)", INFO_COLOR);
        ConsoleHelper.printLine("OPTION", "4. Remove by manually specifying UUID", INFO_COLOR);
        ConsoleHelper.print("INPUT", "Please type your option (1-3) here: ", INFO_COLOR);

        String choice = scanner.nextLine();
        try {
             switch (Integer.parseInt(choice)) {
                case 1:
                    AccountManager.removeAllAccounts();
                    break;
                case 2:
                    // Note: The original logic for removing "cracked" accounts was inverted.
                    // It removed accounts where the token *was* a valid UUID. Corrected here.
                    AccountManager.removeCrackedAccounts();
                    break;
                case 3:
                     // Note: The original logic for removing "premium" accounts was inverted.
                     // It removed accounts where the token was *not* a valid UUID. Corrected here.
                    AccountManager.removePremiumAccounts();
                    break;
                 case 4:
                     ConsoleHelper.print("INPUT", "Enter the UUID you want to delete: ", INFO_COLOR);
                     String uuidInput = scanner.nextLine();
                     AccountManager.removeByUUID(uuidInput);
                     break;
                default:
                    ConsoleHelper.printLine("ERROR", "Invalid option. Returning to main menu.", ERROR_COLOR);
                    break;
            }
        } catch (NumberFormatException e) {
            ConsoleHelper.printLine("ERROR", "Invalid input. Please enter a number.", ERROR_COLOR);
        }
        AccountManager.saveJson();
    }

    private static void createAccountPrompt() {
        ConsoleHelper.print("INPUT", "Enter your desired username: ", INFO_COLOR);
        String usernameAdd = scanner.nextLine();
        if (!Validate.isValidMinecraftUsername(usernameAdd)) {
            ConsoleHelper.printLine("WARNING", "You may experience issues joining servers because your username being invalid.", WARN_COLOR);
        }

        while (true) {
            ConsoleHelper.print("INPUT", "Enter a valid UUID (can be dashless, though premium accounts use dashless): ", INFO_COLOR);
            String customUuidAdd = scanner.nextLine();
            if (!Validate.isValidUUID(customUuidAdd) || !Validate.isValidDashlessUUID(customUuidAdd)) {
                ConsoleHelper.printLine("WARNING", "The UUID you entered is invalid. Please ensure it follows the correct format.", WARN_COLOR);
                ConsoleHelper.print("QUERY", "Would you like to try again? (y/n): ", INFO_COLOR);
                String retry = scanner.nextLine().trim().toLowerCase();
                if ("n".equals(retry)) {
                    ConsoleHelper.printLine("INFO", "Returning to main menu.", INFO_COLOR);
                    return;
                }
            } else {
                AccountManager.createAccount(usernameAdd, customUuidAdd);
                AccountManager.saveJson();
                break;
            }
        }
    }
}