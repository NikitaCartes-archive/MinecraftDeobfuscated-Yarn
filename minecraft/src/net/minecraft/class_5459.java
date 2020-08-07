package net.minecraft;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntStack;
import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class class_5459 {
	public static class_5459.class_5460 method_30574(BlockPos blockPos, Direction.Axis axis, int i, Direction.Axis axis2, int j, Predicate<BlockPos> predicate) {
		BlockPos.Mutable mutable = blockPos.mutableCopy();
		Direction direction = Direction.get(Direction.AxisDirection.field_11060, axis);
		Direction direction2 = direction.getOpposite();
		Direction direction3 = Direction.get(Direction.AxisDirection.field_11060, axis2);
		Direction direction4 = direction3.getOpposite();
		int k = method_30575(predicate, mutable.set(blockPos), direction, i);
		int l = method_30575(predicate, mutable.set(blockPos), direction2, i);
		int m = k;
		class_5459.IntBounds[] intBoundss = new class_5459.IntBounds[k + 1 + l];
		intBoundss[k] = new class_5459.IntBounds(
			method_30575(predicate, mutable.set(blockPos), direction3, j), method_30575(predicate, mutable.set(blockPos), direction4, j)
		);
		int n = intBoundss[k].min;

		for (int o = 1; o <= k; o++) {
			class_5459.IntBounds intBounds = intBoundss[m - (o - 1)];
			intBoundss[m - o] = new class_5459.IntBounds(
				method_30575(predicate, mutable.set(blockPos).move(direction, o), direction3, intBounds.min),
				method_30575(predicate, mutable.set(blockPos).move(direction, o), direction4, intBounds.max)
			);
		}

		for (int o = 1; o <= l; o++) {
			class_5459.IntBounds intBounds = intBoundss[m + o - 1];
			intBoundss[m + o] = new class_5459.IntBounds(
				method_30575(predicate, mutable.set(blockPos).move(direction2, o), direction3, intBounds.min),
				method_30575(predicate, mutable.set(blockPos).move(direction2, o), direction4, intBounds.max)
			);
		}

		int o = 0;
		int p = 0;
		int q = 0;
		int r = 0;
		int[] is = new int[intBoundss.length];

		for (int s = n; s >= 0; s--) {
			for (int t = 0; t < intBoundss.length; t++) {
				class_5459.IntBounds intBounds2 = intBoundss[t];
				int u = n - intBounds2.min;
				int v = n + intBounds2.max;
				is[t] = s >= u && s <= v ? v + 1 - s : 0;
			}

			Pair<class_5459.IntBounds, Integer> pair = method_30576(is);
			class_5459.IntBounds intBounds2 = pair.getFirst();
			int u = 1 + intBounds2.max - intBounds2.min;
			int v = pair.getSecond();
			if (u * v > q * r) {
				o = intBounds2.min;
				p = s;
				q = u;
				r = v;
			}
		}

		return new class_5459.class_5460(blockPos.method_30513(axis, o - m).method_30513(axis2, p - n), q, r);
	}

	private static int method_30575(Predicate<BlockPos> predicate, BlockPos.Mutable mutable, Direction direction, int i) {
		int j = 0;

		while (j < i && predicate.test(mutable.move(direction))) {
			j++;
		}

		return j;
	}

	@VisibleForTesting
	static Pair<class_5459.IntBounds, Integer> method_30576(int[] is) {
		int i = 0;
		int j = 0;
		int k = 0;
		IntStack intStack = new IntArrayList();
		intStack.push(0);

		for (int l = 1; l <= is.length; l++) {
			int m = l == is.length ? 0 : is[l];

			while (!intStack.isEmpty()) {
				int n = is[intStack.topInt()];
				if (m >= n) {
					intStack.push(l);
					break;
				}

				intStack.popInt();
				int o = intStack.isEmpty() ? 0 : intStack.topInt() + 1;
				if (n * (l - o) > k * (j - i)) {
					j = l;
					i = o;
					k = n;
				}
			}

			if (intStack.isEmpty()) {
				intStack.push(l);
			}
		}

		return new Pair<>(new class_5459.IntBounds(i, j - 1), k);
	}

	public static class IntBounds {
		public final int min;
		public final int max;

		public IntBounds(int min, int max) {
			this.min = min;
			this.max = max;
		}

		public String toString() {
			return "IntBounds{min=" + this.min + ", max=" + this.max + '}';
		}
	}

	public static class class_5460 {
		public final BlockPos field_25936;
		public final int field_25937;
		public final int field_25938;

		public class_5460(BlockPos blockPos, int i, int j) {
			this.field_25936 = blockPos;
			this.field_25937 = i;
			this.field_25938 = j;
		}
	}
}
