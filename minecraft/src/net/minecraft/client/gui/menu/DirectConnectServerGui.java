package net.minecraft.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.options.ServerEntry;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class DirectConnectServerGui extends Gui {
	private ButtonWidget field_2462;
	private final Gui field_2461;
	private final ServerEntry field_2460;
	private TextFieldWidget field_2463;

	public DirectConnectServerGui(Gui gui, ServerEntry serverEntry) {
		this.field_2461 = gui;
		this.field_2460 = serverEntry;
	}

	@Override
	public void update() {
		this.field_2463.tick();
	}

	@Override
	protected void onInitialized() {
		this.client.keyboard.enableRepeatEvents(true);
		this.field_2462 = this.addButton(new ButtonWidget(0, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.translate("selectServer.select")) {
			@Override
			public void onPressed(double d, double e) {
				DirectConnectServerGui.this.method_2167();
			}
		});
		this.addButton(new ButtonWidget(1, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.translate("gui.cancel")) {
			@Override
			public void onPressed(double d, double e) {
				DirectConnectServerGui.this.field_2461.handle(false, 0);
			}
		});
		this.field_2463 = new TextFieldWidget(2, this.fontRenderer, this.width / 2 - 100, 116, 200, 20);
		this.field_2463.setMaxLength(128);
		this.field_2463.setFocused(true);
		this.field_2463.setText(this.client.field_1690.lastServer);
		this.listeners.add(this.field_2463);
		this.setFocused(this.field_2463);
		this.method_2169();
	}

	@Override
	public void onScaleChanged(MinecraftClient minecraftClient, int i, int j) {
		String string = this.field_2463.getText();
		this.initialize(minecraftClient, i, j);
		this.field_2463.setText(string);
	}

	private void method_2167() {
		this.field_2460.address = this.field_2463.getText();
		this.field_2461.handle(true, 0);
	}

	@Override
	public void onClosed() {
		this.client.keyboard.enableRepeatEvents(false);
		this.client.field_1690.lastServer = this.field_2463.getText();
		this.client.field_1690.write();
	}

	@Override
	public boolean charTyped(char c, int i) {
		if (this.field_2463.charTyped(c, i)) {
			this.method_2169();
			return true;
		} else {
			return false;
		}
	}

	private void method_2169() {
		this.field_2462.enabled = !this.field_2463.getText().isEmpty() && this.field_2463.getText().split(":").length > 0;
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i != 257 && i != 335) {
			if (super.keyPressed(i, j, k)) {
				this.method_2169();
				return true;
			} else {
				return false;
			}
		} else {
			if (this.field_2462.enabled) {
				this.method_2167();
			}

			return true;
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, I18n.translate("selectServer.direct"), this.width / 2, 20, 16777215);
		this.drawString(this.fontRenderer, I18n.translate("addServer.enterIp"), this.width / 2 - 100, 100, 10526880);
		this.field_2463.render(i, j, f);
		super.draw(i, j, f);
	}
}
