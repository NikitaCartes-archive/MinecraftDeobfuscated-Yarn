package net.minecraft;

import java.util.List;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = class_3856.class
	)})
public class class_1686 extends class_1682 implements class_3856 {
	private static final class_2940<class_1799> field_7652 = class_2945.method_12791(class_1686.class, class_2943.field_13322);
	private static final Logger field_7651 = LogManager.getLogger();
	public static final Predicate<class_1309> field_7653 = class_1686::method_7496;

	public class_1686(class_1937 arg) {
		super(class_1299.field_6045, arg);
	}

	public class_1686(class_1937 arg, class_1309 arg2) {
		super(class_1299.field_6045, arg2, arg);
	}

	public class_1686(class_1937 arg, double d, double e, double f) {
		super(class_1299.field_6045, d, e, f, arg);
	}

	@Override
	protected void method_5693() {
		this.method_5841().method_12784(field_7652, class_1799.field_8037);
	}

	@Override
	public class_1799 method_7495() {
		class_1799 lv = this.method_5841().method_12789(field_7652);
		if (lv.method_7909() != class_1802.field_8436 && lv.method_7909() != class_1802.field_8150) {
			if (this.field_6002 != null) {
				field_7651.error("ThrownPotion entity {} has no item?!", this.method_5628());
			}

			return new class_1799(class_1802.field_8436);
		} else {
			return lv;
		}
	}

	public void method_7494(class_1799 arg) {
		this.method_5841().method_12778(field_7652, arg);
	}

	@Override
	protected float method_7490() {
		return 0.05F;
	}

	@Override
	protected void method_7492(class_239 arg) {
		if (!this.field_6002.field_9236) {
			class_1799 lv = this.method_7495();
			class_1842 lv2 = class_1844.method_8063(lv);
			List<class_1293> list = class_1844.method_8067(lv);
			boolean bl = lv2 == class_1847.field_8991 && list.isEmpty();
			if (arg.field_1330 == class_239.class_240.field_1332 && bl) {
				class_2338 lv3 = arg.method_1015().method_10093(arg.field_1327);
				this.method_7499(lv3, arg.field_1327);

				for (class_2350 lv4 : class_2350.class_2353.field_11062) {
					this.method_7499(lv3.method_10093(lv4), lv4);
				}
			}

			if (bl) {
				this.method_7500();
			} else if (!list.isEmpty()) {
				if (this.method_7501()) {
					this.method_7497(lv, lv2);
				} else {
					this.method_7498(arg, list);
				}
			}

			int i = lv2.method_8050() ? 2007 : 2002;
			this.field_6002.method_8535(i, new class_2338(this), class_1844.method_8064(lv));
			this.method_5650();
		}
	}

	private void method_7500() {
		class_238 lv = this.method_5829().method_1009(4.0, 2.0, 4.0);
		List<class_1309> list = this.field_6002.method_8390(class_1309.class, lv, field_7653);
		if (!list.isEmpty()) {
			for (class_1309 lv2 : list) {
				double d = this.method_5858(lv2);
				if (d < 16.0 && method_7496(lv2)) {
					lv2.method_5643(class_1282.field_5859, 1.0F);
				}
			}
		}
	}

	private void method_7498(class_239 arg, List<class_1293> list) {
		class_238 lv = this.method_5829().method_1009(4.0, 2.0, 4.0);
		List<class_1309> list2 = this.field_6002.method_8403(class_1309.class, lv);
		if (!list2.isEmpty()) {
			for (class_1309 lv2 : list2) {
				if (lv2.method_6086()) {
					double d = this.method_5858(lv2);
					if (d < 16.0) {
						double e = 1.0 - Math.sqrt(d) / 4.0;
						if (lv2 == arg.field_1326) {
							e = 1.0;
						}

						for (class_1293 lv3 : list) {
							class_1291 lv4 = lv3.method_5579();
							if (lv4.method_5561()) {
								lv4.method_5564(this, this.method_7491(), lv2, lv3.method_5578(), e);
							} else {
								int i = (int)(e * (double)lv3.method_5584() + 0.5);
								if (i > 20) {
									lv2.method_6092(new class_1293(lv4, i, lv3.method_5578(), lv3.method_5591(), lv3.method_5581()));
								}
							}
						}
					}
				}
			}
		}
	}

	private void method_7497(class_1799 arg, class_1842 arg2) {
		class_1295 lv = new class_1295(this.field_6002, this.field_5987, this.field_6010, this.field_6035);
		lv.method_5607(this.method_7491());
		lv.method_5603(3.0F);
		lv.method_5609(-0.5F);
		lv.method_5595(10);
		lv.method_5596(-lv.method_5599() / (float)lv.method_5605());
		lv.method_5612(arg2);

		for (class_1293 lv2 : class_1844.method_8068(arg)) {
			lv.method_5610(new class_1293(lv2));
		}

		class_2487 lv3 = arg.method_7969();
		if (lv3 != null && lv3.method_10573("CustomPotionColor", 99)) {
			lv.method_5602(lv3.method_10550("CustomPotionColor"));
		}

		this.field_6002.method_8649(lv);
	}

	private boolean method_7501() {
		return this.method_7495().method_7909() == class_1802.field_8150;
	}

	private void method_7499(class_2338 arg, class_2350 arg2) {
		if (this.field_6002.method_8320(arg).method_11614() == class_2246.field_10036) {
			this.field_6002.method_8506(null, arg.method_10093(arg2), arg2.method_10153());
		}
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		class_1799 lv = class_1799.method_7915(arg.method_10562("Potion"));
		if (lv.method_7960()) {
			this.method_5650();
		} else {
			this.method_7494(lv);
		}
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		class_1799 lv = this.method_7495();
		if (!lv.method_7960()) {
			arg.method_10566("Potion", lv.method_7953(new class_2487()));
		}
	}

	private static boolean method_7496(class_1309 arg) {
		return arg instanceof class_1560 || arg instanceof class_1545;
	}
}
