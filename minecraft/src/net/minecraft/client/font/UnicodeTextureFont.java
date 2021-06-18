package net.minecraft.client.font;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.IOException;
import java.util.Arrays;
import java.util.IllegalFormatException;
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
	static final Logger LOGGER = LogManager.getLogger();
	private static final int field_32232 = 256;
	private static final int field_32233 = 256;
	private static final int field_32234 = 256;
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

				label90: {
					label89:
					try (NativeImage nativeImage = NativeImage.read(NativeImage.Format.ABGR, resource.getInputStream())) {
						if (nativeImage.getWidth() == 256 && nativeImage.getHeight() == 256) {
							int k = 0;

							while (true) {
								if (k >= 256) {
									break label89;
								}

								byte b = sizes[j + k];
								if (b != 0 && getStart(b) > getEnd(b)) {
									sizes[j + k] = 0;
								}

								k++;
							}
						}
						break label90;
					} catch (Throwable var14) {
						if (resource != null) {
							try {
								resource.close();
							} catch (Throwable var11) {
								var14.addSuppressed(var11);
							}
						}

						throw var14;
					}

					if (resource != null) {
						resource.close();
					}
					continue;
				}

				if (resource != null) {
					resource.close();
				}
			} catch (IOException var15) {
			}

			Arrays.fill(sizes, j, j + 256, (byte)0);
		}
	}

	@Override
	public void close() {
		this.images.values().forEach(NativeImage::close);
	}

	private Identifier getImageId(int codePoint) {
		Identifier identifier = new Identifier(String.format(this.template, String.format("%02x", codePoint / 256)));
		return new Identifier(identifier.getNamespace(), "textures/" + identifier.getPath());
	}

	@Nullable
	@Override
	public RenderableGlyph getGlyph(int codePoint) {
		if (codePoint >= 0 && codePoint <= 65535) {
			byte b = this.sizes[codePoint];
			if (b != 0) {
				NativeImage nativeImage = (NativeImage)this.images.computeIfAbsent(this.getImageId(codePoint), this::getGlyphImage);
				if (nativeImage != null) {
					int i = getStart(b);
					return new UnicodeTextureFont.UnicodeTextureGlyph(codePoint % 16 * 16 + i, (codePoint & 0xFF) / 16 * 16, getEnd(b) - i, 16, nativeImage);
				}
			}

			return null;
		} else {
			return null;
		}
	}

	@Override
	public IntSet getProvidedGlyphs() {
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

			NativeImage var3;
			try {
				var3 = NativeImage.read(NativeImage.Format.ABGR, resource.getInputStream());
			} catch (Throwable var6) {
				if (resource != null) {
					try {
						resource.close();
					} catch (Throwable var5) {
						var6.addSuppressed(var5);
					}
				}

				throw var6;
			}

			if (resource != null) {
				resource.close();
			}

			return var3;
		} catch (IOException var7) {
			LOGGER.error("Couldn't load texture {}", glyphId, var7);
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
			return new UnicodeTextureFont.Loader(new Identifier(JsonHelper.getString(json, "sizes")), getLegacyUnicodeTemplate(json));
		}

		private static String getLegacyUnicodeTemplate(JsonObject json) {
			String string = JsonHelper.getString(json, "template");

			try {
				String.format(string, "");
				return string;
			} catch (IllegalFormatException var3) {
				throw new JsonParseException("Invalid legacy unicode template supplied, expected single '%s': " + string);
			}
		}

		@Nullable
		@Override
		public Font load(ResourceManager manager) {
			try {
				Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(this.sizes);

				UnicodeTextureFont var4;
				try {
					byte[] bs = new byte[65536];
					resource.getInputStream().read(bs);
					var4 = new UnicodeTextureFont(manager, bs, this.template);
				} catch (Throwable var6) {
					if (resource != null) {
						try {
							resource.close();
						} catch (Throwable var5) {
							var6.addSuppressed(var5);
						}
					}

					throw var6;
				}

				if (resource != null) {
					resource.close();
				}

				return var4;
			} catch (IOException var7) {
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

		UnicodeTextureGlyph(int unpackSkipPixels, int unpackSkipRows, int width, int height, NativeImage image) {
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
