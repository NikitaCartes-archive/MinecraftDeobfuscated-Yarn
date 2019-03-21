package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextComponent;

@Environment(EnvType.CLIENT)
public class CloseWorldScreen extends Screen {
	public CloseWorldScreen(TextComponent textComponent) {
		super(textComponent);
	}

	@Override
	public boolean doesEscapeKeyClose() {
		return false;
	}

	@Override
	public void render(int i, int j, float f) {
		this.drawTextureBackground(0);
		this.drawStringCentered(this.fontRenderer, this.title.getFormattedText(), this.screenWidth / 2, 70, 16777215);
		super.render(i, j, f);
	}
}
