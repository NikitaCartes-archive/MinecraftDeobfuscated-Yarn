package net.minecraft.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class DownloadingTerrainGui extends Gui {
	@Override
	public boolean canClose() {
		return false;
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawTextureBackground(0);
		this.drawStringCentered(this.fontRenderer, I18n.translate("multiplayer.downloadingTerrain"), this.width / 2, this.height / 2 - 50, 16777215);
		super.draw(i, j, f);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
