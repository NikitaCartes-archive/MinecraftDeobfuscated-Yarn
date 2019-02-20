package net.minecraft.client.gui.menu;

import java.net.IDN;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.options.ServerEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.ChatUtil;

@Environment(EnvType.CLIENT)
public class AddServerScreen extends Screen {
	private ButtonWidget buttonAdd;
	private final Screen parent;
	private final ServerEntry serverEntry;
	private TextFieldWidget addressField;
	private TextFieldWidget serverNameField;
	private ButtonWidget resourcePackOptionButton;
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
		this.parent = screen;
		this.serverEntry = serverEntry;
	}

	@Override
	public void update() {
		this.serverNameField.tick();
		this.addressField.tick();
	}

	@Override
	public GuiEventListener getFocused() {
		return this.addressField.isFocused() ? this.addressField : this.serverNameField;
	}

	@Override
	protected void onInitialized() {
		this.client.keyboard.enableRepeatEvents(true);
		this.buttonAdd = this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96 + 18, I18n.translate("addServer.add")) {
			@Override
			public void onPressed(double d, double e) {
				AddServerScreen.this.addAndClose();
			}
		});
		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 18, I18n.translate("gui.cancel")) {
			@Override
			public void onPressed(double d, double e) {
				AddServerScreen.this.parent.confirmResult(false, 0);
			}
		});
		this.resourcePackOptionButton = this.addButton(
			new ButtonWidget(
				this.width / 2 - 100,
				this.height / 4 + 72,
				I18n.translate("addServer.resourcePack") + ": " + this.serverEntry.getResourcePack().getComponent().getFormattedText()
			) {
				@Override
				public void onPressed(double d, double e) {
					AddServerScreen.this.serverEntry
						.setResourcePackState(
							ServerEntry.ResourcePackState.values()[(AddServerScreen.this.serverEntry.getResourcePack().ordinal() + 1)
								% ServerEntry.ResourcePackState.values().length]
						);
					AddServerScreen.this.resourcePackOptionButton
						.setText(I18n.translate("addServer.resourcePack") + ": " + AddServerScreen.this.serverEntry.getResourcePack().getComponent().getFormattedText());
				}
			}
		);
		this.addressField = new TextFieldWidget(this.fontRenderer, this.width / 2 - 100, 106, 200, 20) {
			@Override
			public void setFocused(boolean bl) {
				super.setFocused(bl);
				if (bl) {
					AddServerScreen.this.serverNameField.setFocused(false);
				}
			}
		};
		this.addressField.setMaxLength(128);
		this.addressField.setText(this.serverEntry.address);
		this.addressField.method_1890(this.field_2475);
		this.addressField.setChangedListener(this::method_2171);
		this.listeners.add(this.addressField);
		this.serverNameField = new TextFieldWidget(this.fontRenderer, this.width / 2 - 100, 66, 200, 20) {
			@Override
			public void setFocused(boolean bl) {
				super.setFocused(bl);
				if (bl) {
					AddServerScreen.this.addressField.setFocused(false);
				}
			}
		};
		this.serverNameField.setFocused(true);
		this.serverNameField.setText(this.serverEntry.name);
		this.serverNameField.setChangedListener(this::method_2171);
		this.listeners.add(this.serverNameField);
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
		this.serverEntry.name = this.serverNameField.getText();
		this.serverEntry.address = this.addressField.getText();
		this.parent.confirmResult(true, 0);
	}

	@Override
	public void close() {
		this.buttonAdd.enabled = !this.addressField.getText().isEmpty()
			&& this.addressField.getText().split(":").length > 0
			&& !this.serverNameField.getText().isEmpty();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 258) {
			if (this.serverNameField.isFocused()) {
				this.addressField.setFocused(true);
			} else {
				this.serverNameField.setFocused(true);
			}

			return true;
		} else if ((i == 257 || i == 335) && this.buttonAdd.enabled) {
			this.addAndClose();
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	@Override
	public void method_18326(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, I18n.translate("addServer.title"), this.width / 2, 17, 16777215);
		this.drawString(this.fontRenderer, I18n.translate("addServer.enterName"), this.width / 2 - 100, 53, 10526880);
		this.drawString(this.fontRenderer, I18n.translate("addServer.enterIp"), this.width / 2 - 100, 94, 10526880);
		this.serverNameField.method_18326(i, j, f);
		this.addressField.method_18326(i, j, f);
		super.method_18326(i, j, f);
	}
}
