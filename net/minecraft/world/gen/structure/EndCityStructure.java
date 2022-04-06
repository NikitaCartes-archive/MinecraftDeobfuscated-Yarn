/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.StructureType;

public class EndCityStructure
extends StructureType {
    public static final Codec<EndCityStructure> CODEC = EndCityStructure.createCodec(EndCityStructure::new);

    public EndCityStructure(StructureType.Config config) {
        super(config);
    }

    @Override
    public Optional<StructureType.StructurePosition> getStructurePosition(StructureType.Context context) {
        BlockRotation blockRotation = BlockRotation.random(context.random());
        BlockPos blockPos = this.getShiftedPos(context, blockRotation);
        if (blockPos.getY() < 60) {
            return Optional.empty();
        }
        return Optional.of(new StructureType.StructurePosition(blockPos, collector -> this.addPieces((StructurePiecesCollector)collector, blockPos, blockRotation, context)));
    }

    private void addPieces(StructurePiecesCollector collector, BlockPos pos, BlockRotation rotation, StructureType.Context context) {
        ArrayList<StructurePiece> list = Lists.newArrayList();
        EndCityGenerator.addPieces(context.structureManager(), pos, rotation, list, context.random());
        list.forEach(collector::addPiece);
    }

    @Override
    public net.minecraft.structure.StructureType<?> getType() {
        return net.minecraft.structure.StructureType.END_CITY;
    }
}

