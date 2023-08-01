package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class DownloadingTerrainScreen extends Screen {
	private static final Text TEXT = Text.translatable("multiplayer.downloadingTerrain");
	private static final long MIN_LOAD_TIME_MS = 30000L;
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
	protected boolean hasUsageText() {
		return false;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, TEXT, this.width / 2, this.height / 2 - 50, 16777215);
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackgroundTexture(context);
	}

	@Override
	public void tick() {
		if (System.currentTimeMillis() > this.loadStartTime + 30000L) {
			this.close();
		} else {
			if (this.closeOnNextTick) {
				if (this.client.player == null) {
					return;
				}

				BlockPos blockPos = this.client.player.getBlockPos();
				boolean bl = this.client.world != null && this.client.world.isOutOfHeightLimit(blockPos.getY());
				if (bl || this.client.worldRenderer.isRenderingReady(blockPos) || this.client.player.isSpectator() || !this.client.player.isAlive()) {
					this.close();
				}
			} else {
				this.closeOnNextTick = this.ready;
			}
		}
	}

	@Override
	public void close() {
		this.client.getNarratorManager().narrate(Text.translatable("narrator.ready_to_play"));
		super.close();
	}

	public void setReady() {
		this.ready = true;
	}

	@Override
	public boolean shouldPause() {
		return false;
	}
}
