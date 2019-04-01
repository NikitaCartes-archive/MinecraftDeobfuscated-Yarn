package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_1959 {
	public static final Logger field_9330 = LogManager.getLogger();
	public static final Set<class_1959> field_9323 = Sets.<class_1959>newHashSet();
	public static final class_2361<class_1959> field_9328 = new class_2361<>();
	protected static final class_3543 field_9335 = new class_3543(new Random(1234L), 1);
	public static final class_3543 field_9324 = new class_3543(new Random(2345L), 1);
	@Nullable
	protected String field_9331;
	protected final float field_9343;
	protected final float field_9341;
	protected final float field_9339;
	protected final float field_9338;
	protected final int field_9342;
	protected final int field_9340;
	@Nullable
	protected final String field_9332;
	protected final class_3504<?> field_9336;
	protected final class_1959.class_1961 field_9329;
	protected final class_1959.class_1963 field_9327;
	protected final Map<class_2893.class_2894, List<class_2922<?>>> field_9333 = Maps.<class_2893.class_2894, List<class_2922<?>>>newHashMap();
	protected final Map<class_2893.class_2895, List<class_2975<?>>> field_9326 = Maps.<class_2893.class_2895, List<class_2975<?>>>newHashMap();
	protected final List<class_2975<?>> field_9334 = Lists.<class_2975<?>>newArrayList();
	protected final Map<class_3195<?>, class_3037> field_9337 = Maps.<class_3195<?>, class_3037>newHashMap();
	private final Map<class_1311, List<class_1959.class_1964>> field_9325 = Maps.<class_1311, List<class_1959.class_1964>>newHashMap();

	@Nullable
	public static class_1959 method_8716(class_1959 arg) {
		return field_9328.method_10200(class_2378.field_11153.method_10249(arg));
	}

	public static <C extends class_2920> class_2922<C> method_8714(class_2939<C> arg, C arg2) {
		return new class_2922<>(arg, arg2);
	}

	public static <F extends class_3037, D extends class_2998> class_2975<?> method_8699(class_3031<F> arg, F arg2, class_3284<D> arg3, D arg4) {
		class_3031<class_2986> lv = arg instanceof class_3038 ? class_3031.field_13561 : class_3031.field_13572;
		return new class_2975<>(lv, new class_2986(arg, arg2, arg3, arg4));
	}

	protected class_1959(class_1959.class_1960 arg) {
		if (arg.field_9353 != null
			&& arg.field_9346 != null
			&& arg.field_9345 != null
			&& arg.field_9344 != null
			&& arg.field_9348 != null
			&& arg.field_9349 != null
			&& arg.field_9351 != null
			&& arg.field_9347 != null
			&& arg.field_9350 != null) {
			this.field_9336 = arg.field_9353;
			this.field_9327 = arg.field_9346;
			this.field_9329 = arg.field_9345;
			this.field_9343 = arg.field_9344;
			this.field_9341 = arg.field_9348;
			this.field_9339 = arg.field_9349;
			this.field_9338 = arg.field_9351;
			this.field_9342 = arg.field_9347;
			this.field_9340 = arg.field_9350;
			this.field_9332 = arg.field_9352;

			for (class_2893.class_2895 lv : class_2893.class_2895.values()) {
				this.field_9326.put(lv, Lists.newArrayList());
			}

			for (class_1311 lv2 : class_1311.values()) {
				this.field_9325.put(lv2, Lists.newArrayList());
			}

			class_3864.method_20234(this);
		} else {
			throw new IllegalStateException("You are missing parameters to build a proper biome for " + this.getClass().getSimpleName() + "\n" + arg);
		}
	}

	public boolean method_8723() {
		return this.field_9332 != null;
	}

	@Environment(EnvType.CLIENT)
	public int method_8697(float f) {
		f /= 3.0F;
		f = class_3532.method_15363(f, -1.0F, 1.0F);
		return class_3532.method_15369(0.62222224F - f * 0.05F, 0.5F + f * 0.1F, 1.0F);
	}

	protected void method_8708(class_1311 arg, class_1959.class_1964 arg2) {
		((List)this.field_9325.get(arg)).add(arg2);
	}

	public List<class_1959.class_1964> method_8700(class_1311 arg) {
		return (List<class_1959.class_1964>)this.field_9325.get(arg);
	}

	public class_1959.class_1963 method_8694() {
		return this.field_9327;
	}

	public boolean method_8724() {
		return this.method_8715() > 0.85F;
	}

	public float method_8690() {
		return 0.1F;
	}

	public float method_8707(class_2338 arg) {
		if (arg.method_10264() > 64) {
			float f = (float)(field_9335.method_15437((double)((float)arg.method_10263() / 8.0F), (double)((float)arg.method_10260() / 8.0F)) * 4.0);
			return this.method_8712() - (f + (float)arg.method_10264() - 64.0F) * 0.05F / 30.0F;
		} else {
			return this.method_8712();
		}
	}

	public boolean method_8705(class_1941 arg, class_2338 arg2) {
		return this.method_8685(arg, arg2, true);
	}

	public boolean method_8685(class_1941 arg, class_2338 arg2, boolean bl) {
		if (this.method_8707(arg2) >= 0.15F) {
			return false;
		} else {
			if (arg2.method_10264() >= 0 && arg2.method_10264() < 256 && arg.method_8314(class_1944.field_9282, arg2) < 10) {
				class_2680 lv = arg.method_8320(arg2);
				class_3610 lv2 = arg.method_8316(arg2);
				if (lv2.method_15772() == class_3612.field_15910 && lv.method_11614() instanceof class_2404) {
					if (!bl) {
						return true;
					}

					boolean bl2 = arg.method_8585(arg2.method_10067())
						&& arg.method_8585(arg2.method_10078())
						&& arg.method_8585(arg2.method_10095())
						&& arg.method_8585(arg2.method_10072());
					if (!bl2) {
						return true;
					}
				}
			}

			return false;
		}
	}

	public boolean method_8696(class_1941 arg, class_2338 arg2) {
		if (this.method_8707(arg2) >= 0.15F) {
			return false;
		} else {
			if (arg2.method_10264() >= 0 && arg2.method_10264() < 256 && arg.method_8314(class_1944.field_9282, arg2) < 10) {
				class_2680 lv = arg.method_8320(arg2);
				if (lv.method_11588() && class_2246.field_10477.method_9564().method_11591(arg, arg2)) {
					return true;
				}
			}

			return false;
		}
	}

	public void method_8719(class_2893.class_2895 arg, class_2975<?> arg2) {
		if (arg2.field_13376 == class_3031.field_13561) {
			this.field_9334.add(arg2);
		}

		((List)this.field_9326.get(arg)).add(arg2);
	}

	public <C extends class_2920> void method_8691(class_2893.class_2894 arg, class_2922<C> arg2) {
		((List)this.field_9333.computeIfAbsent(arg, argx -> Lists.newArrayList())).add(arg2);
	}

	public List<class_2922<?>> method_8717(class_2893.class_2894 arg) {
		return (List<class_2922<?>>)this.field_9333.computeIfAbsent(arg, argx -> Lists.newArrayList());
	}

	public <C extends class_3037> void method_8710(class_3195<C> arg, C arg2) {
		this.field_9337.put(arg, arg2);
	}

	public <C extends class_3037> boolean method_8684(class_3195<C> arg) {
		return this.field_9337.containsKey(arg);
	}

	@Nullable
	public <C extends class_3037> C method_8706(class_3195<C> arg) {
		return (C)this.field_9337.get(arg);
	}

	public List<class_2975<?>> method_8718() {
		return this.field_9334;
	}

	public List<class_2975<?>> method_8721(class_2893.class_2895 arg) {
		return (List<class_2975<?>>)this.field_9326.get(arg);
	}

	public void method_8702(class_2893.class_2895 arg, class_2794<? extends class_2888> arg2, class_1936 arg3, long l, class_2919 arg4, class_2338 arg5) {
		int i = 0;

		for (class_2975<?> lv : (List)this.field_9326.get(arg)) {
			arg4.method_12664(l, i, arg.ordinal());
			lv.method_12862(arg3, arg2, arg4, arg5);
			i++;
		}
	}

	@Environment(EnvType.CLIENT)
	public int method_8711(class_2338 arg) {
		double d = (double)class_3532.method_15363(this.method_8707(arg), 0.0F, 1.0F);
		double e = (double)class_3532.method_15363(this.method_8715(), 0.0F, 1.0F);
		return class_1933.method_8377(d, e);
	}

	@Environment(EnvType.CLIENT)
	public int method_8698(class_2338 arg) {
		double d = (double)class_3532.method_15363(this.method_8707(arg), 0.0F, 1.0F);
		double e = (double)class_3532.method_15363(this.method_8715(), 0.0F, 1.0F);
		return class_1926.method_8344(d, e);
	}

	public void method_8703(Random random, class_2791 arg, int i, int j, int k, double d, class_2680 arg2, class_2680 arg3, int l, long m) {
		this.field_9336.method_15199(m);
		this.field_9336.method_15198(random, arg, this, i, j, k, d, arg2, arg3, l, m);
	}

	public class_1959.class_1962 method_8704() {
		if (this.field_9329 == class_1959.class_1961.field_9367) {
			return class_1959.class_1962.field_9379;
		} else if ((double)this.method_8712() < 0.2) {
			return class_1959.class_1962.field_9377;
		} else {
			return (double)this.method_8712() < 1.0 ? class_1959.class_1962.field_9375 : class_1959.class_1962.field_9378;
		}
	}

	public final float method_8695() {
		return this.field_9343;
	}

	public final float method_8715() {
		return this.field_9338;
	}

	@Environment(EnvType.CLIENT)
	public class_2561 method_8693() {
		return new class_2588(this.method_8689());
	}

	public String method_8689() {
		if (this.field_9331 == null) {
			this.field_9331 = class_156.method_646("biome", class_2378.field_11153.method_10221(this));
		}

		return this.field_9331;
	}

	public final float method_8686() {
		return this.field_9341;
	}

	public final float method_8712() {
		return this.field_9339;
	}

	public final int method_8687() {
		return this.field_9342;
	}

	public final int method_8713() {
		return this.field_9340;
	}

	public final class_1959.class_1961 method_8688() {
		return this.field_9329;
	}

	public class_3504<?> method_8692() {
		return this.field_9336;
	}

	public class_3531 method_8722() {
		return this.field_9336.method_15197();
	}

	@Nullable
	public String method_8725() {
		return this.field_9332;
	}

	public static class class_1960 {
		@Nullable
		private class_3504<?> field_9353;
		@Nullable
		private class_1959.class_1963 field_9346;
		@Nullable
		private class_1959.class_1961 field_9345;
		@Nullable
		private Float field_9344;
		@Nullable
		private Float field_9348;
		@Nullable
		private Float field_9349;
		@Nullable
		private Float field_9351;
		@Nullable
		private Integer field_9347;
		@Nullable
		private Integer field_9350;
		@Nullable
		private String field_9352;

		public <SC extends class_3531> class_1959.class_1960 method_8737(class_3523<SC> arg, SC arg2) {
			this.field_9353 = new class_3504<>(arg, arg2);
			return this;
		}

		public class_1959.class_1960 method_8731(class_3504<?> arg) {
			this.field_9353 = arg;
			return this;
		}

		public class_1959.class_1960 method_8735(class_1959.class_1963 arg) {
			this.field_9346 = arg;
			return this;
		}

		public class_1959.class_1960 method_8738(class_1959.class_1961 arg) {
			this.field_9345 = arg;
			return this;
		}

		public class_1959.class_1960 method_8740(float f) {
			this.field_9344 = f;
			return this;
		}

		public class_1959.class_1960 method_8743(float f) {
			this.field_9348 = f;
			return this;
		}

		public class_1959.class_1960 method_8747(float f) {
			this.field_9349 = f;
			return this;
		}

		public class_1959.class_1960 method_8727(float f) {
			this.field_9351 = f;
			return this;
		}

		public class_1959.class_1960 method_8733(int i) {
			this.field_9347 = i;
			return this;
		}

		public class_1959.class_1960 method_8728(int i) {
			this.field_9350 = i;
			return this;
		}

		public class_1959.class_1960 method_8745(@Nullable String string) {
			this.field_9352 = string;
			return this;
		}

		public String toString() {
			return "BiomeBuilder{\nsurfaceBuilder="
				+ this.field_9353
				+ ",\nprecipitation="
				+ this.field_9346
				+ ",\nbiomeCategory="
				+ this.field_9345
				+ ",\ndepth="
				+ this.field_9344
				+ ",\nscale="
				+ this.field_9348
				+ ",\ntemperature="
				+ this.field_9349
				+ ",\ndownfall="
				+ this.field_9351
				+ ",\nwaterColor="
				+ this.field_9347
				+ ",\nwaterFogColor="
				+ this.field_9350
				+ ",\nparent='"
				+ this.field_9352
				+ '\''
				+ "\n"
				+ '}';
		}
	}

	public static enum class_1961 {
		field_9371("none"),
		field_9361("taiga"),
		field_9357("extreme_hills"),
		field_9358("jungle"),
		field_9354("mesa"),
		field_9355("plains"),
		field_9356("savanna"),
		field_9362("icy"),
		field_9360("the_end"),
		field_9363("beach"),
		field_9370("forest"),
		field_9367("ocean"),
		field_9368("desert"),
		field_9369("river"),
		field_9364("swamp"),
		field_9365("mushroom"),
		field_9366("nether");

		private static final Map<String, class_1959.class_1961> field_9359 = (Map<String, class_1959.class_1961>)Arrays.stream(values())
			.collect(Collectors.toMap(class_1959.class_1961::method_8749, arg -> arg));
		private final String field_9372;

		private class_1961(String string2) {
			this.field_9372 = string2;
		}

		public String method_8749() {
			return this.field_9372;
		}
	}

	public static enum class_1962 {
		field_9379("ocean"),
		field_9377("cold"),
		field_9375("medium"),
		field_9378("warm");

		private static final Map<String, class_1959.class_1962> field_9374 = (Map<String, class_1959.class_1962>)Arrays.stream(values())
			.collect(Collectors.toMap(class_1959.class_1962::method_8750, arg -> arg));
		private final String field_9380;

		private class_1962(String string2) {
			this.field_9380 = string2;
		}

		public String method_8750() {
			return this.field_9380;
		}
	}

	public static enum class_1963 {
		field_9384("none"),
		field_9382("rain"),
		field_9383("snow");

		private static final Map<String, class_1959.class_1963> field_9381 = (Map<String, class_1959.class_1963>)Arrays.stream(values())
			.collect(Collectors.toMap(class_1959.class_1963::method_8752, arg -> arg));
		private final String field_9385;

		private class_1963(String string2) {
			this.field_9385 = string2;
		}

		public String method_8752() {
			return this.field_9385;
		}
	}

	public static class class_1964 extends class_3549.class_3550 {
		public final class_1299<?> field_9389;
		public final int field_9388;
		public final int field_9387;

		public class_1964(class_1299<?> arg, int i, int j, int k) {
			super(i);
			this.field_9389 = arg;
			this.field_9388 = j;
			this.field_9387 = k;
		}

		public String toString() {
			return class_1299.method_5890(this.field_9389) + "*(" + this.field_9388 + "-" + this.field_9387 + "):" + this.field_15774;
		}
	}
}
