package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.Realms;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class RealmsGenericErrorScreen extends RealmsScreen {
	private final Screen parent;
	private Text line1;
	private Text line2;

	public RealmsGenericErrorScreen(RealmsServiceException realmsServiceException, Screen parent) {
		this.parent = parent;
		this.errorMessage(realmsServiceException);
	}

	public RealmsGenericErrorScreen(Text line2, Screen parent) {
		this.parent = parent;
		this.errorMessage(line2);
	}

	public RealmsGenericErrorScreen(Text line1, Text line2, Screen parent) {
		this.parent = parent;
		this.errorMessage(line1, line2);
	}

	private void errorMessage(RealmsServiceException realmsServiceException) {
		if (realmsServiceException.errorCode == -1) {
			this.line1 = new LiteralText("An error occurred (" + realmsServiceException.httpResultCode + "):");
			this.line2 = new LiteralText(realmsServiceException.httpResponseContent);
		} else {
			this.line1 = new LiteralText("Realms (" + realmsServiceException.errorCode + "):");
			String string = "mco.errorMessage." + realmsServiceException.errorCode;
			this.line2 = (Text)(I18n.hasTranslation(string) ? new TranslatableText(string) : Text.of(realmsServiceException.errorMsg));
		}
	}

	private void errorMessage(Text line2) {
		this.line1 = new LiteralText("An error occurred: ");
		this.line2 = line2;
	}

	private void errorMessage(Text line1, Text line2) {
		this.line1 = line1;
		this.line2 = line2;
	}

	@Override
	public void init() {
		Realms.narrateNow(this.line1.getString() + ": " + this.line2.getString());
		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 52, 200, 20, new LiteralText("Ok"), buttonWidget -> this.client.openScreen(this.parent)));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.line1, this.width / 2, 80, 16777215);
		drawCenteredText(matrices, this.textRenderer, this.line2, this.width / 2, 100, 16711680);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
