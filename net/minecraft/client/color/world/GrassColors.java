/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.color.world;

public class GrassColors {
    private static int[] colorMap = new int[65536];

    public static void setColorMap(int[] map) {
        colorMap = map;
    }

    public static int getColor(double temperature, double humidity) {
        int j = (int)((1.0 - (humidity *= temperature)) * 255.0);
        int i = (int)((1.0 - temperature) * 255.0);
        int k = j << 8 | i;
        if (k >= colorMap.length) {
            return -65281;
        }
        return colorMap[k];
    }
}

