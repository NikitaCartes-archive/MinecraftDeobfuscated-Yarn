package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.TestableWorld;

public abstract class MegaTreeFeature<T extends TreeFeatureConfig> extends AbstractTreeFeature<T> {
	public MegaTreeFeature(Function<Dynamic<?>, ? extends T> function) {
		super(function);
	}

	protected int getHeight(Random random, MegaTreeFeatureConfig megaTreeFeatureConfig) {
		int i = random.nextInt(3) + megaTreeFeatureConfig.baseHeight;
		if (megaTreeFeatureConfig.field_21233 > 1) {
			i += random.nextInt(megaTreeFeatureConfig.field_21233);
		}

		return i;
	}

	private boolean doesTreeFit(TestableWorld world, BlockPos pos, int height) {
		boolean bl = true;
		if (pos.getY() >= 1 && pos.getY() + height + 1 <= 256) {
			for (int i = 0; i <= 1 + height; i++) {
				int j = 2;
				if (i == 0) {
					j = 1;
				} else if (i >= 1 + height - 2) {
					j = 2;
				}

				for (int k = -j; k <= j && bl; k++) {
					for (int l = -j; l <= j && bl; l++) {
						if (pos.getY() + i < 0 || pos.getY() + i >= 256 || !canTreeReplace(world, pos.add(k, i, l))) {
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

	private boolean replaceGround(ModifiableTestableWorld world, BlockPos pos) {
		BlockPos blockPos = pos.method_10074();
		if (isNaturalDirtOrGrass(world, blockPos) && pos.getY() >= 2) {
			this.setToDirt(world, blockPos);
			this.setToDirt(world, blockPos.east());
			this.setToDirt(world, blockPos.south());
			this.setToDirt(world, blockPos.south().east());
			return true;
		} else {
			return false;
		}
	}

	protected boolean checkTreeFitsAndReplaceGround(ModifiableTestableWorld world, BlockPos pos, int height) {
		return this.doesTreeFit(world, pos, height) && this.replaceGround(world, pos);
	}

	protected void makeSquaredLeafLayer(
		ModifiableTestableWorld modifiableTestableWorld,
		Random random,
		BlockPos blockPos,
		int i,
		Set<BlockPos> set,
		BlockBox blockBox,
		TreeFeatureConfig treeFeatureConfig
	) {
		int j = i * i;

		for (int k = -i; k <= i + 1; k++) {
			for (int l = -i; l <= i + 1; l++) {
				int m = Math.min(Math.abs(k), Math.abs(k - 1));
				int n = Math.min(Math.abs(l), Math.abs(l - 1));
				if (m + n < 7 && m * m + n * n <= j) {
					this.method_23383(modifiableTestableWorld, random, blockPos.add(k, 0, l), set, blockBox, treeFeatureConfig);
				}
			}
		}
	}

	protected void makeRoundLeafLayer(
		ModifiableTestableWorld modifiableTestableWorld,
		Random random,
		BlockPos blockPos,
		int i,
		Set<BlockPos> set,
		BlockBox blockBox,
		TreeFeatureConfig treeFeatureConfig
	) {
		int j = i * i;

		for (int k = -i; k <= i; k++) {
			for (int l = -i; l <= i; l++) {
				if (k * k + l * l <= j) {
					this.method_23383(modifiableTestableWorld, random, blockPos.add(k, 0, l), set, blockBox, treeFeatureConfig);
				}
			}
		}
	}

	protected void method_23400(
		ModifiableTestableWorld modifiableTestableWorld,
		Random random,
		BlockPos blockPos,
		int i,
		Set<BlockPos> set,
		BlockBox blockBox,
		MegaTreeFeatureConfig megaTreeFeatureConfig
	) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int j = 0; j < i; j++) {
			mutable.set(blockPos).setOffset(0, j, 0);
			if (canTreeReplace(modifiableTestableWorld, mutable)) {
				this.method_23382(modifiableTestableWorld, random, mutable, set, blockBox, megaTreeFeatureConfig);
			}

			if (j < i - 1) {
				mutable.set(blockPos).setOffset(1, j, 0);
				if (canTreeReplace(modifiableTestableWorld, mutable)) {
					this.method_23382(modifiableTestableWorld, random, mutable, set, blockBox, megaTreeFeatureConfig);
				}

				mutable.set(blockPos).setOffset(1, j, 1);
				if (canTreeReplace(modifiableTestableWorld, mutable)) {
					this.method_23382(modifiableTestableWorld, random, mutable, set, blockBox, megaTreeFeatureConfig);
				}

				mutable.set(blockPos).setOffset(0, j, 1);
				if (canTreeReplace(modifiableTestableWorld, mutable)) {
					this.method_23382(modifiableTestableWorld, random, mutable, set, blockBox, megaTreeFeatureConfig);
				}
			}
		}
	}
}
