package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkStatus;

@Environment(EnvType.CLIENT)
public class LevelLoadingScreen extends Screen {
	private static final long field_32246 = 2000L;
	private final WorldGenerationProgressTracker progressProvider;
	private long field_19101 = -1L;
	private boolean field_33810;
	private static final Object2IntMap<ChunkStatus> STATUS_TO_COLOR = Util.make(new Object2IntOpenHashMap<>(), map -> {
		map.defaultReturnValue(0);
		map.put(ChunkStatus.EMPTY, 5526612);
		map.put(ChunkStatus.STRUCTURE_STARTS, 10066329);
		map.put(ChunkStatus.STRUCTURE_REFERENCES, 6250897);
		map.put(ChunkStatus.BIOMES, 8434258);
		map.put(ChunkStatus.NOISE, 13750737);
		map.put(ChunkStatus.SURFACE, 7497737);
		map.put(ChunkStatus.CARVERS, 7169628);
		map.put(ChunkStatus.LIQUID_CARVERS, 3159410);
		map.put(ChunkStatus.FEATURES, 2213376);
		map.put(ChunkStatus.LIGHT, 13421772);
		map.put(ChunkStatus.SPAWN, 15884384);
		map.put(ChunkStatus.HEIGHTMAPS, 15658734);
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
	public void removed() {
		this.field_33810 = true;
		this.narrateScreenIfNarrationEnabled(true);
	}

	@Override
	protected void addElementNarrations(NarrationMessageBuilder builder) {
		if (this.field_33810) {
			builder.put(NarrationPart.TITLE, new TranslatableText("narrator.loading.done"));
		} else {
			String string = this.getPercentage();
			builder.put(NarrationPart.TITLE, string);
		}
	}

	private String getPercentage() {
		return MathHelper.clamp(this.progressProvider.getProgressPercentage(), 0, 100) + "%";
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		long l = Util.getMeasuringTimeMs();
		if (l - this.field_19101 > 2000L) {
			this.field_19101 = l;
			this.narrateScreenIfNarrationEnabled(true);
		}

		int i = this.width / 2;
		int j = this.height / 2;
		int k = 30;
		drawChunkMap(matrices, this.progressProvider, i, j + 30, 2, 0);
		drawCenteredText(matrices, this.textRenderer, this.getPercentage(), i, j - 9 / 2 - 30, 16777215);
	}

	public static void drawChunkMap(MatrixStack matrices, WorldGenerationProgressTracker progressProvider, int i, int j, int k, int l) {
		int m = k + l;
		int n = progressProvider.getCenterSize();
		int o = n * m - l;
		int p = progressProvider.getSize();
		int q = p * m - l;
		int r = i - q / 2;
		int s = j - q / 2;
		int t = o / 2 + 1;
		int u = -16772609;
		if (l != 0) {
			fill(matrices, i - t, j - t, i - t + 1, j + t, -16772609);
			fill(matrices, i + t - 1, j - t, i + t, j + t, -16772609);
			fill(matrices, i - t, j - t, i + t, j - t + 1, -16772609);
			fill(matrices, i - t, j + t - 1, i + t, j + t, -16772609);
		}

		for (int v = 0; v < p; v++) {
			for (int w = 0; w < p; w++) {
				ChunkStatus chunkStatus = progressProvider.getChunkStatus(v, w);
				int x = r + v * m;
				int y = s + w * m;
				fill(matrices, x, y, x + k, y + k, STATUS_TO_COLOR.getInt(chunkStatus) | 0xFF000000);
			}
		}
	}
}
