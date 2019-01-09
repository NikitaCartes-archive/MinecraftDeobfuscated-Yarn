package net.minecraft;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_1937 implements class_1920, class_1924, class_1936, AutoCloseable {
	protected static final Logger field_9224 = LogManager.getLogger();
	private static final class_2350[] field_9233 = class_2350.values();
	public final List<class_1297> field_9240 = Lists.<class_1297>newArrayList();
	protected final List<class_1297> field_9227 = Lists.<class_1297>newArrayList();
	public final List<class_2586> field_9231 = Lists.<class_2586>newArrayList();
	public final List<class_2586> field_9246 = Lists.<class_2586>newArrayList();
	private final List<class_2586> field_9241 = Lists.<class_2586>newArrayList();
	private final List<class_2586> field_9250 = Lists.<class_2586>newArrayList();
	public final List<class_1657> field_9228 = Lists.<class_1657>newArrayList();
	public final List<class_1297> field_9237 = Lists.<class_1297>newArrayList();
	protected final class_3525<class_1297> field_9225 = new class_3525<>();
	private final long field_9245 = 16777215L;
	private final Thread field_17086;
	private int field_9226;
	protected int field_9256 = new Random().nextInt();
	protected final int field_9238 = 1013904223;
	protected float field_9253;
	protected float field_9235;
	protected float field_9251;
	protected float field_9234;
	private int field_9242;
	public final Random field_9229 = new Random();
	public final class_2869 field_9247;
	protected final class_1411 field_9230 = new class_1411();
	protected final List<class_1938> field_9252 = Lists.<class_1938>newArrayList(this.field_9230);
	protected final class_2802 field_9248;
	protected final class_30 field_9243;
	protected final class_31 field_9232;
	@Nullable
	private final class_37 field_9239;
	protected class_1418 field_9254;
	protected class_3767 field_16642;
	private final class_3695 field_16316;
	public final boolean field_9236;
	private boolean field_9249;
	private final class_2784 field_9223;

	protected class_1937(
		class_30 arg, @Nullable class_37 arg2, class_31 arg3, class_2874 arg4, BiFunction<class_1937, class_2869, class_2802> biFunction, class_3695 arg5, boolean bl
	) {
		this.field_9243 = arg;
		this.field_9239 = arg2;
		this.field_16316 = arg5;
		this.field_9232 = arg3;
		this.field_9247 = arg4.method_12487(this);
		this.field_9248 = (class_2802)biFunction.apply(this, this.field_9247);
		this.field_9236 = bl;
		this.field_9223 = this.field_9247.method_12463();
		this.field_17086 = Thread.currentThread();
	}

	@Override
	public class_1959 method_8310(class_2338 arg) {
		class_2802 lv = this.method_8398();
		class_2818 lv2 = lv.method_12126(arg.method_10263() >> 4, arg.method_10260() >> 4, false);
		if (lv2 != null) {
			return lv2.method_16552(arg);
		} else {
			class_2794<?> lv3 = this.method_8398().method_12129();
			return lv3 == null ? class_1972.field_9451 : lv3.method_12098().method_8758(arg);
		}
	}

	public void method_8414(class_1940 arg) {
		this.field_9232.method_223(true);
	}

	@Override
	public boolean method_8608() {
		return this.field_9236;
	}

	@Nullable
	public MinecraftServer method_8503() {
		return null;
	}

	@Environment(EnvType.CLIENT)
	public void method_8513() {
		this.method_8554(new class_2338(8, 64, 8));
	}

	public class_2680 method_8495(class_2338 arg) {
		class_2338 lv = new class_2338(arg.method_10263(), this.method_8615(), arg.method_10260());

		while (!this.method_8623(lv.method_10084())) {
			lv = lv.method_10084();
		}

		return this.method_8320(lv);
	}

	public static boolean method_8558(class_2338 arg) {
		return !method_8518(arg)
			&& arg.method_10263() >= -30000000
			&& arg.method_10260() >= -30000000
			&& arg.method_10263() < 30000000
			&& arg.method_10260() < 30000000;
	}

	public static boolean method_8518(class_2338 arg) {
		return method_8476(arg.method_10264());
	}

	public static boolean method_8476(int i) {
		return i < 0 || i >= 256;
	}

	public class_2818 method_8500(class_2338 arg) {
		return this.method_8497(arg.method_10263() >> 4, arg.method_10260() >> 4);
	}

	public class_2818 method_8497(int i, int j) {
		return (class_2818)this.method_16956(i, j, class_2806.field_12803);
	}

	@Override
	public class_2791 method_8402(int i, int j, class_2806 arg, boolean bl) {
		class_2791 lv = this.field_9248.method_12121(i, j, arg, bl);
		if (lv == null && bl) {
			throw new IllegalStateException("Should always be able to create a chunk!");
		} else {
			return lv;
		}
	}

	@Override
	public boolean method_8652(class_2338 arg, class_2680 arg2, int i) {
		if (method_8518(arg)) {
			return false;
		} else if (!this.field_9236 && this.field_9232.method_153() == class_1942.field_9266) {
			return false;
		} else {
			class_2818 lv = this.method_8500(arg);
			class_2248 lv2 = arg2.method_11614();
			class_2680 lv3 = lv.method_12010(arg, arg2, (i & 64) != 0);
			if (lv3 == null) {
				return false;
			} else {
				class_2680 lv4 = this.method_8320(arg);
				if (lv4 != lv3
					&& (lv4.method_11581(this, arg) != lv3.method_11581(this, arg) || lv4.method_11630() != lv3.method_11630() || lv4.method_16386() || lv3.method_16386())) {
					this.field_16316.method_15396("queueCheckLight");
					this.method_8398().method_12130().method_15559(arg);
					this.field_16316.method_15407();
				}

				if (lv4 == arg2) {
					if (lv3 != lv4) {
						this.method_16109(arg);
					}

					if ((i & 2) != 0
						&& (!this.field_9236 || (i & 4) == 0)
						&& (this.field_9236 || lv.method_12225() != null && lv.method_12225().method_14014(class_3193.class_3194.field_13875))) {
						this.method_8413(arg, lv3, arg2, i);
					}

					if (!this.field_9236 && (i & 1) != 0) {
						this.method_8408(arg, lv3.method_11614());
						if (arg2.method_11584()) {
							this.method_8455(arg, lv2);
						}
					}

					if ((i & 16) == 0) {
						int j = i & -2;
						lv3.method_11637(this, arg, j);
						arg2.method_11635(this, arg, j);
						arg2.method_11637(this, arg, j);
					}
				}

				return true;
			}
		}
	}

	@Override
	public boolean method_8650(class_2338 arg) {
		class_3610 lv = this.method_8316(arg);
		return this.method_8652(arg, lv.method_15759(), 3);
	}

	@Override
	public boolean method_8651(class_2338 arg, boolean bl) {
		class_2680 lv = this.method_8320(arg);
		if (lv.method_11588()) {
			return false;
		} else {
			class_3610 lv2 = this.method_8316(arg);
			this.method_8535(2001, arg, class_2248.method_9507(lv));
			if (bl) {
				class_2586 lv3 = lv.method_11614().method_9570() ? this.method_8321(arg) : null;
				class_2248.method_9610(lv, this, arg, lv3);
			}

			return this.method_8652(arg, lv2.method_15759(), 3);
		}
	}

	public boolean method_8501(class_2338 arg, class_2680 arg2) {
		return this.method_8652(arg, arg2, 3);
	}

	public void method_8413(class_2338 arg, class_2680 arg2, class_2680 arg3, int i) {
		for (int j = 0; j < this.field_9252.size(); j++) {
			((class_1938)this.field_9252.get(j)).method_8570(this, arg, arg2, arg3, i);
		}
	}

	@Override
	public void method_8408(class_2338 arg, class_2248 arg2) {
		if (this.field_9232.method_153() != class_1942.field_9266) {
			this.method_8452(arg, arg2);
		}
	}

	public void method_16109(class_2338 arg) {
		for (class_1938 lv : this.field_9252) {
			lv.method_16111(arg.method_10263(), arg.method_10264(), arg.method_10260(), arg.method_10263(), arg.method_10264(), arg.method_10260());
		}
	}

	@Environment(EnvType.CLIENT)
	public void method_16108(int i, int j, int k) {
		for (class_1938 lv : this.field_9252) {
			lv.method_16110(i, j, k);
		}
	}

	public void method_8452(class_2338 arg, class_2248 arg2) {
		this.method_8492(arg.method_10067(), arg2, arg);
		this.method_8492(arg.method_10078(), arg2, arg);
		this.method_8492(arg.method_10074(), arg2, arg);
		this.method_8492(arg.method_10084(), arg2, arg);
		this.method_8492(arg.method_10095(), arg2, arg);
		this.method_8492(arg.method_10072(), arg2, arg);
	}

	public void method_8508(class_2338 arg, class_2248 arg2, class_2350 arg3) {
		if (arg3 != class_2350.field_11039) {
			this.method_8492(arg.method_10067(), arg2, arg);
		}

		if (arg3 != class_2350.field_11034) {
			this.method_8492(arg.method_10078(), arg2, arg);
		}

		if (arg3 != class_2350.field_11033) {
			this.method_8492(arg.method_10074(), arg2, arg);
		}

		if (arg3 != class_2350.field_11036) {
			this.method_8492(arg.method_10084(), arg2, arg);
		}

		if (arg3 != class_2350.field_11043) {
			this.method_8492(arg.method_10095(), arg2, arg);
		}

		if (arg3 != class_2350.field_11035) {
			this.method_8492(arg.method_10072(), arg2, arg);
		}
	}

	public void method_8492(class_2338 arg, class_2248 arg2, class_2338 arg3) {
		if (!this.field_9236) {
			class_2680 lv = this.method_8320(arg);

			try {
				lv.method_11622(this, arg, arg2, arg3);
			} catch (Throwable var8) {
				class_128 lv2 = class_128.method_560(var8, "Exception while updating neighbours");
				class_129 lv3 = lv2.method_562("Block being updated");
				lv3.method_577("Source block type", () -> {
					try {
						return String.format("ID #%s (%s // %s)", class_2378.field_11146.method_10221(arg2), arg2.method_9539(), arg2.getClass().getCanonicalName());
					} catch (Throwable var2) {
						return "ID #" + class_2378.field_11146.method_10221(arg2);
					}
				});
				class_129.method_586(lv3, arg, lv);
				throw new class_148(lv2);
			}
		}
	}

	@Override
	public int method_8624(class_2338 arg, int i) {
		if (arg.method_10263() < -30000000 || arg.method_10260() < -30000000 || arg.method_10263() >= 30000000 || arg.method_10260() >= 30000000) {
			return 15;
		} else if (arg.method_10264() < 0) {
			return 0;
		} else {
			if (arg.method_10264() >= 256) {
				arg = new class_2338(arg.method_10263(), 255, arg.method_10260());
			}

			return this.method_8500(arg).method_12233(arg, i);
		}
	}

	@Override
	public int method_8589(class_2902.class_2903 arg, int i, int j) {
		int k;
		if (i >= -30000000 && j >= -30000000 && i < 30000000 && j < 30000000) {
			if (this.method_8393(i >> 4, j >> 4)) {
				k = this.method_8497(i >> 4, j >> 4).method_12005(arg, i & 15, j & 15) + 1;
			} else {
				k = 0;
			}
		} else {
			k = this.method_8615() + 1;
		}

		return k;
	}

	@Override
	public int method_8314(class_1944 arg, class_2338 arg2) {
		return this.method_8398().method_12130().method_15562(arg).method_15543(arg2);
	}

	@Override
	public class_2680 method_8320(class_2338 arg) {
		if (method_8518(arg)) {
			return class_2246.field_10243.method_9564();
		} else {
			class_2818 lv = this.method_8497(arg.method_10263() >> 4, arg.method_10260() >> 4);
			return lv.method_8320(arg);
		}
	}

	@Override
	public class_3610 method_8316(class_2338 arg) {
		if (method_8518(arg)) {
			return class_3612.field_15906.method_15785();
		} else {
			class_2818 lv = this.method_8500(arg);
			return lv.method_8316(arg);
		}
	}

	public boolean method_8530() {
		return this.field_9226 < 4;
	}

	@Nullable
	public class_239 method_8549(class_243 arg, class_243 arg2) {
		return this.method_8531(arg, arg2, class_242.field_1348, false, false);
	}

	@Nullable
	public class_239 method_8418(class_243 arg, class_243 arg2, class_242 arg3) {
		return this.method_8531(arg, arg2, arg3, false, false);
	}

	@Nullable
	public class_239 method_8531(class_243 arg, class_243 arg2, class_242 arg3, boolean bl, boolean bl2) {
		double d = arg.field_1352;
		double e = arg.field_1351;
		double f = arg.field_1350;
		if (Double.isNaN(d) || Double.isNaN(e) || Double.isNaN(f)) {
			return null;
		} else if (!Double.isNaN(arg2.field_1352) && !Double.isNaN(arg2.field_1351) && !Double.isNaN(arg2.field_1350)) {
			int i = class_3532.method_15357(arg2.field_1352);
			int j = class_3532.method_15357(arg2.field_1351);
			int k = class_3532.method_15357(arg2.field_1350);
			int l = class_3532.method_15357(d);
			int m = class_3532.method_15357(e);
			int n = class_3532.method_15357(f);
			class_2338 lv = new class_2338(l, m, n);
			class_2680 lv2 = this.method_8320(lv);
			class_3610 lv3 = this.method_8316(lv);
			if (!bl || !lv2.method_11628(this, lv).method_1110()) {
				boolean bl3 = lv2.method_11614().method_9532(lv2);
				boolean bl4 = arg3.field_1346.test(lv3);
				if (bl3 || bl4) {
					class_239 lv4 = null;
					if (bl3) {
						lv4 = class_2248.method_9509(lv2, this, lv, arg, arg2);
					}

					if (lv4 == null && bl4) {
						lv4 = class_259.method_1081(0.0, 0.0, 0.0, 1.0, (double)lv3.method_15763(), 1.0).method_1092(arg, arg2, lv);
					}

					if (lv4 != null) {
						return lv4;
					}
				}
			}

			class_239 lv5 = null;
			int o = 200;

			while (o-- >= 0) {
				if (Double.isNaN(d) || Double.isNaN(e) || Double.isNaN(f)) {
					return null;
				}

				if (l == i && m == j && n == k) {
					return bl2 ? lv5 : null;
				}

				boolean bl3 = true;
				boolean bl4 = true;
				boolean bl5 = true;
				double g = 999.0;
				double h = 999.0;
				double p = 999.0;
				if (i > l) {
					g = (double)l + 1.0;
				} else if (i < l) {
					g = (double)l + 0.0;
				} else {
					bl3 = false;
				}

				if (j > m) {
					h = (double)m + 1.0;
				} else if (j < m) {
					h = (double)m + 0.0;
				} else {
					bl4 = false;
				}

				if (k > n) {
					p = (double)n + 1.0;
				} else if (k < n) {
					p = (double)n + 0.0;
				} else {
					bl5 = false;
				}

				double q = 999.0;
				double r = 999.0;
				double s = 999.0;
				double t = arg2.field_1352 - d;
				double u = arg2.field_1351 - e;
				double v = arg2.field_1350 - f;
				if (bl3) {
					q = (g - d) / t;
				}

				if (bl4) {
					r = (h - e) / u;
				}

				if (bl5) {
					s = (p - f) / v;
				}

				if (q == -0.0) {
					q = -1.0E-4;
				}

				if (r == -0.0) {
					r = -1.0E-4;
				}

				if (s == -0.0) {
					s = -1.0E-4;
				}

				class_2350 lv6;
				if (q < r && q < s) {
					lv6 = i > l ? class_2350.field_11039 : class_2350.field_11034;
					d = g;
					e += u * q;
					f += v * q;
				} else if (r < s) {
					lv6 = j > m ? class_2350.field_11033 : class_2350.field_11036;
					d += t * r;
					e = h;
					f += v * r;
				} else {
					lv6 = k > n ? class_2350.field_11043 : class_2350.field_11035;
					d += t * s;
					e += u * s;
					f = p;
				}

				l = class_3532.method_15357(d) - (lv6 == class_2350.field_11034 ? 1 : 0);
				m = class_3532.method_15357(e) - (lv6 == class_2350.field_11036 ? 1 : 0);
				n = class_3532.method_15357(f) - (lv6 == class_2350.field_11035 ? 1 : 0);
				lv = new class_2338(l, m, n);
				class_2680 lv7 = this.method_8320(lv);
				class_3610 lv8 = this.method_8316(lv);
				if (!bl || lv7.method_11620() == class_3614.field_15919 || !lv7.method_11628(this, lv).method_1110()) {
					boolean bl6 = lv7.method_11614().method_9532(lv7);
					boolean bl7 = arg3.field_1346.test(lv8);
					if (!bl6 && !bl7) {
						lv5 = new class_239(class_239.class_240.field_1333, new class_243(d, e, f), lv6, lv);
					} else {
						class_239 lv9 = null;
						if (bl6) {
							lv9 = class_2248.method_9509(lv7, this, lv, arg, arg2);
						}

						if (lv9 == null && bl7) {
							lv9 = class_259.method_1081(0.0, 0.0, 0.0, 1.0, (double)lv8.method_15763(), 1.0).method_1092(arg, arg2, lv);
						}

						if (lv9 != null) {
							return lv9;
						}
					}
				}
			}

			return bl2 ? lv5 : null;
		} else {
			return null;
		}
	}

	@Override
	public void method_8396(@Nullable class_1657 arg, class_2338 arg2, class_3414 arg3, class_3419 arg4, float f, float g) {
		this.method_8465(arg, (double)arg2.method_10263() + 0.5, (double)arg2.method_10264() + 0.5, (double)arg2.method_10260() + 0.5, arg3, arg4, f, g);
	}

	public void method_8465(@Nullable class_1657 arg, double d, double e, double f, class_3414 arg2, class_3419 arg3, float g, float h) {
		for (int i = 0; i < this.field_9252.size(); i++) {
			((class_1938)this.field_9252.get(i)).method_8572(arg, arg2, arg3, d, e, f, g, h);
		}
	}

	public void method_8449(@Nullable class_1657 arg, class_1297 arg2, class_3414 arg3, class_3419 arg4, float f, float g) {
		for (int i = 0; i < this.field_9252.size(); i++) {
			((class_1938)this.field_9252.get(i)).method_8565(arg, arg3, arg4, arg2, f, g);
		}
	}

	public void method_8486(double d, double e, double f, class_3414 arg, class_3419 arg2, float g, float h, boolean bl) {
	}

	public void method_8432(class_2338 arg, @Nullable class_3414 arg2) {
		for (int i = 0; i < this.field_9252.size(); i++) {
			((class_1938)this.field_9252.get(i)).method_8562(arg2, arg);
		}
	}

	@Override
	public void method_8406(class_2394 arg, double d, double e, double f, double g, double h, double i) {
		for (int j = 0; j < this.field_9252.size(); j++) {
			((class_1938)this.field_9252.get(j)).method_8568(arg, arg.method_10295().method_10299(), d, e, f, g, h, i);
		}
	}

	@Environment(EnvType.CLIENT)
	public void method_8466(class_2394 arg, boolean bl, double d, double e, double f, double g, double h, double i) {
		for (int j = 0; j < this.field_9252.size(); j++) {
			((class_1938)this.field_9252.get(j)).method_8568(arg, arg.method_10295().method_10299() || bl, d, e, f, g, h, i);
		}
	}

	public void method_8494(class_2394 arg, double d, double e, double f, double g, double h, double i) {
		for (int j = 0; j < this.field_9252.size(); j++) {
			((class_1938)this.field_9252.get(j)).method_8563(arg, false, true, d, e, f, g, h, i);
		}
	}

	public void method_17452(class_2394 arg, boolean bl, double d, double e, double f, double g, double h, double i) {
		for (int j = 0; j < this.field_9252.size(); j++) {
			((class_1938)this.field_9252.get(j)).method_8563(arg, arg.method_10295().method_10299() || bl, true, d, e, f, g, h, i);
		}
	}

	public boolean method_8416(class_1297 arg) {
		this.field_9237.add(arg);
		return true;
	}

	@Override
	public boolean method_8649(class_1297 arg) {
		int i = class_3532.method_15357(arg.field_5987 / 16.0);
		int j = class_3532.method_15357(arg.field_6035 / 16.0);
		boolean bl = arg.field_5983;
		if (arg instanceof class_1657) {
			bl = true;
		}

		if (!bl && !this.method_8393(i, j)) {
			return false;
		} else {
			if (arg instanceof class_1657) {
				class_1657 lv = (class_1657)arg;
				this.field_9228.add(lv);
				this.method_8448();
			}

			this.method_8497(i, j).method_12002(arg);
			this.field_9240.add(arg);
			this.method_8485(arg);
			return true;
		}
	}

	protected void method_8485(class_1297 arg) {
		for (int i = 0; i < this.field_9252.size(); i++) {
			((class_1938)this.field_9252.get(i)).method_8561(arg);
		}
	}

	protected void method_8539(class_1297 arg) {
		for (int i = 0; i < this.field_9252.size(); i++) {
			((class_1938)this.field_9252.get(i)).method_8566(arg);
		}
	}

	public void method_8463(class_1297 arg) {
		if (arg.method_5782()) {
			arg.method_5772();
		}

		if (arg.method_5765()) {
			arg.method_5848();
		}

		arg.method_5650();
		if (arg instanceof class_1657) {
			this.field_9228.remove(arg);
			this.method_8448();
			this.method_8539(arg);
		}
	}

	public void method_8507(class_1297 arg) {
		arg.method_5633(false);
		arg.method_5650();
		if (arg instanceof class_1657) {
			this.field_9228.remove(arg);
			this.method_8448();
		}

		int i = arg.field_6024;
		int j = arg.field_5980;
		if (arg.field_6016 && this.method_8393(i, j)) {
			this.method_8497(i, j).method_12203(arg);
		}

		this.field_9240.remove(arg);
		this.method_8539(arg);
	}

	public void method_8521(class_1938 arg) {
		this.field_9252.add(arg);
	}

	@Environment(EnvType.CLIENT)
	public void method_8415(class_1938 arg) {
		this.field_9252.remove(arg);
	}

	public int method_8533(float f) {
		float g = this.method_8400(f);
		float h = 1.0F - (class_3532.method_15362(g * (float) (Math.PI * 2)) * 2.0F + 0.5F);
		h = class_3532.method_15363(h, 0.0F, 1.0F);
		h = 1.0F - h;
		h = (float)((double)h * (1.0 - (double)(this.method_8430(f) * 5.0F) / 16.0));
		h = (float)((double)h * (1.0 - (double)(this.method_8478(f) * 5.0F) / 16.0));
		h = 1.0F - h;
		return (int)(h * 11.0F);
	}

	@Environment(EnvType.CLIENT)
	public float method_8453(float f) {
		float g = this.method_8400(f);
		float h = 1.0F - (class_3532.method_15362(g * (float) (Math.PI * 2)) * 2.0F + 0.2F);
		h = class_3532.method_15363(h, 0.0F, 1.0F);
		h = 1.0F - h;
		h = (float)((double)h * (1.0 - (double)(this.method_8430(f) * 5.0F) / 16.0));
		h = (float)((double)h * (1.0 - (double)(this.method_8478(f) * 5.0F) / 16.0));
		return h * 0.8F + 0.2F;
	}

	@Environment(EnvType.CLIENT)
	public class_243 method_8548(class_1297 arg, float f) {
		float g = this.method_8400(f);
		float h = class_3532.method_15362(g * (float) (Math.PI * 2)) * 2.0F + 0.5F;
		h = class_3532.method_15363(h, 0.0F, 1.0F);
		int i = class_3532.method_15357(arg.field_5987);
		int j = class_3532.method_15357(arg.field_6010);
		int k = class_3532.method_15357(arg.field_6035);
		class_2338 lv = new class_2338(i, j, k);
		class_1959 lv2 = this.method_8310(lv);
		float l = lv2.method_8707(lv);
		int m = lv2.method_8697(l);
		float n = (float)(m >> 16 & 0xFF) / 255.0F;
		float o = (float)(m >> 8 & 0xFF) / 255.0F;
		float p = (float)(m & 0xFF) / 255.0F;
		n *= h;
		o *= h;
		p *= h;
		float q = this.method_8430(f);
		if (q > 0.0F) {
			float r = (n * 0.3F + o * 0.59F + p * 0.11F) * 0.6F;
			float s = 1.0F - q * 0.75F;
			n = n * s + r * (1.0F - s);
			o = o * s + r * (1.0F - s);
			p = p * s + r * (1.0F - s);
		}

		float r = this.method_8478(f);
		if (r > 0.0F) {
			float s = (n * 0.3F + o * 0.59F + p * 0.11F) * 0.2F;
			float t = 1.0F - r * 0.75F;
			n = n * t + s * (1.0F - t);
			o = o * t + s * (1.0F - t);
			p = p * t + s * (1.0F - t);
		}

		if (this.field_9242 > 0) {
			float s = (float)this.field_9242 - f;
			if (s > 1.0F) {
				s = 1.0F;
			}

			s *= 0.45F;
			n = n * (1.0F - s) + 0.8F * s;
			o = o * (1.0F - s) + 0.8F * s;
			p = p * (1.0F - s) + 1.0F * s;
		}

		return new class_243((double)n, (double)o, (double)p);
	}

	public float method_8442(float f) {
		float g = this.method_8400(f);
		return g * (float) (Math.PI * 2);
	}

	@Environment(EnvType.CLIENT)
	public class_243 method_8423(float f) {
		float g = this.method_8400(f);
		float h = class_3532.method_15362(g * (float) (Math.PI * 2)) * 2.0F + 0.5F;
		h = class_3532.method_15363(h, 0.0F, 1.0F);
		float i = 1.0F;
		float j = 1.0F;
		float k = 1.0F;
		float l = this.method_8430(f);
		if (l > 0.0F) {
			float m = (i * 0.3F + j * 0.59F + k * 0.11F) * 0.6F;
			float n = 1.0F - l * 0.95F;
			i = i * n + m * (1.0F - n);
			j = j * n + m * (1.0F - n);
			k = k * n + m * (1.0F - n);
		}

		i *= h * 0.9F + 0.1F;
		j *= h * 0.9F + 0.1F;
		k *= h * 0.85F + 0.15F;
		float m = this.method_8478(f);
		if (m > 0.0F) {
			float n = (i * 0.3F + j * 0.59F + k * 0.11F) * 0.2F;
			float o = 1.0F - m * 0.95F;
			i = i * o + n * (1.0F - o);
			j = j * o + n * (1.0F - o);
			k = k * o + n * (1.0F - o);
		}

		return new class_243((double)i, (double)j, (double)k);
	}

	@Environment(EnvType.CLIENT)
	public class_243 method_8464(float f) {
		float g = this.method_8400(f);
		return this.field_9247.method_12445(g, f);
	}

	@Environment(EnvType.CLIENT)
	public float method_8550(float f) {
		float g = this.method_8400(f);
		float h = 1.0F - (class_3532.method_15362(g * (float) (Math.PI * 2)) * 2.0F + 0.25F);
		h = class_3532.method_15363(h, 0.0F, 1.0F);
		return h * h * 0.5F;
	}

	public void method_8429() {
		this.field_16316.method_15396("entities");
		this.field_16316.method_15396("global");

		for (int i = 0; i < this.field_9237.size(); i++) {
			class_1297 lv = (class_1297)this.field_9237.get(i);

			try {
				lv.field_6012++;
				lv.method_5773();
			} catch (Throwable var9) {
				class_128 lv2 = class_128.method_560(var9, "Ticking entity");
				class_129 lv3 = lv2.method_562("Entity being ticked");
				if (lv == null) {
					lv3.method_578("Entity", "~~NULL~~");
				} else {
					lv.method_5819(lv3);
				}

				throw new class_148(lv2);
			}

			if (lv.field_5988) {
				this.field_9237.remove(i--);
			}
		}

		this.field_16316.method_15405("remove");
		this.field_9240.removeAll(this.field_9227);

		for (int i = 0; i < this.field_9227.size(); i++) {
			class_1297 lv = (class_1297)this.field_9227.get(i);
			int j = lv.field_6024;
			int k = lv.field_5980;
			if (lv.field_6016 && this.method_8393(j, k)) {
				this.method_8497(j, k).method_12203(lv);
			}
		}

		for (int ix = 0; ix < this.field_9227.size(); ix++) {
			this.method_8539((class_1297)this.field_9227.get(ix));
		}

		this.field_9227.clear();
		this.method_8541();
		this.field_16316.method_15405("regular");

		for (int ix = 0; ix < this.field_9240.size(); ix++) {
			class_1297 lv = (class_1297)this.field_9240.get(ix);
			class_1297 lv4 = lv.method_5854();
			if (lv4 != null) {
				if (!lv4.field_5988 && lv4.method_5626(lv)) {
					continue;
				}

				lv.method_5848();
			}

			this.field_16316.method_15396("tick");
			if (!lv.field_5988 && !(lv instanceof class_3222)) {
				try {
					this.method_8552(lv);
				} catch (Throwable var8) {
					class_128 lv5 = class_128.method_560(var8, "Ticking entity");
					class_129 lv6 = lv5.method_562("Entity being ticked");
					lv.method_5819(lv6);
					throw new class_148(lv5);
				}
			}

			this.field_16316.method_15407();
			this.field_16316.method_15396("remove");
			if (lv.field_5988) {
				int k = lv.field_6024;
				int l = lv.field_5980;
				if (lv.field_6016 && this.method_8393(k, l)) {
					this.method_8497(k, l).method_12203(lv);
				}

				this.field_9240.remove(ix--);
				this.method_8539(lv);
			}

			this.field_16316.method_15407();
		}

		this.field_16316.method_15405("blockEntities");
		if (!this.field_9250.isEmpty()) {
			this.field_9246.removeAll(this.field_9250);
			this.field_9231.removeAll(this.field_9250);
			this.field_9250.clear();
		}

		this.field_9249 = true;
		Iterator<class_2586> iterator = this.field_9246.iterator();

		while (iterator.hasNext()) {
			class_2586 lv7 = (class_2586)iterator.next();
			if (!lv7.method_11015() && lv7.method_11002()) {
				class_2338 lv8 = lv7.method_11016();
				if (this.method_8591(lv8) && this.field_9223.method_11952(lv8)) {
					try {
						this.field_16316.method_15400(() -> String.valueOf(class_2591.method_11033(lv7.method_11017())));
						((class_3000)lv7).method_16896();
						this.field_16316.method_15407();
					} catch (Throwable var7) {
						class_128 lv5 = class_128.method_560(var7, "Ticking block entity");
						class_129 lv6 = lv5.method_562("Block entity being ticked");
						lv7.method_11003(lv6);
						throw new class_148(lv5);
					}
				}
			}

			if (lv7.method_11015()) {
				iterator.remove();
				this.field_9231.remove(lv7);
				if (this.method_8591(lv7.method_11016())) {
					this.method_8500(lv7.method_11016()).method_12041(lv7.method_11016());
				}
			}
		}

		this.field_9249 = false;
		this.field_16316.method_15405("pendingBlockEntities");
		if (!this.field_9241.isEmpty()) {
			for (int m = 0; m < this.field_9241.size(); m++) {
				class_2586 lv9 = (class_2586)this.field_9241.get(m);
				if (!lv9.method_11015()) {
					if (!this.field_9231.contains(lv9)) {
						this.method_8438(lv9);
					}

					if (this.method_8591(lv9.method_11016())) {
						class_2818 lv10 = this.method_8500(lv9.method_11016());
						class_2680 lv11 = lv10.method_8320(lv9.method_11016());
						lv10.method_12007(lv9.method_11016(), lv9);
						this.method_8413(lv9.method_11016(), lv11, lv11, 3);
					}
				}
			}

			this.field_9241.clear();
		}

		this.field_16316.method_15407();
		this.field_16316.method_15407();
	}

	protected void method_8541() {
	}

	public boolean method_8438(class_2586 arg) {
		boolean bl = this.field_9231.add(arg);
		if (bl && arg instanceof class_3000) {
			this.field_9246.add(arg);
		}

		if (this.field_9236) {
			class_2338 lv = arg.method_11016();
			class_2680 lv2 = this.method_8320(lv);
			this.method_8413(lv, lv2, lv2, 2);
		}

		return bl;
	}

	public void method_8447(Collection<class_2586> collection) {
		if (this.field_9249) {
			this.field_9241.addAll(collection);
		} else {
			for (class_2586 lv : collection) {
				this.method_8438(lv);
			}
		}
	}

	public void method_8552(class_1297 arg) {
		this.method_8553(arg, true);
	}

	public void method_8553(class_1297 arg, boolean bl) {
		if (arg instanceof class_1657 || !bl || this.method_8398().method_12125(arg)) {
			arg.field_6038 = arg.field_5987;
			arg.field_5971 = arg.field_6010;
			arg.field_5989 = arg.field_6035;
			arg.field_5982 = arg.field_6031;
			arg.field_6004 = arg.field_5965;
			if (bl && arg.field_6016) {
				arg.field_6012++;
				if (arg.method_5765()) {
					arg.method_5842();
				} else {
					this.field_16316.method_15400(() -> class_2378.field_11145.method_10221(arg.method_5864()).toString());
					arg.method_5773();
					this.field_16316.method_15407();
				}
			}

			this.field_16316.method_15396("chunkCheck");
			if (Double.isNaN(arg.field_5987) || Double.isInfinite(arg.field_5987)) {
				arg.field_5987 = arg.field_6038;
			}

			if (Double.isNaN(arg.field_6010) || Double.isInfinite(arg.field_6010)) {
				arg.field_6010 = arg.field_5971;
			}

			if (Double.isNaN(arg.field_6035) || Double.isInfinite(arg.field_6035)) {
				arg.field_6035 = arg.field_5989;
			}

			if (Double.isNaN((double)arg.field_5965) || Double.isInfinite((double)arg.field_5965)) {
				arg.field_5965 = arg.field_6004;
			}

			if (Double.isNaN((double)arg.field_6031) || Double.isInfinite((double)arg.field_6031)) {
				arg.field_6031 = arg.field_5982;
			}

			int i = class_3532.method_15357(arg.field_5987 / 16.0);
			int j = class_3532.method_15357(arg.field_6010 / 16.0);
			int k = class_3532.method_15357(arg.field_6035 / 16.0);
			if (!arg.field_6016 || arg.field_6024 != i || arg.field_5959 != j || arg.field_5980 != k) {
				if (arg.field_6016 && this.method_8393(arg.field_6024, arg.field_5980)) {
					this.method_8497(arg.field_6024, arg.field_5980).method_12219(arg, arg.field_5959);
				}

				if (!arg.method_5754() && !this.method_8393(i, k)) {
					arg.field_6016 = false;
				} else {
					this.method_8497(i, k).method_12002(arg);
				}
			}

			this.field_16316.method_15407();
			if (bl && arg.field_6016) {
				for (class_1297 lv : arg.method_5685()) {
					if (!lv.field_5988 && lv.method_5854() == arg) {
						this.method_8552(lv);
					} else {
						lv.method_5848();
					}
				}
			}
		}
	}

	@Override
	public boolean method_8611(@Nullable class_1297 arg, class_265 arg2) {
		if (arg2.method_1110()) {
			return true;
		} else {
			List<class_1297> list = this.method_8335(null, arg2.method_1107());

			for (int i = 0; i < list.size(); i++) {
				class_1297 lv = (class_1297)list.get(i);
				if (!lv.field_5988
					&& lv.field_6033
					&& lv != arg
					&& (arg == null || !lv.method_5794(arg))
					&& class_259.method_1074(arg2, class_259.method_1078(lv.method_5829()), class_247.field_16896)) {
					return false;
				}
			}

			return true;
		}
	}

	public boolean method_8534(class_238 arg) {
		int i = class_3532.method_15357(arg.field_1323);
		int j = class_3532.method_15384(arg.field_1320);
		int k = class_3532.method_15357(arg.field_1322);
		int l = class_3532.method_15384(arg.field_1325);
		int m = class_3532.method_15357(arg.field_1321);
		int n = class_3532.method_15384(arg.field_1324);

		try (class_2338.class_2340 lv = class_2338.class_2340.method_10109()) {
			for (int o = i; o < j; o++) {
				for (int p = k; p < l; p++) {
					for (int q = m; q < n; q++) {
						class_2680 lv2 = this.method_8320(lv.method_10113(o, p, q));
						if (!lv2.method_11588()) {
							return true;
						}
					}
				}
			}

			return false;
		}
	}

	public boolean method_8425(class_238 arg) {
		int i = class_3532.method_15357(arg.field_1323);
		int j = class_3532.method_15384(arg.field_1320);
		int k = class_3532.method_15357(arg.field_1322);
		int l = class_3532.method_15384(arg.field_1325);
		int m = class_3532.method_15357(arg.field_1321);
		int n = class_3532.method_15384(arg.field_1324);
		if (this.method_8627(i, k, m, j, l, n)) {
			try (class_2338.class_2340 lv = class_2338.class_2340.method_10109()) {
				for (int o = i; o < j; o++) {
					for (int p = k; p < l; p++) {
						for (int q = m; q < n; q++) {
							class_2248 lv2 = this.method_8320(lv.method_10113(o, p, q)).method_11614();
							if (lv2 == class_2246.field_10036 || lv2 == class_2246.field_10164) {
								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}

	@Nullable
	public class_2680 method_8475(class_238 arg, class_2248 arg2) {
		int i = class_3532.method_15357(arg.field_1323);
		int j = class_3532.method_15384(arg.field_1320);
		int k = class_3532.method_15357(arg.field_1322);
		int l = class_3532.method_15384(arg.field_1325);
		int m = class_3532.method_15357(arg.field_1321);
		int n = class_3532.method_15384(arg.field_1324);
		if (this.method_8627(i, k, m, j, l, n)) {
			try (class_2338.class_2340 lv = class_2338.class_2340.method_10109()) {
				for (int o = i; o < j; o++) {
					for (int p = k; p < l; p++) {
						for (int q = m; q < n; q++) {
							class_2680 lv2 = this.method_8320(lv.method_10113(o, p, q));
							if (lv2.method_11614() == arg2) {
								return lv2;
							}
						}
					}
				}

				return null;
			}
		} else {
			return null;
		}
	}

	public boolean method_8422(class_238 arg, class_3614 arg2) {
		int i = class_3532.method_15357(arg.field_1323);
		int j = class_3532.method_15384(arg.field_1320);
		int k = class_3532.method_15357(arg.field_1322);
		int l = class_3532.method_15384(arg.field_1325);
		int m = class_3532.method_15357(arg.field_1321);
		int n = class_3532.method_15384(arg.field_1324);
		class_2710 lv = class_2710.method_11746(arg2);
		class_2338.class_2339 lv2 = new class_2338.class_2339();

		for (int o = i; o < j; o++) {
			for (int p = k; p < l; p++) {
				for (int q = m; q < n; q++) {
					if (lv.method_11745(this.method_8320(lv2.method_10103(o, p, q)))) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public class_1927 method_8437(@Nullable class_1297 arg, double d, double e, double f, float g, boolean bl) {
		return this.method_8454(arg, null, d, e, f, g, false, bl);
	}

	public class_1927 method_8537(@Nullable class_1297 arg, double d, double e, double f, float g, boolean bl, boolean bl2) {
		return this.method_8454(arg, null, d, e, f, g, bl, bl2);
	}

	public class_1927 method_8454(@Nullable class_1297 arg, @Nullable class_1282 arg2, double d, double e, double f, float g, boolean bl, boolean bl2) {
		class_1927 lv = new class_1927(this, arg, d, e, f, g, bl, bl2);
		if (arg2 != null) {
			lv.method_8345(arg2);
		}

		lv.method_8348();
		lv.method_8350(true);
		return lv;
	}

	public float method_8542(class_243 arg, class_238 arg2) {
		double d = 1.0 / ((arg2.field_1320 - arg2.field_1323) * 2.0 + 1.0);
		double e = 1.0 / ((arg2.field_1325 - arg2.field_1322) * 2.0 + 1.0);
		double f = 1.0 / ((arg2.field_1324 - arg2.field_1321) * 2.0 + 1.0);
		double g = (1.0 - Math.floor(1.0 / d) * d) / 2.0;
		double h = (1.0 - Math.floor(1.0 / f) * f) / 2.0;
		if (!(d < 0.0) && !(e < 0.0) && !(f < 0.0)) {
			int i = 0;
			int j = 0;

			for (float k = 0.0F; k <= 1.0F; k = (float)((double)k + d)) {
				for (float l = 0.0F; l <= 1.0F; l = (float)((double)l + e)) {
					for (float m = 0.0F; m <= 1.0F; m = (float)((double)m + f)) {
						double n = class_3532.method_16436((double)k, arg2.field_1323, arg2.field_1320);
						double o = class_3532.method_16436((double)l, arg2.field_1322, arg2.field_1325);
						double p = class_3532.method_16436((double)m, arg2.field_1321, arg2.field_1324);
						if (this.method_8549(new class_243(n + g, o, p + h), arg) == null) {
							i++;
						}

						j++;
					}
				}
			}

			return (float)i / (float)j;
		} else {
			return 0.0F;
		}
	}

	public boolean method_8506(@Nullable class_1657 arg, class_2338 arg2, class_2350 arg3) {
		arg2 = arg2.method_10093(arg3);
		if (this.method_8320(arg2).method_11614() == class_2246.field_10036) {
			this.method_8444(arg, 1009, arg2, 0);
			this.method_8650(arg2);
			return true;
		} else {
			return false;
		}
	}

	@Environment(EnvType.CLIENT)
	public String method_8431() {
		return "All: " + this.field_9240.size();
	}

	@Environment(EnvType.CLIENT)
	public String method_8457() {
		return this.field_9248.method_12122();
	}

	@Nullable
	@Override
	public class_2586 method_8321(class_2338 arg) {
		if (method_8518(arg)) {
			return null;
		} else if (!this.field_9236 && Thread.currentThread() != this.field_17086) {
			return null;
		} else {
			class_2586 lv = null;
			if (this.field_9249) {
				lv = this.method_8426(arg);
			}

			if (lv == null) {
				lv = this.method_8500(arg).method_12201(arg, class_2818.class_2819.field_12860);
			}

			if (lv == null) {
				lv = this.method_8426(arg);
			}

			return lv;
		}
	}

	@Nullable
	private class_2586 method_8426(class_2338 arg) {
		for (int i = 0; i < this.field_9241.size(); i++) {
			class_2586 lv = (class_2586)this.field_9241.get(i);
			if (!lv.method_11015() && lv.method_11016().equals(arg)) {
				return lv;
			}
		}

		return null;
	}

	public void method_8526(class_2338 arg, @Nullable class_2586 arg2) {
		if (!method_8518(arg)) {
			if (arg2 != null && !arg2.method_11015()) {
				if (this.field_9249) {
					arg2.method_10998(arg);
					Iterator<class_2586> iterator = this.field_9241.iterator();

					while (iterator.hasNext()) {
						class_2586 lv = (class_2586)iterator.next();
						if (lv.method_11016().equals(arg)) {
							lv.method_11012();
							iterator.remove();
						}
					}

					this.field_9241.add(arg2);
				} else {
					this.method_8500(arg).method_12007(arg, arg2);
					this.method_8438(arg2);
				}
			}
		}
	}

	public void method_8544(class_2338 arg) {
		class_2586 lv = this.method_8321(arg);
		if (lv != null && this.field_9249) {
			lv.method_11012();
			this.field_9241.remove(lv);
		} else {
			if (lv != null) {
				this.field_9241.remove(lv);
				this.field_9231.remove(lv);
				this.field_9246.remove(lv);
			}

			this.method_8500(arg).method_12041(arg);
		}
	}

	public void method_8556(class_2586 arg) {
		this.field_9250.add(arg);
	}

	public boolean method_8504(class_2338 arg) {
		return class_2248.method_9614(this.method_8320(arg).method_11628(this, arg));
	}

	public boolean method_8477(class_2338 arg) {
		return method_8518(arg) ? false : this.field_9248.method_12123(arg.method_10263() >> 4, arg.method_10260() >> 4);
	}

	public boolean method_8515(class_2338 arg) {
		return this.method_8477(arg) && this.method_8320(arg).method_11631(this, arg);
	}

	public void method_8451() {
		int i = this.method_8533(1.0F);
		if (i != this.field_9226) {
			this.field_9226 = i;
		}
	}

	public void method_8424(boolean bl, boolean bl2) {
		this.method_8398().method_12128(bl, bl2);
	}

	public void method_8441(BooleanSupplier booleanSupplier) {
		this.field_9223.method_11982();
		this.method_8511();
	}

	public void method_8462(class_2818 arg, int i) {
		class_1923 lv = arg.method_12004();
		boolean bl = this.method_8419();
		int j = lv.method_8326();
		int k = lv.method_8328();
		this.field_16316.method_15396("thunder");
		if (bl && this.method_8546() && this.field_9229.nextInt(100000) == 0) {
			class_2338 lv2 = this.method_8481(this.method_8536(j, 0, k, 15));
			if (this.method_8520(lv2)) {
				class_1266 lv3 = this.method_8404(lv2);
				boolean bl2 = this.method_8450().method_8355("doMobSpawning") && this.field_9229.nextDouble() < (double)lv3.method_5457() * 0.01;
				if (bl2) {
					class_1506 lv4 = new class_1506(this);
					lv4.method_6813(true);
					lv4.method_5614(0);
					lv4.method_5814((double)lv2.method_10263(), (double)lv2.method_10264(), (double)lv2.method_10260());
					this.method_8649(lv4);
				}

				this.method_8416(new class_1538(this, (double)lv2.method_10263() + 0.5, (double)lv2.method_10264(), (double)lv2.method_10260() + 0.5, bl2));
			}
		}

		this.field_16316.method_15405("iceandsnow");
		if (this.field_9229.nextInt(16) == 0) {
			class_2338 lv2 = this.method_8598(class_2902.class_2903.field_13197, this.method_8536(j, 0, k, 15));
			class_2338 lv5 = lv2.method_10074();
			class_1959 lv6 = this.method_8310(lv2);
			if (lv6.method_8705(this, lv5)) {
				this.method_8501(lv5, class_2246.field_10295.method_9564());
			}

			if (bl && lv6.method_8696(this, lv2)) {
				this.method_8501(lv2, class_2246.field_10477.method_9564());
			}

			if (bl && this.method_8310(lv5).method_8694() == class_1959.class_1963.field_9382) {
				this.method_8320(lv5).method_11614().method_9504(this, lv5);
			}
		}

		this.field_16316.method_15405("tickBlocks");
		if (i > 0) {
			for (class_2826 lv7 : arg.method_12006()) {
				if (lv7 != class_2818.field_12852 && lv7.method_12262()) {
					int l = lv7.method_12259();

					for (int m = 0; m < i; m++) {
						class_2338 lv8 = this.method_8536(j, l, k, 15);
						this.field_16316.method_15396("randomTick");
						class_2680 lv9 = lv7.method_12254(lv8.method_10263() - j, lv8.method_10264() - l, lv8.method_10260() - k);
						if (lv9.method_11616()) {
							lv9.method_11624(this, lv8, this.field_9229);
						}

						class_3610 lv10 = lv7.method_12255(lv8.method_10263() - j, lv8.method_10264() - l, lv8.method_10260() - k);
						if (lv10.method_15773()) {
							lv10.method_15757(this, lv8, this.field_9229);
						}

						this.field_16316.method_15407();
					}
				}
			}
		}

		this.field_16316.method_15407();
	}

	protected class_2338 method_8481(class_2338 arg) {
		class_2338 lv = this.method_8598(class_2902.class_2903.field_13197, arg);
		class_238 lv2 = new class_238(lv, new class_2338(lv.method_10263(), this.method_8322(), lv.method_10260())).method_1014(3.0);
		List<class_1309> list = this.method_8390(class_1309.class, lv2, argx -> argx != null && argx.method_5805() && this.method_8311(argx.method_5704()));
		if (!list.isEmpty()) {
			return ((class_1309)list.get(this.field_9229.nextInt(list.size()))).method_5704();
		} else {
			if (lv.method_10264() == -1) {
				lv = lv.method_10086(2);
			}

			return lv;
		}
	}

	protected void method_8543() {
		if (this.field_9232.method_156()) {
			this.field_9235 = 1.0F;
			if (this.field_9232.method_203()) {
				this.field_9234 = 1.0F;
			}
		}
	}

	public void close() {
		this.field_9248.close();
	}

	protected void method_8511() {
		if (this.field_9247.method_12451()) {
			if (!this.field_9236) {
				boolean bl = this.method_8450().method_8355("doWeatherCycle");
				if (bl) {
					int i = this.field_9232.method_155();
					if (i > 0) {
						this.field_9232.method_167(--i);
						this.field_9232.method_173(this.field_9232.method_203() ? 1 : 2);
						this.field_9232.method_164(this.field_9232.method_156() ? 1 : 2);
					}

					int j = this.field_9232.method_145();
					if (j <= 0) {
						if (this.field_9232.method_203()) {
							this.field_9232.method_173(this.field_9229.nextInt(12000) + 3600);
						} else {
							this.field_9232.method_173(this.field_9229.nextInt(168000) + 12000);
						}
					} else {
						this.field_9232.method_173(--j);
						if (j <= 0) {
							this.field_9232.method_147(!this.field_9232.method_203());
						}
					}

					int k = this.field_9232.method_190();
					if (k <= 0) {
						if (this.field_9232.method_156()) {
							this.field_9232.method_164(this.field_9229.nextInt(12000) + 12000);
						} else {
							this.field_9232.method_164(this.field_9229.nextInt(168000) + 12000);
						}
					} else {
						this.field_9232.method_164(--k);
						if (k <= 0) {
							this.field_9232.method_157(!this.field_9232.method_156());
						}
					}
				}

				this.field_9251 = this.field_9234;
				if (this.field_9232.method_203()) {
					this.field_9234 = (float)((double)this.field_9234 + 0.01);
				} else {
					this.field_9234 = (float)((double)this.field_9234 - 0.01);
				}

				this.field_9234 = class_3532.method_15363(this.field_9234, 0.0F, 1.0F);
				this.field_9253 = this.field_9235;
				if (this.field_9232.method_156()) {
					this.field_9235 = (float)((double)this.field_9235 + 0.01);
				} else {
					this.field_9235 = (float)((double)this.field_9235 - 0.01);
				}

				this.field_9235 = class_3532.method_15363(this.field_9235, 0.0F, 1.0F);
			}
		}
	}

	@Override
	public Stream<class_265> method_8600(@Nullable class_1297 arg, class_265 arg2, class_265 arg3, Set<class_1297> set) {
		Stream<class_265> stream = class_1936.super.method_8600(arg, arg2, arg3, set);
		return arg == null ? stream : Stream.concat(stream, this.method_8334(arg, arg2, set));
	}

	@Override
	public List<class_1297> method_8333(@Nullable class_1297 arg, class_238 arg2, @Nullable Predicate<? super class_1297> predicate) {
		List<class_1297> list = Lists.<class_1297>newArrayList();
		int i = class_3532.method_15357((arg2.field_1323 - 2.0) / 16.0);
		int j = class_3532.method_15357((arg2.field_1320 + 2.0) / 16.0);
		int k = class_3532.method_15357((arg2.field_1321 - 2.0) / 16.0);
		int l = class_3532.method_15357((arg2.field_1324 + 2.0) / 16.0);

		for (int m = i; m <= j; m++) {
			for (int n = k; n <= l; n++) {
				if (this.method_8393(m, n)) {
					this.method_8497(m, n).method_12205(arg, arg2, list, predicate);
				}
			}
		}

		return list;
	}

	public <T extends class_1297> List<T> method_8490(Class<? extends T> class_, Predicate<? super T> predicate) {
		List<T> list = Lists.<T>newArrayList();

		for (class_1297 lv : this.field_9240) {
			if (class_.isAssignableFrom(lv.getClass()) && predicate.test(lv)) {
				list.add(lv);
			}
		}

		return list;
	}

	public <T extends class_1297> List<T> method_8498(Class<? extends T> class_, Predicate<? super T> predicate) {
		List<T> list = Lists.<T>newArrayList();

		for (class_1297 lv : this.field_9228) {
			if (class_.isAssignableFrom(lv.getClass()) && predicate.test(lv)) {
				list.add(lv);
			}
		}

		return list;
	}

	@Override
	public <T extends class_1297> List<T> method_8390(Class<? extends T> class_, class_238 arg, @Nullable Predicate<? super T> predicate) {
		int i = class_3532.method_15357((arg.field_1323 - 2.0) / 16.0);
		int j = class_3532.method_15384((arg.field_1320 + 2.0) / 16.0);
		int k = class_3532.method_15357((arg.field_1321 - 2.0) / 16.0);
		int l = class_3532.method_15384((arg.field_1324 + 2.0) / 16.0);
		List<T> list = Lists.<T>newArrayList();

		for (int m = i; m < j; m++) {
			for (int n = k; n < l; n++) {
				class_2818 lv = this.method_8398().method_12126(m, n, false);
				if (lv != null) {
					lv.method_12210(class_, arg, list, predicate);
				}
			}
		}

		return list;
	}

	@Nullable
	public <T extends class_1297> T method_8472(Class<? extends T> class_, class_238 arg, T arg2) {
		List<T> list = this.method_8403(class_, arg);
		T lv = null;
		double d = Double.MAX_VALUE;

		for (int i = 0; i < list.size(); i++) {
			T lv2 = (T)list.get(i);
			if (lv2 != arg2 && class_1301.field_6155.test(lv2)) {
				double e = arg2.method_5858(lv2);
				if (!(e > d)) {
					lv = lv2;
					d = e;
				}
			}
		}

		return lv;
	}

	@Nullable
	public class_1297 method_8469(int i) {
		return this.field_9225.method_15316(i);
	}

	@Nullable
	public class_1297 method_14190(UUID uUID) {
		return (class_1297)this.field_9240.stream().filter(arg -> arg.method_5667().equals(uUID)).findAny().orElse(null);
	}

	@Environment(EnvType.CLIENT)
	public int method_8446() {
		return this.field_9240.size();
	}

	public void method_8524(class_2338 arg, class_2586 arg2) {
		if (this.method_8591(arg)) {
			this.method_8500(arg).method_12220();
		}
	}

	public Object2IntMap<class_1311> method_17450() {
		Object2IntMap<class_1311> object2IntMap = new Object2IntOpenHashMap<>();

		for (class_1297 lv : this.field_9240) {
			if ((!(lv instanceof class_1308) || !((class_1308)lv).method_5947()) && lv instanceof class_1298) {
				class_1311 lv2 = class_1311.method_17350(lv);
				object2IntMap.computeInt(lv2, (arg, integer) -> 1 + (integer == null ? 0 : integer));
			}
		}

		return object2IntMap;
	}

	public void method_8555(Stream<class_1297> stream) {
		stream.forEach(arg -> {
			this.field_9240.add(arg);
			this.method_8485(arg);
		});
	}

	public void method_8512(Collection<class_1297> collection) {
		this.field_9227.addAll(collection);
	}

	@Override
	public int method_8615() {
		return 63;
	}

	@Override
	public class_1937 method_8410() {
		return this;
	}

	@Override
	public int method_8596(class_2338 arg, class_2350 arg2) {
		return this.method_8320(arg).method_11577(this, arg, arg2);
	}

	public class_1942 method_8527() {
		return this.field_9232.method_153();
	}

	public int method_8488(class_2338 arg) {
		int i = 0;
		i = Math.max(i, this.method_8596(arg.method_10074(), class_2350.field_11033));
		if (i >= 15) {
			return i;
		} else {
			i = Math.max(i, this.method_8596(arg.method_10084(), class_2350.field_11036));
			if (i >= 15) {
				return i;
			} else {
				i = Math.max(i, this.method_8596(arg.method_10095(), class_2350.field_11043));
				if (i >= 15) {
					return i;
				} else {
					i = Math.max(i, this.method_8596(arg.method_10072(), class_2350.field_11035));
					if (i >= 15) {
						return i;
					} else {
						i = Math.max(i, this.method_8596(arg.method_10067(), class_2350.field_11039));
						if (i >= 15) {
							return i;
						} else {
							i = Math.max(i, this.method_8596(arg.method_10078(), class_2350.field_11034));
							return i >= 15 ? i : i;
						}
					}
				}
			}
		}
	}

	public boolean method_8459(class_2338 arg, class_2350 arg2) {
		return this.method_8499(arg, arg2) > 0;
	}

	public int method_8499(class_2338 arg, class_2350 arg2) {
		class_2680 lv = this.method_8320(arg);
		return lv.method_11621(this, arg) ? this.method_8488(arg) : lv.method_11597(this, arg, arg2);
	}

	public boolean method_8479(class_2338 arg) {
		if (this.method_8499(arg.method_10074(), class_2350.field_11033) > 0) {
			return true;
		} else if (this.method_8499(arg.method_10084(), class_2350.field_11036) > 0) {
			return true;
		} else if (this.method_8499(arg.method_10095(), class_2350.field_11043) > 0) {
			return true;
		} else if (this.method_8499(arg.method_10072(), class_2350.field_11035) > 0) {
			return true;
		} else {
			return this.method_8499(arg.method_10067(), class_2350.field_11039) > 0 ? true : this.method_8499(arg.method_10078(), class_2350.field_11034) > 0;
		}
	}

	public int method_8482(class_2338 arg) {
		int i = 0;

		for (class_2350 lv : field_9233) {
			int j = this.method_8499(arg.method_10093(lv), lv);
			if (j >= 15) {
				return 15;
			}

			if (j > i) {
				i = j;
			}
		}

		return i;
	}

	@Nullable
	@Override
	public class_1657 method_8604(double d, double e, double f, double g, Predicate<class_1297> predicate) {
		double h = -1.0;
		class_1657 lv = null;

		for (int i = 0; i < this.field_9228.size(); i++) {
			class_1657 lv2 = (class_1657)this.field_9228.get(i);
			if (predicate.test(lv2)) {
				double j = lv2.method_5649(d, e, f);
				if ((g < 0.0 || j < g * g) && (h == -1.0 || j < h)) {
					h = j;
					lv = lv2;
				}
			}
		}

		return lv;
	}

	public boolean method_8528(double d, double e, double f, double g) {
		for (int i = 0; i < this.field_9228.size(); i++) {
			class_1657 lv = (class_1657)this.field_9228.get(i);
			if (class_1301.field_6155.test(lv)) {
				double h = lv.method_5649(d, e, f);
				if (g < 0.0 || h < g * g) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean method_8523(double d, double e, double f, double g) {
		for (class_1657 lv : this.field_9228) {
			if (class_1301.field_6155.test(lv) && class_1301.field_6157.test(lv)) {
				double h = lv.method_5649(d, e, f);
				if (g < 0.0 || h < g * g) {
					return true;
				}
			}
		}

		return false;
	}

	@Nullable
	public class_1657 method_8491(double d, double e, double f) {
		double g = -1.0;
		class_1657 lv = null;

		for (int i = 0; i < this.field_9228.size(); i++) {
			class_1657 lv2 = (class_1657)this.field_9228.get(i);
			if (class_1301.field_6155.test(lv2)) {
				double h = lv2.method_5649(d, lv2.field_6010, e);
				if ((f < 0.0 || h < f * f) && (g == -1.0 || h < g)) {
					g = h;
					lv = lv2;
				}
			}
		}

		return lv;
	}

	@Nullable
	public class_1657 method_8460(class_1297 arg, double d, double e) {
		return this.method_8439(arg.field_5987, arg.field_6010, arg.field_6035, d, e, null, null);
	}

	@Nullable
	public class_1657 method_8483(class_2338 arg, double d, double e) {
		return this.method_8439(
			(double)((float)arg.method_10263() + 0.5F), (double)((float)arg.method_10264() + 0.5F), (double)((float)arg.method_10260() + 0.5F), d, e, null, null
		);
	}

	@Nullable
	public class_1657 method_8439(
		double d, double e, double f, double g, double h, @Nullable Function<class_1657, Double> function, @Nullable Predicate<class_1657> predicate
	) {
		double i = -1.0;
		class_1657 lv = null;

		for (int j = 0; j < this.field_9228.size(); j++) {
			class_1657 lv2 = (class_1657)this.field_9228.get(j);
			if (!lv2.field_7503.field_7480 && lv2.method_5805() && !lv2.method_7325() && (predicate == null || predicate.test(lv2))) {
				double k = lv2.method_5649(d, lv2.field_6010, f);
				double l = g;
				if (lv2.method_5715()) {
					l = g * 0.8F;
				}

				if (lv2.method_5767()) {
					float m = lv2.method_7309();
					if (m < 0.1F) {
						m = 0.1F;
					}

					l *= (double)(0.7F * m);
				}

				if (function != null) {
					l *= MoreObjects.firstNonNull(function.apply(lv2), 1.0);
				}

				if ((h < 0.0 || Math.abs(lv2.field_6010 - e) < h * h) && (g < 0.0 || k < l * l) && (i == -1.0 || k < i)) {
					i = k;
					lv = lv2;
				}
			}
		}

		return lv;
	}

	@Nullable
	public class_1657 method_8434(String string) {
		for (int i = 0; i < this.field_9228.size(); i++) {
			class_1657 lv = (class_1657)this.field_9228.get(i);
			if (string.equals(lv.method_5477().getString())) {
				return lv;
			}
		}

		return null;
	}

	@Nullable
	public class_1657 method_8420(UUID uUID) {
		for (int i = 0; i < this.field_9228.size(); i++) {
			class_1657 lv = (class_1657)this.field_9228.get(i);
			if (uUID.equals(lv.method_5667())) {
				return lv;
			}
		}

		return null;
	}

	@Environment(EnvType.CLIENT)
	public void method_8525() {
	}

	public void method_8468() throws class_1939 {
		this.field_9243.method_137();
	}

	public void method_8516(long l) {
		this.field_9232.method_177(l);
	}

	@Override
	public long method_8412() {
		return this.field_9232.method_184();
	}

	public long method_8510() {
		return this.field_9232.method_188();
	}

	public long method_8532() {
		return this.field_9232.method_217();
	}

	public void method_8435(long l) {
		this.field_9232.method_165(l);
	}

	protected void method_8560() {
		this.method_8516(this.field_9232.method_188() + 1L);
		if (this.field_9232.method_146().method_8355("doDaylightCycle")) {
			this.method_8435(this.field_9232.method_217() + 1L);
		}
	}

	@Override
	public class_2338 method_8395() {
		class_2338 lv = new class_2338(this.field_9232.method_215(), this.field_9232.method_144(), this.field_9232.method_166());
		if (!this.method_8621().method_11952(lv)) {
			lv = this.method_8598(class_2902.class_2903.field_13197, new class_2338(this.method_8621().method_11964(), 0.0, this.method_8621().method_11980()));
		}

		return lv;
	}

	public void method_8554(class_2338 arg) {
		this.field_9232.method_187(arg);
	}

	@Environment(EnvType.CLIENT)
	public void method_8443(class_1297 arg) {
		int i = class_3532.method_15357(arg.field_5987 / 16.0);
		int j = class_3532.method_15357(arg.field_6035 / 16.0);
		int k = 2;

		for (int l = -2; l <= 2; l++) {
			for (int m = -2; m <= 2; m++) {
				this.method_8497(i + l, j + m);
			}
		}

		if (!this.field_9240.contains(arg)) {
			this.field_9240.add(arg);
		}
	}

	public boolean method_8505(class_1657 arg, class_2338 arg2) {
		return true;
	}

	public void method_8421(class_1297 arg, byte b) {
	}

	@Override
	public class_2802 method_8398() {
		return this.field_9248;
	}

	public void method_8427(class_2338 arg, class_2248 arg2, int i, int j) {
		this.method_8320(arg).method_11583(this, arg, i, j);
	}

	@Override
	public class_30 method_8411() {
		return this.field_9243;
	}

	@Override
	public class_31 method_8401() {
		return this.field_9232;
	}

	public class_1928 method_8450() {
		return this.field_9232.method_146();
	}

	public void method_8448() {
	}

	public float method_8478(float f) {
		return class_3532.method_16439(f, this.field_9251, this.field_9234) * this.method_8430(f);
	}

	@Environment(EnvType.CLIENT)
	public void method_8496(float f) {
		this.field_9251 = f;
		this.field_9234 = f;
	}

	public float method_8430(float f) {
		return class_3532.method_16439(f, this.field_9253, this.field_9235);
	}

	@Environment(EnvType.CLIENT)
	public void method_8519(float f) {
		this.field_9253 = f;
		this.field_9235 = f;
	}

	public boolean method_8546() {
		return this.field_9247.method_12451() && !this.field_9247.method_12467() ? (double)this.method_8478(1.0F) > 0.9 : false;
	}

	public boolean method_8419() {
		return (double)this.method_8430(1.0F) > 0.2;
	}

	public boolean method_8520(class_2338 arg) {
		if (!this.method_8419()) {
			return false;
		} else if (!this.method_8311(arg)) {
			return false;
		} else {
			return this.method_8598(class_2902.class_2903.field_13197, arg).method_10264() > arg.method_10264()
				? false
				: this.method_8310(arg).method_8694() == class_1959.class_1963.field_9382;
		}
	}

	public boolean method_8480(class_2338 arg) {
		class_1959 lv = this.method_8310(arg);
		return lv.method_8724();
	}

	@Nullable
	@Override
	public class_37 method_8646() {
		return this.field_9239;
	}

	public void method_8474(int i, class_2338 arg, int j) {
		for (int k = 0; k < this.field_9252.size(); k++) {
			((class_1938)this.field_9252.get(k)).method_8564(i, arg, j);
		}
	}

	public void method_8535(int i, class_2338 arg, int j) {
		this.method_8444(null, i, arg, j);
	}

	public void method_8444(@Nullable class_1657 arg, int i, class_2338 arg2, int j) {
		try {
			for (int k = 0; k < this.field_9252.size(); k++) {
				((class_1938)this.field_9252.get(k)).method_8567(arg, i, arg2, j);
			}
		} catch (Throwable var8) {
			class_128 lv = class_128.method_560(var8, "Playing level event");
			class_129 lv2 = lv.method_562("Level event being played");
			lv2.method_578("Block coordinates", class_129.method_582(arg2));
			lv2.method_578("Event source", arg);
			lv2.method_578("Event type", i);
			lv2.method_578("Event data", j);
			throw new class_148(lv);
		}
	}

	public int method_8456() {
		return this.field_9247.method_12467() ? 128 : 256;
	}

	@Environment(EnvType.CLIENT)
	public double method_8540() {
		return this.field_9232.method_153() == class_1942.field_9277 ? 0.0 : 63.0;
	}

	public class_129 method_8538(class_128 arg) {
		class_129 lv = arg.method_556("Affected level", 1);
		lv.method_578("Level name", this.field_9232 == null ? "????" : this.field_9232.method_150());
		lv.method_577("All players", () -> this.field_9228.size() + " total; " + this.field_9228);
		lv.method_577("Chunk stats", () -> this.field_9248.method_12122());

		try {
			this.field_9232.method_151(lv);
		} catch (Throwable var4) {
			lv.method_585("Level Data Unobtainable", var4);
		}

		return lv;
	}

	public void method_8517(int i, class_2338 arg, int j) {
		for (int k = 0; k < this.field_9252.size(); k++) {
			class_1938 lv = (class_1938)this.field_9252.get(k);
			lv.method_8569(i, arg, j);
		}
	}

	@Environment(EnvType.CLIENT)
	public void method_8547(double d, double e, double f, double g, double h, double i, @Nullable class_2487 arg) {
	}

	public abstract class_269 method_8428();

	public void method_8455(class_2338 arg, class_2248 arg2) {
		for (class_2350 lv : class_2350.class_2353.field_11062) {
			class_2338 lv2 = arg.method_10093(lv);
			if (this.method_8591(lv2)) {
				class_2680 lv3 = this.method_8320(lv2);
				if (lv3.method_11614() == class_2246.field_10377) {
					lv3.method_11622(this, lv2, arg2, arg);
				} else if (lv3.method_11621(this, lv2)) {
					lv2 = lv2.method_10093(lv);
					lv3 = this.method_8320(lv2);
					if (lv3.method_11614() == class_2246.field_10377) {
						lv3.method_11622(this, lv2, arg2, arg);
					}
				}
			}
		}
	}

	@Override
	public class_1266 method_8404(class_2338 arg) {
		long l = 0L;
		float f = 0.0F;
		if (this.method_8591(arg)) {
			f = this.method_8391();
			l = this.method_8500(arg).method_12033();
		}

		return new class_1266(this.method_8407(), this.method_8532(), l, f);
	}

	@Override
	public int method_8594() {
		return this.field_9226;
	}

	public void method_8417(int i) {
		this.field_9226 = i;
	}

	@Environment(EnvType.CLIENT)
	public int method_8529() {
		return this.field_9242;
	}

	public void method_8509(int i) {
		this.field_9242 = i;
	}

	public class_1418 method_8557() {
		return this.field_9254;
	}

	public class_3767 method_16542() {
		return this.field_16642;
	}

	@Override
	public class_2784 method_8621() {
		return this.field_9223;
	}

	public LongSet method_8440() {
		class_1932 lv = this.method_8648(this.field_9247.method_12460(), class_1932::new, "chunks");
		return (LongSet)(lv != null ? LongSets.unmodifiable(lv.method_8375()) : LongSets.EMPTY_SET);
	}

	public boolean method_8551() {
		class_1932 lv = this.method_8648(this.field_9247.method_12460(), class_1932::new, "chunks");
		return lv != null && !lv.method_8375().isEmpty();
	}

	public boolean method_8559(int i, int j) {
		class_1932 lv = this.method_8648(this.field_9247.method_12460(), class_1932::new, "chunks");
		return lv != null && lv.method_8375().contains(class_1923.method_8331(i, j));
	}

	public boolean method_8461(int i, int j, boolean bl) {
		String string = "chunks";
		class_1932 lv = this.method_8648(this.field_9247.method_12460(), class_1932::new, "chunks");
		if (lv == null) {
			lv = new class_1932("chunks");
			this.method_8647(this.field_9247.method_12460(), "chunks", lv);
		}

		class_1923 lv2 = new class_1923(i, j);
		long l = lv2.method_8324();
		boolean bl2;
		if (bl) {
			bl2 = lv.method_8375().add(l);
			if (bl2) {
				this.method_8497(i, j);
			}
		} else {
			bl2 = lv.method_8375().remove(l);
		}

		lv.method_78(bl2);
		if (bl2) {
			this.method_8398().method_12124(lv2, bl);
		}

		return bl2;
	}

	public void method_8522(class_2596<?> arg) {
		throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
	}

	@Nullable
	public class_2338 method_8487(String string, class_2338 arg, int i, boolean bl) {
		return null;
	}

	@Override
	public class_2869 method_8597() {
		return this.field_9247;
	}

	@Override
	public Random method_8409() {
		return this.field_9229;
	}

	@Override
	public boolean method_16358(class_2338 arg, Predicate<class_2680> predicate) {
		return predicate.test(this.method_8320(arg));
	}

	public abstract class_1863 method_8433();

	public abstract class_3505 method_8514();

	public class_2338 method_8536(int i, int j, int k, int l) {
		this.field_9256 = this.field_9256 * 3 + 1013904223;
		int m = this.field_9256 >> 2;
		return new class_2338(i + (m & 15), j + (m >> 16 & l), k + (m >> 8 & 15));
	}

	public boolean method_8458() {
		return false;
	}

	public class_3695 method_16107() {
		return this.field_16316;
	}

	@Override
	public class_2338 method_8598(class_2902.class_2903 arg, class_2338 arg2) {
		return new class_2338(arg2.method_10263(), this.method_8589(arg, arg2.method_10263(), arg2.method_10260()), arg2.method_10260());
	}
}
