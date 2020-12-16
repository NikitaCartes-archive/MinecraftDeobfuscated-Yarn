/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.bridge.game.GameVersion;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.netty.util.ResourceLeakDetector;
import java.time.Duration;
import net.minecraft.MinecraftVersion;
import net.minecraft.command.TranslatableBuiltInExceptions;

public class SharedConstants {
    public static final ResourceLeakDetector.Level RESOURCE_LEAK_DETECTOR_DISABLED = ResourceLeakDetector.Level.DISABLED;
    public static final long field_22251 = Duration.ofMillis(300L).toNanos();
    /**
     * Specifies whether Minecraft should use choice type registrations from the game's schema when entity types or block entity types are created.
     */
    public static boolean useChoiceTypeRegistrations = true;
    public static boolean isDevelopment;
    public static final char[] INVALID_CHARS_LEVEL_NAME;
    private static GameVersion gameVersion;

    public static boolean isValidChar(char chr) {
        return chr != '\u00a7' && chr >= ' ' && chr != '\u007f';
    }

    public static String stripInvalidChars(String s) {
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (!SharedConstants.isValidChar(c)) continue;
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    public static GameVersion getGameVersion() {
        if (gameVersion == null) {
            gameVersion = MinecraftVersion.create();
        }
        return gameVersion;
    }

    public static int getProtocolVersion() {
        return 0x40000009;
    }

    static {
        INVALID_CHARS_LEVEL_NAME = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};
        ResourceLeakDetector.setLevel(RESOURCE_LEAK_DETECTOR_DISABLED);
        CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES = false;
        CommandSyntaxException.BUILT_IN_EXCEPTIONS = new TranslatableBuiltInExceptions();
    }
}

