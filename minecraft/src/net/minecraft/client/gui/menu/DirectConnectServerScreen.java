package net.minecraft.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.options.ServerEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableTextComponent;

@Environment(EnvType.CLIENT)
public class DirectConnectServerScreen extends Screen {
	private ButtonWidget selectServerButton;
	private final Screen parent;
	private final ServerEntry serverEntry;
	private TextFieldWidget addressField;

	public DirectConnectServerScreen(Screen screen, ServerEntry serverEntry) {
		super(new TranslatableTextComponent("selectServer.direct"));
		this.parent = screen;
		this.serverEntry = serverEntry;
	}

	@Override
	public void update() {
		this.addressField.tick();
	}

	@Override
	protected void onInitialized() {
		this.client.keyboard.enableRepeatEvents(true);
		this.selectServerButton = this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 - 100, this.screenHeight / 4 + 96 + 12, 200, 20, I18n.translate("selectServer.select"), buttonWidget -> this.saveAndClose()
			)
		);
		this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 - 100, this.screenHeight / 4 + 120 + 12, 200, 20, I18n.translate("gui.cancel"), buttonWidget -> this.parent.confirmResult(false, 0)
			)
		);
		this.addressField = new TextFieldWidget(this.fontRenderer, this.screenWidth / 2 - 100, 116, 200, 20);
		this.addressField.setMaxLength(128);
		this.addressField.setFocused(true);
		this.addressField.setText(this.client.options.lastServer);
		this.addressField.setChangedListener(string -> this.onAddressFieldChanged());
		this.listeners.add(this.addressField);
		this.focusOn(this.addressField);
		this.onAddressFieldChanged();
	}

	@Override
	public void onScaleChanged(MinecraftClient minecraftClient, int i, int j) {
		String string = this.addressField.getText();
		this.initialize(minecraftClient, i, j);
		this.addressField.setText(string);
	}

	private void saveAndClose() {
		this.serverEntry.address = this.addressField.getText();
		this.parent.confirmResult(true, 0);
	}

	@Override
	public void onClosed() {
		this.client.keyboard.enableRepeatEvents(false);
		this.client.options.lastServer = this.addressField.getText();
		this.client.options.write();
	}

	private void onAddressFieldChanged() {
		this.selectServerButton.active = !this.addressField.getText().isEmpty() && this.addressField.getText().split(":").length > 0;
	}

	@Override
	public void render(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, this.title.getFormattedText(), this.screenWidth / 2, 20, 16777215);
		this.drawString(this.fontRenderer, I18n.translate("addServer.enterIp"), this.screenWidth / 2 - 100, 100, 10526880);
		this.addressField.render(i, j, f);
		super.render(i, j, f);
	}
}
