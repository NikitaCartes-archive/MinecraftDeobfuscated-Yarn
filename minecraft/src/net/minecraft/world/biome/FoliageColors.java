package net.minecraft.world.biome;

public class FoliageColors {
	private static int[] colorMap = new int[65536];

	public static void setColorMap(int[] pixels) {
		colorMap = pixels;
	}

	public static int getColor(double temperature, double humidity) {
		humidity *= temperature;
		int i = (int)((1.0 - temperature) * 255.0);
		int j = (int)((1.0 - humidity) * 255.0);
		int k = j << 8 | i;
		return k >= colorMap.length ? getDefaultColor() : colorMap[k];
	}

	public static int getSpruceColor() {
		return -10380959;
	}

	public static int getBirchColor() {
		return -8345771;
	}

	public static int getDefaultColor() {
		return -12012264;
	}

	public static int getMangroveColor() {
		return -7158200;
	}
}
