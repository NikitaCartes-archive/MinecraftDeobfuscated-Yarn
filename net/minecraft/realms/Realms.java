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

    public static void narrateNow(String string) {
        NarratorManager narratorManager = NarratorManager.INSTANCE;
        narratorManager.clear();
        narratorManager.onChatMessage(MessageType.SYSTEM, new LiteralText(Realms.fixNarrationNewlines(string)));
    }

    private static String fixNarrationNewlines(String string) {
        return string.replace("\\n", System.lineSeparator());
    }

    public static void narrateNow(String ... strings) {
        Realms.narrateNow(Arrays.asList(strings));
    }

    public static void narrateNow(Iterable<String> iterable) {
        Realms.narrateNow(Realms.joinNarrations(iterable));
    }

    public static String joinNarrations(Iterable<String> iterable) {
        return String.join((CharSequence)System.lineSeparator(), iterable);
    }

    public static void narrateRepeatedly(String string) {
        REPEATED_NARRATOR.narrate(Realms.fixNarrationNewlines(string));
    }
}

