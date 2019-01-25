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
	private ButtonWidget field_2472;
	private final Screen parent;
	private final ServerEntry field_2469;
	private TextFieldWidget field_2474;
	private TextFieldWidget field_2471;
	private ButtonWidget field_2473;
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
		this.field_2469 = serverEntry;
	}

	@Override
	public void update() {
		this.field_2471.tick();
		this.field_2474.tick();
	}

	@Override
	public GuiEventListener getFocused() {
		return this.field_2474.isFocused() ? this.field_2474 : this.field_2471;
	}

	@Override
	protected void onInitialized() {
		this.client.keyboard.enableRepeatEvents(true);
		this.field_2472 = this.addButton(new ButtonWidget(0, this.width / 2 - 100, this.height / 4 + 96 + 18, I18n.translate("addServer.add")) {
			@Override
			public void onPressed(double d, double e) {
				AddServerScreen.this.method_2172();
			}
		});
		this.addButton(new ButtonWidget(1, this.width / 2 - 100, this.height / 4 + 120 + 18, I18n.translate("gui.cancel")) {
			@Override
			public void onPressed(double d, double e) {
				AddServerScreen.this.parent.confirmResult(false, 0);
			}
		});
		this.field_2473 = this.addButton(
			new ButtonWidget(
				2,
				this.width / 2 - 100,
				this.height / 4 + 72,
				I18n.translate("addServer.resourcePack") + ": " + this.field_2469.getResourcePack().getComponent().getFormattedText()
			) {
				@Override
				public void onPressed(double d, double e) {
					AddServerScreen.this.field_2469
						.setResourcePackState(
							ServerEntry.ResourcePackState.values()[(AddServerScreen.this.field_2469.getResourcePack().ordinal() + 1) % ServerEntry.ResourcePackState.values().length]
						);
					AddServerScreen.this.field_2473.text = I18n.translate("addServer.resourcePack")
						+ ": "
						+ AddServerScreen.this.field_2469.getResourcePack().getComponent().getFormattedText();
				}
			}
		);
		this.field_2474 = new TextFieldWidget(1, this.fontRenderer, this.width / 2 - 100, 106, 200, 20) {
			@Override
			public void setFocused(boolean bl) {
				super.setFocused(bl);
				if (bl) {
					AddServerScreen.this.field_2471.setFocused(false);
				}
			}
		};
		this.field_2474.setMaxLength(128);
		this.field_2474.setText(this.field_2469.address);
		this.field_2474.method_1890(this.field_2475);
		this.field_2474.setChangedListener(this::method_2171);
		this.listeners.add(this.field_2474);
		this.field_2471 = new TextFieldWidget(0, this.fontRenderer, this.width / 2 - 100, 66, 200, 20) {
			@Override
			public void setFocused(boolean bl) {
				super.setFocused(bl);
				if (bl) {
					AddServerScreen.this.field_2474.setFocused(false);
				}
			}
		};
		this.field_2471.setFocused(true);
		this.field_2471.setText(this.field_2469.name);
		this.field_2471.setChangedListener(this::method_2171);
		this.listeners.add(this.field_2471);
		this.close();
	}

	@Override
	public void onScaleChanged(MinecraftClient minecraftClient, int i, int j) {
		String string = this.field_2474.getText();
		String string2 = this.field_2471.getText();
		this.initialize(minecraftClient, i, j);
		this.field_2474.setText(string);
		this.field_2471.setText(string2);
	}

	private void method_2171(int i, String string) {
		this.close();
	}

	@Override
	public void onClosed() {
		this.client.keyboard.enableRepeatEvents(false);
	}

	private void method_2172() {
		this.field_2469.name = this.field_2471.getText();
		this.field_2469.address = this.field_2474.getText();
		this.parent.confirmResult(true, 0);
	}

	@Override
	public void close() {
		this.field_2472.enabled = !this.field_2474.getText().isEmpty() && this.field_2474.getText().split(":").length > 0 && !this.field_2471.getText().isEmpty();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 258) {
			if (this.field_2471.isFocused()) {
				this.field_2474.setFocused(true);
			} else {
				this.field_2471.setFocused(true);
			}

			return true;
		} else if ((i == 257 || i == 335) && this.field_2472.enabled) {
			this.method_2172();
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, I18n.translate("addServer.title"), this.width / 2, 17, 16777215);
		this.drawString(this.fontRenderer, I18n.translate("addServer.enterName"), this.width / 2 - 100, 53, 10526880);
		this.drawString(this.fontRenderer, I18n.translate("addServer.enterIp"), this.width / 2 - 100, 94, 10526880);
		this.field_2471.render(i, j, f);
		this.field_2474.render(i, j, f);
		super.draw(i, j, f);
	}
}
