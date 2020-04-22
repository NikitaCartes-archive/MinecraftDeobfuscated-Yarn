package net.minecraft.client.font;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
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
			int j = i * 256;
			Identifier identifier = this.getImageId(j);

			try {
				Resource resource = this.resourceManager.getResource(identifier);
				Throwable var8 = null;

				try (NativeImage nativeImage = NativeImage.read(NativeImage.Format.RGBA, resource.getInputStream())) {
					if (nativeImage.getWidth() == 256 && nativeImage.getHeight() == 256) {
						for (int k = 0; k < 256; k++) {
							byte b = sizes[j + k];
							if (b != 0 && getStart(b) > getEnd(b)) {
								sizes[j + k] = 0;
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

			Arrays.fill(sizes, j, j + 256, (byte)0);
		}
	}

	@Override
	public void close() {
		this.images.values().forEach(NativeImage::close);
	}

	private Identifier getImageId(int i) {
		Identifier identifier = new Identifier(String.format(this.template, String.format("%02x", i / 256)));
		return new Identifier(identifier.getNamespace(), "textures/" + identifier.getPath());
	}

	@Nullable
	@Override
	public RenderableGlyph getGlyph(int i) {
		if (i >= 0 && i <= 65535) {
			byte b = this.sizes[i];
			if (b != 0) {
				NativeImage nativeImage = (NativeImage)this.images.computeIfAbsent(this.getImageId(i), this::getGlyphImage);
				if (nativeImage != null) {
					int j = getStart(b);
					return new UnicodeTextureFont.UnicodeTextureGlyph(i % 16 * 16 + j, (i & 0xFF) / 16 * 16, getEnd(b) - j, 16, nativeImage);
				}
			}

			return null;
		} else {
			return null;
		}
	}

	@Override
	public IntSet method_27442() {
		IntSet intSet = new IntOpenHashSet();

		for (int i = 0; i < 65535; i++) {
			if (this.sizes[i] != 0) {
				intSet.add(i);
			}
		}

		return intSet;
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

	private static int getStart(byte size) {
		return size >> 4 & 15;
	}

	private static int getEnd(byte size) {
		return (size & 15) + 1;
	}

	@Environment(EnvType.CLIENT)
	public static class Loader implements FontLoader {
		private final Identifier sizes;
		private final String template;

		public Loader(Identifier sizes, String template) {
			this.sizes = sizes;
			this.template = template;
		}

		public static FontLoader fromJson(JsonObject json) {
			return new UnicodeTextureFont.Loader(new Identifier(JsonHelper.getString(json, "sizes")), JsonHelper.getString(json, "template"));
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

		private UnicodeTextureGlyph(int x, int y, int width, int height, NativeImage image) {
			this.width = width;
			this.height = height;
			this.unpackSkipPixels = x;
			this.unpackSkipRows = y;
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
			this.image.upload(0, x, y, this.unpackSkipPixels, this.unpackSkipRows, this.width, this.height, false, false);
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
