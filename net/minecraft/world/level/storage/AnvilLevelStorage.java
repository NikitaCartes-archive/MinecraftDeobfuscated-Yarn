/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.level.storage;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSourceConfig;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSourceConfig;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.AlphaChunkIo;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.storage.RegionFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilLevelStorage {
    private static final Logger LOGGER = LogManager.getLogger();

    static boolean convertLevel(Path path, DataFixer dataFixer, String string, ProgressListener progressListener) {
        progressListener.progressStagePercentage(0);
        ArrayList<File> list = Lists.newArrayList();
        ArrayList<File> list2 = Lists.newArrayList();
        ArrayList<File> list3 = Lists.newArrayList();
        File file = new File(path.toFile(), string);
        File file2 = DimensionType.THE_NETHER.getSaveDirectory(file);
        File file3 = DimensionType.THE_END.getSaveDirectory(file);
        LOGGER.info("Scanning folders...");
        AnvilLevelStorage.addRegionFiles(file, list);
        if (file2.exists()) {
            AnvilLevelStorage.addRegionFiles(file2, list2);
        }
        if (file3.exists()) {
            AnvilLevelStorage.addRegionFiles(file3, list3);
        }
        int i = list.size() + list2.size() + list3.size();
        LOGGER.info("Total conversion count is {}", (Object)i);
        LevelProperties levelProperties = LevelStorage.getLevelProperties(path, dataFixer, string);
        BiomeSourceType<FixedBiomeSourceConfig, FixedBiomeSource> biomeSourceType = BiomeSourceType.FIXED;
        BiomeSourceType<VanillaLayeredBiomeSourceConfig, VanillaLayeredBiomeSource> biomeSourceType2 = BiomeSourceType.VANILLA_LAYERED;
        BiomeSource biomeSource = levelProperties != null && levelProperties.getGeneratorType() == LevelGeneratorType.FLAT ? biomeSourceType.applyConfig(biomeSourceType.getConfig(levelProperties).setBiome(Biomes.PLAINS)) : biomeSourceType2.applyConfig(biomeSourceType2.getConfig(levelProperties));
        AnvilLevelStorage.convertRegions(new File(file, "region"), list, biomeSource, 0, i, progressListener);
        AnvilLevelStorage.convertRegions(new File(file2, "region"), list2, biomeSourceType.applyConfig(biomeSourceType.getConfig(levelProperties).setBiome(Biomes.NETHER)), list.size(), i, progressListener);
        AnvilLevelStorage.convertRegions(new File(file3, "region"), list3, biomeSourceType.applyConfig(biomeSourceType.getConfig(levelProperties).setBiome(Biomes.THE_END)), list.size() + list2.size(), i, progressListener);
        levelProperties.setVersion(19133);
        if (levelProperties.getGeneratorType() == LevelGeneratorType.DEFAULT_1_1) {
            levelProperties.setGeneratorType(LevelGeneratorType.DEFAULT);
        }
        AnvilLevelStorage.makeMcrLevelDatBackup(path, string);
        WorldSaveHandler worldSaveHandler = LevelStorage.createSaveHandler(path, dataFixer, string, null);
        worldSaveHandler.saveWorld(levelProperties);
        return true;
    }

    private static void makeMcrLevelDatBackup(Path path, String string) {
        File file = new File(path.toFile(), string);
        if (!file.exists()) {
            LOGGER.warn("Unable to create level.dat_mcr backup");
            return;
        }
        File file2 = new File(file, "level.dat");
        if (!file2.exists()) {
            LOGGER.warn("Unable to create level.dat_mcr backup");
            return;
        }
        File file3 = new File(file, "level.dat_mcr");
        if (!file2.renameTo(file3)) {
            LOGGER.warn("Unable to create level.dat_mcr backup");
        }
    }

    private static void convertRegions(File file, Iterable<File> iterable, BiomeSource biomeSource, int i, int currentCount, ProgressListener progressListener) {
        for (File file2 : iterable) {
            AnvilLevelStorage.convertRegion(file, file2, biomeSource, i, currentCount, progressListener);
            int j = (int)Math.round(100.0 * (double)(++i) / (double)currentCount);
            progressListener.progressStagePercentage(j);
        }
    }

    private static void convertRegion(File file, File baseFolder, BiomeSource biomeSource, int i, int progressStart, ProgressListener progressListener) {
        String string = baseFolder.getName();
        try (RegionFile regionFile = new RegionFile(baseFolder, file);
             RegionFile regionFile2 = new RegionFile(new File(file, string.substring(0, string.length() - ".mcr".length()) + ".mca"), file);){
            for (int j = 0; j < 32; ++j) {
                int k;
                for (k = 0; k < 32; ++k) {
                    CompoundTag compoundTag;
                    ChunkPos chunkPos = new ChunkPos(j, k);
                    if (!regionFile.hasChunk(chunkPos) || regionFile2.hasChunk(chunkPos)) continue;
                    try (DataInputStream dataInputStream = regionFile.getChunkInputStream(chunkPos);){
                        if (dataInputStream == null) {
                            LOGGER.warn("Failed to fetch input stream for chunk {}", (Object)chunkPos);
                            continue;
                        }
                        compoundTag = NbtIo.read(dataInputStream);
                    } catch (IOException iOException) {
                        LOGGER.warn("Failed to read data for chunk {}", (Object)chunkPos, (Object)iOException);
                        continue;
                    }
                    CompoundTag compoundTag2 = compoundTag.getCompound("Level");
                    AlphaChunkIo.AlphaChunk alphaChunk = AlphaChunkIo.readAlphaChunk(compoundTag2);
                    CompoundTag compoundTag3 = new CompoundTag();
                    CompoundTag compoundTag4 = new CompoundTag();
                    compoundTag3.put("Level", compoundTag4);
                    AlphaChunkIo.convertAlphaChunk(alphaChunk, compoundTag4, biomeSource);
                    try (DataOutputStream dataOutputStream = regionFile2.getChunkOutputStream(chunkPos);){
                        NbtIo.write(compoundTag3, (DataOutput)dataOutputStream);
                        continue;
                    }
                }
                k = (int)Math.round(100.0 * (double)(i * 1024) / (double)(progressStart * 1024));
                int l = (int)Math.round(100.0 * (double)((j + 1) * 32 + i * 1024) / (double)(progressStart * 1024));
                if (l <= k) continue;
                progressListener.progressStagePercentage(l);
            }
        } catch (IOException iOException2) {
            LOGGER.error("Failed to upgrade region file {}", (Object)baseFolder, (Object)iOException2);
        }
    }

    private static void addRegionFiles(File file2, Collection<File> collection) {
        File file22 = new File(file2, "region");
        File[] files = file22.listFiles((file, string) -> string.endsWith(".mcr"));
        if (files != null) {
            Collections.addAll(collection, files);
        }
    }
}

