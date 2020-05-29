package net.minecraft.world.level.storage;

import com.google.common.collect.Lists;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.storage.RegionFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilLevelStorage {
	private static final Logger LOGGER = LogManager.getLogger();

	static boolean convertLevel(LevelStorage.Session session, ProgressListener progressListener) {
		progressListener.progressStagePercentage(0);
		List<File> list = Lists.<File>newArrayList();
		List<File> list2 = Lists.<File>newArrayList();
		List<File> list3 = Lists.<File>newArrayList();
		File file = session.method_27424(World.OVERWORLD);
		File file2 = session.method_27424(World.NETHER);
		File file3 = session.method_27424(World.END);
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
		SaveProperties saveProperties = session.readLevelProperties();
		long l = saveProperties != null ? saveProperties.getGeneratorOptions().getSeed() : 0L;
		BiomeSource biomeSource;
		if (saveProperties != null && saveProperties.getGeneratorOptions().isFlatWorld()) {
			biomeSource = new FixedBiomeSource(Biomes.PLAINS);
		} else {
			biomeSource = new VanillaLayeredBiomeSource(l, false, false);
		}

		convertRegions(new File(file, "region"), list, biomeSource, 0, i, progressListener);
		convertRegions(new File(file2, "region"), list2, new FixedBiomeSource(Biomes.NETHER_WASTES), list.size(), i, progressListener);
		convertRegions(new File(file3, "region"), list3, new FixedBiomeSource(Biomes.THE_END), list.size() + list2.size(), i, progressListener);
		makeMcrLevelDatBackup(session);
		session.method_27425(saveProperties);
		return true;
	}

	private static void makeMcrLevelDatBackup(LevelStorage.Session session) {
		File file = session.getDirectory(WorldSavePath.LEVEL_DAT).toFile();
		if (!file.exists()) {
			LOGGER.warn("Unable to create level.dat_mcr backup");
		} else {
			File file2 = new File(file.getParent(), "level.dat_mcr");
			if (!file.renameTo(file2)) {
				LOGGER.warn("Unable to create level.dat_mcr backup");
			}
		}
	}

	private static void convertRegions(File file, Iterable<File> iterable, BiomeSource biomeSource, int i, int currentCount, ProgressListener progressListener) {
		for (File file2 : iterable) {
			convertRegion(file, file2, biomeSource, i, currentCount, progressListener);
			i++;
			int j = (int)Math.round(100.0 * (double)i / (double)currentCount);
			progressListener.progressStagePercentage(j);
		}
	}

	private static void convertRegion(File file, File baseFolder, BiomeSource biomeSource, int i, int progressStart, ProgressListener progressListener) {
		String string = baseFolder.getName();

		try (
			RegionFile regionFile = new RegionFile(baseFolder, file, true);
			RegionFile regionFile2 = new RegionFile(new File(file, string.substring(0, string.length() - ".mcr".length()) + ".mca"), file, true);
		) {
			for (int j = 0; j < 32; j++) {
				for (int k = 0; k < 32; k++) {
					ChunkPos chunkPos = new ChunkPos(j, k);
					if (regionFile.hasChunk(chunkPos) && !regionFile2.hasChunk(chunkPos)) {
						CompoundTag compoundTag;
						try {
							DataInputStream dataInputStream = regionFile.getChunkInputStream(chunkPos);
							Throwable alphaChunk = null;

							try {
								if (dataInputStream == null) {
									LOGGER.warn("Failed to fetch input stream for chunk {}", chunkPos);
									continue;
								}

								compoundTag = NbtIo.read(dataInputStream);
							} catch (Throwable var104) {
								alphaChunk = var104;
								throw var104;
							} finally {
								if (dataInputStream != null) {
									if (alphaChunk != null) {
										try {
											dataInputStream.close();
										} catch (Throwable var101) {
											alphaChunk.addSuppressed(var101);
										}
									} else {
										dataInputStream.close();
									}
								}
							}
						} catch (IOException var106) {
							LOGGER.warn("Failed to read data for chunk {}", chunkPos, var106);
							continue;
						}

						CompoundTag compoundTag2 = compoundTag.getCompound("Level");
						AlphaChunkIo.AlphaChunk alphaChunk = AlphaChunkIo.readAlphaChunk(compoundTag2);
						CompoundTag compoundTag3 = new CompoundTag();
						CompoundTag compoundTag4 = new CompoundTag();
						compoundTag3.put("Level", compoundTag4);
						AlphaChunkIo.convertAlphaChunk(alphaChunk, compoundTag4, biomeSource);
						DataOutputStream dataOutputStream = regionFile2.getChunkOutputStream(chunkPos);
						Throwable var20 = null;

						try {
							NbtIo.write(compoundTag3, dataOutputStream);
						} catch (Throwable var102) {
							var20 = var102;
							throw var102;
						} finally {
							if (dataOutputStream != null) {
								if (var20 != null) {
									try {
										dataOutputStream.close();
									} catch (Throwable var100) {
										var20.addSuppressed(var100);
									}
								} else {
									dataOutputStream.close();
								}
							}
						}
					}
				}

				int kx = (int)Math.round(100.0 * (double)(i * 1024) / (double)(progressStart * 1024));
				int l = (int)Math.round(100.0 * (double)((j + 1) * 32 + i * 1024) / (double)(progressStart * 1024));
				if (l > kx) {
					progressListener.progressStagePercentage(l);
				}
			}
		} catch (IOException var111) {
			LOGGER.error("Failed to upgrade region file {}", baseFolder, var111);
		}
	}

	private static void addRegionFiles(File file, Collection<File> collection) {
		File file2 = new File(file, "region");
		File[] files = file2.listFiles((filex, string) -> string.endsWith(".mcr"));
		if (files != null) {
			Collections.addAll(collection, files);
		}
	}
}
