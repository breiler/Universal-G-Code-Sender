/*
    Copyright 2026 Joacim Breiler

    This file is part of Universal Gcode Sender (UGS).

    UGS is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    UGS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with UGS.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.willwinder.universalgcodesender.fx.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.willwinder.universalgcodesender.fx.service.ShortcutService;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * Handles the default keyboard shortcut mappings. The defaults are loaded from a
 * bundled JSON resource and applied through the {@link ShortcutService}.
 * <p>
 * The defaults are applied the first time the application is run. The version of
 * the defaults that has been applied is stored as an integer so that the bundled
 * defaults can be re-applied in the future by bumping {@link #DEFAULTS_VERSION}.
 */
public final class ShortcutSettings {
    private static final Logger LOGGER = Logger.getLogger(ShortcutSettings.class.getName());
    private static final String MAC_SHORTCUTS_RESOURCE = "/shortcuts/default-shortcuts-mac.json";
    private static final String WINDOWS_SHORTCUTS_RESOURCE = "/shortcuts/default-shortcuts-windows.json";
    private static final String LINUX_SHORTCUTS_RESOURCE = "/shortcuts/default-shortcuts-linux.json";
    private static final String APPLIED_DEFAULTS_VERSION = "shortcuts.appliedDefaultsVersion";
    private static final String SHORTCUT_PREFIX = "shortcuts.";

    /**
     * The version of the bundled default shortcuts. Bump this value to force the
     * defaults to be applied again on the next startup.
     */
    private static final int DEFAULTS_VERSION = 1;

    private static final Preferences preferences = Preferences.userNodeForPackage(ShortcutSettings.class);

    private ShortcutSettings() {
    }

    /**
     * Returns the stored shortcut for the given action, if any.
     */
    public static Optional<String> getShortcut(String actionId) {
        return Optional.ofNullable(preferences.get(toPreferenceKey(actionId), null));
    }

    /**
     * Stores the shortcut for the given action.
     */
    public static void saveShortcut(String actionId, String shortcut) {
        preferences.put(toPreferenceKey(actionId), shortcut);
    }

    /**
     * Removes any stored shortcut for the given action.
     */
    public static void removeShortcut(String actionId) {
        preferences.remove(toPreferenceKey(actionId));
    }

    /**
     * Action ids are fully qualified class names which can exceed the maximum key
     * length allowed by the preferences store. Each package segment is shortened to
     * its first character while keeping the class name, so
     * {@code com.willwinder.universalgcodesender.fx.actions.StartAction} becomes
     * {@code c.w.u.f.a.StartAction}.
     */
    private static String toPreferenceKey(String actionId) {
        String[] segments = actionId.split("\\.");
        StringBuilder key = new StringBuilder(SHORTCUT_PREFIX);
        for (int i = 0; i < segments.length - 1; i++) {
            if (!segments[i].isEmpty()) {
                key.append(segments[i].charAt(0)).append('.');
            }
        }
        key.append(segments[segments.length - 1]);
        return key.toString();
    }

    /**
     * Applies the bundled default shortcuts if they have not been applied for the
     * current {@link #DEFAULTS_VERSION} yet.
     */
    public static void applyDefaultsIfNeeded() {
        if (preferences.getInt(APPLIED_DEFAULTS_VERSION, 0) >= DEFAULTS_VERSION) {
            return;
        }

        loadDefaultShortcuts().forEach(ShortcutService::setShortcut);
        preferences.putInt(APPLIED_DEFAULTS_VERSION, DEFAULTS_VERSION);
    }

    /**
     * Removes all currently configured shortcuts and re-applies the bundled
     * defaults.
     */
    public static void restoreDefaults() {
        Set<String> existingActionIds = new HashSet<>(ShortcutService.getShortcuts().keySet());
        existingActionIds.forEach(ShortcutService::removeShortcut);

        loadDefaultShortcuts().forEach(ShortcutService::setShortcut);
        preferences.putInt(APPLIED_DEFAULTS_VERSION, DEFAULTS_VERSION);
    }

    /**
     * Writes the currently configured shortcuts to the given file using the same
     * JSON format as the bundled default shortcut files.
     */
    public static void exportShortcuts(File file) throws IOException {
        Map<String, String> shortcuts = new TreeMap<>(ShortcutService.getShortcuts());
        try (Writer writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(shortcuts, writer);
        }
    }

    /**
     * Replaces the currently configured shortcuts with the ones read from the
     * given JSON file.
     */
    public static void importShortcuts(File file) throws IOException {
        Map<String, String> imported;
        Type type = new TypeToken<LinkedHashMap<String, String>>() {
        }.getType();
        try (Reader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            imported = new Gson().fromJson(reader, type);
        }

        if (imported == null) {
            return;
        }

        Set<String> existingActionIds = new HashSet<>(ShortcutService.getShortcuts().keySet());
        existingActionIds.forEach(ShortcutService::removeShortcut);
        imported.forEach(ShortcutService::setShortcut);
    }

    private static String getPlatformShortcutsResource() {
        if (SystemUtils.IS_OS_MAC) {
            return MAC_SHORTCUTS_RESOURCE;
        } else if (SystemUtils.IS_OS_WINDOWS) {
            return WINDOWS_SHORTCUTS_RESOURCE;
        }
        return LINUX_SHORTCUTS_RESOURCE;
    }

    private static Map<String, String> loadDefaultShortcuts() {
        String resource = getPlatformShortcutsResource();
        try (InputStream inputStream = ShortcutSettings.class.getResourceAsStream(resource)) {
            if (inputStream == null) {
                LOGGER.warning("Could not find default shortcuts resource " + resource);
                return Collections.emptyMap();
            }

            Type type = new TypeToken<LinkedHashMap<String, String>>() {
            }.getType();
            return new Gson().fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), type);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Could not load default shortcuts", e);
            return Collections.emptyMap();
        }
    }
}
