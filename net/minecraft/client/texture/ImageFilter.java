/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;

@Environment(value=EnvType.CLIENT)
public interface ImageFilter {
    public NativeImage filterImage(NativeImage var1);

    public void method_3238();
}

