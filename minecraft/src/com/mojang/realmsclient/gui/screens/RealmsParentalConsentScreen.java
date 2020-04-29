package com.mojang.realmsclient.gui.screens;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
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
		Text text = new TranslatableText("mco.account.update");
		Text text2 = ScreenTexts.BACK;
		int i = Math.max(this.textRenderer.getStringWidth(text), this.textRenderer.getStringWidth(text2)) + 30;
		Text text3 = new TranslatableText("mco.account.privacy.info");
		int j = (int)((double)this.textRenderer.getStringWidth(text3) * 1.2);
		this.addButton(
			new ButtonWidget(this.width / 2 - j / 2, row(11), j, 20, text3, buttonWidget -> Util.getOperatingSystem().open("https://minecraft.net/privacy/gdpr/"))
		);
		this.addButton(
			new ButtonWidget(this.width / 2 - (i + 5), row(13), i, 20, text, buttonWidget -> Util.getOperatingSystem().open("https://minecraft.net/update-account"))
		);
		this.addButton(new ButtonWidget(this.width / 2 + 5, row(13), i, 20, text2, buttonWidget -> this.client.openScreen(this.field_22701)));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		List<Text> list = this.client.textRenderer.wrapLines(new TranslatableText("mco.account.privacyinfo"), (int)Math.round((double)this.width * 0.9));
		int i = 15;

		for (Text text : list) {
			this.drawStringWithShadow(matrices, this.textRenderer, text, this.width / 2, i, 16777215);
			i += 15;
		}

		super.render(matrices, mouseX, mouseY, delta);
	}
}
