package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LogBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;

public class LargeOakTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig> {
	private static final BlockState LOG = Blocks.field_10431.getDefaultState();
	private static final BlockState LEAVES = Blocks.field_10503.getDefaultState();

	public LargeOakTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl) {
		super(function, bl);
	}

	private void method_12811(ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, float f) {
		int i = (int)((double)f + 0.618);

		for (int j = -i; j <= i; j++) {
			for (int k = -i; k <= i; k++) {
				if (Math.pow((double)Math.abs(j) + 0.5, 2.0) + Math.pow((double)Math.abs(k) + 0.5, 2.0) <= (double)(f * f)) {
					BlockPos blockPos2 = blockPos.add(j, 0, k);
					if (isAirOrLeaves(modifiableTestableWorld, blockPos2)) {
						this.setBlockState(modifiableTestableWorld, blockPos2, LEAVES);
					}
				}
			}
		}
	}

	private float method_12807(int i, int j) {
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

	private float method_12804(int i) {
		if (i < 0 || i >= 5) {
			return -1.0F;
		} else {
			return i != 0 && i != 4 ? 3.0F : 2.0F;
		}
	}

	private void method_12810(ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos) {
		for (int i = 0; i < 5; i++) {
			this.method_12811(modifiableTestableWorld, blockPos.up(i), this.method_12804(i));
		}
	}

	private int method_12808(Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, BlockPos blockPos2, boolean bl) {
		if (!bl && Objects.equals(blockPos, blockPos2)) {
			return -1;
		} else {
			BlockPos blockPos3 = blockPos2.add(-blockPos.getX(), -blockPos.getY(), -blockPos.getZ());
			int i = this.method_12805(blockPos3);
			float f = (float)blockPos3.getX() / (float)i;
			float g = (float)blockPos3.getY() / (float)i;
			float h = (float)blockPos3.getZ() / (float)i;

			for (int j = 0; j <= i; j++) {
				BlockPos blockPos4 = blockPos.add((double)(0.5F + (float)j * f), (double)(0.5F + (float)j * g), (double)(0.5F + (float)j * h));
				if (bl) {
					this.setBlockState(set, modifiableTestableWorld, blockPos4, LOG.with(LogBlock.AXIS, this.method_12809(blockPos, blockPos4)));
				} else if (!canTreeReplace(modifiableTestableWorld, blockPos4)) {
					return j;
				}
			}

			return -1;
		}
	}

	private int method_12805(BlockPos blockPos) {
		int i = MathHelper.abs(blockPos.getX());
		int j = MathHelper.abs(blockPos.getY());
		int k = MathHelper.abs(blockPos.getZ());
		if (k > i && k > j) {
			return k;
		} else {
			return j > i ? j : i;
		}
	}

	private Direction.Axis method_12809(BlockPos blockPos, BlockPos blockPos2) {
		Direction.Axis axis = Direction.Axis.Y;
		int i = Math.abs(blockPos2.getX() - blockPos.getX());
		int j = Math.abs(blockPos2.getZ() - blockPos.getZ());
		int k = Math.max(i, j);
		if (k > 0) {
			if (i == k) {
				axis = Direction.Axis.X;
			} else if (j == k) {
				axis = Direction.Axis.Z;
			}
		}

		return axis;
	}

	private void method_12802(ModifiableTestableWorld modifiableTestableWorld, int i, BlockPos blockPos, List<LargeOakTreeFeature.class_2949> list) {
		for (LargeOakTreeFeature.class_2949 lv : list) {
			if (this.method_12801(i, lv.method_12812() - blockPos.getY())) {
				this.method_12810(modifiableTestableWorld, lv);
			}
		}
	}

	private boolean method_12801(int i, int j) {
		return (double)j >= (double)i * 0.2;
	}

	private void method_12806(Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, int i) {
		this.method_12808(set, modifiableTestableWorld, blockPos, blockPos.up(i), true);
	}

	private void method_12800(
		Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, int i, BlockPos blockPos, List<LargeOakTreeFeature.class_2949> list
	) {
		for (LargeOakTreeFeature.class_2949 lv : list) {
			int j = lv.method_12812();
			BlockPos blockPos2 = new BlockPos(blockPos.getX(), j, blockPos.getZ());
			if (!blockPos2.equals(lv) && this.method_12801(i, j - blockPos.getY())) {
				this.method_12808(set, modifiableTestableWorld, blockPos2, lv, true);
			}
		}
	}

	@Override
	public boolean generate(Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos) {
		Random random2 = new Random(random.nextLong());
		int i = this.method_12803(set, modifiableTestableWorld, blockPos, 5 + random2.nextInt(12));
		if (i == -1) {
			return false;
		} else {
			this.setToDirt(modifiableTestableWorld, blockPos.down());
			int j = (int)((double)i * 0.618);
			if (j >= i) {
				j = i - 1;
			}

			double d = 1.0;
			int k = (int)(1.382 + Math.pow(1.0 * (double)i / 13.0, 2.0));
			if (k < 1) {
				k = 1;
			}

			int l = blockPos.getY() + j;
			int m = i - 5;
			List<LargeOakTreeFeature.class_2949> list = Lists.<LargeOakTreeFeature.class_2949>newArrayList();
			list.add(new LargeOakTreeFeature.class_2949(blockPos.up(m), l));

			for (; m >= 0; m--) {
				float f = this.method_12807(i, m);
				if (!(f < 0.0F)) {
					for (int n = 0; n < k; n++) {
						double e = 1.0;
						double g = 1.0 * (double)f * ((double)random2.nextFloat() + 0.328);
						double h = (double)(random2.nextFloat() * 2.0F) * Math.PI;
						double o = g * Math.sin(h) + 0.5;
						double p = g * Math.cos(h) + 0.5;
						BlockPos blockPos2 = blockPos.add(o, (double)(m - 1), p);
						BlockPos blockPos3 = blockPos2.up(5);
						if (this.method_12808(set, modifiableTestableWorld, blockPos2, blockPos3, false) == -1) {
							int q = blockPos.getX() - blockPos2.getX();
							int r = blockPos.getZ() - blockPos2.getZ();
							double s = (double)blockPos2.getY() - Math.sqrt((double)(q * q + r * r)) * 0.381;
							int t = s > (double)l ? l : (int)s;
							BlockPos blockPos4 = new BlockPos(blockPos.getX(), t, blockPos.getZ());
							if (this.method_12808(set, modifiableTestableWorld, blockPos4, blockPos2, false) == -1) {
								list.add(new LargeOakTreeFeature.class_2949(blockPos2, blockPos4.getY()));
							}
						}
					}
				}
			}

			this.method_12802(modifiableTestableWorld, i, blockPos, list);
			this.method_12806(set, modifiableTestableWorld, blockPos, j);
			this.method_12800(set, modifiableTestableWorld, i, blockPos, list);
			return true;
		}
	}

	private int method_12803(Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, int i) {
		if (!isDirtOrGrass(modifiableTestableWorld, blockPos.down())) {
			return -1;
		} else {
			int j = this.method_12808(set, modifiableTestableWorld, blockPos, blockPos.up(i - 1), false);
			if (j == -1) {
				return i;
			} else {
				return j < 6 ? -1 : j;
			}
		}
	}

	static class class_2949 extends BlockPos {
		private final int field_13344;

		public class_2949(BlockPos blockPos, int i) {
			super(blockPos.getX(), blockPos.getY(), blockPos.getZ());
			this.field_13344 = i;
		}

		public int method_12812() {
			return this.field_13344;
		}
	}
}
