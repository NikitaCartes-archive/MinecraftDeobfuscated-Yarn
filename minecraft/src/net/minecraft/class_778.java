package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_778 {
	private final class_324 field_4178;
	private static final ThreadLocal<Object2IntLinkedOpenHashMap<class_2338>> field_4177 = ThreadLocal.withInitial(() -> {
		Object2IntLinkedOpenHashMap<class_2338> object2IntLinkedOpenHashMap = new Object2IntLinkedOpenHashMap<class_2338>(50) {
			@Override
			protected void rehash(int i) {
			}
		};
		object2IntLinkedOpenHashMap.defaultReturnValue(Integer.MAX_VALUE);
		return object2IntLinkedOpenHashMap;
	});
	private static final ThreadLocal<Boolean> field_4179 = ThreadLocal.withInitial(() -> false);

	public class_778(class_324 arg) {
		this.field_4178 = arg;
	}

	public boolean method_3374(class_1920 arg, class_1087 arg2, class_2680 arg3, class_2338 arg4, class_287 arg5, boolean bl, Random random, long l) {
		boolean bl2 = class_310.method_1588() && arg3.method_11630() == 0 && arg2.method_4708();

		try {
			return bl2 ? this.method_3361(arg, arg2, arg3, arg4, arg5, bl, random, l) : this.method_3373(arg, arg2, arg3, arg4, arg5, bl, random, l);
		} catch (Throwable var14) {
			class_128 lv = class_128.method_560(var14, "Tesselating block model");
			class_129 lv2 = lv.method_562("Block model being tesselated");
			class_129.method_586(lv2, arg4, arg3);
			lv2.method_578("Using AO", bl2);
			throw new class_148(lv);
		}
	}

	public boolean method_3361(class_1920 arg, class_1087 arg2, class_2680 arg3, class_2338 arg4, class_287 arg5, boolean bl, Random random, long l) {
		boolean bl2 = false;
		float[] fs = new float[class_2350.values().length * 2];
		BitSet bitSet = new BitSet(3);
		class_778.class_780 lv = new class_778.class_780();

		for (class_2350 lv2 : class_2350.values()) {
			random.setSeed(l);
			List<class_777> list = arg2.method_4707(arg3, lv2, random);
			if (!list.isEmpty() && (!bl || class_2248.method_9607(arg3, arg, arg4, lv2))) {
				this.method_3363(arg, arg3, arg4, arg5, list, fs, bitSet, lv);
				bl2 = true;
			}
		}

		random.setSeed(l);
		List<class_777> list2 = arg2.method_4707(arg3, null, random);
		if (!list2.isEmpty()) {
			this.method_3363(arg, arg3, arg4, arg5, list2, fs, bitSet, lv);
			bl2 = true;
		}

		return bl2;
	}

	public boolean method_3373(class_1920 arg, class_1087 arg2, class_2680 arg3, class_2338 arg4, class_287 arg5, boolean bl, Random random, long l) {
		boolean bl2 = false;
		BitSet bitSet = new BitSet(3);

		for (class_2350 lv : class_2350.values()) {
			random.setSeed(l);
			List<class_777> list = arg2.method_4707(arg3, lv, random);
			if (!list.isEmpty() && (!bl || class_2248.method_9607(arg3, arg, arg4, lv))) {
				int i = arg3.method_11632(arg, arg4.method_10093(lv));
				this.method_3370(arg, arg3, arg4, i, false, arg5, list, bitSet);
				bl2 = true;
			}
		}

		random.setSeed(l);
		List<class_777> list2 = arg2.method_4707(arg3, null, random);
		if (!list2.isEmpty()) {
			this.method_3370(arg, arg3, arg4, -1, true, arg5, list2, bitSet);
			bl2 = true;
		}

		return bl2;
	}

	private void method_3363(
		class_1920 arg, class_2680 arg2, class_2338 arg3, class_287 arg4, List<class_777> list, float[] fs, BitSet bitSet, class_778.class_780 arg5
	) {
		class_243 lv = arg2.method_11599(arg, arg3);
		double d = (double)arg3.method_10263() + lv.field_1352;
		double e = (double)arg3.method_10264() + lv.field_1351;
		double f = (double)arg3.method_10260() + lv.field_1350;
		int i = 0;

		for (int j = list.size(); i < j; i++) {
			class_777 lv2 = (class_777)list.get(i);
			this.method_3364(arg, arg2, arg3, lv2.method_3357(), lv2.method_3358(), fs, bitSet);
			arg5.method_3388(arg, arg2, arg3, lv2.method_3358(), fs, bitSet);
			arg4.method_1333(lv2.method_3357());
			arg4.method_1339(arg5.field_4194[0], arg5.field_4194[1], arg5.field_4194[2], arg5.field_4194[3]);
			if (lv2.method_3360()) {
				int k = this.field_4178.method_1697(arg2, arg, arg3, lv2.method_3359());
				float g = (float)(k >> 16 & 0xFF) / 255.0F;
				float h = (float)(k >> 8 & 0xFF) / 255.0F;
				float l = (float)(k & 0xFF) / 255.0F;
				arg4.method_1317(arg5.field_4196[0] * g, arg5.field_4196[0] * h, arg5.field_4196[0] * l, 4);
				arg4.method_1317(arg5.field_4196[1] * g, arg5.field_4196[1] * h, arg5.field_4196[1] * l, 3);
				arg4.method_1317(arg5.field_4196[2] * g, arg5.field_4196[2] * h, arg5.field_4196[2] * l, 2);
				arg4.method_1317(arg5.field_4196[3] * g, arg5.field_4196[3] * h, arg5.field_4196[3] * l, 1);
			} else {
				arg4.method_1317(arg5.field_4196[0], arg5.field_4196[0], arg5.field_4196[0], 4);
				arg4.method_1317(arg5.field_4196[1], arg5.field_4196[1], arg5.field_4196[1], 3);
				arg4.method_1317(arg5.field_4196[2], arg5.field_4196[2], arg5.field_4196[2], 2);
				arg4.method_1317(arg5.field_4196[3], arg5.field_4196[3], arg5.field_4196[3], 1);
			}

			arg4.method_1322(d, e, f);
		}
	}

	private void method_3364(class_1920 arg, class_2680 arg2, class_2338 arg3, int[] is, class_2350 arg4, @Nullable float[] fs, BitSet bitSet) {
		float f = 32.0F;
		float g = 32.0F;
		float h = 32.0F;
		float i = -32.0F;
		float j = -32.0F;
		float k = -32.0F;

		for (int l = 0; l < 4; l++) {
			float m = Float.intBitsToFloat(is[l * 7]);
			float n = Float.intBitsToFloat(is[l * 7 + 1]);
			float o = Float.intBitsToFloat(is[l * 7 + 2]);
			f = Math.min(f, m);
			g = Math.min(g, n);
			h = Math.min(h, o);
			i = Math.max(i, m);
			j = Math.max(j, n);
			k = Math.max(k, o);
		}

		if (fs != null) {
			fs[class_2350.field_11039.method_10146()] = f;
			fs[class_2350.field_11034.method_10146()] = i;
			fs[class_2350.field_11033.method_10146()] = g;
			fs[class_2350.field_11036.method_10146()] = j;
			fs[class_2350.field_11043.method_10146()] = h;
			fs[class_2350.field_11035.method_10146()] = k;
			int l = class_2350.values().length;
			fs[class_2350.field_11039.method_10146() + l] = 1.0F - f;
			fs[class_2350.field_11034.method_10146() + l] = 1.0F - i;
			fs[class_2350.field_11033.method_10146() + l] = 1.0F - g;
			fs[class_2350.field_11036.method_10146() + l] = 1.0F - j;
			fs[class_2350.field_11043.method_10146() + l] = 1.0F - h;
			fs[class_2350.field_11035.method_10146() + l] = 1.0F - k;
		}

		float p = 1.0E-4F;
		float m = 0.9999F;
		switch (arg4) {
			case field_11033:
				bitSet.set(1, f >= 1.0E-4F || h >= 1.0E-4F || i <= 0.9999F || k <= 0.9999F);
				bitSet.set(0, (g < 1.0E-4F || class_2248.method_9614(arg2.method_11628(arg, arg3))) && g == j);
				break;
			case field_11036:
				bitSet.set(1, f >= 1.0E-4F || h >= 1.0E-4F || i <= 0.9999F || k <= 0.9999F);
				bitSet.set(0, (j > 0.9999F || class_2248.method_9614(arg2.method_11628(arg, arg3))) && g == j);
				break;
			case field_11043:
				bitSet.set(1, f >= 1.0E-4F || g >= 1.0E-4F || i <= 0.9999F || j <= 0.9999F);
				bitSet.set(0, (h < 1.0E-4F || class_2248.method_9614(arg2.method_11628(arg, arg3))) && h == k);
				break;
			case field_11035:
				bitSet.set(1, f >= 1.0E-4F || g >= 1.0E-4F || i <= 0.9999F || j <= 0.9999F);
				bitSet.set(0, (k > 0.9999F || class_2248.method_9614(arg2.method_11628(arg, arg3))) && h == k);
				break;
			case field_11039:
				bitSet.set(1, g >= 1.0E-4F || h >= 1.0E-4F || j <= 0.9999F || k <= 0.9999F);
				bitSet.set(0, (f < 1.0E-4F || class_2248.method_9614(arg2.method_11628(arg, arg3))) && f == i);
				break;
			case field_11034:
				bitSet.set(1, g >= 1.0E-4F || h >= 1.0E-4F || j <= 0.9999F || k <= 0.9999F);
				bitSet.set(0, (i > 0.9999F || class_2248.method_9614(arg2.method_11628(arg, arg3))) && f == i);
		}
	}

	private void method_3370(class_1920 arg, class_2680 arg2, class_2338 arg3, int i, boolean bl, class_287 arg4, List<class_777> list, BitSet bitSet) {
		class_243 lv = arg2.method_11599(arg, arg3);
		double d = (double)arg3.method_10263() + lv.field_1352;
		double e = (double)arg3.method_10264() + lv.field_1351;
		double f = (double)arg3.method_10260() + lv.field_1350;
		int j = 0;

		for (int k = list.size(); j < k; j++) {
			class_777 lv2 = (class_777)list.get(j);
			if (bl) {
				this.method_3364(arg, arg2, arg3, lv2.method_3357(), lv2.method_3358(), null, bitSet);
				class_2338 lv3 = bitSet.get(0) ? arg3.method_10093(lv2.method_3358()) : arg3;
				i = arg2.method_11632(arg, lv3);
			}

			arg4.method_1333(lv2.method_3357());
			arg4.method_1339(i, i, i, i);
			if (lv2.method_3360()) {
				int l = this.field_4178.method_1697(arg2, arg, arg3, lv2.method_3359());
				float g = (float)(l >> 16 & 0xFF) / 255.0F;
				float h = (float)(l >> 8 & 0xFF) / 255.0F;
				float m = (float)(l & 0xFF) / 255.0F;
				arg4.method_1317(g, h, m, 4);
				arg4.method_1317(g, h, m, 3);
				arg4.method_1317(g, h, m, 2);
				arg4.method_1317(g, h, m, 1);
			}

			arg4.method_1322(d, e, f);
		}
	}

	public void method_3368(class_1087 arg, float f, float g, float h, float i) {
		this.method_3367(null, arg, f, g, h, i);
	}

	public void method_3367(@Nullable class_2680 arg, class_1087 arg2, float f, float g, float h, float i) {
		Random random = new Random();
		long l = 42L;

		for (class_2350 lv : class_2350.values()) {
			random.setSeed(42L);
			this.method_3365(f, g, h, i, arg2.method_4707(arg, lv, random));
		}

		random.setSeed(42L);
		this.method_3365(f, g, h, i, arg2.method_4707(arg, null, random));
	}

	public void method_3366(class_1087 arg, class_2680 arg2, float f, boolean bl) {
		GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
		int i = this.field_4178.method_1697(arg2, null, null, 0);
		float g = (float)(i >> 16 & 0xFF) / 255.0F;
		float h = (float)(i >> 8 & 0xFF) / 255.0F;
		float j = (float)(i & 0xFF) / 255.0F;
		if (!bl) {
			GlStateManager.color4f(f, f, f, 1.0F);
		}

		this.method_3367(arg2, arg, f, g, h, j);
	}

	private void method_3365(float f, float g, float h, float i, List<class_777> list) {
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		int j = 0;

		for (int k = list.size(); j < k; j++) {
			class_777 lv3 = (class_777)list.get(j);
			lv2.method_1328(7, class_290.field_1590);
			lv2.method_1333(lv3.method_3357());
			if (lv3.method_3360()) {
				lv2.method_1330(g * f, h * f, i * f);
			} else {
				lv2.method_1330(f, f, f);
			}

			class_2382 lv4 = lv3.method_3358().method_10163();
			lv2.method_1320((float)lv4.method_10263(), (float)lv4.method_10264(), (float)lv4.method_10260());
			lv.method_1350();
		}
	}

	public static void method_3375() {
		field_4179.set(true);
	}

	public static void method_3376() {
		((Object2IntLinkedOpenHashMap)field_4177.get()).clear();
		field_4179.set(false);
	}

	private static int method_3371(class_2680 arg, class_1920 arg2, class_2338 arg3) {
		Boolean boolean_ = (Boolean)field_4179.get();
		Object2IntLinkedOpenHashMap<class_2338> object2IntLinkedOpenHashMap = null;
		if (boolean_) {
			object2IntLinkedOpenHashMap = (Object2IntLinkedOpenHashMap<class_2338>)field_4177.get();
			int i = object2IntLinkedOpenHashMap.getInt(arg3);
			if (i != Integer.MAX_VALUE) {
				return i;
			}
		}

		int i = arg.method_11632(arg2, arg3);
		if (object2IntLinkedOpenHashMap != null) {
			if (object2IntLinkedOpenHashMap.size() == 50) {
				object2IntLinkedOpenHashMap.removeFirstInt();
			}

			object2IntLinkedOpenHashMap.put(arg3.method_10062(), i);
		}

		return i;
	}

	@Environment(EnvType.CLIENT)
	public static enum class_779 {
		field_4181(
			new class_2350[]{class_2350.field_11039, class_2350.field_11034, class_2350.field_11043, class_2350.field_11035},
			0.5F,
			true,
			new class_778.class_782[]{
				class_778.class_782.field_4216,
				class_778.class_782.field_4213,
				class_778.class_782.field_4216,
				class_778.class_782.field_4221,
				class_778.class_782.field_4215,
				class_778.class_782.field_4221,
				class_778.class_782.field_4215,
				class_778.class_782.field_4213
			},
			new class_778.class_782[]{
				class_778.class_782.field_4216,
				class_778.class_782.field_4211,
				class_778.class_782.field_4216,
				class_778.class_782.field_4218,
				class_778.class_782.field_4215,
				class_778.class_782.field_4218,
				class_778.class_782.field_4215,
				class_778.class_782.field_4211
			},
			new class_778.class_782[]{
				class_778.class_782.field_4214,
				class_778.class_782.field_4211,
				class_778.class_782.field_4214,
				class_778.class_782.field_4218,
				class_778.class_782.field_4219,
				class_778.class_782.field_4218,
				class_778.class_782.field_4219,
				class_778.class_782.field_4211
			},
			new class_778.class_782[]{
				class_778.class_782.field_4214,
				class_778.class_782.field_4213,
				class_778.class_782.field_4214,
				class_778.class_782.field_4221,
				class_778.class_782.field_4219,
				class_778.class_782.field_4221,
				class_778.class_782.field_4219,
				class_778.class_782.field_4213
			}
		),
		field_4182(
			new class_2350[]{class_2350.field_11034, class_2350.field_11039, class_2350.field_11043, class_2350.field_11035},
			1.0F,
			true,
			new class_778.class_782[]{
				class_778.class_782.field_4219,
				class_778.class_782.field_4213,
				class_778.class_782.field_4219,
				class_778.class_782.field_4221,
				class_778.class_782.field_4214,
				class_778.class_782.field_4221,
				class_778.class_782.field_4214,
				class_778.class_782.field_4213
			},
			new class_778.class_782[]{
				class_778.class_782.field_4219,
				class_778.class_782.field_4211,
				class_778.class_782.field_4219,
				class_778.class_782.field_4218,
				class_778.class_782.field_4214,
				class_778.class_782.field_4218,
				class_778.class_782.field_4214,
				class_778.class_782.field_4211
			},
			new class_778.class_782[]{
				class_778.class_782.field_4215,
				class_778.class_782.field_4211,
				class_778.class_782.field_4215,
				class_778.class_782.field_4218,
				class_778.class_782.field_4216,
				class_778.class_782.field_4218,
				class_778.class_782.field_4216,
				class_778.class_782.field_4211
			},
			new class_778.class_782[]{
				class_778.class_782.field_4215,
				class_778.class_782.field_4213,
				class_778.class_782.field_4215,
				class_778.class_782.field_4221,
				class_778.class_782.field_4216,
				class_778.class_782.field_4221,
				class_778.class_782.field_4216,
				class_778.class_782.field_4213
			}
		),
		field_4183(
			new class_2350[]{class_2350.field_11036, class_2350.field_11033, class_2350.field_11034, class_2350.field_11039},
			0.8F,
			true,
			new class_778.class_782[]{
				class_778.class_782.field_4212,
				class_778.class_782.field_4216,
				class_778.class_782.field_4212,
				class_778.class_782.field_4215,
				class_778.class_782.field_4217,
				class_778.class_782.field_4215,
				class_778.class_782.field_4217,
				class_778.class_782.field_4216
			},
			new class_778.class_782[]{
				class_778.class_782.field_4212,
				class_778.class_782.field_4214,
				class_778.class_782.field_4212,
				class_778.class_782.field_4219,
				class_778.class_782.field_4217,
				class_778.class_782.field_4219,
				class_778.class_782.field_4217,
				class_778.class_782.field_4214
			},
			new class_778.class_782[]{
				class_778.class_782.field_4210,
				class_778.class_782.field_4214,
				class_778.class_782.field_4210,
				class_778.class_782.field_4219,
				class_778.class_782.field_4220,
				class_778.class_782.field_4219,
				class_778.class_782.field_4220,
				class_778.class_782.field_4214
			},
			new class_778.class_782[]{
				class_778.class_782.field_4210,
				class_778.class_782.field_4216,
				class_778.class_782.field_4210,
				class_778.class_782.field_4215,
				class_778.class_782.field_4220,
				class_778.class_782.field_4215,
				class_778.class_782.field_4220,
				class_778.class_782.field_4216
			}
		),
		field_4184(
			new class_2350[]{class_2350.field_11039, class_2350.field_11034, class_2350.field_11033, class_2350.field_11036},
			0.8F,
			true,
			new class_778.class_782[]{
				class_778.class_782.field_4212,
				class_778.class_782.field_4216,
				class_778.class_782.field_4217,
				class_778.class_782.field_4216,
				class_778.class_782.field_4217,
				class_778.class_782.field_4215,
				class_778.class_782.field_4212,
				class_778.class_782.field_4215
			},
			new class_778.class_782[]{
				class_778.class_782.field_4210,
				class_778.class_782.field_4216,
				class_778.class_782.field_4220,
				class_778.class_782.field_4216,
				class_778.class_782.field_4220,
				class_778.class_782.field_4215,
				class_778.class_782.field_4210,
				class_778.class_782.field_4215
			},
			new class_778.class_782[]{
				class_778.class_782.field_4210,
				class_778.class_782.field_4214,
				class_778.class_782.field_4220,
				class_778.class_782.field_4214,
				class_778.class_782.field_4220,
				class_778.class_782.field_4219,
				class_778.class_782.field_4210,
				class_778.class_782.field_4219
			},
			new class_778.class_782[]{
				class_778.class_782.field_4212,
				class_778.class_782.field_4214,
				class_778.class_782.field_4217,
				class_778.class_782.field_4214,
				class_778.class_782.field_4217,
				class_778.class_782.field_4219,
				class_778.class_782.field_4212,
				class_778.class_782.field_4219
			}
		),
		field_4187(
			new class_2350[]{class_2350.field_11036, class_2350.field_11033, class_2350.field_11043, class_2350.field_11035},
			0.6F,
			true,
			new class_778.class_782[]{
				class_778.class_782.field_4212,
				class_778.class_782.field_4213,
				class_778.class_782.field_4212,
				class_778.class_782.field_4221,
				class_778.class_782.field_4217,
				class_778.class_782.field_4221,
				class_778.class_782.field_4217,
				class_778.class_782.field_4213
			},
			new class_778.class_782[]{
				class_778.class_782.field_4212,
				class_778.class_782.field_4211,
				class_778.class_782.field_4212,
				class_778.class_782.field_4218,
				class_778.class_782.field_4217,
				class_778.class_782.field_4218,
				class_778.class_782.field_4217,
				class_778.class_782.field_4211
			},
			new class_778.class_782[]{
				class_778.class_782.field_4210,
				class_778.class_782.field_4211,
				class_778.class_782.field_4210,
				class_778.class_782.field_4218,
				class_778.class_782.field_4220,
				class_778.class_782.field_4218,
				class_778.class_782.field_4220,
				class_778.class_782.field_4211
			},
			new class_778.class_782[]{
				class_778.class_782.field_4210,
				class_778.class_782.field_4213,
				class_778.class_782.field_4210,
				class_778.class_782.field_4221,
				class_778.class_782.field_4220,
				class_778.class_782.field_4221,
				class_778.class_782.field_4220,
				class_778.class_782.field_4213
			}
		),
		field_4186(
			new class_2350[]{class_2350.field_11033, class_2350.field_11036, class_2350.field_11043, class_2350.field_11035},
			0.6F,
			true,
			new class_778.class_782[]{
				class_778.class_782.field_4220,
				class_778.class_782.field_4213,
				class_778.class_782.field_4220,
				class_778.class_782.field_4221,
				class_778.class_782.field_4210,
				class_778.class_782.field_4221,
				class_778.class_782.field_4210,
				class_778.class_782.field_4213
			},
			new class_778.class_782[]{
				class_778.class_782.field_4220,
				class_778.class_782.field_4211,
				class_778.class_782.field_4220,
				class_778.class_782.field_4218,
				class_778.class_782.field_4210,
				class_778.class_782.field_4218,
				class_778.class_782.field_4210,
				class_778.class_782.field_4211
			},
			new class_778.class_782[]{
				class_778.class_782.field_4217,
				class_778.class_782.field_4211,
				class_778.class_782.field_4217,
				class_778.class_782.field_4218,
				class_778.class_782.field_4212,
				class_778.class_782.field_4218,
				class_778.class_782.field_4212,
				class_778.class_782.field_4211
			},
			new class_778.class_782[]{
				class_778.class_782.field_4217,
				class_778.class_782.field_4213,
				class_778.class_782.field_4217,
				class_778.class_782.field_4221,
				class_778.class_782.field_4212,
				class_778.class_782.field_4221,
				class_778.class_782.field_4212,
				class_778.class_782.field_4213
			}
		);

		private final class_2350[] field_4191;
		private final boolean field_4189;
		private final class_778.class_782[] field_4192;
		private final class_778.class_782[] field_4185;
		private final class_778.class_782[] field_4180;
		private final class_778.class_782[] field_4188;
		private static final class_778.class_779[] field_4190 = class_156.method_654(new class_778.class_779[6], args -> {
			args[class_2350.field_11033.method_10146()] = field_4181;
			args[class_2350.field_11036.method_10146()] = field_4182;
			args[class_2350.field_11043.method_10146()] = field_4183;
			args[class_2350.field_11035.method_10146()] = field_4184;
			args[class_2350.field_11039.method_10146()] = field_4187;
			args[class_2350.field_11034.method_10146()] = field_4186;
		});

		private class_779(
			class_2350[] args, float f, boolean bl, class_778.class_782[] args2, class_778.class_782[] args3, class_778.class_782[] args4, class_778.class_782[] args5
		) {
			this.field_4191 = args;
			this.field_4189 = bl;
			this.field_4192 = args2;
			this.field_4185 = args3;
			this.field_4180 = args4;
			this.field_4188 = args5;
		}

		public static class_778.class_779 method_3378(class_2350 arg) {
			return field_4190[arg.method_10146()];
		}
	}

	@Environment(EnvType.CLIENT)
	class class_780 {
		private final float[] field_4196 = new float[4];
		private final int[] field_4194 = new int[4];

		public class_780() {
		}

		public void method_3388(class_1920 arg, class_2680 arg2, class_2338 arg3, class_2350 arg4, float[] fs, BitSet bitSet) {
			class_2338 lv = bitSet.get(0) ? arg3.method_10093(arg4) : arg3;
			class_778.class_779 lv2 = class_778.class_779.method_3378(arg4);
			class_2338.class_2339 lv3 = new class_2338.class_2339();
			lv3.method_10101(lv).method_10098(lv2.field_4191[0]);
			int i = class_778.method_3371(arg2, arg, lv3);
			float f = arg.method_8320(lv3).method_11596(arg, lv3);
			lv3.method_10101(lv).method_10098(lv2.field_4191[1]);
			int j = class_778.method_3371(arg2, arg, lv3);
			float g = arg.method_8320(lv3).method_11596(arg, lv3);
			lv3.method_10101(lv).method_10098(lv2.field_4191[2]);
			int k = class_778.method_3371(arg2, arg, lv3);
			float h = arg.method_8320(lv3).method_11596(arg, lv3);
			lv3.method_10101(lv).method_10098(lv2.field_4191[3]);
			int l = class_778.method_3371(arg2, arg, lv3);
			float m = arg.method_8320(lv3).method_11596(arg, lv3);
			lv3.method_10101(lv).method_10098(lv2.field_4191[0]).method_10098(arg4);
			boolean bl = arg.method_8320(lv3).method_11581(arg, lv3) == 0;
			lv3.method_10101(lv).method_10098(lv2.field_4191[1]).method_10098(arg4);
			boolean bl2 = arg.method_8320(lv3).method_11581(arg, lv3) == 0;
			lv3.method_10101(lv).method_10098(lv2.field_4191[2]).method_10098(arg4);
			boolean bl3 = arg.method_8320(lv3).method_11581(arg, lv3) == 0;
			lv3.method_10101(lv).method_10098(lv2.field_4191[3]).method_10098(arg4);
			boolean bl4 = arg.method_8320(lv3).method_11581(arg, lv3) == 0;
			float n;
			int o;
			if (!bl3 && !bl) {
				n = f;
				o = i;
			} else {
				lv3.method_10101(lv).method_10098(lv2.field_4191[0]).method_10098(lv2.field_4191[2]);
				n = arg.method_8320(lv3).method_11596(arg, lv3);
				o = class_778.method_3371(arg2, arg, lv3);
			}

			float p;
			int q;
			if (!bl4 && !bl) {
				p = f;
				q = i;
			} else {
				lv3.method_10101(lv).method_10098(lv2.field_4191[0]).method_10098(lv2.field_4191[3]);
				p = arg.method_8320(lv3).method_11596(arg, lv3);
				q = class_778.method_3371(arg2, arg, lv3);
			}

			float r;
			int s;
			if (!bl3 && !bl2) {
				r = g;
				s = j;
			} else {
				lv3.method_10101(lv).method_10098(lv2.field_4191[1]).method_10098(lv2.field_4191[2]);
				r = arg.method_8320(lv3).method_11596(arg, lv3);
				s = class_778.method_3371(arg2, arg, lv3);
			}

			float t;
			int u;
			if (!bl4 && !bl2) {
				t = g;
				u = j;
			} else {
				lv3.method_10101(lv).method_10098(lv2.field_4191[1]).method_10098(lv2.field_4191[3]);
				t = arg.method_8320(lv3).method_11596(arg, lv3);
				u = class_778.method_3371(arg2, arg, lv3);
			}

			int v = class_778.method_3371(arg2, arg, arg3);
			lv3.method_10101(arg3).method_10098(arg4);
			if (bitSet.get(0) || !arg.method_8320(lv3).method_11598(arg, lv3)) {
				v = class_778.method_3371(arg2, arg, lv3);
			}

			float w = bitSet.get(0) ? arg.method_8320(lv).method_11596(arg, lv) : arg.method_8320(arg3).method_11596(arg, arg3);
			class_778.class_781 lv4 = class_778.class_781.method_3394(arg4);
			if (bitSet.get(1) && lv2.field_4189) {
				float x = (m + f + p + w) * 0.25F;
				float y = (h + f + n + w) * 0.25F;
				float z = (h + g + r + w) * 0.25F;
				float aa = (m + g + t + w) * 0.25F;
				float ab = fs[lv2.field_4192[0].field_4222] * fs[lv2.field_4192[1].field_4222];
				float ac = fs[lv2.field_4192[2].field_4222] * fs[lv2.field_4192[3].field_4222];
				float ad = fs[lv2.field_4192[4].field_4222] * fs[lv2.field_4192[5].field_4222];
				float ae = fs[lv2.field_4192[6].field_4222] * fs[lv2.field_4192[7].field_4222];
				float af = fs[lv2.field_4185[0].field_4222] * fs[lv2.field_4185[1].field_4222];
				float ag = fs[lv2.field_4185[2].field_4222] * fs[lv2.field_4185[3].field_4222];
				float ah = fs[lv2.field_4185[4].field_4222] * fs[lv2.field_4185[5].field_4222];
				float ai = fs[lv2.field_4185[6].field_4222] * fs[lv2.field_4185[7].field_4222];
				float aj = fs[lv2.field_4180[0].field_4222] * fs[lv2.field_4180[1].field_4222];
				float ak = fs[lv2.field_4180[2].field_4222] * fs[lv2.field_4180[3].field_4222];
				float al = fs[lv2.field_4180[4].field_4222] * fs[lv2.field_4180[5].field_4222];
				float am = fs[lv2.field_4180[6].field_4222] * fs[lv2.field_4180[7].field_4222];
				float an = fs[lv2.field_4188[0].field_4222] * fs[lv2.field_4188[1].field_4222];
				float ao = fs[lv2.field_4188[2].field_4222] * fs[lv2.field_4188[3].field_4222];
				float ap = fs[lv2.field_4188[4].field_4222] * fs[lv2.field_4188[5].field_4222];
				float aq = fs[lv2.field_4188[6].field_4222] * fs[lv2.field_4188[7].field_4222];
				this.field_4196[lv4.field_4203] = x * ab + y * ac + z * ad + aa * ae;
				this.field_4196[lv4.field_4201] = x * af + y * ag + z * ah + aa * ai;
				this.field_4196[lv4.field_4198] = x * aj + y * ak + z * al + aa * am;
				this.field_4196[lv4.field_4209] = x * an + y * ao + z * ap + aa * aq;
				int ar = this.method_3386(l, i, q, v);
				int as = this.method_3386(k, i, o, v);
				int at = this.method_3386(k, j, s, v);
				int au = this.method_3386(l, j, u, v);
				this.field_4194[lv4.field_4203] = this.method_3389(ar, as, at, au, ab, ac, ad, ae);
				this.field_4194[lv4.field_4201] = this.method_3389(ar, as, at, au, af, ag, ah, ai);
				this.field_4194[lv4.field_4198] = this.method_3389(ar, as, at, au, aj, ak, al, am);
				this.field_4194[lv4.field_4209] = this.method_3389(ar, as, at, au, an, ao, ap, aq);
			} else {
				float x = (m + f + p + w) * 0.25F;
				float y = (h + f + n + w) * 0.25F;
				float z = (h + g + r + w) * 0.25F;
				float aa = (m + g + t + w) * 0.25F;
				this.field_4194[lv4.field_4203] = this.method_3386(l, i, q, v);
				this.field_4194[lv4.field_4201] = this.method_3386(k, i, o, v);
				this.field_4194[lv4.field_4198] = this.method_3386(k, j, s, v);
				this.field_4194[lv4.field_4209] = this.method_3386(l, j, u, v);
				this.field_4196[lv4.field_4203] = x;
				this.field_4196[lv4.field_4201] = y;
				this.field_4196[lv4.field_4198] = z;
				this.field_4196[lv4.field_4209] = aa;
			}
		}

		private int method_3386(int i, int j, int k, int l) {
			if (i == 0) {
				i = l;
			}

			if (j == 0) {
				j = l;
			}

			if (k == 0) {
				k = l;
			}

			return i + j + k + l >> 2 & 16711935;
		}

		private int method_3389(int i, int j, int k, int l, float f, float g, float h, float m) {
			int n = (int)((float)(i >> 16 & 0xFF) * f + (float)(j >> 16 & 0xFF) * g + (float)(k >> 16 & 0xFF) * h + (float)(l >> 16 & 0xFF) * m) & 0xFF;
			int o = (int)((float)(i & 0xFF) * f + (float)(j & 0xFF) * g + (float)(k & 0xFF) * h + (float)(l & 0xFF) * m) & 0xFF;
			return n << 16 | o;
		}
	}

	@Environment(EnvType.CLIENT)
	static enum class_781 {
		field_4199(0, 1, 2, 3),
		field_4200(2, 3, 0, 1),
		field_4204(3, 0, 1, 2),
		field_4205(0, 1, 2, 3),
		field_4206(3, 0, 1, 2),
		field_4207(1, 2, 3, 0);

		private final int field_4203;
		private final int field_4201;
		private final int field_4198;
		private final int field_4209;
		private static final class_778.class_781[] field_4202 = class_156.method_654(new class_778.class_781[6], args -> {
			args[class_2350.field_11033.method_10146()] = field_4199;
			args[class_2350.field_11036.method_10146()] = field_4200;
			args[class_2350.field_11043.method_10146()] = field_4204;
			args[class_2350.field_11035.method_10146()] = field_4205;
			args[class_2350.field_11039.method_10146()] = field_4206;
			args[class_2350.field_11034.method_10146()] = field_4207;
		});

		private class_781(int j, int k, int l, int m) {
			this.field_4203 = j;
			this.field_4201 = k;
			this.field_4198 = l;
			this.field_4209 = m;
		}

		public static class_778.class_781 method_3394(class_2350 arg) {
			return field_4202[arg.method_10146()];
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum class_782 {
		field_4210(class_2350.field_11033, false),
		field_4212(class_2350.field_11036, false),
		field_4211(class_2350.field_11043, false),
		field_4213(class_2350.field_11035, false),
		field_4215(class_2350.field_11039, false),
		field_4219(class_2350.field_11034, false),
		field_4220(class_2350.field_11033, true),
		field_4217(class_2350.field_11036, true),
		field_4218(class_2350.field_11043, true),
		field_4221(class_2350.field_11035, true),
		field_4216(class_2350.field_11039, true),
		field_4214(class_2350.field_11034, true);

		private final int field_4222;

		private class_782(class_2350 arg, boolean bl) {
			this.field_4222 = arg.method_10146() + (bl ? class_2350.values().length : 0);
		}
	}
}
