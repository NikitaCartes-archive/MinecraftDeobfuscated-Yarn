package net.minecraft;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.Random;

public class class_3631 implements class_3628<class_3626> {
	private final Long2IntLinkedOpenHashMap field_16045;
	private final int field_16044;
	protected long field_16731;
	protected class_3756 field_16732;
	private long field_16730;
	private long field_16729;

	public class_3631(int i, long l, long m) {
		this.field_16731 = m;
		this.field_16731 = this.field_16731 * (this.field_16731 * 6364136223846793005L + 1442695040888963407L);
		this.field_16731 += m;
		this.field_16731 = this.field_16731 * (this.field_16731 * 6364136223846793005L + 1442695040888963407L);
		this.field_16731 += m;
		this.field_16731 = this.field_16731 * (this.field_16731 * 6364136223846793005L + 1442695040888963407L);
		this.field_16731 += m;
		this.field_16045 = new Long2IntLinkedOpenHashMap(16, 0.25F);
		this.field_16045.defaultReturnValue(Integer.MIN_VALUE);
		this.field_16044 = i;
		this.method_16671(l);
	}

	public class_3626 method_15837(class_4 arg) {
		return new class_3626(this.field_16045, this.field_16044, arg);
	}

	public class_3626 method_15838(class_4 arg, class_3626 arg2) {
		return new class_3626(this.field_16045, Math.min(1024, arg2.method_15827() * 4), arg);
	}

	public class_3626 method_15836(class_4 arg, class_3626 arg2, class_3626 arg3) {
		return new class_3626(this.field_16045, Math.min(1024, Math.max(arg2.method_15827(), arg3.method_15827()) * 4), arg);
	}

	public void method_16671(long l) {
		this.field_16730 = l;
		this.field_16730 = this.field_16730 * (this.field_16730 * 6364136223846793005L + 1442695040888963407L);
		this.field_16730 = this.field_16730 + this.field_16731;
		this.field_16730 = this.field_16730 * (this.field_16730 * 6364136223846793005L + 1442695040888963407L);
		this.field_16730 = this.field_16730 + this.field_16731;
		this.field_16730 = this.field_16730 * (this.field_16730 * 6364136223846793005L + 1442695040888963407L);
		this.field_16730 = this.field_16730 + this.field_16731;
		this.field_16732 = new class_3756(new Random(l));
	}

	@Override
	public void method_15830(long l, long m) {
		this.field_16729 = this.field_16730;
		this.field_16729 = this.field_16729 * (this.field_16729 * 6364136223846793005L + 1442695040888963407L);
		this.field_16729 += l;
		this.field_16729 = this.field_16729 * (this.field_16729 * 6364136223846793005L + 1442695040888963407L);
		this.field_16729 += m;
		this.field_16729 = this.field_16729 * (this.field_16729 * 6364136223846793005L + 1442695040888963407L);
		this.field_16729 += l;
		this.field_16729 = this.field_16729 * (this.field_16729 * 6364136223846793005L + 1442695040888963407L);
		this.field_16729 += m;
	}

	@Override
	public int method_15834(int i) {
		int j = (int)((this.field_16729 >> 24) % (long)i);
		if (j < 0) {
			j += i;
		}

		this.field_16729 = this.field_16729 * (this.field_16729 * 6364136223846793005L + 1442695040888963407L);
		this.field_16729 = this.field_16729 + this.field_16730;
		return j;
	}

	@Override
	public class_3756 method_15835() {
		return this.field_16732;
	}
}
