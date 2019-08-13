package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.ModifiableTestableWorld;

public class MegaJungleTreeFeature extends MegaTreeFeature<DefaultFeatureConfig> {
	public MegaJungleTreeFeature(
		Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl, int i, int j, BlockState blockState, BlockState blockState2
	) {
		super(function, bl, i, j, blockState, blockState2);
	}

	@Override
	public boolean generate(
		Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, MutableIntBoundingBox mutableIntBoundingBox
	) {
		int i = this.getHeight(random);
		if (!this.checkTreeFitsAndReplaceGround(modifiableTestableWorld, blockPos, i)) {
			return false;
		} else {
			this.makeLeaves(modifiableTestableWorld, blockPos.up(i), 2, mutableIntBoundingBox, set);

			for (int j = blockPos.getY() + i - 2 - random.nextInt(4); j > blockPos.getY() + i / 2; j -= 2 + random.nextInt(4)) {
				float f = random.nextFloat() * (float) (Math.PI * 2);
				int k = blockPos.getX() + (int)(0.5F + MathHelper.cos(f) * 4.0F);
				int l = blockPos.getZ() + (int)(0.5F + MathHelper.sin(f) * 4.0F);

				for (int m = 0; m < 5; m++) {
					k = blockPos.getX() + (int)(1.5F + MathHelper.cos(f) * (float)m);
					l = blockPos.getZ() + (int)(1.5F + MathHelper.sin(f) * (float)m);
					this.setBlockState(set, modifiableTestableWorld, new BlockPos(k, j - 3 + m / 2, l), this.log, mutableIntBoundingBox);
				}

				int m = 1 + random.nextInt(2);
				int n = j;

				for (int o = j - m; o <= n; o++) {
					int p = o - n;
					this.makeRoundLeafLayer(modifiableTestableWorld, new BlockPos(k, o, l), 1 - p, mutableIntBoundingBox, set);
				}
			}

			for (int q = 0; q < i; q++) {
				BlockPos blockPos2 = blockPos.up(q);
				if (canTreeReplace(modifiableTestableWorld, blockPos2)) {
					this.setBlockState(set, modifiableTestableWorld, blockPos2, this.log, mutableIntBoundingBox);
					if (q > 0) {
						this.tryMakingVine(modifiableTestableWorld, random, blockPos2.west(), VineBlock.EAST);
						this.tryMakingVine(modifiableTestableWorld, random, blockPos2.north(), VineBlock.SOUTH);
					}
				}

				if (q < i - 1) {
					BlockPos blockPos3 = blockPos2.east();
					if (canTreeReplace(modifiableTestableWorld, blockPos3)) {
						this.setBlockState(set, modifiableTestableWorld, blockPos3, this.log, mutableIntBoundingBox);
						if (q > 0) {
							this.tryMakingVine(modifiableTestableWorld, random, blockPos3.east(), VineBlock.WEST);
							this.tryMakingVine(modifiableTestableWorld, random, blockPos3.north(), VineBlock.SOUTH);
						}
					}

					BlockPos blockPos4 = blockPos2.south().east();
					if (canTreeReplace(modifiableTestableWorld, blockPos4)) {
						this.setBlockState(set, modifiableTestableWorld, blockPos4, this.log, mutableIntBoundingBox);
						if (q > 0) {
							this.tryMakingVine(modifiableTestableWorld, random, blockPos4.east(), VineBlock.WEST);
							this.tryMakingVine(modifiableTestableWorld, random, blockPos4.south(), VineBlock.NORTH);
						}
					}

					BlockPos blockPos5 = blockPos2.south();
					if (canTreeReplace(modifiableTestableWorld, blockPos5)) {
						this.setBlockState(set, modifiableTestableWorld, blockPos5, this.log, mutableIntBoundingBox);
						if (q > 0) {
							this.tryMakingVine(modifiableTestableWorld, random, blockPos5.west(), VineBlock.EAST);
							this.tryMakingVine(modifiableTestableWorld, random, blockPos5.south(), VineBlock.NORTH);
						}
					}
				}
			}

			return true;
		}
	}

	private void tryMakingVine(ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, BooleanProperty booleanProperty) {
		if (random.nextInt(3) > 0 && isAir(modifiableTestableWorld, blockPos)) {
			this.setBlockState(modifiableTestableWorld, blockPos, Blocks.field_10597.getDefaultState().with(booleanProperty, Boolean.valueOf(true)));
		}
	}

	private void makeLeaves(
		ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, int i, MutableIntBoundingBox mutableIntBoundingBox, Set<BlockPos> set
	) {
		int j = 2;

		for (int k = -2; k <= 0; k++) {
			this.makeSquaredLeafLayer(modifiableTestableWorld, blockPos.up(k), i + 1 - k, mutableIntBoundingBox, set);
		}
	}
}
