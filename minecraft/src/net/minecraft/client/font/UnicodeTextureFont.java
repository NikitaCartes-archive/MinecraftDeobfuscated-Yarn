package net.minecraft.client.font;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class UnicodeTextureFont implements Font {
	static final Logger LOGGER = LogUtils.getLogger();
	private static final int field_32232 = 256;
	private static final int field_32233 = 256;
	private static final int field_32234 = 256;
	private static final byte field_37905 = 0;
	private static final int field_40410 = 65536;
	private final byte[] sizes;
	private final UnicodeTextureFont.FontImage[] fontImages = new UnicodeTextureFont.FontImage[256];

	public UnicodeTextureFont(ResourceManager resourceManager, byte[] sizes, String template) {
		this.sizes = sizes;
		Set<Identifier> set = new HashSet();

		for (int i = 0; i < 256; i++) {
			int j = i * 256;
			set.add(getImageId(template, j));
		}

		String string = getCommonPath(set);
		Map<Identifier, CompletableFuture<NativeImage>> map = new HashMap();
		resourceManager.findResources(string, set::contains).forEach((id, resource) -> map.put(id, CompletableFuture.supplyAsync(() -> {
				try {
					InputStream inputStream = resource.getInputStream();

					NativeImage var3;
					try {
						var3 = NativeImage.read(NativeImage.Format.RGBA, inputStream);
					} catch (Throwable var6x) {
						if (inputStream != null) {
							try {
								inputStream.close();
							} catch (Throwable var5x) {
								var6x.addSuppressed(var5x);
							}
						}

						throw var6x;
					}

					if (inputStream != null) {
						inputStream.close();
					}

					return var3;
				} catch (IOException var7x) {
					LOGGER.error("Failed to read resource {} from pack {}", id, resource.getResourcePackName());
					return null;
				}
			}, Util.getMainWorkerExecutor())));
		List<CompletableFuture<?>> list = new ArrayList(256);

		for (int k = 0; k < 256; k++) {
			int l = k * 256;
			int m = k;
			Identifier identifier = getImageId(template, l);
			CompletableFuture<NativeImage> completableFuture = (CompletableFuture<NativeImage>)map.get(identifier);
			if (completableFuture != null) {
				list.add(completableFuture.thenAcceptAsync(image -> {
					if (image != null) {
						if (image.getWidth() == 256 && image.getHeight() == 256) {
							for (int kx = 0; kx < 256; kx++) {
								byte b = sizes[l + kx];
								if (b != 0 && getStart(b) > getEnd(b)) {
									sizes[l + kx] = 0;
								}
							}

							this.fontImages[m] = new UnicodeTextureFont.FontImage(sizes, image);
						} else {
							image.close();
							Arrays.fill(sizes, l, l + 256, (byte)0);
						}
					}
				}, Util.getMainWorkerExecutor()));
			}
		}

		CompletableFuture.allOf((CompletableFuture[])list.toArray(CompletableFuture[]::new)).join();
	}

	private static String getCommonPath(Set<Identifier> ids) {
		String string = StringUtils.getCommonPrefix((String[])ids.stream().map(Identifier::getPath).toArray(String[]::new));
		int i = string.lastIndexOf("/");
		return i == -1 ? "" : string.substring(0, i);
	}

	@Override
	public void close() {
		for (UnicodeTextureFont.FontImage fontImage : this.fontImages) {
			if (fontImage != null) {
				fontImage.close();
			}
		}
	}

	private static Identifier getImageId(String template, int codePoint) {
		String string = String.format(Locale.ROOT, "%02x", codePoint / 256);
		Identifier identifier = new Identifier(String.format(Locale.ROOT, template, string));
		return identifier.withPrefixedPath("textures/");
	}

	@Nullable
	@Override
	public Glyph getGlyph(int codePoint) {
		if (codePoint >= 0 && codePoint < this.sizes.length) {
			int i = codePoint / 256;
			UnicodeTextureFont.FontImage fontImage = this.fontImages[i];
			return fontImage != null ? fontImage.getGlyph(codePoint) : null;
		} else {
			return null;
		}
	}

	@Override
	public IntSet getProvidedGlyphs() {
		IntSet intSet = new IntOpenHashSet();

		for (int i = 0; i < this.sizes.length; i++) {
			if (this.sizes[i] != 0) {
				intSet.add(i);
			}
		}

		return intSet;
	}

	static int getStart(byte size) {
		return size >> 4 & 15;
	}

	static int getEnd(byte size) {
		return (size & 15) + 1;
	}

	@Environment(EnvType.CLIENT)
	static class FontImage implements AutoCloseable {
		private final byte[] sizes;
		private final NativeImage image;

		FontImage(byte[] sizes, NativeImage image) {
			this.sizes = sizes;
			this.image = image;
		}

		public void close() {
			this.image.close();
		}

		@Nullable
		public Glyph getGlyph(int codePoint) {
			byte b = this.sizes[codePoint];
			if (b != 0) {
				int i = UnicodeTextureFont.getStart(b);
				return new UnicodeTextureFont.UnicodeTextureGlyph(codePoint % 16 * 16 + i, (codePoint & 0xFF) / 16 * 16, UnicodeTextureFont.getEnd(b) - i, 16, this.image);
			} else {
				return null;
			}
		}
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
				String.format(Locale.ROOT, string, "");
				return string;
			} catch (IllegalFormatException var3) {
				throw new JsonParseException("Invalid legacy unicode template supplied, expected single '%s': " + string);
			}
		}

		@Nullable
		@Override
		public Font load(ResourceManager manager) {
			try {
				InputStream inputStream = MinecraftClient.getInstance().getResourceManager().open(this.sizes);

				UnicodeTextureFont var4;
				try {
					byte[] bs = inputStream.readNBytes(65536);
					var4 = new UnicodeTextureFont(manager, bs, this.template);
				} catch (Throwable var6) {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (Throwable var5) {
							var6.addSuppressed(var5);
						}
					}

					throw var6;
				}

				if (inputStream != null) {
					inputStream.close();
				}

				return var4;
			} catch (IOException var7) {
				UnicodeTextureFont.LOGGER.error("Cannot load {}, unicode glyphs will not render correctly", this.sizes);
				return null;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static record UnicodeTextureGlyph(int unpackSkipPixels, int unpackSkipRows, int width, int height, NativeImage image) implements Glyph {

		@Override
		public float getAdvance() {
			return (float)(this.width / 2 + 1);
		}

		@Override
		public float getShadowOffset() {
			return 0.5F;
		}

		@Override
		public float getBoldOffset() {
			return 0.5F;
		}

		@Override
		public GlyphRenderer bake(Function<RenderableGlyph, GlyphRenderer> function) {
			return (GlyphRenderer)function.apply(
				new RenderableGlyph() {
					@Override
					public float getOversample() {
						return 2.0F;
					}

					@Override
					public int getWidth() {
						return UnicodeTextureGlyph.this.width;
					}

					@Override
					public int getHeight() {
						return UnicodeTextureGlyph.this.height;
					}

					@Override
					public void upload(int x, int y) {
						UnicodeTextureGlyph.this.image
							.upload(
								0,
								x,
								y,
								UnicodeTextureGlyph.this.unpackSkipPixels,
								UnicodeTextureGlyph.this.unpackSkipRows,
								UnicodeTextureGlyph.this.width,
								UnicodeTextureGlyph.this.height,
								false,
								false
							);
					}

					@Override
					public boolean hasColor() {
						return UnicodeTextureGlyph.this.image.getFormat().getChannelCount() > 1;
					}
				}
			);
		}
	}
}
