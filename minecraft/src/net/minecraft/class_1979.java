package net.minecraft;

import java.util.Random;

public class class_1979 extends class_1959 {
	protected static final class_3543 field_9484 = new class_3543(new Random(3456L), 3);

	public class_1979() {
		super(
			new class_1959.class_1960()
				.method_8737(class_3523.field_15699, class_3523.field_15677)
				.method_8735(class_1959.class_1963.field_9382)
				.method_8738(class_1959.class_1961.field_9367)
				.method_8740(-1.8F)
				.method_8743(0.1F)
				.method_8747(0.5F)
				.method_8727(0.5F)
				.method_8733(3750089)
				.method_8728(329011)
				.method_8745(null)
		);
		this.method_8710(class_3031.field_13536, new class_3114(class_3411.class_3413.field_14528, 0.3F, 0.9F));
		this.method_8710(class_3031.field_13588, class_3037.field_13603);
		this.method_8710(class_3031.field_13547, new class_3101(0.004, class_3098.class_3100.field_13692));
		this.method_8710(class_3031.field_13589, new class_3172(false));
		class_3864.method_17000(this);
		class_3864.method_17001(this);
		class_3864.method_17002(this);
		class_3864.method_16997(this);
		class_3864.method_17004(this);
		class_3864.method_16998(this);
		class_3864.method_17005(this);
		class_3864.method_17006(this);
		class_3864.method_17010(this);
		class_3864.method_17019(this);
		class_3864.method_16977(this);
		class_3864.method_16979(this);
		class_3864.method_16982(this);
		class_3864.method_16984(this);
		class_3864.method_16996(this);
		class_3864.method_16999(this);
		this.method_8708(class_1311.field_6300, new class_1959.class_1964(class_1299.field_6114, 1, 1, 4));
		this.method_8708(class_1311.field_6300, new class_1959.class_1964(class_1299.field_6073, 15, 1, 5));
		this.method_8708(class_1311.field_6294, new class_1959.class_1964(class_1299.field_6042, 1, 1, 2));
		this.method_8708(class_1311.field_6303, new class_1959.class_1964(class_1299.field_6108, 10, 8, 8));
		this.method_8708(class_1311.field_6302, new class_1959.class_1964(class_1299.field_6079, 100, 4, 4));
		this.method_8708(class_1311.field_6302, new class_1959.class_1964(class_1299.field_6051, 95, 4, 4));
		this.method_8708(class_1311.field_6302, new class_1959.class_1964(class_1299.field_6123, 5, 1, 1));
		this.method_8708(class_1311.field_6302, new class_1959.class_1964(class_1299.field_6054, 5, 1, 1));
		this.method_8708(class_1311.field_6302, new class_1959.class_1964(class_1299.field_6137, 100, 4, 4));
		this.method_8708(class_1311.field_6302, new class_1959.class_1964(class_1299.field_6046, 100, 4, 4));
		this.method_8708(class_1311.field_6302, new class_1959.class_1964(class_1299.field_6069, 100, 4, 4));
		this.method_8708(class_1311.field_6302, new class_1959.class_1964(class_1299.field_6091, 10, 1, 4));
		this.method_8708(class_1311.field_6302, new class_1959.class_1964(class_1299.field_6145, 5, 1, 1));
	}

	@Override
	public float method_8707(class_2338 arg) {
		float f = this.method_8712();
		double d = field_9484.method_15437((double)arg.method_10263() * 0.05, (double)arg.method_10260() * 0.05);
		double e = field_9324.method_15437((double)arg.method_10263() * 0.2, (double)arg.method_10260() * 0.2);
		double g = d + e;
		if (g < 0.3) {
			double h = field_9324.method_15437((double)arg.method_10263() * 0.09, (double)arg.method_10260() * 0.09);
			if (h < 0.8) {
				f = 0.2F;
			}
		}

		if (arg.method_10264() > 64) {
			float i = (float)(field_9335.method_15437((double)((float)arg.method_10263() / 8.0F), (double)((float)arg.method_10260() / 8.0F)) * 4.0);
			return f - (i + (float)arg.method_10264() - 64.0F) * 0.05F / 30.0F;
		} else {
			return f;
		}
	}
}
