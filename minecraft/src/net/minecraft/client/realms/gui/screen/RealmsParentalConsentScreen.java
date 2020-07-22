package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5489;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.Realms;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class RealmsParentalConsentScreen extends RealmsScreen {
	private static final Text field_26491 = new TranslatableText("mco.account.privacyinfo");
	private final Screen parent;
	private class_5489 field_26492 = class_5489.field_26528;

	public RealmsParentalConsentScreen(Screen screen) {
		this.parent = screen;
	}

	@Override
	public void init() {
		Realms.narrateNow(field_26491.getString());
		Text text = new TranslatableText("mco.account.update");
		Text text2 = ScreenTexts.BACK;
		int i = Math.max(this.textRenderer.getWidth(text), this.textRenderer.getWidth(text2)) + 30;
		Text text3 = new TranslatableText("mco.account.privacy.info");
		int j = (int)((double)this.textRenderer.getWidth(text3) * 1.2);
		this.addButton(
			new ButtonWidget(this.width / 2 - j / 2, row(11), j, 20, text3, buttonWidget -> Util.getOperatingSystem().open("https://minecraft.net/privacy/gdpr/"))
		);
		this.addButton(
			new ButtonWidget(this.width / 2 - (i + 5), row(13), i, 20, text, buttonWidget -> Util.getOperatingSystem().open("https://minecraft.net/update-account"))
		);
		this.addButton(new ButtonWidget(this.width / 2 + 5, row(13), i, 20, text2, buttonWidget -> this.client.openScreen(this.parent)));
		this.field_26492 = class_5489.method_30890(this.textRenderer, field_26491, (int)Math.round((double)this.width * 0.9));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.field_26492.method_30889(matrices, this.width / 2, 15, 15, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
