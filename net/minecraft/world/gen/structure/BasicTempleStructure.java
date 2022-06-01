/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.structure;

import java.util.Optional;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.structure.Structure;

public abstract class BasicTempleStructure
extends Structure {
    private final Constructor constructor;
    private int width;
    private int height;

    protected BasicTempleStructure(Constructor constructor, int width, int height, Structure.Config config) {
        super(config);
        this.constructor = constructor;
        this.width = width;
        this.height = height;
    }

    @Override
    public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context context) {
        if (BasicTempleStructure.getMinCornerHeight(context, this.width, this.height) < context.chunkGenerator().getSeaLevel()) {
            return Optional.empty();
        }
        return BasicTempleStructure.getStructurePosition(context, Heightmap.Type.WORLD_SURFACE_WG, collector -> this.addPieces((StructurePiecesCollector)collector, context));
    }

    private void addPieces(StructurePiecesCollector collector, Structure.Context context) {
        ChunkPos chunkPos = context.chunkPos();
        collector.addPiece(this.constructor.construct(context.random(), chunkPos.getStartX(), chunkPos.getStartZ()));
    }

    @FunctionalInterface
    protected static interface Constructor {
        public StructurePiece construct(ChunkRandom var1, int var2, int var3);
    }
}

