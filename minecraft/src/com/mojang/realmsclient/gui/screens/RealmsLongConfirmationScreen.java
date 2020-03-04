package com.mojang.realmsclient.gui.screens;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;

@Environment(EnvType.CLIENT)
public class RealmsLongConfirmationScreen extends RealmsScreen {
	private final RealmsLongConfirmationScreen.Type type;
	private final String line2;
	private final String line3;
	protected final BooleanConsumer field_22697;
	protected final String yesButton;
	protected final String noButton;
	private final String okButton;
	private final boolean yesNoQuestion;

	public RealmsLongConfirmationScreen(BooleanConsumer booleanConsumer, RealmsLongConfirmationScreen.Type type, String line2, String line3, boolean yesNoQuestion) {
		this.field_22697 = booleanConsumer;
		this.type = type;
		this.line2 = line2;
		this.line3 = line3;
		this.yesNoQuestion = yesNoQuestion;
		this.yesButton = I18n.translate("gui.yes");
		this.noButton = I18n.translate("gui.no");
		this.okButton = I18n.translate("mco.gui.ok");
	}

	@Override
	public void init() {
		Realms.narrateNow(this.type.text, this.line2, this.line3);
		if (this.yesNoQuestion) {
			this.addButton(new ButtonWidget(this.width / 2 - 105, row(8), 100, 20, this.yesButton, buttonWidget -> this.field_22697.accept(true)));
			this.addButton(new ButtonWidget(this.width / 2 + 5, row(8), 100, 20, this.noButton, buttonWidget -> this.field_22697.accept(false)));
		} else {
			this.addButton(new ButtonWidget(this.width / 2 - 50, row(8), 100, 20, this.okButton, buttonWidget -> this.field_22697.accept(true)));
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			this.field_22697.accept(false);
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, this.type.text, this.width / 2, row(2), this.type.colorCode);
		this.drawCenteredString(this.textRenderer, this.line2, this.width / 2, row(4), 16777215);
		this.drawCenteredString(this.textRenderer, this.line3, this.width / 2, row(6), 16777215);
		super.render(mouseX, mouseY, delta);
	}

	@Environment(EnvType.CLIENT)
	public static enum Type {
		Warning("Warning!", 16711680),
		Info("Info!", 8226750);

		public final int colorCode;
		public final String text;

		private Type(String text, int colorCode) {
			this.text = text;
			this.colorCode = colorCode;
		}
	}
}
