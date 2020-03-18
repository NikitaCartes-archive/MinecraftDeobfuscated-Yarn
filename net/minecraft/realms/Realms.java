/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.realms;

import java.time.Duration;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.network.MessageType;
import net.minecraft.realms.RepeatedNarrator;
import net.minecraft.text.LiteralText;

@Environment(value=EnvType.CLIENT)
public class Realms {
    private static final RepeatedNarrator REPEATED_NARRATOR = new RepeatedNarrator(Duration.ofSeconds(5L));

    public static void narrateNow(String message) {
        NarratorManager narratorManager = NarratorManager.INSTANCE;
        narratorManager.clear();
        narratorManager.onChatMessage(MessageType.SYSTEM, new LiteralText(Realms.fixNarrationNewlines(message)));
    }

    private static String fixNarrationNewlines(String lines) {
        return lines.replace("\\n", System.lineSeparator());
    }

    public static void narrateNow(String ... lines) {
        Realms.narrateNow(Arrays.asList(lines));
    }

    public static void narrateNow(Iterable<String> lines) {
        Realms.narrateNow(Realms.joinNarrations(lines));
    }

    public static String joinNarrations(Iterable<String> lines) {
        return String.join((CharSequence)System.lineSeparator(), lines);
    }

    public static void narrateRepeatedly(String lines) {
        REPEATED_NARRATOR.narrate(Realms.fixNarrationNewlines(lines));
    }
}

