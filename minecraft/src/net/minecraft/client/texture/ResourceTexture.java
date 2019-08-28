package net.minecraft.client.texture;

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

	public ResourceTexture(Identifier identifier) {
		this.location = identifier;
	}

	@Override
	public void load(ResourceManager resourceManager) throws IOException {
		ResourceTexture.TextureData textureData = this.loadTextureData(resourceManager);
		Throwable var3 = null;

		try {
			boolean bl = false;
			boolean bl2 = false;
			textureData.checkException();
			TextureResourceMetadata textureResourceMetadata = textureData.getMetadata();
			if (textureResourceMetadata != null) {
				bl = textureResourceMetadata.shouldBlur();
				bl2 = textureResourceMetadata.shouldClamp();
			}

			this.bindTexture();
			TextureUtil.prepareImage(this.getGlId(), 0, textureData.getImage().getWidth(), textureData.getImage().getHeight());
			textureData.getImage().upload(0, 0, 0, 0, 0, textureData.getImage().getWidth(), textureData.getImage().getHeight(), bl, bl2, false);
		} catch (Throwable var14) {
			var3 = var14;
			throw var14;
		} finally {
			if (textureData != null) {
				if (var3 != null) {
					try {
						textureData.close();
					} catch (Throwable var13) {
						var3.addSuppressed(var13);
					}
				} else {
					textureData.close();
				}
			}
		}
	}

	protected ResourceTexture.TextureData loadTextureData(ResourceManager resourceManager) {
		return ResourceTexture.TextureData.load(resourceManager, this.location);
	}

	@Environment(EnvType.CLIENT)
	public static class TextureData implements Closeable {
		private final TextureResourceMetadata metadata;
		private final NativeImage image;
		private final IOException exception;

		public TextureData(IOException iOException) {
			this.exception = iOException;
			this.metadata = null;
			this.image = null;
		}

		public TextureData(@Nullable TextureResourceMetadata textureResourceMetadata, NativeImage nativeImage) {
			this.exception = null;
			this.metadata = textureResourceMetadata;
			this.image = nativeImage;
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
