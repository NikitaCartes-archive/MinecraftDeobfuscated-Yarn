package net.minecraft.world.level.storage;

import com.google.common.collect.Lists;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.storage.RegionFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilLevelStorage {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final String FILE_EXTENSION = ".mcr";

	static boolean convertLevel(LevelStorage.Session storageSession, ProgressListener progressListener) {
		progressListener.progressStagePercentage(0);
		List<File> list = Lists.<File>newArrayList();
		List<File> list2 = Lists.<File>newArrayList();
		List<File> list3 = Lists.<File>newArrayList();
		File file = storageSession.getWorldDirectory(World.OVERWORLD);
		File file2 = storageSession.getWorldDirectory(World.NETHER);
		File file3 = storageSession.getWorldDirectory(World.END);
		LOGGER.info("Scanning folders...");
		addRegionFiles(file, list);
		if (file2.exists()) {
			addRegionFiles(file2, list2);
		}

		if (file3.exists()) {
			addRegionFiles(file3, list3);
		}

		int i = list.size() + list2.size() + list3.size();
		LOGGER.info("Total conversion count is {}", i);
		DynamicRegistryManager.Impl impl = DynamicRegistryManager.create();
		RegistryOps<NbtElement> registryOps = RegistryOps.method_36574(NbtOps.INSTANCE, ResourceManager.Empty.INSTANCE, impl);
		SaveProperties saveProperties = storageSession.readLevelProperties(registryOps, DataPackSettings.SAFE_MODE);
		long l = saveProperties != null ? saveProperties.getGeneratorOptions().getSeed() : 0L;
		Registry<Biome> registry = impl.get(Registry.BIOME_KEY);
		BiomeSource biomeSource;
		if (saveProperties != null && saveProperties.getGeneratorOptions().isFlatWorld()) {
			biomeSource = new FixedBiomeSource(registry.getOrThrow(BiomeKeys.PLAINS));
		} else {
			biomeSource = MultiNoiseBiomeSource.createVanillaSource(registry, l);
		}

		convertRegions(impl, new File(file, "region"), list, biomeSource, 0, i, progressListener);
		convertRegions(impl, new File(file2, "region"), list2, new FixedBiomeSource(registry.getOrThrow(BiomeKeys.NETHER_WASTES)), list.size(), i, progressListener);
		convertRegions(
			impl, new File(file3, "region"), list3, new FixedBiomeSource(registry.getOrThrow(BiomeKeys.THE_END)), list.size() + list2.size(), i, progressListener
		);
		makeMcrLevelDatBackup(storageSession);
		storageSession.backupLevelDataFile(impl, saveProperties);
		return true;
	}

	private static void makeMcrLevelDatBackup(LevelStorage.Session storageSession) {
		File file = storageSession.getDirectory(WorldSavePath.LEVEL_DAT).toFile();
		if (!file.exists()) {
			LOGGER.warn("Unable to create level.dat_mcr backup");
		} else {
			File file2 = new File(file.getParent(), "level.dat_mcr");
			if (!file.renameTo(file2)) {
				LOGGER.warn("Unable to create level.dat_mcr backup");
			}
		}
	}

	private static void convertRegions(
		DynamicRegistryManager.Impl registryManager, File directory, Iterable<File> files, BiomeSource biomeSource, int i, int j, ProgressListener progressListener
	) {
		for (File file : files) {
			convertRegion(registryManager, directory, file, biomeSource, i, j, progressListener);
			i++;
			int k = (int)Math.round(100.0 * (double)i / (double)j);
			progressListener.progressStagePercentage(k);
		}
	}

	private static void convertRegion(
		DynamicRegistryManager.Impl registryManager, File directory, File file, BiomeSource biomeSource, int i, int j, ProgressListener progressListener
	) {
		String string = file.getName();

		try (
			RegionFile regionFile = new RegionFile(file, directory, true);
			RegionFile regionFile2 = new RegionFile(new File(directory, string.substring(0, string.length() - ".mcr".length()) + ".mca"), directory, true);
		) {
			for (int k = 0; k < 32; k++) {
				for (int l = 0; l < 32; l++) {
					ChunkPos chunkPos = new ChunkPos(k, l);
					if (regionFile.hasChunk(chunkPos) && !regionFile2.hasChunk(chunkPos)) {
						NbtCompound nbtCompound;
						try {
							DataInputStream dataInputStream = regionFile.getChunkInputStream(chunkPos);

							label111: {
								try {
									if (dataInputStream != null) {
										nbtCompound = NbtIo.read(dataInputStream);
										break label111;
									}

									LOGGER.warn("Failed to fetch input stream for chunk {}", chunkPos);
								} catch (Throwable var26) {
									if (dataInputStream != null) {
										try {
											dataInputStream.close();
										} catch (Throwable var24) {
											var26.addSuppressed(var24);
										}
									}

									throw var26;
								}

								if (dataInputStream != null) {
									dataInputStream.close();
								}
								continue;
							}

							if (dataInputStream != null) {
								dataInputStream.close();
							}
						} catch (IOException var27) {
							LOGGER.warn("Failed to read data for chunk {}", chunkPos, var27);
							continue;
						}

						NbtCompound nbtCompound2 = nbtCompound.getCompound("Level");
						AlphaChunkIo.AlphaChunk alphaChunk = AlphaChunkIo.readAlphaChunk(nbtCompound2);
						NbtCompound nbtCompound3 = new NbtCompound();
						NbtCompound nbtCompound4 = new NbtCompound();
						nbtCompound3.put("Level", nbtCompound4);
						AlphaChunkIo.convertAlphaChunk(registryManager, alphaChunk, nbtCompound4, biomeSource);
						DataOutputStream dataOutputStream = regionFile2.getChunkOutputStream(chunkPos);

						try {
							NbtIo.write(nbtCompound3, dataOutputStream);
						} catch (Throwable var25) {
							if (dataOutputStream != null) {
								try {
									dataOutputStream.close();
								} catch (Throwable var23) {
									var25.addSuppressed(var23);
								}
							}

							throw var25;
						}

						if (dataOutputStream != null) {
							dataOutputStream.close();
						}
					}
				}

				int lx = (int)Math.round(100.0 * (double)(i * 1024) / (double)(j * 1024));
				int m = (int)Math.round(100.0 * (double)((k + 1) * 32 + i * 1024) / (double)(j * 1024));
				if (m > lx) {
					progressListener.progressStagePercentage(m);
				}
			}
		} catch (IOException var30) {
			LOGGER.error("Failed to upgrade region file {}", file, var30);
		}
	}

	private static void addRegionFiles(File worldDirectory, Collection<File> files) {
		File file = new File(worldDirectory, "region");
		File[] files2 = file.listFiles((directory, name) -> name.endsWith(".mcr"));
		if (files2 != null) {
			Collections.addAll(files, files2);
		}
	}
}
