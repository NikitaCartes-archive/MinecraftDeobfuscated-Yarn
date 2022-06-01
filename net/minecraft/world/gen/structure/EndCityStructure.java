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
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

public class EndCityStructure
extends Structure {
    public static final Codec<EndCityStructure> CODEC = EndCityStructure.createCodec(EndCityStructure::new);

    public EndCityStructure(Structure.Config config) {
        super(config);
    }

    @Override
    public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context context) {
        BlockRotation blockRotation = BlockRotation.random(context.random());
        BlockPos blockPos = this.getShiftedPos(context, blockRotation);
        if (blockPos.getY() < 60) {
            return Optional.empty();
        }
        return Optional.of(new Structure.StructurePosition(blockPos, collector -> this.addPieces((StructurePiecesCollector)collector, blockPos, blockRotation, context)));
    }

    private void addPieces(StructurePiecesCollector collector, BlockPos pos, BlockRotation rotation, Structure.Context context) {
        ArrayList<StructurePiece> list = Lists.newArrayList();
        EndCityGenerator.addPieces(context.structureTemplateManager(), pos, rotation, list, context.random());
        list.forEach(collector::addPiece);
    }

    @Override
    public StructureType<?> getType() {
        return StructureType.END_CITY;
    }
}

