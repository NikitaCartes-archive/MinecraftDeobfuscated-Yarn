package net.minecraft.world.updater;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.VersionedChunkStorage;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.storage.RegionFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldUpdater {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final ThreadFactory UPDATE_THREAD_FACTORY = new ThreadFactoryBuilder().setDaemon(true).build();
	private final String levelName;
	private final boolean eraseCache;
	private final WorldSaveHandler worldSaveHandler;
	private final Thread updateThread;
	private final File worldDirectory;
	private volatile boolean keepUpgradingChunks = true;
	private volatile boolean isDone;
	private volatile float progress;
	private volatile int totalChunkCount;
	private volatile int upgradedChunkCount;
	private volatile int skippedChunkCount;
	private final Object2FloatMap<DimensionType> dimensionProgress = Object2FloatMaps.synchronize(
		new Object2FloatOpenCustomHashMap<>(SystemUtil.identityHashStrategy())
	);
	private volatile Component status = new TranslatableComponent("optimizeWorld.stage.counting");
	private static final Pattern REGION_FILE_PATTERN = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
	private final PersistentStateManager persistentStateManager;

	public WorldUpdater(String string, LevelStorage levelStorage, LevelProperties levelProperties, boolean bl) {
		this.levelName = levelProperties.getLevelName();
		this.eraseCache = bl;
		this.worldSaveHandler = levelStorage.createSaveHandler(string, null);
		this.worldSaveHandler.saveWorld(levelProperties);
		this.persistentStateManager = new PersistentStateManager(
			new File(DimensionType.field_13072.getFile(this.worldSaveHandler.getWorldDir()), "data"), this.worldSaveHandler.getDataFixer()
		);
		this.worldDirectory = this.worldSaveHandler.getWorldDir();
		this.updateThread = UPDATE_THREAD_FACTORY.newThread(this::updateWorld);
		this.updateThread.setUncaughtExceptionHandler((thread, throwable) -> {
			LOGGER.error("Error upgrading world", throwable);
			this.status = new TranslatableComponent("optimizeWorld.stage.failed");
		});
		this.updateThread.start();
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
		this.totalChunkCount = 0;
		Builder<DimensionType, ListIterator<ChunkPos>> builder = ImmutableMap.builder();

		for (DimensionType dimensionType : DimensionType.getAll()) {
			List<ChunkPos> list = this.getChunkPositions(dimensionType);
			builder.put(dimensionType, list.listIterator());
			this.totalChunkCount = this.totalChunkCount + list.size();
		}

		if (this.totalChunkCount == 0) {
			this.isDone = true;
		} else {
			float f = (float)this.totalChunkCount;
			ImmutableMap<DimensionType, ListIterator<ChunkPos>> immutableMap = builder.build();
			Builder<DimensionType, VersionedChunkStorage> builder2 = ImmutableMap.builder();

			for (DimensionType dimensionType2 : DimensionType.getAll()) {
				File file2 = dimensionType2.getFile(file);
				builder2.put(dimensionType2, new VersionedChunkStorage(new File(file2, "region"), this.worldSaveHandler.getDataFixer()));
			}

			ImmutableMap<DimensionType, VersionedChunkStorage> immutableMap2 = builder2.build();
			long l = SystemUtil.getMeasuringTimeMs();
			this.status = new TranslatableComponent("optimizeWorld.stage.upgrading");

			while (this.keepUpgradingChunks) {
				boolean bl = false;
				float g = 0.0F;

				for (DimensionType dimensionType3 : DimensionType.getAll()) {
					ListIterator<ChunkPos> listIterator = immutableMap.get(dimensionType3);
					VersionedChunkStorage versionedChunkStorage = immutableMap2.get(dimensionType3);
					if (listIterator.hasNext()) {
						ChunkPos chunkPos = (ChunkPos)listIterator.next();
						boolean bl2 = false;

						try {
							CompoundTag compoundTag = versionedChunkStorage.getTagAt(chunkPos);
							if (compoundTag != null) {
								int i = VersionedChunkStorage.getDataVersion(compoundTag);
								CompoundTag compoundTag2 = versionedChunkStorage.updateChunkTag(dimensionType3, () -> this.persistentStateManager, compoundTag);
								boolean bl3 = i < SharedConstants.getGameVersion().getWorldVersion();
								if (this.eraseCache) {
									CompoundTag compoundTag3 = compoundTag2.getCompound("Level");
									bl3 = bl3 || compoundTag3.containsKey("Heightmaps");
									compoundTag3.remove("Heightmaps");
									bl3 = bl3 || compoundTag3.containsKey("isLightOn");
									compoundTag3.remove("isLightOn");
								}

								if (bl3) {
									versionedChunkStorage.setTagAt(chunkPos, compoundTag2);
									bl2 = true;
								}
							}
						} catch (CrashException var23) {
							Throwable throwable = var23.getCause();
							if (!(throwable instanceof IOException)) {
								throw var23;
							}

							LOGGER.error("Error upgrading chunk {}", chunkPos, throwable);
						} catch (IOException var24) {
							LOGGER.error("Error upgrading chunk {}", chunkPos, var24);
						}

						if (bl2) {
							this.upgradedChunkCount++;
						} else {
							this.skippedChunkCount++;
						}

						bl = true;
					}

					float h = (float)listIterator.nextIndex() / f;
					this.dimensionProgress.put(dimensionType3, h);
					g += h;
				}

				this.progress = g;
				if (!bl) {
					this.keepUpgradingChunks = false;
				}
			}

			this.status = new TranslatableComponent("optimizeWorld.stage.finished");

			for (VersionedChunkStorage versionedChunkStorage2 : immutableMap2.values()) {
				try {
					versionedChunkStorage2.close();
				} catch (IOException var22) {
					LOGGER.error("Error upgrading chunk", (Throwable)var22);
				}
			}

			this.persistentStateManager.save();
			l = SystemUtil.getMeasuringTimeMs() - l;
			LOGGER.info("World optimizaton finished after {} ms", l);
			this.isDone = true;
		}
	}

	private List<ChunkPos> getChunkPositions(DimensionType dimensionType) {
		File file = dimensionType.getFile(this.worldDirectory);
		File file2 = new File(file, "region");
		File[] files = file2.listFiles((filex, string) -> string.endsWith(".mca"));
		if (files == null) {
			return ImmutableList.of();
		} else {
			List<ChunkPos> list = Lists.<ChunkPos>newArrayList();

			for (File file3 : files) {
				Matcher matcher = REGION_FILE_PATTERN.matcher(file3.getName());
				if (matcher.matches()) {
					int i = Integer.parseInt(matcher.group(1)) << 5;
					int j = Integer.parseInt(matcher.group(2)) << 5;

					try (RegionFile regionFile = new RegionFile(file3)) {
						for (int k = 0; k < 32; k++) {
							for (int l = 0; l < 32; l++) {
								ChunkPos chunkPos = new ChunkPos(k + i, l + j);
								if (regionFile.isChunkPresent(chunkPos)) {
									list.add(chunkPos);
								}
							}
						}
					} catch (Throwable var28) {
					}
				}
			}

			return list;
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

	public Component getStatus() {
		return this.status;
	}
}
