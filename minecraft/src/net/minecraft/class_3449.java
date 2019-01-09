package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public abstract class class_3449 {
	public static final class_3449 field_16713 = new class_3449(class_3031.field_13547, 0, 0, class_1972.field_9451, class_3341.method_14665(), 0, 0L) {
		@Override
		public void method_16655(class_2794<?> arg, class_3485 arg2, int i, int j, class_1959 arg3) {
		}
	};
	private final class_3195<?> field_16714;
	protected final List<class_3443> field_15325 = Lists.<class_3443>newArrayList();
	protected class_3341 field_15330;
	private final int field_15329;
	private final int field_15327;
	private final class_1959 field_15328;
	private int field_15326;
	protected final class_2919 field_16715;

	public class_3449(class_3195<?> arg, int i, int j, class_1959 arg2, class_3341 arg3, int k, long l) {
		this.field_16714 = arg;
		this.field_15329 = i;
		this.field_15327 = j;
		this.field_15326 = k;
		this.field_15328 = arg2;
		this.field_16715 = new class_2919();
		this.field_16715.method_12663(l, i, j);
		this.field_15330 = arg3;
	}

	public abstract void method_16655(class_2794<?> arg, class_3485 arg2, int i, int j, class_1959 arg3);

	public class_3341 method_14968() {
		return this.field_15330;
	}

	public List<class_3443> method_14963() {
		return this.field_15325;
	}

	public void method_14974(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
		synchronized (this.field_15325) {
			Iterator<class_3443> iterator = this.field_15325.iterator();

			while (iterator.hasNext()) {
				class_3443 lv = (class_3443)iterator.next();
				if (lv.method_14935().method_14657(arg2) && !lv.method_14931(arg, random, arg2, arg3)) {
					iterator.remove();
				}
			}

			this.method_14969();
		}
	}

	protected void method_14969() {
		this.field_15330 = class_3341.method_14665();

		for (class_3443 lv : this.field_15325) {
			this.field_15330.method_14668(lv.method_14935());
		}
	}

	public class_2487 method_14972(int i, int j) {
		class_2487 lv = new class_2487();
		if (this.method_16657()) {
			lv.method_10582("id", class_2378.field_16644.method_10221(this.method_16656()).toString());
			lv.method_10582("biome", class_2378.field_11153.method_10221(this.field_15328).toString());
			lv.method_10569("ChunkX", i);
			lv.method_10569("ChunkZ", j);
			lv.method_10569("references", this.field_15326);
			lv.method_10566("BB", this.field_15330.method_14658());
			class_2499 lv2 = new class_2499();
			synchronized (this.field_15325) {
				for (class_3443 lv3 : this.field_15325) {
					lv2.method_10606(lv3.method_14946());
				}
			}

			lv.method_10566("Children", lv2);
			return lv;
		} else {
			lv.method_10582("id", "INVALID");
			return lv;
		}
	}

	protected void method_14978(int i, Random random, int j) {
		int k = i - j;
		int l = this.field_15330.method_14663() + 1;
		if (l < k) {
			l += random.nextInt(k - l);
		}

		int m = l - this.field_15330.field_14377;
		this.field_15330.method_14661(0, m, 0);

		for (class_3443 lv : this.field_15325) {
			lv.method_14922(0, m, 0);
		}
	}

	protected void method_14976(Random random, int i, int j) {
		int k = j - i + 1 - this.field_15330.method_14663();
		int l;
		if (k > 1) {
			l = i + random.nextInt(k);
		} else {
			l = i;
		}

		int m = l - this.field_15330.field_14380;
		this.field_15330.method_14661(0, m, 0);

		for (class_3443 lv : this.field_15325) {
			lv.method_14922(0, m, 0);
		}
	}

	public boolean method_16657() {
		return !this.field_15325.isEmpty();
	}

	public int method_14967() {
		return this.field_15329;
	}

	public int method_14966() {
		return this.field_15327;
	}

	public class_2338 method_14962() {
		return new class_2338(this.field_15329 << 4, 0, this.field_15327 << 4);
	}

	public boolean method_14979() {
		return this.field_15326 < this.method_14970();
	}

	public void method_14964() {
		this.field_15326++;
	}

	protected int method_14970() {
		return 1;
	}

	public class_3195<?> method_16656() {
		return this.field_16714;
	}
}
