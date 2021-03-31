/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.Closeable;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ResourceTexture
extends AbstractTexture {
    private static final Logger LOGGER = LogManager.getLogger();
    protected final Identifier location;

    public ResourceTexture(Identifier location) {
        this.location = location;
    }

    @Override
    public void load(ResourceManager manager) throws IOException {
        boolean bl2;
        boolean bl;
        TextureData textureData = this.loadTextureData(manager);
        textureData.checkException();
        TextureResourceMetadata textureResourceMetadata = textureData.getMetadata();
        if (textureResourceMetadata != null) {
            bl = textureResourceMetadata.shouldBlur();
            bl2 = textureResourceMetadata.shouldClamp();
        } else {
            bl = false;
            bl2 = false;
        }
        NativeImage nativeImage = textureData.getImage();
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> this.upload(nativeImage, bl, bl2));
        } else {
            this.upload(nativeImage, bl, bl2);
        }
    }

    private void upload(NativeImage nativeImage, boolean blur, boolean clamp) {
        TextureUtil.prepareImage(this.getGlId(), 0, nativeImage.getWidth(), nativeImage.getHeight());
        nativeImage.upload(0, 0, 0, 0, 0, nativeImage.getWidth(), nativeImage.getHeight(), blur, clamp, false, true);
    }

    protected TextureData loadTextureData(ResourceManager resourceManager) {
        return TextureData.load(resourceManager, this.location);
    }

    @Environment(value=EnvType.CLIENT)
    public static class TextureData
    implements Closeable {
        @Nullable
        private final TextureResourceMetadata metadata;
        @Nullable
        private final NativeImage image;
        @Nullable
        private final IOException exception;

        public TextureData(IOException exception) {
            this.exception = exception;
            this.metadata = null;
            this.image = null;
        }

        public TextureData(@Nullable TextureResourceMetadata metadata, NativeImage image) {
            this.exception = null;
            this.metadata = metadata;
            this.image = image;
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public static TextureData load(ResourceManager resourceManager, Identifier identifier) {
            try (Resource resource = resourceManager.getResource(identifier);){
                NativeImage nativeImage = NativeImage.read(resource.getInputStream());
                TextureResourceMetadata textureResourceMetadata = null;
                try {
                    textureResourceMetadata = resource.getMetadata(TextureResourceMetadata.READER);
                } catch (RuntimeException runtimeException) {
                    LOGGER.warn("Failed reading metadata of: {}", (Object)identifier, (Object)runtimeException);
                }
                TextureData textureData = new TextureData(textureResourceMetadata, nativeImage);
                return textureData;
            } catch (IOException iOException) {
                return new TextureData(iOException);
            }
        }

        @Nullable
        public TextureResourceMetadata getMetadata() {
            return this.metadata;
        }

        public NativeImage getImage() throws IOException {
            if (this.exception != null) {
                throw this.exception;
            }
            return this.image;
        }

        @Override
        public void close() {
            if (this.image != null) {
                this.image.close();
            }
        }

        public void checkException() throws IOException {
            if (this.exception != null) {
                throw this.exception;
            }
        }
    }
}

