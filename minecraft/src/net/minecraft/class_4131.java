package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class class_4131<U> {
	private final List<class_4131<U>.class_4132<? extends U>> field_18397 = Lists.<class_4131<U>.class_4132<? extends U>>newArrayList();
	private final Random field_18398;

	public class_4131() {
		this(new Random());
	}

	public class_4131(Random random) {
		this.field_18398 = random;
	}

	public void method_19031(U object, int i) {
		this.field_18397.add(new class_4131.class_4132(object, i));
	}

	public void method_19029() {
		this.field_18397.forEach(arg -> arg.method_19034(this.field_18398.nextFloat()));
		this.field_18397.sort(Comparator.comparingDouble(class_4131.class_4132::method_19033));
	}

	public Stream<? extends U> method_19032() {
		return this.field_18397.stream().map(class_4131.class_4132::method_19035);
	}

	class class_4132<T> {
		private final T field_18400;
		private final int field_18401;
		private double field_18402;

		private class_4132(T object, int i) {
			this.field_18401 = i;
			this.field_18400 = object;
		}

		public double method_19033() {
			return this.field_18402;
		}

		public void method_19034(float f) {
			this.field_18402 = -Math.pow((double)f, (double)(1.0F / (float)this.field_18401));
		}

		public T method_19035() {
			return this.field_18400;
		}
	}
}
