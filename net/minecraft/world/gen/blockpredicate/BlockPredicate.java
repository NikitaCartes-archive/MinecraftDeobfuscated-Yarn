/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.BiPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.blockpredicate.AllOfBlockPredicate;
import net.minecraft.world.gen.blockpredicate.AnyOfBlockPredicate;
import net.minecraft.world.gen.blockpredicate.BlockPredicateType;
import net.minecraft.world.gen.blockpredicate.MatchingBlocksBlockPredicate;
import net.minecraft.world.gen.blockpredicate.MatchingFluidsBlockPredicate;
import net.minecraft.world.gen.blockpredicate.NotBlockPredicate;
import net.minecraft.world.gen.blockpredicate.ReplaceableBlockPredicate;
import net.minecraft.world.gen.blockpredicate.TrueBlockPredicate;
import net.minecraft.world.gen.blockpredicate.WouldSurviveBlockPredicate;

public interface BlockPredicate
extends BiPredicate<StructureWorldAccess, BlockPos> {
    public static final Codec<BlockPredicate> BASE_CODEC = Registry.BLOCK_PREDICATE_TYPE.dispatch(BlockPredicate::getType, BlockPredicateType::codec);

    public BlockPredicateType<?> getType();

    public static BlockPredicate allOf(List<BlockPredicate> predicates) {
        return new AllOfBlockPredicate(predicates);
    }

    public static BlockPredicate allOf(BlockPredicate ... predicates) {
        return BlockPredicate.allOf(List.of(predicates));
    }

    public static BlockPredicate bothOf(BlockPredicate first, BlockPredicate second) {
        return BlockPredicate.allOf(List.of(first, second));
    }

    public static BlockPredicate anyOf(List<BlockPredicate> predicates) {
        return new AnyOfBlockPredicate(predicates);
    }

    public static BlockPredicate anyOf(BlockPredicate ... predicates) {
        return BlockPredicate.anyOf(List.of(predicates));
    }

    public static BlockPredicate eitherOf(BlockPredicate first, BlockPredicate second) {
        return BlockPredicate.anyOf(List.of(first, second));
    }

    public static BlockPredicate matchingBlocks(List<Block> blocks, BlockPos offset) {
        return new MatchingBlocksBlockPredicate(offset, blocks);
    }

    public static BlockPredicate matchingBlock(Block block, BlockPos offset) {
        return BlockPredicate.matchingBlocks(List.of(block), offset);
    }

    public static BlockPredicate matchingFluids(List<Fluid> fluids, BlockPos offset) {
        return new MatchingFluidsBlockPredicate(offset, fluids);
    }

    public static BlockPredicate matchingFluid(Fluid fluid, BlockPos offset) {
        return BlockPredicate.matchingFluids(List.of(fluid), offset);
    }

    public static BlockPredicate not(BlockPredicate predicate) {
        return new NotBlockPredicate(predicate);
    }

    public static BlockPredicate replaceable(BlockPos offset) {
        return new ReplaceableBlockPredicate(offset);
    }

    public static BlockPredicate replaceable() {
        return BlockPredicate.replaceable(BlockPos.ORIGIN);
    }

    public static BlockPredicate wouldSurvive(BlockState state, BlockPos offset) {
        return new WouldSurviveBlockPredicate(offset, state);
    }

    public static BlockPredicate alwaysTrue() {
        return TrueBlockPredicate.INSTANCE;
    }
}

