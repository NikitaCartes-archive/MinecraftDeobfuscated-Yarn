package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.GuiAtlasManager;
import net.minecraft.client.texture.Scaling;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector2ic;

@Environment(EnvType.CLIENT)
public class DrawContext {
	public static final float field_44931 = 10000.0F;
	public static final float field_44932 = -10000.0F;
	private static final int field_44655 = 2;
	private final MinecraftClient client;
	private final MatrixStack matrices;
	private final VertexConsumerProvider.Immediate vertexConsumers;
	private final DrawContext.ScissorStack scissorStack = new DrawContext.ScissorStack();
	private final GuiAtlasManager guiAtlasManager;
	private boolean runningDrawCallback;

	private DrawContext(MinecraftClient client, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers) {
		this.client = client;
		this.matrices = matrices;
		this.vertexConsumers = vertexConsumers;
		this.guiAtlasManager = client.getGuiAtlasManager();
	}

	public DrawContext(MinecraftClient client, VertexConsumerProvider.Immediate vertexConsumers) {
		this(client, new MatrixStack(), vertexConsumers);
	}

	@Deprecated
	public void draw(Runnable drawCallback) {
		this.draw();
		this.runningDrawCallback = true;
		drawCallback.run();
		this.runningDrawCallback = false;
		this.draw();
	}

	@Deprecated
	private void tryDraw() {
		if (!this.runningDrawCallback) {
			this.draw();
		}
	}

	@Deprecated
	private void drawIfRunning() {
		if (this.runningDrawCallback) {
			this.draw();
		}
	}

	public int getScaledWindowWidth() {
		return this.client.getWindow().getScaledWidth();
	}

	public int getScaledWindowHeight() {
		return this.client.getWindow().getScaledHeight();
	}

	public MatrixStack getMatrices() {
		return this.matrices;
	}

	public VertexConsumerProvider.Immediate getVertexConsumers() {
		return this.vertexConsumers;
	}

	public void draw() {
		RenderSystem.disableDepthTest();
		this.vertexConsumers.draw();
		RenderSystem.enableDepthTest();
	}

	public void drawHorizontalLine(int x1, int x2, int y, int color) {
		this.drawHorizontalLine(RenderLayer.getGui(), x1, x2, y, color);
	}

	public void drawHorizontalLine(RenderLayer layer, int x1, int x2, int y, int color) {
		if (x2 < x1) {
			int i = x1;
			x1 = x2;
			x2 = i;
		}

		this.fill(layer, x1, y, x2 + 1, y + 1, color);
	}

	public void drawVerticalLine(int x, int y1, int y2, int color) {
		this.drawVerticalLine(RenderLayer.getGui(), x, y1, y2, color);
	}

	public void drawVerticalLine(RenderLayer layer, int x, int y1, int y2, int color) {
		if (y2 < y1) {
			int i = y1;
			y1 = y2;
			y2 = i;
		}

		this.fill(layer, x, y1 + 1, x + 1, y2, color);
	}

	public void enableScissor(int x1, int y1, int x2, int y2) {
		this.setScissor(this.scissorStack.push(new ScreenRect(x1, y1, x2 - x1, y2 - y1)));
	}

	public void disableScissor() {
		this.setScissor(this.scissorStack.pop());
	}

	private void setScissor(@Nullable ScreenRect rect) {
		this.drawIfRunning();
		if (rect != null) {
			Window window = MinecraftClient.getInstance().getWindow();
			int i = window.getFramebufferHeight();
			double d = window.getScaleFactor();
			double e = (double)rect.getLeft() * d;
			double f = (double)i - (double)rect.getBottom() * d;
			double g = (double)rect.width() * d;
			double h = (double)rect.height() * d;
			RenderSystem.enableScissor((int)e, (int)f, Math.max(0, (int)g), Math.max(0, (int)h));
		} else {
			RenderSystem.disableScissor();
		}
	}

	public void setShaderColor(float red, float green, float blue, float alpha) {
		this.drawIfRunning();
		RenderSystem.setShaderColor(red, green, blue, alpha);
	}

	public void fill(int x1, int y1, int x2, int y2, int color) {
		this.fill(x1, y1, x2, y2, 0, color);
	}

	public void fill(int x1, int y1, int x2, int y2, int z, int color) {
		this.fill(RenderLayer.getGui(), x1, y1, x2, y2, z, color);
	}

	public void fill(RenderLayer layer, int x1, int y1, int x2, int y2, int color) {
		this.fill(layer, x1, y1, x2, y2, 0, color);
	}

	public void fill(RenderLayer layer, int x1, int y1, int x2, int y2, int z, int color) {
		Matrix4f matrix4f = this.matrices.peek().getPositionMatrix();
		if (x1 < x2) {
			int i = x1;
			x1 = x2;
			x2 = i;
		}

		if (y1 < y2) {
			int i = y1;
			y1 = y2;
			y2 = i;
		}

		float f = (float)ColorHelper.Argb.getAlpha(color) / 255.0F;
		float g = (float)ColorHelper.Argb.getRed(color) / 255.0F;
		float h = (float)ColorHelper.Argb.getGreen(color) / 255.0F;
		float j = (float)ColorHelper.Argb.getBlue(color) / 255.0F;
		VertexConsumer vertexConsumer = this.vertexConsumers.getBuffer(layer);
		vertexConsumer.vertex(matrix4f, (float)x1, (float)y1, (float)z).color(g, h, j, f).next();
		vertexConsumer.vertex(matrix4f, (float)x1, (float)y2, (float)z).color(g, h, j, f).next();
		vertexConsumer.vertex(matrix4f, (float)x2, (float)y2, (float)z).color(g, h, j, f).next();
		vertexConsumer.vertex(matrix4f, (float)x2, (float)y1, (float)z).color(g, h, j, f).next();
		this.tryDraw();
	}

	public void fillGradient(int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
		this.fillGradient(startX, startY, endX, endY, 0, colorStart, colorEnd);
	}

	public void fillGradient(int startX, int startY, int endX, int endY, int z, int colorStart, int colorEnd) {
		this.fillGradient(RenderLayer.getGui(), startX, startY, endX, endY, colorStart, colorEnd, z);
	}

	public void fillGradient(RenderLayer layer, int startX, int startY, int endX, int endY, int colorStart, int colorEnd, int z) {
		VertexConsumer vertexConsumer = this.vertexConsumers.getBuffer(layer);
		this.fillGradient(vertexConsumer, startX, startY, endX, endY, z, colorStart, colorEnd);
		this.tryDraw();
	}

	private void fillGradient(VertexConsumer vertexConsumer, int startX, int startY, int endX, int endY, int z, int colorStart, int colorEnd) {
		float f = (float)ColorHelper.Argb.getAlpha(colorStart) / 255.0F;
		float g = (float)ColorHelper.Argb.getRed(colorStart) / 255.0F;
		float h = (float)ColorHelper.Argb.getGreen(colorStart) / 255.0F;
		float i = (float)ColorHelper.Argb.getBlue(colorStart) / 255.0F;
		float j = (float)ColorHelper.Argb.getAlpha(colorEnd) / 255.0F;
		float k = (float)ColorHelper.Argb.getRed(colorEnd) / 255.0F;
		float l = (float)ColorHelper.Argb.getGreen(colorEnd) / 255.0F;
		float m = (float)ColorHelper.Argb.getBlue(colorEnd) / 255.0F;
		Matrix4f matrix4f = this.matrices.peek().getPositionMatrix();
		vertexConsumer.vertex(matrix4f, (float)startX, (float)startY, (float)z).color(g, h, i, f).next();
		vertexConsumer.vertex(matrix4f, (float)startX, (float)endY, (float)z).color(k, l, m, j).next();
		vertexConsumer.vertex(matrix4f, (float)endX, (float)endY, (float)z).color(k, l, m, j).next();
		vertexConsumer.vertex(matrix4f, (float)endX, (float)startY, (float)z).color(g, h, i, f).next();
	}

	public void drawCenteredTextWithShadow(TextRenderer textRenderer, String text, int centerX, int y, int color) {
		this.drawTextWithShadow(textRenderer, text, centerX - textRenderer.getWidth(text) / 2, y, color);
	}

	public void drawCenteredTextWithShadow(TextRenderer textRenderer, Text text, int centerX, int y, int color) {
		OrderedText orderedText = text.asOrderedText();
		this.drawTextWithShadow(textRenderer, orderedText, centerX - textRenderer.getWidth(orderedText) / 2, y, color);
	}

	public void drawCenteredTextWithShadow(TextRenderer textRenderer, OrderedText text, int centerX, int y, int color) {
		this.drawTextWithShadow(textRenderer, text, centerX - textRenderer.getWidth(text) / 2, y, color);
	}

	public int drawTextWithShadow(TextRenderer textRenderer, @Nullable String text, int x, int y, int color) {
		return this.drawText(textRenderer, text, x, y, color, true);
	}

	public int drawText(TextRenderer textRenderer, @Nullable String text, int x, int y, int color, boolean shadow) {
		if (text == null) {
			return 0;
		} else {
			int i = textRenderer.draw(
				text,
				(float)x,
				(float)y,
				color,
				shadow,
				this.matrices.peek().getPositionMatrix(),
				this.vertexConsumers,
				TextRenderer.TextLayerType.NORMAL,
				0,
				15728880,
				textRenderer.isRightToLeft()
			);
			this.tryDraw();
			return i;
		}
	}

	public int drawTextWithShadow(TextRenderer textRenderer, OrderedText text, int x, int y, int color) {
		return this.drawText(textRenderer, text, x, y, color, true);
	}

	public int drawText(TextRenderer textRenderer, OrderedText text, int x, int y, int color, boolean shadow) {
		int i = textRenderer.draw(
			text, (float)x, (float)y, color, shadow, this.matrices.peek().getPositionMatrix(), this.vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 15728880
		);
		this.tryDraw();
		return i;
	}

	public int drawTextWithShadow(TextRenderer textRenderer, Text text, int x, int y, int color) {
		return this.drawText(textRenderer, text, x, y, color, true);
	}

	public int drawText(TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow) {
		return this.drawText(textRenderer, text.asOrderedText(), x, y, color, shadow);
	}

	public void drawTextWrapped(TextRenderer textRenderer, StringVisitable text, int x, int y, int width, int color) {
		for (OrderedText orderedText : textRenderer.wrapLines(text, width)) {
			this.drawText(textRenderer, orderedText, x, y, color, false);
			y += 9;
		}
	}

	public void drawSprite(int x, int y, int z, int width, int height, Sprite sprite) {
		this.drawSprite(sprite, x, y, z, width, height);
	}

	public void drawSprite(int x, int y, int z, int width, int height, Sprite sprite, float red, float green, float blue, float alpha) {
		this.drawTexturedQuad(
			sprite.getAtlasId(), x, x + width, y, y + height, z, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV(), red, green, blue, alpha
		);
	}

	public void drawBorder(int x, int y, int width, int height, int color) {
		this.fill(x, y, x + width, y + 1, color);
		this.fill(x, y + height - 1, x + width, y + height, color);
		this.fill(x, y + 1, x + 1, y + height - 1, color);
		this.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
	}

	public void drawGuiTexture(Identifier texture, int x, int y, int width, int height) {
		this.drawGuiTexture(texture, x, y, 0, width, height);
	}

	public void drawGuiTexture(Identifier texture, int x, int y, int z, int width, int height) {
		Sprite sprite = this.guiAtlasManager.getSprite(texture);
		Scaling scaling = this.guiAtlasManager.getScaling(sprite);
		if (scaling instanceof Scaling.Stretch) {
			this.drawSprite(sprite, x, y, z, width, height);
		} else if (scaling instanceof Scaling.Tile tile) {
			this.drawSpriteTiled(sprite, x, y, z, width, height, 0, 0, tile.width(), tile.height(), tile.width(), tile.height());
		} else if (scaling instanceof Scaling.NineSlice nineSlice) {
			this.drawSprite(sprite, nineSlice, x, y, z, width, height);
		}
	}

	public void drawGuiTexture(Identifier texture, int i, int j, int k, int l, int x, int y, int width, int height) {
		this.drawGuiTexture(texture, i, j, k, l, x, y, 0, width, height);
	}

	public void drawGuiTexture(Identifier texture, int i, int j, int k, int l, int x, int y, int z, int width, int height) {
		Sprite sprite = this.guiAtlasManager.getSprite(texture);
		Scaling scaling = this.guiAtlasManager.getScaling(sprite);
		if (scaling instanceof Scaling.Stretch) {
			this.drawSprite(sprite, i, j, k, l, x, y, z, width, height);
		} else {
			this.drawSprite(sprite, x, y, z, width, height);
		}
	}

	private void drawSprite(Sprite sprite, int i, int j, int k, int l, int x, int y, int z, int width, int height) {
		if (width != 0 && height != 0) {
			this.drawTexturedQuad(
				sprite.getAtlasId(),
				x,
				x + width,
				y,
				y + height,
				z,
				sprite.getFrameU((float)k / (float)i),
				sprite.getFrameU((float)(k + width) / (float)i),
				sprite.getFrameV((float)l / (float)j),
				sprite.getFrameV((float)(l + height) / (float)j)
			);
		}
	}

	private void drawSprite(Sprite sprite, int x, int y, int z, int width, int height) {
		if (width != 0 && height != 0) {
			this.drawTexturedQuad(sprite.getAtlasId(), x, x + width, y, y + height, z, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV());
		}
	}

	/**
	 * Draws a textured rectangle from a region in a 256x256 texture.
	 * 
	 * <p>The Z coordinate of the rectangle is {@code 0}.
	 * 
	 * <p>The width and height of the region are the same as
	 * the dimensions of the rectangle.
	 */
	public void drawTexture(Identifier texture, int x, int y, int u, int v, int width, int height) {
		this.drawTexture(texture, x, y, 0, (float)u, (float)v, width, height, 256, 256);
	}

	/**
	 * Draws a textured rectangle from a region in a texture.
	 * 
	 * <p>The width and height of the region are the same as
	 * the dimensions of the rectangle.
	 */
	public void drawTexture(Identifier texture, int x, int y, int z, float u, float v, int width, int height, int textureWidth, int textureHeight) {
		this.drawTexture(texture, x, x + width, y, y + height, z, width, height, u, v, textureWidth, textureHeight);
	}

	/**
	 * Draws a textured rectangle from a region in a texture.
	 */
	public void drawTexture(
		Identifier texture, int x, int y, int width, int height, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight
	) {
		this.drawTexture(texture, x, x + width, y, y + height, 0, regionWidth, regionHeight, u, v, textureWidth, textureHeight);
	}

	/**
	 * Draws a textured rectangle from a region in a texture.
	 * 
	 * <p>The width and height of the region are the same as
	 * the dimensions of the rectangle.
	 */
	public void drawTexture(Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
		this.drawTexture(texture, x, y, width, height, u, v, width, height, textureWidth, textureHeight);
	}

	void drawTexture(
		Identifier texture, int x1, int x2, int y1, int y2, int z, int regionWidth, int regionHeight, float u, float v, int textureWidth, int textureHeight
	) {
		this.drawTexturedQuad(
			texture,
			x1,
			x2,
			y1,
			y2,
			z,
			(u + 0.0F) / (float)textureWidth,
			(u + (float)regionWidth) / (float)textureWidth,
			(v + 0.0F) / (float)textureHeight,
			(v + (float)regionHeight) / (float)textureHeight
		);
	}

	void drawTexturedQuad(Identifier texture, int x1, int x2, int y1, int y2, int z, float u1, float u2, float v1, float v2) {
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		Matrix4f matrix4f = this.matrices.peek().getPositionMatrix();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(matrix4f, (float)x1, (float)y1, (float)z).texture(u1, v1).next();
		bufferBuilder.vertex(matrix4f, (float)x1, (float)y2, (float)z).texture(u1, v2).next();
		bufferBuilder.vertex(matrix4f, (float)x2, (float)y2, (float)z).texture(u2, v2).next();
		bufferBuilder.vertex(matrix4f, (float)x2, (float)y1, (float)z).texture(u2, v1).next();
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
	}

	void drawTexturedQuad(
		Identifier texture, int x1, int x2, int y1, int y2, int z, float u1, float u2, float v1, float v2, float red, float green, float blue, float alpha
	) {
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
		RenderSystem.enableBlend();
		Matrix4f matrix4f = this.matrices.peek().getPositionMatrix();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
		bufferBuilder.vertex(matrix4f, (float)x1, (float)y1, (float)z).color(red, green, blue, alpha).texture(u1, v1).next();
		bufferBuilder.vertex(matrix4f, (float)x1, (float)y2, (float)z).color(red, green, blue, alpha).texture(u1, v2).next();
		bufferBuilder.vertex(matrix4f, (float)x2, (float)y2, (float)z).color(red, green, blue, alpha).texture(u2, v2).next();
		bufferBuilder.vertex(matrix4f, (float)x2, (float)y1, (float)z).color(red, green, blue, alpha).texture(u2, v1).next();
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		RenderSystem.disableBlend();
	}

	private void drawSprite(Sprite sprite, Scaling.NineSlice nineSlice, int x, int y, int z, int width, int height) {
		Scaling.NineSlice.Border border = nineSlice.border();
		int i = Math.min(border.left(), width / 2);
		int j = Math.min(border.right(), width / 2);
		int k = Math.min(border.top(), height / 2);
		int l = Math.min(border.bottom(), height / 2);
		if (width == nineSlice.width() && height == nineSlice.height()) {
			this.drawSprite(sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, z, width, height);
		} else if (height == nineSlice.height()) {
			this.drawSprite(sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, z, i, height);
			this.drawSpriteTiled(sprite, x + i, y, z, width - j - i, height, i, 0, nineSlice.width() - j - i, nineSlice.height(), nineSlice.width(), nineSlice.height());
			this.drawSprite(sprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - j, 0, x + width - j, y, z, j, height);
		} else if (width == nineSlice.width()) {
			this.drawSprite(sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, z, width, k);
			this.drawSpriteTiled(sprite, x, y + k, z, width, height - l - k, 0, k, nineSlice.width(), nineSlice.height() - l - k, nineSlice.width(), nineSlice.height());
			this.drawSprite(sprite, nineSlice.width(), nineSlice.height(), 0, nineSlice.height() - l, x, y + height - l, z, width, l);
		} else {
			this.drawSprite(sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, z, i, k);
			this.drawSpriteTiled(sprite, x + i, y, z, width - j - i, k, i, 0, nineSlice.width() - j - i, k, nineSlice.width(), nineSlice.height());
			this.drawSprite(sprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - j, 0, x + width - j, y, z, j, k);
			this.drawSprite(sprite, nineSlice.width(), nineSlice.height(), 0, nineSlice.height() - l, x, y + height - l, z, i, l);
			this.drawSpriteTiled(
				sprite, x + i, y + height - l, z, width - j - i, l, i, nineSlice.height() - l, nineSlice.width() - j - i, l, nineSlice.width(), nineSlice.height()
			);
			this.drawSprite(sprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - j, nineSlice.height() - l, x + width - j, y + height - l, z, j, l);
			this.drawSpriteTiled(sprite, x, y + k, z, i, height - l - k, 0, k, i, nineSlice.height() - l - k, nineSlice.width(), nineSlice.height());
			this.drawSpriteTiled(
				sprite, x + i, y + k, z, width - j - i, height - l - k, i, k, nineSlice.width() - j - i, nineSlice.height() - l - k, nineSlice.width(), nineSlice.height()
			);
			this.drawSpriteTiled(
				sprite, x + width - j, y + k, z, i, height - l - k, nineSlice.width() - j, k, j, nineSlice.height() - l - k, nineSlice.width(), nineSlice.height()
			);
		}
	}

	private void drawSpriteTiled(Sprite sprite, int x, int y, int z, int width, int height, int i, int j, int tileWidth, int tileHeight, int k, int l) {
		if (width > 0 && height > 0) {
			if (tileWidth > 0 && tileHeight > 0) {
				for (int m = 0; m < width; m += tileWidth) {
					int n = Math.min(tileWidth, width - m);

					for (int o = 0; o < height; o += tileHeight) {
						int p = Math.min(tileHeight, height - o);
						this.drawSprite(sprite, k, l, i, j, x + m, y + o, z, n, p);
					}
				}
			} else {
				throw new IllegalArgumentException("Tiled sprite texture size must be positive, got " + tileWidth + "x" + tileHeight);
			}
		}
	}

	public void drawItem(ItemStack item, int x, int y) {
		this.drawItem(this.client.player, this.client.world, item, x, y, 0);
	}

	public void drawItem(ItemStack stack, int x, int y, int seed) {
		this.drawItem(this.client.player, this.client.world, stack, x, y, seed);
	}

	public void drawItem(ItemStack stack, int x, int y, int seed, int z) {
		this.drawItem(this.client.player, this.client.world, stack, x, y, seed, z);
	}

	public void drawItemWithoutEntity(ItemStack stack, int x, int y) {
		this.drawItemWithoutEntity(stack, x, y, 0);
	}

	public void drawItemWithoutEntity(ItemStack stack, int x, int y, int seed) {
		this.drawItem(null, this.client.world, stack, x, y, seed);
	}

	public void drawItem(LivingEntity entity, ItemStack stack, int x, int y, int seed) {
		this.drawItem(entity, entity.getWorld(), stack, x, y, seed);
	}

	private void drawItem(@Nullable LivingEntity entity, @Nullable World world, ItemStack stack, int x, int y, int seed) {
		this.drawItem(entity, world, stack, x, y, seed, 0);
	}

	private void drawItem(@Nullable LivingEntity entity, @Nullable World world, ItemStack stack, int x, int y, int seed, int z) {
		if (!stack.isEmpty()) {
			BakedModel bakedModel = this.client.getItemRenderer().getModel(stack, world, entity, seed);
			this.matrices.push();
			this.matrices.translate((float)(x + 8), (float)(y + 8), (float)(150 + (bakedModel.hasDepth() ? z : 0)));

			try {
				this.matrices.scale(16.0F, -16.0F, 16.0F);
				boolean bl = !bakedModel.isSideLit();
				if (bl) {
					DiffuseLighting.disableGuiDepthLighting();
				}

				this.client
					.getItemRenderer()
					.renderItem(stack, ModelTransformationMode.GUI, false, this.matrices, this.getVertexConsumers(), 15728880, OverlayTexture.DEFAULT_UV, bakedModel);
				this.draw();
				if (bl) {
					DiffuseLighting.enableGuiDepthLighting();
				}
			} catch (Throwable var12) {
				CrashReport crashReport = CrashReport.create(var12, "Rendering item");
				CrashReportSection crashReportSection = crashReport.addElement("Item being rendered");
				crashReportSection.add("Item Type", (CrashCallable<String>)(() -> String.valueOf(stack.getItem())));
				crashReportSection.add("Item Damage", (CrashCallable<String>)(() -> String.valueOf(stack.getDamage())));
				crashReportSection.add("Item NBT", (CrashCallable<String>)(() -> String.valueOf(stack.getNbt())));
				crashReportSection.add("Item Foil", (CrashCallable<String>)(() -> String.valueOf(stack.hasGlint())));
				throw new CrashException(crashReport);
			}

			this.matrices.pop();
		}
	}

	public void drawItemInSlot(TextRenderer textRenderer, ItemStack stack, int x, int y) {
		this.drawItemInSlot(textRenderer, stack, x, y, null);
	}

	public void drawItemInSlot(TextRenderer textRenderer, ItemStack stack, int x, int y, @Nullable String countOverride) {
		if (!stack.isEmpty()) {
			this.matrices.push();
			if (stack.getCount() != 1 || countOverride != null) {
				String string = countOverride == null ? String.valueOf(stack.getCount()) : countOverride;
				this.matrices.translate(0.0F, 0.0F, 200.0F);
				this.drawText(textRenderer, string, x + 19 - 2 - textRenderer.getWidth(string), y + 6 + 3, 16777215, true);
			}

			if (stack.isItemBarVisible()) {
				int i = stack.getItemBarStep();
				int j = stack.getItemBarColor();
				int k = x + 2;
				int l = y + 13;
				this.fill(RenderLayer.getGuiOverlay(), k, l, k + 13, l + 2, Colors.BLACK);
				this.fill(RenderLayer.getGuiOverlay(), k, l, k + i, l + 1, j | Colors.BLACK);
			}

			ClientPlayerEntity clientPlayerEntity = this.client.player;
			float f = clientPlayerEntity == null ? 0.0F : clientPlayerEntity.getItemCooldownManager().getCooldownProgress(stack.getItem(), this.client.getTickDelta());
			if (f > 0.0F) {
				int k = y + MathHelper.floor(16.0F * (1.0F - f));
				int l = k + MathHelper.ceil(16.0F * f);
				this.fill(RenderLayer.getGuiOverlay(), x, k, x + 16, l, Integer.MAX_VALUE);
			}

			this.matrices.pop();
		}
	}

	public void drawItemTooltip(TextRenderer textRenderer, ItemStack stack, int x, int y) {
		this.drawTooltip(textRenderer, Screen.getTooltipFromItem(this.client, stack), stack.getTooltipData(), x, y);
	}

	public void drawTooltip(TextRenderer textRenderer, List<Text> text, Optional<TooltipData> data, int x, int y) {
		List<TooltipComponent> list = (List<TooltipComponent>)text.stream().map(Text::asOrderedText).map(TooltipComponent::of).collect(Collectors.toList());
		data.ifPresent(datax -> list.add(1, TooltipComponent.of(datax)));
		this.drawTooltip(textRenderer, list, x, y, HoveredTooltipPositioner.INSTANCE);
	}

	public void drawTooltip(TextRenderer textRenderer, Text text, int x, int y) {
		this.drawOrderedTooltip(textRenderer, List.of(text.asOrderedText()), x, y);
	}

	public void drawTooltip(TextRenderer textRenderer, List<Text> text, int x, int y) {
		this.drawOrderedTooltip(textRenderer, Lists.transform(text, Text::asOrderedText), x, y);
	}

	public void drawOrderedTooltip(TextRenderer textRenderer, List<? extends OrderedText> text, int x, int y) {
		this.drawTooltip(
			textRenderer, (List<TooltipComponent>)text.stream().map(TooltipComponent::of).collect(Collectors.toList()), x, y, HoveredTooltipPositioner.INSTANCE
		);
	}

	public void drawTooltip(TextRenderer textRenderer, List<OrderedText> text, TooltipPositioner positioner, int x, int y) {
		this.drawTooltip(textRenderer, (List<TooltipComponent>)text.stream().map(TooltipComponent::of).collect(Collectors.toList()), x, y, positioner);
	}

	private void drawTooltip(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner) {
		if (!components.isEmpty()) {
			int i = 0;
			int j = components.size() == 1 ? -2 : 0;

			for (TooltipComponent tooltipComponent : components) {
				int k = tooltipComponent.getWidth(textRenderer);
				if (k > i) {
					i = k;
				}

				j += tooltipComponent.getHeight();
			}

			int l = i;
			int m = j;
			Vector2ic vector2ic = positioner.getPosition(this.getScaledWindowWidth(), this.getScaledWindowHeight(), x, y, l, m);
			int n = vector2ic.x();
			int o = vector2ic.y();
			this.matrices.push();
			int p = 400;
			this.draw(() -> TooltipBackgroundRenderer.render(this, n, o, l, m, 400));
			this.matrices.translate(0.0F, 0.0F, 400.0F);
			int q = o;

			for (int r = 0; r < components.size(); r++) {
				TooltipComponent tooltipComponent2 = (TooltipComponent)components.get(r);
				tooltipComponent2.drawText(textRenderer, n, q, this.matrices.peek().getPositionMatrix(), this.vertexConsumers);
				q += tooltipComponent2.getHeight() + (r == 0 ? 2 : 0);
			}

			q = o;

			for (int r = 0; r < components.size(); r++) {
				TooltipComponent tooltipComponent2 = (TooltipComponent)components.get(r);
				tooltipComponent2.drawItems(textRenderer, n, q, this);
				q += tooltipComponent2.getHeight() + (r == 0 ? 2 : 0);
			}

			this.matrices.pop();
		}
	}

	public void drawHoverEvent(TextRenderer textRenderer, @Nullable Style style, int x, int y) {
		if (style != null && style.getHoverEvent() != null) {
			HoverEvent hoverEvent = style.getHoverEvent();
			HoverEvent.ItemStackContent itemStackContent = hoverEvent.getValue(HoverEvent.Action.SHOW_ITEM);
			if (itemStackContent != null) {
				this.drawItemTooltip(textRenderer, itemStackContent.asStack(), x, y);
			} else {
				HoverEvent.EntityContent entityContent = hoverEvent.getValue(HoverEvent.Action.SHOW_ENTITY);
				if (entityContent != null) {
					if (this.client.options.advancedItemTooltips) {
						this.drawTooltip(textRenderer, entityContent.asTooltip(), x, y);
					}
				} else {
					Text text = hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT);
					if (text != null) {
						this.drawOrderedTooltip(textRenderer, textRenderer.wrapLines(text, Math.max(this.getScaledWindowWidth() / 2, 200)), x, y);
					}
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class ScissorStack {
		private final Deque<ScreenRect> stack = new ArrayDeque();

		public ScreenRect push(ScreenRect rect) {
			ScreenRect screenRect = (ScreenRect)this.stack.peekLast();
			if (screenRect != null) {
				ScreenRect screenRect2 = (ScreenRect)Objects.requireNonNullElse(rect.intersection(screenRect), ScreenRect.empty());
				this.stack.addLast(screenRect2);
				return screenRect2;
			} else {
				this.stack.addLast(rect);
				return rect;
			}
		}

		@Nullable
		public ScreenRect pop() {
			if (this.stack.isEmpty()) {
				throw new IllegalStateException("Scissor stack underflow");
			} else {
				this.stack.removeLast();
				return (ScreenRect)this.stack.peekLast();
			}
		}
	}
}
