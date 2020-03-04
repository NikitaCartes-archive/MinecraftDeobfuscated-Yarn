package net.minecraft.world.gen.foliage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;

public abstract class FoliagePlacer implements DynamicSerializable {
	protected final int radius;
	protected final int randomRadius;
	protected final FoliagePlacerType<?> type;

	public FoliagePlacer(int radius, int randomRadius, FoliagePlacerType<?> type) {
		this.radius = radius;
		this.randomRadius = randomRadius;
		this.type = type;
	}

	/**
	 * This is the main method used to generate foliage.
	 * 
	 * @param baseHeight the height of the full tree
	 * @param trunkHeight the height of just the trunk, or the part of the tree without leaves
	 * @param radius the radius of the foliage
	 */
	public abstract void generate(
		ModifiableTestableWorld world,
		Random random,
		BranchedTreeFeatureConfig config,
		int baseHeight,
		int trunkHeight,
		int radius,
		BlockPos pos,
		Set<BlockPos> leaves
	);

	public abstract int getRadius(Random random, int baseHeight, int trunkHeight, BranchedTreeFeatureConfig config);

	protected abstract boolean isInvalidForLeaves(Random random, int baseHeight, int x, int y, int z, int radius);

	/**
	 * This method is used to ensure that a tree can place foliage when it generates.
	 * 
	 * <p>It runs for every y-level of the tree being generated.
	 * 
	 * @param trunkHeight the height of the trunk
	 * @param baseHeight the height of the full tree
	 * @param radius the radius of the foliage
	 * @param currentTreeHeight the current y-level of the tree being tested.
	 */
	public abstract int getRadiusForPlacement(int trunkHeight, int baseHeight, int radius, int currentTreeHeight);

	protected void generate(
		ModifiableTestableWorld world, Random random, BranchedTreeFeatureConfig config, int baseHeight, BlockPos pos, int y, int radius, Set<BlockPos> leaves
	) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = -radius; i <= radius; i++) {
			for (int j = -radius; j <= radius; j++) {
				if (!this.isInvalidForLeaves(random, baseHeight, i, y, j, radius)) {
					mutable.set(i + pos.getX(), y + pos.getY(), j + pos.getZ());
					this.placeLeaves(world, random, mutable, config, leaves);
				}
			}
		}
	}

	protected void placeLeaves(ModifiableTestableWorld world, Random random, BlockPos pos, BranchedTreeFeatureConfig config, Set<BlockPos> leaves) {
		if (AbstractTreeFeature.isAirOrLeaves(world, pos) || AbstractTreeFeature.isReplaceablePlant(world, pos) || AbstractTreeFeature.isWater(world, pos)) {
			world.setBlockState(pos, config.leavesProvider.getBlockState(random, pos), 19);
			leaves.add(pos.toImmutable());
		}
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(ops.createString("type"), ops.createString(Registry.FOLIAGE_PLACER_TYPE.getId(this.type).toString()))
			.put(ops.createString("radius"), ops.createInt(this.radius))
			.put(ops.createString("radius_random"), ops.createInt(this.randomRadius));
		return new Dynamic<>(ops, ops.createMap(builder.build())).getValue();
	}
}
