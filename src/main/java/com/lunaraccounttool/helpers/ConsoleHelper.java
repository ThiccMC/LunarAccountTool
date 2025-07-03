package com.lunaraccounttool.helpers;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConsoleHelper {

    public static final String ANSI_RESET = "\u001B[0m";

    private static String getAnsiColor(Color color) {
        return String.format("\u001B[38;2;%d;%d;%dm", color.getRed(), color.getGreen(), color.getBlue());
    }

    public static void print(String info, String text, Color color) {
        String dateDebug = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        Color grey = new Color(80, 80, 80);

        System.out.print(getAnsiColor(grey) + " [" + dateDebug + "] > " + ANSI_RESET);
        System.out.print(getAnsiColor(color) + "[" + info + "] " + ANSI_RESET);
        System.out.print(text);
    }

    public static void printLine(String info, String text, Color color) {
        print(info, text, color);
        System.out.println();
    }

    public static void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; ++i) System.out.println();
        }
    }
}