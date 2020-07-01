package net.minecraft.client.realms;

import java.time.Duration;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class Realms {
	private static final RepeatedNarrator REPEATED_NARRATOR = new RepeatedNarrator(Duration.ofSeconds(5L));

	public static void narrateNow(String message) {
		NarratorManager narratorManager = NarratorManager.INSTANCE;
		narratorManager.clear();
		narratorManager.onChatMessage(MessageType.SYSTEM, new LiteralText(fixNarrationNewlines(message)), Util.NIL_UUID);
	}

	private static String fixNarrationNewlines(String lines) {
		return lines.replace("\\n", System.lineSeparator());
	}

	public static void narrateNow(String... lines) {
		narrateNow(Arrays.asList(lines));
	}

	public static void narrateNow(Iterable<String> lines) {
		narrateNow(joinNarrations(lines));
	}

	public static String joinNarrations(Iterable<String> lines) {
		return String.join(System.lineSeparator(), lines);
	}

	public static void narrateRepeatedly(String lines) {
		REPEATED_NARRATOR.narrate(fixNarrationNewlines(lines));
	}
}
