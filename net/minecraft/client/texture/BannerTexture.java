/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4536;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class BannerTexture
extends AbstractTexture {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Identifier filename;
    private final List<String> patternNames;
    private final List<DyeColor> dyes;

    public BannerTexture(Identifier identifier, List<String> list, List<DyeColor> list2) {
        this.filename = identifier;
        this.patternNames = list;
        this.dyes = list2;
    }

    @Override
    public void load(ResourceManager resourceManager) throws IOException {
        try (Resource resource = resourceManager.getResource(this.filename);
             NativeImage nativeImage = NativeImage.read(resource.getInputStream());
             NativeImage nativeImage2 = new NativeImage(nativeImage.getWidth(), nativeImage.getHeight(), false);){
            nativeImage2.copyFrom(nativeImage);
            for (int i = 0; i < 17 && i < this.patternNames.size() && i < this.dyes.size(); ++i) {
                String string = this.patternNames.get(i);
                if (string == null) continue;
                try (Resource resource2 = resourceManager.getResource(new Identifier(string));
                     NativeImage nativeImage3 = NativeImage.read(resource2.getInputStream());){
                    int j = this.dyes.get(i).getColorSwapped();
                    if (nativeImage3.getWidth() != nativeImage2.getWidth() || nativeImage3.getHeight() != nativeImage2.getHeight()) continue;
                    for (int k = 0; k < nativeImage3.getHeight(); ++k) {
                        for (int l = 0; l < nativeImage3.getWidth(); ++l) {
                            int m = nativeImage3.getPixelRGBA(l, k);
                            if ((m & 0xFF000000) == 0) continue;
                            int n = (m & 0xFF) << 24 & 0xFF000000;
                            int o = nativeImage.getPixelRGBA(l, k);
                            int p = MathHelper.multiplyColors(o, j) & 0xFFFFFF;
                            nativeImage2.blendPixel(l, k, n | p);
                        }
                    }
                    continue;
                }
            }
            class_4536.prepareImage(this.getGlId(), nativeImage2.getWidth(), nativeImage2.getHeight());
            RenderSystem.pixelTransfer(3357, Float.MAX_VALUE);
            nativeImage2.upload(0, 0, 0, false);
            RenderSystem.pixelTransfer(3357, 0.0f);
        } catch (IOException iOException) {
            LOGGER.error("Couldn't load layered color mask image", (Throwable)iOException);
        }
    }
}

