/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class MipmapHelper {
    private static final int field_32949 = 96;
    private static final float[] COLOR_FRACTIONS = Util.make(new float[256], list -> {
        for (int i = 0; i < ((float[])list).length; ++i) {
            list[i] = (float)Math.pow((float)i / 255.0f, 2.2);
        }
    });

    private MipmapHelper() {
    }

    public static NativeImage[] getMipmapLevelsImages(NativeImage[] originals, int mipmap) {
        if (mipmap + 1 <= originals.length) {
            return originals;
        }
        NativeImage[] nativeImages = new NativeImage[mipmap + 1];
        nativeImages[0] = originals[0];
        boolean bl = MipmapHelper.hasAlpha(nativeImages[0]);
        for (int i = 1; i <= mipmap; ++i) {
            if (i < originals.length) {
                nativeImages[i] = originals[i];
                continue;
            }
            NativeImage nativeImage = nativeImages[i - 1];
            NativeImage nativeImage2 = new NativeImage(nativeImage.getWidth() >> 1, nativeImage.getHeight() >> 1, false);
            int j = nativeImage2.getWidth();
            int k = nativeImage2.getHeight();
            for (int l = 0; l < j; ++l) {
                for (int m = 0; m < k; ++m) {
                    nativeImage2.setColor(l, m, MipmapHelper.blend(nativeImage.getColor(l * 2 + 0, m * 2 + 0), nativeImage.getColor(l * 2 + 1, m * 2 + 0), nativeImage.getColor(l * 2 + 0, m * 2 + 1), nativeImage.getColor(l * 2 + 1, m * 2 + 1), bl));
                }
            }
            nativeImages[i] = nativeImage2;
        }
        return nativeImages;
    }

    private static boolean hasAlpha(NativeImage image) {
        for (int i = 0; i < image.getWidth(); ++i) {
            for (int j = 0; j < image.getHeight(); ++j) {
                if (image.getColor(i, j) >> 24 != 0) continue;
                return true;
            }
        }
        return false;
    }

    private static int blend(int one, int two, int three, int four, boolean checkAlpha) {
        if (checkAlpha) {
            float f = 0.0f;
            float g = 0.0f;
            float h = 0.0f;
            float i = 0.0f;
            if (one >> 24 != 0) {
                f += MipmapHelper.getColorFraction(one >> 24);
                g += MipmapHelper.getColorFraction(one >> 16);
                h += MipmapHelper.getColorFraction(one >> 8);
                i += MipmapHelper.getColorFraction(one >> 0);
            }
            if (two >> 24 != 0) {
                f += MipmapHelper.getColorFraction(two >> 24);
                g += MipmapHelper.getColorFraction(two >> 16);
                h += MipmapHelper.getColorFraction(two >> 8);
                i += MipmapHelper.getColorFraction(two >> 0);
            }
            if (three >> 24 != 0) {
                f += MipmapHelper.getColorFraction(three >> 24);
                g += MipmapHelper.getColorFraction(three >> 16);
                h += MipmapHelper.getColorFraction(three >> 8);
                i += MipmapHelper.getColorFraction(three >> 0);
            }
            if (four >> 24 != 0) {
                f += MipmapHelper.getColorFraction(four >> 24);
                g += MipmapHelper.getColorFraction(four >> 16);
                h += MipmapHelper.getColorFraction(four >> 8);
                i += MipmapHelper.getColorFraction(four >> 0);
            }
            int j = (int)(Math.pow(f /= 4.0f, 0.45454545454545453) * 255.0);
            int k = (int)(Math.pow(g /= 4.0f, 0.45454545454545453) * 255.0);
            int l = (int)(Math.pow(h /= 4.0f, 0.45454545454545453) * 255.0);
            int m = (int)(Math.pow(i /= 4.0f, 0.45454545454545453) * 255.0);
            if (j < 96) {
                j = 0;
            }
            return j << 24 | k << 16 | l << 8 | m;
        }
        int n = MipmapHelper.getColorComponent(one, two, three, four, 24);
        int o = MipmapHelper.getColorComponent(one, two, three, four, 16);
        int p = MipmapHelper.getColorComponent(one, two, three, four, 8);
        int q = MipmapHelper.getColorComponent(one, two, three, four, 0);
        return n << 24 | o << 16 | p << 8 | q;
    }

    private static int getColorComponent(int one, int two, int three, int four, int bits) {
        float f = MipmapHelper.getColorFraction(one >> bits);
        float g = MipmapHelper.getColorFraction(two >> bits);
        float h = MipmapHelper.getColorFraction(three >> bits);
        float i = MipmapHelper.getColorFraction(four >> bits);
        float j = (float)((double)((float)Math.pow((double)(f + g + h + i) * 0.25, 0.45454545454545453)));
        return (int)((double)j * 255.0);
    }

    private static float getColorFraction(int value) {
        return COLOR_FRACTIONS[value & 0xFF];
    }
}

