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
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

public class StrongholdStructure
extends Structure {
    public static final Codec<StrongholdStructure> CODEC = StrongholdStructure.createCodec(StrongholdStructure::new);

    public StrongholdStructure(Structure.Config config) {
        super(config);
    }

    @Override
    public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context context) {
        return Optional.of(new Structure.StructurePosition(context.chunkPos().getStartPos(), collector -> StrongholdStructure.addPieces(collector, context)));
    }

    private static void addPieces(StructurePiecesCollector collector, Structure.Context context) {
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
    public StructureType<?> getType() {
        return StructureType.STRONGHOLD;
    }
}

