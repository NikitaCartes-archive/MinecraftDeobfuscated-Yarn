package com.mojang.realmsclient.gui.screens;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.RealmsScreen;

@Environment(EnvType.CLIENT)
public class RealmsClientOutdatedScreen extends RealmsScreen {
	private final Screen lastScreen;
	private final boolean outdated;

	public RealmsClientOutdatedScreen(Screen screen, boolean outdated) {
		this.lastScreen = screen;
		this.outdated = outdated;
	}

	@Override
	public void init() {
		this.addButton(new ButtonWidget(this.width / 2 - 100, row(12), 200, 20, I18n.translate("gui.back"), buttonWidget -> this.client.openScreen(this.lastScreen)));
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		String string = I18n.translate(this.outdated ? "mco.client.outdated.title" : "mco.client.incompatible.title");
		this.drawCenteredString(this.textRenderer, string, this.width / 2, row(3), 16711680);
		int i = this.outdated ? 2 : 3;

		for (int j = 0; j < i; j++) {
			String string2 = (this.outdated ? "mco.client.outdated.msg.line" : "mco.client.incompatible.msg.line") + (j + 1);
			String string3 = I18n.translate(string2);
			this.drawCenteredString(this.textRenderer, string3, this.width / 2, row(5) + j * 12, 16777215);
		}

		super.render(mouseX, mouseY, delta);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode != 257 && keyCode != 335 && keyCode != 256) {
			return super.keyPressed(keyCode, scanCode, modifiers);
		} else {
			this.client.openScreen(this.lastScreen);
			return true;
		}
	}
}
