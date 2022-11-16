package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class class_7940 extends ClickableWidget {
	private final MultilineText field_41341;
	private final int field_41342;
	private final boolean field_41343;

	private class_7940(MultilineText multilineText, TextRenderer textRenderer, Text text, boolean bl) {
		super(0, 0, multilineText.getMaxWidth(), multilineText.count() * 9, text);
		this.field_41341 = multilineText;
		this.field_41342 = 9;
		this.field_41343 = bl;
		this.active = false;
	}

	public static class_7940 method_47617(int i, TextRenderer textRenderer, Text text) {
		MultilineText multilineText = MultilineText.create(textRenderer, text, i);
		return new class_7940(multilineText, textRenderer, text, true);
	}

	public static class_7940 method_47618(int i, TextRenderer textRenderer, Text text) {
		MultilineText multilineText = MultilineText.create(textRenderer, text, i);
		return new class_7940(multilineText, textRenderer, text, false);
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (this.field_41343) {
			this.field_41341.drawCenterWithShadow(matrices, this.getX() + this.getWidth() / 2, this.getY(), this.field_41342, 16777215);
		} else {
			this.field_41341.drawWithShadow(matrices, this.getX(), this.getY(), this.field_41342, 16777215);
		}
	}
}
