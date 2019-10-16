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
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Rotation3;
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

	public void setFonts(List<Font> list) {
		this.fontStorage.setFonts(list);
	}

	public void close() {
		this.fontStorage.close();
	}

	public int drawWithShadow(String string, float f, float g, int i) {
		RenderSystem.enableAlphaTest();
		return this.method_22941(string, f, g, i, Rotation3.identity().getMatrix(), true);
	}

	public int draw(String string, float f, float g, int i) {
		RenderSystem.enableAlphaTest();
		return this.method_22941(string, f, g, i, Rotation3.identity().getMatrix(), false);
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

	private int method_22941(String string, float f, float g, int i, Matrix4f matrix4f, boolean bl) {
		if (string == null) {
			return 0;
		} else {
			LayeredVertexConsumerStorage.Drawer drawer = LayeredVertexConsumerStorage.makeDrawer(Tessellator.getInstance().getBufferBuilder());
			int j = this.method_22942(string, f, g, i, bl, matrix4f, drawer, false, 0, 15728880);
			drawer.draw();
			return j;
		}
	}

	public int method_22942(
		String string, float f, float g, int i, boolean bl, Matrix4f matrix4f, LayeredVertexConsumerStorage layeredVertexConsumerStorage, boolean bl2, int j, int k
	) {
		return this.draw(string, f, g, i, bl, matrix4f, layeredVertexConsumerStorage, bl2, j, k);
	}

	private int draw(
		String string, float f, float g, int i, boolean bl, Matrix4f matrix4f, LayeredVertexConsumerStorage layeredVertexConsumerStorage, boolean bl2, int j, int k
	) {
		if (this.rightToLeft) {
			string = this.mirror(string);
		}

		if ((i & -67108864) == 0) {
			i |= -16777216;
		}

		if (bl) {
			this.drawLayer(string, f, g, i, true, matrix4f, layeredVertexConsumerStorage, bl2, j, k);
		}

		f = this.drawLayer(string, f, g, i, false, matrix4f, layeredVertexConsumerStorage, bl2, j, k);
		return (int)f + (bl ? 1 : 0);
	}

	private float drawLayer(
		String string, float f, float g, int i, boolean bl, Matrix4f matrix4f, LayeredVertexConsumerStorage layeredVertexConsumerStorage, boolean bl2, int j, int k
	) {
		float h = bl ? 0.25F : 1.0F;
		float l = (float)(i >> 16 & 0xFF) / 255.0F * h;
		float m = (float)(i >> 8 & 0xFF) / 255.0F * h;
		float n = (float)(i & 0xFF) / 255.0F * h;
		float o = f;
		float p = l;
		float q = m;
		float r = n;
		float s = (float)(i >> 24 & 0xFF) / 255.0F;
		boolean bl3 = false;
		boolean bl4 = false;
		boolean bl5 = false;
		boolean bl6 = false;
		boolean bl7 = false;
		List<GlyphRenderer.Rectangle> list = Lists.<GlyphRenderer.Rectangle>newArrayList();

		for (int t = 0; t < string.length(); t++) {
			char c = string.charAt(t);
			if (c == 167 && t + 1 < string.length()) {
				Formatting formatting = Formatting.byCode(string.charAt(t + 1));
				if (formatting != null) {
					if (formatting.affectsGlyphWidth()) {
						bl3 = false;
						bl4 = false;
						bl7 = false;
						bl6 = false;
						bl5 = false;
						p = l;
						q = m;
						r = n;
					}

					if (formatting.getColorValue() != null) {
						int u = formatting.getColorValue();
						p = (float)(u >> 16 & 0xFF) / 255.0F * h;
						q = (float)(u >> 8 & 0xFF) / 255.0F * h;
						r = (float)(u & 0xFF) / 255.0F * h;
					} else if (formatting == Formatting.OBFUSCATED) {
						bl3 = true;
					} else if (formatting == Formatting.BOLD) {
						bl4 = true;
					} else if (formatting == Formatting.STRIKETHROUGH) {
						bl7 = true;
					} else if (formatting == Formatting.UNDERLINE) {
						bl6 = true;
					} else if (formatting == Formatting.ITALIC) {
						bl5 = true;
					}
				}

				t++;
			} else {
				Glyph glyph = this.fontStorage.getGlyph(c);
				GlyphRenderer glyphRenderer = bl3 && c != ' ' ? this.fontStorage.getObfuscatedGlyphRenderer(glyph) : this.fontStorage.getGlyphRenderer(c);
				Identifier identifier = glyphRenderer.getId();
				if (identifier != null) {
					float v = bl4 ? glyph.getBoldOffset() : 0.0F;
					float w = bl ? glyph.getShadowOffset() : 0.0F;
					VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(bl2 ? RenderLayer.getTextSeeThrough(identifier) : RenderLayer.getText(identifier));
					this.drawGlyph(glyphRenderer, bl4, bl5, v, o + w, g + w, matrix4f, vertexConsumer, p, q, r, s, k);
				}

				float v = glyph.getAdvance(bl4);
				float w = bl ? 1.0F : 0.0F;
				if (bl7) {
					list.add(new GlyphRenderer.Rectangle(o + w - 1.0F, g + w + 4.5F, o + w + v, g + w + 4.5F - 1.0F, -0.01F, p, q, r, s));
				}

				if (bl6) {
					list.add(new GlyphRenderer.Rectangle(o + w - 1.0F, g + w + 9.0F, o + w + v, g + w + 9.0F - 1.0F, -0.01F, p, q, r, s));
				}

				o += v;
			}
		}

		if (j != 0) {
			float x = (float)(j >> 24 & 0xFF) / 255.0F;
			float y = (float)(j >> 16 & 0xFF) / 255.0F;
			float z = (float)(j >> 8 & 0xFF) / 255.0F;
			float aa = (float)(j & 0xFF) / 255.0F;
			list.add(new GlyphRenderer.Rectangle(f - 1.0F, g + 9.0F, o + 1.0F, g - 1.0F, 0.01F, y, z, aa, x));
		}

		if (!list.isEmpty()) {
			GlyphRenderer glyphRenderer2 = this.fontStorage.method_22943();
			Identifier identifier2 = glyphRenderer2.getId();
			if (identifier2 != null) {
				VertexConsumer vertexConsumer2 = layeredVertexConsumerStorage.getBuffer(bl2 ? RenderLayer.getTextSeeThrough(identifier2) : RenderLayer.getText(identifier2));

				for (GlyphRenderer.Rectangle rectangle : list) {
					glyphRenderer2.method_22944(rectangle, matrix4f, vertexConsumer2, k);
				}
			}
		}

		return o;
	}

	private void drawGlyph(
		GlyphRenderer glyphRenderer,
		boolean bl,
		boolean bl2,
		float f,
		float g,
		float h,
		Matrix4f matrix4f,
		VertexConsumer vertexConsumer,
		float i,
		float j,
		float k,
		float l,
		int m
	) {
		glyphRenderer.draw(bl2, g, h, matrix4f, vertexConsumer, i, j, k, l, m);
		if (bl) {
			glyphRenderer.draw(bl2, g + f, h, matrix4f, vertexConsumer, i, j, k, l, m);
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

	public float getCharWidth(char c) {
		return c == 167 ? 0.0F : this.fontStorage.getGlyph(c).getAdvance(false);
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
				if (formatting == Formatting.BOLD) {
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
		List<String> list = this.wrapStringToWidthAsList(string, k);
		Matrix4f matrix4f = Rotation3.identity().getMatrix();

		for (String string2 : list) {
			float f = (float)i;
			if (this.rightToLeft) {
				int m = this.getStringWidth(this.mirror(string2));
				f += (float)(k - m);
			}

			this.method_22941(string2, f, (float)j, l, matrix4f, false);
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
						if (formatting == Formatting.BOLD) {
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
}
