package net.minecraft.client.gui.tooltip;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.item.tooltip.BundleTooltipData;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.text.OrderedText;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public interface TooltipComponent {
	static TooltipComponent of(OrderedText text) {
		return new OrderedTextTooltipComponent(text);
	}

	static TooltipComponent of(TooltipData tooltipData) {
		Objects.requireNonNull(tooltipData);

		return (TooltipComponent)(switch (tooltipData) {
			case BundleTooltipData bundleTooltipData -> new BundleTooltipComponent(bundleTooltipData.contents());
			case ProfilesTooltipComponent.ProfilesData profilesData -> new ProfilesTooltipComponent(profilesData);
			default -> throw new IllegalArgumentException("Unknown TooltipComponent");
		});
	}

	int getHeight(TextRenderer textRenderer);

	int getWidth(TextRenderer textRenderer);

	/**
	 * Returns whether this tooltip component should be visible when the item that
	 * it is on is focused, regardless of whether the cursor is hovering over
	 * another item.
	 */
	default boolean isSticky() {
		return false;
	}

	default void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
	}

	default void drawItems(TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context) {
	}
}
