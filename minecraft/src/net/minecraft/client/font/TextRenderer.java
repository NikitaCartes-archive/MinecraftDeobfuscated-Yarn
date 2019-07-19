package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TextRenderer implements AutoCloseable {
	public final int fontHeight = 9;
	public final Random random = new Random();
	private final TextureManager textureManager;
	private final FontStorage fontStorage;
	private boolean rightToLeft;

	public TextRenderer(TextureManager textureManager, FontStorage fontStorage) {
		this.textureManager = textureManager;
		this.fontStorage = fontStorage;
	}

	public void setFonts(List<Font> fonts) {
		this.fontStorage.setFonts(fonts);
	}

	public void close() {
		this.fontStorage.close();
	}

	public int drawWithShadow(String text, float x, float y, int color) {
		GlStateManager.enableAlphaTest();
		return this.draw(text, x, y, color, true);
	}

	public int draw(String text, float x, float y, int color) {
		GlStateManager.enableAlphaTest();
		return this.draw(text, x, y, color, false);
	}

	public String mirror(String text) {
		try {
			Bidi bidi = new Bidi(new ArabicShaping(8).shape(text), 127);
			bidi.setReorderingMode(0);
			return bidi.writeReordered(2);
		} catch (ArabicShapingException var3) {
			return text;
		}
	}

	private int draw(String str, float x, float y, int color, boolean withShadow) {
		if (str == null) {
			return 0;
		} else {
			if (this.rightToLeft) {
				str = this.mirror(str);
			}

			if ((color & -67108864) == 0) {
				color |= -16777216;
			}

			if (withShadow) {
				this.drawLayer(str, x, y, color, true);
			}

			x = this.drawLayer(str, x, y, color, false);
			return (int)x + (withShadow ? 1 : 0);
		}
	}

	private float drawLayer(String str, float x, float y, int color, boolean isShadow) {
		float f = isShadow ? 0.25F : 1.0F;
		float g = (float)(color >> 16 & 0xFF) / 255.0F * f;
		float h = (float)(color >> 8 & 0xFF) / 255.0F * f;
		float i = (float)(color & 0xFF) / 255.0F * f;
		float j = g;
		float k = h;
		float l = i;
		float m = (float)(color >> 24 & 0xFF) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		Identifier identifier = null;
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		boolean bl = false;
		boolean bl2 = false;
		boolean bl3 = false;
		boolean bl4 = false;
		boolean bl5 = false;
		List<TextRenderer.Rectangle> list = Lists.<TextRenderer.Rectangle>newArrayList();

		for (int n = 0; n < str.length(); n++) {
			char c = str.charAt(n);
			if (c == 167 && n + 1 < str.length()) {
				Formatting formatting = Formatting.byCode(str.charAt(n + 1));
				if (formatting != null) {
					if (formatting.affectsGlyphWidth()) {
						bl = false;
						bl2 = false;
						bl5 = false;
						bl4 = false;
						bl3 = false;
						j = g;
						k = h;
						l = i;
					}

					if (formatting.getColorValue() != null) {
						int o = formatting.getColorValue();
						j = (float)(o >> 16 & 0xFF) / 255.0F * f;
						k = (float)(o >> 8 & 0xFF) / 255.0F * f;
						l = (float)(o & 0xFF) / 255.0F * f;
					} else if (formatting == Formatting.OBFUSCATED) {
						bl = true;
					} else if (formatting == Formatting.BOLD) {
						bl2 = true;
					} else if (formatting == Formatting.STRIKETHROUGH) {
						bl5 = true;
					} else if (formatting == Formatting.UNDERLINE) {
						bl4 = true;
					} else if (formatting == Formatting.ITALIC) {
						bl3 = true;
					}
				}

				n++;
			} else {
				Glyph glyph = this.fontStorage.getGlyph(c);
				GlyphRenderer glyphRenderer = bl && c != ' ' ? this.fontStorage.getObfuscatedGlyphRenderer(glyph) : this.fontStorage.getGlyphRenderer(c);
				Identifier identifier2 = glyphRenderer.getId();
				if (identifier2 != null) {
					if (identifier != identifier2) {
						tessellator.draw();
						this.textureManager.bindTexture(identifier2);
						bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
						identifier = identifier2;
					}

					float p = bl2 ? glyph.getBoldOffset() : 0.0F;
					float q = isShadow ? glyph.getShadowOffset() : 0.0F;
					this.drawGlyph(glyphRenderer, bl2, bl3, p, x + q, y + q, bufferBuilder, j, k, l, m);
				}

				float p = glyph.getAdvance(bl2);
				float q = isShadow ? 1.0F : 0.0F;
				if (bl5) {
					list.add(new TextRenderer.Rectangle(x + q - 1.0F, y + q + 4.5F, x + q + p, y + q + 4.5F - 1.0F, j, k, l, m));
				}

				if (bl4) {
					list.add(new TextRenderer.Rectangle(x + q - 1.0F, y + q + 9.0F, x + q + p, y + q + 9.0F - 1.0F, j, k, l, m));
				}

				x += p;
			}
		}

		tessellator.draw();
		if (!list.isEmpty()) {
			GlStateManager.disableTexture();
			bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);

			for (TextRenderer.Rectangle rectangle : list) {
				rectangle.draw(bufferBuilder);
			}

			tessellator.draw();
			GlStateManager.enableTexture();
		}

		return x;
	}

	private void drawGlyph(
		GlyphRenderer glyphRenderer,
		boolean bold,
		boolean strikethrough,
		float boldOffset,
		float x,
		float y,
		BufferBuilder buffer,
		float red,
		float green,
		float blue,
		float alpha
	) {
		glyphRenderer.draw(this.textureManager, strikethrough, x, y, buffer, red, green, blue, alpha);
		if (bold) {
			glyphRenderer.draw(this.textureManager, strikethrough, x + boldOffset, y, buffer, red, green, blue, alpha);
		}
	}

	public int getStringWidth(String text) {
		if (text == null) {
			return 0;
		} else {
			float f = 0.0F;
			boolean bl = false;

			for (int i = 0; i < text.length(); i++) {
				char c = text.charAt(i);
				if (c == 167 && i < text.length() - 1) {
					Formatting formatting = Formatting.byCode(text.charAt(++i));
					if (formatting == Formatting.BOLD) {
						bl = true;
					} else if (formatting != null && formatting.affectsGlyphWidth()) {
						bl = false;
					}
				} else {
					f += this.fontStorage.getGlyph(c).getAdvance(bl);
				}
			}

			return MathHelper.ceil(f);
		}
	}

	public float getCharWidth(char character) {
		return character == 167 ? 0.0F : this.fontStorage.getGlyph(character).getAdvance(false);
	}

	public String trimToWidth(String text, int width) {
		return this.trimToWidth(text, width, false);
	}

	public String trimToWidth(String text, int width, boolean rightToLeft) {
		StringBuilder stringBuilder = new StringBuilder();
		float f = 0.0F;
		int i = rightToLeft ? text.length() - 1 : 0;
		int j = rightToLeft ? -1 : 1;
		boolean bl = false;
		boolean bl2 = false;

		for (int k = i; k >= 0 && k < text.length() && f < (float)width; k += j) {
			char c = text.charAt(k);
			if (bl) {
				bl = false;
				Formatting formatting = Formatting.byCode(c);
				if (formatting == Formatting.BOLD) {
					bl2 = true;
				} else if (formatting != null && formatting.affectsGlyphWidth()) {
					bl2 = false;
				}
			} else if (c == 167) {
				bl = true;
			} else {
				f += this.getCharWidth(c);
				if (bl2) {
					f++;
				}
			}

			if (f > (float)width) {
				break;
			}

			if (rightToLeft) {
				stringBuilder.insert(0, c);
			} else {
				stringBuilder.append(c);
			}
		}

		return stringBuilder.toString();
	}

	private String trimEndNewlines(String text) {
		while (text != null && text.endsWith("\n")) {
			text = text.substring(0, text.length() - 1);
		}

		return text;
	}

	public void drawTrimmed(String text, int x, int y, int maxWidth, int color) {
		text = this.trimEndNewlines(text);
		this.drawWrapped(text, x, y, maxWidth, color);
	}

	private void drawWrapped(String text, int x, int y, int maxWidth, int color) {
		for (String string : this.wrapStringToWidthAsList(text, maxWidth)) {
			float f = (float)x;
			if (this.rightToLeft) {
				int i = this.getStringWidth(this.mirror(string));
				f += (float)(maxWidth - i);
			}

			this.draw(string, f, (float)y, color, false);
			y += 9;
		}
	}

	public int getStringBoundedHeight(String text, int maxWidth) {
		return 9 * this.wrapStringToWidthAsList(text, maxWidth).size();
	}

	public void setRightToLeft(boolean rightToLeft) {
		this.rightToLeft = rightToLeft;
	}

	public List<String> wrapStringToWidthAsList(String text, int width) {
		return Arrays.asList(this.wrapStringToWidth(text, width).split("\n"));
	}

	public String wrapStringToWidth(String text, int width) {
		String string = "";

		while (!text.isEmpty()) {
			int i = this.getCharacterCountForWidth(text, width);
			if (text.length() <= i) {
				return string + text;
			}

			String string2 = text.substring(0, i);
			char c = text.charAt(i);
			boolean bl = c == ' ' || c == '\n';
			text = Formatting.getFormatAtEnd(string2) + text.substring(i + (bl ? 1 : 0));
			string = string + string2 + "\n";
		}

		return string;
	}

	public int getCharacterCountForWidth(String text, int offset) {
		int i = Math.max(1, offset);
		int j = text.length();
		float f = 0.0F;
		int k = 0;
		int l = -1;
		boolean bl = false;

		for (boolean bl2 = true; k < j; k++) {
			char c = text.charAt(k);
			switch (c) {
				case '\n':
					k--;
					break;
				case ' ':
					l = k;
				default:
					if (f != 0.0F) {
						bl2 = false;
					}

					f += this.getCharWidth(c);
					if (bl) {
						f++;
					}
					break;
				case '§':
					if (k < j - 1) {
						Formatting formatting = Formatting.byCode(text.charAt(++k));
						if (formatting == Formatting.BOLD) {
							bl = true;
						} else if (formatting != null && formatting.affectsGlyphWidth()) {
							bl = false;
						}
					}
			}

			if (c == '\n') {
				l = ++k;
				break;
			}

			if (f > (float)i) {
				if (bl2) {
					k++;
				}
				break;
			}
		}

		return k != j && l != -1 && l < k ? l : k;
	}

	public int findWordEdge(String text, int direction, int position, boolean skipWhitespaceToRightOfWord) {
		int i = position;
		boolean bl = direction < 0;
		int j = Math.abs(direction);

		for (int k = 0; k < j; k++) {
			if (bl) {
				while (skipWhitespaceToRightOfWord && i > 0 && (text.charAt(i - 1) == ' ' || text.charAt(i - 1) == '\n')) {
					i--;
				}

				while (i > 0 && text.charAt(i - 1) != ' ' && text.charAt(i - 1) != '\n') {
					i--;
				}
			} else {
				int l = text.length();
				int m = text.indexOf(32, i);
				int n = text.indexOf(10, i);
				if (m == -1 && n == -1) {
					i = -1;
				} else if (m != -1 && n != -1) {
					i = Math.min(m, n);
				} else if (m != -1) {
					i = m;
				} else {
					i = n;
				}

				if (i == -1) {
					i = l;
				} else {
					while (skipWhitespaceToRightOfWord && i < l && (text.charAt(i) == ' ' || text.charAt(i) == '\n')) {
						i++;
					}
				}
			}
		}

		return i;
	}

	public boolean isRightToLeft() {
		return this.rightToLeft;
	}

	@Environment(EnvType.CLIENT)
	static class Rectangle {
		protected final float xMin;
		protected final float yMin;
		protected final float xMax;
		protected final float yMax;
		protected final float red;
		protected final float green;
		protected final float blue;
		protected final float alpha;

		private Rectangle(float xMin, float yMin, float xMax, float yMax, float red, float green, float blue, float alpha) {
			this.xMin = xMin;
			this.yMin = yMin;
			this.xMax = xMax;
			this.yMax = yMax;
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.alpha = alpha;
		}

		public void draw(BufferBuilder bufferBuilder) {
			bufferBuilder.vertex((double)this.xMin, (double)this.yMin, 0.0).color(this.red, this.green, this.blue, this.alpha).next();
			bufferBuilder.vertex((double)this.xMax, (double)this.yMin, 0.0).color(this.red, this.green, this.blue, this.alpha).next();
			bufferBuilder.vertex((double)this.xMax, (double)this.yMax, 0.0).color(this.red, this.green, this.blue, this.alpha).next();
			bufferBuilder.vertex((double)this.xMin, (double)this.yMax, 0.0).color(this.red, this.green, this.blue, this.alpha).next();
		}
	}
}
