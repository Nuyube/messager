package com.github.nuyube.messager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Messages {
    private static Messages instance;
    private static final int LOGGING_LEVEL = 4;
    private Map<String, String> messages;
    private Logger logger;

    public static Messages getInstance() {
        if (instance == null) {
            instance = new Messages();
        }
        return instance;
    }

    private Messages() {
        messages = new HashMap<String, String>();
    }

    public String get(String Key) {
        return messages.get(Key);
    }

    public void init(Logger logger, String messagesPath) throws FileNotFoundException {
        this.logger = logger;
        // Read our configuration
        log("Initializing messages...", 1);
        messages = new HashMap<String, String>();
        File ConfigFile = new File(messagesPath);
        // If our file or directory doesn't exist,
        if (!ConfigFile.exists()) {
            throw new FileNotFoundException(messagesPath);
        }
        // Read the file (which might be new)
        readMessages(messagesPath);
    }

    private void readMessages(String messagesPath) {
        log("Reading configuration file", 3);
        // Get files
        File ConfigFile = new File(messagesPath);
        Scanner fr = null;
        try {
            // Open our file
            fr = new Scanner(ConfigFile);
        } catch (FileNotFoundException e) {
            log("Failed to read messages file: Not found", 0);
            return;
        }
        while (fr.hasNextLine()) {
            String s = fr.nextLine();
            if (s.contains(":") && !s.trim().startsWith("#")) {
                s = s.trim();
                try {
                    String key = s.substring(0, s.indexOf(':')).trim();
                    String value = s.substring(s.indexOf(':') + 1).trim();
                    messages.put(key, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        fr.close();

    }

    /**
     * Emits a message to the console without replacing any keys.
     * 
     * @param key The name of the message to send.
     */
    public void emitConsole(String key) {
        emitConsole(key, null);
    }

    /**
     * Emits a severe message to the console without replacing any keys.
     * 
     * @param key The name of the message to send.
     */
    public void emitConsoleSevere(String key) {
        emitConsoleSevere(key, null);
    }

    /**
     * Emits a message to the console, making the replacements specified.
     * 
     * @param key          The name of the message to send
     * @param replacements The replacements to make
     */
    public void emitConsole(String key, HashMap<String, String> replacements) {
        _emitConsole(key, replacements, Level.INFO);
    }

    /**
     * Emits a message to the console with a specified severity and replacements
     * 
     * @param key          The name of the message to send
     * @param replacements The replacements to make
     * @param logLevel     The severity of the message
     */
    private void _emitConsole(String key, HashMap<String, String> replacements, Level logLevel) {
        String value = getKeyWithReplacements(key, replacements);
        if (!value.isBlank()) {
            logger.log(logLevel, value);
        }
    }

    /**
     * Emits a severe message to the console, making specified replacements
     * 
     * @param key          The name of the message to send
     * @param replacements The replacements to make
     */
    public void emitConsoleSevere(String key, HashMap<String, String> replacements) {
        _emitConsole(key, replacements, Level.SEVERE);
    }

    public String getKeyWithReplacements(String key, HashMap<String, String> replacements) {
        String value = get(key);
        value = replaceDictionary(replacements, value);
        return value;
    }

    // These two members are commented to remove the dependency on Spigot API.
    // If you'd like to be able to message a player directly, you can remove
    // this comment.
    /**
     * Emits a message to a player
     * 
     * @param player The player to send the message to
     * @param key    The name of the message to send
     */
    /*
     * public void emitPlayer(Player player, String key) { emitPlayer(player, key,
     * null); }
     */
    /**
     * Emits a message to a player with specified replacements
     * 
     * @param player       The player to send the message to
     * @param key          The name of the message to send
     * @param replacements The replacements to make
     */

    /*
     * public void emitPlayer(Player player, String key, HashMap<String, String>
     * replacements) { String value = getKeyWithReplacements(key, replacements); if
     * (!value.isBlank()) player.sendMessage(value); }
     */

    /**
     * Makes a set of replacements to a base string
     * 
     * @param replacements The replacements to make. A HashMap with from and to
     *                     values for each replacement
     * @param value        The string to operate on
     * @return The string with all applicable replacements made
     */
    private String replaceDictionary(HashMap<String, String> replacements, String value) {
        if (value == null)
            return "";
        value = value.replace("&&", String.valueOf('§'));
        if (replacements != null) {
            Set<String> keys = replacements.keySet();
            Iterator<String> keyIterator = keys.iterator();
            for (; keyIterator.hasNext();) {
                String s = keyIterator.next();
                value = value.replace(s, replacements.get(s).toString());
            }
        }
        return value;
    }

    private final void log(String message, int logLevel) {
        if (LOGGING_LEVEL >= logLevel) {
            logger.info(message);
        }
    }
}
