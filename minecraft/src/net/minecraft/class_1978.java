package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public final class class_1978 extends class_1959 {
	public class_1978() {
		super(
			new class_1959.class_1960()
				.method_8737(class_3523.field_15701, class_3523.field_15677)
				.method_8735(class_1959.class_1963.field_9382)
				.method_8738(class_1959.class_1961.field_9370)
				.method_8740(0.1F)
				.method_8743(0.2F)
				.method_8747(0.7F)
				.method_8727(0.8F)
				.method_8733(4159204)
				.method_8728(329011)
				.method_8745(null)
		);
		this.method_8710(class_3031.field_13528, class_3037.field_13603);
		this.method_8710(class_3031.field_13547, new class_3101(0.004, class_3098.class_3100.field_13692));
		this.method_8710(class_3031.field_13565, class_3037.field_13603);
		class_3864.method_16983(this);
		class_3864.method_17001(this);
		class_3864.method_17002(this);
		class_3864.method_17004(this);
		this.method_8719(
			class_2893.class_2895.field_13178,
			method_8699(
				class_3031.field_13593,
				new class_3141(
					new class_3031[]{class_3031.field_13531, class_3031.field_13571, class_3031.field_13532, class_3031.field_13529},
					new class_3037[]{class_3037.field_13603, class_3037.field_13603, class_3037.field_13603, class_3037.field_13603},
					new float[]{0.025F, 0.05F, 0.6666667F, 0.1F},
					class_3031.field_13510,
					class_3037.field_13603
				),
				class_3284.field_14239,
				class_2998.field_13436
			)
		);
		class_3864.method_16970(this);
		class_3864.method_17005(this);
		class_3864.method_17006(this);
		class_3864.method_17010(this);
		class_3864.method_16977(this);
		class_3864.method_16971(this);
		class_3864.method_16982(this);
		class_3864.method_16984(this);
		class_3864.method_16996(this);
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
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int method_8711(class_2338 arg) {
		int i = super.method_8711(arg);
		return (i & 16711422) + 2634762 >> 1;
	}
}
