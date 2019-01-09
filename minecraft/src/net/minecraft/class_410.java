package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_410 extends class_437 {
	protected final class_411 field_2403;
	protected final String field_2405;
	private final String field_2401;
	private final List<String> field_2404 = Lists.<String>newArrayList();
	protected String field_2402;
	protected String field_2399;
	protected final int field_2398;
	private int field_2400;

	public class_410(class_411 arg, String string, String string2, int i) {
		this.field_2403 = arg;
		this.field_2405 = string;
		this.field_2401 = string2;
		this.field_2398 = i;
		this.field_2402 = class_1074.method_4662("gui.yes");
		this.field_2399 = class_1074.method_4662("gui.no");
	}

	public class_410(class_411 arg, String string, String string2, String string3, String string4, int i) {
		this.field_2403 = arg;
		this.field_2405 = string;
		this.field_2401 = string2;
		this.field_2402 = string3;
		this.field_2399 = string4;
		this.field_2398 = i;
	}

	@Override
	protected void method_2224() {
		super.method_2224();
		this.method_2219(new class_349(0, this.field_2561 / 2 - 155, this.field_2559 / 6 + 96, this.field_2402) {
			@Override
			public void method_1826(double d, double e) {
				class_410.this.field_2403.confirmResult(true, class_410.this.field_2398);
			}
		});
		this.method_2219(new class_349(1, this.field_2561 / 2 - 155 + 160, this.field_2559 / 6 + 96, this.field_2399) {
			@Override
			public void method_1826(double d, double e) {
				class_410.this.field_2403.confirmResult(false, class_410.this.field_2398);
			}
		});
		this.field_2404.clear();
		this.field_2404.addAll(this.field_2554.method_1728(this.field_2401, this.field_2561 - 50));
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		this.method_1789(this.field_2554, this.field_2405, this.field_2561 / 2, 70, 16777215);
		int k = 90;

		for (String string : this.field_2404) {
			this.method_1789(this.field_2554, string, this.field_2561 / 2, k, 16777215);
			k += 9;
		}

		super.method_2214(i, j, f);
	}

	public void method_2125(int i) {
		this.field_2400 = i;

		for (class_339 lv : this.field_2564) {
			lv.field_2078 = false;
		}
	}

	@Override
	public void method_2225() {
		super.method_2225();
		if (--this.field_2400 == 0) {
			for (class_339 lv : this.field_2564) {
				lv.field_2078 = true;
			}
		}
	}

	@Override
	public boolean method_16890() {
		return false;
	}

	@Override
	public boolean method_16805(int i, int j, int k) {
		if (i == 256) {
			this.field_2403.confirmResult(false, this.field_2398);
			return true;
		} else {
			return super.method_16805(i, j, k);
		}
	}
}
