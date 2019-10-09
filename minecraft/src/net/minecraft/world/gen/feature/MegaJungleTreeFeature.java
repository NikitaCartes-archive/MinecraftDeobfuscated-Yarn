package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.class_4636;
import net.minecraft.class_4643;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;

public class MegaJungleTreeFeature extends MegaTreeFeature<class_4636> {
	public MegaJungleTreeFeature(Function<Dynamic<?>, ? extends class_4636> function) {
		super(function);
	}

	public boolean method_23398(
		ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, Set<BlockPos> set, Set<BlockPos> set2, BlockBox blockBox, class_4636 arg
	) {
		int i = this.getHeight(random, arg);
		if (!this.checkTreeFitsAndReplaceGround(modifiableTestableWorld, blockPos, i)) {
			return false;
		} else {
			this.makeLeaves(modifiableTestableWorld, random, blockPos.up(i), 2, set2, blockBox, arg);

			for (int j = blockPos.getY() + i - 2 - random.nextInt(4); j > blockPos.getY() + i / 2; j -= 2 + random.nextInt(4)) {
				float f = random.nextFloat() * (float) (Math.PI * 2);
				int k = blockPos.getX() + (int)(0.5F + MathHelper.cos(f) * 4.0F);
				int l = blockPos.getZ() + (int)(0.5F + MathHelper.sin(f) * 4.0F);

				for (int m = 0; m < 5; m++) {
					k = blockPos.getX() + (int)(1.5F + MathHelper.cos(f) * (float)m);
					l = blockPos.getZ() + (int)(1.5F + MathHelper.sin(f) * (float)m);
					BlockPos blockPos2 = new BlockPos(k, j - 3 + m / 2, l);
					this.method_23382(modifiableTestableWorld, random, blockPos2, set, blockBox, arg);
				}

				int m = 1 + random.nextInt(2);
				int n = j;

				for (int o = j - m; o <= n; o++) {
					int p = o - n;
					this.makeRoundLeafLayer(modifiableTestableWorld, random, new BlockPos(k, o, l), 1 - p, set2, blockBox, arg);
				}
			}

			this.method_23400(modifiableTestableWorld, random, blockPos, i, set, blockBox, arg);
			return true;
		}
	}

	private void makeLeaves(
		ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, int i, Set<BlockPos> set, BlockBox blockBox, class_4643 arg
	) {
		int j = 2;

		for (int k = -2; k <= 0; k++) {
			this.makeSquaredLeafLayer(modifiableTestableWorld, random, blockPos.up(k), i + 1 - k, set, blockBox, arg);
		}
	}
}
