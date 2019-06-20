package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3222 extends class_1657 implements class_1712 {
	private static final Logger field_13975 = LogManager.getLogger();
	private String field_13981 = "en_US";
	public class_3244 field_13987;
	public final MinecraftServer field_13995;
	public final class_3225 field_13974;
	private final List<Integer> field_13988 = Lists.<Integer>newLinkedList();
	private final class_2985 field_13970;
	private final class_3442 field_13966;
	private float field_13963 = Float.MIN_VALUE;
	private int field_13983 = Integer.MIN_VALUE;
	private int field_13968 = Integer.MIN_VALUE;
	private int field_13982 = Integer.MIN_VALUE;
	private int field_13965 = Integer.MIN_VALUE;
	private int field_13980 = Integer.MIN_VALUE;
	private float field_13997 = -1.0E8F;
	private int field_13979 = -99999999;
	private boolean field_13972 = true;
	private int field_13978 = -99999999;
	private int field_13998 = 60;
	private class_1659 field_13993;
	private boolean field_13971 = true;
	private long field_13976 = class_156.method_658();
	private class_1297 field_13984;
	private boolean field_13985;
	private boolean field_13969;
	private final class_3441 field_13996;
	private class_243 field_13992;
	private int field_13973;
	private boolean field_13964;
	@Nullable
	private class_243 field_13994;
	private class_4076 field_13990 = class_4076.method_18676(0, 0, 0);
	private int field_13986;
	public boolean field_13991;
	public int field_13967;
	public boolean field_13989;

	public class_3222(MinecraftServer minecraftServer, class_3218 arg, GameProfile gameProfile, class_3225 arg2) {
		super(arg, gameProfile);
		arg2.field_14008 = this;
		this.field_13974 = arg2;
		this.field_13995 = minecraftServer;
		this.field_13996 = new class_3441(minecraftServer.method_3772());
		this.field_13966 = minecraftServer.method_3760().method_14583(this);
		this.field_13970 = minecraftServer.method_3760().method_14578(this);
		this.field_6013 = 1.0F;
		this.method_14245(arg);
	}

	private void method_14245(class_3218 arg) {
		class_2338 lv = arg.method_8395();
		if (arg.field_9247.method_12451() && arg.method_8401().method_210() != class_1934.field_9216) {
			int i = Math.max(0, this.field_13995.method_3829(arg));
			int j = class_3532.method_15357(arg.method_8621().method_11961((double)lv.method_10263(), (double)lv.method_10260()));
			if (j < i) {
				i = j;
			}

			if (j <= 1) {
				i = 1;
			}

			long l = (long)(i * 2 + 1);
			long m = l * l;
			int k = m > 2147483647L ? Integer.MAX_VALUE : (int)m;
			int n = this.method_14244(k);
			int o = new Random().nextInt(k);

			for (int p = 0; p < k; p++) {
				int q = (o + n * p) % k;
				int r = q % (i * 2 + 1);
				int s = q / (i * 2 + 1);
				class_2338 lv2 = arg.method_8597().method_12444(lv.method_10263() + r - i, lv.method_10260() + s - i, false);
				if (lv2 != null) {
					this.method_5725(lv2, 0.0F, 0.0F);
					if (arg.method_17892(this)) {
						break;
					}
				}
			}
		} else {
			this.method_5725(lv, 0.0F, 0.0F);

			while (!arg.method_17892(this) && this.field_6010 < 255.0) {
				this.method_5814(this.field_5987, this.field_6010 + 1.0, this.field_6035);
			}
		}
	}

	private int method_14244(int i) {
		return i <= 16 ? i - 1 : 17;
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		if (arg.method_10573("playerGameType", 99)) {
			if (this.method_5682().method_3761()) {
				this.field_13974.method_14261(this.method_5682().method_3790());
			} else {
				this.field_13974.method_14261(class_1934.method_8384(arg.method_10550("playerGameType")));
			}
		}

		if (arg.method_10573("enteredNetherPosition", 10)) {
			class_2487 lv = arg.method_10562("enteredNetherPosition");
			this.field_13994 = new class_243(lv.method_10574("x"), lv.method_10574("y"), lv.method_10574("z"));
		}

		this.field_13969 = arg.method_10577("seenCredits");
		if (arg.method_10573("recipeBook", 10)) {
			this.field_13996.method_14901(arg.method_10562("recipeBook"));
		}

		if (this.method_6113()) {
			this.method_18400();
		}
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("playerGameType", this.field_13974.method_14257().method_8379());
		arg.method_10556("seenCredits", this.field_13969);
		if (this.field_13994 != null) {
			class_2487 lv = new class_2487();
			lv.method_10549("x", this.field_13994.field_1352);
			lv.method_10549("y", this.field_13994.field_1351);
			lv.method_10549("z", this.field_13994.field_1350);
			arg.method_10566("enteredNetherPosition", lv);
		}

		class_1297 lv2 = this.method_5668();
		class_1297 lv3 = this.method_5854();
		if (lv3 != null && lv2 != this && lv2.method_5817()) {
			class_2487 lv4 = new class_2487();
			class_2487 lv5 = new class_2487();
			lv2.method_5662(lv5);
			lv4.method_10560("Attach", lv3.method_5667());
			lv4.method_10566("Entity", lv5);
			arg.method_10566("RootVehicle", lv4);
		}

		arg.method_10566("recipeBook", this.field_13996.method_14902());
	}

	public void method_14228(int i) {
		float f = (float)this.method_7349();
		float g = (f - 1.0F) / f;
		this.field_7510 = class_3532.method_15363((float)i / f, 0.0F, g);
		this.field_13978 = -1;
	}

	public void method_14252(int i) {
		this.field_7520 = i;
		this.field_13978 = -1;
	}

	@Override
	public void method_7316(int i) {
		super.method_7316(i);
		this.field_13978 = -1;
	}

	@Override
	public void method_7286(class_1799 arg, int i) {
		super.method_7286(arg, i);
		this.field_13978 = -1;
	}

	public void method_14235() {
		this.field_7512.method_7596(this);
	}

	@Override
	public void method_6000() {
		super.method_6000();
		this.field_13987.method_14364(new class_2698(this.method_6066(), class_2698.class_2699.field_12352));
	}

	@Override
	public void method_6044() {
		super.method_6044();
		this.field_13987.method_14364(new class_2698(this.method_6066(), class_2698.class_2699.field_12353));
	}

	@Override
	protected void method_5622(class_2680 arg) {
		class_174.field_1180.method_8885(this, arg);
	}

	@Override
	protected class_1796 method_7265() {
		return new class_1817(this);
	}

	@Override
	public void method_5773() {
		this.field_13974.method_14264();
		this.field_13998--;
		if (this.field_6008 > 0) {
			this.field_6008--;
		}

		this.field_7512.method_7623();
		if (!this.field_6002.field_9236 && !this.field_7512.method_7597(this)) {
			this.method_7346();
			this.field_7512 = this.field_7498;
		}

		while (!this.field_13988.isEmpty()) {
			int i = Math.min(this.field_13988.size(), Integer.MAX_VALUE);
			int[] is = new int[i];
			Iterator<Integer> iterator = this.field_13988.iterator();
			int j = 0;

			while (iterator.hasNext() && j < i) {
				is[j++] = (Integer)iterator.next();
				iterator.remove();
			}

			this.field_13987.method_14364(new class_2716(is));
		}

		class_1297 lv = this.method_14242();
		if (lv != this) {
			if (lv.method_5805()) {
				this.method_5641(lv.field_5987, lv.field_6010, lv.field_6035, lv.field_6031, lv.field_5965);
				this.method_14220().method_14178().method_14096(this);
				if (this.method_5715()) {
					this.method_14224(this);
				}
			} else {
				this.method_14224(this);
			}
		}

		class_174.field_1187.method_9141(this);
		if (this.field_13992 != null) {
			class_174.field_1200.method_9008(this, this.field_13992, this.field_6012 - this.field_13973);
		}

		this.field_13970.method_12876(this);
	}

	public void method_14226() {
		try {
			if (!this.method_7325() || this.field_6002.method_8591(new class_2338(this))) {
				super.method_5773();
			}

			for (int i = 0; i < this.field_7514.method_5439(); i++) {
				class_1799 lv = this.field_7514.method_5438(i);
				if (lv.method_7909().method_16698()) {
					class_2596<?> lv2 = ((class_1762)lv.method_7909()).method_7757(lv, this.field_6002, this);
					if (lv2 != null) {
						this.field_13987.method_14364(lv2);
					}
				}
			}

			if (this.method_6032() != this.field_13997 || this.field_13979 != this.field_7493.method_7586() || this.field_7493.method_7589() == 0.0F != this.field_13972
				)
			 {
				this.field_13987.method_14364(new class_2749(this.method_6032(), this.field_7493.method_7586(), this.field_7493.method_7589()));
				this.field_13997 = this.method_6032();
				this.field_13979 = this.field_7493.method_7586();
				this.field_13972 = this.field_7493.method_7589() == 0.0F;
			}

			if (this.method_6032() + this.method_6067() != this.field_13963) {
				this.field_13963 = this.method_6032() + this.method_6067();
				this.method_14212(class_274.field_1453, class_3532.method_15386(this.field_13963));
			}

			if (this.field_7493.method_7586() != this.field_13983) {
				this.field_13983 = this.field_7493.method_7586();
				this.method_14212(class_274.field_1464, class_3532.method_15386((float)this.field_13983));
			}

			if (this.method_5669() != this.field_13968) {
				this.field_13968 = this.method_5669();
				this.method_14212(class_274.field_1459, class_3532.method_15386((float)this.field_13968));
			}

			if (this.method_6096() != this.field_13982) {
				this.field_13982 = this.method_6096();
				this.method_14212(class_274.field_1452, class_3532.method_15386((float)this.field_13982));
			}

			if (this.field_7495 != this.field_13980) {
				this.field_13980 = this.field_7495;
				this.method_14212(class_274.field_1460, class_3532.method_15386((float)this.field_13980));
			}

			if (this.field_7520 != this.field_13965) {
				this.field_13965 = this.field_7520;
				this.method_14212(class_274.field_1465, class_3532.method_15386((float)this.field_13965));
			}

			if (this.field_7495 != this.field_13978) {
				this.field_13978 = this.field_7495;
				this.field_13987.method_14364(new class_2748(this.field_7510, this.field_7495, this.field_7520));
			}

			if (this.field_6012 % 20 == 0) {
				class_174.field_1194.method_9027(this);
			}
		} catch (Throwable var4) {
			class_128 lv3 = class_128.method_560(var4, "Ticking player");
			class_129 lv4 = lv3.method_562("Player being ticked");
			this.method_5819(lv4);
			throw new class_148(lv3);
		}
	}

	private void method_14212(class_274 arg, int i) {
		this.method_7327().method_1162(arg, this.method_5820(), argx -> argx.method_1128(i));
	}

	@Override
	public void method_6078(class_1282 arg) {
		boolean bl = this.field_6002.method_8450().method_8355(class_1928.field_19398);
		if (bl) {
			class_2561 lv = this.method_6066().method_5548();
			this.field_13987
				.method_14369(
					new class_2698(this.method_6066(), class_2698.class_2699.field_12350, lv),
					future -> {
						if (!future.isSuccess()) {
							int i = 256;
							String string = lv.method_10858(256);
							class_2561 lvx = new class_2588("death.attack.message_too_long", new class_2585(string).method_10854(class_124.field_1054));
							class_2561 lv2 = new class_2588("death.attack.even_more_magic", this.method_5476())
								.method_10859(arg2 -> arg2.method_10949(new class_2568(class_2568.class_2569.field_11762, lv)));
							this.field_13987.method_14364(new class_2698(this.method_6066(), class_2698.class_2699.field_12350, lv2));
						}
					}
				);
			class_270 lv2 = this.method_5781();
			if (lv2 == null || lv2.method_1200() == class_270.class_272.field_1442) {
				this.field_13995.method_3760().method_14593(lv);
			} else if (lv2.method_1200() == class_270.class_272.field_1444) {
				this.field_13995.method_3760().method_14564(this, lv);
			} else if (lv2.method_1200() == class_270.class_272.field_1446) {
				this.field_13995.method_3760().method_14565(this, lv);
			}
		} else {
			this.field_13987.method_14364(new class_2698(this.method_6066(), class_2698.class_2699.field_12350));
		}

		this.method_7262();
		if (!this.method_7325()) {
			this.method_16080(arg);
		}

		this.method_7327().method_1162(class_274.field_1456, this.method_5820(), class_267::method_1130);
		class_1309 lv3 = this.method_6124();
		if (lv3 != null) {
			this.method_7259(class_3468.field_15411.method_14956(lv3.method_5864()));
			lv3.method_5716(this, this.field_6232, arg);
			if (!this.field_6002.field_9236 && lv3 instanceof class_1528) {
				boolean bl2 = false;
				if (this.field_6002.method_8450().method_8355(class_1928.field_19388)) {
					class_2338 lv4 = new class_2338(this.field_5987, this.field_6010, this.field_6035);
					class_2680 lv5 = class_2246.field_10606.method_9564();
					if (this.field_6002.method_8320(lv4).method_11588() && lv5.method_11591(this.field_6002, lv4)) {
						this.field_6002.method_8652(lv4, lv5, 3);
						bl2 = true;
					}
				}

				if (!bl2) {
					class_1542 lv6 = new class_1542(this.field_6002, this.field_5987, this.field_6010, this.field_6035, new class_1799(class_1802.field_17515));
					this.field_6002.method_8649(lv6);
				}
			}
		}

		this.method_7281(class_3468.field_15421);
		this.method_7266(class_3468.field_15419.method_14956(class_3468.field_15400));
		this.method_7266(class_3468.field_15419.method_14956(class_3468.field_15429));
		this.method_5646();
		this.method_5729(0, false);
		this.method_6066().method_5539();
	}

	@Override
	public void method_5716(class_1297 arg, int i, class_1282 arg2) {
		if (arg != this) {
			super.method_5716(arg, i, arg2);
			this.method_7285(i);
			String string = this.method_5820();
			String string2 = arg.method_5820();
			this.method_7327().method_1162(class_274.field_1457, string, class_267::method_1130);
			if (arg instanceof class_1657) {
				this.method_7281(class_3468.field_15404);
				this.method_7327().method_1162(class_274.field_1463, string, class_267::method_1130);
			} else {
				this.method_7281(class_3468.field_15414);
			}

			this.method_14227(string, string2, class_274.field_1466);
			this.method_14227(string2, string, class_274.field_1458);
			class_174.field_1192.method_8990(this, arg, arg2);
		}
	}

	private void method_14227(String string, String string2, class_274[] args) {
		class_268 lv = this.method_7327().method_1164(string2);
		if (lv != null) {
			int i = lv.method_1202().method_536();
			if (i >= 0 && i < args.length) {
				this.method_7327().method_1162(args[i], string, class_267::method_1130);
			}
		}
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		if (this.method_5679(arg)) {
			return false;
		} else {
			boolean bl = this.field_13995.method_3816() && this.method_14230() && "fall".equals(arg.field_5841);
			if (!bl && this.field_13998 > 0 && arg != class_1282.field_5849) {
				return false;
			} else {
				if (arg instanceof class_1285) {
					class_1297 lv = arg.method_5529();
					if (lv instanceof class_1657 && !this.method_7256((class_1657)lv)) {
						return false;
					}

					if (lv instanceof class_1665) {
						class_1665 lv2 = (class_1665)lv;
						class_1297 lv3 = lv2.method_7452();
						if (lv3 instanceof class_1657 && !this.method_7256((class_1657)lv3)) {
							return false;
						}
					}
				}

				return super.method_5643(arg, f);
			}
		}
	}

	@Override
	public boolean method_7256(class_1657 arg) {
		return !this.method_14230() ? false : super.method_7256(arg);
	}

	private boolean method_14230() {
		return this.field_13995.method_3852();
	}

	@Nullable
	@Override
	public class_1297 method_5731(class_2874 arg) {
		this.field_13985 = true;
		class_2874 lv = this.field_6026;
		if (lv == class_2874.field_13078 && arg == class_2874.field_13072) {
			this.method_18375();
			this.method_14220().method_18770(this);
			if (!this.field_13989) {
				this.field_13989 = true;
				this.field_13987.method_14364(new class_2668(4, this.field_13969 ? 0.0F : 1.0F));
				this.field_13969 = true;
			}

			return this;
		} else {
			class_3218 lv2 = this.field_13995.method_3847(lv);
			this.field_6026 = arg;
			class_3218 lv3 = this.field_13995.method_3847(arg);
			class_31 lv4 = this.field_6002.method_8401();
			this.field_13987.method_14364(new class_2724(arg, lv4.method_153(), this.field_13974.method_14257()));
			this.field_13987.method_14364(new class_2632(lv4.method_207(), lv4.method_197()));
			class_3324 lv5 = this.field_13995.method_3760();
			lv5.method_14576(this);
			lv2.method_18770(this);
			this.field_5988 = false;
			double d = this.field_5987;
			double e = this.field_6010;
			double f = this.field_6035;
			float g = this.field_5965;
			float h = this.field_6031;
			double i = 8.0;
			float j = h;
			lv2.method_16107().method_15396("moving");
			if (lv == class_2874.field_13072 && arg == class_2874.field_13076) {
				this.field_13994 = new class_243(this.field_5987, this.field_6010, this.field_6035);
				d /= 8.0;
				f /= 8.0;
			} else if (lv == class_2874.field_13076 && arg == class_2874.field_13072) {
				d *= 8.0;
				f *= 8.0;
			} else if (lv == class_2874.field_13072 && arg == class_2874.field_13078) {
				class_2338 lv6 = lv3.method_14169();
				d = (double)lv6.method_10263();
				e = (double)lv6.method_10264();
				f = (double)lv6.method_10260();
				h = 90.0F;
				g = 0.0F;
			}

			this.method_5808(d, e, f, h, g);
			lv2.method_16107().method_15407();
			lv2.method_16107().method_15396("placing");
			double k = Math.min(-2.9999872E7, lv3.method_8621().method_11976() + 16.0);
			double l = Math.min(-2.9999872E7, lv3.method_8621().method_11958() + 16.0);
			double m = Math.min(2.9999872E7, lv3.method_8621().method_11963() - 16.0);
			double n = Math.min(2.9999872E7, lv3.method_8621().method_11977() - 16.0);
			d = class_3532.method_15350(d, k, m);
			f = class_3532.method_15350(f, l, n);
			this.method_5808(d, e, f, h, g);
			if (arg == class_2874.field_13078) {
				int o = class_3532.method_15357(this.field_5987);
				int p = class_3532.method_15357(this.field_6010) - 1;
				int q = class_3532.method_15357(this.field_6035);
				int r = 1;
				int s = 0;

				for (int t = -2; t <= 2; t++) {
					for (int u = -2; u <= 2; u++) {
						for (int v = -1; v < 3; v++) {
							int w = o + u * 1 + t * 0;
							int x = p + v;
							int y = q + u * 0 - t * 1;
							boolean bl = v < 0;
							lv3.method_8501(new class_2338(w, x, y), bl ? class_2246.field_10540.method_9564() : class_2246.field_10124.method_9564());
						}
					}
				}

				this.method_5808((double)o, (double)p, (double)q, h, 0.0F);
				this.method_18799(class_243.field_1353);
			} else if (!lv3.method_14173().method_8653(this, j)) {
				lv3.method_14173().method_8654(this);
				lv3.method_14173().method_8653(this, j);
			}

			lv2.method_16107().method_15407();
			this.method_5866(lv3);
			lv3.method_18211(this);
			this.method_18783(lv2);
			this.field_13987.method_14363(this.field_5987, this.field_6010, this.field_6035, h, g);
			this.field_13974.method_14259(lv3);
			this.field_13987.method_14364(new class_2696(this.field_7503));
			lv5.method_14606(this, lv3);
			lv5.method_14594(this);

			for (class_1293 lv7 : this.method_6026()) {
				this.field_13987.method_14364(new class_2783(this.method_5628(), lv7));
			}

			this.field_13987.method_14364(new class_2673(1032, class_2338.field_10980, 0, false));
			this.field_13978 = -1;
			this.field_13997 = -1.0F;
			this.field_13979 = -1;
			return this;
		}
	}

	private void method_18783(class_3218 arg) {
		class_2874 lv = arg.field_9247.method_12460();
		class_2874 lv2 = this.field_6002.field_9247.method_12460();
		class_174.field_1183.method_8794(this, lv, lv2);
		if (lv == class_2874.field_13076 && lv2 == class_2874.field_13072 && this.field_13994 != null) {
			class_174.field_1211.method_9080(this, this.field_13994);
		}

		if (lv2 != class_2874.field_13076) {
			this.field_13994 = null;
		}
	}

	@Override
	public boolean method_5680(class_3222 arg) {
		if (arg.method_7325()) {
			return this.method_14242() == this;
		} else {
			return this.method_7325() ? false : super.method_5680(arg);
		}
	}

	private void method_14216(class_2586 arg) {
		if (arg != null) {
			class_2622 lv = arg.method_16886();
			if (lv != null) {
				this.field_13987.method_14364(lv);
			}
		}
	}

	@Override
	public void method_6103(class_1297 arg, int i) {
		super.method_6103(arg, i);
		this.field_7512.method_7623();
	}

	@Override
	public Either<class_1657.class_1658, class_3902> method_7269(class_2338 arg) {
		return super.method_7269(arg).ifRight(argx -> {
			this.method_7281(class_3468.field_15381);
			class_174.field_1212.method_9027(this);
		});
	}

	@Override
	public void method_7358(boolean bl, boolean bl2, boolean bl3) {
		if (this.method_6113()) {
			this.method_14220().method_14178().method_18751(this, new class_2616(this, 2));
		}

		super.method_7358(bl, bl2, bl3);
		if (this.field_13987 != null) {
			this.field_13987.method_14363(this.field_5987, this.field_6010, this.field_6035, this.field_6031, this.field_5965);
		}
	}

	@Override
	public boolean method_5873(class_1297 arg, boolean bl) {
		class_1297 lv = this.method_5854();
		if (!super.method_5873(arg, bl)) {
			return false;
		} else {
			class_1297 lv2 = this.method_5854();
			if (lv2 != lv && this.field_13987 != null) {
				this.field_13987.method_14363(this.field_5987, this.field_6010, this.field_6035, this.field_6031, this.field_5965);
			}

			return true;
		}
	}

	@Override
	public void method_5848() {
		class_1297 lv = this.method_5854();
		super.method_5848();
		class_1297 lv2 = this.method_5854();
		if (lv2 != lv && this.field_13987 != null) {
			this.field_13987.method_14363(this.field_5987, this.field_6010, this.field_6035, this.field_6031, this.field_5965);
		}
	}

	@Override
	public boolean method_5679(class_1282 arg) {
		return super.method_5679(arg) || this.method_14208() || this.field_7503.field_7480 && arg == class_1282.field_5850;
	}

	@Override
	protected void method_5623(double d, boolean bl, class_2680 arg, class_2338 arg2) {
	}

	@Override
	protected void method_6126(class_2338 arg) {
		if (!this.method_7325()) {
			super.method_6126(arg);
		}
	}

	public void method_14207(double d, boolean bl) {
		int i = class_3532.method_15357(this.field_5987);
		int j = class_3532.method_15357(this.field_6010 - 0.2F);
		int k = class_3532.method_15357(this.field_6035);
		class_2338 lv = new class_2338(i, j, k);
		if (this.field_6002.method_8591(lv)) {
			class_2680 lv2 = this.field_6002.method_8320(lv);
			if (lv2.method_11588()) {
				class_2338 lv3 = lv.method_10074();
				class_2680 lv4 = this.field_6002.method_8320(lv3);
				class_2248 lv5 = lv4.method_11614();
				if (lv5.method_9525(class_3481.field_16584) || lv5.method_9525(class_3481.field_15504) || lv5 instanceof class_2349) {
					lv = lv3;
					lv2 = lv4;
				}
			}

			super.method_5623(d, bl, lv2, lv);
		}
	}

	@Override
	public void method_7311(class_2625 arg) {
		arg.method_11306(this);
		this.field_13987.method_14364(new class_2693(arg.method_11016()));
	}

	private void method_14237() {
		this.field_13986 = this.field_13986 % 100 + 1;
	}

	@Override
	public OptionalInt method_17355(@Nullable class_3908 arg) {
		if (arg == null) {
			return OptionalInt.empty();
		} else {
			if (this.field_7512 != this.field_7498) {
				this.method_7346();
			}

			this.method_14237();
			class_1703 lv = arg.createMenu(this.field_13986, this.field_7514, this);
			if (lv == null) {
				if (this.method_7325()) {
					this.method_7353(new class_2588("container.spectatorCantOpen").method_10854(class_124.field_1061), true);
				}

				return OptionalInt.empty();
			} else {
				this.field_13987.method_14364(new class_3944(lv.field_7763, lv.method_17358(), arg.method_5476()));
				lv.method_7596(this);
				this.field_7512 = lv;
				return OptionalInt.of(this.field_13986);
			}
		}
	}

	@Override
	public void method_17354(int i, class_1916 arg, int j, int k, boolean bl, boolean bl2) {
		this.field_13987.method_14364(new class_3943(i, arg, j, k, bl, bl2));
	}

	@Override
	public void method_7291(class_1496 arg, class_1263 arg2) {
		if (this.field_7512 != this.field_7498) {
			this.method_7346();
		}

		this.method_14237();
		this.field_13987.method_14364(new class_2648(this.field_13986, arg2.method_5439(), arg.method_5628()));
		this.field_7512 = new class_1724(this.field_13986, this.field_7514, arg2, arg);
		this.field_7512.method_7596(this);
	}

	@Override
	public void method_7315(class_1799 arg, class_1268 arg2) {
		class_1792 lv = arg.method_7909();
		if (lv == class_1802.field_8360) {
			if (class_1843.method_8054(arg, this.method_5671(), this)) {
				this.field_7512.method_7623();
			}

			this.field_13987.method_14364(new class_3895(arg2));
		}
	}

	@Override
	public void method_7323(class_2593 arg) {
		arg.method_11037(true);
		this.method_14216(arg);
	}

	@Override
	public void method_7635(class_1703 arg, int i, class_1799 arg2) {
		if (!(arg.method_7611(i) instanceof class_1734)) {
			if (arg == this.field_7498) {
				class_174.field_1195.method_8950(this, this.field_7514);
			}

			if (!this.field_13991) {
				this.field_13987.method_14364(new class_2653(arg.field_7763, i, arg2));
			}
		}
	}

	public void method_14204(class_1703 arg) {
		this.method_7634(arg, arg.method_7602());
	}

	@Override
	public void method_7634(class_1703 arg, class_2371<class_1799> arg2) {
		this.field_13987.method_14364(new class_2649(arg.field_7763, arg2));
		this.field_13987.method_14364(new class_2653(-1, -1, this.field_7514.method_7399()));
	}

	@Override
	public void method_7633(class_1703 arg, int i, int j) {
		this.field_13987.method_14364(new class_2651(arg.field_7763, i, j));
	}

	@Override
	public void method_7346() {
		this.field_13987.method_14364(new class_2645(this.field_7512.field_7763));
		this.method_14247();
	}

	public void method_14241() {
		if (!this.field_13991) {
			this.field_13987.method_14364(new class_2653(-1, -1, this.field_7514.method_7399()));
		}
	}

	public void method_14247() {
		this.field_7512.method_7595(this);
		this.field_7512 = this.field_7498;
	}

	public void method_14218(float f, float g, boolean bl, boolean bl2) {
		if (this.method_5765()) {
			if (f >= -1.0F && f <= 1.0F) {
				this.field_6212 = f;
			}

			if (g >= -1.0F && g <= 1.0F) {
				this.field_6250 = g;
			}

			this.field_6282 = bl;
			this.method_5660(bl2);
		}
	}

	@Override
	public void method_7342(class_3445<?> arg, int i) {
		this.field_13966.method_15022(this, arg, i);
		this.method_7327().method_1162(arg, this.method_5820(), argx -> argx.method_1124(i));
	}

	@Override
	public void method_7266(class_3445<?> arg) {
		this.field_13966.method_15023(this, arg, 0);
		this.method_7327().method_1162(arg, this.method_5820(), class_267::method_1132);
	}

	@Override
	public int method_7254(Collection<class_1860<?>> collection) {
		return this.field_13996.method_14903(collection, this);
	}

	@Override
	public void method_7335(class_2960[] args) {
		List<class_1860<?>> list = Lists.<class_1860<?>>newArrayList();

		for (class_2960 lv : args) {
			this.field_13995.method_3772().method_8130(lv).ifPresent(list::add);
		}

		this.method_7254(list);
	}

	@Override
	public int method_7333(Collection<class_1860<?>> collection) {
		return this.field_13996.method_14900(collection, this);
	}

	@Override
	public void method_7255(int i) {
		super.method_7255(i);
		this.field_13978 = -1;
	}

	public void method_14231() {
		this.field_13964 = true;
		this.method_5772();
		if (this.method_6113()) {
			this.method_7358(true, false, false);
		}
	}

	public boolean method_14239() {
		return this.field_13964;
	}

	public void method_14217() {
		this.field_13997 = -1.0E8F;
	}

	@Override
	public void method_7353(class_2561 arg, boolean bl) {
		this.field_13987.method_14364(new class_2635(arg, bl ? class_2556.field_11733 : class_2556.field_11737));
	}

	@Override
	protected void method_6040() {
		if (!this.field_6277.method_7960() && this.method_6115()) {
			this.field_13987.method_14364(new class_2663(this, (byte)9));
			super.method_6040();
		}
	}

	@Override
	public void method_5702(class_2183.class_2184 arg, class_243 arg2) {
		super.method_5702(arg, arg2);
		this.field_13987.method_14364(new class_2707(arg, arg2.field_1352, arg2.field_1351, arg2.field_1350));
	}

	public void method_14222(class_2183.class_2184 arg, class_1297 arg2, class_2183.class_2184 arg3) {
		class_243 lv = arg3.method_9302(arg2);
		super.method_5702(arg, lv);
		this.field_13987.method_14364(new class_2707(arg, arg2, arg3));
	}

	public void method_14203(class_3222 arg, boolean bl) {
		if (bl) {
			this.field_7514.method_7377(arg.field_7514);
			this.method_6033(arg.method_6032());
			this.field_7493 = arg.field_7493;
			this.field_7520 = arg.field_7520;
			this.field_7495 = arg.field_7495;
			this.field_7510 = arg.field_7510;
			this.method_7320(arg.method_7272());
			this.field_5991 = arg.field_5991;
			this.field_6020 = arg.field_6020;
			this.field_6028 = arg.field_6028;
		} else if (this.field_6002.method_8450().method_8355(class_1928.field_19389) || arg.method_7325()) {
			this.field_7514.method_7377(arg.field_7514);
			this.field_7520 = arg.field_7520;
			this.field_7495 = arg.field_7495;
			this.field_7510 = arg.field_7510;
			this.method_7320(arg.method_7272());
		}

		this.field_7494 = arg.field_7494;
		this.field_7486 = arg.field_7486;
		this.method_5841().method_12778(field_7518, arg.method_5841().method_12789(field_7518));
		this.field_13978 = -1;
		this.field_13997 = -1.0F;
		this.field_13979 = -1;
		this.field_13996.method_14875(arg.field_13996);
		this.field_13988.addAll(arg.field_13988);
		this.field_13969 = arg.field_13969;
		this.field_13994 = arg.field_13994;
		this.method_7273(arg.method_7356());
		this.method_7345(arg.method_7308());
	}

	@Override
	protected void method_6020(class_1293 arg) {
		super.method_6020(arg);
		this.field_13987.method_14364(new class_2783(this.method_5628(), arg));
		if (arg.method_5579() == class_1294.field_5902) {
			this.field_13973 = this.field_6012;
			this.field_13992 = new class_243(this.field_5987, this.field_6010, this.field_6035);
		}

		class_174.field_1193.method_8863(this);
	}

	@Override
	protected void method_6009(class_1293 arg, boolean bl) {
		super.method_6009(arg, bl);
		this.field_13987.method_14364(new class_2783(this.method_5628(), arg));
		class_174.field_1193.method_8863(this);
	}

	@Override
	protected void method_6129(class_1293 arg) {
		super.method_6129(arg);
		this.field_13987.method_14364(new class_2718(this.method_5628(), arg.method_5579()));
		if (arg.method_5579() == class_1294.field_5902) {
			this.field_13992 = null;
		}

		class_174.field_1193.method_8863(this);
	}

	@Override
	public void method_5859(double d, double e, double f) {
		this.field_13987.method_14363(d, e, f, this.field_6031, this.field_5965);
	}

	@Override
	public void method_7277(class_1297 arg) {
		this.method_14220().method_14178().method_18751(this, new class_2616(arg, 4));
	}

	@Override
	public void method_7304(class_1297 arg) {
		this.method_14220().method_14178().method_18751(this, new class_2616(arg, 5));
	}

	@Override
	public void method_7355() {
		if (this.field_13987 != null) {
			this.field_13987.method_14364(new class_2696(this.field_7503));
			this.method_6027();
		}
	}

	public class_3218 method_14220() {
		return (class_3218)this.field_6002;
	}

	@Override
	public void method_7336(class_1934 arg) {
		this.field_13974.method_14261(arg);
		this.field_13987.method_14364(new class_2668(3, (float)arg.method_8379()));
		if (arg == class_1934.field_9219) {
			this.method_7262();
			this.method_5848();
		} else {
			this.method_14224(this);
		}

		this.method_7355();
		this.method_6008();
	}

	@Override
	public boolean method_7325() {
		return this.field_13974.method_14257() == class_1934.field_9219;
	}

	@Override
	public boolean method_7337() {
		return this.field_13974.method_14257() == class_1934.field_9220;
	}

	@Override
	public void method_9203(class_2561 arg) {
		this.method_14254(arg, class_2556.field_11735);
	}

	public void method_14254(class_2561 arg, class_2556 arg2) {
		this.field_13987
			.method_14369(
				new class_2635(arg, arg2),
				future -> {
					if (!future.isSuccess() && (arg2 == class_2556.field_11733 || arg2 == class_2556.field_11735)) {
						int i = 256;
						String string = arg.method_10858(256);
						class_2561 lv = new class_2585(string).method_10854(class_124.field_1054);
						this.field_13987
							.method_14364(new class_2635(new class_2588("multiplayer.message_not_delivered", lv).method_10854(class_124.field_1061), class_2556.field_11735));
					}
				}
			);
	}

	public String method_14209() {
		String string = this.field_13987.field_14127.method_10755().toString();
		string = string.substring(string.indexOf("/") + 1);
		return string.substring(0, string.indexOf(":"));
	}

	public void method_14213(class_2803 arg) {
		this.field_13981 = arg.method_12131();
		this.field_13993 = arg.method_12134();
		this.field_13971 = arg.method_12135();
		this.method_5841().method_12778(field_7518, (byte)arg.method_12136());
		this.method_5841().method_12778(field_7488, (byte)(arg.method_12132() == class_1306.field_6182 ? 0 : 1));
	}

	public class_1659 method_14238() {
		return this.field_13993;
	}

	public void method_14255(String string, String string2) {
		this.field_13987.method_14364(new class_2720(string, string2));
	}

	@Override
	protected int method_5691() {
		return this.field_13995.method_3835(this.method_7334());
	}

	public void method_14234() {
		this.field_13976 = class_156.method_658();
	}

	public class_3442 method_14248() {
		return this.field_13966;
	}

	public class_3441 method_14253() {
		return this.field_13996;
	}

	public void method_14249(class_1297 arg) {
		if (arg instanceof class_1657) {
			this.field_13987.method_14364(new class_2716(arg.method_5628()));
		} else {
			this.field_13988.add(arg.method_5628());
		}
	}

	public void method_14211(class_1297 arg) {
		this.field_13988.remove(arg.method_5628());
	}

	@Override
	protected void method_6027() {
		if (this.method_7325()) {
			this.method_6069();
			this.method_5648(true);
		} else {
			super.method_6027();
		}
	}

	public class_1297 method_14242() {
		return (class_1297)(this.field_13984 == null ? this : this.field_13984);
	}

	public void method_14224(class_1297 arg) {
		class_1297 lv = this.method_14242();
		this.field_13984 = (class_1297)(arg == null ? this : arg);
		if (lv != this.field_13984) {
			this.field_13987.method_14364(new class_2734(this.field_13984));
			this.method_5859(this.field_13984.field_5987, this.field_13984.field_6010, this.field_13984.field_6035);
		}
	}

	@Override
	protected void method_5760() {
		if (this.field_6018 > 0 && !this.field_13985) {
			this.field_6018--;
		}
	}

	@Override
	public void method_7324(class_1297 arg) {
		if (this.field_13974.method_14257() == class_1934.field_9219) {
			this.method_14224(arg);
		} else {
			super.method_7324(arg);
		}
	}

	public long method_14219() {
		return this.field_13976;
	}

	@Nullable
	public class_2561 method_14206() {
		return null;
	}

	@Override
	public void method_6104(class_1268 arg) {
		super.method_6104(arg);
		if (this.method_7261(0.0F) > 1.0F) {
			this.method_7350();
		}
	}

	public boolean method_14208() {
		return this.field_13985;
	}

	public void method_14240() {
		this.field_13985 = false;
	}

	public void method_14243() {
		this.method_5729(7, true);
	}

	public void method_14229() {
		this.method_5729(7, true);
		this.method_5729(7, false);
	}

	public class_2985 method_14236() {
		return this.field_13970;
	}

	public void method_14251(class_3218 arg, double d, double e, double f, float g, float h) {
		this.method_14224(this);
		this.method_5848();
		if (arg == this.field_6002) {
			this.field_13987.method_14363(d, e, f, g, h);
		} else {
			class_3218 lv = this.method_14220();
			this.field_6026 = arg.field_9247.method_12460();
			class_31 lv2 = arg.method_8401();
			this.field_13987.method_14364(new class_2724(this.field_6026, lv2.method_153(), this.field_13974.method_14257()));
			this.field_13987.method_14364(new class_2632(lv2.method_207(), lv2.method_197()));
			this.field_13995.method_3760().method_14576(this);
			lv.method_18770(this);
			this.field_5988 = false;
			this.method_5808(d, e, f, g, h);
			this.method_5866(arg);
			arg.method_18207(this);
			this.method_18783(lv);
			this.field_13987.method_14363(d, e, f, g, h);
			this.field_13974.method_14259(arg);
			this.field_13995.method_3760().method_14606(this, arg);
			this.field_13995.method_3760().method_14594(this);
		}
	}

	public void method_14205(class_1923 arg, class_2596<?> arg2, class_2596<?> arg3) {
		this.field_13987.method_14364(arg3);
		this.field_13987.method_14364(arg2);
	}

	public void method_14246(class_1923 arg) {
		this.field_13987.method_14364(new class_2666(arg.field_9181, arg.field_9180));
	}

	public class_4076 method_14232() {
		return this.field_13990;
	}

	public void method_17668(class_4076 arg) {
		this.field_13990 = arg;
	}

	@Override
	public void method_17356(class_3414 arg, class_3419 arg2, float f, float g) {
		this.field_13987.method_14364(new class_2767(arg, arg2, this.field_5987, this.field_6010, this.field_6035, f, g));
	}

	@Override
	public class_2596<?> method_18002() {
		return new class_2613(this);
	}

	@Override
	public class_1542 method_7329(class_1799 arg, boolean bl, boolean bl2) {
		class_1542 lv = super.method_7329(arg, bl, bl2);
		if (lv == null) {
			return null;
		} else {
			this.field_6002.method_8649(lv);
			class_1799 lv2 = lv.method_6983();
			if (bl2) {
				if (!lv2.method_7960()) {
					this.method_7342(class_3468.field_15405.method_14956(lv2.method_7909()), arg.method_7947());
				}

				this.method_7281(class_3468.field_15406);
			}

			return lv;
		}
	}
}
