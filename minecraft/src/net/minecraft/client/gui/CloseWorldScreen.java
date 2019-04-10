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
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderDirtBackground(0);
		this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 70, 16777215);
		super.render(i, j, f);
	}
}
