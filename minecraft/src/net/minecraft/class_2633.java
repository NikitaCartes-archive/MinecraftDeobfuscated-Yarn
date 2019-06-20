package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2633 extends class_2586 {
	private class_2960 field_12102;
	private String field_12104 = "";
	private String field_12098 = "";
	private class_2338 field_12092 = new class_2338(0, 1, 0);
	private class_2338 field_12100 = class_2338.field_10980;
	private class_2415 field_12093 = class_2415.field_11302;
	private class_2470 field_12105 = class_2470.field_11467;
	private class_2776 field_12094 = class_2776.field_12696;
	private boolean field_12099 = true;
	private boolean field_12097;
	private boolean field_12096;
	private boolean field_12095 = true;
	private float field_12101 = 1.0F;
	private long field_12103;

	public class_2633() {
		super(class_2591.field_11895);
	}

	@Override
	public class_2487 method_11007(class_2487 arg) {
		super.method_11007(arg);
		arg.method_10582("name", this.method_11362());
		arg.method_10582("author", this.field_12104);
		arg.method_10582("metadata", this.field_12098);
		arg.method_10569("posX", this.field_12092.method_10263());
		arg.method_10569("posY", this.field_12092.method_10264());
		arg.method_10569("posZ", this.field_12092.method_10260());
		arg.method_10569("sizeX", this.field_12100.method_10263());
		arg.method_10569("sizeY", this.field_12100.method_10264());
		arg.method_10569("sizeZ", this.field_12100.method_10260());
		arg.method_10582("rotation", this.field_12105.toString());
		arg.method_10582("mirror", this.field_12093.toString());
		arg.method_10582("mode", this.field_12094.toString());
		arg.method_10556("ignoreEntities", this.field_12099);
		arg.method_10556("powered", this.field_12097);
		arg.method_10556("showair", this.field_12096);
		arg.method_10556("showboundingbox", this.field_12095);
		arg.method_10548("integrity", this.field_12101);
		arg.method_10544("seed", this.field_12103);
		return arg;
	}

	@Override
	public void method_11014(class_2487 arg) {
		super.method_11014(arg);
		this.method_11343(arg.method_10558("name"));
		this.field_12104 = arg.method_10558("author");
		this.field_12098 = arg.method_10558("metadata");
		int i = class_3532.method_15340(arg.method_10550("posX"), -32, 32);
		int j = class_3532.method_15340(arg.method_10550("posY"), -32, 32);
		int k = class_3532.method_15340(arg.method_10550("posZ"), -32, 32);
		this.field_12092 = new class_2338(i, j, k);
		int l = class_3532.method_15340(arg.method_10550("sizeX"), 0, 32);
		int m = class_3532.method_15340(arg.method_10550("sizeY"), 0, 32);
		int n = class_3532.method_15340(arg.method_10550("sizeZ"), 0, 32);
		this.field_12100 = new class_2338(l, m, n);

		try {
			this.field_12105 = class_2470.valueOf(arg.method_10558("rotation"));
		} catch (IllegalArgumentException var11) {
			this.field_12105 = class_2470.field_11467;
		}

		try {
			this.field_12093 = class_2415.valueOf(arg.method_10558("mirror"));
		} catch (IllegalArgumentException var10) {
			this.field_12093 = class_2415.field_11302;
		}

		try {
			this.field_12094 = class_2776.valueOf(arg.method_10558("mode"));
		} catch (IllegalArgumentException var9) {
			this.field_12094 = class_2776.field_12696;
		}

		this.field_12099 = arg.method_10577("ignoreEntities");
		this.field_12097 = arg.method_10577("powered");
		this.field_12096 = arg.method_10577("showair");
		this.field_12095 = arg.method_10577("showboundingbox");
		if (arg.method_10545("integrity")) {
			this.field_12101 = arg.method_10583("integrity");
		} else {
			this.field_12101 = 1.0F;
		}

		this.field_12103 = arg.method_10537("seed");
		this.method_11348();
	}

	private void method_11348() {
		if (this.field_11863 != null) {
			class_2338 lv = this.method_11016();
			class_2680 lv2 = this.field_11863.method_8320(lv);
			if (lv2.method_11614() == class_2246.field_10465) {
				this.field_11863.method_8652(lv, lv2.method_11657(class_2515.field_11586, this.field_12094), 2);
			}
		}
	}

	@Nullable
	@Override
	public class_2622 method_16886() {
		return new class_2622(this.field_11867, 7, this.method_16887());
	}

	@Override
	public class_2487 method_16887() {
		return this.method_11007(new class_2487());
	}

	public boolean method_11351(class_1657 arg) {
		if (!arg.method_7338()) {
			return false;
		} else {
			if (arg.method_5770().field_9236) {
				arg.method_7303(this);
			}

			return true;
		}
	}

	public String method_11362() {
		return this.field_12102 == null ? "" : this.field_12102.toString();
	}

	public boolean method_11384() {
		return this.field_12102 != null;
	}

	public void method_11343(@Nullable String string) {
		this.method_11344(class_3544.method_15438(string) ? null : class_2960.method_12829(string));
	}

	public void method_11344(@Nullable class_2960 arg) {
		this.field_12102 = arg;
	}

	public void method_11373(class_1309 arg) {
		this.field_12104 = arg.method_5477().getString();
	}

	@Environment(EnvType.CLIENT)
	public class_2338 method_11359() {
		return this.field_12092;
	}

	public void method_11378(class_2338 arg) {
		this.field_12092 = arg;
	}

	@Environment(EnvType.CLIENT)
	public class_2338 method_11349() {
		return this.field_12100;
	}

	public void method_11377(class_2338 arg) {
		this.field_12100 = arg;
	}

	@Environment(EnvType.CLIENT)
	public class_2415 method_11345() {
		return this.field_12093;
	}

	public void method_11356(class_2415 arg) {
		this.field_12093 = arg;
	}

	@Environment(EnvType.CLIENT)
	public class_2470 method_11353() {
		return this.field_12105;
	}

	public void method_11385(class_2470 arg) {
		this.field_12105 = arg;
	}

	@Environment(EnvType.CLIENT)
	public String method_11358() {
		return this.field_12098;
	}

	public void method_11363(String string) {
		this.field_12098 = string;
	}

	public class_2776 method_11374() {
		return this.field_12094;
	}

	public void method_11381(class_2776 arg) {
		this.field_12094 = arg;
		class_2680 lv = this.field_11863.method_8320(this.method_11016());
		if (lv.method_11614() == class_2246.field_10465) {
			this.field_11863.method_8652(this.method_11016(), lv.method_11657(class_2515.field_11586, arg), 2);
		}
	}

	@Environment(EnvType.CLIENT)
	public void method_11380() {
		switch (this.method_11374()) {
			case field_12695:
				this.method_11381(class_2776.field_12697);
				break;
			case field_12697:
				this.method_11381(class_2776.field_12699);
				break;
			case field_12699:
				this.method_11381(class_2776.field_12696);
				break;
			case field_12696:
				this.method_11381(class_2776.field_12695);
		}
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11367() {
		return this.field_12099;
	}

	public void method_11352(boolean bl) {
		this.field_12099 = bl;
	}

	@Environment(EnvType.CLIENT)
	public float method_11346() {
		return this.field_12101;
	}

	public void method_11370(float f) {
		this.field_12101 = f;
	}

	@Environment(EnvType.CLIENT)
	public long method_11371() {
		return this.field_12103;
	}

	public void method_11382(long l) {
		this.field_12103 = l;
	}

	public boolean method_11383() {
		if (this.field_12094 != class_2776.field_12695) {
			return false;
		} else {
			class_2338 lv = this.method_11016();
			int i = 80;
			class_2338 lv2 = new class_2338(lv.method_10263() - 80, 0, lv.method_10260() - 80);
			class_2338 lv3 = new class_2338(lv.method_10263() + 80, 255, lv.method_10260() + 80);
			List<class_2633> list = this.method_11369(lv2, lv3);
			List<class_2633> list2 = this.method_11364(list);
			if (list2.size() < 1) {
				return false;
			} else {
				class_3341 lv4 = this.method_11355(lv, list2);
				if (lv4.field_14378 - lv4.field_14381 > 1 && lv4.field_14377 - lv4.field_14380 > 1 && lv4.field_14376 - lv4.field_14379 > 1) {
					this.field_12092 = new class_2338(
						lv4.field_14381 - lv.method_10263() + 1, lv4.field_14380 - lv.method_10264() + 1, lv4.field_14379 - lv.method_10260() + 1
					);
					this.field_12100 = new class_2338(lv4.field_14378 - lv4.field_14381 - 1, lv4.field_14377 - lv4.field_14380 - 1, lv4.field_14376 - lv4.field_14379 - 1);
					this.method_5431();
					class_2680 lv5 = this.field_11863.method_8320(lv);
					this.field_11863.method_8413(lv, lv5, lv5, 3);
					return true;
				} else {
					return false;
				}
			}
		}
	}

	private List<class_2633> method_11364(List<class_2633> list) {
		Predicate<class_2633> predicate = arg -> arg.field_12094 == class_2776.field_12699 && Objects.equals(this.field_12102, arg.field_12102);
		return (List<class_2633>)list.stream().filter(predicate).collect(Collectors.toList());
	}

	private List<class_2633> method_11369(class_2338 arg, class_2338 arg2) {
		List<class_2633> list = Lists.<class_2633>newArrayList();

		for (class_2338 lv : class_2338.method_10097(arg, arg2)) {
			class_2680 lv2 = this.field_11863.method_8320(lv);
			if (lv2.method_11614() == class_2246.field_10465) {
				class_2586 lv3 = this.field_11863.method_8321(lv);
				if (lv3 != null && lv3 instanceof class_2633) {
					list.add((class_2633)lv3);
				}
			}
		}

		return list;
	}

	private class_3341 method_11355(class_2338 arg, List<class_2633> list) {
		class_3341 lv2;
		if (list.size() > 1) {
			class_2338 lv = ((class_2633)list.get(0)).method_11016();
			lv2 = new class_3341(lv, lv);
		} else {
			lv2 = new class_3341(arg, arg);
		}

		for (class_2633 lv3 : list) {
			class_2338 lv4 = lv3.method_11016();
			if (lv4.method_10263() < lv2.field_14381) {
				lv2.field_14381 = lv4.method_10263();
			} else if (lv4.method_10263() > lv2.field_14378) {
				lv2.field_14378 = lv4.method_10263();
			}

			if (lv4.method_10264() < lv2.field_14380) {
				lv2.field_14380 = lv4.method_10264();
			} else if (lv4.method_10264() > lv2.field_14377) {
				lv2.field_14377 = lv4.method_10264();
			}

			if (lv4.method_10260() < lv2.field_14379) {
				lv2.field_14379 = lv4.method_10260();
			} else if (lv4.method_10260() > lv2.field_14376) {
				lv2.field_14376 = lv4.method_10260();
			}
		}

		return lv2;
	}

	public boolean method_11365() {
		return this.method_11366(true);
	}

	public boolean method_11366(boolean bl) {
		if (this.field_12094 == class_2776.field_12695 && !this.field_11863.field_9236 && this.field_12102 != null) {
			class_2338 lv = this.method_11016().method_10081(this.field_12092);
			class_3218 lv2 = (class_3218)this.field_11863;
			class_3485 lv3 = lv2.method_14183();

			class_3499 lv4;
			try {
				lv4 = lv3.method_15091(this.field_12102);
			} catch (class_151 var8) {
				return false;
			}

			lv4.method_15174(this.field_11863, lv, this.field_12100, !this.field_12099, class_2246.field_10369);
			lv4.method_15161(this.field_12104);
			if (bl) {
				try {
					return lv3.method_15093(this.field_12102);
				} catch (class_151 var7) {
					return false;
				}
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public boolean method_11376() {
		return this.method_11368(true);
	}

	private static Random method_20048(long l) {
		return l == 0L ? new Random(class_156.method_658()) : new Random(l);
	}

	public boolean method_11368(boolean bl) {
		if (this.field_12094 == class_2776.field_12697 && !this.field_11863.field_9236 && this.field_12102 != null) {
			class_2338 lv = this.method_11016();
			class_2338 lv2 = lv.method_10081(this.field_12092);
			class_3218 lv3 = (class_3218)this.field_11863;
			class_3485 lv4 = lv3.method_14183();

			class_3499 lv5;
			try {
				lv5 = lv4.method_15094(this.field_12102);
			} catch (class_151 var10) {
				return false;
			}

			if (lv5 == null) {
				return false;
			} else {
				if (!class_3544.method_15438(lv5.method_15181())) {
					this.field_12104 = lv5.method_15181();
				}

				class_2338 lv7 = lv5.method_15160();
				boolean bl2 = this.field_12100.equals(lv7);
				if (!bl2) {
					this.field_12100 = lv7;
					this.method_5431();
					class_2680 lv8 = this.field_11863.method_8320(lv);
					this.field_11863.method_8413(lv, lv8, lv8, 3);
				}

				if (bl && !bl2) {
					return false;
				} else {
					class_3492 lv9 = new class_3492().method_15125(this.field_12093).method_15123(this.field_12105).method_15133(this.field_12099).method_15130(null);
					if (this.field_12101 < 1.0F) {
						lv9.method_16183().method_16184(new class_3488(class_3532.method_15363(this.field_12101, 0.0F, 1.0F))).method_15112(method_20048(this.field_12103));
					}

					lv5.method_15182(this.field_11863, lv2, lv9);
					return true;
				}
			}
		} else {
			return false;
		}
	}

	public void method_11361() {
		if (this.field_12102 != null) {
			class_3218 lv = (class_3218)this.field_11863;
			class_3485 lv2 = lv.method_14183();
			lv2.method_15087(this.field_12102);
		}
	}

	public boolean method_11372() {
		if (this.field_12094 == class_2776.field_12697 && !this.field_11863.field_9236 && this.field_12102 != null) {
			class_3218 lv = (class_3218)this.field_11863;
			class_3485 lv2 = lv.method_14183();

			try {
				return lv2.method_15094(this.field_12102) != null;
			} catch (class_151 var4) {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean method_11354() {
		return this.field_12097;
	}

	public void method_11379(boolean bl) {
		this.field_12097 = bl;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11375() {
		return this.field_12096;
	}

	public void method_11347(boolean bl) {
		this.field_12096 = bl;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11357() {
		return this.field_12095;
	}

	public void method_11360(boolean bl) {
		this.field_12095 = bl;
	}

	public static enum class_2634 {
		field_12108,
		field_12110,
		field_12109,
		field_12106;
	}
}
