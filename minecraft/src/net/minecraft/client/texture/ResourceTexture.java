package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ResourceTexture extends AbstractTexture {
	static final Logger LOGGER = LogUtils.getLogger();
	protected final Identifier location;

	public ResourceTexture(Identifier location) {
		this.location = location;
	}

	@Override
	public void load(ResourceManager manager) throws IOException {
		ResourceTexture.TextureData textureData = this.loadTextureData(manager);
		textureData.checkException();
		TextureResourceMetadata textureResourceMetadata = textureData.getMetadata();
		boolean bl;
		if (textureResourceMetadata != null) {
			this.bilinear = textureResourceMetadata.shouldBlur();
			bl = textureResourceMetadata.shouldClamp();
		} else {
			this.bilinear = false;
			bl = false;
		}

		NativeImage nativeImage = textureData.getImage();
		if (!RenderSystem.isOnRenderThreadOrInit()) {
			RenderSystem.recordRenderCall(() -> this.upload(nativeImage, this.bilinear, bl));
		} else {
			this.upload(nativeImage, this.bilinear, bl);
		}
	}

	private void upload(NativeImage image, boolean blur, boolean clamp) {
		TextureUtil.prepareImage(this.getGlId(), 0, image.getWidth(), image.getHeight());
		image.upload(0, 0, 0, 0, 0, image.getWidth(), image.getHeight(), blur, clamp, false, true);
	}

	protected ResourceTexture.TextureData loadTextureData(ResourceManager resourceManager) {
		return ResourceTexture.TextureData.load(resourceManager, this.location);
	}

	@Environment(EnvType.CLIENT)
	protected static class TextureData implements Closeable {
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

		public static ResourceTexture.TextureData load(ResourceManager resourceManager, Identifier id) {
			try {
				Resource resource = resourceManager.getResourceOrThrow(id);
				InputStream inputStream = resource.getInputStream();

				NativeImage nativeImage;
				try {
					nativeImage = NativeImage.read(inputStream);
				} catch (Throwable var9) {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (Throwable var7) {
							var9.addSuppressed(var7);
						}
					}

					throw var9;
				}

				if (inputStream != null) {
					inputStream.close();
				}

				TextureResourceMetadata textureResourceMetadata = null;

				try {
					textureResourceMetadata = (TextureResourceMetadata)resource.getMetadata().decode(TextureResourceMetadata.READER).orElse(null);
				} catch (RuntimeException var8) {
					ResourceTexture.LOGGER.warn("Failed reading metadata of: {}", id, var8);
				}

				return new ResourceTexture.TextureData(textureResourceMetadata, nativeImage);
			} catch (IOException var10) {
				return new ResourceTexture.TextureData(var10);
			}
		}

		@Nullable
		public TextureResourceMetadata getMetadata() {
			return this.metadata;
		}

		public NativeImage getImage() throws IOException {
			if (this.exception != null) {
				throw this.exception;
			} else {
				return this.image;
			}
		}

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
