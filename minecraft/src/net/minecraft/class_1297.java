package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_1297 implements class_1275, class_2165 {
	protected static final Logger field_5955 = LogManager.getLogger();
	private static final AtomicInteger field_5978 = new AtomicInteger();
	private static final List<class_1799> field_6030 = Collections.emptyList();
	private static final class_238 field_6025 = new class_238(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
	private static double field_5999 = 1.0;
	private final class_1299<?> field_5961;
	private int field_5986 = field_5978.incrementAndGet();
	public boolean field_6033;
	private final List<class_1297> field_5979 = Lists.<class_1297>newArrayList();
	protected int field_5951;
	private class_1297 field_6034;
	public boolean field_5983;
	public class_1937 field_6002;
	public double field_6014;
	public double field_6036;
	public double field_5969;
	public double field_5987;
	public double field_6010;
	public double field_6035;
	private class_243 field_18276 = class_243.field_1353;
	public float field_6031;
	public float field_5965;
	public float field_5982;
	public float field_6004;
	private class_238 field_6005 = field_6025;
	public boolean field_5952;
	public boolean field_5976;
	public boolean field_5992;
	public boolean field_6015;
	public boolean field_6037;
	protected class_243 field_17046 = class_243.field_1353;
	public boolean field_5988;
	public float field_6039;
	public float field_5973;
	public float field_5994;
	public float field_6017;
	private float field_6003 = 1.0F;
	private float field_6022 = 1.0F;
	public double field_6038;
	public double field_5971;
	public double field_5989;
	public float field_6013;
	public boolean field_5960;
	public float field_5968;
	protected final Random field_5974 = new Random();
	public int field_6012;
	private int field_5956 = -this.method_5676();
	protected boolean field_5957;
	protected double field_5964;
	protected boolean field_6000;
	protected boolean field_19271;
	public int field_6008;
	protected boolean field_5953 = true;
	protected final class_2945 field_6011;
	protected static final class_2940<Byte> field_5990 = class_2945.method_12791(class_1297.class, class_2943.field_13319);
	private static final class_2940<Integer> field_6032 = class_2945.method_12791(class_1297.class, class_2943.field_13327);
	private static final class_2940<Optional<class_2561>> field_6027 = class_2945.method_12791(class_1297.class, class_2943.field_13325);
	private static final class_2940<Boolean> field_5975 = class_2945.method_12791(class_1297.class, class_2943.field_13323);
	private static final class_2940<Boolean> field_5962 = class_2945.method_12791(class_1297.class, class_2943.field_13323);
	private static final class_2940<Boolean> field_5995 = class_2945.method_12791(class_1297.class, class_2943.field_13323);
	protected static final class_2940<class_4050> field_18064 = class_2945.method_12791(class_1297.class, class_2943.field_18238);
	public boolean field_6016;
	public int field_6024;
	public int field_5959;
	public int field_5980;
	@Environment(EnvType.CLIENT)
	public long field_6001;
	@Environment(EnvType.CLIENT)
	public long field_6023;
	@Environment(EnvType.CLIENT)
	public long field_5954;
	public boolean field_5985;
	public boolean field_6007;
	public int field_6018;
	protected boolean field_5963;
	protected int field_5972;
	public class_2874 field_6026;
	protected class_2338 field_5991;
	protected class_243 field_6020;
	protected class_2350 field_6028;
	private boolean field_6009;
	protected UUID field_6021 = class_3532.method_15378(this.field_5974);
	protected String field_5981 = this.field_6021.toString();
	protected boolean field_5958;
	private final Set<String> field_6029 = Sets.<String>newHashSet();
	private boolean field_5966;
	private final double[] field_5993 = new double[]{0.0, 0.0, 0.0};
	private long field_5996;
	private class_4048 field_18065;
	private float field_18066;

	public class_1297(class_1299<?> arg, class_1937 arg2) {
		this.field_5961 = arg;
		this.field_6002 = arg2;
		this.field_18065 = arg.method_18386();
		this.method_5814(0.0, 0.0, 0.0);
		if (arg2 != null) {
			this.field_6026 = arg2.field_9247.method_12460();
		}

		this.field_6011 = new class_2945(this);
		this.field_6011.method_12784(field_5990, (byte)0);
		this.field_6011.method_12784(field_6032, this.method_5748());
		this.field_6011.method_12784(field_5975, false);
		this.field_6011.method_12784(field_6027, Optional.empty());
		this.field_6011.method_12784(field_5962, false);
		this.field_6011.method_12784(field_5995, false);
		this.field_6011.method_12784(field_18064, class_4050.field_18076);
		this.method_5693();
		this.field_18066 = this.method_18378(class_4050.field_18076, this.field_18065);
	}

	public boolean method_7325() {
		return false;
	}

	public final void method_18375() {
		if (this.method_5782()) {
			this.method_5772();
		}

		if (this.method_5765()) {
			this.method_5848();
		}
	}

	@Environment(EnvType.CLIENT)
	public void method_18003(double d, double e, double f) {
		this.field_6001 = class_2684.method_18047(d);
		this.field_6023 = class_2684.method_18047(e);
		this.field_5954 = class_2684.method_18047(f);
	}

	public class_1299<?> method_5864() {
		return this.field_5961;
	}

	public int method_5628() {
		return this.field_5986;
	}

	public void method_5838(int i) {
		this.field_5986 = i;
	}

	public Set<String> method_5752() {
		return this.field_6029;
	}

	public boolean method_5780(String string) {
		return this.field_6029.size() >= 1024 ? false : this.field_6029.add(string);
	}

	public boolean method_5738(String string) {
		return this.field_6029.remove(string);
	}

	public void method_5768() {
		this.method_5650();
	}

	protected abstract void method_5693();

	public class_2945 method_5841() {
		return this.field_6011;
	}

	public boolean equals(Object object) {
		return object instanceof class_1297 ? ((class_1297)object).field_5986 == this.field_5986 : false;
	}

	public int hashCode() {
		return this.field_5986;
	}

	@Environment(EnvType.CLIENT)
	protected void method_5823() {
		if (this.field_6002 != null) {
			while (this.field_6010 > 0.0 && this.field_6010 < 256.0) {
				this.method_5814(this.field_5987, this.field_6010, this.field_6035);
				if (this.field_6002.method_17892(this)) {
					break;
				}

				this.field_6010++;
			}

			this.method_18799(class_243.field_1353);
			this.field_5965 = 0.0F;
		}
	}

	public void method_5650() {
		this.field_5988 = true;
	}

	protected void method_18380(class_4050 arg) {
		this.field_6011.method_12778(field_18064, arg);
	}

	public class_4050 method_18376() {
		return this.field_6011.method_12789(field_18064);
	}

	protected void method_5710(float f, float g) {
		this.field_6031 = f % 360.0F;
		this.field_5965 = g % 360.0F;
	}

	public void method_5814(double d, double e, double f) {
		this.field_5987 = d;
		this.field_6010 = e;
		this.field_6035 = f;
		float g = this.field_18065.field_18067 / 2.0F;
		float h = this.field_18065.field_18068;
		this.method_5857(new class_238(d - (double)g, e, f - (double)g, d + (double)g, e + (double)h, f + (double)g));
	}

	@Environment(EnvType.CLIENT)
	public void method_5872(double d, double e) {
		double f = e * 0.15;
		double g = d * 0.15;
		this.field_5965 = (float)((double)this.field_5965 + f);
		this.field_6031 = (float)((double)this.field_6031 + g);
		this.field_5965 = class_3532.method_15363(this.field_5965, -90.0F, 90.0F);
		this.field_6004 = (float)((double)this.field_6004 + f);
		this.field_5982 = (float)((double)this.field_5982 + g);
		this.field_6004 = class_3532.method_15363(this.field_6004, -90.0F, 90.0F);
		if (this.field_6034 != null) {
			this.field_6034.method_5644(this);
		}
	}

	public void method_5773() {
		if (!this.field_6002.field_9236) {
			this.method_5729(6, this.method_5851());
		}

		this.method_5670();
	}

	public void method_5670() {
		this.field_6002.method_16107().method_15396("entityBaseTick");
		if (this.method_5765() && this.method_5854().field_5988) {
			this.method_5848();
		}

		if (this.field_5951 > 0) {
			this.field_5951--;
		}

		this.field_6039 = this.field_5973;
		this.field_6014 = this.field_5987;
		this.field_6036 = this.field_6010;
		this.field_5969 = this.field_6035;
		this.field_6004 = this.field_5965;
		this.field_5982 = this.field_6031;
		this.method_18379();
		this.method_5666();
		this.method_5876();
		if (this.field_6002.field_9236) {
			this.method_5646();
		} else if (this.field_5956 > 0) {
			if (this.method_5753()) {
				this.field_5956 -= 4;
				if (this.field_5956 < 0) {
					this.method_5646();
				}
			} else {
				if (this.field_5956 % 20 == 0) {
					this.method_5643(class_1282.field_5854, 1.0F);
				}

				this.field_5956--;
			}
		}

		if (this.method_5771()) {
			this.method_5730();
			this.field_6017 *= 0.5F;
		}

		if (this.field_6010 < -64.0) {
			this.method_5825();
		}

		if (!this.field_6002.field_9236) {
			this.method_5729(0, this.field_5956 > 0);
		}

		this.field_5953 = false;
		this.field_6002.method_16107().method_15407();
	}

	protected void method_5760() {
		if (this.field_6018 > 0) {
			this.field_6018--;
		}
	}

	public int method_5741() {
		return 1;
	}

	protected void method_5730() {
		if (!this.method_5753()) {
			this.method_5639(15);
			this.method_5643(class_1282.field_5863, 4.0F);
		}
	}

	public void method_5639(int i) {
		int j = i * 20;
		if (this instanceof class_1309) {
			j = class_1900.method_8238((class_1309)this, j);
		}

		if (this.field_5956 < j) {
			this.field_5956 = j;
		}
	}

	public void method_20803(int i) {
		this.field_5956 = i;
	}

	public int method_20802() {
		return this.field_5956;
	}

	public void method_5646() {
		this.field_5956 = 0;
	}

	protected void method_5825() {
		this.method_5650();
	}

	public boolean method_5654(double d, double e, double f) {
		return this.method_5629(this.method_5829().method_989(d, e, f));
	}

	private boolean method_5629(class_238 arg) {
		return this.field_6002.method_8587(this, arg) && !this.field_6002.method_8599(arg);
	}

	public void method_5784(class_1313 arg, class_243 arg2) {
		if (this.field_5960) {
			this.method_5857(this.method_5829().method_997(arg2));
			this.method_5792();
		} else {
			if (arg == class_1313.field_6310) {
				arg2 = this.method_18794(arg2);
				if (arg2.equals(class_243.field_1353)) {
					return;
				}
			}

			this.field_6002.method_16107().method_15396("move");
			if (this.field_17046.method_1027() > 1.0E-7) {
				arg2 = arg2.method_18806(this.field_17046);
				this.field_17046 = class_243.field_1353;
				this.method_18799(class_243.field_1353);
			}

			arg2 = this.method_18796(arg2, arg);
			class_243 lv = this.method_17835(arg2);
			if (lv.method_1027() > 1.0E-7) {
				this.method_5857(this.method_5829().method_997(lv));
				this.method_5792();
			}

			this.field_6002.method_16107().method_15407();
			this.field_6002.method_16107().method_15396("rest");
			this.field_5976 = !class_3532.method_20390(arg2.field_1352, lv.field_1352) || !class_3532.method_20390(arg2.field_1350, lv.field_1350);
			this.field_5992 = arg2.field_1351 != lv.field_1351;
			this.field_5952 = this.field_5992 && arg2.field_1351 < 0.0;
			this.field_6015 = this.field_5976 || this.field_5992;
			int i = class_3532.method_15357(this.field_5987);
			int j = class_3532.method_15357(this.field_6010 - 0.2F);
			int k = class_3532.method_15357(this.field_6035);
			class_2338 lv2 = new class_2338(i, j, k);
			class_2680 lv3 = this.field_6002.method_8320(lv2);
			if (lv3.method_11588()) {
				class_2338 lv4 = lv2.method_10074();
				class_2680 lv5 = this.field_6002.method_8320(lv4);
				class_2248 lv6 = lv5.method_11614();
				if (lv6.method_9525(class_3481.field_16584) || lv6.method_9525(class_3481.field_15504) || lv6 instanceof class_2349) {
					lv3 = lv5;
					lv2 = lv4;
				}
			}

			this.method_5623(lv.field_1351, this.field_5952, lv3, lv2);
			class_243 lv7 = this.method_18798();
			if (arg2.field_1352 != lv.field_1352) {
				this.method_18800(0.0, lv7.field_1351, lv7.field_1350);
			}

			if (arg2.field_1350 != lv.field_1350) {
				this.method_18800(lv7.field_1352, lv7.field_1351, 0.0);
			}

			class_2248 lv8 = lv3.method_11614();
			if (arg2.field_1351 != lv.field_1351) {
				lv8.method_9502(this.field_6002, this);
			}

			if (this.method_5658() && (!this.field_5952 || !this.method_5715() || !(this instanceof class_1657)) && !this.method_5765()) {
				double d = lv.field_1352;
				double e = lv.field_1351;
				double f = lv.field_1350;
				if (lv8 != class_2246.field_9983 && lv8 != class_2246.field_16492) {
					e = 0.0;
				}

				if (this.field_5952) {
					lv8.method_9591(this.field_6002, lv2, this);
				}

				this.field_5973 = (float)((double)this.field_5973 + (double)class_3532.method_15368(method_17996(lv)) * 0.6);
				this.field_5994 = (float)((double)this.field_5994 + (double)class_3532.method_15368(d * d + e * e + f * f) * 0.6);
				if (this.field_5994 > this.field_6003 && !lv3.method_11588()) {
					this.field_6003 = this.method_5867();
					if (this.method_5799()) {
						class_1297 lv9 = this.method_5782() && this.method_5642() != null ? this.method_5642() : this;
						float g = lv9 == this ? 0.35F : 0.4F;
						class_243 lv10 = lv9.method_18798();
						float h = class_3532.method_15368(lv10.field_1352 * lv10.field_1352 * 0.2F + lv10.field_1351 * lv10.field_1351 + lv10.field_1350 * lv10.field_1350 * 0.2F)
							* g;
						if (h > 1.0F) {
							h = 1.0F;
						}

						this.method_5734(h);
					} else {
						this.method_5712(lv2, lv3);
					}
				} else if (this.field_5994 > this.field_6022 && this.method_5776() && lv3.method_11588()) {
					this.field_6022 = this.method_5801(this.field_5994);
				}
			}

			try {
				this.field_19271 = false;
				this.method_5852();
			} catch (Throwable var21) {
				class_128 lv11 = class_128.method_560(var21, "Checking entity block collision");
				class_129 lv12 = lv11.method_562("Entity being checked for collision");
				this.method_5819(lv12);
				throw new class_148(lv11);
			}

			boolean bl = this.method_5637();
			if (this.field_6002.method_8425(this.method_5829().method_1011(0.001))) {
				if (!bl) {
					this.field_5956++;
					if (this.field_5956 == 0) {
						this.method_5639(8);
					}
				}

				this.method_5714(1);
			} else if (this.field_5956 <= 0) {
				this.field_5956 = -this.method_5676();
			}

			if (bl && this.method_5809()) {
				this.method_5783(class_3417.field_15222, 0.7F, 1.6F + (this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.4F);
				this.field_5956 = -this.method_5676();
			}

			this.field_6002.method_16107().method_15407();
		}
	}

	protected class_243 method_18796(class_243 arg, class_1313 arg2) {
		if (this instanceof class_1657 && (arg2 == class_1313.field_6308 || arg2 == class_1313.field_6305) && this.field_5952 && this.method_5715()) {
			double d = arg.field_1352;
			double e = arg.field_1350;
			double f = 0.05;

			while (d != 0.0 && this.field_6002.method_8587(this, this.method_5829().method_989(d, (double)(-this.field_6013), 0.0))) {
				if (d < 0.05 && d >= -0.05) {
					d = 0.0;
				} else if (d > 0.0) {
					d -= 0.05;
				} else {
					d += 0.05;
				}
			}

			while (e != 0.0 && this.field_6002.method_8587(this, this.method_5829().method_989(0.0, (double)(-this.field_6013), e))) {
				if (e < 0.05 && e >= -0.05) {
					e = 0.0;
				} else if (e > 0.0) {
					e -= 0.05;
				} else {
					e += 0.05;
				}
			}

			while (d != 0.0 && e != 0.0 && this.field_6002.method_8587(this, this.method_5829().method_989(d, (double)(-this.field_6013), e))) {
				if (d < 0.05 && d >= -0.05) {
					d = 0.0;
				} else if (d > 0.0) {
					d -= 0.05;
				} else {
					d += 0.05;
				}

				if (e < 0.05 && e >= -0.05) {
					e = 0.0;
				} else if (e > 0.0) {
					e -= 0.05;
				} else {
					e += 0.05;
				}
			}

			arg = new class_243(d, arg.field_1351, e);
		}

		return arg;
	}

	protected class_243 method_18794(class_243 arg) {
		if (arg.method_1027() <= 1.0E-7) {
			return arg;
		} else {
			long l = this.field_6002.method_8510();
			if (l != this.field_5996) {
				Arrays.fill(this.field_5993, 0.0);
				this.field_5996 = l;
			}

			if (arg.field_1352 != 0.0) {
				double d = this.method_18797(class_2350.class_2351.field_11048, arg.field_1352);
				return Math.abs(d) <= 1.0E-5F ? class_243.field_1353 : new class_243(d, 0.0, 0.0);
			} else if (arg.field_1351 != 0.0) {
				double d = this.method_18797(class_2350.class_2351.field_11052, arg.field_1351);
				return Math.abs(d) <= 1.0E-5F ? class_243.field_1353 : new class_243(0.0, d, 0.0);
			} else if (arg.field_1350 != 0.0) {
				double d = this.method_18797(class_2350.class_2351.field_11051, arg.field_1350);
				return Math.abs(d) <= 1.0E-5F ? class_243.field_1353 : new class_243(0.0, 0.0, d);
			} else {
				return class_243.field_1353;
			}
		}
	}

	private double method_18797(class_2350.class_2351 arg, double d) {
		int i = arg.ordinal();
		double e = class_3532.method_15350(d + this.field_5993[i], -0.51, 0.51);
		d = e - this.field_5993[i];
		this.field_5993[i] = e;
		return d;
	}

	private class_243 method_17835(class_243 arg) {
		class_238 lv = this.method_5829();
		class_3726 lv2 = class_3726.method_16195(this);
		class_265 lv3 = this.field_6002.method_8621().method_17903();
		Stream<class_265> stream = class_259.method_1074(lv3, class_259.method_1078(lv.method_1011(1.0E-7)), class_247.field_16896) ? Stream.empty() : Stream.of(lv3);
		Stream<class_265> stream2 = this.field_6002.method_20743(this, lv.method_18804(arg), ImmutableSet.of());
		class_3538<class_265> lv4 = new class_3538<>(Stream.concat(stream2, stream));
		class_243 lv5 = arg.method_1027() == 0.0 ? arg : method_20736(this, arg, lv, this.field_6002, lv2, lv4);
		boolean bl = arg.field_1352 != lv5.field_1352;
		boolean bl2 = arg.field_1351 != lv5.field_1351;
		boolean bl3 = arg.field_1350 != lv5.field_1350;
		boolean bl4 = this.field_5952 || bl2 && arg.field_1351 < 0.0;
		if (this.field_6013 > 0.0F && bl4 && (bl || bl3)) {
			class_243 lv6 = method_20736(this, new class_243(arg.field_1352, (double)this.field_6013, arg.field_1350), lv, this.field_6002, lv2, lv4);
			class_243 lv7 = method_20736(
				this, new class_243(0.0, (double)this.field_6013, 0.0), lv.method_1012(arg.field_1352, 0.0, arg.field_1350), this.field_6002, lv2, lv4
			);
			if (lv7.field_1351 < (double)this.field_6013) {
				class_243 lv8 = method_20736(this, new class_243(arg.field_1352, 0.0, arg.field_1350), lv.method_997(lv7), this.field_6002, lv2, lv4).method_1019(lv7);
				if (method_17996(lv8) > method_17996(lv6)) {
					lv6 = lv8;
				}
			}

			if (method_17996(lv6) > method_17996(lv5)) {
				return lv6.method_1019(method_20736(this, new class_243(0.0, -lv6.field_1351 + arg.field_1351, 0.0), lv.method_997(lv6), this.field_6002, lv2, lv4));
			}
		}

		return lv5;
	}

	public static double method_17996(class_243 arg) {
		return arg.field_1352 * arg.field_1352 + arg.field_1350 * arg.field_1350;
	}

	public static class_243 method_20736(@Nullable class_1297 arg, class_243 arg2, class_238 arg3, class_1937 arg4, class_3726 arg5, class_3538<class_265> arg6) {
		boolean bl = arg2.field_1352 == 0.0;
		boolean bl2 = arg2.field_1351 == 0.0;
		boolean bl3 = arg2.field_1350 == 0.0;
		if ((!bl || !bl2) && (!bl || !bl3) && (!bl2 || !bl3)) {
			class_3538<class_265> lv = new class_3538<>(Stream.concat(arg6.method_15418(), arg4.method_20812(arg, arg3.method_18804(arg2))));
			return method_20737(arg2, arg3, lv);
		} else {
			return method_17833(arg2, arg3, arg4, arg5, arg6);
		}
	}

	public static class_243 method_20737(class_243 arg, class_238 arg2, class_3538<class_265> arg3) {
		double d = arg.field_1352;
		double e = arg.field_1351;
		double f = arg.field_1350;
		if (e != 0.0) {
			e = class_259.method_1085(class_2350.class_2351.field_11052, arg2, arg3.method_15418(), e);
			if (e != 0.0) {
				arg2 = arg2.method_989(0.0, e, 0.0);
			}
		}

		boolean bl = Math.abs(d) < Math.abs(f);
		if (bl && f != 0.0) {
			f = class_259.method_1085(class_2350.class_2351.field_11051, arg2, arg3.method_15418(), f);
			if (f != 0.0) {
				arg2 = arg2.method_989(0.0, 0.0, f);
			}
		}

		if (d != 0.0) {
			d = class_259.method_1085(class_2350.class_2351.field_11048, arg2, arg3.method_15418(), d);
			if (!bl && d != 0.0) {
				arg2 = arg2.method_989(d, 0.0, 0.0);
			}
		}

		if (!bl && f != 0.0) {
			f = class_259.method_1085(class_2350.class_2351.field_11051, arg2, arg3.method_15418(), f);
		}

		return new class_243(d, e, f);
	}

	public static class_243 method_17833(class_243 arg, class_238 arg2, class_1941 arg3, class_3726 arg4, class_3538<class_265> arg5) {
		double d = arg.field_1352;
		double e = arg.field_1351;
		double f = arg.field_1350;
		if (e != 0.0) {
			e = class_259.method_17945(class_2350.class_2351.field_11052, arg2, arg3, e, arg4, arg5.method_15418());
			if (e != 0.0) {
				arg2 = arg2.method_989(0.0, e, 0.0);
			}
		}

		boolean bl = Math.abs(d) < Math.abs(f);
		if (bl && f != 0.0) {
			f = class_259.method_17945(class_2350.class_2351.field_11051, arg2, arg3, f, arg4, arg5.method_15418());
			if (f != 0.0) {
				arg2 = arg2.method_989(0.0, 0.0, f);
			}
		}

		if (d != 0.0) {
			d = class_259.method_17945(class_2350.class_2351.field_11048, arg2, arg3, d, arg4, arg5.method_15418());
			if (!bl && d != 0.0) {
				arg2 = arg2.method_989(d, 0.0, 0.0);
			}
		}

		if (!bl && f != 0.0) {
			f = class_259.method_17945(class_2350.class_2351.field_11051, arg2, arg3, f, arg4, arg5.method_15418());
		}

		return new class_243(d, e, f);
	}

	protected float method_5867() {
		return (float)((int)this.field_5994 + 1);
	}

	public void method_5792() {
		class_238 lv = this.method_5829();
		this.field_5987 = (lv.field_1323 + lv.field_1320) / 2.0;
		this.field_6010 = lv.field_1322;
		this.field_6035 = (lv.field_1321 + lv.field_1324) / 2.0;
	}

	protected class_3414 method_5737() {
		return class_3417.field_14818;
	}

	protected class_3414 method_5625() {
		return class_3417.field_14737;
	}

	protected class_3414 method_5672() {
		return class_3417.field_14737;
	}

	protected void method_5852() {
		class_238 lv = this.method_5829();

		try (
			class_2338.class_2340 lv2 = class_2338.class_2340.method_10115(lv.field_1323 + 0.001, lv.field_1322 + 0.001, lv.field_1321 + 0.001);
			class_2338.class_2340 lv3 = class_2338.class_2340.method_10115(lv.field_1320 - 0.001, lv.field_1325 - 0.001, lv.field_1324 - 0.001);
			class_2338.class_2340 lv4 = class_2338.class_2340.method_10109();
		) {
			if (this.field_6002.method_8617(lv2, lv3)) {
				for (int i = lv2.method_10263(); i <= lv3.method_10263(); i++) {
					for (int j = lv2.method_10264(); j <= lv3.method_10264(); j++) {
						for (int k = lv2.method_10260(); k <= lv3.method_10260(); k++) {
							lv4.method_10113(i, j, k);
							class_2680 lv5 = this.field_6002.method_8320(lv4);

							try {
								lv5.method_11613(this.field_6002, lv4, this);
								this.method_5622(lv5);
							} catch (Throwable var60) {
								class_128 lv6 = class_128.method_560(var60, "Colliding entity with block");
								class_129 lv7 = lv6.method_562("Block being collided with");
								class_129.method_586(lv7, lv4, lv5);
								throw new class_148(lv6);
							}
						}
					}
				}
			}
		}
	}

	protected void method_5622(class_2680 arg) {
	}

	protected void method_5712(class_2338 arg, class_2680 arg2) {
		if (!arg2.method_11620().method_15797()) {
			class_2680 lv = this.field_6002.method_8320(arg.method_10084());
			class_2498 lv2 = lv.method_11614() == class_2246.field_10477 ? lv.method_11638() : arg2.method_11638();
			this.method_5783(lv2.method_10594(), lv2.method_10597() * 0.15F, lv2.method_10599());
		}
	}

	protected void method_5734(float f) {
		this.method_5783(this.method_5737(), f, 1.0F + (this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.4F);
	}

	protected float method_5801(float f) {
		return 0.0F;
	}

	protected boolean method_5776() {
		return false;
	}

	public void method_5783(class_3414 arg, float f, float g) {
		if (!this.method_5701()) {
			this.field_6002.method_8465(null, this.field_5987, this.field_6010, this.field_6035, arg, this.method_5634(), f, g);
		}
	}

	public boolean method_5701() {
		return this.field_6011.method_12789(field_5962);
	}

	public void method_5803(boolean bl) {
		this.field_6011.method_12778(field_5962, bl);
	}

	public boolean method_5740() {
		return this.field_6011.method_12789(field_5995);
	}

	public void method_5875(boolean bl) {
		this.field_6011.method_12778(field_5995, bl);
	}

	protected boolean method_5658() {
		return true;
	}

	protected void method_5623(double d, boolean bl, class_2680 arg, class_2338 arg2) {
		if (bl) {
			if (this.field_6017 > 0.0F) {
				arg.method_11614().method_9554(this.field_6002, arg2, this, this.field_6017);
			}

			this.field_6017 = 0.0F;
		} else if (d < 0.0) {
			this.field_6017 = (float)((double)this.field_6017 - d);
		}
	}

	@Nullable
	public class_238 method_5827() {
		return null;
	}

	protected void method_5714(int i) {
		if (!this.method_5753()) {
			this.method_5643(class_1282.field_5867, (float)i);
		}
	}

	public final boolean method_5753() {
		return this.method_5864().method_19946();
	}

	public void method_5747(float f, float g) {
		if (this.method_5782()) {
			for (class_1297 lv : this.method_5685()) {
				lv.method_5747(f, g);
			}
		}
	}

	public boolean method_5799() {
		return this.field_5957;
	}

	private boolean method_5778() {
		boolean var3;
		try (class_2338.class_2340 lv = class_2338.class_2340.method_10117(this)) {
			var3 = this.field_6002.method_8520(lv)
				|| this.field_6002.method_8520(lv.method_10112(this.field_5987, this.field_6010 + (double)this.field_18065.field_18068, this.field_6035));
		}

		return var3;
	}

	private boolean method_5798() {
		return this.field_6002.method_8320(new class_2338(this)).method_11614() == class_2246.field_10422;
	}

	public boolean method_5721() {
		return this.method_5799() || this.method_5778();
	}

	public boolean method_5637() {
		return this.method_5799() || this.method_5778() || this.method_5798();
	}

	public boolean method_5816() {
		return this.method_5799() || this.method_5798();
	}

	public boolean method_5869() {
		return this.field_6000 && this.method_5799();
	}

	private void method_5876() {
		this.method_5713();
		this.method_5630();
		this.method_5790();
	}

	public void method_5790() {
		if (this.method_5681()) {
			this.method_5796(this.method_5624() && this.method_5799() && !this.method_5765());
		} else {
			this.method_5796(this.method_5624() && this.method_5869() && !this.method_5765());
		}
	}

	public boolean method_5713() {
		if (this.method_5854() instanceof class_1690) {
			this.field_5957 = false;
		} else if (this.method_5692(class_3486.field_15517)) {
			if (!this.field_5957 && !this.field_5953) {
				this.method_5746();
			}

			this.field_6017 = 0.0F;
			this.field_5957 = true;
			this.method_5646();
		} else {
			this.field_5957 = false;
		}

		return this.field_5957;
	}

	private void method_5630() {
		this.field_6000 = this.method_5744(class_3486.field_15517, true);
	}

	protected void method_5746() {
		class_1297 lv = this.method_5782() && this.method_5642() != null ? this.method_5642() : this;
		float f = lv == this ? 0.2F : 0.9F;
		class_243 lv2 = lv.method_18798();
		float g = class_3532.method_15368(lv2.field_1352 * lv2.field_1352 * 0.2F + lv2.field_1351 * lv2.field_1351 + lv2.field_1350 * lv2.field_1350 * 0.2F) * f;
		if (g > 1.0F) {
			g = 1.0F;
		}

		if ((double)g < 0.25) {
			this.method_5783(this.method_5625(), g, 1.0F + (this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.4F);
		} else {
			this.method_5783(this.method_5672(), g, 1.0F + (this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.4F);
		}

		float h = (float)class_3532.method_15357(this.method_5829().field_1322);

		for (int i = 0; (float)i < 1.0F + this.field_18065.field_18067 * 20.0F; i++) {
			float j = (this.field_5974.nextFloat() * 2.0F - 1.0F) * this.field_18065.field_18067;
			float k = (this.field_5974.nextFloat() * 2.0F - 1.0F) * this.field_18065.field_18067;
			this.field_6002
				.method_8406(
					class_2398.field_11247,
					this.field_5987 + (double)j,
					(double)(h + 1.0F),
					this.field_6035 + (double)k,
					lv2.field_1352,
					lv2.field_1351 - (double)(this.field_5974.nextFloat() * 0.2F),
					lv2.field_1350
				);
		}

		for (int i = 0; (float)i < 1.0F + this.field_18065.field_18067 * 20.0F; i++) {
			float j = (this.field_5974.nextFloat() * 2.0F - 1.0F) * this.field_18065.field_18067;
			float k = (this.field_5974.nextFloat() * 2.0F - 1.0F) * this.field_18065.field_18067;
			this.field_6002
				.method_8406(
					class_2398.field_11202, this.field_5987 + (double)j, (double)(h + 1.0F), this.field_6035 + (double)k, lv2.field_1352, lv2.field_1351, lv2.field_1350
				);
		}
	}

	public void method_5666() {
		if (this.method_5624() && !this.method_5799()) {
			this.method_5839();
		}
	}

	protected void method_5839() {
		int i = class_3532.method_15357(this.field_5987);
		int j = class_3532.method_15357(this.field_6010 - 0.2F);
		int k = class_3532.method_15357(this.field_6035);
		class_2338 lv = new class_2338(i, j, k);
		class_2680 lv2 = this.field_6002.method_8320(lv);
		if (lv2.method_11610() != class_2464.field_11455) {
			class_243 lv3 = this.method_18798();
			this.field_6002
				.method_8406(
					new class_2388(class_2398.field_11217, lv2),
					this.field_5987 + ((double)this.field_5974.nextFloat() - 0.5) * (double)this.field_18065.field_18067,
					this.field_6010 + 0.1,
					this.field_6035 + ((double)this.field_5974.nextFloat() - 0.5) * (double)this.field_18065.field_18067,
					lv3.field_1352 * -4.0,
					1.5,
					lv3.field_1350 * -4.0
				);
		}
	}

	public boolean method_5777(class_3494<class_3611> arg) {
		return this.method_5744(arg, false);
	}

	public boolean method_5744(class_3494<class_3611> arg, boolean bl) {
		if (this.method_5854() instanceof class_1690) {
			return false;
		} else {
			double d = this.field_6010 + (double)this.method_5751();
			class_2338 lv = new class_2338(this.field_5987, d, this.field_6035);
			if (bl && !this.field_6002.method_8393(lv.method_10263() >> 4, lv.method_10260() >> 4)) {
				return false;
			} else {
				class_3610 lv2 = this.field_6002.method_8316(lv);
				return lv2.method_15767(arg) && d < (double)((float)lv.method_10264() + lv2.method_15763(this.field_6002, lv) + 0.11111111F);
			}
		}
	}

	public void method_20447() {
		this.field_19271 = true;
	}

	public boolean method_5771() {
		return this.field_19271;
	}

	public void method_5724(float f, class_243 arg) {
		class_243 lv = method_18795(arg, f, this.field_6031);
		this.method_18799(this.method_18798().method_1019(lv));
	}

	protected static class_243 method_18795(class_243 arg, float f, float g) {
		double d = arg.method_1027();
		if (d < 1.0E-7) {
			return class_243.field_1353;
		} else {
			class_243 lv = (d > 1.0 ? arg.method_1029() : arg).method_1021((double)f);
			float h = class_3532.method_15374(g * (float) (Math.PI / 180.0));
			float i = class_3532.method_15362(g * (float) (Math.PI / 180.0));
			return new class_243(lv.field_1352 * (double)i - lv.field_1350 * (double)h, lv.field_1351, lv.field_1350 * (double)i + lv.field_1352 * (double)h);
		}
	}

	@Environment(EnvType.CLIENT)
	public int method_5635() {
		class_2338 lv = new class_2338(this.field_5987, this.field_6010 + (double)this.method_5751(), this.field_6035);
		return this.field_6002.method_8591(lv) ? this.field_6002.method_8313(lv, 0) : 0;
	}

	public float method_5718() {
		class_2338.class_2339 lv = new class_2338.class_2339(this.field_5987, 0.0, this.field_6035);
		if (this.field_6002.method_8591(lv)) {
			lv.method_10099(class_3532.method_15357(this.field_6010 + (double)this.method_5751()));
			return this.field_6002.method_8610(lv);
		} else {
			return 0.0F;
		}
	}

	public void method_5866(class_1937 arg) {
		this.field_6002 = arg;
	}

	public void method_5641(double d, double e, double f, float g, float h) {
		this.field_5987 = class_3532.method_15350(d, -3.0E7, 3.0E7);
		this.field_6010 = e;
		this.field_6035 = class_3532.method_15350(f, -3.0E7, 3.0E7);
		this.field_6014 = this.field_5987;
		this.field_6036 = this.field_6010;
		this.field_5969 = this.field_6035;
		h = class_3532.method_15363(h, -90.0F, 90.0F);
		this.field_6031 = g;
		this.field_5965 = h;
		this.field_5982 = this.field_6031;
		this.field_6004 = this.field_5965;
		double i = (double)(this.field_5982 - g);
		if (i < -180.0) {
			this.field_5982 += 360.0F;
		}

		if (i >= 180.0) {
			this.field_5982 -= 360.0F;
		}

		this.method_5814(this.field_5987, this.field_6010, this.field_6035);
		this.method_5710(g, h);
	}

	public void method_5725(class_2338 arg, float f, float g) {
		this.method_5808((double)arg.method_10263() + 0.5, (double)arg.method_10264(), (double)arg.method_10260() + 0.5, f, g);
	}

	public void method_5808(double d, double e, double f, float g, float h) {
		this.field_5987 = d;
		this.field_6010 = e;
		this.field_6035 = f;
		this.field_6014 = this.field_5987;
		this.field_6036 = this.field_6010;
		this.field_5969 = this.field_6035;
		this.field_6038 = this.field_5987;
		this.field_5971 = this.field_6010;
		this.field_5989 = this.field_6035;
		this.field_6031 = g;
		this.field_5965 = h;
		this.method_5814(this.field_5987, this.field_6010, this.field_6035);
	}

	public float method_5739(class_1297 arg) {
		float f = (float)(this.field_5987 - arg.field_5987);
		float g = (float)(this.field_6010 - arg.field_6010);
		float h = (float)(this.field_6035 - arg.field_6035);
		return class_3532.method_15355(f * f + g * g + h * h);
	}

	public double method_5649(double d, double e, double f) {
		double g = this.field_5987 - d;
		double h = this.field_6010 - e;
		double i = this.field_6035 - f;
		return g * g + h * h + i * i;
	}

	public double method_5858(class_1297 arg) {
		return this.method_5707(arg.method_19538());
	}

	public double method_5707(class_243 arg) {
		double d = this.field_5987 - arg.field_1352;
		double e = this.field_6010 - arg.field_1351;
		double f = this.field_6035 - arg.field_1350;
		return d * d + e * e + f * f;
	}

	public void method_5694(class_1657 arg) {
	}

	public void method_5697(class_1297 arg) {
		if (!this.method_5794(arg)) {
			if (!arg.field_5960 && !this.field_5960) {
				double d = arg.field_5987 - this.field_5987;
				double e = arg.field_6035 - this.field_6035;
				double f = class_3532.method_15391(d, e);
				if (f >= 0.01F) {
					f = (double)class_3532.method_15368(f);
					d /= f;
					e /= f;
					double g = 1.0 / f;
					if (g > 1.0) {
						g = 1.0;
					}

					d *= g;
					e *= g;
					d *= 0.05F;
					e *= 0.05F;
					d *= (double)(1.0F - this.field_5968);
					e *= (double)(1.0F - this.field_5968);
					if (!this.method_5782()) {
						this.method_5762(-d, 0.0, -e);
					}

					if (!arg.method_5782()) {
						arg.method_5762(d, 0.0, e);
					}
				}
			}
		}
	}

	public void method_5762(double d, double e, double f) {
		this.method_18799(this.method_18798().method_1031(d, e, f));
		this.field_6007 = true;
	}

	protected void method_5785() {
		this.field_6037 = true;
	}

	public boolean method_5643(class_1282 arg, float f) {
		if (this.method_5679(arg)) {
			return false;
		} else {
			this.method_5785();
			return false;
		}
	}

	public final class_243 method_5828(float f) {
		return this.method_5631(this.method_5695(f), this.method_5705(f));
	}

	public float method_5695(float f) {
		return f == 1.0F ? this.field_5965 : class_3532.method_16439(f, this.field_6004, this.field_5965);
	}

	public float method_5705(float f) {
		return f == 1.0F ? this.field_6031 : class_3532.method_16439(f, this.field_5982, this.field_6031);
	}

	protected final class_243 method_5631(float f, float g) {
		float h = f * (float) (Math.PI / 180.0);
		float i = -g * (float) (Math.PI / 180.0);
		float j = class_3532.method_15362(i);
		float k = class_3532.method_15374(i);
		float l = class_3532.method_15362(h);
		float m = class_3532.method_15374(h);
		return new class_243((double)(k * l), (double)(-m), (double)(j * l));
	}

	public final class_243 method_18864(float f) {
		return this.method_18863(this.method_5695(f), this.method_5705(f));
	}

	protected final class_243 method_18863(float f, float g) {
		return this.method_5631(f - 90.0F, g);
	}

	public class_243 method_5836(float f) {
		if (f == 1.0F) {
			return new class_243(this.field_5987, this.field_6010 + (double)this.method_5751(), this.field_6035);
		} else {
			double d = class_3532.method_16436((double)f, this.field_6014, this.field_5987);
			double e = class_3532.method_16436((double)f, this.field_6036, this.field_6010) + (double)this.method_5751();
			double g = class_3532.method_16436((double)f, this.field_5969, this.field_6035);
			return new class_243(d, e, g);
		}
	}

	@Environment(EnvType.CLIENT)
	public class_239 method_5745(double d, float f, boolean bl) {
		class_243 lv = this.method_5836(f);
		class_243 lv2 = this.method_5828(f);
		class_243 lv3 = lv.method_1031(lv2.field_1352 * d, lv2.field_1351 * d, lv2.field_1350 * d);
		return this.field_6002
			.method_17742(new class_3959(lv, lv3, class_3959.class_3960.field_17559, bl ? class_3959.class_242.field_1347 : class_3959.class_242.field_1348, this));
	}

	@Environment(EnvType.CLIENT)
	public class_239 method_26731(double d, float f) {
		class_243 lv = this.method_5836(f);
		class_243 lv2 = this.method_5828(f);
		class_243 lv3 = lv.method_1031(lv2.field_1352 * d, lv2.field_1351 * d, lv2.field_1350 * d);
		return this.field_6002.method_17742(new class_3959(lv, lv3, class_3959.class_3960.field_17558, class_3959.class_242.field_1348, this));
	}

	public boolean method_5863() {
		return false;
	}

	public boolean method_5810() {
		return false;
	}

	public void method_5716(class_1297 arg, int i, class_1282 arg2) {
		if (arg instanceof class_3222) {
			class_174.field_1188.method_8990((class_3222)arg, this, arg2);
		}
	}

	@Environment(EnvType.CLIENT)
	public boolean method_5727(double d, double e, double f) {
		double g = this.field_5987 - d;
		double h = this.field_6010 - e;
		double i = this.field_6035 - f;
		double j = g * g + h * h + i * i;
		return this.method_5640(j);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_5640(double d) {
		double e = this.method_5829().method_995();
		if (Double.isNaN(e)) {
			e = 1.0;
		}

		e *= 64.0 * field_5999;
		return d < e * e;
	}

	public boolean method_5786(class_2487 arg) {
		String string = this.method_5653();
		if (!this.field_5988 && string != null) {
			arg.method_10582("id", string);
			this.method_5647(arg);
			return true;
		} else {
			return false;
		}
	}

	public boolean method_5662(class_2487 arg) {
		return this.method_5765() ? false : this.method_5786(arg);
	}

	public class_2487 method_5647(class_2487 arg) {
		try {
			arg.method_10566("Pos", this.method_5846(this.field_5987, this.field_6010, this.field_6035));
			class_243 lv = this.method_18798();
			arg.method_10566("Motion", this.method_5846(lv.field_1352, lv.field_1351, lv.field_1350));
			arg.method_10566("Rotation", this.method_5726(this.field_6031, this.field_5965));
			arg.method_10548("FallDistance", this.field_6017);
			arg.method_10575("Fire", (short)this.field_5956);
			arg.method_10575("Air", (short)this.method_5669());
			arg.method_10556("OnGround", this.field_5952);
			arg.method_10569("Dimension", this.field_6026.method_12484());
			arg.method_10556("Invulnerable", this.field_6009);
			arg.method_10569("PortalCooldown", this.field_6018);
			arg.method_10560("UUID", this.method_5667());
			class_2561 lv2 = this.method_5797();
			if (lv2 != null) {
				arg.method_10582("CustomName", class_2561.class_2562.method_10867(lv2));
			}

			if (this.method_5807()) {
				arg.method_10556("CustomNameVisible", this.method_5807());
			}

			if (this.method_5701()) {
				arg.method_10556("Silent", this.method_5701());
			}

			if (this.method_5740()) {
				arg.method_10556("NoGravity", this.method_5740());
			}

			if (this.field_5958) {
				arg.method_10556("Glowing", this.field_5958);
			}

			if (!this.field_6029.isEmpty()) {
				class_2499 lv3 = new class_2499();

				for (String string : this.field_6029) {
					lv3.add(new class_2519(string));
				}

				arg.method_10566("Tags", lv3);
			}

			this.method_5652(arg);
			if (this.method_5782()) {
				class_2499 lv3 = new class_2499();

				for (class_1297 lv4 : this.method_5685()) {
					class_2487 lv5 = new class_2487();
					if (lv4.method_5786(lv5)) {
						lv3.add(lv5);
					}
				}

				if (!lv3.isEmpty()) {
					arg.method_10566("Passengers", lv3);
				}
			}

			return arg;
		} catch (Throwable var8) {
			class_128 lv6 = class_128.method_560(var8, "Saving entity NBT");
			class_129 lv7 = lv6.method_562("Entity being saved");
			this.method_5819(lv7);
			throw new class_148(lv6);
		}
	}

	public void method_5651(class_2487 arg) {
		try {
			class_2499 lv = arg.method_10554("Pos", 6);
			class_2499 lv2 = arg.method_10554("Motion", 6);
			class_2499 lv3 = arg.method_10554("Rotation", 5);
			double d = lv2.method_10611(0);
			double e = lv2.method_10611(1);
			double f = lv2.method_10611(2);
			this.method_18800(Math.abs(d) > 10.0 ? 0.0 : d, Math.abs(e) > 10.0 ? 0.0 : e, Math.abs(f) > 10.0 ? 0.0 : f);
			this.field_5987 = lv.method_10611(0);
			this.field_6010 = lv.method_10611(1);
			this.field_6035 = lv.method_10611(2);
			this.field_6038 = this.field_5987;
			this.field_5971 = this.field_6010;
			this.field_5989 = this.field_6035;
			this.field_6014 = this.field_5987;
			this.field_6036 = this.field_6010;
			this.field_5969 = this.field_6035;
			this.field_6031 = lv3.method_10604(0);
			this.field_5965 = lv3.method_10604(1);
			this.field_5982 = this.field_6031;
			this.field_6004 = this.field_5965;
			this.method_5847(this.field_6031);
			this.method_5636(this.field_6031);
			this.field_6017 = arg.method_10583("FallDistance");
			this.field_5956 = arg.method_10568("Fire");
			this.method_5855(arg.method_10568("Air"));
			this.field_5952 = arg.method_10577("OnGround");
			if (arg.method_10545("Dimension")) {
				this.field_6026 = class_2874.method_12490(arg.method_10550("Dimension"));
			}

			this.field_6009 = arg.method_10577("Invulnerable");
			this.field_6018 = arg.method_10550("PortalCooldown");
			if (arg.method_10576("UUID")) {
				this.field_6021 = arg.method_10584("UUID");
				this.field_5981 = this.field_6021.toString();
			}

			if (!Double.isFinite(this.field_5987) || !Double.isFinite(this.field_6010) || !Double.isFinite(this.field_6035)) {
				throw new IllegalStateException("Entity has invalid position");
			} else if (Double.isFinite((double)this.field_6031) && Double.isFinite((double)this.field_5965)) {
				this.method_5814(this.field_5987, this.field_6010, this.field_6035);
				this.method_5710(this.field_6031, this.field_5965);
				if (arg.method_10573("CustomName", 8)) {
					this.method_5665(class_2561.class_2562.method_10877(arg.method_10558("CustomName")));
				}

				this.method_5880(arg.method_10577("CustomNameVisible"));
				this.method_5803(arg.method_10577("Silent"));
				this.method_5875(arg.method_10577("NoGravity"));
				this.method_5834(arg.method_10577("Glowing"));
				if (arg.method_10573("Tags", 9)) {
					this.field_6029.clear();
					class_2499 lv4 = arg.method_10554("Tags", 8);
					int i = Math.min(lv4.size(), 1024);

					for (int j = 0; j < i; j++) {
						this.field_6029.add(lv4.method_10608(j));
					}
				}

				this.method_5749(arg);
				if (this.method_5638()) {
					this.method_5814(this.field_5987, this.field_6010, this.field_6035);
				}
			} else {
				throw new IllegalStateException("Entity has invalid rotation");
			}
		} catch (Throwable var14) {
			class_128 lv5 = class_128.method_560(var14, "Loading entity NBT");
			class_129 lv6 = lv5.method_562("Entity being loaded");
			this.method_5819(lv6);
			throw new class_148(lv5);
		}
	}

	protected boolean method_5638() {
		return true;
	}

	@Nullable
	protected final String method_5653() {
		class_1299<?> lv = this.method_5864();
		class_2960 lv2 = class_1299.method_5890(lv);
		return lv.method_5893() && lv2 != null ? lv2.toString() : null;
	}

	protected abstract void method_5749(class_2487 arg);

	protected abstract void method_5652(class_2487 arg);

	protected class_2499 method_5846(double... ds) {
		class_2499 lv = new class_2499();

		for (double d : ds) {
			lv.add(new class_2489(d));
		}

		return lv;
	}

	protected class_2499 method_5726(float... fs) {
		class_2499 lv = new class_2499();

		for (float f : fs) {
			lv.add(new class_2494(f));
		}

		return lv;
	}

	@Nullable
	public class_1542 method_5706(class_1935 arg) {
		return this.method_5870(arg, 0);
	}

	@Nullable
	public class_1542 method_5870(class_1935 arg, int i) {
		return this.method_5699(new class_1799(arg), (float)i);
	}

	@Nullable
	public class_1542 method_5775(class_1799 arg) {
		return this.method_5699(arg, 0.0F);
	}

	@Nullable
	public class_1542 method_5699(class_1799 arg, float f) {
		if (arg.method_7960()) {
			return null;
		} else if (this.field_6002.field_9236) {
			return null;
		} else {
			class_1542 lv = new class_1542(this.field_6002, this.field_5987, this.field_6010 + (double)f, this.field_6035, arg);
			lv.method_6988();
			this.field_6002.method_8649(lv);
			return lv;
		}
	}

	public boolean method_5805() {
		return !this.field_5988;
	}

	public boolean method_5757() {
		if (this.field_5960) {
			return false;
		} else {
			try (class_2338.class_2340 lv = class_2338.class_2340.method_10109()) {
				for (int i = 0; i < 8; i++) {
					int j = class_3532.method_15357(this.field_6010 + (double)(((float)((i >> 0) % 2) - 0.5F) * 0.1F) + (double)this.field_18066);
					int k = class_3532.method_15357(this.field_5987 + (double)(((float)((i >> 1) % 2) - 0.5F) * this.field_18065.field_18067 * 0.8F));
					int l = class_3532.method_15357(this.field_6035 + (double)(((float)((i >> 2) % 2) - 0.5F) * this.field_18065.field_18067 * 0.8F));
					if (lv.method_10263() != k || lv.method_10264() != j || lv.method_10260() != l) {
						lv.method_10113(k, j, l);
						if (this.field_6002.method_8320(lv).method_11582(this.field_6002, lv)) {
							return true;
						}
					}
				}

				return false;
			}
		}
	}

	public boolean method_5688(class_1657 arg, class_1268 arg2) {
		return false;
	}

	@Nullable
	public class_238 method_5708(class_1297 arg) {
		return null;
	}

	public void method_5842() {
		this.method_18799(class_243.field_1353);
		this.method_5773();
		if (this.method_5765()) {
			this.method_5854().method_5865(this);
		}
	}

	public void method_5865(class_1297 arg) {
		if (this.method_5626(arg)) {
			arg.method_5814(this.field_5987, this.field_6010 + this.method_5621() + arg.method_5678(), this.field_6035);
		}
	}

	@Environment(EnvType.CLIENT)
	public void method_5644(class_1297 arg) {
	}

	public double method_5678() {
		return 0.0;
	}

	public double method_5621() {
		return (double)this.field_18065.field_18068 * 0.75;
	}

	public boolean method_5804(class_1297 arg) {
		return this.method_5873(arg, false);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_5709() {
		return this instanceof class_1309;
	}

	public boolean method_5873(class_1297 arg, boolean bl) {
		for (class_1297 lv = arg; lv.field_6034 != null; lv = lv.field_6034) {
			if (lv.field_6034 == this) {
				return false;
			}
		}

		if (bl || this.method_5860(arg) && arg.method_5818(this)) {
			if (this.method_5765()) {
				this.method_5848();
			}

			this.field_6034 = arg;
			this.field_6034.method_5627(this);
			return true;
		} else {
			return false;
		}
	}

	protected boolean method_5860(class_1297 arg) {
		return this.field_5951 <= 0;
	}

	protected boolean method_20233(class_4050 arg) {
		return this.field_6002.method_8587(this, this.method_20343(arg));
	}

	public void method_5772() {
		for (int i = this.field_5979.size() - 1; i >= 0; i--) {
			((class_1297)this.field_5979.get(i)).method_5848();
		}
	}

	public void method_5848() {
		if (this.field_6034 != null) {
			class_1297 lv = this.field_6034;
			this.field_6034 = null;
			lv.method_5793(this);
		}
	}

	protected void method_5627(class_1297 arg) {
		if (arg.method_5854() != this) {
			throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
		} else {
			if (!this.field_6002.field_9236 && arg instanceof class_1657 && !(this.method_5642() instanceof class_1657)) {
				this.field_5979.add(0, arg);
			} else {
				this.field_5979.add(arg);
			}
		}
	}

	protected void method_5793(class_1297 arg) {
		if (arg.method_5854() == this) {
			throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
		} else {
			this.field_5979.remove(arg);
			arg.field_5951 = 60;
		}
	}

	protected boolean method_5818(class_1297 arg) {
		return this.method_5685().size() < 1;
	}

	@Environment(EnvType.CLIENT)
	public void method_5759(double d, double e, double f, float g, float h, int i, boolean bl) {
		this.method_5814(d, e, f);
		this.method_5710(g, h);
	}

	@Environment(EnvType.CLIENT)
	public void method_5683(float f, int i) {
		this.method_5847(f);
	}

	public float method_5871() {
		return 0.0F;
	}

	public class_243 method_5720() {
		return this.method_5631(this.field_5965, this.field_6031);
	}

	public class_241 method_5802() {
		return new class_241(this.field_5965, this.field_6031);
	}

	@Environment(EnvType.CLIENT)
	public class_243 method_5663() {
		return class_243.method_1034(this.method_5802());
	}

	public void method_5717(class_2338 arg) {
		if (this.field_6018 > 0) {
			this.field_6018 = this.method_5806();
		} else {
			if (!this.field_6002.field_9236 && !arg.equals(this.field_5991)) {
				this.field_5991 = new class_2338(arg);
				class_2700.class_2702 lv = ((class_2423)class_2246.field_10316).method_10350(this.field_6002, this.field_5991);
				double d = lv.method_11719().method_10166() == class_2350.class_2351.field_11048
					? (double)lv.method_11715().method_10260()
					: (double)lv.method_11715().method_10263();
				double e = Math.abs(
					class_3532.method_15370(
						(lv.method_11719().method_10166() == class_2350.class_2351.field_11048 ? this.field_6035 : this.field_5987)
							- (double)(lv.method_11719().method_10170().method_10171() == class_2350.class_2352.field_11060 ? 1 : 0),
						d,
						d - (double)lv.method_11718()
					)
				);
				double f = class_3532.method_15370(
					this.field_6010 - 1.0, (double)lv.method_11715().method_10264(), (double)(lv.method_11715().method_10264() - lv.method_11720())
				);
				this.field_6020 = new class_243(e, f, 0.0);
				this.field_6028 = lv.method_11719();
			}

			this.field_5963 = true;
		}
	}

	protected void method_18379() {
		if (this.field_6002 instanceof class_3218) {
			int i = this.method_5741();
			if (this.field_5963) {
				if (this.field_6002.method_8503().method_3839() && !this.method_5765() && this.field_5972++ >= i) {
					this.field_6002.method_16107().method_15396("portal");
					this.field_5972 = i;
					this.field_6018 = this.method_5806();
					this.method_5731(this.field_6002.field_9247.method_12460() == class_2874.field_13076 ? class_2874.field_13072 : class_2874.field_13076);
					this.field_6002.method_16107().method_15407();
				}

				this.field_5963 = false;
			} else {
				if (this.field_5972 > 0) {
					this.field_5972 -= 4;
				}

				if (this.field_5972 < 0) {
					this.field_5972 = 0;
				}
			}

			this.method_5760();
		}
	}

	public int method_5806() {
		return 300;
	}

	@Environment(EnvType.CLIENT)
	public void method_5750(double d, double e, double f) {
		this.method_18800(d, e, f);
	}

	@Environment(EnvType.CLIENT)
	public void method_5711(byte b) {
	}

	@Environment(EnvType.CLIENT)
	public void method_5879() {
	}

	public Iterable<class_1799> method_5877() {
		return field_6030;
	}

	public Iterable<class_1799> method_5661() {
		return field_6030;
	}

	public Iterable<class_1799> method_5743() {
		return Iterables.concat(this.method_5877(), this.method_5661());
	}

	public void method_5673(class_1304 arg, class_1799 arg2) {
	}

	public boolean method_5809() {
		boolean bl = this.field_6002 != null && this.field_6002.field_9236;
		return !this.method_5753() && (this.field_5956 > 0 || bl && this.method_5795(0));
	}

	public boolean method_5765() {
		return this.method_5854() != null;
	}

	public boolean method_5782() {
		return !this.method_5685().isEmpty();
	}

	public boolean method_5788() {
		return true;
	}

	public boolean method_5715() {
		return this.method_5795(1);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_20231() {
		return this.method_18376() == class_4050.field_18081;
	}

	public void method_5660(boolean bl) {
		this.method_5729(1, bl);
	}

	public boolean method_5624() {
		return this.method_5795(3);
	}

	public void method_5728(boolean bl) {
		this.method_5729(3, bl);
	}

	public boolean method_5681() {
		return this.method_5795(4);
	}

	public boolean method_20232() {
		return this.method_18376() == class_4050.field_18079;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_20448() {
		return this.method_20232() && !this.method_5799();
	}

	public void method_5796(boolean bl) {
		this.method_5729(4, bl);
	}

	public boolean method_5851() {
		return this.field_5958 || this.field_6002.field_9236 && this.method_5795(6);
	}

	public void method_5834(boolean bl) {
		this.field_5958 = bl;
		if (!this.field_6002.field_9236) {
			this.method_5729(6, this.field_5958);
		}
	}

	public boolean method_5767() {
		return this.method_5795(5);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_5756(class_1657 arg) {
		if (arg.method_7325()) {
			return false;
		} else {
			class_270 lv = this.method_5781();
			return lv != null && arg != null && arg.method_5781() == lv && lv.method_1199() ? false : this.method_5767();
		}
	}

	@Nullable
	public class_270 method_5781() {
		return this.field_6002.method_8428().method_1164(this.method_5820());
	}

	public boolean method_5722(class_1297 arg) {
		return this.method_5645(arg.method_5781());
	}

	public boolean method_5645(class_270 arg) {
		return this.method_5781() != null ? this.method_5781().method_1206(arg) : false;
	}

	public void method_5648(boolean bl) {
		this.method_5729(5, bl);
	}

	protected boolean method_5795(int i) {
		return (this.field_6011.method_12789(field_5990) & 1 << i) != 0;
	}

	protected void method_5729(int i, boolean bl) {
		byte b = this.field_6011.method_12789(field_5990);
		if (bl) {
			this.field_6011.method_12778(field_5990, (byte)(b | 1 << i));
		} else {
			this.field_6011.method_12778(field_5990, (byte)(b & ~(1 << i)));
		}
	}

	public int method_5748() {
		return 300;
	}

	public int method_5669() {
		return this.field_6011.method_12789(field_6032);
	}

	public void method_5855(int i) {
		this.field_6011.method_12778(field_6032, i);
	}

	public void method_5800(class_1538 arg) {
		this.field_5956++;
		if (this.field_5956 == 0) {
			this.method_5639(8);
		}

		this.method_5643(class_1282.field_5861, 5.0F);
	}

	public void method_5700(boolean bl) {
		class_243 lv = this.method_18798();
		double d;
		if (bl) {
			d = Math.max(-0.9, lv.field_1351 - 0.03);
		} else {
			d = Math.min(1.8, lv.field_1351 + 0.1);
		}

		this.method_18800(lv.field_1352, d, lv.field_1350);
	}

	public void method_5764(boolean bl) {
		class_243 lv = this.method_18798();
		double d;
		if (bl) {
			d = Math.max(-0.3, lv.field_1351 - 0.03);
		} else {
			d = Math.min(0.7, lv.field_1351 + 0.06);
		}

		this.method_18800(lv.field_1352, d, lv.field_1350);
		this.field_6017 = 0.0F;
	}

	public void method_5874(class_1309 arg) {
	}

	protected void method_5632(double d, double e, double f) {
		class_2338 lv = new class_2338(d, e, f);
		class_243 lv2 = new class_243(d - (double)lv.method_10263(), e - (double)lv.method_10264(), f - (double)lv.method_10260());
		class_2338.class_2339 lv3 = new class_2338.class_2339();
		class_2350 lv4 = class_2350.field_11036;
		double g = Double.MAX_VALUE;

		for (class_2350 lv5 : new class_2350[]{
			class_2350.field_11043, class_2350.field_11035, class_2350.field_11039, class_2350.field_11034, class_2350.field_11036
		}) {
			lv3.method_10101(lv).method_10098(lv5);
			if (!class_2248.method_9614(this.field_6002.method_8320(lv3).method_11628(this.field_6002, lv3))) {
				double h = lv2.method_18043(lv5.method_10166());
				double i = lv5.method_10171() == class_2350.class_2352.field_11056 ? 1.0 - h : h;
				if (i < g) {
					g = i;
					lv4 = lv5;
				}
			}
		}

		float j = this.field_5974.nextFloat() * 0.2F + 0.1F;
		float k = (float)lv4.method_10171().method_10181();
		class_243 lv6 = this.method_18798().method_1021(0.75);
		if (lv4.method_10166() == class_2350.class_2351.field_11048) {
			this.method_18800((double)(k * j), lv6.field_1351, lv6.field_1350);
		} else if (lv4.method_10166() == class_2350.class_2351.field_11052) {
			this.method_18800(lv6.field_1352, (double)(k * j), lv6.field_1350);
		} else if (lv4.method_10166() == class_2350.class_2351.field_11051) {
			this.method_18800(lv6.field_1352, lv6.field_1351, (double)(k * j));
		}
	}

	public void method_5844(class_2680 arg, class_243 arg2) {
		this.field_6017 = 0.0F;
		this.field_17046 = arg2;
	}

	private static void method_5856(class_2561 arg) {
		arg.method_10859(argx -> argx.method_10958(null)).method_10855().forEach(class_1297::method_5856);
	}

	@Override
	public class_2561 method_5477() {
		class_2561 lv = this.method_5797();
		if (lv != null) {
			class_2561 lv2 = lv.method_10853();
			method_5856(lv2);
			return lv2;
		} else {
			return this.field_5961.method_5897();
		}
	}

	public boolean method_5779(class_1297 arg) {
		return this == arg;
	}

	public float method_5791() {
		return 0.0F;
	}

	public void method_5847(float f) {
	}

	public void method_5636(float f) {
	}

	public boolean method_5732() {
		return true;
	}

	public boolean method_5698(class_1297 arg) {
		return false;
	}

	public String toString() {
		return String.format(
			Locale.ROOT,
			"%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]",
			this.getClass().getSimpleName(),
			this.method_5477().method_10851(),
			this.field_5986,
			this.field_6002 == null ? "~NULL~" : this.field_6002.method_8401().method_150(),
			this.field_5987,
			this.field_6010,
			this.field_6035
		);
	}

	public boolean method_5679(class_1282 arg) {
		return this.field_6009 && arg != class_1282.field_5849 && !arg.method_5530();
	}

	public boolean method_5655() {
		return this.field_6009;
	}

	public void method_5684(boolean bl) {
		this.field_6009 = bl;
	}

	public void method_5719(class_1297 arg) {
		this.method_5808(arg.field_5987, arg.field_6010, arg.field_6035, arg.field_6031, arg.field_5965);
	}

	public void method_5878(class_1297 arg) {
		class_2487 lv = arg.method_5647(new class_2487());
		lv.method_10551("Dimension");
		this.method_5651(lv);
		this.field_6018 = arg.field_6018;
		this.field_5991 = arg.field_5991;
		this.field_6020 = arg.field_6020;
		this.field_6028 = arg.field_6028;
	}

	@Nullable
	public class_1297 method_5731(class_2874 arg) {
		if (!this.field_6002.field_9236 && !this.field_5988) {
			this.field_6002.method_16107().method_15396("changeDimension");
			MinecraftServer minecraftServer = this.method_5682();
			class_2874 lv = this.field_6026;
			class_3218 lv2 = minecraftServer.method_3847(lv);
			class_3218 lv3 = minecraftServer.method_3847(arg);
			this.field_6026 = arg;
			this.method_18375();
			this.field_6002.method_16107().method_15396("reposition");
			class_243 lv4 = this.method_18798();
			float f = 0.0F;
			class_2338 lv5;
			if (lv == class_2874.field_13078 && arg == class_2874.field_13072) {
				lv5 = lv3.method_8598(class_2902.class_2903.field_13203, lv3.method_8395());
			} else if (arg == class_2874.field_13078) {
				lv5 = lv3.method_14169();
			} else {
				double d = this.field_5987;
				double e = this.field_6035;
				double g = 8.0;
				if (lv == class_2874.field_13072 && arg == class_2874.field_13076) {
					d /= 8.0;
					e /= 8.0;
				} else if (lv == class_2874.field_13076 && arg == class_2874.field_13072) {
					d *= 8.0;
					e *= 8.0;
				}

				double h = Math.min(-2.9999872E7, lv3.method_8621().method_11976() + 16.0);
				double i = Math.min(-2.9999872E7, lv3.method_8621().method_11958() + 16.0);
				double j = Math.min(2.9999872E7, lv3.method_8621().method_11963() - 16.0);
				double k = Math.min(2.9999872E7, lv3.method_8621().method_11977() - 16.0);
				d = class_3532.method_15350(d, h, j);
				e = class_3532.method_15350(e, i, k);
				class_243 lv6 = this.method_5656();
				lv5 = new class_2338(d, this.field_6010, e);
				class_2700.class_4297 lv7 = lv3.method_14173().method_18475(lv5, lv4, this.method_5843(), lv6.field_1352, lv6.field_1351, this instanceof class_1657);
				if (lv7 == null) {
					return null;
				}

				lv5 = new class_2338(lv7.field_19281);
				lv4 = lv7.field_19282;
				f = (float)lv7.field_19283;
			}

			this.field_6002.method_16107().method_15405("reloading");
			class_1297 lv8 = this.method_5864().method_5883(lv3);
			if (lv8 != null) {
				lv8.method_5878(this);
				lv8.method_5725(lv5, lv8.field_6031 + f, lv8.field_5965);
				lv8.method_18799(lv4);
				lv3.method_18769(lv8);
			}

			this.field_5988 = true;
			this.field_6002.method_16107().method_15407();
			lv2.method_14197();
			lv3.method_14197();
			this.field_6002.method_16107().method_15407();
			return lv8;
		} else {
			return null;
		}
	}

	public boolean method_5822() {
		return true;
	}

	public float method_5774(class_1927 arg, class_1922 arg2, class_2338 arg3, class_2680 arg4, class_3610 arg5, float f) {
		return f;
	}

	public boolean method_5853(class_1927 arg, class_1922 arg2, class_2338 arg3, class_2680 arg4, float f) {
		return true;
	}

	public int method_5850() {
		return 3;
	}

	public class_243 method_5656() {
		return this.field_6020;
	}

	public class_2350 method_5843() {
		return this.field_6028;
	}

	public boolean method_5696() {
		return false;
	}

	public void method_5819(class_129 arg) {
		arg.method_577("Entity Type", () -> class_1299.method_5890(this.method_5864()) + " (" + this.getClass().getCanonicalName() + ")");
		arg.method_578("Entity ID", this.field_5986);
		arg.method_577("Entity Name", () -> this.method_5477().getString());
		arg.method_578("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.field_5987, this.field_6010, this.field_6035));
		arg.method_578(
			"Entity's Block location",
			class_129.method_581(class_3532.method_15357(this.field_5987), class_3532.method_15357(this.field_6010), class_3532.method_15357(this.field_6035))
		);
		class_243 lv = this.method_18798();
		arg.method_578("Entity's Momentum", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", lv.field_1352, lv.field_1351, lv.field_1350));
		arg.method_577("Entity's Passengers", () -> this.method_5685().toString());
		arg.method_577("Entity's Vehicle", () -> this.method_5854().toString());
	}

	@Environment(EnvType.CLIENT)
	public boolean method_5862() {
		return this.method_5809();
	}

	public void method_5826(UUID uUID) {
		this.field_6021 = uUID;
		this.field_5981 = this.field_6021.toString();
	}

	public UUID method_5667() {
		return this.field_6021;
	}

	public String method_5845() {
		return this.field_5981;
	}

	public String method_5820() {
		return this.field_5981;
	}

	public boolean method_5675() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	public static double method_5824() {
		return field_5999;
	}

	@Environment(EnvType.CLIENT)
	public static void method_5840(double d) {
		field_5999 = d;
	}

	@Override
	public class_2561 method_5476() {
		return class_268.method_1142(this.method_5781(), this.method_5477())
			.method_10859(arg -> arg.method_10949(this.method_5769()).method_10975(this.method_5845()));
	}

	public void method_5665(@Nullable class_2561 arg) {
		this.field_6011.method_12778(field_6027, Optional.ofNullable(arg));
	}

	@Nullable
	@Override
	public class_2561 method_5797() {
		return (class_2561)this.field_6011.method_12789(field_6027).orElse(null);
	}

	@Override
	public boolean method_16914() {
		return this.field_6011.method_12789(field_6027).isPresent();
	}

	public void method_5880(boolean bl) {
		this.field_6011.method_12778(field_5975, bl);
	}

	public boolean method_5807() {
		return this.field_6011.method_12789(field_5975);
	}

	public final void method_20620(double d, double e, double f) {
		if (this.field_6002 instanceof class_3218) {
			class_1923 lv = new class_1923(new class_2338(d, e, f));
			((class_3218)this.field_6002).method_14178().method_17297(class_3230.field_19347, lv, 0, this.method_5628());
			this.field_6002.method_8497(lv.field_9181, lv.field_9180);
			this.method_5859(d, e, f);
		}
	}

	public void method_5859(double d, double e, double f) {
		if (this.field_6002 instanceof class_3218) {
			this.field_5966 = true;
			this.method_5808(d, e, f, this.field_6031, this.field_5965);
			((class_3218)this.field_6002).method_18767(this);
		}
	}

	@Environment(EnvType.CLIENT)
	public boolean method_5733() {
		return this.method_5807();
	}

	public void method_5674(class_2940<?> arg) {
		if (field_18064.equals(arg)) {
			this.method_18382();
		}
	}

	public void method_18382() {
		class_4048 lv = this.field_18065;
		class_4050 lv2 = this.method_18376();
		class_4048 lv3 = this.method_18377(lv2);
		this.field_18065 = lv3;
		this.field_18066 = this.method_18378(lv2, lv3);
		if (lv3.field_18067 < lv.field_18067) {
			double d = (double)lv3.field_18067 / 2.0;
			this.method_5857(
				new class_238(
					this.field_5987 - d, this.field_6010, this.field_6035 - d, this.field_5987 + d, this.field_6010 + (double)lv3.field_18068, this.field_6035 + d
				)
			);
		} else {
			class_238 lv4 = this.method_5829();
			this.method_5857(
				new class_238(
					lv4.field_1323,
					lv4.field_1322,
					lv4.field_1321,
					lv4.field_1323 + (double)lv3.field_18067,
					lv4.field_1322 + (double)lv3.field_18068,
					lv4.field_1321 + (double)lv3.field_18067
				)
			);
			if (lv3.field_18067 > lv.field_18067 && !this.field_5953 && !this.field_6002.field_9236) {
				float f = lv.field_18067 - lv3.field_18067;
				this.method_5784(class_1313.field_6308, new class_243((double)f, 0.0, (double)f));
			}
		}
	}

	public class_2350 method_5735() {
		return class_2350.method_10150((double)this.field_6031);
	}

	public class_2350 method_5755() {
		return this.method_5735();
	}

	protected class_2568 method_5769() {
		class_2487 lv = new class_2487();
		class_2960 lv2 = class_1299.method_5890(this.method_5864());
		lv.method_10582("id", this.method_5845());
		if (lv2 != null) {
			lv.method_10582("type", lv2.toString());
		}

		lv.method_10582("name", class_2561.class_2562.method_10867(this.method_5477()));
		return new class_2568(class_2568.class_2569.field_11761, new class_2585(lv.toString()));
	}

	public boolean method_5680(class_3222 arg) {
		return true;
	}

	public class_238 method_5829() {
		return this.field_6005;
	}

	@Environment(EnvType.CLIENT)
	public class_238 method_5830() {
		return this.method_5829();
	}

	protected class_238 method_20343(class_4050 arg) {
		class_4048 lv = this.method_18377(arg);
		float f = lv.field_18067 / 2.0F;
		class_243 lv2 = new class_243(this.field_5987 - (double)f, this.field_6010, this.field_6035 - (double)f);
		class_243 lv3 = new class_243(this.field_5987 + (double)f, this.field_6010 + (double)lv.field_18068, this.field_6035 + (double)f);
		return new class_238(lv2, lv3);
	}

	public void method_5857(class_238 arg) {
		this.field_6005 = arg;
	}

	protected float method_18378(class_4050 arg, class_4048 arg2) {
		return arg2.field_18068 * 0.85F;
	}

	@Environment(EnvType.CLIENT)
	public float method_18381(class_4050 arg) {
		return this.method_18378(arg, this.method_18377(arg));
	}

	public final float method_5751() {
		return this.field_18066;
	}

	public boolean method_5758(int i, class_1799 arg) {
		return false;
	}

	@Override
	public void method_9203(class_2561 arg) {
	}

	public class_2338 method_5704() {
		return new class_2338(this);
	}

	public class_243 method_5812() {
		return new class_243(this.field_5987, this.field_6010, this.field_6035);
	}

	public class_1937 method_5770() {
		return this.field_6002;
	}

	@Nullable
	public MinecraftServer method_5682() {
		return this.field_6002.method_8503();
	}

	public class_1269 method_5664(class_1657 arg, class_243 arg2, class_1268 arg3) {
		return class_1269.field_5811;
	}

	public boolean method_5659() {
		return false;
	}

	protected void method_5723(class_1309 arg, class_1297 arg2) {
		if (arg2 instanceof class_1309) {
			class_1890.method_8210((class_1309)arg2, arg);
		}

		class_1890.method_8213(arg, arg2);
	}

	public void method_5837(class_3222 arg) {
	}

	public void method_5742(class_3222 arg) {
	}

	public float method_5832(class_2470 arg) {
		float f = class_3532.method_15393(this.field_6031);
		switch (arg) {
			case field_11464:
				return f + 180.0F;
			case field_11465:
				return f + 270.0F;
			case field_11463:
				return f + 90.0F;
			default:
				return f;
		}
	}

	public float method_5763(class_2415 arg) {
		float f = class_3532.method_15393(this.field_6031);
		switch (arg) {
			case field_11300:
				return -f;
			case field_11301:
				return 180.0F - f;
			default:
				return f;
		}
	}

	public boolean method_5833() {
		return false;
	}

	public boolean method_5754() {
		boolean bl = this.field_5966;
		this.field_5966 = false;
		return bl;
	}

	@Nullable
	public class_1297 method_5642() {
		return null;
	}

	public List<class_1297> method_5685() {
		return (List<class_1297>)(this.field_5979.isEmpty() ? Collections.emptyList() : Lists.<class_1297>newArrayList(this.field_5979));
	}

	public boolean method_5626(class_1297 arg) {
		for (class_1297 lv : this.method_5685()) {
			if (lv.equals(arg)) {
				return true;
			}
		}

		return false;
	}

	public boolean method_5703(Class<? extends class_1297> class_) {
		for (class_1297 lv : this.method_5685()) {
			if (class_.isAssignableFrom(lv.getClass())) {
				return true;
			}
		}

		return false;
	}

	public Collection<class_1297> method_5736() {
		Set<class_1297> set = Sets.<class_1297>newHashSet();

		for (class_1297 lv : this.method_5685()) {
			set.add(lv);
			lv.method_5868(false, set);
		}

		return set;
	}

	public boolean method_5817() {
		Set<class_1297> set = Sets.<class_1297>newHashSet();
		this.method_5868(true, set);
		return set.size() == 1;
	}

	private void method_5868(boolean bl, Set<class_1297> set) {
		for (class_1297 lv : this.method_5685()) {
			if (!bl || class_3222.class.isAssignableFrom(lv.getClass())) {
				set.add(lv);
			}

			lv.method_5868(bl, set);
		}
	}

	public class_1297 method_5668() {
		class_1297 lv = this;

		while (lv.method_5765()) {
			lv = lv.method_5854();
		}

		return lv;
	}

	public boolean method_5794(class_1297 arg) {
		return this.method_5668() == arg.method_5668();
	}

	public boolean method_5821(class_1297 arg) {
		for (class_1297 lv : this.method_5685()) {
			if (lv.equals(arg)) {
				return true;
			}

			if (lv.method_5821(arg)) {
				return true;
			}
		}

		return false;
	}

	public boolean method_5787() {
		class_1297 lv = this.method_5642();
		return lv instanceof class_1657 ? ((class_1657)lv).method_7340() : !this.field_6002.field_9236;
	}

	@Nullable
	public class_1297 method_5854() {
		return this.field_6034;
	}

	public class_3619 method_5657() {
		return class_3619.field_15974;
	}

	public class_3419 method_5634() {
		return class_3419.field_15254;
	}

	protected int method_5676() {
		return 1;
	}

	public class_2168 method_5671() {
		return new class_2168(
			this,
			new class_243(this.field_5987, this.field_6010, this.field_6035),
			this.method_5802(),
			this.field_6002 instanceof class_3218 ? (class_3218)this.field_6002 : null,
			this.method_5691(),
			this.method_5477().getString(),
			this.method_5476(),
			this.field_6002.method_8503(),
			this
		);
	}

	protected int method_5691() {
		return 0;
	}

	public boolean method_5687(int i) {
		return this.method_5691() >= i;
	}

	@Override
	public boolean method_9200() {
		return this.field_6002.method_8450().method_8355(class_1928.field_19400);
	}

	@Override
	public boolean method_9202() {
		return true;
	}

	@Override
	public boolean method_9201() {
		return true;
	}

	public void method_5702(class_2183.class_2184 arg, class_243 arg2) {
		class_243 lv = arg.method_9302(this);
		double d = arg2.field_1352 - lv.field_1352;
		double e = arg2.field_1351 - lv.field_1351;
		double f = arg2.field_1350 - lv.field_1350;
		double g = (double)class_3532.method_15368(d * d + f * f);
		this.field_5965 = class_3532.method_15393((float)(-(class_3532.method_15349(e, g) * 180.0F / (float)Math.PI)));
		this.field_6031 = class_3532.method_15393((float)(class_3532.method_15349(f, d) * 180.0F / (float)Math.PI) - 90.0F);
		this.method_5847(this.field_6031);
		this.field_6004 = this.field_5965;
		this.field_5982 = this.field_6031;
	}

	public boolean method_5692(class_3494<class_3611> arg) {
		class_238 lv = this.method_5829().method_1011(0.001);
		int i = class_3532.method_15357(lv.field_1323);
		int j = class_3532.method_15384(lv.field_1320);
		int k = class_3532.method_15357(lv.field_1322);
		int l = class_3532.method_15384(lv.field_1325);
		int m = class_3532.method_15357(lv.field_1321);
		int n = class_3532.method_15384(lv.field_1324);
		if (!this.field_6002.method_8627(i, k, m, j, l, n)) {
			return false;
		} else {
			double d = 0.0;
			boolean bl = this.method_5675();
			boolean bl2 = false;
			class_243 lv2 = class_243.field_1353;
			int o = 0;

			try (class_2338.class_2340 lv3 = class_2338.class_2340.method_10109()) {
				for (int p = i; p < j; p++) {
					for (int q = k; q < l; q++) {
						for (int r = m; r < n; r++) {
							lv3.method_10113(p, q, r);
							class_3610 lv4 = this.field_6002.method_8316(lv3);
							if (lv4.method_15767(arg)) {
								double e = (double)((float)q + lv4.method_15763(this.field_6002, lv3));
								if (e >= lv.field_1322) {
									bl2 = true;
									d = Math.max(e - lv.field_1322, d);
									if (bl) {
										class_243 lv5 = lv4.method_15758(this.field_6002, lv3);
										if (d < 0.4) {
											lv5 = lv5.method_1021(d);
										}

										lv2 = lv2.method_1019(lv5);
										o++;
									}
								}
							}
						}
					}
				}
			}

			if (lv2.method_1033() > 0.0) {
				if (o > 0) {
					lv2 = lv2.method_1021(1.0 / (double)o);
				}

				if (!(this instanceof class_1657)) {
					lv2 = lv2.method_1029();
				}

				this.method_18799(this.method_18798().method_1019(lv2.method_1021(0.014)));
			}

			this.field_5964 = d;
			return bl2;
		}
	}

	public double method_5861() {
		return this.field_5964;
	}

	public final float method_17681() {
		return this.field_18065.field_18067;
	}

	public final float method_17682() {
		return this.field_18065.field_18068;
	}

	public abstract class_2596<?> method_18002();

	public class_4048 method_18377(class_4050 arg) {
		return this.field_5961.method_18386();
	}

	public class_243 method_19538() {
		return new class_243(this.field_5987, this.field_6010, this.field_6035);
	}

	public class_243 method_18798() {
		return this.field_18276;
	}

	public void method_18799(class_243 arg) {
		this.field_18276 = arg;
	}

	public void method_18800(double d, double e, double f) {
		this.method_18799(new class_243(d, e, f));
	}
}
