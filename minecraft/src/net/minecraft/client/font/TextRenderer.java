package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Rotation3;
import net.minecraft.client.util.math.Vector3f;
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
		RenderSystem.enableAlphaTest();
		return this.draw(text, x, y, color, Rotation3.identity().getMatrix(), true);
	}

	public int draw(String text, float x, float y, int color) {
		RenderSystem.enableAlphaTest();
		return this.draw(text, x, y, color, Rotation3.identity().getMatrix(), false);
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

	private int draw(String text, float x, float y, int color, Matrix4f matrix, boolean shadow) {
		if (text == null) {
			return 0;
		} else {
			VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
			int i = this.draw(text, x, y, color, shadow, matrix, immediate, false, 0, 15728880);
			immediate.draw();
			return i;
		}
	}

	public int draw(
		String text,
		float x,
		float y,
		int color,
		boolean shadow,
		Matrix4f matrix,
		VertexConsumerProvider vertexConsumerProvider,
		boolean seeThrough,
		int backgroundColor,
		int light
	) {
		return this.drawInternal(text, x, y, color, shadow, matrix, vertexConsumerProvider, seeThrough, backgroundColor, light);
	}

	private int drawInternal(
		String text,
		float x,
		float y,
		int color,
		boolean shadow,
		Matrix4f matrix,
		VertexConsumerProvider vertexConsumerProvider,
		boolean seeThrough,
		int backgroundColor,
		int light
	) {
		if (this.rightToLeft) {
			text = this.mirror(text);
		}

		if ((color & -67108864) == 0) {
			color |= -16777216;
		}

		if (shadow) {
			this.drawLayer(text, x, y, color, true, matrix, vertexConsumerProvider, seeThrough, backgroundColor, light);
		}

		Matrix4f matrix4f = matrix.copy();
		matrix4f.addToLastColumn(new Vector3f(0.0F, 0.0F, 0.001F));
		x = this.drawLayer(text, x, y, color, false, matrix4f, vertexConsumerProvider, seeThrough, backgroundColor, light);
		return (int)x + (shadow ? 1 : 0);
	}

	private float drawLayer(
		String text,
		float x,
		float y,
		int color,
		boolean shadow,
		Matrix4f matrix,
		VertexConsumerProvider vertexConsumerProvider,
		boolean seeThrough,
		int underlineColor,
		int light
	) {
		float f = shadow ? 0.25F : 1.0F;
		float g = (float)(color >> 16 & 0xFF) / 255.0F * f;
		float h = (float)(color >> 8 & 0xFF) / 255.0F * f;
		float i = (float)(color & 0xFF) / 255.0F * f;
		float j = x;
		float k = g;
		float l = h;
		float m = i;
		float n = (float)(color >> 24 & 0xFF) / 255.0F;
		boolean bl = false;
		boolean bl2 = false;
		boolean bl3 = false;
		boolean bl4 = false;
		boolean bl5 = false;
		List<GlyphRenderer.Rectangle> list = Lists.<GlyphRenderer.Rectangle>newArrayList();

		for (int o = 0; o < text.length(); o++) {
			char c = text.charAt(o);
			if (c == 167 && o + 1 < text.length()) {
				Formatting formatting = Formatting.byCode(text.charAt(o + 1));
				if (formatting != null) {
					if (formatting.affectsGlyphWidth()) {
						bl = false;
						bl2 = false;
						bl5 = false;
						bl4 = false;
						bl3 = false;
						k = g;
						l = h;
						m = i;
					}

					if (formatting.getColorValue() != null) {
						int p = formatting.getColorValue();
						k = (float)(p >> 16 & 0xFF) / 255.0F * f;
						l = (float)(p >> 8 & 0xFF) / 255.0F * f;
						m = (float)(p & 0xFF) / 255.0F * f;
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

				o++;
			} else {
				Glyph glyph = this.fontStorage.getGlyph(c);
				GlyphRenderer glyphRenderer = bl && c != ' ' ? this.fontStorage.getObfuscatedGlyphRenderer(glyph) : this.fontStorage.getGlyphRenderer(c);
				Identifier identifier = glyphRenderer.getId();
				if (identifier != null) {
					float q = bl2 ? glyph.getBoldOffset() : 0.0F;
					float r = shadow ? glyph.getShadowOffset() : 0.0F;
					VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(seeThrough ? RenderLayer.getTextSeeThrough(identifier) : RenderLayer.getText(identifier));
					this.drawGlyph(glyphRenderer, bl2, bl3, q, j + r, y + r, matrix, vertexConsumer, k, l, m, n, light);
				}

				float q = glyph.getAdvance(bl2);
				float r = shadow ? 1.0F : 0.0F;
				if (bl5) {
					list.add(new GlyphRenderer.Rectangle(j + r - 1.0F, y + r + 4.5F, j + r + q, y + r + 4.5F - 1.0F, -0.01F, k, l, m, n));
				}

				if (bl4) {
					list.add(new GlyphRenderer.Rectangle(j + r - 1.0F, y + r + 9.0F, j + r + q, y + r + 9.0F - 1.0F, -0.01F, k, l, m, n));
				}

				j += q;
			}
		}

		if (underlineColor != 0) {
			float s = (float)(underlineColor >> 24 & 0xFF) / 255.0F;
			float t = (float)(underlineColor >> 16 & 0xFF) / 255.0F;
			float u = (float)(underlineColor >> 8 & 0xFF) / 255.0F;
			float v = (float)(underlineColor & 0xFF) / 255.0F;
			list.add(new GlyphRenderer.Rectangle(x - 1.0F, y + 9.0F, j + 1.0F, y - 1.0F, 0.01F, t, u, v, s));
		}

		if (!list.isEmpty()) {
			GlyphRenderer glyphRenderer2 = this.fontStorage.getRectangleRenderer();
			Identifier identifier2 = glyphRenderer2.getId();
			if (identifier2 != null) {
				VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(
					seeThrough ? RenderLayer.getTextSeeThrough(identifier2) : RenderLayer.getText(identifier2)
				);

				for (GlyphRenderer.Rectangle rectangle : list) {
					glyphRenderer2.drawRectangle(rectangle, matrix, vertexConsumer2, light);
				}
			}
		}

		return j;
	}

	private void drawGlyph(
		GlyphRenderer glyphRenderer,
		boolean bold,
		boolean italic,
		float weight,
		float x,
		float y,
		Matrix4f matrix,
		VertexConsumer vertexConsumer,
		float red,
		float green,
		float blue,
		float alpha,
		int light
	) {
		glyphRenderer.draw(italic, x, y, matrix, vertexConsumer, red, green, blue, alpha, light);
		if (bold) {
			glyphRenderer.draw(italic, x + weight, y, matrix, vertexConsumer, red, green, blue, alpha, light);
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
		List<String> list = this.wrapStringToWidthAsList(text, maxWidth);
		Matrix4f matrix4f = Rotation3.identity().getMatrix();

		for (String string : list) {
			float f = (float)x;
			if (this.rightToLeft) {
				int i = this.getStringWidth(this.mirror(string));
				f += (float)(maxWidth - i);
			}

			this.draw(string, f, (float)y, color, matrix4f, false);
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
}
