package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_519 extends class_437 {
	private final class_437 field_3156;
	private class_522 field_3157;
	private class_523 field_3154;
	private boolean field_3155;

	public class_519(class_437 arg) {
		this.field_3156 = arg;
	}

	@Override
	protected void method_2224() {
		this.method_2219(new class_349(2, this.field_2561 / 2 - 154, this.field_2559 - 48, class_1074.method_4662("resourcePack.openFolder")) {
			@Override
			public void method_1826(double d, double e) {
				class_156.method_668().method_672(class_519.this.field_2563.method_1479());
			}
		});
		this.method_2219(new class_349(1, this.field_2561 / 2 + 4, this.field_2559 - 48, class_1074.method_4662("gui.done")) {
			@Override
			public void method_1826(double d, double e) {
				if (class_519.this.field_3155) {
					List<class_1075> list = Lists.<class_1075>newArrayList();

					for (class_520 lv : class_519.this.field_3154.method_1968()) {
						list.add(lv.method_2681());
					}

					Collections.reverse(list);
					class_519.this.field_2563.method_1520().method_14447(list);
					class_519.this.field_2563.field_1690.field_1887.clear();
					class_519.this.field_2563.field_1690.field_1846.clear();

					for (class_1075 lv2 : list) {
						if (!lv2.method_14465()) {
							class_519.this.field_2563.field_1690.field_1887.add(lv2.method_14463());
							if (!lv2.method_14460().method_14437()) {
								class_519.this.field_2563.field_1690.field_1846.add(lv2.method_14463());
							}
						}
					}

					class_519.this.field_2563.field_1690.method_1640();
					class_519.this.field_2563.method_1521();
				}

				class_519.this.field_2563.method_1507(class_519.this.field_3156);
			}
		});
		class_522 lv = this.field_3157;
		class_523 lv2 = this.field_3154;
		this.field_3157 = new class_522(this.field_2563, 200, this.field_2559);
		this.field_3157.method_1945(this.field_2561 / 2 - 4 - 200);
		if (lv != null) {
			this.field_3157.method_1968().addAll(lv.method_1968());
		}

		this.field_2557.add(this.field_3157);
		this.field_3154 = new class_523(this.field_2563, 200, this.field_2559);
		this.field_3154.method_1945(this.field_2561 / 2 + 4);
		if (lv2 != null) {
			this.field_3154.method_1968().addAll(lv2.method_1968());
		}

		this.field_2557.add(this.field_3154);
		if (!this.field_3155) {
			this.field_3157.method_1968().clear();
			this.field_3154.method_1968().clear();
			class_3283<class_1075> lv3 = this.field_2563.method_1520();
			lv3.method_14445();
			List<class_1075> list = Lists.<class_1075>newArrayList(lv3.method_14441());
			list.removeAll(lv3.method_14444());

			for (class_1075 lv4 : list) {
				this.field_3157.method_2690(new class_520(this, lv4));
			}

			for (class_1075 lv4 : Lists.reverse(Lists.newArrayList(lv3.method_14444()))) {
				this.field_3154.method_2690(new class_520(this, lv4));
			}
		}
	}

	@Override
	public void method_16014(double d, double e) {
		if (this.field_3157.method_1938(d, e)) {
			this.method_1967(this.field_3157);
		} else if (this.field_3154.method_1938(d, e)) {
			this.method_1967(this.field_3154);
		}
	}

	public void method_2674(class_520 arg) {
		this.field_3157.method_1968().remove(arg);
		arg.method_2686(this.field_3154);
		this.method_2660();
	}

	public void method_2663(class_520 arg) {
		this.field_3154.method_1968().remove(arg);
		this.field_3157.method_2690(arg);
		this.method_2660();
	}

	public boolean method_2669(class_520 arg) {
		return this.field_3154.method_1968().contains(arg);
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2220(0);
		this.field_3157.method_1930(i, j, f);
		this.field_3154.method_1930(i, j, f);
		this.method_1789(this.field_2554, class_1074.method_4662("resourcePack.title"), this.field_2561 / 2, 16, 16777215);
		this.method_1789(this.field_2554, class_1074.method_4662("resourcePack.folderInfo"), this.field_2561 / 2 - 77, this.field_2559 - 26, 8421504);
		super.method_2214(i, j, f);
	}

	public void method_2660() {
		this.field_3155 = true;
	}
}
