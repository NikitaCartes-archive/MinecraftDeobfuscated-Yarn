package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class DownloadingTerrainScreen extends Screen {
	public DownloadingTerrainScreen() {
		super(NarratorManager.EMPTY);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderDirtBackground(0);
		this.drawCenteredString(matrices, this.textRenderer, I18n.translate("multiplayer.downloadingTerrain"), this.width / 2, this.height / 2 - 50, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
