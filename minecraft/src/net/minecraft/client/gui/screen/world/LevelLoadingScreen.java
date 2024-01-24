package net.minecraft.client.gui.screen.world;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.server.WorldGenerationProgressTracker;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkStatus;

@Environment(EnvType.CLIENT)
public class LevelLoadingScreen extends Screen {
	private static final long NARRATION_DELAY = 2000L;
	private final WorldGenerationProgressTracker progressProvider;
	private long lastNarrationTime = -1L;
	private boolean done;
	private static final Object2IntMap<ChunkStatus> STATUS_TO_COLOR = Util.make(new Object2IntOpenHashMap<>(), map -> {
		map.defaultReturnValue(0);
		map.put(ChunkStatus.EMPTY, 5526612);
		map.put(ChunkStatus.STRUCTURE_STARTS, 10066329);
		map.put(ChunkStatus.STRUCTURE_REFERENCES, 6250897);
		map.put(ChunkStatus.BIOMES, 8434258);
		map.put(ChunkStatus.NOISE, 13750737);
		map.put(ChunkStatus.SURFACE, 7497737);
		map.put(ChunkStatus.CARVERS, 3159410);
		map.put(ChunkStatus.FEATURES, 2213376);
		map.put(ChunkStatus.INITIALIZE_LIGHT, 13421772);
		map.put(ChunkStatus.LIGHT, 16769184);
		map.put(ChunkStatus.SPAWN, 15884384);
		map.put(ChunkStatus.FULL, 16777215);
	});

	public LevelLoadingScreen(WorldGenerationProgressTracker progressProvider) {
		super(NarratorManager.EMPTY);
		this.progressProvider = progressProvider;
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
	public void removed() {
		this.done = true;
		this.narrateScreenIfNarrationEnabled(true);
	}

	@Override
	protected void addElementNarrations(NarrationMessageBuilder builder) {
		if (this.done) {
			builder.put(NarrationPart.TITLE, Text.translatable("narrator.loading.done"));
		} else {
			builder.put(NarrationPart.TITLE, this.getPercentage());
		}
	}

	private Text getPercentage() {
		return Text.translatable("loading.progress", MathHelper.clamp(this.progressProvider.getProgressPercentage(), 0, 100));
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		long l = Util.getMeasuringTimeMs();
		if (l - this.lastNarrationTime > 2000L) {
			this.lastNarrationTime = l;
			this.narrateScreenIfNarrationEnabled(true);
		}

		int i = this.width / 2;
		int j = this.height / 2;
		drawChunkMap(context, this.progressProvider, i, j, 2, 0);
		int k = this.progressProvider.getSize() + 9 + 2;
		context.drawCenteredTextWithShadow(this.textRenderer, this.getPercentage(), i, j - k, 16777215);
	}

	public static void drawChunkMap(DrawContext context, WorldGenerationProgressTracker progressProvider, int centerX, int centerY, int pixelSize, int pixelMargin) {
		int i = pixelSize + pixelMargin;
		int j = progressProvider.getCenterSize();
		int k = j * i - pixelMargin;
		int l = progressProvider.getSize();
		int m = l * i - pixelMargin;
		int n = centerX - m / 2;
		int o = centerY - m / 2;
		int p = k / 2 + 1;
		int q = -16772609;
		context.draw(() -> {
			if (pixelMargin != 0) {
				context.fill(centerX - p, centerY - p, centerX - p + 1, centerY + p, -16772609);
				context.fill(centerX + p - 1, centerY - p, centerX + p, centerY + p, -16772609);
				context.fill(centerX - p, centerY - p, centerX + p, centerY - p + 1, -16772609);
				context.fill(centerX - p, centerY + p - 1, centerX + p, centerY + p, -16772609);
			}

			for (int r = 0; r < l; r++) {
				for (int s = 0; s < l; s++) {
					ChunkStatus chunkStatus = progressProvider.getChunkStatus(r, s);
					int t = n + r * i;
					int u = o + s * i;
					context.fill(t, u, t + pixelSize, u + pixelSize, STATUS_TO_COLOR.getInt(chunkStatus) | 0xFF000000);
				}
			}
		});
	}
}
