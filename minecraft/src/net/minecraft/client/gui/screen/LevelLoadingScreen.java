package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkStatus;

@Environment(EnvType.CLIENT)
public class LevelLoadingScreen extends Screen {
	private final WorldGenerationProgressTracker progressProvider;
	private long field_19101 = -1L;
	private static final Object2IntMap<ChunkStatus> STATUS_TO_COLOR = SystemUtil.consume(new Object2IntOpenHashMap<>(), object2IntOpenHashMap -> {
		object2IntOpenHashMap.defaultReturnValue(0);
		object2IntOpenHashMap.put(ChunkStatus.field_12798, 5526612);
		object2IntOpenHashMap.put(ChunkStatus.field_16423, 10066329);
		object2IntOpenHashMap.put(ChunkStatus.field_16422, 6250897);
		object2IntOpenHashMap.put(ChunkStatus.field_12794, 8434258);
		object2IntOpenHashMap.put(ChunkStatus.field_12804, 13750737);
		object2IntOpenHashMap.put(ChunkStatus.field_12796, 7497737);
		object2IntOpenHashMap.put(ChunkStatus.field_12801, 7169628);
		object2IntOpenHashMap.put(ChunkStatus.field_12790, 3159410);
		object2IntOpenHashMap.put(ChunkStatus.field_12795, 2213376);
		object2IntOpenHashMap.put(ChunkStatus.field_12805, 13421772);
		object2IntOpenHashMap.put(ChunkStatus.field_12786, 15884384);
		object2IntOpenHashMap.put(ChunkStatus.field_12800, 15658734);
		object2IntOpenHashMap.put(ChunkStatus.field_12803, 16777215);
	});

	public LevelLoadingScreen(WorldGenerationProgressTracker worldGenerationProgressTracker) {
		super(NarratorManager.field_18967);
		this.progressProvider = worldGenerationProgressTracker;
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
	public void render(int i, int j, float f) {
		this.renderBackground();
		String string = MathHelper.clamp(this.progressProvider.getProgressPercentage(), 0, 100) + "%";
		long l = SystemUtil.getMeasuringTimeMs();
		if (l - this.field_19101 > 2000L) {
			this.field_19101 = l;
			NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.loading", string).getString());
		}

		int k = this.width / 2;
		int m = this.height / 2;
		int n = 30;
		drawChunkMap(this.progressProvider, k, m + 30, 2, 0);
		this.drawCenteredString(this.font, string, k, m - 9 / 2 - 30, 16777215);
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
			fill(i - t, j - t, i - t + 1, j + t, -16772609);
			fill(i + t - 1, j - t, i + t, j + t, -16772609);
			fill(i - t, j - t, i + t, j - t + 1, -16772609);
			fill(i - t, j + t - 1, i + t, j + t, -16772609);
		}

		for (int v = 0; v < p; v++) {
			for (int w = 0; w < p; w++) {
				ChunkStatus chunkStatus = worldGenerationProgressTracker.getChunkStatus(v, w);
				int x = r + v * m;
				int y = s + w * m;
				fill(x, y, x + k, y + k, STATUS_TO_COLOR.getInt(chunkStatus) | 0xFF000000);
			}
		}
	}
}
