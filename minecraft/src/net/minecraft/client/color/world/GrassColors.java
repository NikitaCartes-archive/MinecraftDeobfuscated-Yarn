package net.minecraft.client.color.world;

public class GrassColors {
	private static int[] colorMap = new int[65536];

	public static void setColorMap(int[] map) {
		colorMap = map;
	}

	public static int getColor(double temperature, double humidity) {
		humidity *= temperature;
		int i = (int)((1.0 - temperature) * 255.0);
		int j = (int)((1.0 - humidity) * 255.0);
		int k = j << 8 | i;
		return k >= colorMap.length ? -65281 : colorMap[k];
	}

	public static int getDefaultColor() {
		return getColor(0.5, 1.0);
	}
}
