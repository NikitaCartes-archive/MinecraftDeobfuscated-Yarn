package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkStatus;

@Environment(EnvType.CLIENT)
public class LevelLoadingScreen extends Screen {
	private final WorldGenerationProgressTracker progressProvider;
	private long field_19101 = -1L;
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
		NarratorManager.INSTANCE.narrate(I18n.translate("narrator.loading.done"));
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		String string = MathHelper.clamp(this.progressProvider.getProgressPercentage(), 0, 100) + "%";
		long l = Util.getMeasuringTimeMs();
		if (l - this.field_19101 > 2000L) {
			this.field_19101 = l;
			NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.loading", string).getString());
		}

		int i = this.width / 2;
		int j = this.height / 2;
		int k = 30;
		drawChunkMap(this.progressProvider, i, j + 30, 2, 0);
		this.drawCenteredString(this.textRenderer, string, i, j - 9 / 2 - 30, 16777215);
	}

	public static void drawChunkMap(WorldGenerationProgressTracker progressProvider, int centerX, int centerY, int chunkSize, int i) {
		int j = chunkSize + i;
		int k = progressProvider.getCenterSize();
		int l = k * j - i;
		int m = progressProvider.getSize();
		int n = m * j - i;
		int o = centerX - n / 2;
		int p = centerY - n / 2;
		int q = l / 2 + 1;
		int r = -16772609;
		if (i != 0) {
			fill(centerX - q, centerY - q, centerX - q + 1, centerY + q, -16772609);
			fill(centerX + q - 1, centerY - q, centerX + q, centerY + q, -16772609);
			fill(centerX - q, centerY - q, centerX + q, centerY - q + 1, -16772609);
			fill(centerX - q, centerY + q - 1, centerX + q, centerY + q, -16772609);
		}

		for (int s = 0; s < m; s++) {
			for (int t = 0; t < m; t++) {
				ChunkStatus chunkStatus = progressProvider.getChunkStatus(s, t);
				int u = o + s * j;
				int v = p + t * j;
				fill(u, v, u + chunkSize, v + chunkSize, STATUS_TO_COLOR.getInt(chunkStatus) | 0xFF000000);
			}
		}
	}
}
