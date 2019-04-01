package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2680 extends class_2679<class_2248, class_2680> implements class_2688<class_2680> {
	@Nullable
	private class_2680.class_3752 field_12290;
	private final int field_16553;
	private final boolean field_16554;

	public class_2680(class_2248 arg, ImmutableMap<class_2769<?>, Comparable<?>> immutableMap) {
		super(arg, immutableMap);
		this.field_16553 = arg.method_9593(this);
		this.field_16554 = arg.method_9526(this);
	}

	public void method_11590() {
		if (!this.method_11614().method_9543()) {
			this.field_12290 = new class_2680.class_3752(this);
		}
	}

	public class_2248 method_11614() {
		return this.field_12287;
	}

	public class_3614 method_11620() {
		return this.method_11614().method_9597(this);
	}

	public boolean method_11611(class_1922 arg, class_2338 arg2, class_1299<?> arg3) {
		return this.method_11614().method_9523(this, arg, arg2, arg3);
	}

	public boolean method_11623(class_1922 arg, class_2338 arg2) {
		return this.field_12290 != null ? this.field_12290.field_16556 : this.method_11614().method_9579(this, arg, arg2);
	}

	public int method_11581(class_1922 arg, class_2338 arg2) {
		return this.field_12290 != null ? this.field_12290.field_16555 : this.method_11614().method_9505(this, arg, arg2);
	}

	@Environment(EnvType.CLIENT)
	public class_265 method_16384(class_1922 arg, class_2338 arg2, class_2350 arg3) {
		return this.field_12290 != null && this.field_12290.field_16560 != null
			? this.field_12290.field_16560[arg3.ordinal()]
			: class_259.method_16344(this.method_11615(arg, arg2), arg3);
	}

	public boolean method_17900() {
		return this.field_12290 == null || this.field_12290.field_17651;
	}

	public boolean method_16386() {
		return this.field_16554;
	}

	public int method_11630() {
		return this.field_16553;
	}

	public boolean method_11588() {
		return this.method_11614().method_9500(this);
	}

	public boolean method_11593(class_1922 arg, class_2338 arg2) {
		return this.method_11614().method_9599(this, arg, arg2);
	}

	public class_3620 method_11625(class_1922 arg, class_2338 arg2) {
		return this.method_11614().method_9602(this, arg, arg2);
	}

	public class_2680 method_11626(class_2470 arg) {
		return this.method_11614().method_9598(this, arg);
	}

	public class_2680 method_11605(class_2415 arg) {
		return this.method_11614().method_9569(this, arg);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11608() {
		return this.method_11614().method_9589(this);
	}

	public class_2464 method_11610() {
		return this.method_11614().method_9604(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11632(class_1920 arg, class_2338 arg2) {
		return this.method_11614().method_9546(this, arg, arg2);
	}

	@Environment(EnvType.CLIENT)
	public float method_11596(class_1922 arg, class_2338 arg2) {
		return this.method_11614().method_9575(this, arg, arg2);
	}

	public boolean method_11621(class_1922 arg, class_2338 arg2) {
		return this.method_11614().method_9521(this, arg, arg2);
	}

	public boolean method_11634() {
		return this.method_11614().method_9506(this);
	}

	public int method_11597(class_1922 arg, class_2338 arg2, class_2350 arg3) {
		return this.method_11614().method_9524(this, arg, arg2, arg3);
	}

	public boolean method_11584() {
		return this.method_11614().method_9498(this);
	}

	public int method_11627(class_1937 arg, class_2338 arg2) {
		return this.method_11614().method_9572(this, arg, arg2);
	}

	public float method_11579(class_1922 arg, class_2338 arg2) {
		return this.method_11614().method_9537(this, arg, arg2);
	}

	public float method_11589(class_1657 arg, class_1922 arg2, class_2338 arg3) {
		return this.method_11614().method_9594(this, arg, arg2, arg3);
	}

	public int method_11577(class_1922 arg, class_2338 arg2, class_2350 arg3) {
		return this.method_11614().method_9603(this, arg, arg2, arg3);
	}

	public class_3619 method_11586() {
		return this.method_11614().method_9527(this);
	}

	public boolean method_11598(class_1922 arg, class_2338 arg2) {
		return this.field_12290 != null ? this.field_12290.field_16557 : this.method_11614().method_9557(this, arg, arg2);
	}

	public boolean method_11619() {
		return this.field_12290 != null ? this.field_12290.field_16558 : this.method_11614().method_9601(this);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11592(class_2680 arg, class_2350 arg2) {
		return this.method_11614().method_9522(this, arg, arg2);
	}

	public class_265 method_17770(class_1922 arg, class_2338 arg2) {
		return this.method_11606(arg, arg2, class_3726.method_16194());
	}

	public class_265 method_11606(class_1922 arg, class_2338 arg2, class_3726 arg3) {
		return this.method_11614().method_9530(this, arg, arg2, arg3);
	}

	public class_265 method_11628(class_1922 arg, class_2338 arg2) {
		return this.method_16337(arg, arg2, class_3726.method_16194());
	}

	public class_265 method_16337(class_1922 arg, class_2338 arg2, class_3726 arg3) {
		return this.method_11614().method_9549(this, arg, arg2, arg3);
	}

	public class_265 method_11615(class_1922 arg, class_2338 arg2) {
		return this.method_11614().method_9571(this, arg, arg2);
	}

	public class_265 method_11607(class_1922 arg, class_2338 arg2) {
		return this.method_11614().method_9584(this, arg, arg2);
	}

	public final boolean method_11631(class_1922 arg, class_2338 arg2, class_1297 arg3) {
		return class_2248.method_9501(this.method_16337(arg, arg2, class_3726.method_16195(arg3)), class_2350.field_11036);
	}

	public class_243 method_11599(class_1922 arg, class_2338 arg2) {
		return this.method_11614().method_9540(this, arg, arg2);
	}

	public boolean method_11583(class_1937 arg, class_2338 arg2, int i, int j) {
		return this.method_11614().method_9592(this, arg, arg2, i, j);
	}

	public void method_11622(class_1937 arg, class_2338 arg2, class_2248 arg3, class_2338 arg4, boolean bl) {
		this.method_11614().method_9612(this, arg, arg2, arg3, arg4, bl);
	}

	public void method_11635(class_1936 arg, class_2338 arg2, int i) {
		this.method_11614().method_9528(this, arg, arg2, i);
	}

	public void method_11637(class_1936 arg, class_2338 arg2, int i) {
		this.method_11614().method_9517(this, arg, arg2, i);
	}

	public void method_11580(class_1937 arg, class_2338 arg2, class_2680 arg3, boolean bl) {
		this.method_11614().method_9615(this, arg, arg2, arg3, bl);
	}

	public void method_11600(class_1937 arg, class_2338 arg2, class_2680 arg3, boolean bl) {
		this.method_11614().method_9536(this, arg, arg2, arg3, bl);
	}

	public void method_11585(class_1937 arg, class_2338 arg2, Random random) {
		this.method_11614().method_9588(this, arg, arg2, random);
	}

	public void method_11624(class_1937 arg, class_2338 arg2, Random random) {
		this.method_11614().method_9514(this, arg, arg2, random);
	}

	public void method_11613(class_1937 arg, class_2338 arg2, class_1297 arg3) {
		this.method_11614().method_9548(this, arg, arg2, arg3);
	}

	public void method_11595(class_1937 arg, class_2338 arg2, class_1799 arg3) {
		this.method_11614().method_9565(this, arg, arg2, arg3);
	}

	public List<class_1799> method_11612(class_47.class_48 arg) {
		return this.method_11614().method_9560(this, arg);
	}

	public boolean method_11629(class_1937 arg, class_1657 arg2, class_1268 arg3, class_3965 arg4) {
		return this.method_11614().method_9534(this, arg, arg4.method_17777(), arg2, arg3, arg4);
	}

	public void method_11636(class_1937 arg, class_2338 arg2, class_1657 arg3) {
		this.method_11614().method_9606(this, arg, arg2, arg3);
	}

	public boolean method_11582(class_1922 arg, class_2338 arg2) {
		return this.method_11614().method_16362(this, arg, arg2);
	}

	public class_2680 method_11578(class_2350 arg, class_2680 arg2, class_1936 arg3, class_2338 arg4, class_2338 arg5) {
		return this.method_11614().method_9559(this, arg, arg2, arg3, arg4, arg5);
	}

	public boolean method_11609(class_1922 arg, class_2338 arg2, class_10 arg3) {
		return this.method_11614().method_9516(this, arg, arg2, arg3);
	}

	public boolean method_11587(class_1750 arg) {
		return this.method_11614().method_9616(this, arg);
	}

	public boolean method_11591(class_1941 arg, class_2338 arg2) {
		return this.method_11614().method_9558(this, arg, arg2);
	}

	public boolean method_11601(class_1922 arg, class_2338 arg2) {
		return this.method_11614().method_9552(this, arg, arg2);
	}

	@Nullable
	public class_3908 method_17526(class_1937 arg, class_2338 arg2) {
		return this.method_11614().method_17454(this, arg, arg2);
	}

	public boolean method_11602(class_3494<class_2248> arg) {
		return this.method_11614().method_9525(arg);
	}

	public class_3610 method_11618() {
		return this.method_11614().method_9545(this);
	}

	public boolean method_11616() {
		return this.method_11614().method_9542(this);
	}

	@Environment(EnvType.CLIENT)
	public long method_11617(class_2338 arg) {
		return this.method_11614().method_9535(this, arg);
	}

	public class_2498 method_11638() {
		return this.method_11614().method_9573(this);
	}

	public void method_19287(class_1937 arg, class_2680 arg2, class_3965 arg3, class_1297 arg4) {
		this.method_11614().method_19286(arg, arg2, arg3, arg4);
	}

	public static <T> Dynamic<T> method_16550(DynamicOps<T> dynamicOps, class_2680 arg) {
		ImmutableMap<class_2769<?>, Comparable<?>> immutableMap = arg.method_11656();
		T object;
		if (immutableMap.isEmpty()) {
			object = dynamicOps.createMap(
				ImmutableMap.of(dynamicOps.createString("Name"), dynamicOps.createString(class_2378.field_11146.method_10221(arg.method_11614()).toString()))
			);
		} else {
			object = dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("Name"),
					dynamicOps.createString(class_2378.field_11146.method_10221(arg.method_11614()).toString()),
					dynamicOps.createString("Properties"),
					dynamicOps.createMap(
						(Map<T, T>)immutableMap.entrySet()
							.stream()
							.map(
								entry -> Pair.of(
										dynamicOps.createString(((class_2769)entry.getKey()).method_11899()),
										dynamicOps.createString(class_2688.method_16551((class_2769<T>)entry.getKey(), (Comparable<?>)entry.getValue()))
									)
							)
							.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))
					)
				)
			);
		}

		return new Dynamic<>(dynamicOps, object);
	}

	public static <T> class_2680 method_11633(Dynamic<T> dynamic) {
		class_2248 lv = class_2378.field_11146
			.method_10223(new class_2960((String)dynamic.getElement("Name").flatMap(dynamic.getOps()::getStringValue).orElse("minecraft:air")));
		Map<String, String> map = dynamic.get("Properties").asMap(dynamicx -> dynamicx.asString(""), dynamicx -> dynamicx.asString(""));
		class_2680 lv2 = lv.method_9564();
		class_2689<class_2248, class_2680> lv3 = lv.method_9595();

		for (Entry<String, String> entry : map.entrySet()) {
			String string = (String)entry.getKey();
			class_2769<?> lv4 = lv3.method_11663(string);
			if (lv4 != null) {
				lv2 = class_2688.method_11655(lv2, lv4, string, dynamic.toString(), (String)entry.getValue());
			}
		}

		return lv2;
	}

	static final class class_3752 {
		private static final class_2350[] field_16559 = class_2350.values();
		private final boolean field_16558;
		private final boolean field_16557;
		private final boolean field_16556;
		private final int field_16555;
		private final class_265[] field_16560;
		private final boolean field_17651;

		private class_3752(class_2680 arg) {
			class_2248 lv = arg.method_11614();
			this.field_16558 = lv.method_9601(arg);
			this.field_16557 = lv.method_9557(arg, class_2682.field_12294, class_2338.field_10980);
			this.field_16556 = lv.method_9579(arg, class_2682.field_12294, class_2338.field_10980);
			this.field_16555 = lv.method_9505(arg, class_2682.field_12294, class_2338.field_10980);
			if (!arg.method_11619()) {
				this.field_16560 = null;
			} else {
				this.field_16560 = new class_265[field_16559.length];
				class_265 lv2 = lv.method_9571(arg, class_2682.field_12294, class_2338.field_10980);

				for (class_2350 lv3 : field_16559) {
					this.field_16560[lv3.ordinal()] = class_259.method_16344(lv2, lv3);
				}
			}

			class_265 lv2 = lv.method_9549(arg, class_2682.field_12294, class_2338.field_10980, class_3726.method_16194());
			this.field_17651 = Arrays.stream(class_2350.class_2351.values()).anyMatch(arg2 -> lv2.method_1091(arg2) < 0.0 || lv2.method_1105(arg2) > 1.0);
		}
	}
}
