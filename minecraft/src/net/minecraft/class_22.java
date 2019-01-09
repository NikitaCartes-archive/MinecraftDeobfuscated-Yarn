package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class class_22 extends class_18 {
	public int field_116;
	public int field_115;
	public class_2874 field_118;
	public boolean field_114;
	public boolean field_113;
	public byte field_119;
	public byte[] field_122 = new byte[16384];
	public boolean field_17403;
	public final List<class_22.class_23> field_112 = Lists.<class_22.class_23>newArrayList();
	private final Map<class_1657, class_22.class_23> field_120 = Maps.<class_1657, class_22.class_23>newHashMap();
	private final Map<String, class_17> field_123 = Maps.<String, class_17>newHashMap();
	public final Map<String, class_20> field_117 = Maps.<String, class_20>newLinkedHashMap();
	private final Map<String, class_19> field_121 = Maps.<String, class_19>newHashMap();

	public class_22(String string) {
		super(string);
	}

	public void method_105(int i, int j, int k, boolean bl, boolean bl2, class_2874 arg) {
		this.field_119 = (byte)k;
		this.method_106((double)i, (double)j, this.field_119);
		this.field_118 = arg;
		this.field_114 = bl;
		this.field_113 = bl2;
		this.method_80();
	}

	public void method_106(double d, double e, int i) {
		int j = 128 * (1 << i);
		int k = class_3532.method_15357((d + 64.0) / (double)j);
		int l = class_3532.method_15357((e + 64.0) / (double)j);
		this.field_116 = k * j + j / 2 - 64;
		this.field_115 = l * j + j / 2 - 64;
	}

	@Override
	public void method_77(class_2487 arg) {
		this.field_118 = class_2874.method_12490(arg.method_10550("dimension"));
		this.field_116 = arg.method_10550("xCenter");
		this.field_115 = arg.method_10550("zCenter");
		this.field_119 = (byte)class_3532.method_15340(arg.method_10571("scale"), 0, 4);
		this.field_114 = !arg.method_10573("trackingPosition", 1) || arg.method_10577("trackingPosition");
		this.field_113 = arg.method_10577("unlimitedTracking");
		this.field_17403 = arg.method_10577("locked");
		this.field_122 = arg.method_10547("colors");
		if (this.field_122.length != 16384) {
			this.field_122 = new byte[16384];
		}

		class_2499 lv = arg.method_10554("banners", 10);

		for (int i = 0; i < lv.size(); i++) {
			class_17 lv2 = class_17.method_67(lv.method_10602(i));
			this.field_123.put(lv2.method_71(), lv2);
			this.method_107(
				lv2.method_72(), null, lv2.method_71(), (double)lv2.method_70().method_10263(), (double)lv2.method_70().method_10260(), 180.0, lv2.method_68()
			);
		}

		class_2499 lv3 = arg.method_10554("frames", 10);

		for (int j = 0; j < lv3.size(); j++) {
			class_19 lv4 = class_19.method_87(lv3.method_10602(j));
			this.field_121.put(lv4.method_82(), lv4);
			this.method_107(
				class_20.class_21.field_95,
				null,
				"frame-" + lv4.method_85(),
				(double)lv4.method_86().method_10263(),
				(double)lv4.method_86().method_10260(),
				(double)lv4.method_83(),
				null
			);
		}
	}

	@Override
	public class_2487 method_75(class_2487 arg) {
		arg.method_10569("dimension", this.field_118.method_12484());
		arg.method_10569("xCenter", this.field_116);
		arg.method_10569("zCenter", this.field_115);
		arg.method_10567("scale", this.field_119);
		arg.method_10570("colors", this.field_122);
		arg.method_10556("trackingPosition", this.field_114);
		arg.method_10556("unlimitedTracking", this.field_113);
		arg.method_10556("locked", this.field_17403);
		class_2499 lv = new class_2499();

		for (class_17 lv2 : this.field_123.values()) {
			lv.method_10606(lv2.method_74());
		}

		arg.method_10566("banners", lv);
		class_2499 lv3 = new class_2499();

		for (class_19 lv4 : this.field_121.values()) {
			lv3.method_10606(lv4.method_84());
		}

		arg.method_10566("frames", lv3);
		return arg;
	}

	public void method_102(class_1657 arg, class_1799 arg2) {
		if (!this.field_120.containsKey(arg)) {
			class_22.class_23 lv = new class_22.class_23(arg);
			this.field_120.put(arg, lv);
			this.field_112.add(lv);
		}

		if (!arg.field_7514.method_7379(arg2)) {
			this.field_117.remove(arg.method_5477().getString());
		}

		for (int i = 0; i < this.field_112.size(); i++) {
			class_22.class_23 lv2 = (class_22.class_23)this.field_112.get(i);
			String string = lv2.field_125.method_5477().getString();
			if (!lv2.field_125.field_5988 && (lv2.field_125.field_7514.method_7379(arg2) || arg2.method_7961())) {
				if (!arg2.method_7961() && lv2.field_125.field_6026 == this.field_118 && this.field_114) {
					this.method_107(
						class_20.class_21.field_91, lv2.field_125.field_6002, string, lv2.field_125.field_5987, lv2.field_125.field_6035, (double)lv2.field_125.field_6031, null
					);
				}
			} else {
				this.field_120.remove(lv2.field_125);
				this.field_112.remove(lv2);
				this.field_117.remove(string);
			}
		}

		if (arg2.method_7961() && this.field_114) {
			class_1533 lv3 = arg2.method_7945();
			class_2338 lv4 = lv3.method_6896();
			class_19 lv5 = (class_19)this.field_121.get(class_19.method_81(lv4));
			if (lv5 != null && lv3.method_5628() != lv5.method_85() && this.field_121.containsKey(lv5.method_82())) {
				this.field_117.remove("frame-" + lv5.method_85());
			}

			class_19 lv6 = new class_19(lv4, lv3.field_7099.method_10161() * 90, lv3.method_5628());
			this.method_107(
				class_20.class_21.field_95,
				arg.field_6002,
				"frame-" + lv3.method_5628(),
				(double)lv4.method_10263(),
				(double)lv4.method_10260(),
				(double)(lv3.field_7099.method_10161() * 90),
				null
			);
			this.field_121.put(lv6.method_82(), lv6);
		}

		class_2487 lv7 = arg2.method_7969();
		if (lv7 != null && lv7.method_10573("Decorations", 9)) {
			class_2499 lv8 = lv7.method_10554("Decorations", 10);

			for (int j = 0; j < lv8.size(); j++) {
				class_2487 lv9 = lv8.method_10602(j);
				if (!this.field_117.containsKey(lv9.method_10558("id"))) {
					this.method_107(
						class_20.class_21.method_99(lv9.method_10571("type")),
						arg.field_6002,
						lv9.method_10558("id"),
						lv9.method_10574("x"),
						lv9.method_10574("z"),
						lv9.method_10574("rot"),
						null
					);
				}
			}
		}
	}

	public static void method_110(class_1799 arg, class_2338 arg2, String string, class_20.class_21 arg3) {
		class_2499 lv;
		if (arg.method_7985() && arg.method_7969().method_10573("Decorations", 9)) {
			lv = arg.method_7969().method_10554("Decorations", 10);
		} else {
			lv = new class_2499();
			arg.method_7959("Decorations", lv);
		}

		class_2487 lv2 = new class_2487();
		lv2.method_10567("type", arg3.method_98());
		lv2.method_10582("id", string);
		lv2.method_10549("x", (double)arg2.method_10263());
		lv2.method_10549("z", (double)arg2.method_10260());
		lv2.method_10549("rot", 180.0);
		lv.method_10606(lv2);
		if (arg3.method_97()) {
			class_2487 lv3 = arg.method_7911("display");
			lv3.method_10569("MapColor", arg3.method_96());
		}
	}

	private void method_107(class_20.class_21 arg, @Nullable class_1936 arg2, String string, double d, double e, double f, @Nullable class_2561 arg3) {
		int i = 1 << this.field_119;
		float g = (float)(d - (double)this.field_116) / (float)i;
		float h = (float)(e - (double)this.field_115) / (float)i;
		byte b = (byte)((int)((double)(g * 2.0F) + 0.5));
		byte c = (byte)((int)((double)(h * 2.0F) + 0.5));
		int j = 63;
		byte k;
		if (g >= -63.0F && h >= -63.0F && g <= 63.0F && h <= 63.0F) {
			f += f < 0.0 ? -8.0 : 8.0;
			k = (byte)((int)(f * 16.0 / 360.0));
			if (this.field_118 == class_2874.field_13076 && arg2 != null) {
				int l = (int)(arg2.method_8401().method_217() / 10L);
				k = (byte)(l * l * 34187121 + l * 121 >> 15 & 15);
			}
		} else {
			if (arg != class_20.class_21.field_91) {
				this.field_117.remove(string);
				return;
			}

			int l = 320;
			if (Math.abs(g) < 320.0F && Math.abs(h) < 320.0F) {
				arg = class_20.class_21.field_86;
			} else {
				if (!this.field_113) {
					this.field_117.remove(string);
					return;
				}

				arg = class_20.class_21.field_87;
			}

			k = 0;
			if (g <= -63.0F) {
				b = -128;
			}

			if (h <= -63.0F) {
				c = -128;
			}

			if (g >= 63.0F) {
				b = 127;
			}

			if (h >= 63.0F) {
				c = 127;
			}
		}

		this.field_117.put(string, new class_20(arg, b, c, k, arg3));
	}

	@Nullable
	public class_2596<?> method_100(class_1799 arg, class_1922 arg2, class_1657 arg3) {
		class_22.class_23 lv = (class_22.class_23)this.field_120.get(arg3);
		return lv == null ? null : lv.method_112(arg);
	}

	public void method_103(int i, int j) {
		this.method_80();

		for (class_22.class_23 lv : this.field_112) {
			lv.method_111(i, j);
		}
	}

	public class_22.class_23 method_101(class_1657 arg) {
		class_22.class_23 lv = (class_22.class_23)this.field_120.get(arg);
		if (lv == null) {
			lv = new class_22.class_23(arg);
			this.field_120.put(arg, lv);
			this.field_112.add(lv);
		}

		return lv;
	}

	public void method_108(class_1936 arg, class_2338 arg2) {
		float f = (float)arg2.method_10263() + 0.5F;
		float g = (float)arg2.method_10260() + 0.5F;
		int i = 1 << this.field_119;
		float h = (f - (float)this.field_116) / (float)i;
		float j = (g - (float)this.field_115) / (float)i;
		int k = 63;
		boolean bl = false;
		if (h >= -63.0F && j >= -63.0F && h <= 63.0F && j <= 63.0F) {
			class_17 lv = class_17.method_73(arg, arg2);
			if (lv == null) {
				return;
			}

			boolean bl2 = true;
			if (this.field_123.containsKey(lv.method_71()) && ((class_17)this.field_123.get(lv.method_71())).equals(lv)) {
				this.field_123.remove(lv.method_71());
				this.field_117.remove(lv.method_71());
				bl2 = false;
				bl = true;
			}

			if (bl2) {
				this.field_123.put(lv.method_71(), lv);
				this.method_107(lv.method_72(), arg, lv.method_71(), (double)f, (double)g, 180.0, lv.method_68());
				bl = true;
			}

			if (bl) {
				this.method_80();
			}
		}
	}

	public void method_109(class_1922 arg, int i, int j) {
		Iterator<class_17> iterator = this.field_123.values().iterator();

		while (iterator.hasNext()) {
			class_17 lv = (class_17)iterator.next();
			if (lv.method_70().method_10263() == i && lv.method_70().method_10260() == j) {
				class_17 lv2 = class_17.method_73(arg, lv.method_70());
				if (!lv.equals(lv2)) {
					iterator.remove();
					this.field_117.remove(lv.method_71());
				}
			}
		}
	}

	public void method_104(class_2338 arg, int i) {
		this.field_117.remove("frame-" + i);
		this.field_121.remove(class_19.method_81(arg));
	}

	public class class_23 {
		public final class_1657 field_125;
		private boolean field_130 = true;
		private int field_129;
		private int field_128;
		private int field_127 = 127;
		private int field_126 = 127;
		private int field_124;
		public int field_131;

		public class_23(class_1657 arg2) {
			this.field_125 = arg2;
		}

		@Nullable
		public class_2596<?> method_112(class_1799 arg) {
			if (this.field_130) {
				this.field_130 = false;
				return new class_2683(
					class_1806.method_8003(arg),
					class_22.this.field_119,
					class_22.this.field_114,
					class_22.this.field_17403,
					class_22.this.field_117.values(),
					class_22.this.field_122,
					this.field_129,
					this.field_128,
					this.field_127 + 1 - this.field_129,
					this.field_126 + 1 - this.field_128
				);
			} else {
				return this.field_124++ % 5 == 0
					? new class_2683(
						class_1806.method_8003(arg),
						class_22.this.field_119,
						class_22.this.field_114,
						class_22.this.field_17403,
						class_22.this.field_117.values(),
						class_22.this.field_122,
						0,
						0,
						0,
						0
					)
					: null;
			}
		}

		public void method_111(int i, int j) {
			if (this.field_130) {
				this.field_129 = Math.min(this.field_129, i);
				this.field_128 = Math.min(this.field_128, j);
				this.field_127 = Math.max(this.field_127, i);
				this.field_126 = Math.max(this.field_126, j);
			} else {
				this.field_130 = true;
				this.field_129 = i;
				this.field_128 = j;
				this.field_127 = i;
				this.field_126 = j;
			}
		}
	}
}
