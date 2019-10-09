package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.class_4636;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;

public class MegaPineTreeFeature extends MegaTreeFeature<class_4636> {
	public MegaPineTreeFeature(Function<Dynamic<?>, ? extends class_4636> function) {
		super(function);
	}

	public boolean method_23399(
		ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, Set<BlockPos> set, Set<BlockPos> set2, BlockBox blockBox, class_4636 arg
	) {
		int i = this.getHeight(random, arg);
		if (!this.checkTreeFitsAndReplaceGround(modifiableTestableWorld, blockPos, i)) {
			return false;
		} else {
			this.makeTopLeaves(modifiableTestableWorld, random, blockPos.getX(), blockPos.getZ(), blockPos.getY() + i, 0, set2, blockBox, arg);
			this.method_23400(modifiableTestableWorld, random, blockPos, i, set, blockBox, arg);
			return true;
		}
	}

	private void makeTopLeaves(
		ModifiableTestableWorld modifiableTestableWorld, Random random, int i, int j, int k, int l, Set<BlockPos> set, BlockBox blockBox, class_4636 arg
	) {
		int m = random.nextInt(5) + arg.field_21291;
		int n = 0;

		for (int o = k - m; o <= k; o++) {
			int p = k - o;
			int q = l + MathHelper.floor((float)p / (float)m * 3.5F);
			this.makeSquaredLeafLayer(modifiableTestableWorld, random, new BlockPos(i, o, j), q + (p > 0 && q == n && (o & 1) == 0 ? 1 : 0), set, blockBox, arg);
			n = q;
		}
	}
}
