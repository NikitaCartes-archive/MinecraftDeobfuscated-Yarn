package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class HugeFungiFeature extends Feature<HugeFungiFeatureConfig> {
	public HugeFungiFeature(Function<Dynamic<?>, ? extends HugeFungiFeatureConfig> function) {
		super(function);
	}

	public boolean generate(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, HugeFungiFeatureConfig hugeFungiFeatureConfig
	) {
		BlockPos.Mutable mutable = getStartPos(iWorld, blockPos);
		if (mutable == null) {
			return false;
		} else {
			int i = MathHelper.nextInt(random, 4, 13);
			if (random.nextInt(12) == 0) {
				i *= 2;
			}

			if (mutable.getY() + i + 1 >= 256) {
				return false;
			} else {
				boolean bl = !hugeFungiFeatureConfig.planted && random.nextFloat() < 0.06F;
				this.generateHat(iWorld, random, hugeFungiFeatureConfig, mutable, i, bl);
				this.generateStem(iWorld, random, hugeFungiFeatureConfig, mutable, i, bl);
				return true;
			}
		}
	}

	private void generateStem(IWorld world, Random random, HugeFungiFeatureConfig config, BlockPos.Mutable pos, int stemHeight, boolean thickStem) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		BlockState blockState = config.stemState;
		int i = thickStem ? 1 : 0;

		for (int j = -i; j <= i; j++) {
			for (int k = -i; k <= i; k++) {
				boolean bl = thickStem && MathHelper.abs(j) == i && MathHelper.abs(k) == i;

				for (int l = 0; l < stemHeight; l++) {
					mutable.set(pos).setOffset(j, l, k);
					if (!world.getBlockState(mutable).isFullOpaque(world, mutable)) {
						if (config.planted) {
							world.setBlockState(mutable, blockState, 3);
						} else if (bl) {
							if (random.nextFloat() < 0.1F) {
								this.setBlockState(world, mutable, blockState);
							}
						} else {
							this.setBlockState(world, mutable, blockState);
						}
					}
				}
			}
		}
	}

	private void generateHat(IWorld world, Random random, HugeFungiFeatureConfig config, BlockPos.Mutable pos, int hatHeight, boolean thickStem) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		boolean bl = config.hatState.getBlock() == Blocks.NETHER_WART_BLOCK;
		int i = Math.min(random.nextInt(1 + hatHeight / 3) + 5, hatHeight);
		int j = hatHeight - i;

		for (int k = j; k <= hatHeight; k++) {
			int l = k < hatHeight - random.nextInt(3) ? 2 : 1;
			if (i > 8 && k < j + 4) {
				l = 3;
			}

			if (thickStem) {
				l++;
			}

			for (int m = -l; m <= l; m++) {
				for (int n = -l; n <= l; n++) {
					boolean bl2 = m == -l || m == l;
					boolean bl3 = n == -l || n == l;
					boolean bl4 = !bl2 && !bl3 && k != hatHeight;
					boolean bl5 = bl2 && bl3;
					boolean bl6 = k < j + 3;
					mutable.set(pos).setOffset(m, k, n);
					if (!world.getBlockState(mutable).isFullOpaque(world, mutable)) {
						if (bl6) {
							if (!bl4) {
								this.tryGenerateVines(world, random, mutable, config.hatState, bl);
							}
						} else if (bl4) {
							this.generateHatBlock(world, random, config, mutable, 0.1F, 0.2F, bl ? 0.1F : 0.0F);
						} else if (bl5) {
							this.generateHatBlock(world, random, config, mutable, 0.01F, 0.7F, bl ? 0.083F : 0.0F);
						} else {
							this.generateHatBlock(world, random, config, mutable, 5.0E-4F, 0.98F, bl ? 0.07F : 0.0F);
						}
					}
				}
			}
		}
	}

	private void generateHatBlock(
		IWorld world, Random random, HugeFungiFeatureConfig config, BlockPos.Mutable pos, float decorationChance, float generationChance, float vineChance
	) {
		if (random.nextFloat() < decorationChance) {
			this.setBlockState(world, pos, config.decorationState);
		} else if (random.nextFloat() < generationChance) {
			this.setBlockState(world, pos, config.hatState);
			if (random.nextFloat() < vineChance) {
				this.generateVines(pos, world, random);
			}
		}
	}

	private void tryGenerateVines(IWorld world, Random random, BlockPos pos, BlockState state, boolean bl) {
		if (world.getBlockState(pos.down()).getBlock() == state.getBlock()) {
			this.setBlockState(world, pos, state);
		} else if ((double)random.nextFloat() < 0.15) {
			this.setBlockState(world, pos, state);
			if (bl && random.nextInt(11) == 0) {
				this.generateVines(pos, world, random);
			}
		}
	}

	@Nullable
	private static BlockPos.Mutable getStartPos(IWorld world, BlockPos pos) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(pos);

		for (int i = pos.getY(); i >= 1; i--) {
			mutable.setY(i);
			Block block = world.getBlockState(mutable.down()).getBlock();
			if (block.matches(BlockTags.NYLIUM)) {
				return mutable;
			}
		}

		return null;
	}

	private void generateVines(BlockPos pos, IWorld world, Random random) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(pos).setOffset(Direction.DOWN);
		if (world.isAir(mutable)) {
			int i = MathHelper.nextInt(random, 1, 5);
			if (random.nextInt(7) == 0) {
				i *= 2;
			}

			int j = 23;
			int k = 25;
			WeepingVinesFeature.generateVines(world, random, mutable, i, 23, 25);
		}
	}
}
