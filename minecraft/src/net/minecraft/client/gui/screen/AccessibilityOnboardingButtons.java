package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AccessibilityOnboardingButtons {
	public static TextIconButtonWidget createLanguageButton(int width, ButtonWidget.PressAction onPress, boolean hideText) {
		return TextIconButtonWidget.builder(Text.translatable("options.language"), onPress, hideText)
			.width(width)
			.texture(new Identifier("icon/language"), 15, 15)
			.build();
	}

	public static TextIconButtonWidget createAccessibilityButton(int width, ButtonWidget.PressAction onPress, boolean hideText) {
		return TextIconButtonWidget.builder(Text.translatable("options.accessibility"), onPress, hideText)
			.width(width)
			.texture(new Identifier("icon/accessibility"), 15, 15)
			.build();
	}
}
