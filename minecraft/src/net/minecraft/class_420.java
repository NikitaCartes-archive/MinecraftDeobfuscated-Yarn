package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_420 extends class_437 {
	private class_339 field_2462;
	private final class_437 field_2461;
	private final class_642 field_2460;
	private class_342 field_2463;

	public class_420(class_437 arg, class_642 arg2) {
		this.field_2461 = arg;
		this.field_2460 = arg2;
	}

	@Override
	public void method_2225() {
		this.field_2463.method_1865();
	}

	@Override
	protected void method_2224() {
		this.field_2563.field_1774.method_1462(true);
		this.field_2462 = this.method_2219(
			new class_339(0, this.field_2561 / 2 - 100, this.field_2559 / 4 + 96 + 12, class_1074.method_4662("selectServer.select")) {
				@Override
				public void method_1826(double d, double e) {
					class_420.this.method_2167();
				}
			}
		);
		this.method_2219(new class_339(1, this.field_2561 / 2 - 100, this.field_2559 / 4 + 120 + 12, class_1074.method_4662("gui.cancel")) {
			@Override
			public void method_1826(double d, double e) {
				class_420.this.field_2461.confirmResult(false, 0);
			}
		});
		this.field_2463 = new class_342(2, this.field_2554, this.field_2561 / 2 - 100, 116, 200, 20);
		this.field_2463.method_1880(128);
		this.field_2463.method_1876(true);
		this.field_2463.method_1852(this.field_2563.field_1690.field_1864);
		this.field_2557.add(this.field_2463);
		this.method_1967(this.field_2463);
		this.method_2169();
	}

	@Override
	public void method_2228(class_310 arg, int i, int j) {
		String string = this.field_2463.method_1882();
		this.method_2233(arg, i, j);
		this.field_2463.method_1852(string);
	}

	private void method_2167() {
		this.field_2460.field_3761 = this.field_2463.method_1882();
		this.field_2461.confirmResult(true, 0);
	}

	@Override
	public void method_2234() {
		this.field_2563.field_1774.method_1462(false);
		this.field_2563.field_1690.field_1864 = this.field_2463.method_1882();
		this.field_2563.field_1690.method_1640();
	}

	@Override
	public boolean method_16806(char c, int i) {
		if (this.field_2463.method_16806(c, i)) {
			this.method_2169();
			return true;
		} else {
			return false;
		}
	}

	private void method_2169() {
		this.field_2462.field_2078 = !this.field_2463.method_1882().isEmpty() && this.field_2463.method_1882().split(":").length > 0;
	}

	@Override
	public boolean method_16805(int i, int j, int k) {
		if (i != 257 && i != 335) {
			if (super.method_16805(i, j, k)) {
				this.method_2169();
				return true;
			} else {
				return false;
			}
		} else {
			if (this.field_2462.field_2078) {
				this.method_2167();
			}

			return true;
		}
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		this.method_1789(this.field_2554, class_1074.method_4662("selectServer.direct"), this.field_2561 / 2, 20, 16777215);
		this.method_1780(this.field_2554, class_1074.method_4662("addServer.enterIp"), this.field_2561 / 2 - 100, 100, 10526880);
		this.field_2463.method_1857(i, j, f);
		super.method_2214(i, j, f);
	}
}
