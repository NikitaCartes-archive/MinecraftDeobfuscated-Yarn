/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.ImageFilter;
import net.minecraft.client.texture.NativeImage;

@Environment(value=EnvType.CLIENT)
public class SkinRemappingImageFilter
implements ImageFilter {
    @Override
    public NativeImage filterImage(NativeImage nativeImage) {
        boolean bl;
        boolean bl2 = bl = nativeImage.getHeight() == 32;
        if (bl) {
            NativeImage nativeImage2 = new NativeImage(64, 64, true);
            nativeImage2.copyFrom(nativeImage);
            nativeImage.close();
            nativeImage = nativeImage2;
            nativeImage.fillRect(0, 32, 64, 32, 0);
            nativeImage.copyRect(4, 16, 16, 32, 4, 4, true, false);
            nativeImage.copyRect(8, 16, 16, 32, 4, 4, true, false);
            nativeImage.copyRect(0, 20, 24, 32, 4, 12, true, false);
            nativeImage.copyRect(4, 20, 16, 32, 4, 12, true, false);
            nativeImage.copyRect(8, 20, 8, 32, 4, 12, true, false);
            nativeImage.copyRect(12, 20, 16, 32, 4, 12, true, false);
            nativeImage.copyRect(44, 16, -8, 32, 4, 4, true, false);
            nativeImage.copyRect(48, 16, -8, 32, 4, 4, true, false);
            nativeImage.copyRect(40, 20, 0, 32, 4, 12, true, false);
            nativeImage.copyRect(44, 20, -8, 32, 4, 12, true, false);
            nativeImage.copyRect(48, 20, -16, 32, 4, 12, true, false);
            nativeImage.copyRect(52, 20, -8, 32, 4, 12, true, false);
        }
        SkinRemappingImageFilter.method_3312(nativeImage, 0, 0, 32, 16);
        if (bl) {
            SkinRemappingImageFilter.method_3311(nativeImage, 32, 0, 64, 32);
        }
        SkinRemappingImageFilter.method_3312(nativeImage, 0, 16, 64, 32);
        SkinRemappingImageFilter.method_3312(nativeImage, 16, 48, 48, 64);
        return nativeImage;
    }

    @Override
    public void method_3238() {
    }

    private static void method_3311(NativeImage nativeImage, int i, int j, int k, int l) {
        int n;
        int m;
        for (m = i; m < k; ++m) {
            for (n = j; n < l; ++n) {
                int o = nativeImage.getPixelRgba(m, n);
                if ((o >> 24 & 0xFF) >= 128) continue;
                return;
            }
        }
        for (m = i; m < k; ++m) {
            for (n = j; n < l; ++n) {
                nativeImage.setPixelRgba(m, n, nativeImage.getPixelRgba(m, n) & 0xFFFFFF);
            }
        }
    }

    private static void method_3312(NativeImage nativeImage, int i, int j, int k, int l) {
        for (int m = i; m < k; ++m) {
            for (int n = j; n < l; ++n) {
                nativeImage.setPixelRgba(m, n, nativeImage.getPixelRgba(m, n) | 0xFF000000);
            }
        }
    }
}

