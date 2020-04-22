package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class class_5212 extends TrunkPlacer {
	public class_5212(int i, int j, int k) {
		this(i, j, k, TrunkPlacerType.FANCY_TRUNK_PLACER);
	}

	public class_5212(int i, int j, int k, TrunkPlacerType<? extends class_5212> trunkPlacerType) {
		super(i, j, k, trunkPlacerType);
	}

	public <T> class_5212(Dynamic<T> dynamic) {
		this(dynamic.get("base_height").asInt(0), dynamic.get("height_rand_a").asInt(0), dynamic.get("height_rand_b").asInt(0));
	}

	@Override
	public List<FoliagePlacer.class_5208> generate(
		ModifiableTestableWorld world, Random random, int trunkHeight, BlockPos pos, Set<BlockPos> set, BlockBox blockBox, TreeFeatureConfig treeFeatureConfig
	) {
		int i = 5;
		int j = trunkHeight + 2;
		int k = MathHelper.floor((double)j * 0.618);
		method_27400(world, pos.down());
		double d = 1.0;
		int l = Math.min(1, MathHelper.floor(1.382 + Math.pow(1.0 * (double)j / 13.0, 2.0)));
		int m = pos.getY() + k;
		int n = j - 5;
		List<class_5212.class_5213> list = Lists.<class_5212.class_5213>newArrayList();
		list.add(new class_5212.class_5213(pos.up(n), m));

		for (; n >= 0; n--) {
			float f = this.method_27396(j, n);
			if (!(f < 0.0F)) {
				for (int o = 0; o < l; o++) {
					double e = 1.0;
					double g = 1.0 * (double)f * ((double)random.nextFloat() + 0.328);
					double h = (double)(random.nextFloat() * 2.0F) * Math.PI;
					double p = g * Math.sin(h) + 0.5;
					double q = g * Math.cos(h) + 0.5;
					BlockPos blockPos = pos.add(p, (double)(n - 1), q);
					BlockPos blockPos2 = blockPos.up(5);
					if (this.method_27393(world, random, blockPos, blockPos2, false, set, blockBox, treeFeatureConfig)) {
						int r = pos.getX() - blockPos.getX();
						int s = pos.getZ() - blockPos.getZ();
						double t = (double)blockPos.getY() - Math.sqrt((double)(r * r + s * s)) * 0.381;
						int u = t > (double)m ? m : (int)t;
						BlockPos blockPos3 = new BlockPos(pos.getX(), u, pos.getZ());
						if (this.method_27393(world, random, blockPos3, blockPos, false, set, blockBox, treeFeatureConfig)) {
							list.add(new class_5212.class_5213(blockPos, blockPos3.getY()));
						}
					}
				}
			}
		}

		this.method_27393(world, random, pos, pos.up(k), true, set, blockBox, treeFeatureConfig);
		this.method_27392(world, random, j, pos, list, set, blockBox, treeFeatureConfig);
		List<FoliagePlacer.class_5208> list2 = Lists.<FoliagePlacer.class_5208>newArrayList();

		for (class_5212.class_5213 lv : list) {
			if (this.method_27391(j, lv.getWidth() - pos.getY())) {
				list2.add(lv.field_24169);
			}
		}

		return list2;
	}

	private boolean method_27393(
		ModifiableTestableWorld modifiableTestableWorld,
		Random random,
		BlockPos blockPos,
		BlockPos blockPos2,
		boolean bl,
		Set<BlockPos> set,
		BlockBox blockBox,
		TreeFeatureConfig treeFeatureConfig
	) {
		if (!bl && Objects.equals(blockPos, blockPos2)) {
			return true;
		} else {
			BlockPos blockPos3 = blockPos2.add(-blockPos.getX(), -blockPos.getY(), -blockPos.getZ());
			int i = this.method_27394(blockPos3);
			float f = (float)blockPos3.getX() / (float)i;
			float g = (float)blockPos3.getY() / (float)i;
			float h = (float)blockPos3.getZ() / (float)i;

			for (int j = 0; j <= i; j++) {
				BlockPos blockPos4 = blockPos.add((double)(0.5F + (float)j * f), (double)(0.5F + (float)j * g), (double)(0.5F + (float)j * h));
				if (bl) {
					method_27404(
						modifiableTestableWorld,
						blockPos4,
						treeFeatureConfig.trunkProvider.getBlockState(random, blockPos4).with(PillarBlock.AXIS, this.method_27395(blockPos, blockPos4)),
						blockBox
					);
					set.add(blockPos4.toImmutable());
				} else if (!AbstractTreeFeature.canTreeReplace(modifiableTestableWorld, blockPos4)) {
					return false;
				}
			}

			return true;
		}
	}

	private int method_27394(BlockPos blockPos) {
		int i = MathHelper.abs(blockPos.getX());
		int j = MathHelper.abs(blockPos.getY());
		int k = MathHelper.abs(blockPos.getZ());
		return Math.max(i, Math.max(j, k));
	}

	private Direction.Axis method_27395(BlockPos blockPos, BlockPos blockPos2) {
		Direction.Axis axis = Direction.Axis.Y;
		int i = Math.abs(blockPos2.getX() - blockPos.getX());
		int j = Math.abs(blockPos2.getZ() - blockPos.getZ());
		int k = Math.max(i, j);
		if (k > 0) {
			if (i == k) {
				axis = Direction.Axis.X;
			} else {
				axis = Direction.Axis.Z;
			}
		}

		return axis;
	}

	private boolean method_27391(int i, int j) {
		return (double)j >= (double)i * 0.2;
	}

	private void method_27392(
		ModifiableTestableWorld modifiableTestableWorld,
		Random random,
		int i,
		BlockPos blockPos,
		List<class_5212.class_5213> list,
		Set<BlockPos> set,
		BlockBox blockBox,
		TreeFeatureConfig treeFeatureConfig
	) {
		for (class_5212.class_5213 lv : list) {
			int j = lv.getWidth();
			BlockPos blockPos2 = new BlockPos(blockPos.getX(), j, blockPos.getZ());
			if (!blockPos2.equals(lv.field_24169.method_27388()) && this.method_27391(i, j - blockPos.getY())) {
				this.method_27393(modifiableTestableWorld, random, blockPos2, lv.field_24169.method_27388(), true, set, blockBox, treeFeatureConfig);
			}
		}
	}

	private float method_27396(int i, int j) {
		if ((float)j < (float)i * 0.3F) {
			return -1.0F;
		} else {
			float f = (float)i / 2.0F;
			float g = f - (float)j;
			float h = MathHelper.sqrt(f * f - g * g);
			if (g == 0.0F) {
				h = f;
			} else if (Math.abs(g) >= f) {
				return 0.0F;
			}

			return h * 0.5F;
		}
	}

	static class class_5213 {
		private final FoliagePlacer.class_5208 field_24169;
		private final int _width;

		public class_5213(BlockPos blockPos, int width) {
			this.field_24169 = new FoliagePlacer.class_5208(blockPos, 0, false);
			this._width = width;
		}

		public int getWidth() {
			return this._width;
		}
	}
}
