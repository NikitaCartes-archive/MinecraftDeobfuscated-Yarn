package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;

public class class_4645 extends class_4647 {
	public class_4645(int i, int j) {
		super(i, j, class_4648.ACACIA_FOLIAGE_PLACER);
	}

	public <T> class_4645(Dynamic<T> dynamic) {
		this(dynamic.get("radius").asInt(0), dynamic.get("radius_random").asInt(0));
	}

	@Override
	public void method_23448(
		ModifiableTestableWorld modifiableTestableWorld, Random random, class_4640 arg, int i, int j, int k, BlockPos blockPos, Set<BlockPos> set
	) {
		arg.field_21259.method_23449(modifiableTestableWorld, random, arg, i, blockPos, 0, k, set);
		arg.field_21259.method_23449(modifiableTestableWorld, random, arg, i, blockPos, 1, 1, set);
		BlockPos blockPos2 = blockPos.up();

		for (int l = 2; l <= k - 1; l++) {
			this.method_23450(modifiableTestableWorld, random, blockPos2.east(l), arg, set);
			this.method_23450(modifiableTestableWorld, random, blockPos2.west(l), arg, set);
			this.method_23450(modifiableTestableWorld, random, blockPos2.south(l), arg, set);
			this.method_23450(modifiableTestableWorld, random, blockPos2.north(l), arg, set);
		}
	}

	@Override
	public int method_23452(Random random, int i, int j, class_4640 arg) {
		return this.field_21296 + random.nextInt(this.field_21297 + 1);
	}

	@Override
	protected boolean method_23451(Random random, int i, int j, int k, int l, int m) {
		return Math.abs(j) == m && Math.abs(l) == m && m > 0;
	}

	@Override
	public int method_23447(int i, int j, int k, int l) {
		if (l == 0) {
			return 0;
		} else {
			return l >= 1 + j - 2 ? k : 1;
		}
	}
}
