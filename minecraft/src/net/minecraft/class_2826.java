package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2826 {
	private static final class_2837<class_2680> field_12879 = new class_2816<>(class_2248.field_10651, class_2246.field_10124.method_9564());
	private final int field_12880;
	private short field_12877;
	private short field_12882;
	private short field_12881;
	private final class_2841<class_2680> field_12878;

	public class_2826(int i) {
		this(i, (short)0, (short)0, (short)0);
	}

	public class_2826(int i, short s, short t, short u) {
		this.field_12880 = i;
		this.field_12877 = s;
		this.field_12882 = t;
		this.field_12881 = u;
		this.field_12878 = new class_2841<>(
			field_12879, class_2248.field_10651, class_2512::method_10681, class_2512::method_10686, class_2246.field_10124.method_9564()
		);
	}

	public class_2680 method_12254(int i, int j, int k) {
		return this.field_12878.method_12321(i, j, k);
	}

	public class_3610 method_12255(int i, int j, int k) {
		return this.field_12878.method_12321(i, j, k).method_11618();
	}

	public void method_16676() {
		this.field_12878.method_12334();
	}

	public void method_16677() {
		this.field_12878.method_12335();
	}

	public class_2680 method_16675(int i, int j, int k, class_2680 arg) {
		return this.method_12256(i, j, k, arg, true);
	}

	public class_2680 method_12256(int i, int j, int k, class_2680 arg, boolean bl) {
		class_2680 lv;
		if (bl) {
			lv = this.field_12878.method_12328(i, j, k, arg);
		} else {
			lv = this.field_12878.method_16678(i, j, k, arg);
		}

		class_3610 lv2 = lv.method_11618();
		class_3610 lv3 = arg.method_11618();
		if (!lv.method_11588()) {
			this.field_12877--;
			if (lv.method_11616()) {
				this.field_12882--;
			}
		}

		if (!lv2.method_15769()) {
			this.field_12881--;
		}

		if (!arg.method_11588()) {
			this.field_12877++;
			if (arg.method_11616()) {
				this.field_12882++;
			}
		}

		if (!lv3.method_15769()) {
			this.field_12881++;
		}

		return lv;
	}

	public boolean method_12261() {
		return this.field_12877 == 0;
	}

	public static boolean method_18090(@Nullable class_2826 arg) {
		return arg == class_2818.field_12852 || arg.method_12261();
	}

	public boolean method_12262() {
		return this.method_12263() || this.method_12264();
	}

	public boolean method_12263() {
		return this.field_12882 > 0;
	}

	public boolean method_12264() {
		return this.field_12881 > 0;
	}

	public int method_12259() {
		return this.field_12880;
	}

	public void method_12253() {
		this.field_12877 = 0;
		this.field_12882 = 0;
		this.field_12881 = 0;

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				for (int k = 0; k < 16; k++) {
					class_2680 lv = this.method_12254(i, j, k);
					class_3610 lv2 = this.method_12255(i, j, k);
					if (!lv.method_11588()) {
						this.field_12877++;
						if (lv.method_11616()) {
							this.field_12882++;
						}
					}

					if (!lv2.method_15769()) {
						this.field_12877++;
						if (lv2.method_15773()) {
							this.field_12881++;
						}
					}
				}
			}
		}
	}

	public class_2841<class_2680> method_12265() {
		return this.field_12878;
	}

	@Environment(EnvType.CLIENT)
	public void method_12258(class_2540 arg) {
		this.field_12877 = arg.readShort();
		this.field_12878.method_12326(arg);
	}

	public void method_12257(class_2540 arg) {
		arg.writeShort(this.field_12877);
		this.field_12878.method_12325(arg);
	}

	public int method_12260() {
		return 2 + this.field_12878.method_12327();
	}

	public boolean method_19523(class_2680 arg) {
		return this.field_12878.method_19526(arg);
	}
}
