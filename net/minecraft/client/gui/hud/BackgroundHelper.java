/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class BackgroundHelper {

    public static class ColorMixer {
        @Environment(value=EnvType.CLIENT)
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

        @Environment(value=EnvType.CLIENT)
        public static int getArgb(int alpha, int red, int green, int blue) {
            return alpha << 24 | red << 16 | green << 8 | blue;
        }

        @Environment(value=EnvType.CLIENT)
        public static int mixColor(int first, int second) {
            return ColorMixer.getArgb(ColorMixer.getAlpha(first) * ColorMixer.getAlpha(second) / 255, ColorMixer.getRed(first) * ColorMixer.getRed(second) / 255, ColorMixer.getGreen(first) * ColorMixer.getGreen(second) / 255, ColorMixer.getBlue(first) * ColorMixer.getBlue(second) / 255);
        }
    }
}

