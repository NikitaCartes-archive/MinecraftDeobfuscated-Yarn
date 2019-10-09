package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;

public class class_4649 extends class_4647 {
	public class_4649(int i, int j) {
		super(i, j, class_4648.PINE_FOLIAGE_PLACER);
	}

	public <T> class_4649(Dynamic<T> dynamic) {
		this(dynamic.get("radius").asInt(0), dynamic.get("radius_random").asInt(0));
	}

	@Override
	public void method_23448(
		ModifiableTestableWorld modifiableTestableWorld, Random random, class_4640 arg, int i, int j, int k, BlockPos blockPos, Set<BlockPos> set
	) {
		int l = 0;

		for (int m = i; m >= j; m--) {
			this.method_23449(modifiableTestableWorld, random, arg, i, blockPos, m, l, set);
			if (l >= 1 && m == j + 1) {
				l--;
			} else if (l < k) {
				l++;
			}
		}
	}

	@Override
	public int method_23452(Random random, int i, int j, class_4640 arg) {
		return this.field_21296 + random.nextInt(this.field_21297 + 1) + random.nextInt(j - i + 1);
	}

	@Override
	protected boolean method_23451(Random random, int i, int j, int k, int l, int m) {
		return Math.abs(j) == m && Math.abs(l) == m && m > 0;
	}

	@Override
	public int method_23447(int i, int j, int k, int l) {
		return l < i ? 0 : k;
	}
}
