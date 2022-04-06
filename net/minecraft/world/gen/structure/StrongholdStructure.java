/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.structure;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import net.minecraft.structure.StrongholdGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.world.gen.structure.StructureType;

public class StrongholdStructure
extends StructureType {
    public static final Codec<StrongholdStructure> CODEC = StrongholdStructure.createCodec(StrongholdStructure::new);

    public StrongholdStructure(StructureType.Config config) {
        super(config);
    }

    @Override
    public Optional<StructureType.StructurePosition> getStructurePosition(StructureType.Context context) {
        return Optional.of(new StructureType.StructurePosition(context.chunkPos().getStartPos(), collector -> StrongholdStructure.addPieces(collector, context)));
    }

    private static void addPieces(StructurePiecesCollector collector, StructureType.Context context) {
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

    @Override
    public net.minecraft.structure.StructureType<?> getType() {
        return net.minecraft.structure.StructureType.STRONGHOLD;
    }
}

