/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.blockpredicate;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.blockpredicate.BlockPredicateType;
import net.minecraft.world.gen.blockpredicate.OffsetPredicate;

class MatchingBlocksBlockPredicate
extends OffsetPredicate {
    private final List<Block> blocks;
    public static final Codec<MatchingBlocksBlockPredicate> CODEC = RecordCodecBuilder.create(instance -> MatchingBlocksBlockPredicate.method_39013(instance).and(((MapCodec)Registry.BLOCK.listOf().fieldOf("blocks")).forGetter(matchingBlocksBlockPredicate -> matchingBlocksBlockPredicate.blocks)).apply((Applicative<MatchingBlocksBlockPredicate, ?>)instance, MatchingBlocksBlockPredicate::new));

    public MatchingBlocksBlockPredicate(BlockPos offset, List<Block> blocks) {
        super(offset);
        this.blocks = blocks;
    }

    @Override
    protected boolean test(BlockState state) {
        return this.blocks.contains(state.getBlock());
    }

    @Override
    public BlockPredicateType<?> getType() {
        return BlockPredicateType.MATCHING_BLOCKS;
    }
}

