/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.processor;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class BlockRotStructureProcessor
extends StructureProcessor {
    public static final Codec<BlockRotStructureProcessor> CODEC = RecordCodecBuilder.create(instance -> instance.group(RegistryCodecs.entryList(Registry.BLOCK_KEY).optionalFieldOf("rottable_blocks").forGetter(processor -> processor.rottableBlocks), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("integrity")).forGetter(processor -> Float.valueOf(processor.integrity))).apply((Applicative<BlockRotStructureProcessor, ?>)instance, BlockRotStructureProcessor::new));
    private final Optional<RegistryEntryList<Block>> rottableBlocks;
    private final float integrity;

    public BlockRotStructureProcessor(RegistryEntryList<Block> rottableBlocks, float integrity) {
        this(Optional.of(rottableBlocks), integrity);
    }

    public BlockRotStructureProcessor(float integrity) {
        this(Optional.empty(), integrity);
    }

    private BlockRotStructureProcessor(Optional<RegistryEntryList<Block>> rottableBlocks, float integrity) {
        this.integrity = integrity;
        this.rottableBlocks = rottableBlocks;
    }

    @Override
    @Nullable
    public StructureTemplate.StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos pivot, StructureTemplate.StructureBlockInfo originalBlockInfo, StructureTemplate.StructureBlockInfo currentBlockInfo, StructurePlacementData data) {
        Random random = data.getRandom(currentBlockInfo.pos);
        if (this.rottableBlocks.isPresent() && !originalBlockInfo.state.isIn(this.rottableBlocks.get()) || random.nextFloat() <= this.integrity) {
            return currentBlockInfo;
        }
        return null;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.BLOCK_ROT;
    }
}

