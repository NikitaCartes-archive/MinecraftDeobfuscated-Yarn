package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_446 extends class_437 {
	private final class_437 field_2637;
	protected String field_2636 = "Video Settings";
	private final class_315 field_2638;
	private class_353 field_2639;
	private static final class_315.class_316[] field_2640 = new class_315.class_316[]{
		class_315.class_316.field_1938,
		class_315.class_316.field_1933,
		class_315.class_316.field_1924,
		class_315.class_316.field_1935,
		class_315.class_316.field_1927,
		class_315.class_316.field_1934,
		class_315.class_316.field_1922,
		class_315.class_316.field_1959,
		class_315.class_316.field_1945,
		class_315.class_316.field_1937,
		class_315.class_316.field_1932,
		class_315.class_316.field_1919,
		class_315.class_316.field_1943,
		class_315.class_316.field_1954,
		class_315.class_316.field_1958
	};

	public class_446(class_437 arg, class_315 arg2) {
		this.field_2637 = arg;
		this.field_2638 = arg2;
	}

	@Nullable
	@Override
	public class_364 getFocused() {
		return this.field_2639;
	}

	@Override
	protected void method_2224() {
		this.field_2636 = class_1074.method_4662("options.videoTitle");
		this.method_2219(new class_339(200, this.field_2561 / 2 - 100, this.field_2559 - 27, class_1074.method_4662("gui.done")) {
			@Override
			public void method_1826(double d, double e) {
				class_446.this.field_2563.field_1690.method_1640();
				class_446.this.field_2563.field_1704.method_4475();
				class_446.this.field_2563.method_1507(class_446.this.field_2637);
			}
		});
		this.field_2639 = new class_353(this.field_2563, this.field_2561, this.field_2559, 32, this.field_2559 - 32, 25, field_2640);
		this.field_2557.add(this.field_2639);
	}

	@Override
	public void method_2210() {
		this.field_2563.field_1690.method_1640();
		super.method_2210();
	}

	@Override
	public boolean method_16807(double d, double e, int i) {
		int j = this.field_2638.field_1868;
		if (super.method_16807(d, e, i)) {
			if (this.field_2638.field_1868 != j) {
				this.field_2563.method_15993();
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean method_16804(double d, double e, int i) {
		int j = this.field_2638.field_1868;
		if (super.method_16804(d, e, i)) {
			return true;
		} else if (this.field_2639.method_16804(d, e, i)) {
			if (this.field_2638.field_1868 != j) {
				this.field_2563.method_15993();
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		this.field_2639.method_1930(i, j, f);
		this.method_1789(this.field_2554, this.field_2636, this.field_2561 / 2, 5, 16777215);
		super.method_2214(i, j, f);
	}
}
