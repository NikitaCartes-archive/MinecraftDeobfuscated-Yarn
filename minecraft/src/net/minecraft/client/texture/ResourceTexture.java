package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.TextureUtil;
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
		ResourceTexture.class_4006 lv = this.method_18153(resourceManager);
		Throwable var3 = null;

		try {
			boolean bl = false;
			boolean bl2 = false;
			lv.method_18158();
			TextureResourceMetadata textureResourceMetadata = lv.method_18155();
			if (textureResourceMetadata != null) {
				bl = textureResourceMetadata.shouldBlur();
				bl2 = textureResourceMetadata.shouldClamp();
			}

			this.bindTexture();
			TextureUtil.prepareImage(this.getGlId(), 0, lv.method_18157().getWidth(), lv.method_18157().getHeight());
			lv.method_18157().upload(0, 0, 0, 0, 0, lv.method_18157().getWidth(), lv.method_18157().getHeight(), bl, bl2, false);
		} catch (Throwable var14) {
			var3 = var14;
			throw var14;
		} finally {
			if (lv != null) {
				if (var3 != null) {
					try {
						lv.close();
					} catch (Throwable var13) {
						var3.addSuppressed(var13);
					}
				} else {
					lv.close();
				}
			}
		}
	}

	protected ResourceTexture.class_4006 method_18153(ResourceManager resourceManager) {
		return ResourceTexture.class_4006.method_18156(resourceManager, this.location);
	}

	@Environment(EnvType.CLIENT)
	public static class class_4006 implements Closeable {
		private final TextureResourceMetadata field_17895;
		private final NativeImage field_17896;
		private final IOException field_17897;

		public class_4006(IOException iOException) {
			this.field_17897 = iOException;
			this.field_17895 = null;
			this.field_17896 = null;
		}

		public class_4006(@Nullable TextureResourceMetadata textureResourceMetadata, NativeImage nativeImage) {
			this.field_17897 = null;
			this.field_17895 = textureResourceMetadata;
			this.field_17896 = nativeImage;
		}

		public static ResourceTexture.class_4006 method_18156(ResourceManager resourceManager, Identifier identifier) {
			try {
				Resource resource = resourceManager.getResource(identifier);
				Throwable var3 = null;

				ResourceTexture.class_4006 runtimeException;
				try {
					NativeImage nativeImage = NativeImage.fromInputStream(resource.getInputStream());
					TextureResourceMetadata textureResourceMetadata = null;

					try {
						textureResourceMetadata = resource.getMetadata(TextureResourceMetadata.READER);
					} catch (RuntimeException var17) {
						ResourceTexture.LOGGER.warn("Failed reading metadata of: {}", identifier, var17);
					}

					runtimeException = new ResourceTexture.class_4006(textureResourceMetadata, nativeImage);
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
				return new ResourceTexture.class_4006(var20);
			}
		}

		@Nullable
		public TextureResourceMetadata method_18155() {
			return this.field_17895;
		}

		public NativeImage method_18157() throws IOException {
			if (this.field_17897 != null) {
				throw this.field_17897;
			} else {
				return this.field_17896;
			}
		}

		public void close() {
			if (this.field_17896 != null) {
				this.field_17896.close();
			}
		}

		public void method_18158() throws IOException {
			if (this.field_17897 != null) {
				throw this.field_17897;
			}
		}
	}
}
