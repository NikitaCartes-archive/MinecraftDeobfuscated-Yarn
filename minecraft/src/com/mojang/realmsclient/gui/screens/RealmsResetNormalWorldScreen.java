package com.mojang.realmsclient.gui.screens;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;

@Environment(EnvType.CLIENT)
public class RealmsResetNormalWorldScreen extends RealmsScreen {
	private final RealmsResetWorldScreen lastScreen;
	private RealmsLabel titleLabel;
	private TextFieldWidget seedEdit;
	private Boolean generateStructures = true;
	private Integer levelTypeIndex = 0;
	private String[] levelTypes;
	private String buttonTitle;

	public RealmsResetNormalWorldScreen(RealmsResetWorldScreen realmsResetWorldScreen, String string) {
		this.lastScreen = realmsResetWorldScreen;
		this.buttonTitle = string;
	}

	@Override
	public void tick() {
		this.seedEdit.tick();
		super.tick();
	}

	@Override
	public void init() {
		this.levelTypes = new String[]{
			I18n.translate("generator.default"), I18n.translate("generator.flat"), I18n.translate("generator.largeBiomes"), I18n.translate("generator.amplified")
		};
		this.client.keyboard.enableRepeatEvents(true);
		this.titleLabel = new RealmsLabel(I18n.translate("mco.reset.world.generate"), this.width / 2, 17, 16777215);
		this.addChild(this.titleLabel);
		this.seedEdit = new TextFieldWidget(this.client.textRenderer, this.width / 2 - 100, row(2), 200, 20, null, I18n.translate("mco.reset.world.seed"));
		this.seedEdit.setMaxLength(32);
		this.addChild(this.seedEdit);
		this.setInitialFocus(this.seedEdit);
		this.addButton(new ButtonWidget(this.width / 2 - 102, row(4), 205, 20, this.levelTypeTitle(), buttonWidget -> {
			this.levelTypeIndex = (this.levelTypeIndex + 1) % this.levelTypes.length;
			buttonWidget.setMessage(this.levelTypeTitle());
		}));
		this.addButton(new ButtonWidget(this.width / 2 - 102, row(6) - 2, 205, 20, this.generateStructuresTitle(), buttonWidget -> {
			this.generateStructures = !this.generateStructures;
			buttonWidget.setMessage(this.generateStructuresTitle());
		}));
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 102,
				row(12),
				97,
				20,
				this.buttonTitle,
				buttonWidget -> this.lastScreen
						.resetWorld(new RealmsResetWorldScreen.ResetWorldInfo(this.seedEdit.getText(), this.levelTypeIndex, this.generateStructures))
			)
		);
		this.addButton(new ButtonWidget(this.width / 2 + 8, row(12), 97, 20, I18n.translate("gui.back"), buttonWidget -> this.client.openScreen(this.lastScreen)));
		this.narrateLabels();
	}

	@Override
	public void removed() {
		this.client.keyboard.enableRepeatEvents(false);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			this.client.openScreen(this.lastScreen);
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.titleLabel.render(this);
		this.textRenderer.draw(I18n.translate("mco.reset.world.seed"), (float)(this.width / 2 - 100), (float)row(1), 10526880);
		this.seedEdit.render(mouseX, mouseY, delta);
		super.render(mouseX, mouseY, delta);
	}

	private String levelTypeTitle() {
		String string = I18n.translate("selectWorld.mapType");
		return string + " " + this.levelTypes[this.levelTypeIndex];
	}

	private String generateStructuresTitle() {
		String string = this.generateStructures ? "mco.configure.world.on" : "mco.configure.world.off";
		return I18n.translate("selectWorld.mapFeatures") + " " + I18n.translate(string);
	}
}
