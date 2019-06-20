package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;

public class class_4217 extends class_4097<class_1646> {
	@Nullable
	private class_2338 field_18858;
	private boolean field_18859;
	private boolean field_18860;
	private long field_18861;
	private int field_19239;
	private List<class_2338> field_19351 = Lists.<class_2338>newArrayList();

	public class_4217() {
		super(
			ImmutableMap.of(
				class_4140.field_18446, class_4141.field_18457, class_4140.field_18445, class_4141.field_18457, class_4140.field_18873, class_4141.field_18456
			)
		);
	}

	protected boolean method_19564(class_3218 arg, class_1646 arg2) {
		if (!arg.method_8450().method_8355(class_1928.field_19388)) {
			return false;
		} else if (arg2.method_7231().method_16924() != class_3852.field_17056) {
			return false;
		} else {
			this.field_18859 = arg2.method_19623();
			this.field_18860 = false;
			class_1277 lv = arg2.method_18011();
			int i = lv.method_5439();

			for (int j = 0; j < i; j++) {
				if (lv.method_5438(j).method_7960()) {
					this.field_18860 = true;
					break;
				}
			}

			class_2338.class_2339 lv2 = new class_2338.class_2339(arg2.field_5987, arg2.field_6010, arg2.field_6035);
			this.field_19351.clear();

			for (int k = -1; k <= 1; k++) {
				for (int l = -1; l <= 1; l++) {
					for (int m = -1; m <= 1; m++) {
						lv2.method_10102(arg2.field_5987 + (double)k, arg2.field_6010 + (double)l, arg2.field_6035 + (double)m);
						if (this.method_20640(lv2, arg)) {
							this.field_19351.add(new class_2338(lv2));
						}
					}
				}
			}

			this.field_18858 = this.method_20641(arg);
			return (this.field_18859 || this.field_18860) && this.field_18858 != null;
		}
	}

	@Nullable
	private class_2338 method_20641(class_3218 arg) {
		return this.field_19351.isEmpty() ? null : (class_2338)this.field_19351.get(arg.method_8409().nextInt(this.field_19351.size()));
	}

	private boolean method_20640(class_2338 arg, class_3218 arg2) {
		class_2680 lv = arg2.method_8320(arg);
		class_2248 lv2 = lv.method_11614();
		class_2248 lv3 = arg2.method_8320(arg.method_10074()).method_11614();
		return lv2 instanceof class_2302 && ((class_2302)lv2).method_9825(lv) && this.field_18860
			|| lv.method_11588() && lv3 instanceof class_2344 && this.field_18859;
	}

	protected void method_20392(class_3218 arg, class_1646 arg2, long l) {
		if (l > this.field_18861 && this.field_18858 != null) {
			arg2.method_18868().method_18878(class_4140.field_18446, new class_4099(this.field_18858));
			arg2.method_18868().method_18878(class_4140.field_18445, new class_4142(new class_4099(this.field_18858), 0.5F, 1));
		}
	}

	protected void method_19566(class_3218 arg, class_1646 arg2, long l) {
		arg2.method_18868().method_18875(class_4140.field_18446);
		arg2.method_18868().method_18875(class_4140.field_18445);
		this.field_19239 = 0;
		this.field_18861 = l + 40L;
	}

	protected void method_19565(class_3218 arg, class_1646 arg2, long l) {
		if (this.field_18858 != null && l > this.field_18861) {
			class_2680 lv = arg.method_8320(this.field_18858);
			class_2248 lv2 = lv.method_11614();
			class_2248 lv3 = arg.method_8320(this.field_18858.method_10074()).method_11614();
			if (lv2 instanceof class_2302 && ((class_2302)lv2).method_9825(lv) && this.field_18860) {
				arg.method_8651(this.field_18858, true);
			}

			if (lv.method_11588() && lv3 instanceof class_2344 && this.field_18859) {
				class_1277 lv4 = arg2.method_18011();

				for (int i = 0; i < lv4.method_5439(); i++) {
					class_1799 lv5 = lv4.method_5438(i);
					boolean bl = false;
					if (!lv5.method_7960()) {
						if (lv5.method_7909() == class_1802.field_8317) {
							arg.method_8652(this.field_18858, class_2246.field_10293.method_9564(), 3);
							bl = true;
						} else if (lv5.method_7909() == class_1802.field_8567) {
							arg.method_8652(this.field_18858, class_2246.field_10247.method_9564(), 3);
							bl = true;
						} else if (lv5.method_7909() == class_1802.field_8179) {
							arg.method_8652(this.field_18858, class_2246.field_10609.method_9564(), 3);
							bl = true;
						} else if (lv5.method_7909() == class_1802.field_8309) {
							arg.method_8652(this.field_18858, class_2246.field_10341.method_9564(), 3);
							bl = true;
						}
					}

					if (bl) {
						arg.method_8465(
							null,
							(double)this.field_18858.method_10263(),
							(double)this.field_18858.method_10264(),
							(double)this.field_18858.method_10260(),
							class_3417.field_17611,
							class_3419.field_15245,
							1.0F,
							1.0F
						);
						lv5.method_7934(1);
						if (lv5.method_7960()) {
							lv4.method_5447(i, class_1799.field_8037);
						}
						break;
					}
				}
			}

			if (lv2 instanceof class_2302 && !((class_2302)lv2).method_9825(lv)) {
				this.field_19351.remove(this.field_18858);
				this.field_18858 = this.method_20641(arg);
				if (this.field_18858 != null) {
					this.field_18861 = l + 20L;
					arg2.method_18868().method_18878(class_4140.field_18445, new class_4142(new class_4099(this.field_18858), 0.5F, 1));
					arg2.method_18868().method_18878(class_4140.field_18446, new class_4099(this.field_18858));
				}
			}
		}

		this.field_19239++;
	}

	protected boolean method_20394(class_3218 arg, class_1646 arg2, long l) {
		return this.field_19239 < 200;
	}
}
