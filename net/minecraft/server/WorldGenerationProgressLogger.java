/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class WorldGenerationProgressLogger
implements WorldGenerationProgressListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final int totalCount;
    private int generatedCount;
    private long startTime;
    private long nextMessageTime = Long.MAX_VALUE;

    public WorldGenerationProgressLogger(int radius) {
        int i = radius * 2 + 1;
        this.totalCount = i * i;
    }

    @Override
    public void start(ChunkPos spawnPos) {
        this.startTime = this.nextMessageTime = Util.getMeasuringTimeMs();
    }

    @Override
    public void setChunkStatus(ChunkPos pos, @Nullable ChunkStatus status) {
        if (status == ChunkStatus.FULL) {
            ++this.generatedCount;
        }
        int i = this.getProgressPercentage();
        if (Util.getMeasuringTimeMs() > this.nextMessageTime) {
            this.nextMessageTime += 500L;
            LOGGER.info(new TranslatableText("menu.preparingSpawn", MathHelper.clamp(i, 0, 100)).getString());
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void start() {
    }

    @Override
    public void stop() {
        LOGGER.info("Time elapsed: {} ms", (Object)(Util.getMeasuringTimeMs() - this.startTime));
        this.nextMessageTime = Long.MAX_VALUE;
    }

    public int getProgressPercentage() {
        return MathHelper.floor((float)this.generatedCount * 100.0f / (float)this.totalCount);
    }
}

