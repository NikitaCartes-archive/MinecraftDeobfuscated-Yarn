package net.minecraft.block;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import java.util.Optional;
import java.util.function.Supplier;

public interface Oxidizable extends Degradable<Oxidizable.OxidizationLevel> {
	Supplier<BiMap<Block, Block>> OXIDATION_LEVEL_INCREASES = Suppliers.memoize(
		() -> ImmutableBiMap.<Block, Block>builder()
				.put(Blocks.COPPER_BLOCK, Blocks.EXPOSED_COPPER)
				.put(Blocks.EXPOSED_COPPER, Blocks.WEATHERED_COPPER)
				.put(Blocks.WEATHERED_COPPER, Blocks.OXIDIZED_COPPER)
				.put(Blocks.CUT_COPPER, Blocks.EXPOSED_CUT_COPPER)
				.put(Blocks.EXPOSED_CUT_COPPER, Blocks.WEATHERED_CUT_COPPER)
				.put(Blocks.WEATHERED_CUT_COPPER, Blocks.OXIDIZED_CUT_COPPER)
				.put(Blocks.CUT_COPPER_SLAB, Blocks.EXPOSED_CUT_COPPER_SLAB)
				.put(Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER_SLAB)
				.put(Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.OXIDIZED_CUT_COPPER_SLAB)
				.put(Blocks.CUT_COPPER_STAIRS, Blocks.EXPOSED_CUT_COPPER_STAIRS)
				.put(Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.WEATHERED_CUT_COPPER_STAIRS)
				.put(Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_CUT_COPPER_STAIRS)
				.build()
	);
	Supplier<BiMap<Block, Block>> OXIDATION_LEVEL_DECREASES = Suppliers.memoize(() -> ((BiMap)OXIDATION_LEVEL_INCREASES.get()).inverse());

	static Optional<Block> getDecreasedOxidationBlock(Block block) {
		return Optional.ofNullable((Block)((BiMap)OXIDATION_LEVEL_DECREASES.get()).get(block));
	}

	static Block getUnaffectedOxidationBlock(Block block) {
		Block block2 = block;

		for (Block block3 = (Block)((BiMap)OXIDATION_LEVEL_DECREASES.get()).get(block);
			block3 != null;
			block3 = (Block)((BiMap)OXIDATION_LEVEL_DECREASES.get()).get(block3)
		) {
			block2 = block3;
		}

		return block2;
	}

	static Optional<BlockState> getDecreasedOxidationState(BlockState state) {
		return getDecreasedOxidationBlock(state.getBlock()).map(block -> block.getStateWithProperties(state));
	}

	static Optional<Block> getIncreasedOxidationBlock(Block block) {
		return Optional.ofNullable((Block)((BiMap)OXIDATION_LEVEL_INCREASES.get()).get(block));
	}

	static BlockState getUnaffectedOxidationState(BlockState state) {
		return getUnaffectedOxidationBlock(state.getBlock()).getStateWithProperties(state);
	}

	@Override
	default Optional<BlockState> getDegradationResult(BlockState state) {
		return getIncreasedOxidationBlock(state.getBlock()).map(block -> block.getStateWithProperties(state));
	}

	@Override
	default float getDegradationChanceMultiplier() {
		return this.getDegradationLevel() == Oxidizable.OxidizationLevel.UNAFFECTED ? 0.75F : 1.0F;
	}

	public static enum OxidizationLevel {
		UNAFFECTED,
		EXPOSED,
		WEATHERED,
		OXIDIZED;
	}
}
