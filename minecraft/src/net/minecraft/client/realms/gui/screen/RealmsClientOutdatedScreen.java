package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class RealmsClientOutdatedScreen extends RealmsScreen {
	private static final Text field_26475 = new TranslatableText("mco.client.outdated.title");
	private static final Text[] field_26476 = new Text[]{
		new TranslatableText("mco.client.outdated.msg.line1"), new TranslatableText("mco.client.outdated.msg.line2")
	};
	private static final Text field_26477 = new TranslatableText("mco.client.incompatible.title");
	private static final Text[] field_26478 = new Text[]{
		new TranslatableText("mco.client.incompatible.msg.line1"),
		new TranslatableText("mco.client.incompatible.msg.line2"),
		new TranslatableText("mco.client.incompatible.msg.line3")
	};
	private final Screen parent;
	private final boolean outdated;

	public RealmsClientOutdatedScreen(Screen parent, boolean outdated) {
		this.parent = parent;
		this.outdated = outdated;
	}

	@Override
	public void init() {
		this.addButton(new ButtonWidget(this.width / 2 - 100, row(12), 200, 20, ScreenTexts.BACK, buttonWidget -> this.client.openScreen(this.parent)));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		Text text;
		Text[] texts;
		if (this.outdated) {
			text = field_26477;
			texts = field_26478;
		} else {
			text = field_26475;
			texts = field_26476;
		}

		drawCenteredText(matrices, this.textRenderer, text, this.width / 2, row(3), 16711680);

		for (int i = 0; i < texts.length; i++) {
			drawCenteredText(matrices, this.textRenderer, texts[i], this.width / 2, row(5) + i * 12, 16777215);
		}

		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode != 257 && keyCode != 335 && keyCode != 256) {
			return super.keyPressed(keyCode, scanCode, modifiers);
		} else {
			this.client.openScreen(this.parent);
			return true;
		}
	}
}
