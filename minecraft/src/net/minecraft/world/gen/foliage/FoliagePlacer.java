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
import net.minecraft.world.gen.feature.NormalTreeFeatureConfig;

public abstract class FoliagePlacer implements DynamicSerializable {
	protected final int field_21296;
	protected final int field_21297;
	protected final FoliagePlacerType<?> field_21298;

	public FoliagePlacer(int i, int j, FoliagePlacerType<?> foliagePlacerType) {
		this.field_21296 = i;
		this.field_21297 = j;
		this.field_21298 = foliagePlacerType;
	}

	public abstract void method_23448(
		ModifiableTestableWorld modifiableTestableWorld,
		Random random,
		NormalTreeFeatureConfig normalTreeFeatureConfig,
		int i,
		int j,
		int k,
		BlockPos blockPos,
		Set<BlockPos> set
	);

	public abstract int method_23452(Random random, int i, int j, NormalTreeFeatureConfig normalTreeFeatureConfig);

	protected abstract boolean method_23451(Random random, int i, int j, int k, int l, int m);

	public abstract int method_23447(int i, int j, int k, int l);

	protected void method_23449(
		ModifiableTestableWorld modifiableTestableWorld,
		Random random,
		NormalTreeFeatureConfig normalTreeFeatureConfig,
		int i,
		BlockPos blockPos,
		int j,
		int k,
		Set<BlockPos> set
	) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int l = -k; l <= k; l++) {
			for (int m = -k; m <= k; m++) {
				if (!this.method_23451(random, i, l, j, m, k)) {
					mutable.set(l + blockPos.getX(), j + blockPos.getY(), m + blockPos.getZ());
					this.method_23450(modifiableTestableWorld, random, mutable, normalTreeFeatureConfig, set);
				}
			}
		}
	}

	protected void method_23450(
		ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, NormalTreeFeatureConfig normalTreeFeatureConfig, Set<BlockPos> set
	) {
		if (AbstractTreeFeature.isAirOrLeaves(modifiableTestableWorld, blockPos)
			|| AbstractTreeFeature.isReplaceablePlant(modifiableTestableWorld, blockPos)
			|| AbstractTreeFeature.isWater(modifiableTestableWorld, blockPos)) {
			modifiableTestableWorld.setBlockState(blockPos, normalTreeFeatureConfig.leavesProvider.getBlockState(random, blockPos), 19);
			set.add(blockPos);
		}
	}

	@Override
	public <T> T serialize(DynamicOps<T> dynamicOps) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(dynamicOps.createString("type"), dynamicOps.createString(Registry.FOLIAGE_PLACER_TYPE.getId(this.field_21298).toString()))
			.put(dynamicOps.createString("radius"), dynamicOps.createInt(this.field_21296))
			.put(dynamicOps.createString("radius_random"), dynamicOps.createInt(this.field_21296));
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(builder.build())).getValue();
	}
}
