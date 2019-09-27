/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.SystemUtil;

@Environment(value=EnvType.CLIENT)
public enum class_4591 implements RenderableGlyph
{
    INSTANCE;

    private static final NativeImage field_20913;

    @Override
    public int getWidth() {
        return 5;
    }

    @Override
    public int getHeight() {
        return 8;
    }

    @Override
    public float getAdvance() {
        return 6.0f;
    }

    @Override
    public float getOversample() {
        return 1.0f;
    }

    @Override
    public void upload(int i, int j) {
        field_20913.upload(0, i, j, false);
    }

    @Override
    public boolean hasColor() {
        return true;
    }

    static {
        field_20913 = SystemUtil.consume(new NativeImage(NativeImage.Format.RGBA, 5, 8, false), nativeImage -> {
            for (int i = 0; i < 8; ++i) {
                for (int j = 0; j < 5; ++j) {
                    boolean bl = j == 0 || j + 1 == 5 || i == 0 || i + 1 == 8;
                    nativeImage.setPixelRGBA(j, i, -1);
                }
            }
            nativeImage.untrack();
        });
    }
}

