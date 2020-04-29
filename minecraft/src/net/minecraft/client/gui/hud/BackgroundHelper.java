package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BackgroundHelper {
	@Environment(EnvType.CLIENT)
	public static class ColorMixer {
		public static int getAlpha(int argb) {
			return argb >>> 24;
		}

		public static int getRed(int argb) {
			return argb >> 16 & 0xFF;
		}

		public static int getGreen(int argb) {
			return argb >> 8 & 0xFF;
		}

		public static int getBlue(int argb) {
			return argb & 0xFF;
		}

		public static int getArgb(int alpha, int red, int green, int blue) {
			return alpha << 24 | red << 16 | green << 8 | blue;
		}

		public static int mixColor(int first, int second) {
			return getArgb(
				getAlpha(first) * getAlpha(second) / 255,
				getRed(first) * getRed(second) / 255,
				getGreen(first) * getGreen(second) / 255,
				getBlue(first) * getBlue(second) / 255
			);
		}
	}
}
