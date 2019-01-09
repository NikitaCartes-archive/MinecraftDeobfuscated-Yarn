package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1055 {
	private final int field_5243;
	private final Set<class_1055.class_1056> field_5237 = Sets.<class_1055.class_1056>newHashSetWithExpectedSize(256);
	private final List<class_1055.class_1057> field_5239 = Lists.<class_1055.class_1057>newArrayListWithCapacity(256);
	private int field_5242;
	private int field_5241;
	private final int field_5240;
	private final int field_5238;
	private final int field_5236;

	public class_1055(int i, int j, int k, int l) {
		this.field_5243 = l;
		this.field_5240 = i;
		this.field_5238 = j;
		this.field_5236 = k;
	}

	public int method_4554() {
		return this.field_5242;
	}

	public int method_4555() {
		return this.field_5241;
	}

	public void method_4553(class_1058 arg) {
		class_1055.class_1056 lv = new class_1055.class_1056(arg, this.field_5243);
		if (this.field_5236 > 0) {
			lv.method_4559(this.field_5236);
		}

		this.field_5237.add(lv);
	}

	public void method_4557() {
		class_1055.class_1056[] lvs = (class_1055.class_1056[])this.field_5237.toArray(new class_1055.class_1056[this.field_5237.size()]);
		Arrays.sort(lvs);

		for (class_1055.class_1056 lv : lvs) {
			if (!this.method_4550(lv)) {
				String string = String.format(
					"Unable to fit: %s - size: %dx%d - Maybe try a lowerresolution resourcepack?",
					lv.method_4558().method_4598(),
					lv.method_4558().method_4578(),
					lv.method_4558().method_4595()
				);
				throw new class_1054(lv, string);
			}
		}

		this.field_5242 = class_3532.method_15339(this.field_5242);
		this.field_5241 = class_3532.method_15339(this.field_5241);
	}

	public List<class_1058> method_4549() {
		List<class_1055.class_1057> list = Lists.<class_1055.class_1057>newArrayList();

		for (class_1055.class_1057 lv : this.field_5239) {
			lv.method_4568(list);
		}

		List<class_1058> list2 = Lists.<class_1058>newArrayList();

		for (class_1055.class_1057 lv2 : list) {
			class_1055.class_1056 lv3 = lv2.method_4565();
			class_1058 lv4 = lv3.method_4558();
			lv4.method_4587(this.field_5242, this.field_5241, lv2.method_4569(), lv2.method_4567(), lv3.method_4561());
			list2.add(lv4);
		}

		return list2;
	}

	private static int method_4551(int i, int j) {
		return (i >> j) + ((i & (1 << j) - 1) == 0 ? 0 : 1) << j;
	}

	private boolean method_4550(class_1055.class_1056 arg) {
		class_1058 lv = arg.method_4558();
		boolean bl = lv.method_4578() != lv.method_4595();

		for (int i = 0; i < this.field_5239.size(); i++) {
			if (((class_1055.class_1057)this.field_5239.get(i)).method_4566(arg)) {
				return true;
			}

			if (bl) {
				arg.method_4564();
				if (((class_1055.class_1057)this.field_5239.get(i)).method_4566(arg)) {
					return true;
				}

				arg.method_4564();
			}
		}

		return this.method_4552(arg);
	}

	private boolean method_4552(class_1055.class_1056 arg) {
		int i = Math.min(arg.method_4562(), arg.method_4560());
		int j = Math.max(arg.method_4562(), arg.method_4560());
		int k = class_3532.method_15339(this.field_5242);
		int l = class_3532.method_15339(this.field_5241);
		int m = class_3532.method_15339(this.field_5242 + i);
		int n = class_3532.method_15339(this.field_5241 + i);
		boolean bl = m <= this.field_5240;
		boolean bl2 = n <= this.field_5238;
		if (!bl && !bl2) {
			return false;
		} else {
			boolean bl3 = bl && k != m;
			boolean bl4 = bl2 && l != n;
			boolean bl5;
			if (bl3 ^ bl4) {
				bl5 = bl3;
			} else {
				bl5 = bl && k <= l;
			}

			class_1055.class_1057 lv;
			if (bl5) {
				if (arg.method_4562() > arg.method_4560()) {
					arg.method_4564();
				}

				if (this.field_5241 == 0) {
					this.field_5241 = arg.method_4560();
				}

				lv = new class_1055.class_1057(this.field_5242, 0, arg.method_4562(), this.field_5241);
				this.field_5242 = this.field_5242 + arg.method_4562();
			} else {
				lv = new class_1055.class_1057(0, this.field_5241, this.field_5242, arg.method_4560());
				this.field_5241 = this.field_5241 + arg.method_4560();
			}

			lv.method_4566(arg);
			this.field_5239.add(lv);
			return true;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_1056 implements Comparable<class_1055.class_1056> {
		private final class_1058 field_5249;
		private final int field_5248;
		private final int field_5247;
		private final int field_5245;
		private boolean field_5246;
		private float field_5244 = 1.0F;

		public class_1056(class_1058 arg, int i) {
			this.field_5249 = arg;
			this.field_5248 = arg.method_4578();
			this.field_5247 = arg.method_4595();
			this.field_5245 = i;
			this.field_5246 = class_1055.method_4551(this.field_5247, i) > class_1055.method_4551(this.field_5248, i);
		}

		public class_1058 method_4558() {
			return this.field_5249;
		}

		public int method_4562() {
			int i = this.field_5246 ? this.field_5247 : this.field_5248;
			return class_1055.method_4551((int)((float)i * this.field_5244), this.field_5245);
		}

		public int method_4560() {
			int i = this.field_5246 ? this.field_5248 : this.field_5247;
			return class_1055.method_4551((int)((float)i * this.field_5244), this.field_5245);
		}

		public void method_4564() {
			this.field_5246 = !this.field_5246;
		}

		public boolean method_4561() {
			return this.field_5246;
		}

		public void method_4559(int i) {
			if (this.field_5248 > i && this.field_5247 > i) {
				this.field_5244 = (float)i / (float)Math.min(this.field_5248, this.field_5247);
			}
		}

		public String toString() {
			return "Holder{width=" + this.field_5248 + ", height=" + this.field_5247 + '}';
		}

		public int method_4563(class_1055.class_1056 arg) {
			int i;
			if (this.method_4560() == arg.method_4560()) {
				if (this.method_4562() == arg.method_4562()) {
					return this.field_5249.method_4598().toString().compareTo(arg.field_5249.method_4598().toString());
				}

				i = this.method_4562() < arg.method_4562() ? 1 : -1;
			} else {
				i = this.method_4560() < arg.method_4560() ? 1 : -1;
			}

			return i;
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
				int i = arg.method_4562();
				int j = arg.method_4560();
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

		public void method_4568(List<class_1055.class_1057> list) {
			if (this.field_5254 != null) {
				list.add(this);
			} else if (this.field_5255 != null) {
				for (class_1055.class_1057 lv : this.field_5255) {
					lv.method_4568(list);
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
