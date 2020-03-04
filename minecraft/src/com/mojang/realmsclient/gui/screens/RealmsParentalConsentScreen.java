package com.mojang.realmsclient.gui.screens;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class RealmsParentalConsentScreen extends RealmsScreen {
	private final Screen field_22701;

	public RealmsParentalConsentScreen(Screen screen) {
		this.field_22701 = screen;
	}

	@Override
	public void init() {
		Realms.narrateNow(I18n.translate("mco.account.privacyinfo"));
		String string = I18n.translate("mco.account.update");
		String string2 = I18n.translate("gui.back");
		int i = Math.max(this.textRenderer.getStringWidth(string), this.textRenderer.getStringWidth(string2)) + 30;
		String string3 = I18n.translate("mco.account.privacy.info");
		int j = (int)((double)this.textRenderer.getStringWidth(string3) * 1.2);
		this.addButton(
			new ButtonWidget(this.width / 2 - j / 2, row(11), j, 20, string3, buttonWidget -> Util.getOperatingSystem().open("https://minecraft.net/privacy/gdpr/"))
		);
		this.addButton(
			new ButtonWidget(this.width / 2 - (i + 5), row(13), i, 20, string, buttonWidget -> Util.getOperatingSystem().open("https://minecraft.net/update-account"))
		);
		this.addButton(new ButtonWidget(this.width / 2 + 5, row(13), i, 20, string2, buttonWidget -> this.client.openScreen(this.field_22701)));
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		List<String> list = this.client.textRenderer.wrapStringToWidthAsList(I18n.translate("mco.account.privacyinfo"), (int)Math.round((double)this.width * 0.9));
		int i = 15;

		for (String string : list) {
			this.drawCenteredString(this.textRenderer, string, this.width / 2, i, 16777215);
			i += 15;
		}

		super.render(mouseX, mouseY, delta);
	}
}
