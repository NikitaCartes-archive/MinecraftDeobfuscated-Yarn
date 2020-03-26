/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.EmptyGlyphRenderer;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.AffineTransformation;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class TextRenderer
implements AutoCloseable {
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

    @Override
    public void close() {
        this.fontStorage.close();
    }

    public int drawWithShadow(String text, float x, float y, int color) {
        RenderSystem.enableAlphaTest();
        return this.draw(text, x, y, color, AffineTransformation.identity().getMatrix(), true);
    }

    public int draw(String text, float x, float y, int color) {
        RenderSystem.enableAlphaTest();
        return this.draw(text, x, y, color, AffineTransformation.identity().getMatrix(), false);
    }

    public String mirror(String text) {
        try {
            Bidi bidi = new Bidi(new ArabicShaping(8).shape(text), 127);
            bidi.setReorderingMode(0);
            return bidi.writeReordered(2);
        } catch (ArabicShapingException arabicShapingException) {
            return text;
        }
    }

    private int draw(String text, float x, float y, int color, Matrix4f matrix, boolean shadow) {
        if (text == null) {
            return 0;
        }
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        int i = this.draw(text, x, y, color, shadow, matrix, immediate, false, 0, 0xF000F0);
        immediate.draw();
        return i;
    }

    public int draw(String text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumerProvider, boolean seeThrough, int backgroundColor, int light) {
        return this.drawInternal(text, x, y, color, shadow, matrix, vertexConsumerProvider, seeThrough, backgroundColor, light);
    }

    private int drawInternal(String text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumerProvider, boolean seeThrough, int backgroundColor, int light) {
        if (this.rightToLeft) {
            text = this.mirror(text);
        }
        if ((color & 0xFC000000) == 0) {
            color |= 0xFF000000;
        }
        if (shadow) {
            this.drawLayer(text, x, y, color, true, matrix, vertexConsumerProvider, seeThrough, backgroundColor, light);
        }
        Matrix4f matrix4f = matrix.copy();
        matrix4f.addToLastColumn(new Vector3f(0.0f, 0.0f, 0.001f));
        x = this.drawLayer(text, x, y, color, false, matrix4f, vertexConsumerProvider, seeThrough, backgroundColor, light);
        return (int)x + (shadow ? 1 : 0);
    }

    private float drawLayer(String text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumerProvider, boolean seeThrough, int underlineColor, int light) {
        float f = shadow ? 0.25f : 1.0f;
        float g = (float)(color >> 16 & 0xFF) / 255.0f * f;
        float h = (float)(color >> 8 & 0xFF) / 255.0f * f;
        float i = (float)(color & 0xFF) / 255.0f * f;
        float j = x;
        float k = g;
        float l = h;
        float m = i;
        float n = (float)(color >> 24 & 0xFF) / 255.0f;
        boolean bl = false;
        boolean bl2 = false;
        boolean bl3 = false;
        boolean bl4 = false;
        boolean bl5 = false;
        ArrayList<GlyphRenderer.Rectangle> list = Lists.newArrayList();
        for (int o = 0; o < text.length(); ++o) {
            float r;
            float q;
            GlyphRenderer glyphRenderer;
            char c = text.charAt(o);
            if (c == '\u00a7' && o + 1 < text.length()) {
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
                        k = (float)(p >> 16 & 0xFF) / 255.0f * f;
                        l = (float)(p >> 8 & 0xFF) / 255.0f * f;
                        m = (float)(p & 0xFF) / 255.0f * f;
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
                ++o;
                continue;
            }
            Glyph glyph = this.fontStorage.getGlyph(c);
            GlyphRenderer glyphRenderer2 = glyphRenderer = bl && c != ' ' ? this.fontStorage.getObfuscatedGlyphRenderer(glyph) : this.fontStorage.getGlyphRenderer(c);
            if (!(glyphRenderer instanceof EmptyGlyphRenderer)) {
                q = bl2 ? glyph.getBoldOffset() : 0.0f;
                r = shadow ? glyph.getShadowOffset() : 0.0f;
                VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(glyphRenderer.method_24045(seeThrough));
                this.drawGlyph(glyphRenderer, bl2, bl3, q, j + r, y + r, matrix, vertexConsumer, k, l, m, n, light);
            }
            q = glyph.getAdvance(bl2);
            float f2 = r = shadow ? 1.0f : 0.0f;
            if (bl5) {
                list.add(new GlyphRenderer.Rectangle(j + r - 1.0f, y + r + 4.5f, j + r + q, y + r + 4.5f - 1.0f, -0.01f, k, l, m, n));
            }
            if (bl4) {
                list.add(new GlyphRenderer.Rectangle(j + r - 1.0f, y + r + 9.0f, j + r + q, y + r + 9.0f - 1.0f, -0.01f, k, l, m, n));
            }
            j += q;
        }
        if (underlineColor != 0) {
            float s = (float)(underlineColor >> 24 & 0xFF) / 255.0f;
            float t = (float)(underlineColor >> 16 & 0xFF) / 255.0f;
            float u = (float)(underlineColor >> 8 & 0xFF) / 255.0f;
            float v = (float)(underlineColor & 0xFF) / 255.0f;
            list.add(new GlyphRenderer.Rectangle(x - 1.0f, y + 9.0f, j + 1.0f, y - 1.0f, 0.01f, t, u, v, s));
        }
        if (!list.isEmpty()) {
            GlyphRenderer glyphRenderer2 = this.fontStorage.getRectangleRenderer();
            VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(glyphRenderer2.method_24045(seeThrough));
            for (GlyphRenderer.Rectangle rectangle : list) {
                glyphRenderer2.drawRectangle(rectangle, matrix, vertexConsumer2, light);
            }
        }
        return j;
    }

    private void drawGlyph(GlyphRenderer glyphRenderer, boolean bold, boolean italic, float weight, float x, float y, Matrix4f matrix, VertexConsumer vertexConsumer, float red, float green, float blue, float alpha, int light) {
        glyphRenderer.draw(italic, x, y, matrix, vertexConsumer, red, green, blue, alpha, light);
        if (bold) {
            glyphRenderer.draw(italic, x + weight, y, matrix, vertexConsumer, red, green, blue, alpha, light);
        }
    }

    public int getStringWidth(String text) {
        if (text == null) {
            return 0;
        }
        float f = 0.0f;
        boolean bl = false;
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            if (c == '\u00a7' && i < text.length() - 1) {
                Formatting formatting;
                if ((formatting = Formatting.byCode(text.charAt(++i))) == Formatting.BOLD) {
                    bl = true;
                    continue;
                }
                if (formatting == null || !formatting.affectsGlyphWidth()) continue;
                bl = false;
                continue;
            }
            f += this.fontStorage.getGlyph(c).getAdvance(bl);
        }
        return MathHelper.ceil(f);
    }

    public float getCharWidth(char character) {
        if (character == '\u00a7') {
            return 0.0f;
        }
        return this.fontStorage.getGlyph(character).getAdvance(false);
    }

    public String trimToWidth(String text, int width) {
        return this.trimToWidth(text, width, false);
    }

    public String trimToWidth(String text, int width, boolean rightToLeft) {
        StringBuilder stringBuilder = new StringBuilder();
        float f = 0.0f;
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
            } else if (c == '\u00a7') {
                bl = true;
            } else {
                f += this.getCharWidth(c);
                if (bl2) {
                    f += 1.0f;
                }
            }
            if (f > (float)width) break;
            if (rightToLeft) {
                stringBuilder.insert(0, c);
                continue;
            }
            stringBuilder.append(c);
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
        Matrix4f matrix4f = AffineTransformation.identity().getMatrix();
        for (String string : list) {
            float f = x;
            if (this.rightToLeft) {
                int i = this.getStringWidth(this.mirror(string));
                f += (float)(maxWidth - i);
            }
            this.draw(string, f, y, color, matrix4f, false);
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
        int k;
        int i = Math.max(1, offset);
        int j = text.length();
        float f = 0.0f;
        int l = -1;
        boolean bl = false;
        boolean bl2 = true;
        for (k = 0; k < j; ++k) {
            char c = text.charAt(k);
            switch (c) {
                case '\u00a7': {
                    Formatting formatting;
                    if (k >= j - 1) break;
                    if ((formatting = Formatting.byCode(text.charAt(++k))) == Formatting.BOLD) {
                        bl = true;
                        break;
                    }
                    if (formatting == null || !formatting.affectsGlyphWidth()) break;
                    bl = false;
                    break;
                }
                case '\n': {
                    --k;
                    break;
                }
                case ' ': {
                    l = k;
                }
                default: {
                    if (f != 0.0f) {
                        bl2 = false;
                    }
                    f += this.getCharWidth(c);
                    if (!bl) break;
                    f += 1.0f;
                }
            }
            if (c == '\n') {
                l = ++k;
                break;
            }
            if (!(f > (float)i)) continue;
            if (!bl2) break;
            ++k;
            break;
        }
        if (k != j && l != -1 && l < k) {
            return l;
        }
        return k;
    }

    public int findWordEdge(String text, int direction, int position, boolean skipWhitespaceToRightOfWord) {
        int i = position;
        boolean bl = direction < 0;
        int j = Math.abs(direction);
        for (int k = 0; k < j; ++k) {
            if (bl) {
                while (skipWhitespaceToRightOfWord && i > 0 && (text.charAt(i - 1) == ' ' || text.charAt(i - 1) == '\n')) {
                    --i;
                }
                while (i > 0 && text.charAt(i - 1) != ' ' && text.charAt(i - 1) != '\n') {
                    --i;
                }
                continue;
            }
            int l = text.length();
            int m = text.indexOf(32, i);
            int n = text.indexOf(10, i);
            i = m == -1 && n == -1 ? -1 : (m != -1 && n != -1 ? Math.min(m, n) : (m != -1 ? m : n));
            if (i == -1) {
                i = l;
                continue;
            }
            while (skipWhitespaceToRightOfWord && i < l && (text.charAt(i) == ' ' || text.charAt(i) == '\n')) {
                ++i;
            }
        }
        return i;
    }

    public boolean isRightToLeft() {
        return this.rightToLeft;
    }
}

