package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.dto.RealmsServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.WorldCreationTask;

@Environment(EnvType.CLIENT)
public class RealmsCreateRealmScreen extends RealmsScreen {
	private final RealmsServer server;
	private final RealmsMainScreen lastScreen;
	private TextFieldWidget nameBox;
	private TextFieldWidget descriptionBox;
	private ButtonWidget createButton;
	private RealmsLabel createRealmLabel;

	public RealmsCreateRealmScreen(RealmsServer server, RealmsMainScreen lastScreen) {
		this.server = server;
		this.lastScreen = lastScreen;
	}

	@Override
	public void tick() {
		if (this.nameBox != null) {
			this.nameBox.tick();
		}

		if (this.descriptionBox != null) {
			this.descriptionBox.tick();
		}
	}

	@Override
	public void init() {
		this.client.keyboard.enableRepeatEvents(true);
		this.createButton = this.addButton(
			new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 17, 97, 20, I18n.translate("mco.create.world"), buttonWidget -> this.createWorld())
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 + 5, this.height / 4 + 120 + 17, 95, 20, I18n.translate("gui.cancel"), buttonWidget -> this.client.openScreen(this.lastScreen)
			)
		);
		this.createButton.active = false;
		this.nameBox = new TextFieldWidget(this.client.textRenderer, this.width / 2 - 100, 65, 200, 20, null, I18n.translate("mco.configure.world.name"));
		this.addChild(this.nameBox);
		this.setInitialFocus(this.nameBox);
		this.descriptionBox = new TextFieldWidget(
			this.client.textRenderer, this.width / 2 - 100, 115, 200, 20, null, I18n.translate("mco.configure.world.description")
		);
		this.addChild(this.descriptionBox);
		this.createRealmLabel = new RealmsLabel(I18n.translate("mco.selectServer.create"), this.width / 2, 11, 16777215);
		this.addChild(this.createRealmLabel);
		this.narrateLabels();
	}

	@Override
	public void removed() {
		this.client.keyboard.enableRepeatEvents(false);
	}

	@Override
	public boolean charTyped(char chr, int keyCode) {
		boolean bl = super.charTyped(chr, keyCode);
		this.createButton.active = this.valid();
		return bl;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			this.client.openScreen(this.lastScreen);
			return true;
		} else {
			boolean bl = super.keyPressed(keyCode, scanCode, modifiers);
			this.createButton.active = this.valid();
			return bl;
		}
	}

	private void createWorld() {
		if (this.valid()) {
			RealmsResetWorldScreen realmsResetWorldScreen = new RealmsResetWorldScreen(
				this.lastScreen,
				this.server,
				I18n.translate("mco.selectServer.create"),
				I18n.translate("mco.create.world.subtitle"),
				10526880,
				I18n.translate("mco.create.world.skip"),
				() -> this.client.openScreen(this.lastScreen.newScreen()),
				() -> this.client.openScreen(this.lastScreen.newScreen())
			);
			realmsResetWorldScreen.setResetTitle(I18n.translate("mco.create.world.reset.title"));
			this.client
				.openScreen(
					new RealmsLongRunningMcoTaskScreen(
						this.lastScreen, new WorldCreationTask(this.server.id, this.nameBox.getText(), this.descriptionBox.getText(), realmsResetWorldScreen)
					)
				);
		}
	}

	private boolean valid() {
		return !this.nameBox.getText().trim().isEmpty();
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.createRealmLabel.render(this);
		this.textRenderer.draw(I18n.translate("mco.configure.world.name"), (float)(this.width / 2 - 100), 52.0F, 10526880);
		this.textRenderer.draw(I18n.translate("mco.configure.world.description"), (float)(this.width / 2 - 100), 102.0F, 10526880);
		if (this.nameBox != null) {
			this.nameBox.render(mouseX, mouseY, delta);
		}

		if (this.descriptionBox != null) {
			this.descriptionBox.render(mouseX, mouseY, delta);
		}

		super.render(mouseX, mouseY, delta);
	}
}
