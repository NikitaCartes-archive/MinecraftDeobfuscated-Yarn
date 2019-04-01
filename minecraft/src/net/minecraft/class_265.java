package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.math.DoubleMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_265 {
	protected final class_251 field_1401;

	class_265(class_251 arg) {
		this.field_1401 = arg;
	}

	public double method_1091(class_2350.class_2351 arg) {
		int i = this.field_1401.method_1055(arg);
		return i >= this.field_1401.method_1051(arg) ? Double.POSITIVE_INFINITY : this.method_1099(arg, i);
	}

	public double method_1105(class_2350.class_2351 arg) {
		int i = this.field_1401.method_1045(arg);
		return i <= 0 ? Double.NEGATIVE_INFINITY : this.method_1099(arg, i);
	}

	public class_238 method_1107() {
		if (this.method_1110()) {
			throw new UnsupportedOperationException("No bounds for empty shape.");
		} else {
			return new class_238(
				this.method_1091(class_2350.class_2351.field_11048),
				this.method_1091(class_2350.class_2351.field_11052),
				this.method_1091(class_2350.class_2351.field_11051),
				this.method_1105(class_2350.class_2351.field_11048),
				this.method_1105(class_2350.class_2351.field_11052),
				this.method_1105(class_2350.class_2351.field_11051)
			);
		}
	}

	protected double method_1099(class_2350.class_2351 arg, int i) {
		return this.method_1109(arg).getDouble(i);
	}

	protected abstract DoubleList method_1109(class_2350.class_2351 arg);

	public boolean method_1110() {
		return this.field_1401.method_1056();
	}

	public class_265 method_1096(double d, double e, double f) {
		return (class_265)(this.method_1110()
			? class_259.method_1073()
			: new class_245(
				this.field_1401,
				new class_261(this.method_1109(class_2350.class_2351.field_11048), d),
				new class_261(this.method_1109(class_2350.class_2351.field_11052), e),
				new class_261(this.method_1109(class_2350.class_2351.field_11051), f)
			));
	}

	public class_265 method_1097() {
		class_265[] lvs = new class_265[]{class_259.method_1073()};
		this.method_1089((d, e, f, g, h, i) -> lvs[0] = class_259.method_1082(lvs[0], class_259.method_1081(d, e, f, g, h, i), class_247.field_1366));
		return lvs[0];
	}

	@Environment(EnvType.CLIENT)
	public void method_1104(class_259.class_260 arg) {
		this.field_1401
			.method_1064(
				(i, j, k, l, m, n) -> arg.consume(
						this.method_1099(class_2350.class_2351.field_11048, i),
						this.method_1099(class_2350.class_2351.field_11052, j),
						this.method_1099(class_2350.class_2351.field_11051, k),
						this.method_1099(class_2350.class_2351.field_11048, l),
						this.method_1099(class_2350.class_2351.field_11052, m),
						this.method_1099(class_2350.class_2351.field_11051, n)
					),
				true
			);
	}

	public void method_1089(class_259.class_260 arg) {
		this.field_1401
			.method_1053(
				(i, j, k, l, m, n) -> arg.consume(
						this.method_1099(class_2350.class_2351.field_11048, i),
						this.method_1099(class_2350.class_2351.field_11052, j),
						this.method_1099(class_2350.class_2351.field_11051, k),
						this.method_1099(class_2350.class_2351.field_11048, l),
						this.method_1099(class_2350.class_2351.field_11052, m),
						this.method_1099(class_2350.class_2351.field_11051, n)
					),
				true
			);
	}

	public List<class_238> method_1090() {
		List<class_238> list = Lists.<class_238>newArrayList();
		this.method_1089((d, e, f, g, h, i) -> list.add(new class_238(d, e, f, g, h, i)));
		return list;
	}

	@Environment(EnvType.CLIENT)
	public double method_1093(class_2350.class_2351 arg, double d, double e) {
		class_2350.class_2351 lv = class_2335.field_10963.method_10058(arg);
		class_2350.class_2351 lv2 = class_2335.field_10965.method_10058(arg);
		int i = this.method_1100(lv, d);
		int j = this.method_1100(lv2, e);
		int k = this.field_1401.method_1043(arg, i, j);
		return k >= this.field_1401.method_1051(arg) ? Double.POSITIVE_INFINITY : this.method_1099(arg, k);
	}

	@Environment(EnvType.CLIENT)
	public double method_1102(class_2350.class_2351 arg, double d, double e) {
		class_2350.class_2351 lv = class_2335.field_10963.method_10058(arg);
		class_2350.class_2351 lv2 = class_2335.field_10965.method_10058(arg);
		int i = this.method_1100(lv, d);
		int j = this.method_1100(lv2, e);
		int k = this.field_1401.method_1058(arg, i, j);
		return k <= 0 ? Double.NEGATIVE_INFINITY : this.method_1099(arg, k);
	}

	protected int method_1100(class_2350.class_2351 arg, double d) {
		return class_3532.method_15360(0, this.field_1401.method_1051(arg) + 1, i -> {
			if (i < 0) {
				return false;
			} else {
				return i > this.field_1401.method_1051(arg) ? true : d < this.method_1099(arg, i);
			}
		}) - 1;
	}

	protected boolean method_1095(double d, double e, double f) {
		return this.field_1401
			.method_1044(
				this.method_1100(class_2350.class_2351.field_11048, d),
				this.method_1100(class_2350.class_2351.field_11052, e),
				this.method_1100(class_2350.class_2351.field_11051, f)
			);
	}

	@Nullable
	public class_3965 method_1092(class_243 arg, class_243 arg2, class_2338 arg3) {
		if (this.method_1110()) {
			return null;
		} else {
			class_243 lv = arg2.method_1020(arg);
			if (lv.method_1027() < 1.0E-7) {
				return null;
			} else {
				class_243 lv2 = arg.method_1019(lv.method_1021(0.001));
				return this.method_1095(
						lv2.field_1352 - (double)arg3.method_10263(), lv2.field_1351 - (double)arg3.method_10264(), lv2.field_1350 - (double)arg3.method_10260()
					)
					? new class_3965(lv2, class_2350.method_10142(lv.field_1352, lv.field_1351, lv.field_1350).method_10153(), arg3, true)
					: class_238.method_1010(this.method_1090(), arg, arg2, arg3);
			}
		}
	}

	public class_265 method_1098(class_2350 arg) {
		if (!this.method_1110() && this != class_259.method_1077()) {
			class_2350.class_2351 lv = arg.method_10166();
			class_2350.class_2352 lv2 = arg.method_10171();
			DoubleList doubleList = this.method_1109(lv);
			if (doubleList.size() == 2 && DoubleMath.fuzzyEquals(doubleList.getDouble(0), 0.0, 1.0E-7) && DoubleMath.fuzzyEquals(doubleList.getDouble(1), 1.0, 1.0E-7)) {
				return this;
			} else {
				int i = this.method_1100(lv, lv2 == class_2350.class_2352.field_11056 ? 0.9999999 : 1.0E-7);
				return new class_263(this, lv, i);
			}
		} else {
			return this;
		}
	}

	public double method_1108(class_2350.class_2351 arg, class_238 arg2, double d) {
		return this.method_1103(class_2335.method_10057(arg, class_2350.class_2351.field_11048), arg2, d);
	}

	protected double method_1103(class_2335 arg, class_238 arg2, double d) {
		if (this.method_1110()) {
			return d;
		} else if (Math.abs(d) < 1.0E-7) {
			return 0.0;
		} else {
			class_2335 lv = arg.method_10055();
			class_2350.class_2351 lv2 = lv.method_10058(class_2350.class_2351.field_11048);
			class_2350.class_2351 lv3 = lv.method_10058(class_2350.class_2351.field_11052);
			class_2350.class_2351 lv4 = lv.method_10058(class_2350.class_2351.field_11051);
			double e = arg2.method_990(lv2);
			double f = arg2.method_1001(lv2);
			int i = this.method_1100(lv2, f + 1.0E-7);
			int j = this.method_1100(lv2, e - 1.0E-7);
			int k = Math.max(0, this.method_1100(lv3, arg2.method_1001(lv3) + 1.0E-7));
			int l = Math.min(this.field_1401.method_1051(lv3), this.method_1100(lv3, arg2.method_990(lv3) - 1.0E-7) + 1);
			int m = Math.max(0, this.method_1100(lv4, arg2.method_1001(lv4) + 1.0E-7));
			int n = Math.min(this.field_1401.method_1051(lv4), this.method_1100(lv4, arg2.method_990(lv4) - 1.0E-7) + 1);
			int o = this.field_1401.method_1051(lv2);
			if (d > 0.0) {
				for (int p = j + 1; p < o; p++) {
					for (int q = k; q < l; q++) {
						for (int r = m; r < n; r++) {
							if (this.field_1401.method_1062(lv, p, q, r)) {
								double g = this.method_1099(lv2, p) - e;
								if (g >= -1.0E-7) {
									d = Math.min(d, g);
								}

								return d;
							}
						}
					}
				}
			} else if (d < 0.0) {
				for (int p = i - 1; p >= 0; p--) {
					for (int q = k; q < l; q++) {
						for (int rx = m; rx < n; rx++) {
							if (this.field_1401.method_1062(lv, p, q, rx)) {
								double g = this.method_1099(lv2, p + 1) - f;
								if (g <= 1.0E-7) {
									d = Math.max(d, g);
								}

								return d;
							}
						}
					}
				}
			}

			return d;
		}
	}

	public String toString() {
		return this.method_1110() ? "EMPTY" : "VoxelShape[" + this.method_1107() + "]";
	}
}
