package net.minecraft.client.gui.menu;

import java.net.IDN;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4185;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.options.ServerEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.ChatUtil;

@Environment(EnvType.CLIENT)
public class AddServerScreen extends Screen {
	private class_4185 buttonAdd;
	private final Screen field_2470;
	private final ServerEntry field_2469;
	private TextFieldWidget addressField;
	private TextFieldWidget serverNameField;
	private class_4185 resourcePackOptionButton;
	private final Predicate<String> field_2475 = string -> {
		if (ChatUtil.isEmpty(string)) {
			return true;
		} else {
			String[] strings = string.split(":");
			if (strings.length == 0) {
				return true;
			} else {
				try {
					String string2 = IDN.toASCII(strings[0]);
					return true;
				} catch (IllegalArgumentException var3) {
					return false;
				}
			}
		}
	};

	public AddServerScreen(Screen screen, ServerEntry serverEntry) {
		this.field_2470 = screen;
		this.field_2469 = serverEntry;
	}

	@Override
	public void update() {
		this.serverNameField.tick();
		this.addressField.tick();
	}

	@Override
	protected void onInitialized() {
		this.client.keyboard.enableRepeatEvents(true);
		this.serverNameField = new TextFieldWidget(this.fontRenderer, this.screenWidth / 2 - 100, 66, 200, 20);
		this.serverNameField.setFocused(true);
		this.serverNameField.setText(this.field_2469.name);
		this.serverNameField.setChangedListener(this::method_2171);
		this.listeners.add(this.serverNameField);
		this.addressField = new TextFieldWidget(this.fontRenderer, this.screenWidth / 2 - 100, 106, 200, 20);
		this.addressField.setMaxLength(128);
		this.addressField.setText(this.field_2469.address);
		this.addressField.method_1890(this.field_2475);
		this.addressField.setChangedListener(this::method_2171);
		this.listeners.add(this.addressField);
		this.resourcePackOptionButton = this.addButton(
			new class_4185(
				this.screenWidth / 2 - 100,
				this.screenHeight / 4 + 72,
				I18n.translate("addServer.resourcePack") + ": " + this.field_2469.getResourcePack().method_2997().getFormattedText()
			) {
				@Override
				public void method_1826() {
					AddServerScreen.this.field_2469
						.setResourcePackState(
							ServerEntry.ResourcePackState.values()[(AddServerScreen.this.field_2469.getResourcePack().ordinal() + 1) % ServerEntry.ResourcePackState.values().length]
						);
					AddServerScreen.this.resourcePackOptionButton
						.setText(I18n.translate("addServer.resourcePack") + ": " + AddServerScreen.this.field_2469.getResourcePack().method_2997().getFormattedText());
				}
			}
		);
		this.buttonAdd = this.addButton(new class_4185(this.screenWidth / 2 - 100, this.screenHeight / 4 + 96 + 18, I18n.translate("addServer.add")) {
			@Override
			public void method_1826() {
				AddServerScreen.this.addAndClose();
			}
		});
		this.addButton(new class_4185(this.screenWidth / 2 - 100, this.screenHeight / 4 + 120 + 18, I18n.translate("gui.cancel")) {
			@Override
			public void method_1826() {
				AddServerScreen.this.field_2470.confirmResult(false, 0);
			}
		});
		this.close();
	}

	@Override
	public void onScaleChanged(MinecraftClient minecraftClient, int i, int j) {
		String string = this.addressField.getText();
		String string2 = this.serverNameField.getText();
		this.initialize(minecraftClient, i, j);
		this.addressField.setText(string);
		this.serverNameField.setText(string2);
	}

	private void method_2171(String string) {
		this.close();
	}

	@Override
	public void onClosed() {
		this.client.keyboard.enableRepeatEvents(false);
	}

	private void addAndClose() {
		this.field_2469.name = this.serverNameField.getText();
		this.field_2469.address = this.addressField.getText();
		this.field_2470.confirmResult(true, 0);
	}

	@Override
	public void close() {
		this.buttonAdd.enabled = !this.addressField.getText().isEmpty()
			&& this.addressField.getText().split(":").length > 0
			&& !this.serverNameField.getText().isEmpty();
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, I18n.translate("addServer.title"), this.screenWidth / 2, 17, 16777215);
		this.drawString(this.fontRenderer, I18n.translate("addServer.enterName"), this.screenWidth / 2 - 100, 53, 10526880);
		this.drawString(this.fontRenderer, I18n.translate("addServer.enterIp"), this.screenWidth / 2 - 100, 94, 10526880);
		this.serverNameField.draw(i, j, f);
		this.addressField.draw(i, j, f);
		super.draw(i, j, f);
	}
}
