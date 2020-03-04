package net.minecraft.client.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.Closeable;
import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ResourceTexture extends AbstractTexture {
	private static final Logger LOGGER = LogManager.getLogger();
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
		boolean bl2;
		if (textureResourceMetadata != null) {
			bl = textureResourceMetadata.shouldBlur();
			bl2 = textureResourceMetadata.shouldClamp();
		} else {
			bl = false;
			bl2 = false;
		}

		NativeImage nativeImage = textureData.getImage();
		if (!RenderSystem.isOnRenderThreadOrInit()) {
			RenderSystem.recordRenderCall(() -> this.method_22810(nativeImage, bl, bl2));
		} else {
			this.method_22810(nativeImage, bl, bl2);
		}
	}

	private void method_22810(NativeImage nativeImage, boolean bl, boolean bl2) {
		TextureUtil.method_24959(this.getGlId(), 0, nativeImage.getWidth(), nativeImage.getHeight());
		nativeImage.upload(0, 0, 0, 0, 0, nativeImage.getWidth(), nativeImage.getHeight(), bl, bl2, false, true);
	}

	protected ResourceTexture.TextureData loadTextureData(ResourceManager resourceManager) {
		return ResourceTexture.TextureData.load(resourceManager, this.location);
	}

	@Environment(EnvType.CLIENT)
	public static class TextureData implements Closeable {
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

		public static ResourceTexture.TextureData load(ResourceManager resourceManager, Identifier identifier) {
			try {
				Resource resource = resourceManager.getResource(identifier);
				Throwable var3 = null;

				ResourceTexture.TextureData runtimeException;
				try {
					NativeImage nativeImage = NativeImage.read(resource.getInputStream());
					TextureResourceMetadata textureResourceMetadata = null;

					try {
						textureResourceMetadata = resource.getMetadata(TextureResourceMetadata.READER);
					} catch (RuntimeException var17) {
						ResourceTexture.LOGGER.warn("Failed reading metadata of: {}", identifier, var17);
					}

					runtimeException = new ResourceTexture.TextureData(textureResourceMetadata, nativeImage);
				} catch (Throwable var18) {
					var3 = var18;
					throw var18;
				} finally {
					if (resource != null) {
						if (var3 != null) {
							try {
								resource.close();
							} catch (Throwable var16) {
								var3.addSuppressed(var16);
							}
						} else {
							resource.close();
						}
					}
				}

				return runtimeException;
			} catch (IOException var20) {
				return new ResourceTexture.TextureData(var20);
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
