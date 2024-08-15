package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TextVisitFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Manages the rendering of text.
 * 
 * <p>The current instance used by the client can be obtained by
 * {@code MinecraftClient.getInstance().textRenderer}.
 * 
 * @see net.minecraft.client.MinecraftClient#textRenderer
 */
@Environment(EnvType.CLIENT)
public class TextRenderer {
	private static final float Z_INDEX = 0.01F;
	private static final Vector3f FORWARD_SHIFT = new Vector3f(0.0F, 0.0F, 0.03F);
	public static final int ARABIC_SHAPING_LETTERS_SHAPE = 8;
	/**
	 * The font height of the text that is rendered by the text renderer.
	 */
	public final int fontHeight = 9;
	public final Random random = Random.create();
	private final Function<Identifier, FontStorage> fontStorageAccessor;
	final boolean validateAdvance;
	private final TextHandler handler;

	public TextRenderer(Function<Identifier, FontStorage> fontStorageAccessor, boolean validateAdvance) {
		this.fontStorageAccessor = fontStorageAccessor;
		this.validateAdvance = validateAdvance;
		this.handler = new TextHandler(
			(codePoint, style) -> this.getFontStorage(style.getFont()).getGlyph(codePoint, this.validateAdvance).getAdvance(style.isBold())
		);
	}

	FontStorage getFontStorage(Identifier id) {
		return (FontStorage)this.fontStorageAccessor.apply(id);
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

	/**
	 * @param color the text color in the 0xAARRGGBB format
	 */
	public int draw(
		String text,
		float x,
		float y,
		int color,
		boolean shadow,
		Matrix4f matrix,
		VertexConsumerProvider vertexConsumers,
		TextRenderer.TextLayerType layerType,
		int backgroundColor,
		int light
	) {
		return this.draw(text, x, y, color, shadow, matrix, vertexConsumers, layerType, backgroundColor, light, this.isRightToLeft());
	}

	/**
	 * @param color the text color in the 0xAARRGGBB format
	 */
	public int draw(
		String text,
		float x,
		float y,
		int color,
		boolean shadow,
		Matrix4f matrix,
		VertexConsumerProvider vertexConsumers,
		TextRenderer.TextLayerType layerType,
		int backgroundColor,
		int light,
		boolean rightToLeft
	) {
		return this.drawInternal(text, x, y, color, shadow, matrix, vertexConsumers, layerType, backgroundColor, light, rightToLeft);
	}

	/**
	 * @param color the text color in the 0xAARRGGBB format
	 */
	public int draw(
		Text text,
		float x,
		float y,
		int color,
		boolean shadow,
		Matrix4f matrix,
		VertexConsumerProvider vertexConsumers,
		TextRenderer.TextLayerType layerType,
		int backgroundColor,
		int light
	) {
		return this.draw(text.asOrderedText(), x, y, color, shadow, matrix, vertexConsumers, layerType, backgroundColor, light);
	}

	/**
	 * @param color the text color in the 0xAARRGGBB format
	 */
	public int draw(
		OrderedText text,
		float x,
		float y,
		int color,
		boolean shadow,
		Matrix4f matrix,
		VertexConsumerProvider vertexConsumers,
		TextRenderer.TextLayerType layerType,
		int backgroundColor,
		int light
	) {
		return this.drawInternal(text, x, y, color, shadow, matrix, vertexConsumers, layerType, backgroundColor, light);
	}

	/**
	 * @param outlineColor the outline color in 0xAARRGGBB
	 * @param color the text color in 0xAARRGGBB
	 */
	public void drawWithOutline(
		OrderedText text, float x, float y, int color, int outlineColor, Matrix4f matrix, VertexConsumerProvider vertexConsumers, int light
	) {
		int i = tweakTransparency(outlineColor);
		TextRenderer.Drawer drawer = new TextRenderer.Drawer(vertexConsumers, 0.0F, 0.0F, i, false, matrix, TextRenderer.TextLayerType.NORMAL, light);

		for (int j = -1; j <= 1; j++) {
			for (int k = -1; k <= 1; k++) {
				if (j != 0 || k != 0) {
					float[] fs = new float[]{x};
					int l = j;
					int m = k;
					text.accept((index, style, codePoint) -> {
						boolean bl = style.isBold();
						FontStorage fontStorage = this.getFontStorage(style.getFont());
						Glyph glyph = fontStorage.getGlyph(codePoint, this.validateAdvance);
						drawer.x = fs[0] + (float)l * glyph.getShadowOffset();
						drawer.y = y + (float)m * glyph.getShadowOffset();
						fs[0] += glyph.getAdvance(bl);
						return drawer.accept(index, style.withColor(i), codePoint);
					});
				}
			}
		}

		TextRenderer.Drawer drawer2 = new TextRenderer.Drawer(
			vertexConsumers, x, y, tweakTransparency(color), false, matrix, TextRenderer.TextLayerType.POLYGON_OFFSET, light
		);
		text.accept(drawer2);
		drawer2.drawLayer(0, x);
	}

	private static int tweakTransparency(int argb) {
		return (argb & -67108864) == 0 ? ColorHelper.fullAlpha(argb) : argb;
	}

	private int drawInternal(
		String text,
		float x,
		float y,
		int color,
		boolean shadow,
		Matrix4f matrix,
		VertexConsumerProvider vertexConsumers,
		TextRenderer.TextLayerType layerType,
		int backgroundColor,
		int light,
		boolean mirror
	) {
		if (mirror) {
			text = this.mirror(text);
		}

		color = tweakTransparency(color);
		Matrix4f matrix4f = new Matrix4f(matrix);
		if (shadow) {
			this.drawLayer(text, x, y, color, true, matrix, vertexConsumers, layerType, backgroundColor, light);
			matrix4f.translate(FORWARD_SHIFT);
		}

		x = this.drawLayer(text, x, y, color, false, matrix4f, vertexConsumers, layerType, backgroundColor, light);
		return (int)x + (shadow ? 1 : 0);
	}

	private int drawInternal(
		OrderedText text,
		float x,
		float y,
		int color,
		boolean shadow,
		Matrix4f matrix,
		VertexConsumerProvider vertexConsumerProvider,
		TextRenderer.TextLayerType layerType,
		int backgroundColor,
		int light
	) {
		color = tweakTransparency(color);
		Matrix4f matrix4f = new Matrix4f(matrix);
		if (shadow) {
			this.drawLayer(text, x, y, color, true, matrix, vertexConsumerProvider, layerType, backgroundColor, light);
			matrix4f.translate(FORWARD_SHIFT);
		}

		x = this.drawLayer(text, x, y, color, false, matrix4f, vertexConsumerProvider, layerType, backgroundColor, light);
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
		TextRenderer.TextLayerType layerType,
		int underlineColor,
		int light
	) {
		TextRenderer.Drawer drawer = new TextRenderer.Drawer(vertexConsumerProvider, x, y, color, shadow, matrix, layerType, light);
		TextVisitFactory.visitFormatted(text, Style.EMPTY, drawer);
		return drawer.drawLayer(underlineColor, x);
	}

	private float drawLayer(
		OrderedText text,
		float x,
		float y,
		int color,
		boolean shadow,
		Matrix4f matrix,
		VertexConsumerProvider vertexConsumerProvider,
		TextRenderer.TextLayerType layerType,
		int underlineColor,
		int light
	) {
		TextRenderer.Drawer drawer = new TextRenderer.Drawer(vertexConsumerProvider, x, y, color, shadow, matrix, layerType, light);
		text.accept(drawer);
		return drawer.drawLayer(underlineColor, x);
	}

	void drawGlyph(
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

	/**
	 * Gets the width of some text when rendered.
	 * 
	 * @param text the text
	 */
	public int getWidth(String text) {
		return MathHelper.ceil(this.handler.getWidth(text));
	}

	/**
	 * Gets the width of some text when rendered.
	 * 
	 * @param text the text
	 */
	public int getWidth(StringVisitable text) {
		return MathHelper.ceil(this.handler.getWidth(text));
	}

	/**
	 * Gets the width of some text when rendered.
	 */
	public int getWidth(OrderedText text) {
		return MathHelper.ceil(this.handler.getWidth(text));
	}

	/**
	 * Trims a string to be at most {@code maxWidth} wide.
	 * 
	 * @return the trimmed string
	 */
	public String trimToWidth(String text, int maxWidth, boolean backwards) {
		return backwards ? this.handler.trimToWidthBackwards(text, maxWidth, Style.EMPTY) : this.handler.trimToWidth(text, maxWidth, Style.EMPTY);
	}

	/**
	 * Trims a string to be at most {@code maxWidth} wide.
	 * 
	 * @return the trimmed string
	 * @see TextHandler#trimToWidth(String, int, Style)
	 */
	public String trimToWidth(String text, int maxWidth) {
		return this.handler.trimToWidth(text, maxWidth, Style.EMPTY);
	}

	/**
	 * Trims a string to be at most {@code maxWidth} wide.
	 * 
	 * @return the text
	 * @see TextHandler#trimToWidth(StringVisitable, int, Style)
	 */
	public StringVisitable trimToWidth(StringVisitable text, int width) {
		return this.handler.trimToWidth(text, width, Style.EMPTY);
	}

	/**
	 * Gets the height of the text when it has been wrapped.
	 * 
	 * @return the height of the wrapped text
	 * @see TextRenderer#wrapLines(StringVisitable, int)
	 * @see #getWrappedLinesHeight(StringVisitable, int)
	 */
	public int getWrappedLinesHeight(String text, int maxWidth) {
		return 9 * this.handler.wrapLines(text, maxWidth, Style.EMPTY).size();
	}

	/**
	 * {@return the height of the text, after it has been wrapped, in pixels}
	 * @see TextRenderer#wrapLines(StringVisitable, int)
	 * @see #getWrappedLinesHeight(String, int)
	 */
	public int getWrappedLinesHeight(StringVisitable text, int maxWidth) {
		return 9 * this.handler.wrapLines(text, maxWidth, Style.EMPTY).size();
	}

	/**
	 * Wraps text when the rendered width of text exceeds the {@code width}.
	 * 
	 * @return a list of ordered text which has been wrapped
	 */
	public List<OrderedText> wrapLines(StringVisitable text, int width) {
		return Language.getInstance().reorder(this.handler.wrapLines(text, width, Style.EMPTY));
	}

	/**
	 * Checks if the currently set language uses right to left writing.
	 */
	public boolean isRightToLeft() {
		return Language.getInstance().isRightToLeft();
	}

	public TextHandler getTextHandler() {
		return this.handler;
	}

	@Environment(EnvType.CLIENT)
	class Drawer implements CharacterVisitor {
		final VertexConsumerProvider vertexConsumers;
		private final boolean shadow;
		private final float brightnessMultiplier;
		private final float red;
		private final float green;
		private final float blue;
		private final float alpha;
		private final Matrix4f matrix;
		private final TextRenderer.TextLayerType layerType;
		private final int light;
		float x;
		float y;
		@Nullable
		private List<GlyphRenderer.Rectangle> rectangles;

		private void addRectangle(GlyphRenderer.Rectangle rectangle) {
			if (this.rectangles == null) {
				this.rectangles = Lists.<GlyphRenderer.Rectangle>newArrayList();
			}

			this.rectangles.add(rectangle);
		}

		public Drawer(
			final VertexConsumerProvider vertexConsumers,
			final float x,
			final float y,
			final int color,
			final boolean shadow,
			final Matrix4f matrix,
			final TextRenderer.TextLayerType layerType,
			final int light
		) {
			this.vertexConsumers = vertexConsumers;
			this.x = x;
			this.y = y;
			this.shadow = shadow;
			this.brightnessMultiplier = shadow ? 0.25F : 1.0F;
			this.red = (float)(color >> 16 & 0xFF) / 255.0F * this.brightnessMultiplier;
			this.green = (float)(color >> 8 & 0xFF) / 255.0F * this.brightnessMultiplier;
			this.blue = (float)(color & 0xFF) / 255.0F * this.brightnessMultiplier;
			this.alpha = (float)(color >> 24 & 0xFF) / 255.0F;
			this.matrix = matrix;
			this.layerType = layerType;
			this.light = light;
		}

		@Override
		public boolean accept(int i, Style style, int j) {
			FontStorage fontStorage = TextRenderer.this.getFontStorage(style.getFont());
			Glyph glyph = fontStorage.getGlyph(j, TextRenderer.this.validateAdvance);
			GlyphRenderer glyphRenderer = style.isObfuscated() && j != 32 ? fontStorage.getObfuscatedGlyphRenderer(glyph) : fontStorage.getGlyphRenderer(j);
			boolean bl = style.isBold();
			float f = this.alpha;
			TextColor textColor = style.getColor();
			float g;
			float h;
			float l;
			if (textColor != null) {
				int k = textColor.getRgb();
				g = (float)(k >> 16 & 0xFF) / 255.0F * this.brightnessMultiplier;
				h = (float)(k >> 8 & 0xFF) / 255.0F * this.brightnessMultiplier;
				l = (float)(k & 0xFF) / 255.0F * this.brightnessMultiplier;
			} else {
				g = this.red;
				h = this.green;
				l = this.blue;
			}

			if (!(glyphRenderer instanceof EmptyGlyphRenderer)) {
				float m = bl ? glyph.getBoldOffset() : 0.0F;
				float n = this.shadow ? glyph.getShadowOffset() : 0.0F;
				VertexConsumer vertexConsumer = this.vertexConsumers.getBuffer(glyphRenderer.getLayer(this.layerType));
				TextRenderer.this.drawGlyph(glyphRenderer, bl, style.isItalic(), m, this.x + n, this.y + n, this.matrix, vertexConsumer, g, h, l, f, this.light);
			}

			float m = glyph.getAdvance(bl);
			float n = this.shadow ? 1.0F : 0.0F;
			if (style.isStrikethrough()) {
				this.addRectangle(new GlyphRenderer.Rectangle(this.x + n - 1.0F, this.y + n + 4.5F, this.x + n + m, this.y + n + 4.5F - 1.0F, 0.01F, g, h, l, f));
			}

			if (style.isUnderlined()) {
				this.addRectangle(new GlyphRenderer.Rectangle(this.x + n - 1.0F, this.y + n + 9.0F, this.x + n + m, this.y + n + 9.0F - 1.0F, 0.01F, g, h, l, f));
			}

			this.x += m;
			return true;
		}

		public float drawLayer(int underlineColor, float x) {
			if (underlineColor != 0) {
				float f = (float)(underlineColor >> 24 & 0xFF) / 255.0F;
				float g = (float)(underlineColor >> 16 & 0xFF) / 255.0F;
				float h = (float)(underlineColor >> 8 & 0xFF) / 255.0F;
				float i = (float)(underlineColor & 0xFF) / 255.0F;
				this.addRectangle(new GlyphRenderer.Rectangle(x - 1.0F, this.y + 9.0F, this.x, this.y - 1.0F, 0.01F, g, h, i, f));
			}

			if (this.rectangles != null) {
				GlyphRenderer glyphRenderer = TextRenderer.this.getFontStorage(Style.DEFAULT_FONT_ID).getRectangleRenderer();
				VertexConsumer vertexConsumer = this.vertexConsumers.getBuffer(glyphRenderer.getLayer(this.layerType));

				for (GlyphRenderer.Rectangle rectangle : this.rectangles) {
					glyphRenderer.drawRectangle(rectangle, this.matrix, vertexConsumer, this.light);
				}
			}

			return this.x;
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum TextLayerType {
		NORMAL,
		SEE_THROUGH,
		POLYGON_OFFSET;
	}
}
