package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.class_3747;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.config.feature.DefaultFeatureConfig;

public class MegaJungleTreeFeature extends MegaTreeFeature<DefaultFeatureConfig> {
	public MegaJungleTreeFeature(
		Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl, int i, int j, BlockState blockState, BlockState blockState2
	) {
		super(function, bl, i, j, blockState, blockState2);
	}

	@Override
	public boolean method_12775(Set<BlockPos> set, class_3747 arg, Random random, BlockPos blockPos) {
		int i = this.method_13524(random);
		if (!this.method_13523(arg, blockPos, i)) {
			return false;
		} else {
			this.method_13506(arg, blockPos.up(i), 2);

			for (int j = blockPos.getY() + i - 2 - random.nextInt(4); j > blockPos.getY() + i / 2; j -= 2 + random.nextInt(4)) {
				float f = random.nextFloat() * (float) (Math.PI * 2);
				int k = blockPos.getX() + (int)(0.5F + MathHelper.cos(f) * 4.0F);
				int l = blockPos.getZ() + (int)(0.5F + MathHelper.sin(f) * 4.0F);

				for (int m = 0; m < 5; m++) {
					k = blockPos.getX() + (int)(1.5F + MathHelper.cos(f) * (float)m);
					l = blockPos.getZ() + (int)(1.5F + MathHelper.sin(f) * (float)m);
					this.method_12773(set, arg, new BlockPos(k, j - 3 + m / 2, l), this.field_13685);
				}

				int m = 1 + random.nextInt(2);
				int n = j;

				for (int o = j - m; o <= n; o++) {
					int p = o - n;
					this.method_13526(arg, new BlockPos(k, o, l), 1 - p);
				}
			}

			for (int q = 0; q < i; q++) {
				BlockPos blockPos2 = blockPos.up(q);
				if (method_16432(arg, blockPos2)) {
					this.method_12773(set, arg, blockPos2, this.field_13685);
					if (q > 0) {
						this.method_13507(arg, random, blockPos2.west(), VineBlock.field_11702);
						this.method_13507(arg, random, blockPos2.north(), VineBlock.field_11699);
					}
				}

				if (q < i - 1) {
					BlockPos blockPos3 = blockPos2.east();
					if (method_16432(arg, blockPos3)) {
						this.method_12773(set, arg, blockPos3, this.field_13685);
						if (q > 0) {
							this.method_13507(arg, random, blockPos3.east(), VineBlock.field_11696);
							this.method_13507(arg, random, blockPos3.north(), VineBlock.field_11699);
						}
					}

					BlockPos blockPos4 = blockPos2.south().east();
					if (method_16432(arg, blockPos4)) {
						this.method_12773(set, arg, blockPos4, this.field_13685);
						if (q > 0) {
							this.method_13507(arg, random, blockPos4.east(), VineBlock.field_11696);
							this.method_13507(arg, random, blockPos4.south(), VineBlock.field_11706);
						}
					}

					BlockPos blockPos5 = blockPos2.south();
					if (method_16432(arg, blockPos5)) {
						this.method_12773(set, arg, blockPos5, this.field_13685);
						if (q > 0) {
							this.method_13507(arg, random, blockPos5.west(), VineBlock.field_11702);
							this.method_13507(arg, random, blockPos5.south(), VineBlock.field_11706);
						}
					}
				}
			}

			return true;
		}
	}

	private void method_13507(class_3747 arg, Random random, BlockPos blockPos, BooleanProperty booleanProperty) {
		if (random.nextInt(3) > 0 && method_16424(arg, blockPos)) {
			this.method_13153(arg, blockPos, Blocks.field_10597.getDefaultState().with(booleanProperty, Boolean.valueOf(true)));
		}
	}

	private void method_13506(class_3747 arg, BlockPos blockPos, int i) {
		int j = 2;

		for (int k = -2; k <= 0; k++) {
			this.method_13528(arg, blockPos.up(k), i + 1 - k);
		}
	}
}
