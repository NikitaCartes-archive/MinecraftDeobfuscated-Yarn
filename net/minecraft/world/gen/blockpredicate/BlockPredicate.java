/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.BiPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.blockpredicate.AllOfBlockPredicate;
import net.minecraft.world.gen.blockpredicate.AlwaysTrueBlockPredicate;
import net.minecraft.world.gen.blockpredicate.AnyOfBlockPredicate;
import net.minecraft.world.gen.blockpredicate.BlockPredicateType;
import net.minecraft.world.gen.blockpredicate.InsideWorldBoundsBlockPredicate;
import net.minecraft.world.gen.blockpredicate.MatchingBlocksBlockPredicate;
import net.minecraft.world.gen.blockpredicate.MatchingFluidsBlockPredicate;
import net.minecraft.world.gen.blockpredicate.NotBlockPredicate;
import net.minecraft.world.gen.blockpredicate.ReplaceableBlockPredicate;
import net.minecraft.world.gen.blockpredicate.SolidBlockPredicate;
import net.minecraft.world.gen.blockpredicate.WouldSurviveBlockPredicate;

public interface BlockPredicate
extends BiPredicate<StructureWorldAccess, BlockPos> {
    public static final Codec<BlockPredicate> BASE_CODEC = Registry.BLOCK_PREDICATE_TYPE.getCodec().dispatch(BlockPredicate::getType, BlockPredicateType::codec);
    public static final BlockPredicate IS_AIR = BlockPredicate.matchingBlock(Blocks.AIR, BlockPos.ORIGIN);
    public static final BlockPredicate IS_AIR_OR_WATER = BlockPredicate.matchingBlocks(List.of(Blocks.AIR, Blocks.WATER), BlockPos.ORIGIN);

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

    public static BlockPredicate matchingBlocks(List<Block> blocks, Vec3i offset) {
        return new MatchingBlocksBlockPredicate(offset, blocks);
    }

    public static BlockPredicate matchingBlock(Block block, Vec3i offset) {
        return BlockPredicate.matchingBlocks(List.of(block), offset);
    }

    public static BlockPredicate matchingFluids(List<Fluid> fluids, Vec3i offset) {
        return new MatchingFluidsBlockPredicate(offset, fluids);
    }

    public static BlockPredicate matchingFluid(Fluid fluid, Vec3i offset) {
        return BlockPredicate.matchingFluids(List.of(fluid), offset);
    }

    public static BlockPredicate not(BlockPredicate predicate) {
        return new NotBlockPredicate(predicate);
    }

    public static BlockPredicate replaceable(Vec3i offset) {
        return new ReplaceableBlockPredicate(offset);
    }

    public static BlockPredicate replaceable() {
        return BlockPredicate.replaceable(Vec3i.ZERO);
    }

    public static BlockPredicate wouldSurvive(BlockState state, Vec3i offset) {
        return new WouldSurviveBlockPredicate(offset, state);
    }

    public static BlockPredicate solid(Vec3i offset) {
        return new SolidBlockPredicate(offset);
    }

    public static BlockPredicate solid() {
        return BlockPredicate.solid(Vec3i.ZERO);
    }

    public static BlockPredicate insideWorldBounds(Vec3i offset) {
        return new InsideWorldBoundsBlockPredicate(offset);
    }

    public static BlockPredicate alwaysTrue() {
        return AlwaysTrueBlockPredicate.instance;
    }
}

