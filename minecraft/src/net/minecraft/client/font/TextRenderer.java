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
	private final TextureManager field_1998;
	private final FontStorage field_1997;
	private boolean rightToLeft;

	public TextRenderer(TextureManager textureManager, FontStorage fontStorage) {
		this.field_1998 = textureManager;
		this.field_1997 = fontStorage;
	}

	public void setFonts(List<Font> list) {
		this.field_1997.setFonts(list);
	}

	public void close() {
		this.field_1997.close();
	}

	public int drawWithShadow(String string, float f, float g, int i) {
		GlStateManager.enableAlphaTest();
		return this.draw(string, f, g, i, true);
	}

	public int draw(String string, float f, float g, int i) {
		GlStateManager.enableAlphaTest();
		return this.draw(string, f, g, i, false);
	}

	public String mirror(String string) {
		try {
			Bidi bidi = new Bidi(new ArabicShaping(8).shape(string), 127);
			bidi.setReorderingMode(0);
			return bidi.writeReordered(2);
		} catch (ArabicShapingException var3) {
			return string;
		}
	}

	private int draw(String string, float f, float g, int i, boolean bl) {
		if (string == null) {
			return 0;
		} else {
			if (this.rightToLeft) {
				string = this.mirror(string);
			}

			if ((i & -67108864) == 0) {
				i |= -16777216;
			}

			if (bl) {
				this.drawLayer(string, f, g, i, true);
			}

			f = this.drawLayer(string, f, g, i, false);
			return (int)f + (bl ? 1 : 0);
		}
	}

	private float drawLayer(String string, float f, float g, int i, boolean bl) {
		float h = bl ? 0.25F : 1.0F;
		float j = (float)(i >> 16 & 0xFF) / 255.0F * h;
		float k = (float)(i >> 8 & 0xFF) / 255.0F * h;
		float l = (float)(i & 0xFF) / 255.0F * h;
		float m = j;
		float n = k;
		float o = l;
		float p = (float)(i >> 24 & 0xFF) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		Identifier identifier = null;
		bufferBuilder.method_1328(7, VertexFormats.field_1575);
		boolean bl2 = false;
		boolean bl3 = false;
		boolean bl4 = false;
		boolean bl5 = false;
		boolean bl6 = false;
		List<TextRenderer.Rectangle> list = Lists.<TextRenderer.Rectangle>newArrayList();

		for (int q = 0; q < string.length(); q++) {
			char c = string.charAt(q);
			if (c == 167 && q + 1 < string.length()) {
				Formatting formatting = Formatting.byCode(string.charAt(q + 1));
				if (formatting != null) {
					if (formatting.affectsGlyphWidth()) {
						bl2 = false;
						bl3 = false;
						bl6 = false;
						bl5 = false;
						bl4 = false;
						m = j;
						n = k;
						o = l;
					}

					if (formatting.getColorValue() != null) {
						int r = formatting.getColorValue();
						m = (float)(r >> 16 & 0xFF) / 255.0F * h;
						n = (float)(r >> 8 & 0xFF) / 255.0F * h;
						o = (float)(r & 0xFF) / 255.0F * h;
					} else if (formatting == Formatting.field_1051) {
						bl2 = true;
					} else if (formatting == Formatting.field_1067) {
						bl3 = true;
					} else if (formatting == Formatting.field_1055) {
						bl6 = true;
					} else if (formatting == Formatting.field_1073) {
						bl5 = true;
					} else if (formatting == Formatting.field_1056) {
						bl4 = true;
					}
				}

				q++;
			} else {
				Glyph glyph = this.field_1997.getGlyph(c);
				GlyphRenderer glyphRenderer = bl2 && c != ' ' ? this.field_1997.method_2013(glyph) : this.field_1997.method_2014(c);
				Identifier identifier2 = glyphRenderer.getId();
				if (identifier2 != null) {
					if (identifier != identifier2) {
						tessellator.draw();
						this.field_1998.bindTexture(identifier2);
						bufferBuilder.method_1328(7, VertexFormats.field_1575);
						identifier = identifier2;
					}

					float s = bl3 ? glyph.getBoldOffset() : 0.0F;
					float t = bl ? glyph.getShadowOffset() : 0.0F;
					this.method_1710(glyphRenderer, bl3, bl4, s, f + t, g + t, bufferBuilder, m, n, o, p);
				}

				float s = glyph.getAdvance(bl3);
				float t = bl ? 1.0F : 0.0F;
				if (bl6) {
					list.add(new TextRenderer.Rectangle(f + t - 1.0F, g + t + 4.5F, f + t + s, g + t + 4.5F - 1.0F, m, n, o, p));
				}

				if (bl5) {
					list.add(new TextRenderer.Rectangle(f + t - 1.0F, g + t + 9.0F, f + t + s, g + t + 9.0F - 1.0F, m, n, o, p));
				}

				f += s;
			}
		}

		tessellator.draw();
		if (!list.isEmpty()) {
			GlStateManager.disableTexture();
			bufferBuilder.method_1328(7, VertexFormats.field_1576);

			for (TextRenderer.Rectangle rectangle : list) {
				rectangle.draw(bufferBuilder);
			}

			tessellator.draw();
			GlStateManager.enableTexture();
		}

		return f;
	}

	private void method_1710(
		GlyphRenderer glyphRenderer, boolean bl, boolean bl2, float f, float g, float h, BufferBuilder bufferBuilder, float i, float j, float k, float l
	) {
		glyphRenderer.method_2025(this.field_1998, bl2, g, h, bufferBuilder, i, j, k, l);
		if (bl) {
			glyphRenderer.method_2025(this.field_1998, bl2, g + f, h, bufferBuilder, i, j, k, l);
		}
	}

	public int getStringWidth(String string) {
		if (string == null) {
			return 0;
		} else {
			float f = 0.0F;
			boolean bl = false;

			for (int i = 0; i < string.length(); i++) {
				char c = string.charAt(i);
				if (c == 167 && i < string.length() - 1) {
					Formatting formatting = Formatting.byCode(string.charAt(++i));
					if (formatting == Formatting.field_1067) {
						bl = true;
					} else if (formatting != null && formatting.affectsGlyphWidth()) {
						bl = false;
					}
				} else {
					f += this.field_1997.getGlyph(c).getAdvance(bl);
				}
			}

			return MathHelper.ceil(f);
		}
	}

	public float getCharWidth(char c) {
		return c == 167 ? 0.0F : this.field_1997.getGlyph(c).getAdvance(false);
	}

	public String trimToWidth(String string, int i) {
		return this.trimToWidth(string, i, false);
	}

	public String trimToWidth(String string, int i, boolean bl) {
		StringBuilder stringBuilder = new StringBuilder();
		float f = 0.0F;
		int j = bl ? string.length() - 1 : 0;
		int k = bl ? -1 : 1;
		boolean bl2 = false;
		boolean bl3 = false;

		for (int l = j; l >= 0 && l < string.length() && f < (float)i; l += k) {
			char c = string.charAt(l);
			if (bl2) {
				bl2 = false;
				Formatting formatting = Formatting.byCode(c);
				if (formatting == Formatting.field_1067) {
					bl3 = true;
				} else if (formatting != null && formatting.affectsGlyphWidth()) {
					bl3 = false;
				}
			} else if (c == 167) {
				bl2 = true;
			} else {
				f += this.getCharWidth(c);
				if (bl3) {
					f++;
				}
			}

			if (f > (float)i) {
				break;
			}

			if (bl) {
				stringBuilder.insert(0, c);
			} else {
				stringBuilder.append(c);
			}
		}

		return stringBuilder.toString();
	}

	private String trimEndNewlines(String string) {
		while (string != null && string.endsWith("\n")) {
			string = string.substring(0, string.length() - 1);
		}

		return string;
	}

	public void drawStringBounded(String string, int i, int j, int k, int l) {
		string = this.trimEndNewlines(string);
		this.renderStringBounded(string, i, j, k, l);
	}

	private void renderStringBounded(String string, int i, int j, int k, int l) {
		for (String string2 : this.wrapStringToWidthAsList(string, k)) {
			float f = (float)i;
			if (this.rightToLeft) {
				int m = this.getStringWidth(this.mirror(string2));
				f += (float)(k - m);
			}

			this.draw(string2, f, (float)j, l, false);
			j += 9;
		}
	}

	public int getStringBoundedHeight(String string, int i) {
		return 9 * this.wrapStringToWidthAsList(string, i).size();
	}

	public void setRightToLeft(boolean bl) {
		this.rightToLeft = bl;
	}

	public List<String> wrapStringToWidthAsList(String string, int i) {
		return Arrays.asList(this.wrapStringToWidth(string, i).split("\n"));
	}

	public String wrapStringToWidth(String string, int i) {
		String string2 = "";

		while (!string.isEmpty()) {
			int j = this.getCharacterCountForWidth(string, i);
			if (string.length() <= j) {
				return string2 + string;
			}

			String string3 = string.substring(0, j);
			char c = string.charAt(j);
			boolean bl = c == ' ' || c == '\n';
			string = Formatting.getFormatAtEnd(string3) + string.substring(j + (bl ? 1 : 0));
			string2 = string2 + string3 + "\n";
		}

		return string2;
	}

	public int getCharacterCountForWidth(String string, int i) {
		int j = Math.max(1, i);
		int k = string.length();
		float f = 0.0F;
		int l = 0;
		int m = -1;
		boolean bl = false;

		for (boolean bl2 = true; l < k; l++) {
			char c = string.charAt(l);
			switch (c) {
				case '\n':
					l--;
					break;
				case ' ':
					m = l;
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
					if (l < k - 1) {
						Formatting formatting = Formatting.byCode(string.charAt(++l));
						if (formatting == Formatting.field_1067) {
							bl = true;
						} else if (formatting != null && formatting.affectsGlyphWidth()) {
							bl = false;
						}
					}
			}

			if (c == '\n') {
				m = ++l;
				break;
			}

			if (f > (float)j) {
				if (bl2) {
					l++;
				}
				break;
			}
		}

		return l != k && m != -1 && m < l ? m : l;
	}

	public int findWordEdge(String string, int i, int j, boolean bl) {
		int k = j;
		boolean bl2 = i < 0;
		int l = Math.abs(i);

		for (int m = 0; m < l; m++) {
			if (bl2) {
				while (bl && k > 0 && (string.charAt(k - 1) == ' ' || string.charAt(k - 1) == '\n')) {
					k--;
				}

				while (k > 0 && string.charAt(k - 1) != ' ' && string.charAt(k - 1) != '\n') {
					k--;
				}
			} else {
				int n = string.length();
				int o = string.indexOf(32, k);
				int p = string.indexOf(10, k);
				if (o == -1 && p == -1) {
					k = -1;
				} else if (o != -1 && p != -1) {
					k = Math.min(o, p);
				} else if (o != -1) {
					k = o;
				} else {
					k = p;
				}

				if (k == -1) {
					k = n;
				} else {
					while (bl && k < n && (string.charAt(k) == ' ' || string.charAt(k) == '\n')) {
						k++;
					}
				}
			}
		}

		return k;
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

		private Rectangle(float f, float g, float h, float i, float j, float k, float l, float m) {
			this.xMin = f;
			this.yMin = g;
			this.xMax = h;
			this.yMax = i;
			this.red = j;
			this.green = k;
			this.blue = l;
			this.alpha = m;
		}

		public void draw(BufferBuilder bufferBuilder) {
			bufferBuilder.vertex((double)this.xMin, (double)this.yMin, 0.0).color(this.red, this.green, this.blue, this.alpha).next();
			bufferBuilder.vertex((double)this.xMax, (double)this.yMin, 0.0).color(this.red, this.green, this.blue, this.alpha).next();
			bufferBuilder.vertex((double)this.xMax, (double)this.yMax, 0.0).color(this.red, this.green, this.blue, this.alpha).next();
			bufferBuilder.vertex((double)this.xMin, (double)this.yMax, 0.0).color(this.red, this.green, this.blue, this.alpha).next();
		}
	}
}
