package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.RealmsServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;

@Environment(EnvType.CLIENT)
public class RealmsSettingsScreen extends RealmsScreen {
	private final RealmsConfigureWorldScreen configureWorldScreen;
	private final RealmsServer serverData;
	private ButtonWidget doneButton;
	private TextFieldWidget descEdit;
	private TextFieldWidget nameEdit;
	private RealmsLabel titleLabel;

	public RealmsSettingsScreen(RealmsConfigureWorldScreen configureWorldScreen, RealmsServer serverData) {
		this.configureWorldScreen = configureWorldScreen;
		this.serverData = serverData;
	}

	@Override
	public void tick() {
		this.nameEdit.tick();
		this.descEdit.tick();
		this.doneButton.active = !this.nameEdit.getText().trim().isEmpty();
	}

	@Override
	public void init() {
		this.client.keyboard.enableRepeatEvents(true);
		int i = this.width / 2 - 106;
		this.doneButton = this.addButton(new ButtonWidget(i - 2, row(12), 106, 20, I18n.translate("mco.configure.world.buttons.done"), buttonWidgetx -> this.save()));
		this.addButton(
			new ButtonWidget(this.width / 2 + 2, row(12), 106, 20, I18n.translate("gui.cancel"), buttonWidgetx -> this.client.openScreen(this.configureWorldScreen))
		);
		String string = this.serverData.state == RealmsServer.State.OPEN ? "mco.configure.world.buttons.close" : "mco.configure.world.buttons.open";
		ButtonWidget buttonWidget = new ButtonWidget(this.width / 2 - 53, row(0), 106, 20, I18n.translate(string), buttonWidgetx -> {
			if (this.serverData.state == RealmsServer.State.OPEN) {
				String stringx = I18n.translate("mco.configure.world.close.question.line1");
				String string2 = I18n.translate("mco.configure.world.close.question.line2");
				this.client.openScreen(new RealmsLongConfirmationScreen(bl -> {
					if (bl) {
						this.configureWorldScreen.closeTheWorld(this);
					} else {
						this.client.openScreen(this);
					}
				}, RealmsLongConfirmationScreen.Type.Info, stringx, string2, true));
			} else {
				this.configureWorldScreen.openTheWorld(false, this);
			}
		});
		this.addButton(buttonWidget);
		this.nameEdit = new TextFieldWidget(this.client.textRenderer, i, row(4), 212, 20, null, I18n.translate("mco.configure.world.name"));
		this.nameEdit.setMaxLength(32);
		this.nameEdit.setText(this.serverData.getName());
		this.addChild(this.nameEdit);
		this.focusOn(this.nameEdit);
		this.descEdit = new TextFieldWidget(this.client.textRenderer, i, row(8), 212, 20, null, I18n.translate("mco.configure.world.description"));
		this.descEdit.setMaxLength(32);
		this.descEdit.setText(this.serverData.getDescription());
		this.addChild(this.descEdit);
		this.titleLabel = this.addChild(new RealmsLabel(I18n.translate("mco.configure.world.settings.title"), this.width / 2, 17, 16777215));
		this.narrateLabels();
	}

	@Override
	public void removed() {
		this.client.keyboard.enableRepeatEvents(false);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			this.client.openScreen(this.configureWorldScreen);
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.titleLabel.render(this);
		this.textRenderer.draw(I18n.translate("mco.configure.world.name"), (float)(this.width / 2 - 106), (float)row(3), 10526880);
		this.textRenderer.draw(I18n.translate("mco.configure.world.description"), (float)(this.width / 2 - 106), (float)row(7), 10526880);
		this.nameEdit.render(mouseX, mouseY, delta);
		this.descEdit.render(mouseX, mouseY, delta);
		super.render(mouseX, mouseY, delta);
	}

	public void save() {
		this.configureWorldScreen.saveSettings(this.nameEdit.getText(), this.descEdit.getText());
	}
}
