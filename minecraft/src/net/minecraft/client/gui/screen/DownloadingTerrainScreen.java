package net.minecraft.client.gui.screen;

import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class DownloadingTerrainScreen extends Screen {
	private static final Text TEXT = Text.translatable("multiplayer.downloadingTerrain");
	private static final long MIN_LOAD_TIME_MS = 30000L;
	private final long loadStartTime;
	private final BooleanSupplier shouldClose;

	public DownloadingTerrainScreen(BooleanSupplier shouldClose) {
		super(NarratorManager.EMPTY);
		this.shouldClose = shouldClose;
		this.loadStartTime = System.currentTimeMillis();
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
		if (this.shouldClose.getAsBoolean() || System.currentTimeMillis() > this.loadStartTime + 30000L) {
			this.close();
		}
	}

	@Override
	public void close() {
		this.client.getNarratorManager().narrate(Text.translatable("narrator.ready_to_play"));
		super.close();
	}

	@Override
	public boolean shouldPause() {
		return false;
	}
}
