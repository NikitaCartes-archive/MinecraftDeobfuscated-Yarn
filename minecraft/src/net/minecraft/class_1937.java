package net.minecraft;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_1937 implements class_1920, class_1936, AutoCloseable {
	protected static final Logger field_9224 = LogManager.getLogger();
	private static final class_2350[] field_9233 = class_2350.values();
	public final List<class_2586> field_9231 = Lists.<class_2586>newArrayList();
	public final List<class_2586> field_9246 = Lists.<class_2586>newArrayList();
	protected final List<class_2586> field_9241 = Lists.<class_2586>newArrayList();
	protected final List<class_2586> field_18139 = Lists.<class_2586>newArrayList();
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
	protected final class_2802 field_9248;
	protected final class_31 field_9232;
	private final class_3695 field_16316;
	public final boolean field_9236;
	protected boolean field_9249;
	private final class_2784 field_9223;

	protected class_1937(class_31 arg, class_2874 arg2, BiFunction<class_1937, class_2869, class_2802> biFunction, class_3695 arg3, boolean bl) {
		this.field_16316 = arg3;
		this.field_9232 = arg;
		this.field_9247 = arg2.method_12487(this);
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

					this.method_19282(arg, lv3, lv4);
				}

				return true;
			}
		}
	}

	public void method_19282(class_2338 arg, class_2680 arg2, class_2680 arg3) {
	}

	@Override
	public boolean method_8650(class_2338 arg, boolean bl) {
		class_3610 lv = this.method_8316(arg);
		return this.method_8652(arg, lv.method_15759(), 3 | (bl ? 64 : 0));
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

	public abstract void method_8413(class_2338 arg, class_2680 arg2, class_2680 arg3, int i);

	@Override
	public void method_8408(class_2338 arg, class_2248 arg2) {
		if (this.field_9232.method_153() != class_1942.field_9266) {
			this.method_8452(arg, arg2);
		}
	}

	public void method_16109(class_2338 arg) {
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
				lv.method_11622(this, arg, arg2, arg3, false);
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

	@Override
	public void method_8396(@Nullable class_1657 arg, class_2338 arg2, class_3414 arg3, class_3419 arg4, float f, float g) {
		this.method_8465(arg, (double)arg2.method_10263() + 0.5, (double)arg2.method_10264() + 0.5, (double)arg2.method_10260() + 0.5, arg3, arg4, f, g);
	}

	public abstract void method_8465(@Nullable class_1657 arg, double d, double e, double f, class_3414 arg2, class_3419 arg3, float g, float h);

	public abstract void method_8449(@Nullable class_1657 arg, class_1297 arg2, class_3414 arg3, class_3419 arg4, float f, float g);

	public void method_8486(double d, double e, double f, class_3414 arg, class_3419 arg2, float g, float h, boolean bl) {
	}

	@Override
	public void method_8406(class_2394 arg, double d, double e, double f, double g, double h, double i) {
	}

	@Environment(EnvType.CLIENT)
	public void method_8466(class_2394 arg, boolean bl, double d, double e, double f, double g, double h, double i) {
	}

	public void method_8494(class_2394 arg, double d, double e, double f, double g, double h, double i) {
	}

	public void method_17452(class_2394 arg, boolean bl, double d, double e, double f, double g, double h, double i) {
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
	public class_243 method_8548(class_2338 arg, float f) {
		float g = this.method_8400(f);
		float h = class_3532.method_15362(g * (float) (Math.PI * 2)) * 2.0F + 0.5F;
		h = class_3532.method_15363(h, 0.0F, 1.0F);
		class_1959 lv = this.method_8310(arg);
		float i = lv.method_8707(arg);
		int j = lv.method_8697(i);
		float k = (float)(j >> 16 & 0xFF) / 255.0F;
		float l = (float)(j >> 8 & 0xFF) / 255.0F;
		float m = (float)(j & 0xFF) / 255.0F;
		k *= h;
		l *= h;
		m *= h;
		float n = this.method_8430(f);
		if (n > 0.0F) {
			float o = (k * 0.3F + l * 0.59F + m * 0.11F) * 0.6F;
			float p = 1.0F - n * 0.75F;
			k = k * p + o * (1.0F - p);
			l = l * p + o * (1.0F - p);
			m = m * p + o * (1.0F - p);
		}

		float o = this.method_8478(f);
		if (o > 0.0F) {
			float p = (k * 0.3F + l * 0.59F + m * 0.11F) * 0.2F;
			float q = 1.0F - o * 0.75F;
			k = k * q + p * (1.0F - q);
			l = l * q + p * (1.0F - q);
			m = m * q + p * (1.0F - q);
		}

		if (this.field_9242 > 0) {
			float p = (float)this.field_9242 - f;
			if (p > 1.0F) {
				p = 1.0F;
			}

			p *= 0.45F;
			k = k * (1.0F - p) + 0.8F * p;
			l = l * (1.0F - p) + 0.8F * p;
			m = m * (1.0F - p) + 1.0F * p;
		}

		return new class_243((double)k, (double)l, (double)m);
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

	public void method_18471() {
		class_3695 lv = this.method_16107();
		lv.method_15396("blockEntities");
		if (!this.field_18139.isEmpty()) {
			this.field_9246.removeAll(this.field_18139);
			this.field_9231.removeAll(this.field_18139);
			this.field_18139.clear();
		}

		this.field_9249 = true;
		Iterator<class_2586> iterator = this.field_9246.iterator();

		while (iterator.hasNext()) {
			class_2586 lv2 = (class_2586)iterator.next();
			if (!lv2.method_11015() && lv2.method_11002()) {
				class_2338 lv3 = lv2.method_11016();
				if (this.method_8591(lv3) && this.method_8621().method_11952(lv3)) {
					try {
						lv.method_15400(() -> String.valueOf(class_2591.method_11033(lv2.method_11017())));
						((class_3000)lv2).method_16896();
						lv.method_15407();
					} catch (Throwable var8) {
						class_128 lv4 = class_128.method_560(var8, "Ticking block entity");
						class_129 lv5 = lv4.method_562("Block entity being ticked");
						lv2.method_11003(lv5);
						throw new class_148(lv4);
					}
				}
			}

			if (lv2.method_11015()) {
				iterator.remove();
				this.field_9231.remove(lv2);
				if (this.method_8591(lv2.method_11016())) {
					this.method_8500(lv2.method_11016()).method_12041(lv2.method_11016());
				}
			}
		}

		this.field_9249 = false;
		lv.method_15405("pendingBlockEntities");
		if (!this.field_9241.isEmpty()) {
			for (int i = 0; i < this.field_9241.size(); i++) {
				class_2586 lv6 = (class_2586)this.field_9241.get(i);
				if (!lv6.method_11015()) {
					if (!this.field_9231.contains(lv6)) {
						this.method_8438(lv6);
					}

					if (this.method_8591(lv6.method_11016())) {
						class_2818 lv7 = this.method_8500(lv6.method_11016());
						class_2680 lv8 = lv7.method_8320(lv6.method_11016());
						lv7.method_12007(lv6.method_11016(), lv6);
						this.method_8413(lv6.method_11016(), lv8, lv8, 3);
					}
				}
			}

			this.field_9241.clear();
		}

		lv.method_15407();
	}

	public void method_18472(Consumer<class_1297> consumer, class_1297 arg) {
		try {
			consumer.accept(arg);
		} catch (Throwable var6) {
			class_128 lv = class_128.method_560(var6, "Ticking entity");
			class_129 lv2 = lv.method_562("Entity being ticked");
			arg.method_5819(lv2);
			throw new class_148(lv);
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

	public class_1927 method_8437(@Nullable class_1297 arg, double d, double e, double f, float g, class_1927.class_4179 arg2) {
		return this.method_8454(arg, null, d, e, f, g, false, arg2);
	}

	public class_1927 method_8537(@Nullable class_1297 arg, double d, double e, double f, float g, boolean bl, class_1927.class_4179 arg2) {
		return this.method_8454(arg, null, d, e, f, g, bl, arg2);
	}

	public class_1927 method_8454(
		@Nullable class_1297 arg, @Nullable class_1282 arg2, double d, double e, double f, float g, boolean bl, class_1927.class_4179 arg3
	) {
		class_1927 lv = new class_1927(this, arg, d, e, f, g, bl, arg3);
		if (arg2 != null) {
			lv.method_8345(arg2);
		}

		lv.method_8348();
		lv.method_8350(true);
		return lv;
	}

	public boolean method_8506(@Nullable class_1657 arg, class_2338 arg2, class_2350 arg3) {
		arg2 = arg2.method_10093(arg3);
		if (this.method_8320(arg2).method_11614() == class_2246.field_10036) {
			this.method_8444(arg, 1009, arg2, 0);
			this.method_8650(arg2, false);
			return true;
		} else {
			return false;
		}
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

	public boolean method_8477(class_2338 arg) {
		return method_8518(arg) ? false : this.field_9248.method_12123(arg.method_10263() >> 4, arg.method_10260() >> 4);
	}

	public boolean method_8515(class_2338 arg, class_1297 arg2) {
		if (method_8518(arg)) {
			return false;
		} else {
			class_2791 lv = this.method_8402(arg.method_10263() >> 4, arg.method_10260() >> 4, class_2806.field_12803, false);
			return lv == null ? false : lv.method_8320(arg).method_11631(this, arg, arg2);
		}
	}

	public void method_8533() {
		double d = 1.0 - (double)(this.method_8430(1.0F) * 5.0F) / 16.0;
		double e = 1.0 - (double)(this.method_8478(1.0F) * 5.0F) / 16.0;
		double f = 0.5 + 2.0 * class_3532.method_15350((double)class_3532.method_15362(this.method_8400(1.0F) * (float) (Math.PI * 2)), -0.25, 0.25);
		this.field_9226 = (int)((1.0 - f * d * e) * 11.0);
	}

	public void method_8424(boolean bl, boolean bl2) {
		this.method_8398().method_12128(bl, bl2);
	}

	protected void method_8543() {
		if (this.field_9232.method_156()) {
			this.field_9235 = 1.0F;
			if (this.field_9232.method_203()) {
				this.field_9234 = 1.0F;
			}
		}
	}

	public void close() throws IOException {
		this.field_9248.close();
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
				class_2818 lv = this.method_8398().method_12126(m, n, false);
				if (lv != null) {
					lv.method_12205(arg, arg2, list, predicate);
				}
			}
		}

		return list;
	}

	public List<class_1297> method_18023(@Nullable class_1299<?> arg, class_238 arg2, Predicate<? super class_1297> predicate) {
		int i = class_3532.method_15357((arg2.field_1323 - 2.0) / 16.0);
		int j = class_3532.method_15384((arg2.field_1320 + 2.0) / 16.0);
		int k = class_3532.method_15357((arg2.field_1321 - 2.0) / 16.0);
		int l = class_3532.method_15384((arg2.field_1324 + 2.0) / 16.0);
		List<class_1297> list = Lists.<class_1297>newArrayList();

		for (int m = i; m < j; m++) {
			for (int n = k; n < l; n++) {
				class_2818 lv = this.method_8398().method_12126(m, n, false);
				if (lv != null) {
					lv.method_18029(arg, arg2, list, predicate);
				}
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
	public abstract class_1297 method_8469(int i);

	public void method_8524(class_2338 arg, class_2586 arg2) {
		if (this.method_8591(arg)) {
			this.method_8500(arg).method_12220();
		}
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

	@Environment(EnvType.CLIENT)
	public void method_8525() {
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
	public class_31 method_8401() {
		return this.field_9232;
	}

	public class_1928 method_8450() {
		return this.field_9232.method_146();
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
	public abstract class_22 method_17891(String string);

	public abstract void method_17890(class_22 arg);

	public abstract int method_17889();

	public void method_8474(int i, class_2338 arg, int j) {
	}

	public void method_8535(int i, class_2338 arg, int j) {
		this.method_8444(null, i, arg, j);
	}

	public abstract void method_8444(@Nullable class_1657 arg, int i, class_2338 arg2, int j);

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
		lv.method_577("All players", () -> this.method_18456().size() + " total; " + this.method_18456());
		lv.method_577("Chunk stats", () -> this.field_9248.method_12122());

		try {
			this.field_9232.method_151(lv);
		} catch (Throwable var4) {
			lv.method_585("Level Data Unobtainable", var4);
		}

		return lv;
	}

	public abstract void method_8517(int i, class_2338 arg, int j);

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
					lv3.method_11622(this, lv2, arg2, arg, false);
				} else if (lv3.method_11621(this, lv2)) {
					lv2 = lv2.method_10093(lv);
					lv3 = this.method_8320(lv2);
					if (lv3.method_11614() == class_2246.field_10377) {
						lv3.method_11622(this, lv2, arg2, arg, false);
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

	@Environment(EnvType.CLIENT)
	public int method_8529() {
		return this.field_9242;
	}

	public void method_8509(int i) {
		this.field_9242 = i;
	}

	@Override
	public class_2784 method_8621() {
		return this.field_9223;
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
