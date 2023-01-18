package net.minecraft.world.updater;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.storage.RegionFile;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.slf4j.Logger;

public class WorldUpdater {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final ThreadFactory UPDATE_THREAD_FACTORY = new ThreadFactoryBuilder().setDaemon(true).build();
	private final Registry<DimensionOptions> dimensionOptionsRegistry;
	private final Set<RegistryKey<World>> worldKeys;
	private final boolean eraseCache;
	private final LevelStorage.Session session;
	private final Thread updateThread;
	private final DataFixer dataFixer;
	private volatile boolean keepUpgradingChunks = true;
	private volatile boolean done;
	private volatile float progress;
	private volatile int totalChunkCount;
	private volatile int upgradedChunkCount;
	private volatile int skippedChunkCount;
	private final Object2FloatMap<RegistryKey<World>> dimensionProgress = Object2FloatMaps.synchronize(
		new Object2FloatOpenCustomHashMap<>(Util.identityHashStrategy())
	);
	private volatile Text status = Text.translatable("optimizeWorld.stage.counting");
	private static final Pattern REGION_FILE_PATTERN = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
	private final PersistentStateManager persistentStateManager;

	public WorldUpdater(LevelStorage.Session session, DataFixer dataFixer, Registry<DimensionOptions> dimensionOptionsRegistry, boolean eraseCache) {
		this.dimensionOptionsRegistry = dimensionOptionsRegistry;
		this.worldKeys = (Set<RegistryKey<World>>)dimensionOptionsRegistry.getKeys().stream().map(RegistryKeys::toWorldKey).collect(Collectors.toUnmodifiableSet());
		this.eraseCache = eraseCache;
		this.dataFixer = dataFixer;
		this.session = session;
		this.persistentStateManager = new PersistentStateManager(this.session.getWorldDirectory(World.OVERWORLD).resolve("data").toFile(), dataFixer);
		this.updateThread = UPDATE_THREAD_FACTORY.newThread(this::updateWorld);
		this.updateThread.setUncaughtExceptionHandler((thread, throwable) -> {
			LOGGER.error("Error upgrading world", throwable);
			this.status = Text.translatable("optimizeWorld.stage.failed");
			this.done = true;
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
		this.totalChunkCount = 0;
		Builder<RegistryKey<World>, ListIterator<ChunkPos>> builder = ImmutableMap.builder();

		for (RegistryKey<World> registryKey : this.worldKeys) {
			List<ChunkPos> list = this.getChunkPositions(registryKey);
			builder.put(registryKey, list.listIterator());
			this.totalChunkCount = this.totalChunkCount + list.size();
		}

		if (this.totalChunkCount == 0) {
			this.done = true;
		} else {
			float f = (float)this.totalChunkCount;
			ImmutableMap<RegistryKey<World>, ListIterator<ChunkPos>> immutableMap = builder.build();
			Builder<RegistryKey<World>, VersionedChunkStorage> builder2 = ImmutableMap.builder();

			for (RegistryKey<World> registryKey2 : this.worldKeys) {
				Path path = this.session.getWorldDirectory(registryKey2);
				builder2.put(registryKey2, new VersionedChunkStorage(path.resolve("region"), this.dataFixer, true));
			}

			ImmutableMap<RegistryKey<World>, VersionedChunkStorage> immutableMap2 = builder2.build();
			long l = Util.getMeasuringTimeMs();
			this.status = Text.translatable("optimizeWorld.stage.upgrading");

			while (this.keepUpgradingChunks) {
				boolean bl = false;
				float g = 0.0F;

				for (RegistryKey<World> registryKey3 : this.worldKeys) {
					ListIterator<ChunkPos> listIterator = immutableMap.get(registryKey3);
					VersionedChunkStorage versionedChunkStorage = immutableMap2.get(registryKey3);
					if (listIterator.hasNext()) {
						ChunkPos chunkPos = (ChunkPos)listIterator.next();
						boolean bl2 = false;

						try {
							NbtCompound nbtCompound = (NbtCompound)((Optional)versionedChunkStorage.getNbt(chunkPos).join()).orElse(null);
							if (nbtCompound != null) {
								int i = VersionedChunkStorage.getDataVersion(nbtCompound);
								ChunkGenerator chunkGenerator = this.dimensionOptionsRegistry.getOrThrow(RegistryKeys.toDimensionKey(registryKey3)).chunkGenerator();
								NbtCompound nbtCompound2 = versionedChunkStorage.updateChunkNbt(
									registryKey3, () -> this.persistentStateManager, nbtCompound, chunkGenerator.getCodecKey()
								);
								ChunkPos chunkPos2 = new ChunkPos(nbtCompound2.getInt("xPos"), nbtCompound2.getInt("zPos"));
								if (!chunkPos2.equals(chunkPos)) {
									LOGGER.warn("Chunk {} has invalid position {}", chunkPos, chunkPos2);
								}

								boolean bl3 = i < SharedConstants.getGameVersion().getSaveVersion().getId();
								if (this.eraseCache) {
									bl3 = bl3 || nbtCompound2.contains("Heightmaps");
									nbtCompound2.remove("Heightmaps");
									bl3 = bl3 || nbtCompound2.contains("isLightOn");
									nbtCompound2.remove("isLightOn");
									NbtList nbtList = nbtCompound2.getList("sections", NbtElement.COMPOUND_TYPE);

									for (int j = 0; j < nbtList.size(); j++) {
										NbtCompound nbtCompound3 = nbtList.getCompound(j);
										bl3 = bl3 || nbtCompound3.contains("BlockLight");
										nbtCompound3.remove("BlockLight");
										bl3 = bl3 || nbtCompound3.contains("SkyLight");
										nbtCompound3.remove("SkyLight");
									}
								}

								if (bl3) {
									versionedChunkStorage.setNbt(chunkPos, nbtCompound2);
									bl2 = true;
								}
							}
						} catch (CompletionException | CrashException var26) {
							Throwable throwable = var26.getCause();
							if (!(throwable instanceof IOException)) {
								throw var26;
							}

							LOGGER.error("Error upgrading chunk {}", chunkPos, throwable);
						}

						if (bl2) {
							this.upgradedChunkCount++;
						} else {
							this.skippedChunkCount++;
						}

						bl = true;
					}

					float h = (float)listIterator.nextIndex() / f;
					this.dimensionProgress.put(registryKey3, h);
					g += h;
				}

				this.progress = g;
				if (!bl) {
					this.keepUpgradingChunks = false;
				}
			}

			this.status = Text.translatable("optimizeWorld.stage.finished");

			for (VersionedChunkStorage versionedChunkStorage2 : immutableMap2.values()) {
				try {
					versionedChunkStorage2.close();
				} catch (IOException var25) {
					LOGGER.error("Error upgrading chunk", (Throwable)var25);
				}
			}

			this.persistentStateManager.save();
			l = Util.getMeasuringTimeMs() - l;
			LOGGER.info("World optimizaton finished after {} ms", l);
			this.done = true;
		}
	}

	private List<ChunkPos> getChunkPositions(RegistryKey<World> world) {
		File file = this.session.getWorldDirectory(world).toFile();
		File file2 = new File(file, "region");
		File[] files = file2.listFiles((directory, name) -> name.endsWith(".mca"));
		if (files == null) {
			return ImmutableList.of();
		} else {
			List<ChunkPos> list = Lists.<ChunkPos>newArrayList();

			for (File file3 : files) {
				Matcher matcher = REGION_FILE_PATTERN.matcher(file3.getName());
				if (matcher.matches()) {
					int i = Integer.parseInt(matcher.group(1)) << 5;
					int j = Integer.parseInt(matcher.group(2)) << 5;

					try (RegionFile regionFile = new RegionFile(file3.toPath(), file2.toPath(), true)) {
						for (int k = 0; k < 32; k++) {
							for (int l = 0; l < 32; l++) {
								ChunkPos chunkPos = new ChunkPos(k + i, l + j);
								if (regionFile.isChunkValid(chunkPos)) {
									list.add(chunkPos);
								}
							}
						}
					} catch (Throwable var19) {
					}
				}
			}

			return list;
		}
	}

	public boolean isDone() {
		return this.done;
	}

	public Set<RegistryKey<World>> getWorlds() {
		return this.worldKeys;
	}

	public float getProgress(RegistryKey<World> world) {
		return this.dimensionProgress.getFloat(world);
	}

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

	public Text getStatus() {
		return this.status;
	}
}
