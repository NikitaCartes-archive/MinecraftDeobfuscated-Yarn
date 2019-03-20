package net.minecraft.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class DownloadingTerrainScreen extends Screen {
	@Override
	public boolean doesEscapeKeyClose() {
		return false;
	}

	@Override
	public void render(int i, int j, float f) {
		this.drawTextureBackground(0);
		this.drawStringCentered(this.fontRenderer, I18n.translate("multiplayer.downloadingTerrain"), this.screenWidth / 2, this.screenHeight / 2 - 50, 16777215);
		super.render(i, j, f);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
