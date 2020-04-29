package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class DirectConnectScreen extends Screen {
	private ButtonWidget selectServerButton;
	private final ServerInfo serverEntry;
	private TextFieldWidget addressField;
	private final BooleanConsumer callback;
	private final Screen parent;

	public DirectConnectScreen(Screen parent, BooleanConsumer callback, ServerInfo server) {
		super(new TranslatableText("selectServer.direct"));
		this.parent = parent;
		this.serverEntry = server;
		this.callback = callback;
	}

	@Override
	public void tick() {
		this.addressField.tick();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.getFocused() != this.addressField || keyCode != 257 && keyCode != 335) {
			return super.keyPressed(keyCode, scanCode, modifiers);
		} else {
			this.saveAndClose();
			return true;
		}
	}

	@Override
	protected void init() {
		this.client.keyboard.enableRepeatEvents(true);
		this.selectServerButton = this.addButton(
			new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20, new TranslatableText("selectServer.select"), buttonWidget -> this.saveAndClose())
		);
		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, ScreenTexts.CANCEL, buttonWidget -> this.callback.accept(false)));
		this.addressField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 116, 200, 20, new TranslatableText("addServer.enterIp"));
		this.addressField.setMaxLength(128);
		this.addressField.setSelected(true);
		this.addressField.setText(this.client.options.lastServer);
		this.addressField.setChangedListener(text -> this.onAddressFieldChanged());
		this.children.add(this.addressField);
		this.setInitialFocus(this.addressField);
		this.onAddressFieldChanged();
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		String string = this.addressField.getText();
		this.init(client, width, height);
		this.addressField.setText(string);
	}

	private void saveAndClose() {
		this.serverEntry.address = this.addressField.getText();
		this.callback.accept(true);
	}

	@Override
	public void onClose() {
		this.client.openScreen(this.parent);
	}

	@Override
	public void removed() {
		this.client.keyboard.enableRepeatEvents(false);
		this.client.options.lastServer = this.addressField.getText();
		this.client.options.write();
	}

	private void onAddressFieldChanged() {
		String string = this.addressField.getText();
		this.selectServerButton.active = !string.isEmpty() && string.split(":").length > 0 && string.indexOf(32) == -1;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.drawStringWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 20, 16777215);
		this.drawString(matrices, this.textRenderer, I18n.translate("addServer.enterIp"), this.width / 2 - 100, 100, 10526880);
		this.addressField.render(matrices, mouseX, mouseY, delta);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
