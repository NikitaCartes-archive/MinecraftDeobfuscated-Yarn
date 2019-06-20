package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3971 extends class_1703 {
	static final ImmutableList<class_1792> field_17626 = ImmutableList.of(
		class_1802.field_8352,
		class_1802.field_8709,
		class_1802.field_8134,
		class_1802.field_8738,
		class_1802.field_8724,
		class_1802.field_8061,
		class_1802.field_8421,
		class_1802.field_8796,
		class_1802.field_8079,
		class_1802.field_8553,
		class_1802.field_8044,
		class_1802.field_8784,
		class_1802.field_8718,
		class_1802.field_8082,
		class_1802.field_8123,
		class_1802.field_8532,
		class_1802.field_8642,
		class_1802.field_8413,
		class_1802.field_8221,
		class_1802.field_8195,
		class_1802.field_8555,
		class_1802.field_8813,
		class_1802.field_8339,
		class_1802.field_8590,
		class_1802.field_8518,
		class_1802.field_8314,
		class_1802.field_8216,
		class_1802.field_19174,
		class_1802.field_19175
	);
	private final class_3914 field_17630;
	private final class_3915 field_17631 = class_3915.method_17403();
	private final class_1937 field_17632;
	private List<class_3975> field_17633 = Lists.<class_3975>newArrayList();
	private class_1799 field_17634 = class_1799.field_8037;
	private long field_17635;
	final class_1735 field_17627;
	final class_1735 field_17628;
	private Runnable field_17636 = () -> {
	};
	public final class_1263 field_17629 = new class_1277(1) {
		@Override
		public void method_5431() {
			super.method_5431();
			class_3971.this.method_7609(this);
			class_3971.this.field_17636.run();
		}
	};
	private final class_1731 field_19173 = new class_1731();

	public class_3971(int i, class_1661 arg) {
		this(i, arg, class_3914.field_17304);
	}

	public class_3971(int i, class_1661 arg, class_3914 arg2) {
		super(class_3917.field_17625, i);
		this.field_17630 = arg2;
		this.field_17632 = arg.field_7546.field_6002;
		this.field_17627 = this.method_7621(new class_1735(this.field_17629, 0, 20, 33));
		this.field_17628 = this.method_7621(new class_1735(this.field_19173, 1, 143, 33) {
			@Override
			public boolean method_7680(class_1799 arg) {
				return false;
			}

			@Override
			public class_1799 method_7667(class_1657 arg, class_1799 arg2) {
				class_1799 lv = class_3971.this.field_17627.method_7671(1);
				if (!lv.method_7960()) {
					class_3971.this.method_17866();
				}

				arg2.method_7909().method_7843(arg2, arg.field_6002, arg);
				arg2.method_17393((argx, arg2xxx) -> {
					long l = argx.method_8510();
					if (class_3971.this.field_17635 != l) {
						argx.method_8396(null, arg2xxx, class_3417.field_17710, class_3419.field_15245, 1.0F, 1.0F);
						class_3971.this.field_17635 = l;
					}
				});
				return super.method_7667(arg, arg2);
			}
		});

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 9; k++) {
				this.method_7621(new class_1735(arg, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
			}
		}

		for (int j = 0; j < 9; j++) {
			this.method_7621(new class_1735(arg, j, 8 + j * 18, 142));
		}

		this.method_17362(this.field_17631);
	}

	@Environment(EnvType.CLIENT)
	public int method_17862() {
		return this.field_17631.method_17407();
	}

	@Environment(EnvType.CLIENT)
	public List<class_3975> method_17863() {
		return this.field_17633;
	}

	@Environment(EnvType.CLIENT)
	public int method_17864() {
		return this.field_17633.size();
	}

	@Environment(EnvType.CLIENT)
	public boolean method_17865() {
		return this.field_17627.method_7681() && !this.field_17633.isEmpty();
	}

	@Override
	public boolean method_7597(class_1657 arg) {
		return method_17695(this.field_17630, arg, class_2246.field_16335);
	}

	@Override
	public boolean method_7604(class_1657 arg, int i) {
		if (i >= 0 && i < this.field_17633.size()) {
			this.field_17631.method_17404(i);
			this.method_17866();
		}

		return true;
	}

	@Override
	public void method_7609(class_1263 arg) {
		class_1799 lv = this.field_17627.method_7677();
		if (lv.method_7909() != this.field_17634.method_7909()) {
			this.field_17634 = lv.method_7972();
			this.method_17855(arg, lv);
		}
	}

	private void method_17855(class_1263 arg, class_1799 arg2) {
		this.field_17633.clear();
		this.field_17631.method_17404(-1);
		this.field_17628.method_7673(class_1799.field_8037);
		if (!arg2.method_7960()) {
			this.field_17633 = this.field_17632.method_8433().method_17877(class_3956.field_17641, arg, this.field_17632);
		}
	}

	private void method_17866() {
		if (!this.field_17633.isEmpty()) {
			class_3975 lv = (class_3975)this.field_17633.get(this.field_17631.method_17407());
			this.field_17628.method_7673(lv.method_8116(this.field_17629));
		} else {
			this.field_17628.method_7673(class_1799.field_8037);
		}

		this.method_7623();
	}

	@Override
	public class_3917<?> method_17358() {
		return class_3917.field_17625;
	}

	@Environment(EnvType.CLIENT)
	public void method_17859(Runnable runnable) {
		this.field_17636 = runnable;
	}

	@Override
	public boolean method_7613(class_1799 arg, class_1735 arg2) {
		return false;
	}

	@Override
	public class_1799 method_7601(class_1657 arg, int i) {
		class_1799 lv = class_1799.field_8037;
		class_1735 lv2 = (class_1735)this.field_7761.get(i);
		if (lv2 != null && lv2.method_7681()) {
			class_1799 lv3 = lv2.method_7677();
			class_1792 lv4 = lv3.method_7909();
			lv = lv3.method_7972();
			if (i == 1) {
				lv4.method_7843(lv3, arg.field_6002, arg);
				if (!this.method_7616(lv3, 2, 38, true)) {
					return class_1799.field_8037;
				}

				lv2.method_7670(lv3, lv);
			} else if (i == 0) {
				if (!this.method_7616(lv3, 2, 38, false)) {
					return class_1799.field_8037;
				}
			} else if (field_17626.contains(lv4)) {
				if (!this.method_7616(lv3, 0, 1, false)) {
					return class_1799.field_8037;
				}
			} else if (i >= 2 && i < 29) {
				if (!this.method_7616(lv3, 29, 38, false)) {
					return class_1799.field_8037;
				}
			} else if (i >= 29 && i < 38 && !this.method_7616(lv3, 2, 29, false)) {
				return class_1799.field_8037;
			}

			if (lv3.method_7960()) {
				lv2.method_7673(class_1799.field_8037);
			}

			lv2.method_7668();
			if (lv3.method_7947() == lv.method_7947()) {
				return class_1799.field_8037;
			}

			lv2.method_7667(arg, lv3);
			this.method_7623();
		}

		return lv;
	}

	@Override
	public void method_7595(class_1657 arg) {
		super.method_7595(arg);
		this.field_19173.method_5441(1);
		this.field_17630.method_17393((arg2, arg3) -> this.method_7607(arg, arg.field_6002, this.field_17629));
	}
}
