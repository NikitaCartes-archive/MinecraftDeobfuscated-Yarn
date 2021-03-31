/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.color.world;

public class FoliageColors {
    private static int[] colorMap = new int[65536];

    public static void setColorMap(int[] pixels) {
        colorMap = pixels;
    }

    public static int getColor(double temperature, double humidity) {
        int i = (int)((1.0 - temperature) * 255.0);
        int j = (int)((1.0 - (humidity *= temperature)) * 255.0);
        return colorMap[j << 8 | i];
    }

    public static int getSpruceColor() {
        return 0x619961;
    }

    public static int getBirchColor() {
        return 8431445;
    }

    public static int getDefaultColor() {
        return 4764952;
    }
}

