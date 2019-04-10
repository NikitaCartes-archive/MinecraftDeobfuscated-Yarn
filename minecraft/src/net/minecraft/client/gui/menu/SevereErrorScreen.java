package net.minecraft.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;

@Environment(EnvType.CLIENT)
public class SevereErrorScreen extends Screen {
	private final String message;

	public SevereErrorScreen(TextComponent textComponent, String string) {
		super(textComponent);
		this.message = string;
	}

	@Override
	protected void init() {
		super.init();
		this.addButton(new ButtonWidget(this.width / 2 - 100, 140, 200, 20, I18n.translate("gui.cancel"), buttonWidget -> this.minecraft.openScreen(null)));
	}

	@Override
	public void render(int i, int j, float f) {
		this.fillGradient(0, 0, this.width, this.height, -12574688, -11530224);
		this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 90, 16777215);
		this.drawCenteredString(this.font, this.message, this.width / 2, 110, 16777215);
		super.render(i, j, f);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
}
