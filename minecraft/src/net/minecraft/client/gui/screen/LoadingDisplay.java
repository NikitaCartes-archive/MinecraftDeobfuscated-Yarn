package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class LoadingDisplay {
	private static final String[] TEXTS = new String[]{"O o o", "o O o", "o o O", "o O o"};
	private static final long INTERVAL = 300L;

	public static String get(long tick) {
		int i = (int)(tick / 300L % (long)TEXTS.length);
		return TEXTS[i];
	}
}
