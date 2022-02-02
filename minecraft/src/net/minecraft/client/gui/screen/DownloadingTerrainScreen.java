package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class DownloadingTerrainScreen extends Screen {
	private static final Text TEXT = new TranslatableText("multiplayer.downloadingTerrain");
	private static final long MIN_LOAD_TIME_MS = 2000L;
	private boolean ready = false;
	private boolean closeOnNextTick = false;
	private final long loadStartTime = System.currentTimeMillis();

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
		boolean bl = this.closeOnNextTick || System.currentTimeMillis() > this.loadStartTime + 2000L;
		if (bl && this.client != null && this.client.player != null) {
			BlockPos blockPos = this.client.player.getBlockPos();
			boolean bl2 = this.client.world != null && this.client.world.isOutOfHeightLimit(blockPos.getY());
			if (bl2 || this.client.worldRenderer.isRenderingReady(blockPos)) {
				this.close();
			}

			if (this.ready) {
				this.closeOnNextTick = true;
			}
		}
	}

	public void setReady() {
		this.ready = true;
	}

	@Override
	public boolean shouldPause() {
		return false;
	}
}
