package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;

public class class_4646 extends class_4647 {
	public class_4646(int i, int j) {
		super(i, j, class_4648.BLOB_FOLIAGE_PLACER);
	}

	public <T> class_4646(Dynamic<T> dynamic) {
		this(dynamic.get("radius").asInt(0), dynamic.get("radius_random").asInt(0));
	}

	@Override
	public void method_23448(
		ModifiableTestableWorld modifiableTestableWorld, Random random, class_4640 arg, int i, int j, int k, BlockPos blockPos, Set<BlockPos> set
	) {
		for (int l = i; l >= j; l--) {
			int m = this.method_23447(j, i, k, l);
			this.method_23449(modifiableTestableWorld, random, arg, i, blockPos, l, m, set);
		}
	}

	@Override
	public int method_23452(Random random, int i, int j, class_4640 arg) {
		return this.field_21296 + random.nextInt(this.field_21297 + 1);
	}

	@Override
	protected boolean method_23451(Random random, int i, int j, int k, int l, int m) {
		return Math.abs(j) == m && Math.abs(l) == m && (random.nextInt(2) == 0 || k == i);
	}

	@Override
	public int method_23447(int i, int j, int k, int l) {
		return Math.max(k - 1 - (l - j) / 2, 0);
	}
}
