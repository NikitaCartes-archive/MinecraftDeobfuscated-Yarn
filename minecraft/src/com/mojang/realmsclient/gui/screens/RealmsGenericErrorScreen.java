package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.exception.RealmsServiceException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class RealmsGenericErrorScreen extends RealmsScreen {
	private final Screen field_22695;
	private Text line1;
	private Text line2;

	public RealmsGenericErrorScreen(RealmsServiceException realmsServiceException, Screen screen) {
		this.field_22695 = screen;
		this.errorMessage(realmsServiceException);
	}

	public RealmsGenericErrorScreen(Text text, Screen screen) {
		this.field_22695 = screen;
		this.errorMessage(text);
	}

	public RealmsGenericErrorScreen(Text text, Text text2, Screen screen) {
		this.field_22695 = screen;
		this.errorMessage(text, text2);
	}

	private void errorMessage(RealmsServiceException realmsServiceException) {
		if (realmsServiceException.errorCode == -1) {
			this.line1 = new LiteralText("An error occurred (" + realmsServiceException.httpResultCode + "):");
			this.line2 = new LiteralText(realmsServiceException.httpResponseContent);
		} else {
			this.line1 = new LiteralText("Realms (" + realmsServiceException.errorCode + "):");
			String string = "mco.errorMessage." + realmsServiceException.errorCode;
			this.line2 = (Text)(I18n.hasTranslation(string) ? new TranslatableText(string) : Text.method_30163(realmsServiceException.errorMsg));
		}
	}

	private void errorMessage(Text text) {
		this.line1 = new LiteralText("An error occurred: ");
		this.line2 = text;
	}

	private void errorMessage(Text text, Text text2) {
		this.line1 = text;
		this.line2 = text2;
	}

	@Override
	public void init() {
		Realms.narrateNow(this.line1.getString() + ": " + this.line2.getString());
		this.addButton(
			new ButtonWidget(this.width / 2 - 100, this.height - 52, 200, 20, new LiteralText("Ok"), buttonWidget -> this.client.openScreen(this.field_22695))
		);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.drawCenteredText(matrices, this.textRenderer, this.line1, this.width / 2, 80, 16777215);
		this.drawCenteredText(matrices, this.textRenderer, this.line2, this.width / 2, 100, 16711680);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
