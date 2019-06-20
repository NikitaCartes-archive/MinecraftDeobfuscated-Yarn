package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class class_3609 extends class_3611 {
	public static final class_2746 field_15902 = class_2741.field_12480;
	public static final class_2758 field_15900 = class_2741.field_12490;
	private static final ThreadLocal<Object2ByteLinkedOpenHashMap<class_2248.class_2249>> field_15901 = ThreadLocal.withInitial(() -> {
		Object2ByteLinkedOpenHashMap<class_2248.class_2249> object2ByteLinkedOpenHashMap = new Object2ByteLinkedOpenHashMap<class_2248.class_2249>(200) {
			@Override
			protected void rehash(int i) {
			}
		};
		object2ByteLinkedOpenHashMap.defaultReturnValue((byte)127);
		return object2ByteLinkedOpenHashMap;
	});
	private final Map<class_3610, class_265> field_17587 = Maps.<class_3610, class_265>newIdentityHashMap();

	@Override
	protected void method_15775(class_2689.class_2690<class_3611, class_3610> arg) {
		arg.method_11667(field_15902);
	}

	@Override
	public class_243 method_15782(class_1922 arg, class_2338 arg2, class_3610 arg3) {
		double d = 0.0;
		double e = 0.0;

		class_243 var28;
		try (class_2338.class_2340 lv = class_2338.class_2340.method_10109()) {
			for (class_2350 lv2 : class_2350.class_2353.field_11062) {
				lv.method_10114(arg2).method_10118(lv2);
				class_3610 lv3 = arg.method_8316(lv);
				if (this.method_15748(lv3)) {
					float f = lv3.method_20785();
					float g = 0.0F;
					if (f == 0.0F) {
						if (!arg.method_8320(lv).method_11620().method_15801()) {
							class_2338 lv4 = lv.method_10074();
							class_3610 lv5 = arg.method_8316(lv4);
							if (this.method_15748(lv5)) {
								f = lv5.method_20785();
								if (f > 0.0F) {
									g = arg3.method_20785() - (f - 0.8888889F);
								}
							}
						}
					} else if (f > 0.0F) {
						g = arg3.method_20785() - f;
					}

					if (g != 0.0F) {
						d += (double)((float)lv2.method_10148() * g);
						e += (double)((float)lv2.method_10165() * g);
					}
				}
			}

			class_243 lv6 = new class_243(d, 0.0, e);
			if ((Boolean)arg3.method_11654(field_15902)) {
				for (class_2350 lv7 : class_2350.class_2353.field_11062) {
					lv.method_10114(arg2).method_10118(lv7);
					if (this.method_15749(arg, lv, lv7) || this.method_15749(arg, lv.method_10084(), lv7)) {
						lv6 = lv6.method_1029().method_1031(0.0, -6.0, 0.0);
						break;
					}
				}
			}

			var28 = lv6.method_1029();
		}

		return var28;
	}

	private boolean method_15748(class_3610 arg) {
		return arg.method_15769() || arg.method_15772().method_15780(this);
	}

	protected boolean method_15749(class_1922 arg, class_2338 arg2, class_2350 arg3) {
		class_2680 lv = arg.method_8320(arg2);
		class_3610 lv2 = arg.method_8316(arg2);
		if (lv2.method_15772().method_15780(this)) {
			return false;
		} else if (arg3 == class_2350.field_11036) {
			return true;
		} else {
			return lv.method_11620() == class_3614.field_15958 ? false : class_2248.method_20045(lv, arg, arg2, arg3);
		}
	}

	protected void method_15725(class_1936 arg, class_2338 arg2, class_3610 arg3) {
		if (!arg3.method_15769()) {
			class_2680 lv = arg.method_8320(arg2);
			class_2338 lv2 = arg2.method_10074();
			class_2680 lv3 = arg.method_8320(lv2);
			class_3610 lv4 = this.method_15727(arg, lv2, lv3);
			if (this.method_15738(arg, arg2, lv, class_2350.field_11033, lv2, lv3, arg.method_8316(lv2), lv4.method_15772())) {
				this.method_15745(arg, lv2, lv3, class_2350.field_11033, lv4);
				if (this.method_15740(arg, arg2) >= 3) {
					this.method_15744(arg, arg2, arg3, lv);
				}
			} else if (arg3.method_15771() || !this.method_15736(arg, lv4.method_15772(), arg2, lv, lv2, lv3)) {
				this.method_15744(arg, arg2, arg3, lv);
			}
		}
	}

	private void method_15744(class_1936 arg, class_2338 arg2, class_3610 arg3, class_2680 arg4) {
		int i = arg3.method_15761() - this.method_15739(arg);
		if ((Boolean)arg3.method_11654(field_15902)) {
			i = 7;
		}

		if (i > 0) {
			Map<class_2350, class_3610> map = this.method_15726(arg, arg2, arg4);

			for (Entry<class_2350, class_3610> entry : map.entrySet()) {
				class_2350 lv = (class_2350)entry.getKey();
				class_3610 lv2 = (class_3610)entry.getValue();
				class_2338 lv3 = arg2.method_10093(lv);
				class_2680 lv4 = arg.method_8320(lv3);
				if (this.method_15738(arg, arg2, arg4, lv, lv3, lv4, arg.method_8316(lv3), lv2.method_15772())) {
					this.method_15745(arg, lv3, lv4, lv, lv2);
				}
			}
		}
	}

	protected class_3610 method_15727(class_1941 arg, class_2338 arg2, class_2680 arg3) {
		int i = 0;
		int j = 0;

		for (class_2350 lv : class_2350.class_2353.field_11062) {
			class_2338 lv2 = arg2.method_10093(lv);
			class_2680 lv3 = arg.method_8320(lv2);
			class_3610 lv4 = lv3.method_11618();
			if (lv4.method_15772().method_15780(this) && this.method_15732(lv, arg, arg2, arg3, lv2, lv3)) {
				if (lv4.method_15771()) {
					j++;
				}

				i = Math.max(i, lv4.method_15761());
			}
		}

		if (this.method_15737() && j >= 2) {
			class_2680 lv5 = arg.method_8320(arg2.method_10074());
			class_3610 lv6 = lv5.method_11618();
			if (lv5.method_11620().method_15799() || this.method_15752(lv6)) {
				return this.method_15729(false);
			}
		}

		class_2338 lv7 = arg2.method_10084();
		class_2680 lv8 = arg.method_8320(lv7);
		class_3610 lv9 = lv8.method_11618();
		if (!lv9.method_15769() && lv9.method_15772().method_15780(this) && this.method_15732(class_2350.field_11036, arg, arg2, arg3, lv7, lv8)) {
			return this.method_15728(8, true);
		} else {
			int k = i - this.method_15739(arg);
			return k <= 0 ? class_3612.field_15906.method_15785() : this.method_15728(k, false);
		}
	}

	private boolean method_15732(class_2350 arg, class_1922 arg2, class_2338 arg3, class_2680 arg4, class_2338 arg5, class_2680 arg6) {
		Object2ByteLinkedOpenHashMap<class_2248.class_2249> object2ByteLinkedOpenHashMap;
		if (!arg4.method_11614().method_9543() && !arg6.method_11614().method_9543()) {
			object2ByteLinkedOpenHashMap = (Object2ByteLinkedOpenHashMap<class_2248.class_2249>)field_15901.get();
		} else {
			object2ByteLinkedOpenHashMap = null;
		}

		class_2248.class_2249 lv;
		if (object2ByteLinkedOpenHashMap != null) {
			lv = new class_2248.class_2249(arg4, arg6, arg);
			byte b = object2ByteLinkedOpenHashMap.getAndMoveToFirst(lv);
			if (b != 127) {
				return b != 0;
			}
		} else {
			lv = null;
		}

		class_265 lv2 = arg4.method_11628(arg2, arg3);
		class_265 lv3 = arg6.method_11628(arg2, arg5);
		boolean bl = !class_259.method_1080(lv2, lv3, arg);
		if (object2ByteLinkedOpenHashMap != null) {
			if (object2ByteLinkedOpenHashMap.size() == 200) {
				object2ByteLinkedOpenHashMap.removeLastByte();
			}

			object2ByteLinkedOpenHashMap.putAndMoveToFirst(lv, (byte)(bl ? 1 : 0));
		}

		return bl;
	}

	public abstract class_3611 method_15750();

	public class_3610 method_15728(int i, boolean bl) {
		return this.method_15750().method_15785().method_11657(field_15900, Integer.valueOf(i)).method_11657(field_15902, Boolean.valueOf(bl));
	}

	public abstract class_3611 method_15751();

	public class_3610 method_15729(boolean bl) {
		return this.method_15751().method_15785().method_11657(field_15902, Boolean.valueOf(bl));
	}

	protected abstract boolean method_15737();

	protected void method_15745(class_1936 arg, class_2338 arg2, class_2680 arg3, class_2350 arg4, class_3610 arg5) {
		if (arg3.method_11614() instanceof class_2402) {
			((class_2402)arg3.method_11614()).method_10311(arg, arg2, arg3, arg5);
		} else {
			if (!arg3.method_11588()) {
				this.method_15730(arg, arg2, arg3);
			}

			arg.method_8652(arg2, arg5.method_15759(), 3);
		}
	}

	protected abstract void method_15730(class_1936 arg, class_2338 arg2, class_2680 arg3);

	private static short method_15747(class_2338 arg, class_2338 arg2) {
		int i = arg2.method_10263() - arg.method_10263();
		int j = arg2.method_10260() - arg.method_10260();
		return (short)((i + 128 & 0xFF) << 8 | j + 128 & 0xFF);
	}

	protected int method_15742(
		class_1941 arg,
		class_2338 arg2,
		int i,
		class_2350 arg3,
		class_2680 arg4,
		class_2338 arg5,
		Short2ObjectMap<Pair<class_2680, class_3610>> short2ObjectMap,
		Short2BooleanMap short2BooleanMap
	) {
		int j = 1000;

		for (class_2350 lv : class_2350.class_2353.field_11062) {
			if (lv != arg3) {
				class_2338 lv2 = arg2.method_10093(lv);
				short s = method_15747(arg5, lv2);
				Pair<class_2680, class_3610> pair = short2ObjectMap.computeIfAbsent(s, ix -> {
					class_2680 lvx = arg.method_8320(lv2);
					return Pair.of(lvx, lvx.method_11618());
				});
				class_2680 lv3 = pair.getFirst();
				class_3610 lv4 = pair.getSecond();
				if (this.method_15746(arg, this.method_15750(), arg2, arg4, lv, lv2, lv3, lv4)) {
					boolean bl = short2BooleanMap.computeIfAbsent(s, ix -> {
						class_2338 lvx = lv2.method_10074();
						class_2680 lv2x = arg.method_8320(lvx);
						return this.method_15736(arg, this.method_15750(), lv2, lv3, lvx, lv2x);
					});
					if (bl) {
						return i;
					}

					if (i < this.method_15733(arg)) {
						int k = this.method_15742(arg, lv2, i + 1, lv.method_10153(), lv3, arg5, short2ObjectMap, short2BooleanMap);
						if (k < j) {
							j = k;
						}
					}
				}
			}
		}

		return j;
	}

	private boolean method_15736(class_1922 arg, class_3611 arg2, class_2338 arg3, class_2680 arg4, class_2338 arg5, class_2680 arg6) {
		if (!this.method_15732(class_2350.field_11033, arg, arg3, arg4, arg5, arg6)) {
			return false;
		} else {
			return arg6.method_11618().method_15772().method_15780(this) ? true : this.method_15754(arg, arg5, arg6, arg2);
		}
	}

	private boolean method_15746(
		class_1922 arg, class_3611 arg2, class_2338 arg3, class_2680 arg4, class_2350 arg5, class_2338 arg6, class_2680 arg7, class_3610 arg8
	) {
		return !this.method_15752(arg8) && this.method_15732(arg5, arg, arg3, arg4, arg6, arg7) && this.method_15754(arg, arg6, arg7, arg2);
	}

	private boolean method_15752(class_3610 arg) {
		return arg.method_15772().method_15780(this) && arg.method_15771();
	}

	protected abstract int method_15733(class_1941 arg);

	private int method_15740(class_1941 arg, class_2338 arg2) {
		int i = 0;

		for (class_2350 lv : class_2350.class_2353.field_11062) {
			class_2338 lv2 = arg2.method_10093(lv);
			class_3610 lv3 = arg.method_8316(lv2);
			if (this.method_15752(lv3)) {
				i++;
			}
		}

		return i;
	}

	protected Map<class_2350, class_3610> method_15726(class_1941 arg, class_2338 arg2, class_2680 arg3) {
		int i = 1000;
		Map<class_2350, class_3610> map = Maps.newEnumMap(class_2350.class);
		Short2ObjectMap<Pair<class_2680, class_3610>> short2ObjectMap = new Short2ObjectOpenHashMap<>();
		Short2BooleanMap short2BooleanMap = new Short2BooleanOpenHashMap();

		for (class_2350 lv : class_2350.class_2353.field_11062) {
			class_2338 lv2 = arg2.method_10093(lv);
			short s = method_15747(arg2, lv2);
			Pair<class_2680, class_3610> pair = short2ObjectMap.computeIfAbsent(s, ix -> {
				class_2680 lvx = arg.method_8320(lv2);
				return Pair.of(lvx, lvx.method_11618());
			});
			class_2680 lv3 = pair.getFirst();
			class_3610 lv4 = pair.getSecond();
			class_3610 lv5 = this.method_15727(arg, lv2, lv3);
			if (this.method_15746(arg, lv5.method_15772(), arg2, arg3, lv, lv2, lv3, lv4)) {
				class_2338 lv6 = lv2.method_10074();
				boolean bl = short2BooleanMap.computeIfAbsent(s, ix -> {
					class_2680 lvx = arg.method_8320(lv6);
					return this.method_15736(arg, this.method_15750(), lv2, lv3, lv6, lvx);
				});
				int j;
				if (bl) {
					j = 0;
				} else {
					j = this.method_15742(arg, lv2, 1, lv.method_10153(), lv3, arg2, short2ObjectMap, short2BooleanMap);
				}

				if (j < i) {
					map.clear();
				}

				if (j <= i) {
					map.put(lv, lv5);
					i = j;
				}
			}
		}

		return map;
	}

	private boolean method_15754(class_1922 arg, class_2338 arg2, class_2680 arg3, class_3611 arg4) {
		class_2248 lv = arg3.method_11614();
		if (lv instanceof class_2402) {
			return ((class_2402)lv).method_10310(arg, arg2, arg3, arg4);
		} else if (!(lv instanceof class_2323)
			&& !lv.method_9525(class_3481.field_15500)
			&& lv != class_2246.field_9983
			&& lv != class_2246.field_10424
			&& lv != class_2246.field_10422) {
			class_3614 lv2 = arg3.method_11620();
			return lv2 != class_3614.field_15919 && lv2 != class_3614.field_15927 && lv2 != class_3614.field_15947 && lv2 != class_3614.field_15926
				? !lv2.method_15801()
				: false;
		} else {
			return false;
		}
	}

	protected boolean method_15738(
		class_1922 arg, class_2338 arg2, class_2680 arg3, class_2350 arg4, class_2338 arg5, class_2680 arg6, class_3610 arg7, class_3611 arg8
	) {
		return arg7.method_15764(arg, arg5, arg8, arg4) && this.method_15732(arg4, arg, arg2, arg3, arg5, arg6) && this.method_15754(arg, arg5, arg6, arg8);
	}

	protected abstract int method_15739(class_1941 arg);

	protected int method_15753(class_1937 arg, class_2338 arg2, class_3610 arg3, class_3610 arg4) {
		return this.method_15789(arg);
	}

	@Override
	public void method_15778(class_1937 arg, class_2338 arg2, class_3610 arg3) {
		if (!arg3.method_15771()) {
			class_3610 lv = this.method_15727(arg, arg2, arg.method_8320(arg2));
			int i = this.method_15753(arg, arg2, arg3, lv);
			if (lv.method_15769()) {
				arg3 = lv;
				arg.method_8652(arg2, class_2246.field_10124.method_9564(), 3);
			} else if (!lv.equals(arg3)) {
				arg3 = lv;
				class_2680 lv2 = lv.method_15759();
				arg.method_8652(arg2, lv2, 2);
				arg.method_8405().method_8676(arg2, lv.method_15772(), i);
				arg.method_8452(arg2, lv2.method_11614());
			}
		}

		this.method_15725(arg, arg2, arg3);
	}

	protected static int method_15741(class_3610 arg) {
		return arg.method_15771() ? 0 : 8 - Math.min(arg.method_15761(), 8) + (arg.method_11654(field_15902) ? 8 : 0);
	}

	private static boolean method_17774(class_3610 arg, class_1922 arg2, class_2338 arg3) {
		return arg.method_15772().method_15780(arg2.method_8316(arg3.method_10084()).method_15772());
	}

	@Override
	public float method_15788(class_3610 arg, class_1922 arg2, class_2338 arg3) {
		return method_17774(arg, arg2, arg3) ? 1.0F : arg.method_20785();
	}

	@Override
	public float method_20784(class_3610 arg) {
		return (float)arg.method_15761() / 9.0F;
	}

	@Override
	public class_265 method_17775(class_3610 arg, class_1922 arg2, class_2338 arg3) {
		return arg.method_15761() == 9 && method_17774(arg, arg2, arg3)
			? class_259.method_1077()
			: (class_265)this.field_17587.computeIfAbsent(arg, arg3x -> class_259.method_1081(0.0, 0.0, 0.0, 1.0, (double)arg3x.method_15763(arg2, arg3), 1.0));
	}
}
