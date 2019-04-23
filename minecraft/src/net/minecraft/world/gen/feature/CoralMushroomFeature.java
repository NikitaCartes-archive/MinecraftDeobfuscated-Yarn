package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class CoralMushroomFeature extends CoralFeature {
	public CoralMushroomFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	protected boolean spawnCoral(IWorld iWorld, Random random, BlockPos blockPos, BlockState blockState) {
		int i = random.nextInt(3) + 3;
		int j = random.nextInt(3) + 3;
		int k = random.nextInt(3) + 3;
		int l = random.nextInt(3) + 1;
		BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);

		for (int m = 0; m <= j; m++) {
			for (int n = 0; n <= i; n++) {
				for (int o = 0; o <= k; o++) {
					mutable.set(m + blockPos.getX(), n + blockPos.getY(), o + blockPos.getZ());
					mutable.setOffset(Direction.field_11033, l);
					if ((m != 0 && m != j || n != 0 && n != i)
						&& (o != 0 && o != k || n != 0 && n != i)
						&& (m != 0 && m != j || o != 0 && o != k)
						&& (m == 0 || m == j || n == 0 || n == i || o == 0 || o == k)
						&& !(random.nextFloat() < 0.1F)
						&& !this.spawnCoralPiece(iWorld, random, mutable, blockState)) {
					}
				}
			}
		}

		return true;
	}
}
