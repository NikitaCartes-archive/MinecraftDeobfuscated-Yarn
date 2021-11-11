/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.List;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.StructureHolder;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

public class StructureAccessor {
    private final WorldAccess world;
    private final GeneratorOptions options;

    public StructureAccessor(WorldAccess world, GeneratorOptions options) {
        this.world = world;
        this.options = options;
    }

    public StructureAccessor forRegion(ChunkRegion region) {
        if (region.toServerWorld() != this.world) {
            throw new IllegalStateException("Using invalid feature manager (source level: " + region.toServerWorld() + ", region: " + region);
        }
        return new StructureAccessor(region, this.options);
    }

    /**
     * {@return a list of structure starts for this chunk} The structure starts
     * are computed from the structure references of the given section's chunk.
     */
    public List<? extends StructureStart<?>> getStructureStarts(ChunkSectionPos sectionPos, StructureFeature<?> feature) {
        LongSet longSet = this.world.getChunk(sectionPos.getSectionX(), sectionPos.getSectionZ(), ChunkStatus.STRUCTURE_REFERENCES).getStructureReferences(feature);
        ImmutableList.Builder builder = ImmutableList.builder();
        LongIterator longIterator = longSet.iterator();
        while (longIterator.hasNext()) {
            long l = (Long)longIterator.next();
            ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(new ChunkPos(l), this.world.getBottomSectionCoord());
            StructureStart<?> structureStart = this.getStructureStart(chunkSectionPos, feature, this.world.getChunk(chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), ChunkStatus.STRUCTURE_STARTS));
            if (structureStart == null || !structureStart.hasChildren()) continue;
            builder.add(structureStart);
        }
        return builder.build();
    }

    @Nullable
    public StructureStart<?> getStructureStart(ChunkSectionPos pos, StructureFeature<?> feature, StructureHolder holder) {
        return holder.getStructureStart(feature);
    }

    public void setStructureStart(ChunkSectionPos pos, StructureFeature<?> feature, StructureStart<?> structureStart, StructureHolder holder) {
        holder.setStructureStart(feature, structureStart);
    }

    public void addStructureReference(ChunkSectionPos pos, StructureFeature<?> feature, long reference, StructureHolder holder) {
        holder.addStructureReference(feature, reference);
    }

    public boolean shouldGenerateStructures() {
        return this.options.shouldGenerateStructures();
    }

    public StructureStart<?> getStructureAt(BlockPos pos, StructureFeature<?> structure) {
        for (StructureStart<?> structureStart : this.getStructureStarts(ChunkSectionPos.from(pos), structure)) {
            if (!structureStart.getBoundingBox().contains(pos)) continue;
            return structureStart;
        }
        return StructureStart.DEFAULT;
    }

    /**
     * {@return a structure that contains the given {@code pos}} Compared to
     * {@link #getStructureAt}, this does not return a structure if the given
     * position is in the expanded bounding box of the structure but not in any
     * child piece of it.
     */
    public StructureStart<?> getStructureContaining(BlockPos pos, StructureFeature<?> structure) {
        for (StructureStart<?> structureStart : this.getStructureStarts(ChunkSectionPos.from(pos), structure)) {
            for (StructurePiece structurePiece : structureStart.getChildren()) {
                if (!structurePiece.getBoundingBox().contains(pos)) continue;
                return structureStart;
            }
        }
        return StructureStart.DEFAULT;
    }

    public boolean hasStructureReferences(BlockPos pos) {
        ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(pos);
        return this.world.getChunk(chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), ChunkStatus.STRUCTURE_REFERENCES).hasStructureReferences();
    }
}

