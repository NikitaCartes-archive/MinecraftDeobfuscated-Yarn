package net.minecraft;

import java.net.IDN;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_422 extends class_437 {
	private class_339 field_2472;
	private final class_437 field_2470;
	private final class_642 field_2469;
	private class_342 field_2474;
	private class_342 field_2471;
	private class_339 field_2473;
	private final Predicate<String> field_2475 = string -> {
		if (class_3544.method_15438(string)) {
			return true;
		} else {
			String[] strings = string.split(":");
			if (strings.length == 0) {
				return true;
			} else {
				try {
					String string2 = IDN.toASCII(strings[0]);
					return true;
				} catch (IllegalArgumentException var3) {
					return false;
				}
			}
		}
	};

	public class_422(class_437 arg, class_642 arg2) {
		this.field_2470 = arg;
		this.field_2469 = arg2;
	}

	@Override
	public void method_2225() {
		this.field_2471.method_1865();
		this.field_2474.method_1865();
	}

	@Override
	public class_364 getFocused() {
		return this.field_2474.method_1871() ? this.field_2474 : this.field_2471;
	}

	@Override
	protected void method_2224() {
		this.field_2563.field_1774.method_1462(true);
		this.field_2472 = this.method_2219(new class_339(0, this.field_2561 / 2 - 100, this.field_2559 / 4 + 96 + 18, class_1074.method_4662("addServer.add")) {
			@Override
			public void method_1826(double d, double e) {
				class_422.this.method_2172();
			}
		});
		this.method_2219(new class_339(1, this.field_2561 / 2 - 100, this.field_2559 / 4 + 120 + 18, class_1074.method_4662("gui.cancel")) {
			@Override
			public void method_1826(double d, double e) {
				class_422.this.field_2470.confirmResult(false, 0);
			}
		});
		this.field_2473 = this.method_2219(
			new class_339(
				2,
				this.field_2561 / 2 - 100,
				this.field_2559 / 4 + 72,
				class_1074.method_4662("addServer.resourcePack") + ": " + this.field_2469.method_2990().method_2997().method_10863()
			) {
				@Override
				public void method_1826(double d, double e) {
					class_422.this.field_2469
						.method_2995(class_642.class_643.values()[(class_422.this.field_2469.method_2990().ordinal() + 1) % class_642.class_643.values().length]);
					class_422.this.field_2473.field_2074 = class_1074.method_4662("addServer.resourcePack")
						+ ": "
						+ class_422.this.field_2469.method_2990().method_2997().method_10863();
				}
			}
		);
		this.field_2474 = new class_342(1, this.field_2554, this.field_2561 / 2 - 100, 106, 200, 20) {
			@Override
			public void method_1876(boolean bl) {
				super.method_1876(bl);
				if (bl) {
					class_422.this.field_2471.method_1876(false);
				}
			}
		};
		this.field_2474.method_1880(128);
		this.field_2474.method_1852(this.field_2469.field_3761);
		this.field_2474.method_1890(this.field_2475);
		this.field_2474.method_1863(this::method_2171);
		this.field_2557.add(this.field_2474);
		this.field_2471 = new class_342(0, this.field_2554, this.field_2561 / 2 - 100, 66, 200, 20) {
			@Override
			public void method_1876(boolean bl) {
				super.method_1876(bl);
				if (bl) {
					class_422.this.field_2474.method_1876(false);
				}
			}
		};
		this.field_2471.method_1876(true);
		this.field_2471.method_1852(this.field_2469.field_3752);
		this.field_2471.method_1863(this::method_2171);
		this.field_2557.add(this.field_2471);
		this.method_2210();
	}

	@Override
	public void method_2228(class_310 arg, int i, int j) {
		String string = this.field_2474.method_1882();
		String string2 = this.field_2471.method_1882();
		this.method_2233(arg, i, j);
		this.field_2474.method_1852(string);
		this.field_2471.method_1852(string2);
	}

	private void method_2171(int i, String string) {
		this.method_2210();
	}

	@Override
	public void method_2234() {
		this.field_2563.field_1774.method_1462(false);
	}

	private void method_2172() {
		this.field_2469.field_3752 = this.field_2471.method_1882();
		this.field_2469.field_3761 = this.field_2474.method_1882();
		this.field_2470.confirmResult(true, 0);
	}

	@Override
	public void method_2210() {
		this.field_2472.field_2078 = !this.field_2474.method_1882().isEmpty()
			&& this.field_2474.method_1882().split(":").length > 0
			&& !this.field_2471.method_1882().isEmpty();
	}

	@Override
	public boolean method_16805(int i, int j, int k) {
		if (i == 258) {
			if (this.field_2471.method_1871()) {
				this.field_2474.method_1876(true);
			} else {
				this.field_2471.method_1876(true);
			}

			return true;
		} else if ((i == 257 || i == 335) && this.field_2472.field_2078) {
			this.method_2172();
			return true;
		} else {
			return super.method_16805(i, j, k);
		}
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		this.method_1789(this.field_2554, class_1074.method_4662("addServer.title"), this.field_2561 / 2, 17, 16777215);
		this.method_1780(this.field_2554, class_1074.method_4662("addServer.enterName"), this.field_2561 / 2 - 100, 53, 10526880);
		this.method_1780(this.field_2554, class_1074.method_4662("addServer.enterIp"), this.field_2561 / 2 - 100, 94, 10526880);
		this.field_2471.method_1857(i, j, f);
		this.field_2474.method_1857(i, j, f);
		super.method_2214(i, j, f);
	}
}
