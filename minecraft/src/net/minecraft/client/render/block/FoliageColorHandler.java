package net.minecraft.client.render.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FoliageColorHandler {
	private static int[] colorMap = new int[65536];

	public static void setColorMap(int[] is) {
		colorMap = is;
	}

	public static int getColor(double d, double e) {
		e *= d;
		int i = (int)((1.0 - d) * 255.0);
		int j = (int)((1.0 - e) * 255.0);
		return colorMap[j << 8 | i];
	}

	public static int getSpruceColor() {
		return 6396257;
	}

	public static int getBirchColor() {
		return 8431445;
	}

	public static int getDefaultColor() {
		return 4764952;
	}
}
