package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
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
	private static final Text PRIVACY_INFO_TEXT = new TranslatableText("mco.account.privacyinfo");
	private final Screen parent;
	private MultilineText field_26492 = MultilineText.EMPTY;

	public RealmsParentalConsentScreen(Screen parent) {
		this.parent = parent;
	}

	@Override
	public void init() {
		Realms.narrateNow(PRIVACY_INFO_TEXT.getString());
		Text text = new TranslatableText("mco.account.update");
		Text text2 = ScreenTexts.BACK;
		int i = Math.max(this.textRenderer.getWidth(text), this.textRenderer.getWidth(text2)) + 30;
		Text text3 = new TranslatableText("mco.account.privacy.info");
		int j = (int)((double)this.textRenderer.getWidth(text3) * 1.2);
		this.addButton(
			new ButtonWidget(this.width / 2 - j / 2, row(11), j, 20, text3, buttonWidget -> Util.getOperatingSystem().open("https://aka.ms/MinecraftGDPR"))
		);
		this.addButton(
			new ButtonWidget(this.width / 2 - (i + 5), row(13), i, 20, text, buttonWidget -> Util.getOperatingSystem().open("https://aka.ms/UpdateMojangAccount"))
		);
		this.addButton(new ButtonWidget(this.width / 2 + 5, row(13), i, 20, text2, buttonWidget -> this.client.openScreen(this.parent)));
		this.field_26492 = MultilineText.create(this.textRenderer, PRIVACY_INFO_TEXT, (int)Math.round((double)this.width * 0.9));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.field_26492.drawCenterWithShadow(matrices, this.width / 2, 15, 15, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
