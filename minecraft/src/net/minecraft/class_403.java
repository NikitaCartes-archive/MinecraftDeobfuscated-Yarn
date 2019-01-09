package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_403 extends class_437 {
	private final Runnable field_2345;
	protected final class_2561 field_2350;
	protected final class_2561 field_2346;
	private final List<String> field_2348 = Lists.<String>newArrayList();
	protected final String field_2349;
	private int field_2347;

	public class_403(Runnable runnable, class_2561 arg, class_2561 arg2) {
		this(runnable, arg, arg2, "gui.back");
	}

	public class_403(Runnable runnable, class_2561 arg, class_2561 arg2, String string) {
		this.field_2345 = runnable;
		this.field_2350 = arg;
		this.field_2346 = arg2;
		this.field_2349 = class_1074.method_4662(string);
	}

	@Override
	protected void method_2224() {
		super.method_2224();
		this.method_2219(new class_339(0, this.field_2561 / 2 - 100, this.field_2559 / 6 + 168, this.field_2349) {
			@Override
			public void method_1826(double d, double e) {
				class_403.this.field_2345.run();
			}
		});
		this.field_2348.clear();
		this.field_2348.addAll(this.field_2554.method_1728(this.field_2346.method_10863(), this.field_2561 - 50));
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		this.method_1789(this.field_2554, this.field_2350.method_10863(), this.field_2561 / 2, 70, 16777215);
		int k = 90;

		for (String string : this.field_2348) {
			this.method_1789(this.field_2554, string, this.field_2561 / 2, k, 16777215);
			k += 9;
		}

		super.method_2214(i, j, f);
	}

	@Override
	public void method_2225() {
		super.method_2225();
		if (--this.field_2347 == 0) {
			for (class_339 lv : this.field_2564) {
				lv.field_2078 = true;
			}
		}
	}
}
