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

public interface BlockPredicate extends BiPredicate<StructureWorldAccess, BlockPos> {
	Codec<BlockPredicate> BASE_CODEC = Registry.BLOCK_PREDICATE_TYPE.dispatch(BlockPredicate::getType, BlockPredicateType::codec);

	BlockPredicateType<?> getType();

	static BlockPredicate allOf(List<BlockPredicate> predicates) {
		return new AllOfBlockPredicate(predicates);
	}

	static BlockPredicate allOf(BlockPredicate... predicates) {
		return allOf(List.of(predicates));
	}

	static BlockPredicate bothOf(BlockPredicate first, BlockPredicate second) {
		return allOf(List.of(first, second));
	}

	static BlockPredicate anyOf(List<BlockPredicate> predicates) {
		return new AnyOfBlockPredicate(predicates);
	}

	static BlockPredicate anyOf(BlockPredicate... predicates) {
		return anyOf(List.of(predicates));
	}

	static BlockPredicate eitherOf(BlockPredicate first, BlockPredicate second) {
		return anyOf(List.of(first, second));
	}

	static BlockPredicate matchingBlocks(List<Block> blocks, BlockPos offset) {
		return new MatchingBlocksBlockPredicate(offset, blocks);
	}

	static BlockPredicate matchingBlock(Block block, BlockPos offset) {
		return matchingBlocks(List.of(block), offset);
	}

	static BlockPredicate matchingFluids(List<Fluid> fluids, BlockPos offset) {
		return new MatchingFluidsBlockPredicate(offset, fluids);
	}

	static BlockPredicate matchingFluid(Fluid fluid, BlockPos offset) {
		return matchingFluids(List.of(fluid), offset);
	}

	static BlockPredicate not(BlockPredicate predicate) {
		return new NotBlockPredicate(predicate);
	}

	static BlockPredicate replaceable(BlockPos offset) {
		return new ReplaceableBlockPredicate(offset);
	}

	static BlockPredicate replaceable() {
		return replaceable(BlockPos.ORIGIN);
	}

	static BlockPredicate wouldSurvive(BlockState state, BlockPos offset) {
		return new WouldSurviveBlockPredicate(offset, state);
	}

	static BlockPredicate alwaysTrue() {
		return AlwaysTrueBlockPredicate.instance;
	}
}
