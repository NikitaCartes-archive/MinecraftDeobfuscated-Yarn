package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.IconButtonWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class AccessibilityOnboardingButtons {
	public static IconButtonWidget createLanguageButton(MinecraftClient client, Screen screen) {
		return IconButtonWidget.builder(
				Text.translatable("options.language"),
				ButtonWidget.WIDGETS_TEXTURE,
				button -> client.setScreen(new LanguageOptionsScreen(screen, client.options, client.getLanguageManager()))
			)
			.uv(4, 110)
			.xyOffset(65, 3)
			.hoveredVOffset(20)
			.iconSize(13, 13)
			.textureSize(256, 256)
			.build();
	}

	public static IconButtonWidget createAccessibilityButton(MinecraftClient client, Screen screen) {
		return IconButtonWidget.builder(
				Text.translatable("options.accessibility.title"),
				ButtonWidget.ACCESSIBILITY_TEXTURE,
				button -> client.setScreen(new AccessibilityOptionsScreen(screen, client.options))
			)
			.uv(3, 3)
			.xyOffset(65, 3)
			.hoveredVOffset(20)
			.iconSize(15, 15)
			.textureSize(32, 64)
			.build();
	}
}
