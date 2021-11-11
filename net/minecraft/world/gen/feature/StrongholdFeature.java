/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.structure.StrongholdGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.MarginedStructureFeature;

public class StrongholdFeature
extends MarginedStructureFeature<DefaultFeatureConfig> {
    public StrongholdFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec, StrongholdFeature::addPieces);
    }

    @Override
    protected boolean shouldStartAt(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, ChunkPos chunkPos, DefaultFeatureConfig defaultFeatureConfig, HeightLimitView heightLimitView) {
        return chunkGenerator.isStrongholdStartingChunk(chunkPos);
    }

    private static void addPieces(StructurePiecesCollector collector, DefaultFeatureConfig config, StructurePiecesGenerator.Context context) {
        StrongholdGenerator.Start start;
        int i = 0;
        do {
            collector.clear();
            context.random().setCarverSeed(context.seed() + (long)i++, context.chunkPos().x, context.chunkPos().z);
            StrongholdGenerator.init();
            start = new StrongholdGenerator.Start(context.random(), context.chunkPos().getOffsetX(2), context.chunkPos().getOffsetZ(2));
            collector.addPiece(start);
            start.fillOpenings(start, collector, context.random());
            List<StructurePiece> list = start.pieces;
            while (!list.isEmpty()) {
                int j = context.random().nextInt(list.size());
                StructurePiece structurePiece = list.remove(j);
                structurePiece.fillOpenings(start, collector, context.random());
            }
            collector.shiftInto(context.chunkGenerator().getSeaLevel(), context.chunkGenerator().getMinimumY(), context.random(), 10);
        } while (collector.isEmpty() || start.portalRoom == null);
    }
}

