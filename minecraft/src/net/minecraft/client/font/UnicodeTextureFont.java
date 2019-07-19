package net.minecraft.client.font;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class UnicodeTextureFont implements Font {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ResourceManager resourceManager;
	private final byte[] sizes;
	private final String template;
	private final Map<Identifier, NativeImage> images = Maps.<Identifier, NativeImage>newHashMap();

	public UnicodeTextureFont(ResourceManager resourceManager, byte[] sizes, String template) {
		this.resourceManager = resourceManager;
		this.sizes = sizes;
		this.template = template;

		for (int i = 0; i < 256; i++) {
			char c = (char)(i * 256);
			Identifier identifier = this.getImageId(c);

			try {
				Resource resource = this.resourceManager.getResource(identifier);
				Throwable var8 = null;

				try (NativeImage nativeImage = NativeImage.read(NativeImage.Format.RGBA, resource.getInputStream())) {
					if (nativeImage.getWidth() == 256 && nativeImage.getHeight() == 256) {
						for (int j = 0; j < 256; j++) {
							byte b = sizes[c + j];
							if (b != 0 && method_2043(b) > method_2044(b)) {
								sizes[c + j] = 0;
							}
						}
						continue;
					}
				} catch (Throwable var41) {
					var8 = var41;
					throw var41;
				} finally {
					if (resource != null) {
						if (var8 != null) {
							try {
								resource.close();
							} catch (Throwable var37) {
								var8.addSuppressed(var37);
							}
						} else {
							resource.close();
						}
					}
				}
			} catch (IOException var43) {
			}

			Arrays.fill(sizes, c, c + 256, (byte)0);
		}
	}

	@Override
	public void close() {
		this.images.values().forEach(NativeImage::close);
	}

	private Identifier getImageId(char character) {
		Identifier identifier = new Identifier(String.format(this.template, String.format("%02x", character / 256)));
		return new Identifier(identifier.getNamespace(), "textures/" + identifier.getPath());
	}

	@Nullable
	@Override
	public RenderableGlyph getGlyph(char character) {
		byte b = this.sizes[character];
		if (b != 0) {
			NativeImage nativeImage = (NativeImage)this.images.computeIfAbsent(this.getImageId(character), this::getGlyphImage);
			if (nativeImage != null) {
				int i = method_2043(b);
				return new UnicodeTextureFont.UnicodeTextureGlyph(character % 16 * 16 + i, (character & 255) / 16 * 16, method_2044(b) - i, 16, nativeImage);
			}
		}

		return null;
	}

	@Nullable
	private NativeImage getGlyphImage(Identifier glyphId) {
		try {
			Resource resource = this.resourceManager.getResource(glyphId);
			Throwable var3 = null;

			NativeImage var4;
			try {
				var4 = NativeImage.read(NativeImage.Format.RGBA, resource.getInputStream());
			} catch (Throwable var14) {
				var3 = var14;
				throw var14;
			} finally {
				if (resource != null) {
					if (var3 != null) {
						try {
							resource.close();
						} catch (Throwable var13) {
							var3.addSuppressed(var13);
						}
					} else {
						resource.close();
					}
				}
			}

			return var4;
		} catch (IOException var16) {
			LOGGER.error("Couldn't load texture {}", glyphId, var16);
			return null;
		}
	}

	private static int method_2043(byte b) {
		return b >> 4 & 15;
	}

	private static int method_2044(byte b) {
		return (b & 15) + 1;
	}

	@Environment(EnvType.CLIENT)
	public static class Loader implements FontLoader {
		private final Identifier sizes;
		private final String template;

		public Loader(Identifier sizes, String template) {
			this.sizes = sizes;
			this.template = template;
		}

		public static FontLoader fromJson(JsonObject jsonObject) {
			return new UnicodeTextureFont.Loader(new Identifier(JsonHelper.getString(jsonObject, "sizes")), JsonHelper.getString(jsonObject, "template"));
		}

		@Nullable
		@Override
		public Font load(ResourceManager manager) {
			try {
				Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(this.sizes);
				Throwable var3 = null;

				UnicodeTextureFont var5;
				try {
					byte[] bs = new byte[65536];
					resource.getInputStream().read(bs);
					var5 = new UnicodeTextureFont(manager, bs, this.template);
				} catch (Throwable var15) {
					var3 = var15;
					throw var15;
				} finally {
					if (resource != null) {
						if (var3 != null) {
							try {
								resource.close();
							} catch (Throwable var14) {
								var3.addSuppressed(var14);
							}
						} else {
							resource.close();
						}
					}
				}

				return var5;
			} catch (IOException var17) {
				UnicodeTextureFont.LOGGER.error("Cannot load {}, unicode glyphs will not render correctly", this.sizes);
				return null;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class UnicodeTextureGlyph implements RenderableGlyph {
		private final int width;
		private final int height;
		private final int unpackSkipPixels;
		private final int unpackSkipRows;
		private final NativeImage image;

		private UnicodeTextureGlyph(int unpackSkipPixels, int unpackSkipRows, int width, int height, NativeImage image) {
			this.width = width;
			this.height = height;
			this.unpackSkipPixels = unpackSkipPixels;
			this.unpackSkipRows = unpackSkipRows;
			this.image = image;
		}

		@Override
		public float getOversample() {
			return 2.0F;
		}

		@Override
		public int getWidth() {
			return this.width;
		}

		@Override
		public int getHeight() {
			return this.height;
		}

		@Override
		public float getAdvance() {
			return (float)(this.width / 2 + 1);
		}

		@Override
		public void upload(int x, int y) {
			this.image.upload(0, x, y, this.unpackSkipPixels, this.unpackSkipRows, this.width, this.height, false);
		}

		@Override
		public boolean hasColor() {
			return this.image.getFormat().getChannelCount() > 1;
		}

		@Override
		public float getShadowOffset() {
			return 0.5F;
		}

		@Override
		public float getBoldOffset() {
			return 0.5F;
		}
	}
}
