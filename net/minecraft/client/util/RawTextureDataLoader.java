/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class RawTextureDataLoader {
    @Deprecated
    public static int[] loadRawTextureData(ResourceManager resourceManager, Identifier identifier) throws IOException {
        try (Resource resource = resourceManager.getResource(identifier);){
            NativeImage nativeImage = NativeImage.read(resource.getInputStream());
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

