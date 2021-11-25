/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.chunk;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.PalettedContainer;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class RenderedChunk {
    private final Map<BlockPos, BlockEntity> blockEntities;
    @Nullable
    private final List<PalettedContainer<BlockState>> blockStateContainers;
    private final boolean debugWorld;
    private final WorldChunk chunk;

    RenderedChunk(WorldChunk chunk) {
        this.chunk = chunk;
        this.debugWorld = chunk.getWorld().isDebugWorld();
        this.blockEntities = ImmutableMap.copyOf(chunk.getBlockEntities());
        if (chunk instanceof EmptyChunk) {
            this.blockStateContainers = null;
        } else {
            ChunkSection[] chunkSections = chunk.getSectionArray();
            this.blockStateContainers = new ArrayList<PalettedContainer<BlockState>>(chunkSections.length);
            for (ChunkSection chunkSection : chunkSections) {
                this.blockStateContainers.add(chunkSection.isEmpty() ? null : chunkSection.getBlockStateContainer().copy());
            }
        }
    }

    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos) {
        return this.blockEntities.get(pos);
    }

    public BlockState getBlockState(BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        if (this.debugWorld) {
            BlockState blockState = null;
            if (j == 60) {
                blockState = Blocks.BARRIER.getDefaultState();
            }
            if (j == 70) {
                blockState = DebugChunkGenerator.getBlockState(i, k);
            }
            return blockState == null ? Blocks.AIR.getDefaultState() : blockState;
        }
        if (this.blockStateContainers == null) {
            return Blocks.AIR.getDefaultState();
        }
        try {
            PalettedContainer<BlockState> palettedContainer;
            int l = this.chunk.getSectionIndex(j);
            if (l >= 0 && l < this.blockStateContainers.size() && (palettedContainer = this.blockStateContainers.get(l)) != null) {
                return palettedContainer.get(i & 0xF, j & 0xF, k & 0xF);
            }
            return Blocks.AIR.getDefaultState();
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Getting block state");
            CrashReportSection crashReportSection = crashReport.addElement("Block being got");
            crashReportSection.add("Location", () -> CrashReportSection.createPositionString((HeightLimitView)this.chunk, i, j, k));
            throw new CrashException(crashReport);
        }
    }
}

