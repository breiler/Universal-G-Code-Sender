/*
    Copyright 2025 Joacim Breiler

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
package com.willwinder.universalgcodesender.fx.service;

import com.willwinder.universalgcodesender.fx.helper.ShortcutConverter;
import com.willwinder.universalgcodesender.fx.model.ShortcutEvent;
import com.willwinder.universalgcodesender.fx.settings.ShortcutSettings;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.EventType;
import javafx.scene.Scene;
import static javafx.scene.input.KeyEvent.KEY_PRESSED;
import static javafx.scene.input.KeyEvent.KEY_RELEASED;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Stores action shortcut mappings. Persistence is delegated to {@link ShortcutSettings}.
 */
public class ShortcutService {
    /**
     * Time in milliseconds a shortcut must be held before it is considered a long press.
     */
    private static final long LONG_PRESS_DELAY = 300;

    private static final ObservableMap<String, String> shortcuts =
            FXCollections.observableHashMap();

    private static final Set<String> pressedKeys = ConcurrentHashMap.newKeySet();

    private static final ScheduledExecutorService longPressExecutor = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "shortcut-long-press");
        thread.setDaemon(true);
        return thread;
    });

    private static final Map<String, ScheduledFuture<?>> longPressTimers = new ConcurrentHashMap<>();

    static {
        loadFromPreferences();
    }

    private static void loadFromPreferences() {
        shortcuts.clear();

        ActionRegistry.getInstance().getActions().forEach(action ->
                ShortcutSettings.getShortcut(action.getId())
                        .ifPresent(shortcut -> shortcuts.put(action.getId(), shortcut)));
    }

    public static ObservableMap<String, String> getShortcuts() {
        return shortcuts;
    }

    public static void setShortcut(String actionId, String shortcut) {
        ShortcutSettings.saveShortcut(actionId, shortcut);
        shortcuts.put(actionId, shortcut);
    }

    public static Optional<String> getShortcut(String actionId) {
        return Optional.ofNullable(shortcuts.getOrDefault(actionId, null));
    }

    public static void removeShortcut(String id) {
        shortcuts.remove(id);
        ShortcutSettings.removeShortcut(id);
    }

    public static Optional<String> getActionId(String shortcut) {
        if (!shortcuts.containsValue(shortcut)) {
            return Optional.empty();
        }

        return shortcuts.keySet().stream()
                .filter(actionId -> {
                    String currentShortcut = shortcuts.getOrDefault(actionId, "");
                    return currentShortcut != null && currentShortcut.equals(shortcut);
                })
                .findFirst();
    }


    public static void registerListener(Scene scene) {
        scene.addEventFilter(KEY_PRESSED, e -> {
            String shortcut = ShortcutConverter.toString(e);
            if (StringUtils.isEmpty(shortcut) || pressedKeys.contains(shortcut)) {
                return;
            }

            pressedKeys.add(shortcut);
            dispatch(shortcut, ShortcutEvent.SHORTCUT_PRESSED);
            scheduleLongPress(shortcut);
        });

        scene.addEventFilter(KEY_RELEASED, e -> {
            String shortcut = ShortcutConverter.toString(e);
            if (StringUtils.isEmpty(shortcut) || !pressedKeys.contains(shortcut)) {
                return;
            }
            pressedKeys.remove(shortcut);
            cancelLongPress(shortcut);
            dispatch(shortcut, ShortcutEvent.SHORTCUT_RELEASED);
        });
    }

    private static void dispatch(String shortcut, EventType<ShortcutEvent> eventType) {
        ShortcutService.getActionId(shortcut)
                .flatMap(id -> ActionRegistry.getInstance().getAction(id))
                .ifPresent(a -> Platform.runLater(() -> a.handle(new ShortcutEvent(eventType))));
    }

    private static void scheduleLongPress(String shortcut) {
        ScheduledFuture<?> timer = longPressExecutor.schedule(
                () -> dispatch(shortcut, ShortcutEvent.SHORTCUT_LONG_PRESSED),
                LONG_PRESS_DELAY,
                TimeUnit.MILLISECONDS);
        longPressTimers.put(shortcut, timer);
    }

    private static void cancelLongPress(String shortcut) {
        ScheduledFuture<?> timer = longPressTimers.remove(shortcut);
        if (timer != null) {
            timer.cancel(false);
        }
    }
}
