package net.minecraft.client.gui;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkStatus;

@Environment(EnvType.CLIENT)
public class WorldGenerationProgressScreen extends Screen {
	private final WorldGenerationProgressTracker progressProvider;
	private static final Object2IntMap<ChunkStatus> STATUS_TO_COLOR = SystemUtil.consume(new Object2IntOpenHashMap<>(), object2IntOpenHashMap -> {
		object2IntOpenHashMap.defaultReturnValue(0);
		object2IntOpenHashMap.put(ChunkStatus.EMPTY, 5526612);
		object2IntOpenHashMap.put(ChunkStatus.STRUCTURE_STARTS, 10066329);
		object2IntOpenHashMap.put(ChunkStatus.STRUCTURE_REFERENCES, 6250897);
		object2IntOpenHashMap.put(ChunkStatus.BIOMES, 8434258);
		object2IntOpenHashMap.put(ChunkStatus.NOISE, 13750737);
		object2IntOpenHashMap.put(ChunkStatus.SURFACE, 7497737);
		object2IntOpenHashMap.put(ChunkStatus.CARVERS, 7169628);
		object2IntOpenHashMap.put(ChunkStatus.LIQUID_CARVERS, 3159410);
		object2IntOpenHashMap.put(ChunkStatus.FEATURES, 2213376);
		object2IntOpenHashMap.put(ChunkStatus.LIGHT, 13421772);
		object2IntOpenHashMap.put(ChunkStatus.SPAWN, 15884384);
		object2IntOpenHashMap.put(ChunkStatus.HEIGHTMAPS, 15658734);
		object2IntOpenHashMap.put(ChunkStatus.FULL, 16777215);
	});

	public WorldGenerationProgressScreen(WorldGenerationProgressTracker worldGenerationProgressTracker) {
		this.progressProvider = worldGenerationProgressTracker;
	}

	@Override
	public boolean doesEscapeKeyClose() {
		return false;
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		int k = this.screenWidth / 2;
		int l = this.screenHeight / 2;
		int m = 30;
		drawChunkMap(this.progressProvider, k, l + 30, 2, 0);
		this.drawStringCentered(this.fontRenderer, MathHelper.clamp(this.progressProvider.getProgressPercentage(), 0, 100) + "%", k, l - 9 / 2 - 30, 16777215);
	}

	public static void drawChunkMap(WorldGenerationProgressTracker worldGenerationProgressTracker, int i, int j, int k, int l) {
		int m = k + l;
		int n = worldGenerationProgressTracker.getCenterSize();
		int o = n * m - l;
		int p = worldGenerationProgressTracker.getSize();
		int q = p * m - l;
		int r = i - q / 2;
		int s = j - q / 2;
		int t = o / 2 + 1;
		int u = -16772609;
		if (l != 0) {
			drawRect(i - t, j - t, i - t + 1, j + t, -16772609);
			drawRect(i + t - 1, j - t, i + t, j + t, -16772609);
			drawRect(i - t, j - t, i + t, j - t + 1, -16772609);
			drawRect(i - t, j + t - 1, i + t, j + t, -16772609);
		}

		for (int v = 0; v < p; v++) {
			for (int w = 0; w < p; w++) {
				ChunkStatus chunkStatus = worldGenerationProgressTracker.getChunkStatus(v, w);
				int x = r + v * m;
				int y = s + w * m;
				drawRect(x, y, x + k, y + k, STATUS_TO_COLOR.getInt(chunkStatus) | 0xFF000000);
			}
		}
	}
}
