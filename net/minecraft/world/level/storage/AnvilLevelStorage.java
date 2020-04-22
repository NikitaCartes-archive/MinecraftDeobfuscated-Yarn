/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.level.storage;

import com.google.common.collect.Lists;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.class_5218;
import net.minecraft.class_5219;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSourceConfig;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSourceConfig;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.storage.AlphaChunkIo;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.storage.RegionFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilLevelStorage {
    private static final Logger LOGGER = LogManager.getLogger();

    static boolean convertLevel(LevelStorage.Session session, ProgressListener progressListener) {
        progressListener.progressStagePercentage(0);
        ArrayList<File> list = Lists.newArrayList();
        ArrayList<File> list2 = Lists.newArrayList();
        ArrayList<File> list3 = Lists.newArrayList();
        File file = session.method_27424(DimensionType.OVERWORLD);
        File file2 = session.method_27424(DimensionType.THE_NETHER);
        File file3 = session.method_27424(DimensionType.THE_END);
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
        class_5219 lv = session.readLevelProperties();
        long l = lv != null ? lv.getSeed() : 0L;
        BiomeSourceType<FixedBiomeSourceConfig, FixedBiomeSource> biomeSourceType = BiomeSourceType.FIXED;
        BiomeSourceType<VanillaLayeredBiomeSourceConfig, VanillaLayeredBiomeSource> biomeSourceType2 = BiomeSourceType.VANILLA_LAYERED;
        BiomeSource biomeSource = lv != null && lv.method_27437(DimensionType.OVERWORLD).getGeneratorType() == LevelGeneratorType.FLAT ? biomeSourceType.applyConfig(biomeSourceType.getConfig(lv.getSeed()).setBiome(Biomes.PLAINS)) : biomeSourceType2.applyConfig(biomeSourceType2.getConfig(l));
        AnvilLevelStorage.convertRegions(new File(file, "region"), list, biomeSource, 0, i, progressListener);
        AnvilLevelStorage.convertRegions(new File(file2, "region"), list2, biomeSourceType.applyConfig(biomeSourceType.getConfig(l).setBiome(Biomes.NETHER_WASTES)), list.size(), i, progressListener);
        AnvilLevelStorage.convertRegions(new File(file3, "region"), list3, biomeSourceType.applyConfig(biomeSourceType.getConfig(l).setBiome(Biomes.THE_END)), list.size() + list2.size(), i, progressListener);
        AnvilLevelStorage.makeMcrLevelDatBackup(session);
        session.method_27425(lv);
        return true;
    }

    private static void makeMcrLevelDatBackup(LevelStorage.Session session) {
        File file = session.getDirectory(class_5218.field_24184).toFile();
        if (!file.exists()) {
            LOGGER.warn("Unable to create level.dat_mcr backup");
            return;
        }
        File file2 = new File(file.getParent(), "level.dat_mcr");
        if (!file.renameTo(file2)) {
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
        try (RegionFile regionFile = new RegionFile(baseFolder, file, true);
             RegionFile regionFile2 = new RegionFile(new File(file, string.substring(0, string.length() - ".mcr".length()) + ".mca"), file, true);){
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

