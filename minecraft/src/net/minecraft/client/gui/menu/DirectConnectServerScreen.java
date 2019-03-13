package net.minecraft.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4185;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.options.ServerEntry;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class DirectConnectServerScreen extends Screen {
	private class_4185 selectServerButton;
	private final Screen field_2461;
	private final ServerEntry field_2460;
	private TextFieldWidget addressField;

	public DirectConnectServerScreen(Screen screen, ServerEntry serverEntry) {
		this.field_2461 = screen;
		this.field_2460 = serverEntry;
	}

	@Override
	public void update() {
		this.addressField.tick();
	}

	@Override
	protected void onInitialized() {
		this.client.keyboard.enableRepeatEvents(true);
		this.selectServerButton = this.addButton(new class_4185(this.screenWidth / 2 - 100, this.screenHeight / 4 + 96 + 12, I18n.translate("selectServer.select")) {
			@Override
			public void method_1826() {
				DirectConnectServerScreen.this.saveAndClose();
			}
		});
		this.addButton(new class_4185(this.screenWidth / 2 - 100, this.screenHeight / 4 + 120 + 12, I18n.translate("gui.cancel")) {
			@Override
			public void method_1826() {
				DirectConnectServerScreen.this.field_2461.confirmResult(false, 0);
			}
		});
		this.addressField = new TextFieldWidget(this.fontRenderer, this.screenWidth / 2 - 100, 116, 200, 20);
		this.addressField.setMaxLength(128);
		this.addressField.setFocused(true);
		this.addressField.setText(this.client.field_1690.lastServer);
		this.addressField.setChangedListener(string -> this.onAddressFieldChanged());
		this.listeners.add(this.addressField);
		this.method_18624(this.addressField);
		this.onAddressFieldChanged();
	}

	@Override
	public void onScaleChanged(MinecraftClient minecraftClient, int i, int j) {
		String string = this.addressField.getText();
		this.initialize(minecraftClient, i, j);
		this.addressField.setText(string);
	}

	private void saveAndClose() {
		this.field_2460.address = this.addressField.getText();
		this.field_2461.confirmResult(true, 0);
	}

	@Override
	public void onClosed() {
		this.client.keyboard.enableRepeatEvents(false);
		this.client.field_1690.lastServer = this.addressField.getText();
		this.client.field_1690.write();
	}

	private void onAddressFieldChanged() {
		this.selectServerButton.enabled = !this.addressField.getText().isEmpty() && this.addressField.getText().split(":").length > 0;
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, I18n.translate("selectServer.direct"), this.screenWidth / 2, 20, 16777215);
		this.drawString(this.fontRenderer, I18n.translate("addServer.enterIp"), this.screenWidth / 2 - 100, 100, 10526880);
		this.addressField.draw(i, j, f);
		super.draw(i, j, f);
	}
}
