package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CloseWorldGui extends Gui {
	private final String message;

	public CloseWorldGui(String string) {
		this.message = string;
	}

	@Override
	public boolean doesEscapeKeyClose() {
		return false;
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawTextureBackground(0);
		this.drawStringCentered(this.fontRenderer, this.message, this.width / 2, 70, 16777215);
		super.draw(i, j, f);
	}
}
