package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.gui.RealmsConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;

@Environment(EnvType.CLIENT)
public class RealmsClientOutdatedScreen extends RealmsScreen {
	private final RealmsScreen lastScreen;
	private final boolean outdated;

	public RealmsClientOutdatedScreen(RealmsScreen lastScreen, boolean outdated) {
		this.lastScreen = lastScreen;
		this.outdated = outdated;
	}

	@Override
	public void init() {
		this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 100, RealmsConstants.row(12), getLocalizedString("gui.back")) {
			@Override
			public void onPress() {
				Realms.setScreen(RealmsClientOutdatedScreen.this.lastScreen);
			}
		});
	}

	@Override
	public void render(int xm, int ym, float a) {
		this.renderBackground();
		String string = getLocalizedString(this.outdated ? "mco.client.outdated.title" : "mco.client.incompatible.title");
		this.drawCenteredString(string, this.width() / 2, RealmsConstants.row(3), 16711680);
		int i = this.outdated ? 2 : 3;

		for (int j = 0; j < i; j++) {
			String string2 = getLocalizedString((this.outdated ? "mco.client.outdated.msg.line" : "mco.client.incompatible.msg.line") + (j + 1));
			this.drawCenteredString(string2, this.width() / 2, RealmsConstants.row(5) + j * 12, 16777215);
		}

		super.render(xm, ym, a);
	}

	@Override
	public boolean keyPressed(int eventKey, int scancode, int mods) {
		if (eventKey != 257 && eventKey != 335 && eventKey != 256) {
			return super.keyPressed(eventKey, scancode, mods);
		} else {
			Realms.setScreen(this.lastScreen);
			return true;
		}
	}
}
