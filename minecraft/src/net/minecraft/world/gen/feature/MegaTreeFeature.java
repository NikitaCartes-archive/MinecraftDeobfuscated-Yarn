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

	protected int getHeight(Random random, MegaTreeFeatureConfig config) {
		int i = random.nextInt(3) + config.baseHeight;
		if (config.heightInterval > 1) {
			i += random.nextInt(config.heightInterval);
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
		BlockPos blockPos = pos.down();
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
		ModifiableTestableWorld world, Random random, BlockPos pos, int radius, Set<BlockPos> leaves, BlockBox box, TreeFeatureConfig config
	) {
		int i = radius * radius;

		for (int j = -radius; j <= radius + 1; j++) {
			for (int k = -radius; k <= radius + 1; k++) {
				int l = Math.min(Math.abs(j), Math.abs(j - 1));
				int m = Math.min(Math.abs(k), Math.abs(k - 1));
				if (l + m < 7 && l * l + m * m <= i) {
					this.setLeavesBlockState(world, random, pos.add(j, 0, k), leaves, box, config);
				}
			}
		}
	}

	protected void makeRoundLeafLayer(
		ModifiableTestableWorld world, Random random, BlockPos pos, int radius, Set<BlockPos> leaves, BlockBox box, TreeFeatureConfig config
	) {
		int i = radius * radius;

		for (int j = -radius; j <= radius; j++) {
			for (int k = -radius; k <= radius; k++) {
				if (j * j + k * k <= i) {
					this.setLeavesBlockState(world, random, pos.add(j, 0, k), leaves, box, config);
				}
			}
		}
	}

	protected void generateTrunk(
		ModifiableTestableWorld world, Random random, BlockPos pos, int height, Set<BlockPos> logs, BlockBox box, MegaTreeFeatureConfig config
	) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = 0; i < height; i++) {
			mutable.set(pos, 0, i, 0);
			if (canTreeReplace(world, mutable)) {
				setLogBlockState(world, random, mutable, logs, box, config);
			}

			if (i < height - 1) {
				mutable.set(pos, 1, i, 0);
				if (canTreeReplace(world, mutable)) {
					setLogBlockState(world, random, mutable, logs, box, config);
				}

				mutable.set(pos, 1, i, 1);
				if (canTreeReplace(world, mutable)) {
					setLogBlockState(world, random, mutable, logs, box, config);
				}

				mutable.set(pos, 0, i, 1);
				if (canTreeReplace(world, mutable)) {
					setLogBlockState(world, random, mutable, logs, box, config);
				}
			}
		}
	}
}
