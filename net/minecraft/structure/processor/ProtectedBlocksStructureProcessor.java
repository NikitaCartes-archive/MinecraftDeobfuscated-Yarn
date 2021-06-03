/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.processor;

import com.mojang.serialization.Codec;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.Feature;
import org.jetbrains.annotations.Nullable;

public class ProtectedBlocksStructureProcessor
extends StructureProcessor {
    public final Identifier protectedBlocksTag;
    public static final Codec<ProtectedBlocksStructureProcessor> CODEC = Identifier.CODEC.xmap(ProtectedBlocksStructureProcessor::new, protectedBlocksStructureProcessor -> protectedBlocksStructureProcessor.protectedBlocksTag);

    public ProtectedBlocksStructureProcessor(Identifier protectedBlocksTag) {
        this.protectedBlocksTag = protectedBlocksTag;
    }

    @Override
    @Nullable
    public Structure.StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos pivot, Structure.StructureBlockInfo structureBlockInfo, Structure.StructureBlockInfo structureBlockInfo2, StructurePlacementData data) {
        if (Feature.notInBlockTagPredicate(this.protectedBlocksTag).test(world.getBlockState(structureBlockInfo2.pos))) {
            return structureBlockInfo2;
        }
        return null;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.PROTECTED_BLOCKS;
    }
}

