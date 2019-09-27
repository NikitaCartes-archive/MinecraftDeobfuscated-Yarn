package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class DirectConnectScreen extends Screen {
	private ButtonWidget selectServerButton;
	private final ServerInfo serverEntry;
	private TextFieldWidget addressField;
	private final BooleanConsumer callback;

	public DirectConnectScreen(BooleanConsumer booleanConsumer, ServerInfo serverInfo) {
		super(new TranslatableText("selectServer.direct"));
		this.serverEntry = serverInfo;
		this.callback = booleanConsumer;
	}

	@Override
	public void tick() {
		this.addressField.tick();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (this.getFocused() != this.addressField || i != 257 && i != 335) {
			return super.keyPressed(i, j, k);
		} else {
			this.saveAndClose();
			return true;
		}
	}

	@Override
	protected void init() {
		this.minecraft.keyboard.enableRepeatEvents(true);
		this.selectServerButton = this.addButton(
			new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20, I18n.translate("selectServer.select"), buttonWidget -> this.saveAndClose())
		);
		this.addButton(
			new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, I18n.translate("gui.cancel"), buttonWidget -> this.callback.accept(false))
		);
		this.addressField = new TextFieldWidget(this.font, this.width / 2 - 100, 116, 200, 20, I18n.translate("addServer.enterIp"));
		this.addressField.setMaxLength(128);
		this.addressField.setSelected(true);
		this.addressField.setText(this.minecraft.options.lastServer);
		this.addressField.setChangedListener(string -> this.onAddressFieldChanged());
		this.children.add(this.addressField);
		this.setInitialFocus(this.addressField);
		this.onAddressFieldChanged();
	}

	@Override
	public void resize(MinecraftClient minecraftClient, int i, int j) {
		String string = this.addressField.getText();
		this.init(minecraftClient, i, j);
		this.addressField.setText(string);
	}

	private void saveAndClose() {
		this.serverEntry.address = this.addressField.getText();
		this.callback.accept(true);
	}

	@Override
	public void removed() {
		this.minecraft.keyboard.enableRepeatEvents(false);
		this.minecraft.options.lastServer = this.addressField.getText();
		this.minecraft.options.write();
	}

	private void onAddressFieldChanged() {
		String string = this.addressField.getText();
		this.selectServerButton.active = !string.isEmpty() && string.split(":").length > 0 && string.indexOf(32) == -1;
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 20, 16777215);
		this.drawString(this.font, I18n.translate("addServer.enterIp"), this.width / 2 - 100, 100, 10526880);
		this.addressField.render(i, j, f);
		super.render(i, j, f);
	}
}
