package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.TestableWorld;

public abstract class MegaTreeFeature<T extends FeatureConfig> extends AbstractTreeFeature<T> {
	protected final int baseHeight;
	protected final BlockState log;
	protected final BlockState leaves;
	protected final int maxExtraHeight;

	public MegaTreeFeature(Function<Dynamic<?>, ? extends T> function, boolean bl, int i, int j, BlockState blockState, BlockState blockState2) {
		super(function, bl);
		this.baseHeight = i;
		this.maxExtraHeight = j;
		this.log = blockState;
		this.leaves = blockState2;
	}

	protected int getHeight(Random random) {
		int i = random.nextInt(3) + this.baseHeight;
		if (this.maxExtraHeight > 1) {
			i += random.nextInt(this.maxExtraHeight);
		}

		return i;
	}

	private boolean doesTreeFit(TestableWorld testableWorld, BlockPos blockPos, int i) {
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
						if (blockPos.getY() + j < 0 || blockPos.getY() + j >= 256 || !canTreeReplace(testableWorld, blockPos.add(l, j, m))) {
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

	private boolean replaceGround(ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		if (isNaturalDirtOrGrass(modifiableTestableWorld, blockPos2) && blockPos.getY() >= 2) {
			this.setToDirt(modifiableTestableWorld, blockPos2);
			this.setToDirt(modifiableTestableWorld, blockPos2.east());
			this.setToDirt(modifiableTestableWorld, blockPos2.south());
			this.setToDirt(modifiableTestableWorld, blockPos2.south().east());
			return true;
		} else {
			return false;
		}
	}

	protected boolean checkTreeFitsAndReplaceGround(ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, int i) {
		return this.doesTreeFit(modifiableTestableWorld, blockPos, i) && this.replaceGround(modifiableTestableWorld, blockPos);
	}

	protected void makeSquaredLeafLayer(
		ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, int i, MutableIntBoundingBox mutableIntBoundingBox, Set<BlockPos> set
	) {
		int j = i * i;

		for (int k = -i; k <= i + 1; k++) {
			for (int l = -i; l <= i + 1; l++) {
				int m = Math.min(Math.abs(k), Math.abs(k - 1));
				int n = Math.min(Math.abs(l), Math.abs(l - 1));
				if (m + n < 7 && m * m + n * n <= j) {
					BlockPos blockPos2 = blockPos.add(k, 0, l);
					if (isAirOrLeaves(modifiableTestableWorld, blockPos2)) {
						this.setBlockState(set, modifiableTestableWorld, blockPos2, this.leaves, mutableIntBoundingBox);
					}
				}
			}
		}
	}

	protected void makeRoundLeafLayer(
		ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, int i, MutableIntBoundingBox mutableIntBoundingBox, Set<BlockPos> set
	) {
		int j = i * i;

		for (int k = -i; k <= i; k++) {
			for (int l = -i; l <= i; l++) {
				if (k * k + l * l <= j) {
					BlockPos blockPos2 = blockPos.add(k, 0, l);
					if (isAirOrLeaves(modifiableTestableWorld, blockPos2)) {
						this.setBlockState(set, modifiableTestableWorld, blockPos2, this.leaves, mutableIntBoundingBox);
					}
				}
			}
		}
	}
}
