package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.class_3746;
import net.minecraft.class_3747;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.config.feature.FeatureConfig;

public abstract class MegaTreeFeature<T extends FeatureConfig> extends TreeFeature<T> {
	protected final int field_13683;
	protected final BlockState field_13685;
	protected final BlockState field_13684;
	protected int field_13686;

	public MegaTreeFeature(Function<Dynamic<?>, ? extends T> function, boolean bl, int i, int j, BlockState blockState, BlockState blockState2) {
		super(function, bl);
		this.field_13683 = i;
		this.field_13686 = j;
		this.field_13685 = blockState;
		this.field_13684 = blockState2;
	}

	protected int method_13524(Random random) {
		int i = random.nextInt(3) + this.field_13683;
		if (this.field_13686 > 1) {
			i += random.nextInt(this.field_13686);
		}

		return i;
	}

	private boolean method_13527(class_3746 arg, BlockPos blockPos, int i) {
		boolean bl = true;
		if (blockPos.getY() >= 1 && blockPos.getY() + i + 1 <= 256) {
			for (int j = 0; j <= 1 + i; j++) {
				int k = 2;
				if (j == 0) {
					k = 1;
				} else if (j >= 1 + i - 2) {
					k = 2;
				}

				for (int l = -k; l <= k && bl; l++) {
					for (int m = -k; m <= k && bl; m++) {
						if (blockPos.getY() + j < 0 || blockPos.getY() + j >= 256 || !method_16432(arg, blockPos.add(l, j, m))) {
							bl = false;
						}
					}
				}
			}

			return bl;
		} else {
			return false;
		}
	}

	private boolean method_13525(class_3747 arg, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		if (method_16430(arg, blockPos2) && blockPos.getY() >= 2) {
			this.method_16427(arg, blockPos2);
			this.method_16427(arg, blockPos2.east());
			this.method_16427(arg, blockPos2.south());
			this.method_16427(arg, blockPos2.south().east());
			return true;
		} else {
			return false;
		}
	}

	protected boolean method_13523(class_3747 arg, BlockPos blockPos, int i) {
		return this.method_13527(arg, blockPos, i) && this.method_13525(arg, blockPos);
	}

	protected void method_13528(class_3747 arg, BlockPos blockPos, int i) {
		int j = i * i;

		for (int k = -i; k <= i + 1; k++) {
			for (int l = -i; l <= i + 1; l++) {
				int m = Math.min(Math.abs(k), Math.abs(k - 1));
				int n = Math.min(Math.abs(l), Math.abs(l - 1));
				if (m + n < 7 && m * m + n * n <= j) {
					BlockPos blockPos2 = blockPos.add(k, 0, l);
					if (method_16420(arg, blockPos2)) {
						this.method_13153(arg, blockPos2, this.field_13684);
					}
				}
			}
		}
	}

	protected void method_13526(class_3747 arg, BlockPos blockPos, int i) {
		int j = i * i;

		for (int k = -i; k <= i; k++) {
			for (int l = -i; l <= i; l++) {
				if (k * k + l * l <= j) {
					BlockPos blockPos2 = blockPos.add(k, 0, l);
					if (method_16420(arg, blockPos2)) {
						this.method_13153(arg, blockPos2, this.field_13684);
					}
				}
			}
		}
	}
}
