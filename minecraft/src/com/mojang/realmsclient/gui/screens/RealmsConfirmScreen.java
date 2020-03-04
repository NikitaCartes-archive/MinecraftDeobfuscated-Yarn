package com.mojang.realmsclient.gui.screens;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.RealmsScreen;

@Environment(EnvType.CLIENT)
public class RealmsConfirmScreen extends RealmsScreen {
	protected BooleanConsumer field_22692;
	protected String title1;
	private final String title2;
	protected String yesButton = I18n.translate("gui.yes");
	protected String noButton = I18n.translate("gui.no");
	private int delayTicker;

	public RealmsConfirmScreen(BooleanConsumer booleanConsumer, String title1, String title2) {
		this.field_22692 = booleanConsumer;
		this.title1 = title1;
		this.title2 = title2;
	}

	@Override
	public void init() {
		this.addButton(new ButtonWidget(this.width / 2 - 105, row(9), 100, 20, this.yesButton, buttonWidget -> this.field_22692.accept(true)));
		this.addButton(new ButtonWidget(this.width / 2 + 5, row(9), 100, 20, this.noButton, buttonWidget -> this.field_22692.accept(false)));
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, this.title1, this.width / 2, row(3), 16777215);
		this.drawCenteredString(this.textRenderer, this.title2, this.width / 2, row(5), 16777215);
		super.render(mouseX, mouseY, delta);
	}

	@Override
	public void tick() {
		super.tick();
		if (--this.delayTicker == 0) {
			for (AbstractButtonWidget abstractButtonWidget : this.buttons) {
				abstractButtonWidget.active = true;
			}
		}
	}
}
