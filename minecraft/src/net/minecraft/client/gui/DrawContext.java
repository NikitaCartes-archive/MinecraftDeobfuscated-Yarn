package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
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
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.GuiAtlasManager;
import net.minecraft.client.texture.Scaling;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
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

	private DrawContext(MinecraftClient client, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers) {
		this.client = client;
		this.matrices = matrices;
		this.vertexConsumers = vertexConsumers;
		this.guiAtlasManager = client.getGuiAtlasManager();
	}

	public DrawContext(MinecraftClient client, VertexConsumerProvider.Immediate vertexConsumers) {
		this(client, new MatrixStack(), vertexConsumers);
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
		this.vertexConsumers.draw();
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

	public boolean scissorContains(int x, int y) {
		return this.scissorStack.contains(x, y);
	}

	private void setScissor(@Nullable ScreenRect rect) {
		this.draw();
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

		VertexConsumer vertexConsumer = this.vertexConsumers.getBuffer(layer);
		vertexConsumer.vertex(matrix4f, (float)x1, (float)y1, (float)z).color(color);
		vertexConsumer.vertex(matrix4f, (float)x1, (float)y2, (float)z).color(color);
		vertexConsumer.vertex(matrix4f, (float)x2, (float)y2, (float)z).color(color);
		vertexConsumer.vertex(matrix4f, (float)x2, (float)y1, (float)z).color(color);
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
	}

	private void fillGradient(VertexConsumer vertexConsumer, int startX, int startY, int endX, int endY, int z, int colorStart, int colorEnd) {
		Matrix4f matrix4f = this.matrices.peek().getPositionMatrix();
		vertexConsumer.vertex(matrix4f, (float)startX, (float)startY, (float)z).color(colorStart);
		vertexConsumer.vertex(matrix4f, (float)startX, (float)endY, (float)z).color(colorEnd);
		vertexConsumer.vertex(matrix4f, (float)endX, (float)endY, (float)z).color(colorEnd);
		vertexConsumer.vertex(matrix4f, (float)endX, (float)startY, (float)z).color(colorStart);
	}

	public void fillWithLayer(RenderLayer layer, int startX, int startY, int endX, int endY, int z) {
		Matrix4f matrix4f = this.matrices.peek().getPositionMatrix();
		VertexConsumer vertexConsumer = this.vertexConsumers.getBuffer(layer);
		vertexConsumer.vertex(matrix4f, (float)startX, (float)startY, (float)z);
		vertexConsumer.vertex(matrix4f, (float)startX, (float)endY, (float)z);
		vertexConsumer.vertex(matrix4f, (float)endX, (float)endY, (float)z);
		vertexConsumer.vertex(matrix4f, (float)endX, (float)startY, (float)z);
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
		return text == null
			? 0
			: textRenderer.draw(
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
	}

	public int drawTextWithShadow(TextRenderer textRenderer, OrderedText text, int x, int y, int color) {
		return this.drawText(textRenderer, text, x, y, color, true);
	}

	public int drawText(TextRenderer textRenderer, OrderedText text, int x, int y, int color, boolean shadow) {
		return textRenderer.draw(
			text, (float)x, (float)y, color, shadow, this.matrices.peek().getPositionMatrix(), this.vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 15728880
		);
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

	public int drawTextWithBackground(TextRenderer textRenderer, Text text, int x, int y, int width, int color) {
		int i = this.client.options.getTextBackgroundColor(0.0F);
		if (i != 0) {
			int j = 2;
			this.fill(x - 2, y - 2, x + width + 2, y + 9 + 2, ColorHelper.mix(i, color));
		}

		return this.drawText(textRenderer, text, x, y, color, true);
	}

	public void drawBorder(int x, int y, int width, int height, int color) {
		this.fill(x, y, x + width, y + 1, color);
		this.fill(x, y + height - 1, x + width, y + height, color);
		this.fill(x, y + 1, x + 1, y + height - 1, color);
		this.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
	}

	public void drawGuiTexture(Function<Identifier, RenderLayer> function, Identifier identifier, int i, int j, int height, int k) {
		this.drawGuiTexture(function, identifier, i, j, height, k, -1);
	}

	public void drawGuiTexture(Function<Identifier, RenderLayer> function, Identifier identifier, int i, int j, int k, int l, int m) {
		Sprite sprite = this.guiAtlasManager.getSprite(identifier);
		Scaling scaling = this.guiAtlasManager.getScaling(sprite);
		if (scaling instanceof Scaling.Stretch) {
			this.drawSprite(function, sprite, i, j, k, l, m);
		} else if (scaling instanceof Scaling.Tile tile) {
			this.drawSpriteTiled(function, sprite, i, j, k, l, 0, 0, tile.width(), tile.height(), tile.width(), tile.height(), m);
		} else if (scaling instanceof Scaling.NineSlice nineSlice) {
			this.drawSprite(function, sprite, nineSlice, i, j, k, l, m);
		}
	}

	public void drawGuiTexture(Function<Identifier, RenderLayer> function, Identifier identifier, int i, int j, int k, int l, int m, int n, int o, int p) {
		Sprite sprite = this.guiAtlasManager.getSprite(identifier);
		Scaling scaling = this.guiAtlasManager.getScaling(sprite);
		if (scaling instanceof Scaling.Stretch) {
			this.drawSprite(function, sprite, i, j, k, l, m, n, o, p, -1);
		} else {
			this.drawGuiTexture(function, sprite, m, n, o, p);
		}
	}

	public void drawGuiTexture(Function<Identifier, RenderLayer> function, Sprite sprite, int i, int j, int k, int l) {
		this.drawSprite(function, sprite, i, j, k, l, -1);
	}

	public void drawSprite(Function<Identifier, RenderLayer> function, Sprite sprite, int y, int z, int width, int height, int i) {
		if (width != 0 && height != 0) {
			this.drawTexturedQuad(function, sprite.getAtlasId(), y, y + width, z, z + height, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV(), i);
		}
	}

	private void drawSprite(Function<Identifier, RenderLayer> function, Sprite sprite, int i, int j, int k, int x, int y, int z, int width, int height, int l) {
		if (width != 0 && height != 0) {
			this.drawTexturedQuad(
				function,
				sprite.getAtlasId(),
				y,
				y + width,
				z,
				z + height,
				sprite.getFrameU((float)k / (float)i),
				sprite.getFrameU((float)(k + width) / (float)i),
				sprite.getFrameV((float)x / (float)j),
				sprite.getFrameV((float)(x + height) / (float)j),
				l
			);
		}
	}

	private void drawSprite(Function<Identifier, RenderLayer> function, Sprite sprite, Scaling.NineSlice nineSlice, int y, int z, int width, int height, int i) {
		Scaling.NineSlice.Border border = nineSlice.border();
		int j = Math.min(border.left(), width / 2);
		int k = Math.min(border.right(), width / 2);
		int l = Math.min(border.top(), height / 2);
		int m = Math.min(border.bottom(), height / 2);
		if (width == nineSlice.width() && height == nineSlice.height()) {
			this.drawSprite(function, sprite, nineSlice.width(), nineSlice.height(), 0, 0, y, z, width, height, i);
		} else if (height == nineSlice.height()) {
			this.drawSprite(function, sprite, nineSlice.width(), nineSlice.height(), 0, 0, y, z, j, height, i);
			this.drawSpriteTiled(
				function, sprite, y + j, z, width - k - j, height, j, 0, nineSlice.width() - k - j, nineSlice.height(), nineSlice.width(), nineSlice.height(), i
			);
			this.drawSprite(function, sprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - k, 0, y + width - k, z, k, height, i);
		} else if (width == nineSlice.width()) {
			this.drawSprite(function, sprite, nineSlice.width(), nineSlice.height(), 0, 0, y, z, width, l, i);
			this.drawSpriteTiled(
				function, sprite, y, z + l, width, height - m - l, 0, l, nineSlice.width(), nineSlice.height() - m - l, nineSlice.width(), nineSlice.height(), i
			);
			this.drawSprite(function, sprite, nineSlice.width(), nineSlice.height(), 0, nineSlice.height() - m, y, z + height - m, width, m, i);
		} else {
			this.drawSprite(function, sprite, nineSlice.width(), nineSlice.height(), 0, 0, y, z, j, l, i);
			this.drawSpriteTiled(function, sprite, y + j, z, width - k - j, l, j, 0, nineSlice.width() - k - j, l, nineSlice.width(), nineSlice.height(), i);
			this.drawSprite(function, sprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - k, 0, y + width - k, z, k, l, i);
			this.drawSprite(function, sprite, nineSlice.width(), nineSlice.height(), 0, nineSlice.height() - m, y, z + height - m, j, m, i);
			this.drawSpriteTiled(
				function,
				sprite,
				y + j,
				z + height - m,
				width - k - j,
				m,
				j,
				nineSlice.height() - m,
				nineSlice.width() - k - j,
				m,
				nineSlice.width(),
				nineSlice.height(),
				i
			);
			this.drawSprite(
				function, sprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - k, nineSlice.height() - m, y + width - k, z + height - m, k, m, i
			);
			this.drawSpriteTiled(function, sprite, y, z + l, j, height - m - l, 0, l, j, nineSlice.height() - m - l, nineSlice.width(), nineSlice.height(), i);
			this.drawSpriteTiled(
				function,
				sprite,
				y + j,
				z + l,
				width - k - j,
				height - m - l,
				j,
				l,
				nineSlice.width() - k - j,
				nineSlice.height() - m - l,
				nineSlice.width(),
				nineSlice.height(),
				i
			);
			this.drawSpriteTiled(
				function,
				sprite,
				y + width - k,
				z + l,
				j,
				height - m - l,
				nineSlice.width() - k,
				l,
				k,
				nineSlice.height() - m - l,
				nineSlice.width(),
				nineSlice.height(),
				i
			);
		}
	}

	private void drawSpriteTiled(
		Function<Identifier, RenderLayer> function,
		Sprite sprite,
		int y,
		int z,
		int width,
		int height,
		int i,
		int j,
		int tileWidth,
		int tileHeight,
		int k,
		int l,
		int m
	) {
		if (width > 0 && height > 0) {
			if (tileWidth > 0 && tileHeight > 0) {
				for (int n = 0; n < width; n += tileWidth) {
					int o = Math.min(tileWidth, width - n);

					for (int p = 0; p < height; p += tileHeight) {
						int q = Math.min(tileHeight, height - p);
						this.drawSprite(function, sprite, k, l, i, j, y + n, z + p, o, q, m);
					}
				}
			} else {
				throw new IllegalArgumentException("Tiled sprite texture size must be positive, got " + tileWidth + "x" + tileHeight);
			}
		}
	}

	/**
	 * Draws a textured rectangle from a region in a texture.
	 * 
	 * <p>The width and height of the region are the same as
	 * the dimensions of the rectangle.
	 */
	public void drawTexture(
		Function<Identifier, RenderLayer> function, Identifier identifier, int i, int j, float u, float v, int k, int l, int m, int textureHeight, int n
	) {
		this.drawTexture(function, identifier, i, j, u, v, k, l, k, l, m, textureHeight, n);
	}

	/**
	 * Draws a textured rectangle from a region in a texture.
	 * 
	 * <p>The width and height of the region are the same as
	 * the dimensions of the rectangle.
	 */
	public void drawTexture(Function<Identifier, RenderLayer> function, Identifier identifier, int i, int j, float f, float g, int k, int l, int m, int n) {
		this.drawTexture(function, identifier, i, j, f, g, k, l, k, l, m, n);
	}

	/**
	 * Draws a textured rectangle from a region in a 256x256 texture.
	 * 
	 * <p>The Z coordinate of the rectangle is {@code 0}.
	 * 
	 * <p>The width and height of the region are the same as
	 * the dimensions of the rectangle.
	 */
	public void drawTexture(
		Function<Identifier, RenderLayer> function, Identifier identifier, int y, int u, float f, float g, int i, int j, int k, int l, int m, int n
	) {
		this.drawTexture(function, identifier, y, u, f, g, i, j, k, l, m, n, -1);
	}

	/**
	 * Draws a textured rectangle from a region in a texture.
	 */
	public void drawTexture(
		Function<Identifier, RenderLayer> function,
		Identifier identifier,
		int i,
		int j,
		float f,
		float u,
		int k,
		int l,
		int regionHeight,
		int textureWidth,
		int m,
		int n,
		int o
	) {
		this.drawTexturedQuad(
			function,
			identifier,
			i,
			i + k,
			j,
			j + l,
			(f + 0.0F) / (float)m,
			(f + (float)regionHeight) / (float)m,
			(u + 0.0F) / (float)n,
			(u + (float)textureWidth) / (float)n,
			o
		);
	}

	private void drawTexturedQuad(
		Function<Identifier, RenderLayer> function, Identifier identifier, int x2, int y1, int y2, int z, float u1, float u2, float v1, float v2, int i
	) {
		RenderLayer renderLayer = (RenderLayer)function.apply(identifier);
		Matrix4f matrix4f = this.matrices.peek().getPositionMatrix();
		VertexConsumer vertexConsumer = this.vertexConsumers.getBuffer(renderLayer);
		vertexConsumer.vertex(matrix4f, (float)x2, (float)y2, 0.0F).texture(u1, v1).color(i);
		vertexConsumer.vertex(matrix4f, (float)x2, (float)z, 0.0F).texture(u1, v2).color(i);
		vertexConsumer.vertex(matrix4f, (float)y1, (float)z, 0.0F).texture(u2, v2).color(i);
		vertexConsumer.vertex(matrix4f, (float)y1, (float)y2, 0.0F).texture(u2, v1).color(i);
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
					this.draw();
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
				crashReportSection.add("Item Components", (CrashCallable<String>)(() -> String.valueOf(stack.getComponents())));
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
				this.fill(RenderLayer.getGuiOverlay(), k, l, k + i, l + 1, ColorHelper.fullAlpha(j));
			}

			ClientPlayerEntity clientPlayerEntity = this.client.player;
			float f = clientPlayerEntity == null
				? 0.0F
				: clientPlayerEntity.getItemCooldownManager().getCooldownProgress(stack, this.client.getRenderTickCounter().getTickDelta(true));
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
		List<TooltipComponent> list = (List<TooltipComponent>)text.stream().map(Text::asOrderedText).map(TooltipComponent::of).collect(Util.toArrayList());
		data.ifPresent(datax -> list.add(list.isEmpty() ? 0 : 1, TooltipComponent.of(datax)));
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

				j += tooltipComponent.getHeight(textRenderer);
			}

			Vector2ic vector2ic = positioner.getPosition(this.getScaledWindowWidth(), this.getScaledWindowHeight(), x, y, i, j);
			int n = vector2ic.x();
			int o = vector2ic.y();
			this.matrices.push();
			int p = 400;
			TooltipBackgroundRenderer.render(this, n, o, i, j, 400);
			this.matrices.translate(0.0F, 0.0F, 400.0F);
			int q = o;

			for (int r = 0; r < components.size(); r++) {
				TooltipComponent tooltipComponent2 = (TooltipComponent)components.get(r);
				tooltipComponent2.drawText(textRenderer, n, q, this.matrices.peek().getPositionMatrix(), this.vertexConsumers);
				q += tooltipComponent2.getHeight(textRenderer) + (r == 0 ? 2 : 0);
			}

			q = o;

			for (int r = 0; r < components.size(); r++) {
				TooltipComponent tooltipComponent2 = (TooltipComponent)components.get(r);
				tooltipComponent2.drawItems(textRenderer, n, q, this);
				q += tooltipComponent2.getHeight(textRenderer) + (r == 0 ? 2 : 0);
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

		public boolean contains(int x, int y) {
			return this.stack.isEmpty() ? true : ((ScreenRect)this.stack.peek()).contains(x, y);
		}
	}
}
