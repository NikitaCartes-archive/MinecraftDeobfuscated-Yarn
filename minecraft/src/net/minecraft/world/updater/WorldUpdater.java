package net.minecraft.world.updater;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import java.io.File;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1256;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.SystemUtil;
import net.minecraft.world.ChunkSaveHandlerImpl;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldUpdater {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final ThreadFactory UPDATE_THREAD_FACTORY = new ThreadFactoryBuilder().setDaemon(true).build();
	private final String levelName;
	private final WorldSaveHandler worldSaveHandler;
	private final PersistentStateManager persistentStateManager;
	private final Thread updateThread;
	private volatile boolean keepUpgradingChunks = true;
	private volatile boolean isDone = false;
	private volatile float progress;
	private volatile int totalChunkCount;
	private volatile int upgradedChunkCount = 0;
	private volatile int skippedChunkCount = 0;
	private final Object2FloatMap<DimensionType> dimensionProgress = Object2FloatMaps.synchronize(
		new Object2FloatOpenCustomHashMap<>(SystemUtil.identityHashStrategy())
	);
	private volatile TextComponent status = new TranslatableTextComponent("optimizeWorld.stage.counting");

	public WorldUpdater(String string, LevelStorage levelStorage, LevelProperties levelProperties) {
		this.levelName = levelProperties.getLevelName();
		this.worldSaveHandler = levelStorage.method_242(string, null);
		this.worldSaveHandler.saveWorld(levelProperties);
		this.persistentStateManager = new PersistentStateManager(this.worldSaveHandler);
		this.updateThread = UPDATE_THREAD_FACTORY.newThread(this::updateWorld);
		this.updateThread.setUncaughtExceptionHandler(this::method_5398);
		this.updateThread.start();
	}

	private void method_5398(Thread thread, Throwable throwable) {
		LOGGER.error("Error upgrading world", throwable);
		this.keepUpgradingChunks = false;
		this.status = new TranslatableTextComponent("optimizeWorld.stage.failed");
	}

	public void cancel() {
		this.keepUpgradingChunks = false;

		try {
			this.updateThread.join();
		} catch (InterruptedException var2) {
		}
	}

	private void updateWorld() {
		File file = this.worldSaveHandler.getWorldDir();
		class_1256 lv = new class_1256(file);
		Builder<DimensionType, ChunkSaveHandlerImpl> builder = ImmutableMap.builder();

		for (DimensionType dimensionType : DimensionType.getAll()) {
			builder.put(dimensionType, new ChunkSaveHandlerImpl(dimensionType.getFile(file), this.worldSaveHandler.getDataFixer()));
		}

		Map<DimensionType, ChunkSaveHandlerImpl> map = builder.build();
		long l = SystemUtil.getMeasuringTimeMs();
		this.totalChunkCount = 0;
		Builder<DimensionType, ListIterator<ChunkPos>> builder2 = ImmutableMap.builder();

		for (DimensionType dimensionType2 : DimensionType.getAll()) {
			List<ChunkPos> list = lv.method_5391(dimensionType2);
			builder2.put(dimensionType2, list.listIterator());
			this.totalChunkCount = this.totalChunkCount + list.size();
		}

		ImmutableMap<DimensionType, ListIterator<ChunkPos>> immutableMap = builder2.build();
		float f = (float)this.totalChunkCount;
		this.status = new TranslatableTextComponent("optimizeWorld.stage.structures");

		for (Entry<DimensionType, ChunkSaveHandlerImpl> entry : map.entrySet()) {
			((ChunkSaveHandlerImpl)entry.getValue()).getFeatureUpdater((DimensionType)entry.getKey(), this.persistentStateManager);
		}

		this.persistentStateManager.save();
		this.status = new TranslatableTextComponent("optimizeWorld.stage.upgrading");
		if (f <= 0.0F) {
			for (DimensionType dimensionType3 : DimensionType.getAll()) {
				this.dimensionProgress.put(dimensionType3, 1.0F / (float)map.size());
			}
		}

		while (this.keepUpgradingChunks) {
			boolean bl = false;
			float g = 0.0F;

			for (DimensionType dimensionType4 : DimensionType.getAll()) {
				ListIterator<ChunkPos> listIterator = immutableMap.get(dimensionType4);
				bl |= this.upgradeChunks((ChunkSaveHandlerImpl)map.get(dimensionType4), listIterator, dimensionType4);
				if (f > 0.0F) {
					float h = (float)listIterator.nextIndex() / f;
					this.dimensionProgress.put(dimensionType4, h);
					g += h;
				}
			}

			this.progress = g;
			if (!bl) {
				this.keepUpgradingChunks = false;
			}
		}

		this.status = new TranslatableTextComponent("optimizeWorld.stage.finished");
		l = SystemUtil.getMeasuringTimeMs() - l;
		LOGGER.info("World optimizaton finished after {} ms", l);
		map.values().forEach(ChunkSaveHandlerImpl::saveAllChunks);
		this.persistentStateManager.save();
		this.isDone = true;
	}

	private boolean upgradeChunks(ChunkSaveHandlerImpl chunkSaveHandlerImpl, ListIterator<ChunkPos> listIterator, DimensionType dimensionType) {
		if (listIterator.hasNext()) {
			boolean bl;
			synchronized (chunkSaveHandlerImpl) {
				bl = chunkSaveHandlerImpl.upgradeChunk((ChunkPos)listIterator.next(), dimensionType, this.persistentStateManager);
			}

			if (bl) {
				this.upgradedChunkCount++;
			} else {
				this.skippedChunkCount++;
			}

			return true;
		} else {
			return false;
		}
	}

	public boolean isDone() {
		return this.isDone;
	}

	@Environment(EnvType.CLIENT)
	public float getProgress(DimensionType dimensionType) {
		return this.dimensionProgress.getFloat(dimensionType);
	}

	@Environment(EnvType.CLIENT)
	public float getProgress() {
		return this.progress;
	}

	public int getTotalChunkCount() {
		return this.totalChunkCount;
	}

	public int getUpgradedChunkCount() {
		return this.upgradedChunkCount;
	}

	public int getSkippedChunkCount() {
		return this.skippedChunkCount;
	}

	public TextComponent getStatus() {
		return this.status;
	}

	@Environment(EnvType.CLIENT)
	public String getLevelName() {
		return this.levelName;
	}
}
