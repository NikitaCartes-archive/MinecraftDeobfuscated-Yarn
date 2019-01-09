package net.minecraft;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_527 extends class_437 {
	private static final Object2IntMap<class_2874> field_3232 = class_156.method_654(
		new Object2IntOpenCustomHashMap<>(class_156.method_655()), object2IntOpenCustomHashMap -> {
			object2IntOpenCustomHashMap.put(class_2874.field_13072, -13408734);
			object2IntOpenCustomHashMap.put(class_2874.field_13076, -10075085);
			object2IntOpenCustomHashMap.put(class_2874.field_13078, -8943531);
			object2IntOpenCustomHashMap.defaultReturnValue(-2236963);
		}
	);
	private final class_411 field_3233;
	private final class_1257 field_3234;

	public class_527(class_411 arg, String string, class_32 arg2) {
		this.field_3233 = arg;
		this.field_3234 = new class_1257(string, arg2, arg2.method_238(string));
	}

	@Override
	protected void method_2224() {
		super.method_2224();
		this.method_2219(new class_339(0, this.field_2561 / 2 - 100, this.field_2559 / 4 + 150, class_1074.method_4662("gui.cancel")) {
			@Override
			public void method_1826(double d, double e) {
				class_527.this.field_3234.method_5402();
				class_527.this.field_3233.confirmResult(false, 0);
			}
		});
	}

	@Override
	public void method_2225() {
		if (this.field_3234.method_5403()) {
			this.field_3233.confirmResult(true, 0);
		}
	}

	@Override
	public void method_2234() {
		this.field_3234.method_5402();
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		this.method_1789(this.field_2554, class_1074.method_4662("optimizeWorld.title", this.field_3234.method_5395()), this.field_2561 / 2, 20, 16777215);
		int k = this.field_2561 / 2 - 150;
		int l = this.field_2561 / 2 + 150;
		int m = this.field_2559 / 4 + 100;
		int n = m + 10;
		this.method_1789(this.field_2554, this.field_3234.method_5394().method_10863(), this.field_2561 / 2, m - 9 - 2, 10526880);
		if (this.field_3234.method_5397() > 0) {
			method_1785(k - 1, m - 1, l + 1, n + 1, -16777216);
			this.method_1780(this.field_2554, class_1074.method_4662("optimizeWorld.info.converted", this.field_3234.method_5400()), k, 40, 10526880);
			this.method_1780(this.field_2554, class_1074.method_4662("optimizeWorld.info.skipped", this.field_3234.method_5399()), k, 40 + 9 + 3, 10526880);
			this.method_1780(this.field_2554, class_1074.method_4662("optimizeWorld.info.total", this.field_3234.method_5397()), k, 40 + (9 + 3) * 2, 10526880);
			int o = 0;

			for (class_2874 lv : class_2874.method_12482()) {
				int p = class_3532.method_15375(this.field_3234.method_5393(lv) * (float)(l - k));
				method_1785(k + o, m, k + o + p, n, field_3232.getInt(lv));
				o += p;
			}

			int q = this.field_3234.method_5400() + this.field_3234.method_5399();
			this.method_1789(this.field_2554, q + " / " + this.field_3234.method_5397(), this.field_2561 / 2, m + 2 * 9 + 2, 10526880);
			this.method_1789(
				this.field_2554, class_3532.method_15375(this.field_3234.method_5401() * 100.0F) + "%", this.field_2561 / 2, m + (n - m) / 2 - 9 / 2, 10526880
			);
		}

		super.method_2214(i, j, f);
	}
}
