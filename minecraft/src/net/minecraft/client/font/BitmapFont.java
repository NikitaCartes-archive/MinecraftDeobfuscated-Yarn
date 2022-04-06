package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class BitmapFont implements Font {
	static final Logger LOGGER = LogUtils.getLogger();
	private final NativeImage image;
	private final Int2ObjectMap<BitmapFont.BitmapFontGlyph> glyphs;

	BitmapFont(NativeImage image, Int2ObjectMap<BitmapFont.BitmapFontGlyph> glyphs) {
		this.image = image;
		this.glyphs = glyphs;
	}

	@Override
	public void close() {
		this.image.close();
	}

	@Nullable
	@Override
	public Glyph getGlyph(int codePoint) {
		return this.glyphs.get(codePoint);
	}

	@Override
	public IntSet getProvidedGlyphs() {
		return IntSets.unmodifiable(this.glyphs.keySet());
	}

	@Environment(EnvType.CLIENT)
	static record BitmapFontGlyph(float scaleFactor, NativeImage image, int x, int y, int width, int height, int advance, int ascent) implements Glyph {

		@Override
		public float getAdvance() {
			return (float)this.advance;
		}

		@Override
		public GlyphRenderer bake(Function<RenderableGlyph, GlyphRenderer> function) {
			return (GlyphRenderer)function.apply(
				new RenderableGlyph() {
					@Override
					public float getOversample() {
						return 1.0F / BitmapFontGlyph.this.scaleFactor;
					}

					@Override
					public int getWidth() {
						return BitmapFontGlyph.this.width;
					}

					@Override
					public int getHeight() {
						return BitmapFontGlyph.this.height;
					}

					@Override
					public float getAscent() {
						return RenderableGlyph.super.getAscent() + 7.0F - (float)BitmapFontGlyph.this.ascent;
					}

					@Override
					public void upload(int x, int y) {
						BitmapFontGlyph.this.image
							.upload(0, x, y, BitmapFontGlyph.this.x, BitmapFontGlyph.this.y, BitmapFontGlyph.this.width, BitmapFontGlyph.this.height, false, false);
					}

					@Override
					public boolean hasColor() {
						return BitmapFontGlyph.this.image.getFormat().getChannelCount() > 1;
					}
				}
			);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Loader implements FontLoader {
		private final Identifier filename;
		private final List<int[]> chars;
		private final int height;
		private final int ascent;

		public Loader(Identifier id, int height, int ascent, List<int[]> chars) {
			this.filename = new Identifier(id.getNamespace(), "textures/" + id.getPath());
			this.chars = chars;
			this.height = height;
			this.ascent = ascent;
		}

		public static BitmapFont.Loader fromJson(JsonObject json) {
			int i = JsonHelper.getInt(json, "height", 8);
			int j = JsonHelper.getInt(json, "ascent");
			if (j > i) {
				throw new JsonParseException("Ascent " + j + " higher than height " + i);
			} else {
				List<int[]> list = Lists.<int[]>newArrayList();
				JsonArray jsonArray = JsonHelper.getArray(json, "chars");

				for (int k = 0; k < jsonArray.size(); k++) {
					String string = JsonHelper.asString(jsonArray.get(k), "chars[" + k + "]");
					int[] is = string.codePoints().toArray();
					if (k > 0) {
						int l = ((int[])list.get(0)).length;
						if (is.length != l) {
							throw new JsonParseException("Elements of chars have to be the same length (found: " + is.length + ", expected: " + l + "), pad with space or \\u0000");
						}
					}

					list.add(is);
				}

				if (!list.isEmpty() && ((int[])list.get(0)).length != 0) {
					return new BitmapFont.Loader(new Identifier(JsonHelper.getString(json, "file")), i, j, list);
				} else {
					throw new JsonParseException("Expected to find data in chars, found none.");
				}
			}
		}

		@Nullable
		@Override
		public Font load(ResourceManager manager) {
			try {
				InputStream inputStream = manager.open(this.filename);

				BitmapFont var22;
				try {
					NativeImage nativeImage = NativeImage.read(NativeImage.Format.RGBA, inputStream);
					int i = nativeImage.getWidth();
					int j = nativeImage.getHeight();
					int k = i / ((int[])this.chars.get(0)).length;
					int l = j / this.chars.size();
					float f = (float)this.height / (float)l;
					Int2ObjectMap<BitmapFont.BitmapFontGlyph> int2ObjectMap = new Int2ObjectOpenHashMap<>();

					for (int m = 0; m < this.chars.size(); m++) {
						int n = 0;

						for (int o : (int[])this.chars.get(m)) {
							int p = n++;
							if (o != 0) {
								int q = this.findCharacterStartX(nativeImage, k, l, p, m);
								BitmapFont.BitmapFontGlyph bitmapFontGlyph = int2ObjectMap.put(
									o, new BitmapFont.BitmapFontGlyph(f, nativeImage, p * k, m * l, k, l, (int)(0.5 + (double)((float)q * f)) + 1, this.ascent)
								);
								if (bitmapFontGlyph != null) {
									BitmapFont.LOGGER.warn("Codepoint '{}' declared multiple times in {}", Integer.toHexString(o), this.filename);
								}
							}
						}
					}

					var22 = new BitmapFont(nativeImage, int2ObjectMap);
				} catch (Throwable var20) {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (Throwable var19) {
							var20.addSuppressed(var19);
						}
					}

					throw var20;
				}

				if (inputStream != null) {
					inputStream.close();
				}

				return var22;
			} catch (IOException var21) {
				throw new RuntimeException(var21.getMessage());
			}
		}

		private int findCharacterStartX(NativeImage image, int characterWidth, int characterHeight, int charPosX, int charPosY) {
			int i;
			for (i = characterWidth - 1; i >= 0; i--) {
				int j = charPosX * characterWidth + i;

				for (int k = 0; k < characterHeight; k++) {
					int l = charPosY * characterHeight + k;
					if (image.getOpacity(j, l) != 0) {
						return i + 1;
					}
				}
			}

			return i + 1;
		}
	}
}
