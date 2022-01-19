package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class DownloadingTerrainScreen extends Screen {
	private static final Text TEXT = new TranslatableText("multiplayer.downloadingTerrain");
	private static final long field_36365 = 2000L;
	private boolean field_36366 = false;
	private boolean field_36367 = false;
	private final long field_36368 = System.currentTimeMillis();

	public DownloadingTerrainScreen() {
		super(NarratorManager.EMPTY);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackgroundTexture(0);
		drawCenteredText(matrices, this.textRenderer, TEXT, this.width / 2, this.height / 2 - 50, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public void tick() {
		if ((this.field_36367 || System.currentTimeMillis() > this.field_36368 + 2000L) && this.client.worldRenderer.method_40050(this.client.player.getBlockPos())) {
			this.onClose();
		}

		if (this.field_36366) {
			this.field_36367 = true;
		}
	}

	public void method_40040() {
		this.field_36366 = true;
	}

	@Override
	public boolean shouldPause() {
		return false;
	}
}
