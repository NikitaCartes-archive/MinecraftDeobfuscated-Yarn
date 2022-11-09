package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.BiPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.StructureWorldAccess;

public interface BlockPredicate extends BiPredicate<StructureWorldAccess, BlockPos> {
	Codec<BlockPredicate> BASE_CODEC = Registries.BLOCK_PREDICATE_TYPE.getCodec().dispatch(BlockPredicate::getType, BlockPredicateType::codec);
	BlockPredicate IS_AIR = matchingBlocks(Blocks.AIR);
	BlockPredicate IS_AIR_OR_WATER = matchingBlocks(Blocks.AIR, Blocks.WATER);

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

	static BlockPredicate matchingBlocks(Vec3i offset, List<Block> blocks) {
		return new MatchingBlocksBlockPredicate(offset, RegistryEntryList.of(Block::getRegistryEntry, blocks));
	}

	static BlockPredicate matchingBlocks(List<Block> blocks) {
		return matchingBlocks(Vec3i.ZERO, blocks);
	}

	static BlockPredicate matchingBlocks(Vec3i offset, Block... blocks) {
		return matchingBlocks(offset, List.of(blocks));
	}

	static BlockPredicate matchingBlocks(Block... blocks) {
		return matchingBlocks(Vec3i.ZERO, blocks);
	}

	static BlockPredicate matchingBlockTag(Vec3i offset, TagKey<Block> tag) {
		return new MatchingBlockTagPredicate(offset, tag);
	}

	static BlockPredicate matchingBlockTag(TagKey<Block> offset) {
		return matchingBlockTag(Vec3i.ZERO, offset);
	}

	static BlockPredicate matchingFluids(Vec3i offset, List<Fluid> fluids) {
		return new MatchingFluidsBlockPredicate(offset, RegistryEntryList.of(Fluid::getRegistryEntry, fluids));
	}

	static BlockPredicate matchingFluids(Vec3i offset, Fluid... fluids) {
		return matchingFluids(offset, List.of(fluids));
	}

	static BlockPredicate matchingFluids(Fluid... fluids) {
		return matchingFluids(Vec3i.ZERO, fluids);
	}

	static BlockPredicate not(BlockPredicate predicate) {
		return new NotBlockPredicate(predicate);
	}

	static BlockPredicate replaceable(Vec3i offset) {
		return new ReplaceableBlockPredicate(offset);
	}

	static BlockPredicate replaceable() {
		return replaceable(Vec3i.ZERO);
	}

	static BlockPredicate wouldSurvive(BlockState state, Vec3i offset) {
		return new WouldSurviveBlockPredicate(offset, state);
	}

	static BlockPredicate hasSturdyFace(Vec3i offset, Direction face) {
		return new HasSturdyFacePredicate(offset, face);
	}

	static BlockPredicate hasSturdyFace(Direction face) {
		return hasSturdyFace(Vec3i.ZERO, face);
	}

	static BlockPredicate solid(Vec3i offset) {
		return new SolidBlockPredicate(offset);
	}

	static BlockPredicate solid() {
		return solid(Vec3i.ZERO);
	}

	static BlockPredicate noFluid() {
		return noFluid(Vec3i.ZERO);
	}

	static BlockPredicate noFluid(Vec3i offset) {
		return matchingFluids(offset, Fluids.EMPTY);
	}

	static BlockPredicate insideWorldBounds(Vec3i offset) {
		return new InsideWorldBoundsBlockPredicate(offset);
	}

	static BlockPredicate alwaysTrue() {
		return AlwaysTrueBlockPredicate.instance;
	}
}
