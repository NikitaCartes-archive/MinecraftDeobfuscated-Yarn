/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4588;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;

@Environment(value=EnvType.CLIENT)
public class class_4608
implements AutoCloseable {
    private final NativeImageBackedTexture field_21013 = new NativeImageBackedTexture(16, 16, false);

    public class_4608() {
        NativeImage nativeImage = this.field_21013.getImage();
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                if (i < 8) {
                    nativeImage.setPixelRGBA(j, i, -1308622593);
                    continue;
                }
                int k = (int)((1.0f - (float)j / 15.0f * 0.2f) * 255.0f);
                nativeImage.setPixelRGBA(j, i, k << 24 | 0xFFFFFF);
            }
        }
        RenderSystem.activeTexture(33985);
        this.field_21013.method_23207();
        RenderSystem.matrixMode(5890);
        RenderSystem.loadIdentity();
        float f = 0.06666667f;
        RenderSystem.scalef(0.06666667f, 0.06666667f, 0.06666667f);
        RenderSystem.matrixMode(5888);
        this.field_21013.method_23207();
        nativeImage.method_22619(0, 0, 0, 0, 0, nativeImage.getWidth(), nativeImage.getHeight(), false, true, false, false);
        RenderSystem.activeTexture(33984);
    }

    @Override
    public void close() {
        this.field_21013.close();
    }

    public void method_23209() {
        RenderSystem.setupOverlayColor(this.field_21013::getGlId, 16);
    }

    public static int method_23210(float f) {
        return (int)(f * 15.0f);
    }

    public static int method_23212(boolean bl) {
        return bl ? 3 : 10;
    }

    public void method_23213() {
        RenderSystem.teardownOverlayColor();
    }

    public static void method_23211(class_4588 arg) {
        arg.method_22922(0, 10);
    }
}

