package net.minecraft.client.color.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GrassColors {
	private static int[] colorMap = new int[65536];

	public static void setColorMap(int[] is) {
		colorMap = is;
	}

	public static int getColor(double d, double e) {
		e *= d;
		int i = (int)((1.0 - d) * 255.0);
		int j = (int)((1.0 - e) * 255.0);
		int k = j << 8 | i;
		return k > colorMap.length ? -65281 : colorMap[k];
	}
}
