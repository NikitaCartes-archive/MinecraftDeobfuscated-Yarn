package net.minecraft;

import javax.annotation.Nullable;

public class class_3525<V> {
	private transient class_3525.class_3526<V>[] field_15710;
	private transient int field_15709;
	private int field_15708;
	private final float field_15707 = 0.75F;

	public class_3525() {
		this.field_15708 = 12;
		this.field_15710 = new class_3525.class_3526[16];
	}

	private static int method_15314(int i) {
		i ^= i >>> 20 ^ i >>> 12;
		return i ^ i >>> 7 ^ i >>> 4;
	}

	private static int method_15319(int i, int j) {
		return i & j - 1;
	}

	@Nullable
	public V method_15316(int i) {
		int j = method_15314(i);

		for (class_3525.class_3526<V> lv = this.field_15710[method_15319(j, this.field_15710.length)]; lv != null; lv = lv.field_15711) {
			if (lv.field_15713 == i) {
				return lv.field_15714;
			}
		}

		return null;
	}

	public boolean method_15311(int i) {
		return this.method_15320(i) != null;
	}

	@Nullable
	final class_3525.class_3526<V> method_15320(int i) {
		int j = method_15314(i);

		for (class_3525.class_3526<V> lv = this.field_15710[method_15319(j, this.field_15710.length)]; lv != null; lv = lv.field_15711) {
			if (lv.field_15713 == i) {
				return lv;
			}
		}

		return null;
	}

	public void method_15313(int i, V object) {
		int j = method_15314(i);
		int k = method_15319(j, this.field_15710.length);

		for (class_3525.class_3526<V> lv = this.field_15710[k]; lv != null; lv = lv.field_15711) {
			if (lv.field_15713 == i) {
				lv.field_15714 = object;
				return;
			}
		}

		this.method_15317(j, i, object, k);
	}

	private void method_15318(int i) {
		class_3525.class_3526<V>[] lvs = this.field_15710;
		int j = lvs.length;
		if (j == 1073741824) {
			this.field_15708 = Integer.MAX_VALUE;
		} else {
			class_3525.class_3526<V>[] lvs2 = new class_3525.class_3526[i];
			this.method_15309(lvs2);
			this.field_15710 = lvs2;
			this.field_15708 = (int)((float)i * this.field_15707);
		}
	}

	private void method_15309(class_3525.class_3526<V>[] args) {
		class_3525.class_3526<V>[] lvs = this.field_15710;
		int i = args.length;

		for (int j = 0; j < lvs.length; j++) {
			class_3525.class_3526<V> lv = lvs[j];
			if (lv != null) {
				lvs[j] = null;

				while (true) {
					class_3525.class_3526<V> lv2 = lv.field_15711;
					int k = method_15319(lv.field_15712, i);
					lv.field_15711 = args[k];
					args[k] = lv;
					lv = lv2;
					if (lv2 == null) {
						break;
					}
				}
			}
		}
	}

	@Nullable
	public V method_15312(int i) {
		class_3525.class_3526<V> lv = this.method_15310(i);
		return lv == null ? null : lv.field_15714;
	}

	@Nullable
	final class_3525.class_3526<V> method_15310(int i) {
		int j = method_15314(i);
		int k = method_15319(j, this.field_15710.length);
		class_3525.class_3526<V> lv = this.field_15710[k];
		class_3525.class_3526<V> lv2 = lv;

		while (lv2 != null) {
			class_3525.class_3526<V> lv3 = lv2.field_15711;
			if (lv2.field_15713 == i) {
				this.field_15709--;
				if (lv == lv2) {
					this.field_15710[k] = lv3;
				} else {
					lv.field_15711 = lv3;
				}

				return lv2;
			}

			lv = lv2;
			lv2 = lv3;
		}

		return lv2;
	}

	public void method_15321() {
		class_3525.class_3526<V>[] lvs = this.field_15710;

		for (int i = 0; i < lvs.length; i++) {
			lvs[i] = null;
		}

		this.field_15709 = 0;
	}

	private void method_15317(int i, int j, V object, int k) {
		class_3525.class_3526<V> lv = this.field_15710[k];
		this.field_15710[k] = new class_3525.class_3526<>(i, j, object, lv);
		if (this.field_15709++ >= this.field_15708) {
			this.method_15318(2 * this.field_15710.length);
		}
	}

	static class class_3526<V> {
		private final int field_15713;
		private V field_15714;
		private class_3525.class_3526<V> field_15711;
		private final int field_15712;

		class_3526(int i, int j, V object, class_3525.class_3526<V> arg) {
			this.field_15714 = object;
			this.field_15711 = arg;
			this.field_15713 = j;
			this.field_15712 = i;
		}

		public final int method_15327() {
			return this.field_15713;
		}

		public final V method_15323() {
			return this.field_15714;
		}

		public final boolean equals(Object object) {
			if (!(object instanceof class_3525.class_3526)) {
				return false;
			} else {
				class_3525.class_3526<V> lv = (class_3525.class_3526<V>)object;
				if (this.field_15713 == lv.field_15713) {
					Object object2 = this.method_15323();
					Object object3 = lv.method_15323();
					if (object2 == object3 || object2 != null && object2.equals(object3)) {
						return true;
					}
				}

				return false;
			}
		}

		public final int hashCode() {
			return class_3525.method_15314(this.field_15713);
		}

		public final String toString() {
			return this.method_15327() + "=" + this.method_15323();
		}
	}
}
