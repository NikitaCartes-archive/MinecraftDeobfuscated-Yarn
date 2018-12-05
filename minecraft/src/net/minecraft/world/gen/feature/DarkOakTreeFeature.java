package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.class_3746;
import net.minecraft.class_3747;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.config.feature.DefaultFeatureConfig;

public class DarkOakTreeFeature extends TreeFeature<DefaultFeatureConfig> {
	private static final BlockState LOG = Blocks.field_10010.getDefaultState();
	private static final BlockState LEAVES = Blocks.field_10035.getDefaultState();

	public DarkOakTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl) {
		super(function, bl);
	}

	@Override
	public boolean method_12775(Set<BlockPos> set, class_3747 arg, Random random, BlockPos blockPos) {
		int i = random.nextInt(3) + random.nextInt(2) + 6;
		int j = blockPos.getX();
		int k = blockPos.getY();
		int l = blockPos.getZ();
		if (k >= 1 && k + i + 1 < 256) {
			BlockPos blockPos2 = blockPos.down();
			if (!method_16430(arg, blockPos2)) {
				return false;
			} else if (!this.method_13875(arg, blockPos, i)) {
				return false;
			} else {
				this.method_16427(arg, blockPos2);
				this.method_16427(arg, blockPos2.east());
				this.method_16427(arg, blockPos2.south());
				this.method_16427(arg, blockPos2.south().east());
				Direction direction = Direction.class_2353.HORIZONTAL.random(random);
				int m = i - random.nextInt(4);
				int n = 2 - random.nextInt(3);
				int o = j;
				int p = l;
				int q = k + i - 1;

				for (int r = 0; r < i; r++) {
					if (r >= m && n > 0) {
						o += direction.getOffsetX();
						p += direction.getOffsetZ();
						n--;
					}

					int s = k + r;
					BlockPos blockPos3 = new BlockPos(o, s, p);
					if (method_16420(arg, blockPos3)) {
						this.method_13874(set, arg, blockPos3);
						this.method_13874(set, arg, blockPos3.east());
						this.method_13874(set, arg, blockPos3.south());
						this.method_13874(set, arg, blockPos3.east().south());
					}
				}

				for (int r = -2; r <= 0; r++) {
					for (int s = -2; s <= 0; s++) {
						int t = -1;
						this.method_13873(arg, o + r, q + t, p + s);
						this.method_13873(arg, 1 + o - r, q + t, p + s);
						this.method_13873(arg, o + r, q + t, 1 + p - s);
						this.method_13873(arg, 1 + o - r, q + t, 1 + p - s);
						if ((r > -2 || s > -1) && (r != -1 || s != -2)) {
							int var28 = 1;
							this.method_13873(arg, o + r, q + var28, p + s);
							this.method_13873(arg, 1 + o - r, q + var28, p + s);
							this.method_13873(arg, o + r, q + var28, 1 + p - s);
							this.method_13873(arg, 1 + o - r, q + var28, 1 + p - s);
						}
					}
				}

				if (random.nextBoolean()) {
					this.method_13873(arg, o, q + 2, p);
					this.method_13873(arg, o + 1, q + 2, p);
					this.method_13873(arg, o + 1, q + 2, p + 1);
					this.method_13873(arg, o, q + 2, p + 1);
				}

				for (int r = -3; r <= 4; r++) {
					for (int sx = -3; sx <= 4; sx++) {
						if ((r != -3 || sx != -3) && (r != -3 || sx != 4) && (r != 4 || sx != -3) && (r != 4 || sx != 4) && (Math.abs(r) < 3 || Math.abs(sx) < 3)) {
							this.method_13873(arg, o + r, q, p + sx);
						}
					}
				}

				for (int r = -1; r <= 2; r++) {
					for (int sxx = -1; sxx <= 2; sxx++) {
						if ((r < 0 || r > 1 || sxx < 0 || sxx > 1) && random.nextInt(3) <= 0) {
							int t = random.nextInt(3) + 2;

							for (int u = 0; u < t; u++) {
								this.method_13874(set, arg, new BlockPos(j + r, q - u - 1, l + sxx));
							}

							for (int u = -1; u <= 1; u++) {
								for (int v = -1; v <= 1; v++) {
									this.method_13873(arg, o + r + u, q, p + sxx + v);
								}
							}

							for (int u = -2; u <= 2; u++) {
								for (int v = -2; v <= 2; v++) {
									if (Math.abs(u) != 2 || Math.abs(v) != 2) {
										this.method_13873(arg, o + r + u, q - 1, p + sxx + v);
									}
								}
							}
						}
					}
				}

				return true;
			}
		} else {
			return false;
		}
	}

	private boolean method_13875(class_3746 arg, BlockPos blockPos, int i) {
		int j = blockPos.getX();
		int k = blockPos.getY();
		int l = blockPos.getZ();
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int m = 0; m <= i + 1; m++) {
			int n = 1;
			if (m == 0) {
				n = 0;
			}

			if (m >= i - 1) {
				n = 2;
			}

			for (int o = -n; o <= n; o++) {
				for (int p = -n; p <= n; p++) {
					if (!method_16432(arg, mutable.set(j + o, k + m, l + p))) {
						return false;
					}
				}
			}
		}

		return true;
	}

	private void method_13874(Set<BlockPos> set, class_3747 arg, BlockPos blockPos) {
		if (method_16432(arg, blockPos)) {
			this.method_12773(set, arg, blockPos, LOG);
		}
	}

	private void method_13873(class_3747 arg, int i, int j, int k) {
		BlockPos blockPos = new BlockPos(i, j, k);
		if (method_16424(arg, blockPos)) {
			this.method_13153(arg, blockPos, LEAVES);
		}
	}
}
