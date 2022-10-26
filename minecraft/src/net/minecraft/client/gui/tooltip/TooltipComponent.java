package net.minecraft.client.gui.tooltip;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public interface TooltipComponent {
	static TooltipComponent of(OrderedText text) {
		return new OrderedTextTooltipComponent(text);
	}

	static TooltipComponent of(TooltipData data) {
		if (data instanceof BundleTooltipData) {
			return new BundleTooltipComponent((BundleTooltipData)data);
		} else {
			throw new IllegalArgumentException("Unknown TooltipComponent");
		}
	}

	int getHeight();

	int getWidth(TextRenderer textRenderer);

	default void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
	}

	default void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z) {
	}
}
