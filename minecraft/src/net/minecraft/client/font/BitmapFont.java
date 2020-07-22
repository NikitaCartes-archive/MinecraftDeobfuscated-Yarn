package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class BitmapFont implements Font {
	private static final Logger LOGGER = LogManager.getLogger();
	private final NativeImage image;
	private final Int2ObjectMap<BitmapFont.BitmapFontGlyph> glyphs;

	private BitmapFont(NativeImage image, Int2ObjectMap<BitmapFont.BitmapFontGlyph> glyphs) {
		this.image = image;
		this.glyphs = glyphs;
	}

	@Override
	public void close() {
		this.image.close();
	}

	@Nullable
	@Override
	public RenderableGlyph getGlyph(int codePoint) {
		return this.glyphs.get(codePoint);
	}

	@Override
	public IntSet getProvidedGlyphs() {
		return IntSets.unmodifiable(this.glyphs.keySet());
	}

	@Environment(EnvType.CLIENT)
	static final class BitmapFontGlyph implements RenderableGlyph {
		private final float scaleFactor;
		private final NativeImage image;
		private final int x;
		private final int y;
		private final int width;
		private final int height;
		private final int advance;
		private final int ascent;

		private BitmapFontGlyph(float scaleFactor, NativeImage image, int x, int y, int width, int height, int advance, int ascent) {
			this.scaleFactor = scaleFactor;
			this.image = image;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.advance = advance;
			this.ascent = ascent;
		}

		@Override
		public float getOversample() {
			return 1.0F / this.scaleFactor;
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
			return (float)this.advance;
		}

		@Override
		public float getAscent() {
			return RenderableGlyph.super.getAscent() + 7.0F - (float)this.ascent;
		}

		@Override
		public void upload(int x, int y) {
			this.image.upload(0, x, y, this.x, this.y, this.width, this.height, false, false);
		}

		@Override
		public boolean hasColor() {
			return this.image.getFormat().getChannelCount() > 1;
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
				Resource resource = manager.getResource(this.filename);
				Throwable var3 = null;

				BitmapFont var31;
				try {
					NativeImage nativeImage = NativeImage.read(NativeImage.Format.ABGR, resource.getInputStream());
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
							if (o != 0 && o != 32) {
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

					var31 = new BitmapFont(nativeImage, int2ObjectMap);
				} catch (Throwable var28) {
					var3 = var28;
					throw var28;
				} finally {
					if (resource != null) {
						if (var3 != null) {
							try {
								resource.close();
							} catch (Throwable var27) {
								var3.addSuppressed(var27);
							}
						} else {
							resource.close();
						}
					}
				}

				return var31;
			} catch (IOException var30) {
				throw new RuntimeException(var30.getMessage());
			}
		}

		private int findCharacterStartX(NativeImage image, int characterWidth, int characterHeight, int charPosX, int charPosY) {
			int i;
			for (i = characterWidth - 1; i >= 0; i--) {
				int j = charPosX * characterWidth + i;

				for (int k = 0; k < characterHeight; k++) {
					int l = charPosY * characterHeight + k;
					if (image.getPixelOpacity(j, l) != 0) {
						return i + 1;
					}
				}
			}

			return i + 1;
		}
	}
}
