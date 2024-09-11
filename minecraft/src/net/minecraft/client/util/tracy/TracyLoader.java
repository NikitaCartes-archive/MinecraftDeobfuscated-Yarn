package net.minecraft.client.util.tracy;

import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogListeners;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.event.Level;

@Environment(EnvType.CLIENT)
public class TracyLoader {
	private static boolean loaded;

	public static void load() {
		if (!loaded) {
			TracyClient.load();
			if (TracyClient.isAvailable()) {
				LogListeners.addListener("Tracy", (message, level) -> TracyClient.message(message, getColor(level)));
				loaded = true;
			}
		}
	}

	private static int getColor(Level level) {
		return switch (level) {
			case DEBUG -> 11184810;
			case WARN -> 16777130;
			case ERROR -> 16755370;
			default -> 16777215;
		};
	}
}
