package net.minecraft;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_500 extends class_437 {
	private static final Logger field_3044 = LogManager.getLogger();
	private final class_644 field_3037 = new class_644();
	private final class_437 field_3049;
	private class_503 field_3043;
	private class_641 field_3040;
	private class_339 field_3041;
	private class_339 field_3050;
	private class_339 field_3047;
	private boolean field_3039;
	private boolean field_3038;
	private boolean field_3036;
	private boolean field_3035;
	private String field_3042;
	private class_642 field_3051;
	private class_1134.class_1136 field_3046;
	private class_1134.class_1135 field_3045;
	private boolean field_3048;

	public class_500(class_437 arg) {
		this.field_3049 = arg;
	}

	@Override
	public class_364 getFocused() {
		return this.field_3043;
	}

	@Override
	protected void method_2224() {
		super.method_2224();
		this.field_2563.field_1774.method_1462(true);
		if (this.field_3048) {
			this.field_3043.method_1953(this.field_2561, this.field_2559, 32, this.field_2559 - 64);
		} else {
			this.field_3048 = true;
			this.field_3040 = new class_641(this.field_2563);
			this.field_3040.method_2981();
			this.field_3046 = new class_1134.class_1136();

			try {
				this.field_3045 = new class_1134.class_1135(this.field_3046);
				this.field_3045.start();
			} catch (Exception var2) {
				field_3044.warn("Unable to start LAN server detection: {}", var2.getMessage());
			}

			this.field_3043 = new class_503(this, this.field_2563, this.field_2561, this.field_2559, 32, this.field_2559 - 64, 36);
			this.field_3043.method_2564(this.field_3040);
		}

		this.method_2540();
	}

	public void method_2540() {
		this.field_3041 = this.method_2219(
			new class_339(7, this.field_2561 / 2 - 154, this.field_2559 - 28, 70, 20, class_1074.method_4662("selectServer.edit")) {
				@Override
				public void method_1826(double d, double e) {
					class_350.class_351<?> lv = class_500.this.field_3043.method_2563() < 0
						? null
						: (class_350.class_351)class_500.this.field_3043.method_1968().get(class_500.this.field_3043.method_2563());
					class_500.this.field_3036 = true;
					if (lv instanceof class_501) {
						class_642 lv2 = ((class_501)lv).method_2556();
						class_500.this.field_3051 = new class_642(lv2.field_3752, lv2.field_3761, false);
						class_500.this.field_3051.method_2996(lv2);
						class_500.this.field_2563.method_1507(new class_422(class_500.this, class_500.this.field_3051));
					}
				}
			}
		);
		this.field_3047 = this.method_2219(
			new class_339(2, this.field_2561 / 2 - 74, this.field_2559 - 28, 70, 20, class_1074.method_4662("selectServer.delete")) {
				@Override
				public void method_1826(double d, double e) {
					class_350.class_351<?> lv = class_500.this.field_3043.method_2563() < 0
						? null
						: (class_350.class_351)class_500.this.field_3043.method_1968().get(class_500.this.field_3043.method_2563());
					if (lv instanceof class_501) {
						String string = ((class_501)lv).method_2556().field_3752;
						if (string != null) {
							class_500.this.field_3039 = true;
							String string2 = class_1074.method_4662("selectServer.deleteQuestion");
							String string3 = class_1074.method_4662("selectServer.deleteWarning", string);
							String string4 = class_1074.method_4662("selectServer.deleteButton");
							String string5 = class_1074.method_4662("gui.cancel");
							class_410 lv2 = new class_410(class_500.this, string2, string3, string4, string5, class_500.this.field_3043.method_2563());
							class_500.this.field_2563.method_1507(lv2);
						}
					}
				}
			}
		);
		this.field_3050 = this.method_2219(
			new class_339(1, this.field_2561 / 2 - 154, this.field_2559 - 52, 100, 20, class_1074.method_4662("selectServer.select")) {
				@Override
				public void method_1826(double d, double e) {
					class_500.this.method_2536();
				}
			}
		);
		this.method_2219(new class_339(4, this.field_2561 / 2 - 50, this.field_2559 - 52, 100, 20, class_1074.method_4662("selectServer.direct")) {
			@Override
			public void method_1826(double d, double e) {
				class_500.this.field_3035 = true;
				class_500.this.field_3051 = new class_642(class_1074.method_4662("selectServer.defaultName"), "", false);
				class_500.this.field_2563.method_1507(new class_420(class_500.this, class_500.this.field_3051));
			}
		});
		this.method_2219(new class_339(3, this.field_2561 / 2 + 4 + 50, this.field_2559 - 52, 100, 20, class_1074.method_4662("selectServer.add")) {
			@Override
			public void method_1826(double d, double e) {
				class_500.this.field_3038 = true;
				class_500.this.field_3051 = new class_642(class_1074.method_4662("selectServer.defaultName"), "", false);
				class_500.this.field_2563.method_1507(new class_422(class_500.this, class_500.this.field_3051));
			}
		});
		this.method_2219(new class_339(8, this.field_2561 / 2 + 4, this.field_2559 - 28, 70, 20, class_1074.method_4662("selectServer.refresh")) {
			@Override
			public void method_1826(double d, double e) {
				class_500.this.method_2534();
			}
		});
		this.method_2219(new class_339(0, this.field_2561 / 2 + 4 + 76, this.field_2559 - 28, 75, 20, class_1074.method_4662("gui.cancel")) {
			@Override
			public void method_1826(double d, double e) {
				class_500.this.field_2563.method_1507(class_500.this.field_3049);
			}
		});
		this.field_2557.add(this.field_3043);
		this.method_2544(this.field_3043.method_2563());
	}

	@Override
	public void method_2225() {
		super.method_2225();
		if (this.field_3046.method_4823()) {
			List<class_1131> list = this.field_3046.method_4826();
			this.field_3046.method_4825();
			this.field_3043.method_2562(list);
		}

		this.field_3037.method_3000();
	}

	@Override
	public void method_2234() {
		this.field_2563.field_1774.method_1462(false);
		if (this.field_3045 != null) {
			this.field_3045.interrupt();
			this.field_3045 = null;
		}

		this.field_3037.method_3004();
	}

	private void method_2534() {
		this.field_2563.method_1507(new class_500(this.field_3049));
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		class_350.class_351<?> lv = this.field_3043.method_2563() < 0 ? null : (class_350.class_351)this.field_3043.method_1968().get(this.field_3043.method_2563());
		if (this.field_3039) {
			this.field_3039 = false;
			if (bl && lv instanceof class_501) {
				this.field_3040.method_2983(this.field_3043.method_2563());
				this.field_3040.method_2987();
				this.field_3043.method_2561(-1);
				this.field_3043.method_2564(this.field_3040);
			}

			this.field_2563.method_1507(this);
		} else if (this.field_3035) {
			this.field_3035 = false;
			if (bl) {
				this.method_2548(this.field_3051);
			} else {
				this.field_2563.method_1507(this);
			}
		} else if (this.field_3038) {
			this.field_3038 = false;
			if (bl) {
				this.field_3040.method_2988(this.field_3051);
				this.field_3040.method_2987();
				this.field_3043.method_2561(-1);
				this.field_3043.method_2564(this.field_3040);
			}

			this.field_2563.method_1507(this);
		} else if (this.field_3036) {
			this.field_3036 = false;
			if (bl && lv instanceof class_501) {
				class_642 lv2 = ((class_501)lv).method_2556();
				lv2.field_3752 = this.field_3051.field_3752;
				lv2.field_3761 = this.field_3051.field_3761;
				lv2.method_2996(this.field_3051);
				this.field_3040.method_2987();
				this.field_3043.method_2564(this.field_3040);
			}

			this.field_2563.method_1507(this);
		}
	}

	@Override
	public boolean method_16805(int i, int j, int k) {
		int l = this.field_3043.method_2563();
		class_350.class_351<?> lv = l < 0 ? null : (class_350.class_351)this.field_3043.method_1968().get(l);
		if (i == 294) {
			this.method_2534();
			return true;
		} else {
			if (l >= 0) {
				if (i == 265) {
					if (method_2223()) {
						if (l > 0 && lv instanceof class_501) {
							this.field_3040.method_2985(l, l - 1);
							this.method_2544(this.field_3043.method_2563() - 1);
							this.field_3043.method_1951(-this.field_3043.method_1941());
							this.field_3043.method_2564(this.field_3040);
						}
					} else if (l > 0) {
						this.method_2544(this.field_3043.method_2563() - 1);
						this.field_3043.method_1951(-this.field_3043.method_1941());
						if (this.field_3043.method_1968().get(this.field_3043.method_2563()) instanceof class_499) {
							if (this.field_3043.method_2563() > 0) {
								this.method_2544(this.field_3043.method_1968().size() - 1);
								this.field_3043.method_1951(-this.field_3043.method_1941());
							} else {
								this.method_2544(-1);
							}
						}
					} else {
						this.method_2544(-1);
					}

					return true;
				}

				if (i == 264) {
					if (method_2223()) {
						if (l < this.field_3040.method_2984() - 1) {
							this.field_3040.method_2985(l, l + 1);
							this.method_2544(l + 1);
							this.field_3043.method_1951(this.field_3043.method_1941());
							this.field_3043.method_2564(this.field_3040);
						}
					} else if (l < this.field_3043.method_1968().size()) {
						this.method_2544(this.field_3043.method_2563() + 1);
						this.field_3043.method_1951(this.field_3043.method_1941());
						if (this.field_3043.method_1968().get(this.field_3043.method_2563()) instanceof class_499) {
							if (this.field_3043.method_2563() < this.field_3043.method_1968().size() - 1) {
								this.method_2544(this.field_3043.method_1968().size() + 1);
								this.field_3043.method_1951(this.field_3043.method_1941());
							} else {
								this.method_2544(-1);
							}
						}
					} else {
						this.method_2544(-1);
					}

					return true;
				}

				if (i == 257 || i == 335) {
					this.method_2536();
					return true;
				}
			}

			return super.method_16805(i, j, k);
		}
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.field_3042 = null;
		this.method_2240();
		this.field_3043.method_1930(i, j, f);
		this.method_1789(this.field_2554, class_1074.method_4662("multiplayer.title"), this.field_2561 / 2, 20, 16777215);
		super.method_2214(i, j, f);
		if (this.field_3042 != null) {
			this.method_2211(Lists.<String>newArrayList(Splitter.on("\n").split(this.field_3042)), i, j);
		}
	}

	public void method_2536() {
		class_350.class_351<?> lv = this.field_3043.method_2563() < 0 ? null : (class_350.class_351)this.field_3043.method_1968().get(this.field_3043.method_2563());
		if (lv instanceof class_501) {
			this.method_2548(((class_501)lv).method_2556());
		} else if (lv instanceof class_502) {
			class_1131 lv2 = ((class_502)lv).method_2559();
			this.method_2548(new class_642(lv2.method_4813(), lv2.method_4812(), true));
		}
	}

	private void method_2548(class_642 arg) {
		this.field_2563.method_1507(new class_412(this, this.field_2563, arg));
	}

	public void method_2544(int i) {
		this.field_3043.method_2561(i);
		class_350.class_351<?> lv = i < 0 ? null : (class_350.class_351)this.field_3043.method_1968().get(i);
		this.field_3050.field_2078 = false;
		this.field_3041.field_2078 = false;
		this.field_3047.field_2078 = false;
		if (lv != null && !(lv instanceof class_499)) {
			this.field_3050.field_2078 = true;
			if (lv instanceof class_501) {
				this.field_3041.field_2078 = true;
				this.field_3047.field_2078 = true;
			}
		}
	}

	public class_644 method_2538() {
		return this.field_3037;
	}

	public void method_2528(String string) {
		this.field_3042 = string;
	}

	public class_641 method_2529() {
		return this.field_3040;
	}

	public boolean method_2533(class_501 arg, int i) {
		return i > 0;
	}

	public boolean method_2547(class_501 arg, int i) {
		return i < this.field_3040.method_2984() - 1;
	}

	public void method_2531(class_501 arg, int i, boolean bl) {
		int j = bl ? 0 : i - 1;
		this.field_3040.method_2985(i, j);
		if (this.field_3043.method_2563() == i) {
			this.method_2544(j);
		}

		this.field_3043.method_2564(this.field_3040);
	}

	public void method_2553(class_501 arg, int i, boolean bl) {
		int j = bl ? this.field_3040.method_2984() - 1 : i + 1;
		this.field_3040.method_2985(i, j);
		if (this.field_3043.method_2563() == i) {
			this.method_2544(j);
		}

		this.field_3043.method_2564(this.field_3040);
	}
}
