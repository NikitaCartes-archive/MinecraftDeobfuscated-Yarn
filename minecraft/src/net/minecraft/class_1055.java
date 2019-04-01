package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1055 {
	private static final Comparator<class_1055.class_1056> field_18030 = Comparator.comparing(arg -> -arg.field_5247)
		.thenComparing(arg -> -arg.field_5248)
		.thenComparing(arg -> arg.field_5249.method_4598());
	private final int field_5243;
	private final Set<class_1055.class_1056> field_5237 = Sets.<class_1055.class_1056>newHashSetWithExpectedSize(256);
	private final List<class_1055.class_1057> field_5239 = Lists.<class_1055.class_1057>newArrayListWithCapacity(256);
	private int field_5242;
	private int field_5241;
	private final int field_5240;
	private final int field_5238;

	public class_1055(int i, int j, int k) {
		this.field_5243 = k;
		this.field_5240 = i;
		this.field_5238 = j;
	}

	public int method_4554() {
		return this.field_5242;
	}

	public int method_4555() {
		return this.field_5241;
	}

	public void method_4553(class_1058 arg) {
		class_1055.class_1056 lv = new class_1055.class_1056(arg, this.field_5243);
		this.field_5237.add(lv);
	}

	public void method_4557() {
		List<class_1055.class_1056> list = Lists.<class_1055.class_1056>newArrayList(this.field_5237);
		list.sort(field_18030);

		for (class_1055.class_1056 lv : list) {
			if (!this.method_4550(lv)) {
				throw new class_1054(lv.field_5249);
			}
		}

		this.field_5242 = class_3532.method_15339(this.field_5242);
		this.field_5241 = class_3532.method_15339(this.field_5241);
	}

	public List<class_1058> method_4549() {
		List<class_1058> list = Lists.<class_1058>newArrayList();

		for (class_1055.class_1057 lv : this.field_5239) {
			lv.method_4568(arg -> {
				class_1055.class_1056 lvx = arg.method_4565();
				class_1058 lv2 = lvx.field_5249;
				lv2.method_4587(this.field_5242, this.field_5241, arg.method_4569(), arg.method_4567());
				list.add(lv2);
			});
		}

		return list;
	}

	private static int method_4551(int i, int j) {
		return (i >> j) + ((i & (1 << j) - 1) == 0 ? 0 : 1) << j;
	}

	private boolean method_4550(class_1055.class_1056 arg) {
		for (class_1055.class_1057 lv : this.field_5239) {
			if (lv.method_4566(arg)) {
				return true;
			}
		}

		return this.method_4552(arg);
	}

	private boolean method_4552(class_1055.class_1056 arg) {
		int i = class_3532.method_15339(this.field_5242);
		int j = class_3532.method_15339(this.field_5241);
		int k = class_3532.method_15339(this.field_5242 + arg.field_5248);
		int l = class_3532.method_15339(this.field_5241 + arg.field_5247);
		boolean bl = k <= this.field_5240;
		boolean bl2 = l <= this.field_5238;
		if (!bl && !bl2) {
			return false;
		} else {
			boolean bl3 = bl && i != k;
			boolean bl4 = bl2 && j != l;
			boolean bl5;
			if (bl3 ^ bl4) {
				bl5 = bl3;
			} else {
				bl5 = bl && i <= j;
			}

			class_1055.class_1057 lv;
			if (bl5) {
				if (this.field_5241 == 0) {
					this.field_5241 = arg.field_5247;
				}

				lv = new class_1055.class_1057(this.field_5242, 0, arg.field_5248, this.field_5241);
				this.field_5242 = this.field_5242 + arg.field_5248;
			} else {
				lv = new class_1055.class_1057(0, this.field_5241, this.field_5242, arg.field_5247);
				this.field_5241 = this.field_5241 + arg.field_5247;
			}

			lv.method_4566(arg);
			this.field_5239.add(lv);
			return true;
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_1056 {
		public final class_1058 field_5249;
		public final int field_5248;
		public final int field_5247;

		public class_1056(class_1058 arg, int i) {
			this.field_5249 = arg;
			this.field_5248 = class_1055.method_4551(arg.method_4578(), i);
			this.field_5247 = class_1055.method_4551(arg.method_4595(), i);
		}

		public String toString() {
			return "Holder{width=" + this.field_5248 + ", height=" + this.field_5247 + '}';
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_1057 {
		private final int field_5253;
		private final int field_5252;
		private final int field_5251;
		private final int field_5250;
		private List<class_1055.class_1057> field_5255;
		private class_1055.class_1056 field_5254;

		public class_1057(int i, int j, int k, int l) {
			this.field_5253 = i;
			this.field_5252 = j;
			this.field_5251 = k;
			this.field_5250 = l;
		}

		public class_1055.class_1056 method_4565() {
			return this.field_5254;
		}

		public int method_4569() {
			return this.field_5253;
		}

		public int method_4567() {
			return this.field_5252;
		}

		public boolean method_4566(class_1055.class_1056 arg) {
			if (this.field_5254 != null) {
				return false;
			} else {
				int i = arg.field_5248;
				int j = arg.field_5247;
				if (i <= this.field_5251 && j <= this.field_5250) {
					if (i == this.field_5251 && j == this.field_5250) {
						this.field_5254 = arg;
						return true;
					} else {
						if (this.field_5255 == null) {
							this.field_5255 = Lists.<class_1055.class_1057>newArrayListWithCapacity(1);
							this.field_5255.add(new class_1055.class_1057(this.field_5253, this.field_5252, i, j));
							int k = this.field_5251 - i;
							int l = this.field_5250 - j;
							if (l > 0 && k > 0) {
								int m = Math.max(this.field_5250, k);
								int n = Math.max(this.field_5251, l);
								if (m >= n) {
									this.field_5255.add(new class_1055.class_1057(this.field_5253, this.field_5252 + j, i, l));
									this.field_5255.add(new class_1055.class_1057(this.field_5253 + i, this.field_5252, k, this.field_5250));
								} else {
									this.field_5255.add(new class_1055.class_1057(this.field_5253 + i, this.field_5252, k, j));
									this.field_5255.add(new class_1055.class_1057(this.field_5253, this.field_5252 + j, this.field_5251, l));
								}
							} else if (k == 0) {
								this.field_5255.add(new class_1055.class_1057(this.field_5253, this.field_5252 + j, i, l));
							} else if (l == 0) {
								this.field_5255.add(new class_1055.class_1057(this.field_5253 + i, this.field_5252, k, j));
							}
						}

						for (class_1055.class_1057 lv : this.field_5255) {
							if (lv.method_4566(arg)) {
								return true;
							}
						}

						return false;
					}
				} else {
					return false;
				}
			}
		}

		public void method_4568(Consumer<class_1055.class_1057> consumer) {
			if (this.field_5254 != null) {
				consumer.accept(this);
			} else if (this.field_5255 != null) {
				for (class_1055.class_1057 lv : this.field_5255) {
					lv.method_4568(consumer);
				}
			}
		}

		public String toString() {
			return "Slot{originX="
				+ this.field_5253
				+ ", originY="
				+ this.field_5252
				+ ", width="
				+ this.field_5251
				+ ", height="
				+ this.field_5250
				+ ", texture="
				+ this.field_5254
				+ ", subSlots="
				+ this.field_5255
				+ '}';
		}
	}
}
