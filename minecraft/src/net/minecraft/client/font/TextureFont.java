package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
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
public class TextureFont implements Font {
	private static final Logger LOGGER = LogManager.getLogger();
	private final NativeImage image;
	private final Char2ObjectMap<TextureFont.TextureFontGlyph> characterToGlyphMap;

	public TextureFont(NativeImage nativeImage, Char2ObjectMap<TextureFont.TextureFontGlyph> char2ObjectMap) {
		this.image = nativeImage;
		this.characterToGlyphMap = char2ObjectMap;
	}

	@Override
	public void close() {
		this.image.close();
	}

	@Nullable
	@Override
	public RenderableGlyph getGlyph(char c) {
		return this.characterToGlyphMap.get(c);
	}

	@Environment(EnvType.CLIENT)
	public static class Loader implements FontLoader {
		private final Identifier filename;
		private final List<String> chars;
		private final int height;
		private final int ascent;

		public Loader(Identifier identifier, int i, int j, List<String> list) {
			this.filename = new Identifier(identifier.getNamespace(), "textures/" + identifier.getPath());
			this.chars = list;
			this.height = i;
			this.ascent = j;
		}

		public static TextureFont.Loader fromJson(JsonObject jsonObject) {
			int i = JsonHelper.getInt(jsonObject, "height", 8);
			int j = JsonHelper.getInt(jsonObject, "ascent");
			if (j > i) {
				throw new JsonParseException("Ascent " + j + " higher than height " + i);
			} else {
				List<String> list = Lists.<String>newArrayList();
				JsonArray jsonArray = JsonHelper.getArray(jsonObject, "chars");

				for (int k = 0; k < jsonArray.size(); k++) {
					String string = JsonHelper.asString(jsonArray.get(k), "chars[" + k + "]");
					if (k > 0) {
						int l = string.length();
						int m = ((String)list.get(0)).length();
						if (l != m) {
							throw new JsonParseException("Elements of chars have to be the same length (found: " + l + ", expected: " + m + "), pad with space or \\u0000");
						}
					}

					list.add(string);
				}

				if (!list.isEmpty() && !((String)list.get(0)).isEmpty()) {
					return new TextureFont.Loader(new Identifier(JsonHelper.getString(jsonObject, "file")), i, j, list);
				} else {
					throw new JsonParseException("Expected to find data in chars, found none.");
				}
			}
		}

		@Nullable
		@Override
		public Font load(ResourceManager resourceManager) {
			try {
				Resource resource = resourceManager.getResource(this.filename);
				Throwable var3 = null;

				TextureFont var28;
				try {
					NativeImage nativeImage = NativeImage.fromInputStream(NativeImage.Format.field_4997, resource.getInputStream());
					int i = nativeImage.getWidth();
					int j = nativeImage.getHeight();
					int k = i / ((String)this.chars.get(0)).length();
					int l = j / this.chars.size();
					float f = (float)this.height / (float)l;
					Char2ObjectMap<TextureFont.TextureFontGlyph> char2ObjectMap = new Char2ObjectOpenHashMap<>();

					for (int m = 0; m < this.chars.size(); m++) {
						String string = (String)this.chars.get(m);

						for (int n = 0; n < string.length(); n++) {
							char c = string.charAt(n);
							if (c != 0 && c != ' ') {
								int o = this.findCharacterStartX(nativeImage, k, l, n, m);
								TextureFont.TextureFontGlyph textureFontGlyph = char2ObjectMap.put(
									c, new TextureFont.TextureFontGlyph(f, nativeImage, n * k, m * l, k, l, (int)(0.5 + (double)((float)o * f)) + 1, this.ascent)
								);
								if (textureFontGlyph != null) {
									TextureFont.LOGGER.warn("Codepoint '{}' declared multiple times in {}", Integer.toHexString(c), this.filename);
								}
							}
						}
					}

					var28 = new TextureFont(nativeImage, char2ObjectMap);
				} catch (Throwable var25) {
					var3 = var25;
					throw var25;
				} finally {
					if (resource != null) {
						if (var3 != null) {
							try {
								resource.close();
							} catch (Throwable var24) {
								var3.addSuppressed(var24);
							}
						} else {
							resource.close();
						}
					}
				}

				return var28;
			} catch (IOException var27) {
				throw new RuntimeException(var27.getMessage());
			}
		}

		private int findCharacterStartX(NativeImage nativeImage, int i, int j, int k, int l) {
			int m;
			for (m = i - 1; m >= 0; m--) {
				int n = k * i + m;

				for (int o = 0; o < j; o++) {
					int p = l * j + o;
					if (nativeImage.getAlphaOrLuminance(n, p) != 0) {
						return m + 1;
					}
				}
			}

			return m + 1;
		}
	}

	@Environment(EnvType.CLIENT)
	static final class TextureFontGlyph implements RenderableGlyph {
		private final float scaleFactor;
		private final NativeImage image;
		private final int x;
		private final int y;
		private final int width;
		private final int height;
		private final int advance;
		private final int ascent;

		private TextureFontGlyph(float f, NativeImage nativeImage, int i, int j, int k, int l, int m, int n) {
			this.scaleFactor = f;
			this.image = nativeImage;
			this.x = i;
			this.y = j;
			this.width = k;
			this.height = l;
			this.advance = m;
			this.ascent = n;
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
		public void upload(int i, int j) {
			this.image.upload(0, i, j, this.x, this.y, this.width, this.height, false);
		}

		@Override
		public boolean hasColor() {
			return this.image.getFormat().getBytesPerPixel() > 1;
		}
	}
}
