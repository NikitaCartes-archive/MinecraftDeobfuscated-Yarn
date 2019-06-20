package net.minecraft;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2248 implements class_1935 {
	protected static final Logger field_10638 = LogManager.getLogger();
	public static final class_2361<class_2680> field_10651 = new class_2361<>();
	private static final class_2350[] field_10644 = new class_2350[]{
		class_2350.field_11039, class_2350.field_11034, class_2350.field_11043, class_2350.field_11035, class_2350.field_11033, class_2350.field_11036
	};
	private static final LoadingCache<class_265, Boolean> field_19312 = CacheBuilder.newBuilder()
		.maximumSize(512L)
		.weakKeys()
		.build(new CacheLoader<class_265, Boolean>() {
			public Boolean method_20516(class_265 arg) {
				return !class_259.method_1074(class_259.method_1077(), arg, class_247.field_16892);
			}
		});
	private static final class_265 field_18966 = class_259.method_1072(
		class_259.method_1077(), method_9541(2.0, 0.0, 2.0, 14.0, 16.0, 14.0), class_247.field_16886
	);
	private static final class_265 field_19061 = method_9541(7.0, 0.0, 7.0, 9.0, 10.0, 9.0);
	protected final int field_10634;
	protected final float field_10650;
	protected final float field_10648;
	protected final boolean field_10641;
	protected final class_2498 field_10643;
	protected final class_3614 field_10635;
	protected final class_3620 field_10639;
	private final float field_10637;
	protected final class_2689<class_2248, class_2680> field_10647;
	private class_2680 field_10646;
	protected final boolean field_10640;
	private final boolean field_10645;
	@Nullable
	private class_2960 field_10636;
	@Nullable
	private String field_10642;
	@Nullable
	private class_1792 field_17562;
	private static final ThreadLocal<Object2ByteLinkedOpenHashMap<class_2248.class_2249>> field_10649 = ThreadLocal.withInitial(() -> {
		Object2ByteLinkedOpenHashMap<class_2248.class_2249> object2ByteLinkedOpenHashMap = new Object2ByteLinkedOpenHashMap<class_2248.class_2249>(200) {
			@Override
			protected void rehash(int i) {
			}
		};
		object2ByteLinkedOpenHashMap.defaultReturnValue((byte)127);
		return object2ByteLinkedOpenHashMap;
	});

	public static int method_9507(@Nullable class_2680 arg) {
		if (arg == null) {
			return 0;
		} else {
			int i = field_10651.method_10206(arg);
			return i == -1 ? 0 : i;
		}
	}

	public static class_2680 method_9531(int i) {
		class_2680 lv = field_10651.method_10200(i);
		return lv == null ? class_2246.field_10124.method_9564() : lv;
	}

	public static class_2248 method_9503(@Nullable class_1792 arg) {
		return arg instanceof class_1747 ? ((class_1747)arg).method_7711() : class_2246.field_10124;
	}

	public static class_2680 method_9582(class_2680 arg, class_2680 arg2, class_1937 arg3, class_2338 arg4) {
		class_265 lv = class_259.method_1082(arg.method_11628(arg3, arg4), arg2.method_11628(arg3, arg4), class_247.field_16893)
			.method_1096((double)arg4.method_10263(), (double)arg4.method_10264(), (double)arg4.method_10260());

		for (class_1297 lv2 : arg3.method_8335(null, lv.method_1107())) {
			double d = class_259.method_1085(class_2350.class_2351.field_11052, lv2.method_5829().method_989(0.0, 1.0, 0.0), Stream.of(lv), -1.0);
			lv2.method_5859(lv2.field_5987, lv2.field_6010 + 1.0 + d, lv2.field_6035);
		}

		return arg2;
	}

	public static class_265 method_9541(double d, double e, double f, double g, double h, double i) {
		return class_259.method_1081(d / 16.0, e / 16.0, f / 16.0, g / 16.0, h / 16.0, i / 16.0);
	}

	@Deprecated
	public boolean method_9523(class_2680 arg, class_1922 arg2, class_2338 arg3, class_1299<?> arg4) {
		return method_20045(arg, arg2, arg3, class_2350.field_11036) && this.field_10634 < 14;
	}

	@Deprecated
	public boolean method_9500(class_2680 arg) {
		return false;
	}

	@Deprecated
	public int method_9593(class_2680 arg) {
		return this.field_10634;
	}

	@Deprecated
	public class_3614 method_9597(class_2680 arg) {
		return this.field_10635;
	}

	@Deprecated
	public class_3620 method_9602(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return this.field_10639;
	}

	@Deprecated
	public void method_9528(class_2680 arg, class_1936 arg2, class_2338 arg3, int i) {
		try (class_2338.class_2340 lv = class_2338.class_2340.method_10109()) {
			for (class_2350 lv2 : field_10644) {
				lv.method_10114(arg3).method_10118(lv2);
				class_2680 lv3 = arg2.method_8320(lv);
				class_2680 lv4 = lv3.method_11578(lv2.method_10153(), arg, arg2, lv, arg3);
				method_9611(lv3, lv4, arg2, lv, i);
			}
		}
	}

	public boolean method_9525(class_3494<class_2248> arg) {
		return arg.method_15141(this);
	}

	public static class_2680 method_9510(class_2680 arg, class_1936 arg2, class_2338 arg3) {
		class_2680 lv = arg;
		class_2338.class_2339 lv2 = new class_2338.class_2339();

		for (class_2350 lv3 : field_10644) {
			lv2.method_10101(arg3).method_10098(lv3);
			lv = lv.method_11578(lv3, arg2.method_8320(lv2), arg2, arg3, lv2);
		}

		return lv;
	}

	public static void method_9611(class_2680 arg, class_2680 arg2, class_1936 arg3, class_2338 arg4, int i) {
		if (arg2 != arg) {
			if (arg2.method_11588()) {
				if (!arg3.method_8608()) {
					arg3.method_8651(arg4, (i & 32) == 0);
				}
			} else {
				arg3.method_8652(arg4, arg2, i & -33);
			}
		}
	}

	@Deprecated
	public void method_9517(class_2680 arg, class_1936 arg2, class_2338 arg3, int i) {
	}

	@Deprecated
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		return arg;
	}

	@Deprecated
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg;
	}

	@Deprecated
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg;
	}

	public class_2248(class_2248.class_2251 arg) {
		class_2689.class_2690<class_2248, class_2680> lv = new class_2689.class_2690<>(this);
		this.method_9515(lv);
		this.field_10635 = arg.field_10668;
		this.field_10639 = arg.field_10662;
		this.field_10640 = arg.field_10664;
		this.field_10643 = arg.field_10665;
		this.field_10634 = arg.field_10663;
		this.field_10648 = arg.field_10660;
		this.field_10650 = arg.field_10669;
		this.field_10641 = arg.field_10661;
		this.field_10637 = arg.field_10667;
		this.field_10645 = arg.field_10670;
		this.field_10636 = arg.field_10666;
		this.field_10647 = lv.method_11668(class_2680::new);
		this.method_9590(this.field_10647.method_11664());
	}

	public static boolean method_9581(class_2248 arg) {
		return arg instanceof class_2397
			|| arg == class_2246.field_10499
			|| arg == class_2246.field_10147
			|| arg == class_2246.field_10009
			|| arg == class_2246.field_10545
			|| arg == class_2246.field_10261;
	}

	@Deprecated
	public boolean method_9521(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return arg.method_11620().method_15804() && method_9614(arg.method_11628(arg2, arg3)) && !arg.method_11634();
	}

	@Deprecated
	public boolean method_16362(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return this.field_10635.method_15801() && method_9614(arg.method_11628(arg2, arg3));
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	public boolean method_9589(class_2680 arg) {
		return false;
	}

	@Deprecated
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		switch (arg4) {
			case field_50:
				return !method_9614(arg.method_11628(arg2, arg3));
			case field_48:
				return arg2.method_8316(arg3).method_15767(class_3486.field_15517);
			case field_51:
				return !method_9614(arg.method_11628(arg2, arg3));
			default:
				return false;
		}
	}

	@Deprecated
	public class_2464 method_9604(class_2680 arg) {
		return class_2464.field_11458;
	}

	@Deprecated
	public boolean method_9616(class_2680 arg, class_1750 arg2) {
		return this.field_10635.method_15800() && (arg2.method_8041().method_7960() || arg2.method_8041().method_7909() != this.method_8389());
	}

	@Deprecated
	public float method_9537(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return this.field_10650;
	}

	public boolean method_9542(class_2680 arg) {
		return this.field_10641;
	}

	public boolean method_9570() {
		return this instanceof class_2343;
	}

	@Deprecated
	public boolean method_9552(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return false;
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	public int method_9546(class_2680 arg, class_1920 arg2, class_2338 arg3) {
		return arg2.method_8313(arg3, arg.method_11630());
	}

	@Environment(EnvType.CLIENT)
	public static boolean method_9607(class_2680 arg, class_1922 arg2, class_2338 arg3, class_2350 arg4) {
		class_2338 lv = arg3.method_10093(arg4);
		class_2680 lv2 = arg2.method_8320(lv);
		if (arg.method_11592(lv2, arg4)) {
			return false;
		} else if (lv2.method_11619()) {
			class_2248.class_2249 lv3 = new class_2248.class_2249(arg, lv2, arg4);
			Object2ByteLinkedOpenHashMap<class_2248.class_2249> object2ByteLinkedOpenHashMap = (Object2ByteLinkedOpenHashMap<class_2248.class_2249>)field_10649.get();
			byte b = object2ByteLinkedOpenHashMap.getAndMoveToFirst(lv3);
			if (b != 127) {
				return b != 0;
			} else {
				class_265 lv4 = arg.method_16384(arg2, arg3, arg4);
				class_265 lv5 = lv2.method_16384(arg2, lv, arg4.method_10153());
				boolean bl = class_259.method_1074(lv4, lv5, class_247.field_16886);
				if (object2ByteLinkedOpenHashMap.size() == 200) {
					object2ByteLinkedOpenHashMap.removeLastByte();
				}

				object2ByteLinkedOpenHashMap.putAndMoveToFirst(lv3, (byte)(bl ? 1 : 0));
				return bl;
			}
		} else {
			return true;
		}
	}

	@Deprecated
	public boolean method_9601(class_2680 arg) {
		return this.field_10640 && this.method_9551() == class_1921.field_9178;
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	public boolean method_9522(class_2680 arg, class_2680 arg2, class_2350 arg3) {
		return false;
	}

	@Deprecated
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return class_259.method_1077();
	}

	@Deprecated
	public class_265 method_9549(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return this.field_10640 ? arg.method_17770(arg2, arg3) : class_259.method_1073();
	}

	@Deprecated
	public class_265 method_9571(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return arg.method_17770(arg2, arg3);
	}

	@Deprecated
	public class_265 method_9584(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return class_259.method_1073();
	}

	public static boolean method_16361(class_1922 arg, class_2338 arg2) {
		class_2680 lv = arg.method_8320(arg2);
		return !lv.method_11602(class_3481.field_15503)
			&& !class_259.method_1074(lv.method_11628(arg, arg2).method_20538(class_2350.field_11036), field_18966, class_247.field_16893);
	}

	public static boolean method_20044(class_1941 arg, class_2338 arg2, class_2350 arg3) {
		class_2680 lv = arg.method_8320(arg2);
		return !lv.method_11602(class_3481.field_15503) && !class_259.method_1074(lv.method_11628(arg, arg2).method_20538(arg3), field_19061, class_247.field_16893);
	}

	public static boolean method_20045(class_2680 arg, class_1922 arg2, class_2338 arg3, class_2350 arg4) {
		return !arg.method_11602(class_3481.field_15503) && method_9501(arg.method_11628(arg2, arg3), arg4);
	}

	public static boolean method_9501(class_265 arg, class_2350 arg2) {
		class_265 lv = arg.method_20538(arg2);
		return method_9614(lv);
	}

	public static boolean method_9614(class_265 arg) {
		return field_19312.getUnchecked(arg);
	}

	@Deprecated
	public final boolean method_9557(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return arg.method_11619() ? method_9614(arg.method_11615(arg2, arg3)) : false;
	}

	public boolean method_9579(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return !method_9614(arg.method_17770(arg2, arg3)) && arg.method_11618().method_15769();
	}

	@Deprecated
	public int method_9505(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		if (arg.method_11598(arg2, arg3)) {
			return arg2.method_8315();
		} else {
			return arg.method_11623(arg2, arg3) ? 0 : 1;
		}
	}

	@Deprecated
	public boolean method_9526(class_2680 arg) {
		return false;
	}

	@Deprecated
	public void method_9514(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		this.method_9588(arg, arg2, arg3, random);
	}

	@Deprecated
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
	}

	@Environment(EnvType.CLIENT)
	public void method_9496(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
	}

	public void method_9585(class_1936 arg, class_2338 arg2, class_2680 arg3) {
	}

	@Deprecated
	public void method_9612(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2248 arg4, class_2338 arg5, boolean bl) {
		class_4209.method_19472(arg2, arg3);
	}

	public int method_9563(class_1941 arg) {
		return 10;
	}

	@Nullable
	@Deprecated
	public class_3908 method_17454(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		return null;
	}

	@Deprecated
	public void method_9615(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
	}

	@Deprecated
	public void method_9536(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (this.method_9570() && arg.method_11614() != arg4.method_11614()) {
			arg2.method_8544(arg3);
		}
	}

	@Deprecated
	public float method_9594(class_2680 arg, class_1657 arg2, class_1922 arg3, class_2338 arg4) {
		float f = arg.method_11579(arg3, arg4);
		if (f == -1.0F) {
			return 0.0F;
		} else {
			int i = arg2.method_7305(arg) ? 30 : 100;
			return arg2.method_7351(arg) / f / (float)i;
		}
	}

	@Deprecated
	public void method_9565(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1799 arg4) {
	}

	public class_2960 method_9580() {
		if (this.field_10636 == null) {
			class_2960 lv = class_2378.field_11146.method_10221(this);
			this.field_10636 = new class_2960(lv.method_12836(), "blocks/" + lv.method_12832());
		}

		return this.field_10636;
	}

	@Deprecated
	public List<class_1799> method_9560(class_2680 arg, class_47.class_48 arg2) {
		class_2960 lv = this.method_9580();
		if (lv == class_39.field_844) {
			return Collections.emptyList();
		} else {
			class_47 lv2 = arg2.method_312(class_181.field_1224, arg).method_309(class_173.field_1172);
			class_3218 lv3 = lv2.method_299();
			class_52 lv4 = lv3.method_8503().method_3857().method_367(lv);
			return lv4.method_319(lv2);
		}
	}

	public static List<class_1799> method_9562(class_2680 arg, class_3218 arg2, class_2338 arg3, @Nullable class_2586 arg4) {
		class_47.class_48 lv = new class_47.class_48(arg2)
			.method_311(arg2.field_9229)
			.method_312(class_181.field_1232, arg3)
			.method_312(class_181.field_1229, class_1799.field_8037)
			.method_306(class_181.field_1228, arg4);
		return arg.method_11612(lv);
	}

	public static List<class_1799> method_9609(class_2680 arg, class_3218 arg2, class_2338 arg3, @Nullable class_2586 arg4, class_1297 arg5, class_1799 arg6) {
		class_47.class_48 lv = new class_47.class_48(arg2)
			.method_311(arg2.field_9229)
			.method_312(class_181.field_1232, arg3)
			.method_312(class_181.field_1229, arg6)
			.method_312(class_181.field_1226, arg5)
			.method_306(class_181.field_1228, arg4);
		return arg.method_11612(lv);
	}

	public static void method_9566(class_2680 arg, class_47.class_48 arg2) {
		class_3218 lv = arg2.method_313();
		class_2338 lv2 = arg2.method_308(class_181.field_1232);
		arg.method_11612(arg2).forEach(arg3 -> method_9577(lv, lv2, arg3));
		arg.method_11595(lv, lv2, class_1799.field_8037);
	}

	public static void method_9497(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		if (arg2 instanceof class_3218) {
			method_9562(arg, (class_3218)arg2, arg3, null).forEach(arg3x -> method_9577(arg2, arg3, arg3x));
		}

		arg.method_11595(arg2, arg3, class_1799.field_8037);
	}

	public static void method_9610(class_2680 arg, class_1937 arg2, class_2338 arg3, @Nullable class_2586 arg4) {
		if (arg2 instanceof class_3218) {
			method_9562(arg, (class_3218)arg2, arg3, arg4).forEach(arg3x -> method_9577(arg2, arg3, arg3x));
		}

		arg.method_11595(arg2, arg3, class_1799.field_8037);
	}

	public static void method_9511(class_2680 arg, class_1937 arg2, class_2338 arg3, @Nullable class_2586 arg4, class_1297 arg5, class_1799 arg6) {
		if (arg2 instanceof class_3218) {
			method_9609(arg, (class_3218)arg2, arg3, arg4, arg5, arg6).forEach(arg3x -> method_9577(arg2, arg3, arg3x));
		}

		arg.method_11595(arg2, arg3, arg6);
	}

	public static void method_9577(class_1937 arg, class_2338 arg2, class_1799 arg3) {
		if (!arg.field_9236 && !arg3.method_7960() && arg.method_8450().method_8355(class_1928.field_19392)) {
			float f = 0.5F;
			double d = (double)(arg.field_9229.nextFloat() * 0.5F) + 0.25;
			double e = (double)(arg.field_9229.nextFloat() * 0.5F) + 0.25;
			double g = (double)(arg.field_9229.nextFloat() * 0.5F) + 0.25;
			class_1542 lv = new class_1542(arg, (double)arg2.method_10263() + d, (double)arg2.method_10264() + e, (double)arg2.method_10260() + g, arg3);
			lv.method_6988();
			arg.method_8649(lv);
		}
	}

	protected void method_9583(class_1937 arg, class_2338 arg2, int i) {
		if (!arg.field_9236 && arg.method_8450().method_8355(class_1928.field_19392)) {
			while (i > 0) {
				int j = class_1303.method_5918(i);
				i -= j;
				arg.method_8649(new class_1303(arg, (double)arg2.method_10263() + 0.5, (double)arg2.method_10264() + 0.5, (double)arg2.method_10260() + 0.5, j));
			}
		}
	}

	public float method_9520() {
		return this.field_10648;
	}

	public void method_9586(class_1937 arg, class_2338 arg2, class_1927 arg3) {
	}

	public class_1921 method_9551() {
		return class_1921.field_9178;
	}

	@Deprecated
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		return true;
	}

	@Deprecated
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		return false;
	}

	public void method_9591(class_1937 arg, class_2338 arg2, class_1297 arg3) {
	}

	@Nullable
	public class_2680 method_9605(class_1750 arg) {
		return this.method_9564();
	}

	@Deprecated
	public void method_9606(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4) {
	}

	@Deprecated
	public int method_9524(class_2680 arg, class_1922 arg2, class_2338 arg3, class_2350 arg4) {
		return 0;
	}

	@Deprecated
	public boolean method_9506(class_2680 arg) {
		return false;
	}

	@Deprecated
	public void method_9548(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1297 arg4) {
	}

	@Deprecated
	public int method_9603(class_2680 arg, class_1922 arg2, class_2338 arg3, class_2350 arg4) {
		return 0;
	}

	public void method_9556(class_1937 arg, class_1657 arg2, class_2338 arg3, class_2680 arg4, @Nullable class_2586 arg5, class_1799 arg6) {
		arg2.method_7259(class_3468.field_15427.method_14956(this));
		arg2.method_7322(0.005F);
		method_9511(arg4, arg, arg3, arg5, arg2, arg6);
	}

	public void method_9567(class_1937 arg, class_2338 arg2, class_2680 arg3, @Nullable class_1309 arg4, class_1799 arg5) {
	}

	public boolean method_9538() {
		return !this.field_10635.method_15799() && !this.field_10635.method_15797();
	}

	@Environment(EnvType.CLIENT)
	public class_2561 method_9518() {
		return new class_2588(this.method_9539());
	}

	public String method_9539() {
		if (this.field_10642 == null) {
			this.field_10642 = class_156.method_646("block", class_2378.field_11146.method_10221(this));
		}

		return this.field_10642;
	}

	@Deprecated
	public boolean method_9592(class_2680 arg, class_1937 arg2, class_2338 arg3, int i, int j) {
		return false;
	}

	@Deprecated
	public class_3619 method_9527(class_2680 arg) {
		return this.field_10635.method_15798();
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	public float method_9575(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return method_9614(arg.method_11628(arg2, arg3)) ? 0.2F : 1.0F;
	}

	public void method_9554(class_1937 arg, class_2338 arg2, class_1297 arg3, float f) {
		arg3.method_5747(f, 1.0F);
	}

	public void method_9502(class_1922 arg, class_1297 arg2) {
		arg2.method_18799(arg2.method_18798().method_18805(1.0, 0.0, 1.0));
	}

	@Environment(EnvType.CLIENT)
	public class_1799 method_9574(class_1922 arg, class_2338 arg2, class_2680 arg3) {
		return new class_1799(this);
	}

	public void method_9578(class_1761 arg, class_2371<class_1799> arg2) {
		arg2.add(new class_1799(this));
	}

	@Deprecated
	public class_3610 method_9545(class_2680 arg) {
		return class_3612.field_15906.method_15785();
	}

	public float method_9499() {
		return this.field_10637;
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	public long method_9535(class_2680 arg, class_2338 arg2) {
		return class_3532.method_15389(arg2);
	}

	public void method_19286(class_1937 arg, class_2680 arg2, class_3965 arg3, class_1297 arg4) {
	}

	public void method_9576(class_1937 arg, class_2338 arg2, class_2680 arg3, class_1657 arg4) {
		arg.method_8444(arg4, 2001, arg2, method_9507(arg3));
	}

	public void method_9504(class_1937 arg, class_2338 arg2) {
	}

	public boolean method_9533(class_1927 arg) {
		return true;
	}

	@Deprecated
	public boolean method_9498(class_2680 arg) {
		return false;
	}

	@Deprecated
	public int method_9572(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		return 0;
	}

	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
	}

	public class_2689<class_2248, class_2680> method_9595() {
		return this.field_10647;
	}

	protected final void method_9590(class_2680 arg) {
		this.field_10646 = arg;
	}

	public final class_2680 method_9564() {
		return this.field_10646;
	}

	public class_2248.class_2250 method_16841() {
		return class_2248.class_2250.field_10656;
	}

	@Deprecated
	public class_243 method_9540(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		class_2248.class_2250 lv = this.method_16841();
		if (lv == class_2248.class_2250.field_10656) {
			return class_243.field_1353;
		} else {
			long l = class_3532.method_15371(arg3.method_10263(), 0, arg3.method_10260());
			return new class_243(
				((double)((float)(l & 15L) / 15.0F) - 0.5) * 0.5,
				lv == class_2248.class_2250.field_10655 ? ((double)((float)(l >> 4 & 15L) / 15.0F) - 1.0) * 0.2 : 0.0,
				((double)((float)(l >> 8 & 15L) / 15.0F) - 0.5) * 0.5
			);
		}
	}

	public class_2498 method_9573(class_2680 arg) {
		return this.field_10643;
	}

	@Override
	public class_1792 method_8389() {
		if (this.field_17562 == null) {
			this.field_17562 = class_1792.method_7867(this);
		}

		return this.field_17562;
	}

	public boolean method_9543() {
		return this.field_10645;
	}

	public String toString() {
		return "Block{" + class_2378.field_11146.method_10221(this) + "}";
	}

	@Environment(EnvType.CLIENT)
	public void method_9568(class_1799 arg, @Nullable class_1922 arg2, List<class_2561> list, class_1836 arg3) {
	}

	public static boolean method_9608(class_2248 arg) {
		return arg == class_2246.field_10340 || arg == class_2246.field_10474 || arg == class_2246.field_10508 || arg == class_2246.field_10115;
	}

	public static boolean method_9519(class_2248 arg) {
		return arg == class_2246.field_10566 || arg == class_2246.field_10253 || arg == class_2246.field_10520;
	}

	public static final class class_2249 {
		private final class_2680 field_10652;
		private final class_2680 field_10654;
		private final class_2350 field_10653;

		public class_2249(class_2680 arg, class_2680 arg2, class_2350 arg3) {
			this.field_10652 = arg;
			this.field_10654 = arg2;
			this.field_10653 = arg3;
		}

		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (!(object instanceof class_2248.class_2249)) {
				return false;
			} else {
				class_2248.class_2249 lv = (class_2248.class_2249)object;
				return this.field_10652 == lv.field_10652 && this.field_10654 == lv.field_10654 && this.field_10653 == lv.field_10653;
			}
		}

		public int hashCode() {
			int i = this.field_10652.hashCode();
			i = 31 * i + this.field_10654.hashCode();
			return 31 * i + this.field_10653.hashCode();
		}
	}

	public static enum class_2250 {
		field_10656,
		field_10657,
		field_10655;
	}

	public static class class_2251 {
		private class_3614 field_10668;
		private class_3620 field_10662;
		private boolean field_10664 = true;
		private class_2498 field_10665 = class_2498.field_11544;
		private int field_10663;
		private float field_10660;
		private float field_10669;
		private boolean field_10661;
		private float field_10667 = 0.6F;
		private class_2960 field_10666;
		private boolean field_10670;

		private class_2251(class_3614 arg, class_3620 arg2) {
			this.field_10668 = arg;
			this.field_10662 = arg2;
		}

		public static class_2248.class_2251 method_9637(class_3614 arg) {
			return method_9639(arg, arg.method_15803());
		}

		public static class_2248.class_2251 method_9617(class_3614 arg, class_1767 arg2) {
			return method_9639(arg, arg2.method_7794());
		}

		public static class_2248.class_2251 method_9639(class_3614 arg, class_3620 arg2) {
			return new class_2248.class_2251(arg, arg2);
		}

		public static class_2248.class_2251 method_9630(class_2248 arg) {
			class_2248.class_2251 lv = new class_2248.class_2251(arg.field_10635, arg.field_10639);
			lv.field_10668 = arg.field_10635;
			lv.field_10669 = arg.field_10650;
			lv.field_10660 = arg.field_10648;
			lv.field_10664 = arg.field_10640;
			lv.field_10661 = arg.field_10641;
			lv.field_10663 = arg.field_10634;
			lv.field_10662 = arg.field_10639;
			lv.field_10665 = arg.field_10643;
			lv.field_10667 = arg.method_9499();
			lv.field_10670 = arg.field_10645;
			return lv;
		}

		public class_2248.class_2251 method_9634() {
			this.field_10664 = false;
			return this;
		}

		public class_2248.class_2251 method_9628(float f) {
			this.field_10667 = f;
			return this;
		}

		protected class_2248.class_2251 method_9626(class_2498 arg) {
			this.field_10665 = arg;
			return this;
		}

		protected class_2248.class_2251 method_9631(int i) {
			this.field_10663 = i;
			return this;
		}

		public class_2248.class_2251 method_9629(float f, float g) {
			this.field_10669 = f;
			this.field_10660 = Math.max(0.0F, g);
			return this;
		}

		protected class_2248.class_2251 method_9618() {
			return this.method_9632(0.0F);
		}

		protected class_2248.class_2251 method_9632(float f) {
			this.method_9629(f, f);
			return this;
		}

		protected class_2248.class_2251 method_9640() {
			this.field_10661 = true;
			return this;
		}

		protected class_2248.class_2251 method_9624() {
			this.field_10670 = true;
			return this;
		}

		protected class_2248.class_2251 method_16229() {
			this.field_10666 = class_39.field_844;
			return this;
		}

		public class_2248.class_2251 method_16228(class_2248 arg) {
			this.field_10666 = arg.method_9580();
			return this;
		}
	}
}
