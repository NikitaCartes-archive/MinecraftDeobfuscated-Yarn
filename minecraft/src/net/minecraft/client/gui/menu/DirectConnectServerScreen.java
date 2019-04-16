package net.minecraft.client.gui.menu;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
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
	private final ServerEntry serverEntry;
	private TextFieldWidget addressField;
	private final BooleanConsumer callback;

	public DirectConnectServerScreen(BooleanConsumer booleanConsumer, ServerEntry serverEntry) {
		super(new TranslatableTextComponent("selectServer.direct"));
		this.serverEntry = serverEntry;
		this.callback = booleanConsumer;
	}

	@Override
	public void tick() {
		this.addressField.tick();
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
		this.addressField.setFocused(true);
		this.addressField.setText(this.minecraft.options.lastServer);
		this.addressField.setChangedListener(string -> this.onAddressFieldChanged());
		this.children.add(this.addressField);
		this.method_20085(this.addressField);
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
		this.selectServerButton.active = !this.addressField.getText().isEmpty() && this.addressField.getText().split(":").length > 0;
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 20, 16777215);
		this.drawString(this.font, I18n.translate("addServer.enterIp"), this.width / 2 - 100, 100, 10526880);
		this.addressField.render(i, j, f);
		super.render(i, j, f);
	}
}
