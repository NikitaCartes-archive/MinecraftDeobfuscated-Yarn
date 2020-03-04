package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.exception.RealmsServiceException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;

@Environment(EnvType.CLIENT)
public class RealmsGenericErrorScreen extends RealmsScreen {
	private final Screen field_22695;
	private String line1;
	private String line2;

	public RealmsGenericErrorScreen(RealmsServiceException realmsServiceException, Screen screen) {
		this.field_22695 = screen;
		this.errorMessage(realmsServiceException);
	}

	public RealmsGenericErrorScreen(String message, Screen screen) {
		this.field_22695 = screen;
		this.errorMessage(message);
	}

	public RealmsGenericErrorScreen(String title, String message, Screen screen) {
		this.field_22695 = screen;
		this.errorMessage(title, message);
	}

	private void errorMessage(RealmsServiceException realmsServiceException) {
		if (realmsServiceException.errorCode == -1) {
			this.line1 = "An error occurred (" + realmsServiceException.httpResultCode + "):";
			this.line2 = realmsServiceException.httpResponseContent;
		} else {
			this.line1 = "Realms (" + realmsServiceException.errorCode + "):";
			String string = "mco.errorMessage." + realmsServiceException.errorCode;
			String string2 = I18n.translate(string);
			this.line2 = string2.equals(string) ? realmsServiceException.errorMsg : string2;
		}
	}

	private void errorMessage(String message) {
		this.line1 = "An error occurred: ";
		this.line2 = message;
	}

	private void errorMessage(String title, String message) {
		this.line1 = title;
		this.line2 = message;
	}

	@Override
	public void init() {
		Realms.narrateNow(this.line1 + ": " + this.line2);
		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 52, 200, 20, "Ok", buttonWidget -> this.client.openScreen(this.field_22695)));
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, this.line1, this.width / 2, 80, 16777215);
		this.drawCenteredString(this.textRenderer, this.line2, this.width / 2, 100, 16711680);
		super.render(mouseX, mouseY, delta);
	}
}
