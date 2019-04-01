package net.minecraft;

import com.google.common.base.MoreObjects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3341 {
	public int field_14381;
	public int field_14380;
	public int field_14379;
	public int field_14378;
	public int field_14377;
	public int field_14376;

	public class_3341() {
	}

	public class_3341(int[] is) {
		if (is.length == 6) {
			this.field_14381 = is[0];
			this.field_14380 = is[1];
			this.field_14379 = is[2];
			this.field_14378 = is[3];
			this.field_14377 = is[4];
			this.field_14376 = is[5];
		}
	}

	public static class_3341 method_14665() {
		return new class_3341(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
	}

	public static class_3341 method_14667(int i, int j, int k, int l, int m, int n, int o, int p, int q, class_2350 arg) {
		switch (arg) {
			case field_11043:
				return new class_3341(i + l, j + m, k - q + 1 + n, i + o - 1 + l, j + p - 1 + m, k + n);
			case field_11035:
				return new class_3341(i + l, j + m, k + n, i + o - 1 + l, j + p - 1 + m, k + q - 1 + n);
			case field_11039:
				return new class_3341(i - q + 1 + n, j + m, k + l, i + n, j + p - 1 + m, k + o - 1 + l);
			case field_11034:
				return new class_3341(i + n, j + m, k + l, i + q - 1 + n, j + p - 1 + m, k + o - 1 + l);
			default:
				return new class_3341(i + l, j + m, k + n, i + o - 1 + l, j + p - 1 + m, k + q - 1 + n);
		}
	}

	public static class_3341 method_14666(int i, int j, int k, int l, int m, int n) {
		return new class_3341(Math.min(i, l), Math.min(j, m), Math.min(k, n), Math.max(i, l), Math.max(j, m), Math.max(k, n));
	}

	public class_3341(class_3341 arg) {
		this.field_14381 = arg.field_14381;
		this.field_14380 = arg.field_14380;
		this.field_14379 = arg.field_14379;
		this.field_14378 = arg.field_14378;
		this.field_14377 = arg.field_14377;
		this.field_14376 = arg.field_14376;
	}

	public class_3341(int i, int j, int k, int l, int m, int n) {
		this.field_14381 = i;
		this.field_14380 = j;
		this.field_14379 = k;
		this.field_14378 = l;
		this.field_14377 = m;
		this.field_14376 = n;
	}

	public class_3341(class_2382 arg, class_2382 arg2) {
		this.field_14381 = Math.min(arg.method_10263(), arg2.method_10263());
		this.field_14380 = Math.min(arg.method_10264(), arg2.method_10264());
		this.field_14379 = Math.min(arg.method_10260(), arg2.method_10260());
		this.field_14378 = Math.max(arg.method_10263(), arg2.method_10263());
		this.field_14377 = Math.max(arg.method_10264(), arg2.method_10264());
		this.field_14376 = Math.max(arg.method_10260(), arg2.method_10260());
	}

	public class_3341(int i, int j, int k, int l) {
		this.field_14381 = i;
		this.field_14379 = j;
		this.field_14378 = k;
		this.field_14376 = l;
		this.field_14380 = 1;
		this.field_14377 = 512;
	}

	public boolean method_14657(class_3341 arg) {
		return this.field_14378 >= arg.field_14381
			&& this.field_14381 <= arg.field_14378
			&& this.field_14376 >= arg.field_14379
			&& this.field_14379 <= arg.field_14376
			&& this.field_14377 >= arg.field_14380
			&& this.field_14380 <= arg.field_14377;
	}

	public boolean method_14669(int i, int j, int k, int l) {
		return this.field_14378 >= i && this.field_14381 <= k && this.field_14376 >= j && this.field_14379 <= l;
	}

	public void method_14668(class_3341 arg) {
		this.field_14381 = Math.min(this.field_14381, arg.field_14381);
		this.field_14380 = Math.min(this.field_14380, arg.field_14380);
		this.field_14379 = Math.min(this.field_14379, arg.field_14379);
		this.field_14378 = Math.max(this.field_14378, arg.field_14378);
		this.field_14377 = Math.max(this.field_14377, arg.field_14377);
		this.field_14376 = Math.max(this.field_14376, arg.field_14376);
	}

	public void method_14661(int i, int j, int k) {
		this.field_14381 += i;
		this.field_14380 += j;
		this.field_14379 += k;
		this.field_14378 += i;
		this.field_14377 += j;
		this.field_14376 += k;
	}

	public class_3341 method_19311(int i, int j, int k) {
		return new class_3341(this.field_14381 + i, this.field_14380 + j, this.field_14379 + k, this.field_14378 + i, this.field_14377 + j, this.field_14376 + k);
	}

	public boolean method_14662(class_2382 arg) {
		return arg.method_10263() >= this.field_14381
			&& arg.method_10263() <= this.field_14378
			&& arg.method_10260() >= this.field_14379
			&& arg.method_10260() <= this.field_14376
			&& arg.method_10264() >= this.field_14380
			&& arg.method_10264() <= this.field_14377;
	}

	public class_2382 method_14659() {
		return new class_2382(this.field_14378 - this.field_14381, this.field_14377 - this.field_14380, this.field_14376 - this.field_14379);
	}

	public int method_14660() {
		return this.field_14378 - this.field_14381 + 1;
	}

	public int method_14663() {
		return this.field_14377 - this.field_14380 + 1;
	}

	public int method_14664() {
		return this.field_14376 - this.field_14379 + 1;
	}

	@Environment(EnvType.CLIENT)
	public class_2382 method_19635() {
		return new class_2338(
			this.field_14381 + (this.field_14378 - this.field_14381 + 1) / 2,
			this.field_14380 + (this.field_14377 - this.field_14380 + 1) / 2,
			this.field_14379 + (this.field_14376 - this.field_14379 + 1) / 2
		);
	}

	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("x0", this.field_14381)
			.add("y0", this.field_14380)
			.add("z0", this.field_14379)
			.add("x1", this.field_14378)
			.add("y1", this.field_14377)
			.add("z1", this.field_14376)
			.toString();
	}

	public class_2495 method_14658() {
		return new class_2495(new int[]{this.field_14381, this.field_14380, this.field_14379, this.field_14378, this.field_14377, this.field_14376});
	}
}
