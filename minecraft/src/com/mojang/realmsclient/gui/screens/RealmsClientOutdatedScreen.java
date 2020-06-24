package com.mojang.realmsclient.gui.screens;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class RealmsClientOutdatedScreen extends RealmsScreen {
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
		Text text = new TranslatableText(this.outdated ? "mco.client.outdated.title" : "mco.client.incompatible.title");
		this.drawCenteredText(matrices, this.textRenderer, text, this.width / 2, row(3), 16711680);
		int i = this.outdated ? 2 : 3;

		for (int j = 0; j < i; j++) {
			String string = (this.outdated ? "mco.client.outdated.msg.line" : "mco.client.incompatible.msg.line") + (j + 1);
			this.drawCenteredText(matrices, this.textRenderer, new TranslatableText(string), this.width / 2, row(5) + j * 12, 16777215);
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
