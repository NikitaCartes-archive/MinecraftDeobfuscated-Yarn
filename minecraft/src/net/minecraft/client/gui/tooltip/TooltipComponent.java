package net.minecraft.client.gui.tooltip;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.Matrix4f;

@Environment(EnvType.CLIENT)
public interface TooltipComponent {
	static TooltipComponent createOrderedTextTooltipComponent(OrderedText orderedText) {
		return new OrderedTextTooltipComponent(orderedText);
	}

	static TooltipComponent createTooltipComponent(TooltipData tooltipData) {
		if (tooltipData instanceof BundleTooltipData) {
			return new BundleTooltipComponent((BundleTooltipData)tooltipData);
		} else {
			throw new IllegalArgumentException("Unknown TooltipComponent");
		}
	}

	int getHeight();

	int getWidth(TextRenderer textRenderer);

	default void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix4f, VertexConsumerProvider.Immediate immediate) {
	}

	default void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z, TextureManager textureManager) {
	}
}
