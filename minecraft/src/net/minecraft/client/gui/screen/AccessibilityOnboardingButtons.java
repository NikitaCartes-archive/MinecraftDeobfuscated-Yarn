package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.IconButtonWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class AccessibilityOnboardingButtons {
	public static IconButtonWidget createLanguageButton(ButtonWidget.PressAction action) {
		return IconButtonWidget.builder(Text.translatable("options.language"), ButtonWidget.WIDGETS_TEXTURE, action)
			.uv(3, 109)
			.xyOffset(65, 3)
			.hoveredVOffset(20)
			.iconSize(14, 14)
			.textureSize(256, 256)
			.build();
	}

	public static IconButtonWidget createAccessibilityButton(ButtonWidget.PressAction action) {
		return IconButtonWidget.builder(Text.translatable("options.accessibility.title"), ButtonWidget.ACCESSIBILITY_TEXTURE, action)
			.uv(3, 2)
			.xyOffset(65, 2)
			.hoveredVOffset(20)
			.iconSize(14, 16)
			.textureSize(32, 64)
			.build();
	}
}
