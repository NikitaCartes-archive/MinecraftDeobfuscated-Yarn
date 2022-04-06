/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util;

import java.io.IOException;
import java.io.InputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class RawTextureDataLoader {
    @Deprecated
    public static int[] loadRawTextureData(ResourceManager resourceManager, Identifier id) throws IOException {
        try (InputStream inputStream = resourceManager.open(id);){
            NativeImage nativeImage = NativeImage.read(inputStream);
            try {
                int[] nArray = nativeImage.makePixelArray();
                if (nativeImage != null) {
                    nativeImage.close();
                }
                return nArray;
            } catch (Throwable throwable) {
                if (nativeImage != null) {
                    try {
                        nativeImage.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
        }
    }
}

