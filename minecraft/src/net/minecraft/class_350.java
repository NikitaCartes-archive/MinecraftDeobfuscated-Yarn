package net.minecraft;

import com.google.common.collect.Lists;
import java.util.AbstractList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_350<E extends class_350.class_351<E>> extends class_358 {
	private final List<E> field_2142 = new class_350.class_352();

	public class_350(class_310 arg, int i, int j, int k, int l, int m) {
		super(arg, i, j, k, l, m);
	}

	@Override
	protected boolean method_1937(int i, int j, double d, double e) {
		return this.method_1900(i).method_16807(d, e, j);
	}

	@Override
	protected boolean method_1955(int i) {
		return false;
	}

	@Override
	protected void method_1936() {
	}

	@Override
	protected void method_1935(int i, int j, int k, int l, int m, int n, float f) {
		this.method_1900(i).method_1903(this.method_1932(), l, m, n, this.method_1938((double)m, (double)n) && this.method_1956((double)m, (double)n) == i, f);
	}

	@Override
	protected void method_1952(int i, int j, int k, float f) {
		this.method_1900(i).method_1904(f);
	}

	@Override
	public final List<E> method_1968() {
		return this.field_2142;
	}

	protected final void method_1902() {
		this.field_2142.clear();
	}

	private E method_1900(int i) {
		return (E)this.method_1968().get(i);
	}

	protected final void method_1901(E arg) {
		this.field_2142.add(arg);
	}

	@Override
	public void method_1946(int i) {
		this.field_2176 = i;
		this.field_2177 = class_156.method_658();
	}

	@Override
	protected final int method_1947() {
		return this.method_1968().size();
	}

	@Environment(EnvType.CLIENT)
	public abstract static class class_351<E extends class_350.class_351<E>> implements class_364 {
		protected class_350<E> field_2144;
		protected int field_2143;

		protected class_350<E> method_1905() {
			return this.field_2144;
		}

		protected int method_1908() {
			return this.field_2143;
		}

		protected int method_1906() {
			return this.field_2144.field_2166 + 4 - this.field_2144.method_1944() + this.field_2143 * this.field_2144.field_2179 + this.field_2144.field_2174;
		}

		protected int method_1907() {
			return this.field_2144.field_2180 + this.field_2144.field_2168 / 2 - this.field_2144.method_1932() / 2 + 2;
		}

		protected void method_1904(float f) {
		}

		public abstract void method_1903(int i, int j, int k, int l, boolean bl, float f);
	}

	@Environment(EnvType.CLIENT)
	class class_352 extends AbstractList<E> {
		private final List<E> field_2146 = Lists.<E>newArrayList();

		private class_352() {
		}

		public E method_1912(int i) {
			return (E)this.field_2146.get(i);
		}

		public int size() {
			return this.field_2146.size();
		}

		public E method_1909(int i, E arg) {
			E lv = (E)this.field_2146.set(i, arg);
			arg.field_2144 = class_350.this;
			arg.field_2143 = i;
			return lv;
		}

		public void method_1910(int i, E arg) {
			this.field_2146.add(i, arg);
			arg.field_2144 = class_350.this;
			arg.field_2143 = i;
			int j = i + 1;

			while (j < this.size()) {
				this.method_1912(j).field_2143 = j++;
			}
		}

		public E method_1911(int i) {
			E lv = (E)this.field_2146.remove(i);
			int j = i;

			while (j < this.size()) {
				this.method_1912(j).field_2143 = j++;
			}

			return lv;
		}
	}
}
