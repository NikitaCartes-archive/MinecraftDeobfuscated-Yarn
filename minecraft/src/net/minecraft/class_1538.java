package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1538 extends class_1297 {
	private int field_7185;
	public long field_7186;
	private int field_7183;
	private final boolean field_7184;
	@Nullable
	private class_3222 field_7182;

	public class_1538(class_1937 arg, double d, double e, double f, boolean bl) {
		super(class_1299.field_6112, arg);
		this.field_5985 = true;
		this.method_5808(d, e, f, 0.0F, 0.0F);
		this.field_7185 = 2;
		this.field_7186 = this.field_5974.nextLong();
		this.field_7183 = this.field_5974.nextInt(3) + 1;
		this.field_7184 = bl;
		class_1267 lv = arg.method_8407();
		if (lv == class_1267.field_5802 || lv == class_1267.field_5807) {
			this.method_6960(4);
		}
	}

	@Override
	public class_3419 method_5634() {
		return class_3419.field_15252;
	}

	public void method_6961(@Nullable class_3222 arg) {
		this.field_7182 = arg;
	}

	@Override
	public void method_5773() {
		super.method_5773();
		if (this.field_7185 == 2) {
			this.field_6002
				.method_8465(
					null,
					this.field_5987,
					this.field_6010,
					this.field_6035,
					class_3417.field_14865,
					class_3419.field_15252,
					10000.0F,
					0.8F + this.field_5974.nextFloat() * 0.2F
				);
			this.field_6002
				.method_8465(
					null, this.field_5987, this.field_6010, this.field_6035, class_3417.field_14956, class_3419.field_15252, 2.0F, 0.5F + this.field_5974.nextFloat() * 0.2F
				);
		}

		this.field_7185--;
		if (this.field_7185 < 0) {
			if (this.field_7183 == 0) {
				this.method_5650();
			} else if (this.field_7185 < -this.field_5974.nextInt(10)) {
				this.field_7183--;
				this.field_7185 = 1;
				this.field_7186 = this.field_5974.nextLong();
				this.method_6960(0);
			}
		}

		if (this.field_7185 >= 0) {
			if (this.field_6002.field_9236) {
				this.field_6002.method_8509(2);
			} else if (!this.field_7184) {
				double d = 3.0;
				List<class_1297> list = this.field_6002
					.method_8333(
						this,
						new class_238(
							this.field_5987 - 3.0, this.field_6010 - 3.0, this.field_6035 - 3.0, this.field_5987 + 3.0, this.field_6010 + 6.0 + 3.0, this.field_6035 + 3.0
						),
						class_1297::method_5805
					);

				for (class_1297 lv : list) {
					lv.method_5800(this);
				}

				if (this.field_7182 != null) {
					class_174.field_1202.method_8803(this.field_7182, list);
				}
			}
		}
	}

	private void method_6960(int i) {
		if (!this.field_7184 && !this.field_6002.field_9236 && this.field_6002.method_8450().method_8355("doFireTick")) {
			class_2680 lv = class_2246.field_10036.method_9564();
			class_2338 lv2 = new class_2338(this);
			if (this.field_6002.method_8320(lv2).method_11588() && lv.method_11591(this.field_6002, lv2)) {
				this.field_6002.method_8501(lv2, lv);
			}

			for (int j = 0; j < i; j++) {
				class_2338 lv3 = lv2.method_10069(this.field_5974.nextInt(3) - 1, this.field_5974.nextInt(3) - 1, this.field_5974.nextInt(3) - 1);
				if (this.field_6002.method_8320(lv3).method_11588() && lv.method_11591(this.field_6002, lv3)) {
					this.field_6002.method_8501(lv3, lv);
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_5640(double d) {
		double e = 64.0 * method_5824();
		return d < e * e;
	}

	@Override
	protected void method_5693() {
	}

	@Override
	protected void method_5749(class_2487 arg) {
	}

	@Override
	protected void method_5652(class_2487 arg) {
	}

	@Override
	public class_2596<?> method_18002() {
		return new class_2607(this);
	}
}
