package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3742 extends class_437 {
	private final class_3751 field_16522;
	private class_342 field_16520;
	private class_342 field_16518;
	private class_342 field_16519;
	private final List<class_342> field_16521 = Lists.<class_342>newArrayList();

	public class_3742(class_3751 arg) {
		this.field_16522 = arg;
	}

	@Override
	public void method_2225() {
		this.field_16520.method_1865();
		this.field_16518.method_1865();
		this.field_16519.method_1865();
	}

	private void method_16346() {
		this.method_16348();
		this.field_2563.method_1507(null);
	}

	private void method_16349() {
		this.field_2563.method_1507(null);
	}

	private void method_16348() {
		this.field_2563
			.method_1562()
			.method_2883(
				new class_3753(
					this.field_16522.method_11016(),
					new class_2960(this.field_16520.method_1882()),
					new class_2960(this.field_16518.method_1882()),
					this.field_16519.method_1882()
				)
			);
	}

	@Override
	public void method_2210() {
		this.method_16349();
	}

	@Override
	protected void method_2224() {
		this.field_2563.field_1774.method_1462(true);
		this.method_2219(new class_339(0, this.field_2561 / 2 - 4 - 150, 210, 150, 20, class_1074.method_4662("gui.done")) {
			@Override
			public void method_1826(double d, double e) {
				class_3742.this.method_16346();
			}
		});
		this.method_2219(new class_339(1, this.field_2561 / 2 + 4, 210, 150, 20, class_1074.method_4662("gui.cancel")) {
			@Override
			public void method_1826(double d, double e) {
				class_3742.this.method_16349();
			}
		});
		this.field_16521.clear();
		this.field_16518 = new class_342(2, this.field_2554, this.field_2561 / 2 - 152, 40, 300, 20);
		this.field_16518.method_1880(128);
		this.field_16518.method_1852(this.field_16522.method_16382().toString());
		this.field_16521.add(this.field_16518);
		this.field_16520 = new class_342(2, this.field_2554, this.field_2561 / 2 - 152, 80, 300, 20);
		this.field_16520.method_1880(128);
		this.field_16520.method_1852(this.field_16522.method_16381().toString());
		this.field_16521.add(this.field_16520);
		this.field_16519 = new class_342(2, this.field_2554, this.field_2561 / 2 - 152, 120, 300, 20);
		this.field_16519.method_1880(256);
		this.field_16519.method_1852(this.field_16522.method_16380());
		this.field_16521.add(this.field_16519);
		this.field_2557.addAll(this.field_16521);
		this.field_16520.method_1876(true);
		this.method_1967(this.field_16520);
	}

	@Override
	public void method_2228(class_310 arg, int i, int j) {
		String string = this.field_16520.method_1882();
		String string2 = this.field_16518.method_1882();
		String string3 = this.field_16519.method_1882();
		this.method_2233(arg, i, j);
		this.field_16520.method_1852(string);
		this.field_16518.method_1852(string2);
		this.field_16519.method_1852(string3);
	}

	@Override
	public void method_2234() {
		this.field_2563.field_1774.method_1462(false);
	}

	@Override
	public boolean method_16807(double d, double e, int i) {
		if (super.method_16807(d, e, i)) {
			for (class_342 lv : this.field_16521) {
				lv.method_1876(this.getFocused() == lv);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean method_16805(int i, int j, int k) {
		if (i != 258) {
			if (i != 257 && i != 335) {
				return super.method_16805(i, j, k);
			} else {
				this.method_16346();
				return true;
			}
		} else {
			class_342 lv = null;
			class_342 lv2 = null;

			for (class_342 lv3 : this.field_16521) {
				if (lv != null && lv3.method_1885()) {
					lv2 = lv3;
					break;
				}

				if (lv3.method_1871() && lv3.method_1885()) {
					lv = lv3;
				}
			}

			if (lv != null && lv2 == null) {
				for (class_342 lv3 : this.field_16521) {
					if (lv3.method_1885() && lv3 != lv) {
						lv2 = lv3;
						break;
					}
				}
			}

			if (lv2 != null && lv2 != lv) {
				lv.method_1876(false);
				lv2.method_1876(true);
				this.method_1967(lv2);
			}

			return true;
		}
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		this.method_1780(this.field_2554, class_1074.method_4662("jigsaw_block.target_pool"), this.field_2561 / 2 - 153, 30, 10526880);
		this.field_16518.method_1857(i, j, f);
		this.method_1780(this.field_2554, class_1074.method_4662("jigsaw_block.attachement_type"), this.field_2561 / 2 - 153, 70, 10526880);
		this.field_16520.method_1857(i, j, f);
		this.method_1780(this.field_2554, class_1074.method_4662("jigsaw_block.final_state"), this.field_2561 / 2 - 153, 110, 10526880);
		this.field_16519.method_1857(i, j, f);
		super.method_2214(i, j, f);
	}
}
