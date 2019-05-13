package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class SaveLevelScreen extends Screen {
	public SaveLevelScreen(Component component) {
		super(component);
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
