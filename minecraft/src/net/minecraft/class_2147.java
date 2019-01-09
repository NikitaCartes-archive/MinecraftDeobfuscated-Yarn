package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public final class class_2147 extends class_1959 {
	protected class_2147() {
		super(
			new class_1959.class_1960()
				.method_8737(class_3523.field_15681, class_3523.field_15677)
				.method_8735(class_1959.class_1963.field_9382)
				.method_8738(class_1959.class_1961.field_9364)
				.method_8740(-0.1F)
				.method_8743(0.3F)
				.method_8747(0.8F)
				.method_8727(0.9F)
				.method_8733(6388580)
				.method_8728(2302743)
				.method_8745("swamp")
		);
		this.method_8710(class_3031.field_13547, new class_3101(0.004, class_3098.class_3100.field_13692));
		class_3864.method_16983(this);
		class_3864.method_17001(this);
		class_3864.method_17002(this);
		class_3864.method_17004(this);
		class_3864.method_17005(this);
		class_3864.method_17006(this);
		class_3864.method_17011(this);
		class_3864.method_16972(this);
		class_3864.method_16982(this);
		class_3864.method_16988(this);
		class_3864.method_16996(this);
		class_3864.method_16990(this);
		class_3864.method_16999(this);
		this.method_8708(class_1311.field_6294, new class_1959.class_1964(class_1299.field_6115, 12, 4, 4));
		this.method_8708(class_1311.field_6294, new class_1959.class_1964(class_1299.field_6093, 10, 4, 4));
		this.method_8708(class_1311.field_6294, new class_1959.class_1964(class_1299.field_6132, 10, 4, 4));
		this.method_8708(class_1311.field_6294, new class_1959.class_1964(class_1299.field_6085, 8, 4, 4));
		this.method_8708(class_1311.field_6303, new class_1959.class_1964(class_1299.field_6108, 10, 8, 8));
		this.method_8708(class_1311.field_6302, new class_1959.class_1964(class_1299.field_6079, 100, 4, 4));
		this.method_8708(class_1311.field_6302, new class_1959.class_1964(class_1299.field_6051, 95, 4, 4));
		this.method_8708(class_1311.field_6302, new class_1959.class_1964(class_1299.field_6054, 5, 1, 1));
		this.method_8708(class_1311.field_6302, new class_1959.class_1964(class_1299.field_6137, 100, 4, 4));
		this.method_8708(class_1311.field_6302, new class_1959.class_1964(class_1299.field_6046, 100, 4, 4));
		this.method_8708(class_1311.field_6302, new class_1959.class_1964(class_1299.field_6069, 100, 4, 4));
		this.method_8708(class_1311.field_6302, new class_1959.class_1964(class_1299.field_6091, 10, 1, 4));
		this.method_8708(class_1311.field_6302, new class_1959.class_1964(class_1299.field_6145, 5, 1, 1));
		this.method_8708(class_1311.field_6302, new class_1959.class_1964(class_1299.field_6069, 1, 1, 1));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int method_8711(class_2338 arg) {
		double d = field_9324.method_15437((double)arg.method_10263() * 0.0225, (double)arg.method_10260() * 0.0225);
		return d < -0.1 ? 5011004 : 6975545;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int method_8698(class_2338 arg) {
		return 6975545;
	}
}
