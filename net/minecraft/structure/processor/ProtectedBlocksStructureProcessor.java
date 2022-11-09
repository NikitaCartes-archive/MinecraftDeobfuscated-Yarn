/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.processor;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.Feature;
import org.jetbrains.annotations.Nullable;

public class ProtectedBlocksStructureProcessor
extends StructureProcessor {
    public final TagKey<Block> protectedBlocksTag;
    public static final Codec<ProtectedBlocksStructureProcessor> CODEC = TagKey.codec(RegistryKeys.BLOCK).xmap(ProtectedBlocksStructureProcessor::new, processor -> processor.protectedBlocksTag);

    public ProtectedBlocksStructureProcessor(TagKey<Block> protectedBlocksTag) {
        this.protectedBlocksTag = protectedBlocksTag;
    }

    @Override
    @Nullable
    public StructureTemplate.StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos pivot, StructureTemplate.StructureBlockInfo originalBlockInfo, StructureTemplate.StructureBlockInfo currentBlockInfo, StructurePlacementData data) {
        if (Feature.notInBlockTagPredicate(this.protectedBlocksTag).test(world.getBlockState(currentBlockInfo.pos))) {
            return currentBlockInfo;
        }
        return null;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.PROTECTED_BLOCKS;
    }
}

