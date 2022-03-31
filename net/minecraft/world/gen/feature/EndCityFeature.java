/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructureType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.StructureFeature;

public class EndCityFeature
extends StructureFeature {
    public static final Codec<EndCityFeature> CODEC = EndCityFeature.createCodec(EndCityFeature::new);

    public EndCityFeature(StructureFeature.Config config) {
        super(config);
    }

    @Override
    public Optional<StructureFeature.StructurePosition> getStructurePosition(StructureFeature.Context context) {
        BlockRotation blockRotation = BlockRotation.random(context.random());
        BlockPos blockPos = this.getShiftedPos(context, blockRotation);
        if (blockPos.getY() < 60) {
            return Optional.empty();
        }
        return Optional.of(new StructureFeature.StructurePosition(blockPos, structurePiecesCollector -> this.method_39817((StructurePiecesCollector)structurePiecesCollector, blockPos, blockRotation, context)));
    }

    private void method_39817(StructurePiecesCollector collector, BlockPos blockPos, BlockRotation blockRotation, StructureFeature.Context context) {
        ArrayList<StructurePiece> list = Lists.newArrayList();
        EndCityGenerator.addPieces(context.structureManager(), blockPos, blockRotation, list, context.random());
        list.forEach(collector::addPiece);
    }

    @Override
    public StructureType<?> getType() {
        return StructureType.END_CITY;
    }
}

