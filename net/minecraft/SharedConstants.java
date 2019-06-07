/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.bridge.game.GameVersion;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.types.constant.NamespacedStringType;
import io.netty.util.ResourceLeakDetector;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.MinecraftVersion;
import net.minecraft.command.TranslatableBuiltInExceptions;
import net.minecraft.datafixers.schemas.SchemaIdentifierNormalize;

public class SharedConstants {
    public static final ResourceLeakDetector.Level RESOURCE_LEAK_DETECTOR_DISABLED = ResourceLeakDetector.Level.DISABLED;
    public static boolean isDevelopment;
    public static final char[] INVALID_CHARS_LEVEL_NAME;
    private static GameVersion gameVersion;

    public static boolean isValidChar(char c) {
        return c != '\u00a7' && c >= ' ' && c != '\u007f';
    }

    public static String stripInvalidChars(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : string.toCharArray()) {
            if (!SharedConstants.isValidChar(c)) continue;
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    @Environment(value=EnvType.CLIENT)
    public static String stripSupplementaryChars(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        while (i < string.length()) {
            int j = string.codePointAt(i);
            if (!Character.isSupplementaryCodePoint(j)) {
                stringBuilder.appendCodePoint(j);
            } else {
                stringBuilder.append('\ufffd');
            }
            i = string.offsetByCodePoints(i, 1);
        }
        return stringBuilder.toString();
    }

    public static GameVersion getGameVersion() {
        if (gameVersion == null) {
            gameVersion = MinecraftVersion.create();
        }
        return gameVersion;
    }

    static {
        INVALID_CHARS_LEVEL_NAME = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};
        ResourceLeakDetector.setLevel(RESOURCE_LEAK_DETECTOR_DISABLED);
        CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES = false;
        CommandSyntaxException.BUILT_IN_EXCEPTIONS = new TranslatableBuiltInExceptions();
        NamespacedStringType.ENSURE_NAMESPACE = SchemaIdentifierNormalize::normalize;
    }
}

