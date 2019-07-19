package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class SaveLevelScreen extends Screen {
	public SaveLevelScreen(Text text) {
		super(text);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderDirtBackground(0);
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 70, 16777215);
		super.render(mouseX, mouseY, delta);
	}
}
