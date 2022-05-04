/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.processor;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.Feature;
import org.jetbrains.annotations.Nullable;

public class ProtectedBlocksStructureProcessor
extends StructureProcessor {
    public final TagKey<Block> protectedBlocksTag;
    public static final Codec<ProtectedBlocksStructureProcessor> CODEC = TagKey.codec(Registry.BLOCK_KEY).xmap(ProtectedBlocksStructureProcessor::new, protectedBlocksStructureProcessor -> protectedBlocksStructureProcessor.protectedBlocksTag);

    public ProtectedBlocksStructureProcessor(TagKey<Block> tagKey) {
        this.protectedBlocksTag = tagKey;
    }

    @Override
    @Nullable
    public Structure.StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos pivot, Structure.StructureBlockInfo originalBlockInfo, Structure.StructureBlockInfo currentBlockInfo, StructurePlacementData data) {
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

