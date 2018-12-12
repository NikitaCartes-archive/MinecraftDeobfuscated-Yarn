package net.minecraft.world.level.storage;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.AnvilWorldSaveHandler;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSourceConfig;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSourceConfig;
import net.minecraft.world.chunk.storage.RegionFile;
import net.minecraft.world.chunk.storage.RegionFileCache;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilLevelStorage extends OldLevelStorage {
	private static final Logger LOGGER = LogManager.getLogger();

	public AnvilLevelStorage(Path path, Path path2, DataFixer dataFixer) {
		super(path, path2, dataFixer);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public String getName() {
		return "Anvil";
	}

	@Environment(EnvType.CLIENT)
	@Override
	public List<LevelSummary> getAvailableLevels() throws LevelStorageException {
		if (!Files.isDirectory(this.baseDir, new LinkOption[0])) {
			throw new LevelStorageException(new TranslatableTextComponent("selectWorld.load_folder_access").getString());
		} else {
			List<LevelSummary> list = Lists.<LevelSummary>newArrayList();
			File[] files = this.baseDir.toFile().listFiles();

			for (File file : files) {
				if (file.isDirectory()) {
					String string = file.getName();
					LevelProperties levelProperties = this.getLevelProperties(string);
					if (levelProperties != null && (levelProperties.getVersion() == 19132 || levelProperties.getVersion() == 19133)) {
						boolean bl = levelProperties.getVersion() != this.getVersion();
						String string2 = levelProperties.getLevelName();
						if (StringUtils.isEmpty(string2)) {
							string2 = string;
						}

						long l = 0L;
						list.add(new LevelSummary(levelProperties, string, string2, 0L, bl));
					}
				}
			}

			return list;
		}
	}

	protected int getVersion() {
		return 19133;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void clearAll() {
		RegionFileCache.clear();
	}

	@Override
	public WorldSaveHandler method_242(String string, @Nullable MinecraftServer minecraftServer) {
		return new AnvilWorldSaveHandler(this.baseDir.toFile(), string, minecraftServer, this.field_143);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isConvertible(String string) {
		LevelProperties levelProperties = this.getLevelProperties(string);
		return levelProperties != null && levelProperties.getVersion() == 19132;
	}

	@Override
	public boolean requiresConversion(String string) {
		LevelProperties levelProperties = this.getLevelProperties(string);
		return levelProperties != null && levelProperties.getVersion() != this.getVersion();
	}

	@Override
	public boolean convertLevel(String string, ProgressListener progressListener) {
		progressListener.progressStagePercentage(0);
		List<File> list = Lists.<File>newArrayList();
		List<File> list2 = Lists.<File>newArrayList();
		List<File> list3 = Lists.<File>newArrayList();
		File file = new File(this.baseDir.toFile(), string);
		File file2 = DimensionType.field_13076.getFile(file);
		File file3 = DimensionType.field_13078.getFile(file);
		LOGGER.info("Scanning folders...");
		this.addRegionFiles(file, list);
		if (file2.exists()) {
			this.addRegionFiles(file2, list2);
		}

		if (file3.exists()) {
			this.addRegionFiles(file3, list3);
		}

		int i = list.size() + list2.size() + list3.size();
		LOGGER.info("Total conversion count is {}", i);
		LevelProperties levelProperties = this.getLevelProperties(string);
		BiomeSourceType<FixedBiomeSourceConfig, FixedBiomeSource> biomeSourceType = BiomeSourceType.FIXED;
		BiomeSourceType<VanillaLayeredBiomeSourceConfig, VanillaLayeredBiomeSource> biomeSourceType2 = BiomeSourceType.VANILLA_LAYERED;
		BiomeSource biomeSource;
		if (levelProperties != null && levelProperties.getGeneratorType() == LevelGeneratorType.FLAT) {
			biomeSource = biomeSourceType.applyConfig(biomeSourceType.getConfig().method_8782(Biomes.field_9451));
		} else {
			biomeSource = biomeSourceType2.applyConfig(
				biomeSourceType2.getConfig().setLevelProperties(levelProperties).method_9004(ChunkGeneratorType.field_12769.method_12117())
			);
		}

		this.convertRegions(new File(file, "region"), list, biomeSource, 0, i, progressListener);
		this.convertRegions(
			new File(file2, "region"), list2, biomeSourceType.applyConfig(biomeSourceType.getConfig().method_8782(Biomes.field_9461)), list.size(), i, progressListener
		);
		this.convertRegions(
			new File(file3, "region"),
			list3,
			biomeSourceType.applyConfig(biomeSourceType.getConfig().method_8782(Biomes.field_9411)),
			list.size() + list2.size(),
			i,
			progressListener
		);
		levelProperties.setVersion(19133);
		if (levelProperties.getGeneratorType() == LevelGeneratorType.DEFAULT_1_1) {
			levelProperties.setGeneratorType(LevelGeneratorType.DEFAULT);
		}

		this.makeMcrLevelDatBackup(string);
		WorldSaveHandler worldSaveHandler = this.method_242(string, null);
		worldSaveHandler.saveWorld(levelProperties);
		return true;
	}

	private void makeMcrLevelDatBackup(String string) {
		File file = new File(this.baseDir.toFile(), string);
		if (!file.exists()) {
			LOGGER.warn("Unable to create level.dat_mcr backup");
		} else {
			File file2 = new File(file, "level.dat");
			if (!file2.exists()) {
				LOGGER.warn("Unable to create level.dat_mcr backup");
			} else {
				File file3 = new File(file, "level.dat_mcr");
				if (!file2.renameTo(file3)) {
					LOGGER.warn("Unable to create level.dat_mcr backup");
				}
			}
		}
	}

	private void convertRegions(File file, Iterable<File> iterable, BiomeSource biomeSource, int i, int j, ProgressListener progressListener) {
		for (File file2 : iterable) {
			this.convertRegion(file, file2, biomeSource, i, j, progressListener);
			i++;
			int k = (int)Math.round(100.0 * (double)i / (double)j);
			progressListener.progressStagePercentage(k);
		}
	}

	private void convertRegion(File file, File file2, BiomeSource biomeSource, int i, int j, ProgressListener progressListener) {
		try {
			String string = file2.getName();
			RegionFile regionFile = new RegionFile(file2);
			RegionFile regionFile2 = new RegionFile(new File(file, string.substring(0, string.length() - ".mcr".length()) + ".mca"));

			for (int k = 0; k < 32; k++) {
				for (int l = 0; l < 32; l++) {
					if (regionFile.hasChunk(k, l) && !regionFile2.hasChunk(k, l)) {
						DataInputStream dataInputStream = regionFile.getChunkDataInputStream(k, l);
						if (dataInputStream == null) {
							LOGGER.warn("Failed to fetch input stream");
						} else {
							CompoundTag compoundTag = NbtIo.read(dataInputStream);
							dataInputStream.close();
							CompoundTag compoundTag2 = compoundTag.getCompound("Level");
							AlphaChunkIo.AlphaChunk alphaChunk = AlphaChunkIo.readAlphaChunk(compoundTag2);
							CompoundTag compoundTag3 = new CompoundTag();
							CompoundTag compoundTag4 = new CompoundTag();
							compoundTag3.put("Level", compoundTag4);
							AlphaChunkIo.convertAlphaChunk(alphaChunk, compoundTag4, biomeSource);
							DataOutputStream dataOutputStream = regionFile2.getChunkDataOutputStream(k, l);
							NbtIo.write(compoundTag3, dataOutputStream);
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

			regionFile.close();
			regionFile2.close();
		} catch (IOException var19) {
			var19.printStackTrace();
		}
	}

	private void addRegionFiles(File file, Collection<File> collection) {
		File file2 = new File(file, "region");
		File[] files = file2.listFiles((filex, string) -> string.endsWith(".mcr"));
		if (files != null) {
			Collections.addAll(collection, files);
		}
	}
}
