/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.updater;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.storage.RegionFile;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.slf4j.Logger;

public class WorldUpdater {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ThreadFactory UPDATE_THREAD_FACTORY = new ThreadFactoryBuilder().setDaemon(true).build();
    private final GeneratorOptions generatorOptions;
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
    private final Object2FloatMap<RegistryKey<World>> dimensionProgress = Object2FloatMaps.synchronize(new Object2FloatOpenCustomHashMap(Util.identityHashStrategy()));
    private volatile Text status = new TranslatableText("optimizeWorld.stage.counting");
    private static final Pattern REGION_FILE_PATTERN = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
    private final PersistentStateManager persistentStateManager;

    public WorldUpdater(LevelStorage.Session session, DataFixer dataFixer, GeneratorOptions generatorOptions, boolean eraseCache) {
        this.generatorOptions = generatorOptions;
        this.eraseCache = eraseCache;
        this.dataFixer = dataFixer;
        this.session = session;
        this.persistentStateManager = new PersistentStateManager(this.session.getWorldDirectory(World.OVERWORLD).resolve("data").toFile(), dataFixer);
        this.updateThread = UPDATE_THREAD_FACTORY.newThread(this::updateWorld);
        this.updateThread.setUncaughtExceptionHandler((thread, throwable) -> {
            LOGGER.error("Error upgrading world", throwable);
            this.status = new TranslatableText("optimizeWorld.stage.failed");
            this.done = true;
        });
        this.updateThread.start();
    }

    public void cancel() {
        this.keepUpgradingChunks = false;
        try {
            this.updateThread.join();
        } catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    private void updateWorld() {
        this.totalChunkCount = 0;
        ImmutableMap.Builder<RegistryKey, ListIterator<ChunkPos>> builder = ImmutableMap.builder();
        ImmutableSet<RegistryKey<World>> immutableSet = this.generatorOptions.getWorlds();
        for (RegistryKey registryKey : immutableSet) {
            List<ChunkPos> list = this.getChunkPositions(registryKey);
            builder.put(registryKey, list.listIterator());
            this.totalChunkCount += list.size();
        }
        if (this.totalChunkCount == 0) {
            this.done = true;
            return;
        }
        float f = this.totalChunkCount;
        ImmutableMap immutableMap = builder.build();
        ImmutableMap.Builder<RegistryKey, VersionedChunkStorage> builder2 = ImmutableMap.builder();
        for (RegistryKey registryKey : immutableSet) {
            Path path = this.session.getWorldDirectory(registryKey);
            builder2.put(registryKey, new VersionedChunkStorage(path.resolve("region"), this.dataFixer, true));
        }
        ImmutableMap immutableMap2 = builder2.build();
        long l = Util.getMeasuringTimeMs();
        this.status = new TranslatableText("optimizeWorld.stage.upgrading");
        while (this.keepUpgradingChunks) {
            boolean bl = false;
            float g = 0.0f;
            for (RegistryKey registryKey : immutableSet) {
                ListIterator listIterator = (ListIterator)immutableMap.get(registryKey);
                VersionedChunkStorage versionedChunkStorage = (VersionedChunkStorage)immutableMap2.get(registryKey);
                if (listIterator.hasNext()) {
                    ChunkPos chunkPos = (ChunkPos)listIterator.next();
                    boolean bl2 = false;
                    try {
                        NbtCompound nbtCompound = versionedChunkStorage.getNbt(chunkPos);
                        if (nbtCompound != null) {
                            boolean bl3;
                            int i = VersionedChunkStorage.getDataVersion(nbtCompound);
                            ChunkGenerator chunkGenerator = this.generatorOptions.getDimensions().get(GeneratorOptions.toDimensionOptionsKey(registryKey)).getChunkGenerator();
                            NbtCompound nbtCompound2 = versionedChunkStorage.updateChunkNbt(registryKey, () -> this.persistentStateManager, nbtCompound, chunkGenerator.getCodecKey());
                            ChunkPos chunkPos2 = new ChunkPos(nbtCompound2.getInt("xPos"), nbtCompound2.getInt("zPos"));
                            if (!chunkPos2.equals(chunkPos)) {
                                LOGGER.warn("Chunk {} has invalid position {}", (Object)chunkPos, (Object)chunkPos2);
                            }
                            boolean bl4 = bl3 = i < SharedConstants.getGameVersion().getWorldVersion();
                            if (this.eraseCache) {
                                bl3 = bl3 || nbtCompound2.contains("Heightmaps");
                                nbtCompound2.remove("Heightmaps");
                                bl3 = bl3 || nbtCompound2.contains("isLightOn");
                                nbtCompound2.remove("isLightOn");
                            }
                            if (bl3) {
                                versionedChunkStorage.setNbt(chunkPos, nbtCompound2);
                                bl2 = true;
                            }
                        }
                    } catch (CrashException crashException) {
                        Throwable throwable = crashException.getCause();
                        if (throwable instanceof IOException) {
                            LOGGER.error("Error upgrading chunk {}", (Object)chunkPos, (Object)throwable);
                        }
                        throw crashException;
                    } catch (IOException iOException) {
                        LOGGER.error("Error upgrading chunk {}", (Object)chunkPos, (Object)iOException);
                    }
                    if (bl2) {
                        ++this.upgradedChunkCount;
                    } else {
                        ++this.skippedChunkCount;
                    }
                    bl = true;
                }
                float h = (float)listIterator.nextIndex() / f;
                this.dimensionProgress.put((RegistryKey<World>)registryKey, h);
                g += h;
            }
            this.progress = g;
            if (bl) continue;
            this.keepUpgradingChunks = false;
        }
        this.status = new TranslatableText("optimizeWorld.stage.finished");
        for (VersionedChunkStorage versionedChunkStorage2 : immutableMap2.values()) {
            try {
                versionedChunkStorage2.close();
            } catch (IOException iOException2) {
                LOGGER.error("Error upgrading chunk", iOException2);
            }
        }
        this.persistentStateManager.save();
        l = Util.getMeasuringTimeMs() - l;
        LOGGER.info("World optimizaton finished after {} ms", (Object)l);
        this.done = true;
    }

    private List<ChunkPos> getChunkPositions(RegistryKey<World> world) {
        File file = this.session.getWorldDirectory(world).toFile();
        File file2 = new File(file, "region");
        File[] files = file2.listFiles((directory, name) -> name.endsWith(".mca"));
        if (files == null) {
            return ImmutableList.of();
        }
        ArrayList<ChunkPos> list = Lists.newArrayList();
        for (File file3 : files) {
            Matcher matcher = REGION_FILE_PATTERN.matcher(file3.getName());
            if (!matcher.matches()) continue;
            int i = Integer.parseInt(matcher.group(1)) << 5;
            int j = Integer.parseInt(matcher.group(2)) << 5;
            try (RegionFile regionFile = new RegionFile(file3.toPath(), file2.toPath(), true);){
                for (int k = 0; k < 32; ++k) {
                    for (int l = 0; l < 32; ++l) {
                        ChunkPos chunkPos = new ChunkPos(k + i, l + j);
                        if (!regionFile.isChunkValid(chunkPos)) continue;
                        list.add(chunkPos);
                    }
                }
            } catch (Throwable throwable) {
                // empty catch block
            }
        }
        return list;
    }

    public boolean isDone() {
        return this.done;
    }

    public ImmutableSet<RegistryKey<World>> getWorlds() {
        return this.generatorOptions.getWorlds();
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

