/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.color.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class GrassColors {
    private static int[] colorMap = new int[65536];

    public static void setColorMap(int[] is) {
        colorMap = is;
    }

    public static int getColor(double d, double e) {
        int j = (int)((1.0 - (e *= d)) * 255.0);
        int i = (int)((1.0 - d) * 255.0);
        int k = j << 8 | i;
        if (k > colorMap.length) {
            return -65281;
        }
        return colorMap[k];
    }
}

