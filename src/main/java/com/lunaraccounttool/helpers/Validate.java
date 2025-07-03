package com.lunaraccounttool.helpers;

import java.util.regex.Pattern;

public class Validate {

    private static final Pattern MINECRAFT_USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");

    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    private static final Pattern DASHLESS_UUID_PATTERN = Pattern.compile("^[0-9a-fA-F]{32}$");


    public static boolean isValidMinecraftUsername(String username) {
        if (username == null) {
            return false;
        }
        return MINECRAFT_USERNAME_PATTERN.matcher(username).matches();
    }

    public static boolean isValidUUID(String uuid) {
        if (uuid == null) {
            return false;
        }
        return UUID_PATTERN.matcher(uuid).matches();
    }

    public static boolean isValidDashlessUUID(String uuid) {
        if (uuid == null) {
            return false;
        }
        return DASHLESS_UUID_PATTERN.matcher(uuid).matches();
    }
}