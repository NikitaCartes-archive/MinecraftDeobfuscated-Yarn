package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.exception.RealmsServiceException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;

@Environment(EnvType.CLIENT)
public class RealmsGenericErrorScreen extends RealmsScreen {
	private final RealmsScreen nextScreen;
	private String line1;
	private String line2;

	public RealmsGenericErrorScreen(RealmsServiceException realmsServiceException, RealmsScreen nextScreen) {
		this.nextScreen = nextScreen;
		this.errorMessage(realmsServiceException);
	}

	public RealmsGenericErrorScreen(String message, RealmsScreen nextScreen) {
		this.nextScreen = nextScreen;
		this.errorMessage(message);
	}

	public RealmsGenericErrorScreen(String title, String message, RealmsScreen nextScreen) {
		this.nextScreen = nextScreen;
		this.errorMessage(title, message);
	}

	private void errorMessage(RealmsServiceException realmsServiceException) {
		if (realmsServiceException.errorCode == -1) {
			this.line1 = "An error occurred (" + realmsServiceException.httpResultCode + "):";
			this.line2 = realmsServiceException.httpResponseContent;
		} else {
			this.line1 = "Realms (" + realmsServiceException.errorCode + "):";
			String string = "mco.errorMessage." + realmsServiceException.errorCode;
			String string2 = getLocalizedString(string);
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
		this.buttonsAdd(new RealmsButton(10, this.width() / 2 - 100, this.height() - 52, 200, 20, "Ok") {
			@Override
			public void onPress() {
				Realms.setScreen(RealmsGenericErrorScreen.this.nextScreen);
			}
		});
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public void render(int xm, int ym, float a) {
		this.renderBackground();
		this.drawCenteredString(this.line1, this.width() / 2, 80, 16777215);
		this.drawCenteredString(this.line2, this.width() / 2, 100, 16711680);
		super.render(xm, ym, a);
	}
}
