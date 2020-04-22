package net.minecraft.world.gen.foliage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.dynamic.DynamicSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public abstract class FoliagePlacer implements DynamicSerializable {
	private final int radius;
	private final int randomRadius;
	private final int offset;
	private final int randomOffset;
	private final FoliagePlacerType<?> type;

	public FoliagePlacer(int radius, int randomRadius, int offset, int randomOffset, FoliagePlacerType<?> type) {
		this.radius = radius;
		this.randomRadius = randomRadius;
		this.offset = offset;
		this.randomOffset = randomOffset;
		this.type = type;
	}

	public void method_27385(
		ModifiableTestableWorld modifiableTestableWorld,
		Random random,
		TreeFeatureConfig treeFeatureConfig,
		int i,
		FoliagePlacer.class_5208 arg,
		int j,
		int k,
		Set<BlockPos> set
	) {
		this.generate(modifiableTestableWorld, random, treeFeatureConfig, i, arg, j, k, set, this.method_27386(random));
	}

	/**
	 * This is the main method used to generate foliage.
	 */
	protected abstract void generate(
		ModifiableTestableWorld world,
		Random random,
		TreeFeatureConfig treeFeatureConfig,
		int trunkHeight,
		FoliagePlacer.class_5208 arg,
		int foliageHeight,
		int radius,
		Set<BlockPos> leaves,
		int i
	);

	public abstract int getHeight(Random random, int trunkHeight, TreeFeatureConfig treeFeatureConfig);

	public int getRadius(Random random, int baseHeight) {
		return this.radius + random.nextInt(this.randomRadius + 1);
	}

	private int method_27386(Random random) {
		return this.offset + random.nextInt(this.randomOffset + 1);
	}

	protected abstract boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl);

	protected boolean method_27387(Random random, int i, int j, int k, int l, boolean bl) {
		int m;
		int n;
		if (bl) {
			m = Math.min(Math.abs(i), Math.abs(i - 1));
			n = Math.min(Math.abs(k), Math.abs(k - 1));
		} else {
			m = Math.abs(i);
			n = Math.abs(k);
		}

		return this.isInvalidForLeaves(random, m, j, n, l, bl);
	}

	protected void generate(
		ModifiableTestableWorld modifiableTestableWorld,
		Random random,
		TreeFeatureConfig treeFeatureConfig,
		BlockPos blockPos,
		int baseHeight,
		Set<BlockPos> set,
		int i,
		boolean bl
	) {
		int j = bl ? 1 : 0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int k = -baseHeight; k <= baseHeight + j; k++) {
			for (int l = -baseHeight; l <= baseHeight + j; l++) {
				if (!this.method_27387(random, k, i, l, baseHeight, bl)) {
					mutable.set(blockPos, k, i, l);
					if (AbstractTreeFeature.method_27371(modifiableTestableWorld, mutable)) {
						modifiableTestableWorld.setBlockState(mutable, treeFeatureConfig.leavesProvider.getBlockState(random, mutable), 19);
						set.add(mutable.toImmutable());
					}
				}
			}
		}
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(ops.createString("type"), ops.createString(Registry.FOLIAGE_PLACER_TYPE.getId(this.type).toString()))
			.put(ops.createString("radius"), ops.createInt(this.radius))
			.put(ops.createString("radius_random"), ops.createInt(this.randomRadius))
			.put(ops.createString("offset"), ops.createInt(this.offset))
			.put(ops.createString("offset_random"), ops.createInt(this.randomOffset));
		return new Dynamic<>(ops, ops.createMap(builder.build())).getValue();
	}

	public static final class class_5208 {
		private final BlockPos field_24158;
		private final int field_24159;
		private final boolean field_24160;

		public class_5208(BlockPos blockPos, int i, boolean bl) {
			this.field_24158 = blockPos;
			this.field_24159 = i;
			this.field_24160 = bl;
		}

		public BlockPos method_27388() {
			return this.field_24158;
		}

		public int method_27389() {
			return this.field_24159;
		}

		public boolean method_27390() {
			return this.field_24160;
		}
	}
}
