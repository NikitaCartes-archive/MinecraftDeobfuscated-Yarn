package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class FatalErrorScreen extends Screen {
	private final String message;

	public FatalErrorScreen(Text text, String message) {
		super(text);
		this.message = message;
	}

	@Override
	protected void init() {
		super.init();
		this.addButton(new ButtonWidget(this.width / 2 - 100, 140, 200, 20, I18n.translate("gui.cancel"), buttonWidget -> this.minecraft.openScreen(null)));
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.fillGradient(0, 0, this.width, this.height, -12574688, -11530224);
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 90, 16777215);
		this.drawCenteredString(this.font, this.message, this.width / 2, 110, 16777215);
		super.render(mouseX, mouseY, delta);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
}
