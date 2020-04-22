package com.mojang.realmsclient.gui.screens;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class RealmsResetNormalWorldScreen extends RealmsScreen {
	private final RealmsResetWorldScreen lastScreen;
	private RealmsLabel titleLabel;
	private TextFieldWidget seedEdit;
	private Boolean generateStructures = true;
	private Integer levelTypeIndex = 0;
	private Text[] field_24205 = new Text[]{
		new TranslatableText("generator.default"),
		new TranslatableText("generator.flat"),
		new TranslatableText("generator.largeBiomes"),
		new TranslatableText("generator.amplified")
	};
	private Text field_24206;

	public RealmsResetNormalWorldScreen(RealmsResetWorldScreen realmsResetWorldScreen, Text text) {
		this.lastScreen = realmsResetWorldScreen;
		this.field_24206 = text;
	}

	@Override
	public void tick() {
		this.seedEdit.tick();
		super.tick();
	}

	@Override
	public void init() {
		this.client.keyboard.enableRepeatEvents(true);
		this.titleLabel = new RealmsLabel(new TranslatableText("mco.reset.world.generate"), this.width / 2, 17, 16777215);
		this.addChild(this.titleLabel);
		this.seedEdit = new TextFieldWidget(this.client.textRenderer, this.width / 2 - 100, row(2), 200, 20, null, new TranslatableText("mco.reset.world.seed"));
		this.seedEdit.setMaxLength(32);
		this.addChild(this.seedEdit);
		this.setInitialFocus(this.seedEdit);
		this.addButton(new ButtonWidget(this.width / 2 - 102, row(4), 205, 20, this.method_27458(), buttonWidget -> {
			this.levelTypeIndex = (this.levelTypeIndex + 1) % this.field_24205.length;
			buttonWidget.setMessage(this.method_27458());
		}));
		this.addButton(new ButtonWidget(this.width / 2 - 102, row(6) - 2, 205, 20, this.method_27459(), buttonWidget -> {
			this.generateStructures = !this.generateStructures;
			buttonWidget.setMessage(this.method_27459());
		}));
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 102,
				row(12),
				97,
				20,
				this.field_24206,
				buttonWidget -> this.lastScreen
						.resetWorld(new RealmsResetWorldScreen.ResetWorldInfo(this.seedEdit.getText(), this.levelTypeIndex, this.generateStructures))
			)
		);
		this.addButton(new ButtonWidget(this.width / 2 + 8, row(12), 97, 20, ScreenTexts.BACK, buttonWidget -> this.client.openScreen(this.lastScreen)));
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
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.titleLabel.render(this, matrices);
		this.textRenderer.draw(matrices, I18n.translate("mco.reset.world.seed"), (float)(this.width / 2 - 100), (float)row(1), 10526880);
		this.seedEdit.render(matrices, mouseX, mouseY, delta);
		super.render(matrices, mouseX, mouseY, delta);
	}

	private Text method_27458() {
		return new TranslatableText("selectWorld.mapType").append(" ").append(this.field_24205[this.levelTypeIndex]);
	}

	private Text method_27459() {
		String string = this.generateStructures ? "mco.configure.world.on" : "mco.configure.world.off";
		return new TranslatableText("selectWorld.mapFeatures").append(" ").append(new TranslatableText(string));
	}
}
