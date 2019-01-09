package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_438 extends class_437 {
	private final class_437 field_2570;
	private final class_315 field_2566;
	private final List<String> field_2569 = Lists.<String>newArrayList();
	private final List<String> field_2567 = Lists.<String>newArrayList();
	private String field_2571;
	private String[] field_2572;
	private class_438.class_439 field_2573;
	private class_339 field_2568;

	public class_438(class_437 arg, class_315 arg2) {
		this.field_2570 = arg;
		this.field_2566 = arg2;
	}

	@Override
	public class_364 getFocused() {
		return this.field_2573;
	}

	@Override
	protected void method_2224() {
		this.field_2571 = class_1074.method_4662("options.snooper.title");
		String string = class_1074.method_4662("options.snooper.desc");
		List<String> list = Lists.<String>newArrayList();

		for (String string2 : this.field_2554.method_1728(string, this.field_2561 - 30)) {
			list.add(string2);
		}

		this.field_2572 = (String[])list.toArray(new String[list.size()]);
		this.field_2569.clear();
		this.field_2567.clear();
		class_339 lv = new class_339(1, this.field_2561 / 2 - 152, this.field_2559 - 30, 150, 20, this.field_2566.method_1642(class_315.class_316.field_1926)) {
			@Override
			public void method_1826(double d, double e) {
				class_438.this.field_2566.method_1629(class_315.class_316.field_1926, 1);
				class_438.this.field_2568.field_2074 = class_438.this.field_2566.method_1642(class_315.class_316.field_1926);
			}
		};
		lv.field_2078 = false;
		this.field_2568 = this.method_2219(lv);
		this.method_2219(new class_339(2, this.field_2561 / 2 + 2, this.field_2559 - 30, 150, 20, class_1074.method_4662("gui.done")) {
			@Override
			public void method_1826(double d, double e) {
				class_438.this.field_2566.method_1640();
				class_438.this.field_2566.method_1640();
				class_438.this.field_2563.method_1507(class_438.this.field_2570);
			}
		});
		boolean bl = this.field_2563.method_1576() != null && this.field_2563.method_1576().method_3795() != null;

		for (Entry<String, String> entry : new TreeMap(this.field_2563.method_1552().method_5486()).entrySet()) {
			this.field_2569.add((bl ? "C " : "") + (String)entry.getKey());
			this.field_2567.add(this.field_2554.method_1714((String)entry.getValue(), this.field_2561 - 220));
		}

		if (bl) {
			for (Entry<String, String> entry : new TreeMap(this.field_2563.method_1576().method_3795().method_5486()).entrySet()) {
				this.field_2569.add("S " + (String)entry.getKey());
				this.field_2567.add(this.field_2554.method_1714((String)entry.getValue(), this.field_2561 - 220));
			}
		}

		this.field_2573 = new class_438.class_439();
		this.field_2557.add(this.field_2573);
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		this.field_2573.method_1930(i, j, f);
		this.method_1789(this.field_2554, this.field_2571, this.field_2561 / 2, 8, 16777215);
		int k = 22;

		for (String string : this.field_2572) {
			this.method_1789(this.field_2554, string, this.field_2561 / 2, k, 8421504);
			k += 9;
		}

		super.method_2214(i, j, f);
	}

	@Environment(EnvType.CLIENT)
	class class_439 extends class_358 {
		public class_439() {
			super(class_438.this.field_2563, class_438.this.field_2561, class_438.this.field_2559, 80, class_438.this.field_2559 - 40, 9 + 1);
		}

		@Override
		protected int method_1947() {
			return class_438.this.field_2569.size();
		}

		@Override
		protected boolean method_1955(int i) {
			return false;
		}

		@Override
		protected void method_1936() {
		}

		@Override
		protected void method_1935(int i, int j, int k, int l, int m, int n, float f) {
			class_438.this.field_2554.method_1729((String)class_438.this.field_2569.get(i), 10.0F, (float)k, 16777215);
			class_438.this.field_2554.method_1729((String)class_438.this.field_2567.get(i), 230.0F, (float)k, 16777215);
		}

		@Override
		protected int method_1948() {
			return this.field_2168 - 10;
		}
	}
}
