package net.minecraft.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.options.ServerEntry;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class DirectConnectServerScreen extends Screen {
	private ButtonWidget selectServerButton;
	private final Screen parent;
	private final ServerEntry serverEntry;
	private TextFieldWidget addressField;

	public DirectConnectServerScreen(Screen screen, ServerEntry serverEntry) {
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
		this.selectServerButton = this.addButton(new ButtonWidget(0, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.translate("selectServer.select")) {
			@Override
			public void onPressed(double d, double e) {
				DirectConnectServerScreen.this.saveAndClose();
			}
		});
		this.addButton(new ButtonWidget(1, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.translate("gui.cancel")) {
			@Override
			public void onPressed(double d, double e) {
				DirectConnectServerScreen.this.parent.confirmResult(false, 0);
			}
		});
		this.addressField = new TextFieldWidget(2, this.fontRenderer, this.width / 2 - 100, 116, 200, 20);
		this.addressField.setMaxLength(128);
		this.addressField.setFocused(true);
		this.addressField.setText(this.client.options.lastServer);
		this.listeners.add(this.addressField);
		this.setFocused(this.addressField);
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

	@Override
	public boolean charTyped(char c, int i) {
		if (this.addressField.charTyped(c, i)) {
			this.onAddressFieldChanged();
			return true;
		} else {
			return false;
		}
	}

	private void onAddressFieldChanged() {
		this.selectServerButton.enabled = !this.addressField.getText().isEmpty() && this.addressField.getText().split(":").length > 0;
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i != 257 && i != 335) {
			if (super.keyPressed(i, j, k)) {
				this.onAddressFieldChanged();
				return true;
			} else {
				return false;
			}
		} else {
			if (this.selectServerButton.enabled) {
				this.saveAndClose();
			}

			return true;
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, I18n.translate("selectServer.direct"), this.width / 2, 20, 16777215);
		this.drawString(this.fontRenderer, I18n.translate("addServer.enterIp"), this.width / 2 - 100, 100, 10526880);
		this.addressField.render(i, j, f);
		super.draw(i, j, f);
	}
}
