package net.minecraft.realms;

import java.time.Duration;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;

@Environment(EnvType.CLIENT)
public class Realms {
	private static final RepeatedNarrator REPEATED_NARRATOR = new RepeatedNarrator(Duration.ofSeconds(5L));

	public static void narrateNow(String string) {
		NarratorManager narratorManager = NarratorManager.INSTANCE;
		narratorManager.clear();
		narratorManager.onChatMessage(MessageType.SYSTEM, new LiteralText(fixNarrationNewlines(string)));
	}

	private static String fixNarrationNewlines(String string) {
		return string.replace("\\n", System.lineSeparator());
	}

	public static void narrateNow(String... strings) {
		narrateNow(Arrays.asList(strings));
	}

	public static void narrateNow(Iterable<String> iterable) {
		narrateNow(joinNarrations(iterable));
	}

	public static String joinNarrations(Iterable<String> iterable) {
		return String.join(System.lineSeparator(), iterable);
	}

	public static void narrateRepeatedly(String string) {
		REPEATED_NARRATOR.narrate(fixNarrationNewlines(string));
	}
}
