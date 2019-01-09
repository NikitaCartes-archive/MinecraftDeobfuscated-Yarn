package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_1657 extends class_1309 {
	private static final class_2940<Float> field_7491 = class_2945.method_12791(class_1657.class, class_2943.field_13320);
	private static final class_2940<Integer> field_7511 = class_2945.method_12791(class_1657.class, class_2943.field_13327);
	protected static final class_2940<Byte> field_7518 = class_2945.method_12791(class_1657.class, class_2943.field_13319);
	protected static final class_2940<Byte> field_7488 = class_2945.method_12791(class_1657.class, class_2943.field_13319);
	protected static final class_2940<class_2487> field_7496 = class_2945.method_12791(class_1657.class, class_2943.field_13318);
	protected static final class_2940<class_2487> field_7506 = class_2945.method_12791(class_1657.class, class_2943.field_13318);
	public final class_1661 field_7514 = new class_1661(this);
	protected class_1730 field_7486 = new class_1730();
	public final class_1723 field_7498;
	public class_1703 field_7512;
	protected class_1702 field_7493 = new class_1702();
	protected int field_7489;
	public float field_7505;
	public float field_7483;
	public int field_7504;
	public double field_7524;
	public double field_7502;
	public double field_7522;
	public double field_7500;
	public double field_7521;
	public double field_7499;
	protected boolean field_7492;
	public class_2338 field_7519;
	private int field_7487;
	public float field_7516;
	@Environment(EnvType.CLIENT)
	public float field_7485;
	public float field_7497;
	private boolean field_7517;
	protected boolean field_7490;
	private class_2338 field_7501;
	private boolean field_7515;
	public final class_1656 field_7503 = new class_1656();
	public int field_7520;
	public int field_7495;
	public float field_7510;
	protected int field_7494;
	protected final float field_7509 = 0.02F;
	private int field_7508;
	private final GameProfile field_7507;
	@Environment(EnvType.CLIENT)
	private boolean field_7523;
	private class_1799 field_7525 = class_1799.field_8037;
	private final class_1796 field_7484 = this.method_7265();
	@Nullable
	public class_1536 field_7513;

	public class_1657(class_1937 arg, GameProfile gameProfile) {
		super(class_1299.field_6097, arg);
		this.method_5826(method_7271(gameProfile));
		this.field_7507 = gameProfile;
		this.field_7498 = new class_1723(this.field_7514, !arg.field_9236, this);
		this.field_7512 = this.field_7498;
		class_2338 lv = arg.method_8395();
		this.method_5808((double)lv.method_10263() + 0.5, (double)(lv.method_10264() + 1), (double)lv.method_10260() + 0.5, 0.0F, 0.0F);
		this.field_6215 = 180.0F;
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_6127().method_6208(class_1612.field_7363).method_6192(1.0);
		this.method_5996(class_1612.field_7357).method_6192(0.1F);
		this.method_6127().method_6208(class_1612.field_7356);
		this.method_6127().method_6208(class_1612.field_7362);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_7491, 0.0F);
		this.field_6011.method_12784(field_7511, 0);
		this.field_6011.method_12784(field_7518, (byte)0);
		this.field_6011.method_12784(field_7488, (byte)1);
		this.field_6011.method_12784(field_7496, new class_2487());
		this.field_6011.method_12784(field_7506, new class_2487());
	}

	@Override
	public void method_5773() {
		this.field_5960 = this.method_7325();
		if (this.method_7325()) {
			this.field_5952 = false;
		}

		if (this.field_7504 > 0) {
			this.field_7504--;
		}

		if (this.method_6113()) {
			this.field_7487++;
			if (this.field_7487 > 100) {
				this.field_7487 = 100;
			}

			if (!this.field_6002.field_9236) {
				if (!this.method_7275()) {
					this.method_7358(true, true, false);
				} else if (this.field_6002.method_8530()) {
					this.method_7358(false, true, true);
				}
			}
		} else if (this.field_7487 > 0) {
			this.field_7487++;
			if (this.field_7487 >= 110) {
				this.field_7487 = 0;
			}
		}

		this.method_7300();
		this.method_7295();
		super.method_5773();
		if (!this.field_6002.field_9236 && this.field_7512 != null && !this.field_7512.method_7597(this)) {
			this.method_7346();
			this.field_7512 = this.field_7498;
		}

		if (this.method_5809() && this.field_7503.field_7480) {
			this.method_5646();
		}

		this.method_7313();
		if (!this.field_6002.field_9236) {
			this.field_7493.method_7588(this);
			this.method_7281(class_3468.field_15417);
			if (this.method_5805()) {
				this.method_7281(class_3468.field_15400);
			}

			if (this.method_5715()) {
				this.method_7281(class_3468.field_15422);
			}

			if (!this.method_6113()) {
				this.method_7281(class_3468.field_15429);
			}
		}

		int i = 29999999;
		double d = class_3532.method_15350(this.field_5987, -2.9999999E7, 2.9999999E7);
		double e = class_3532.method_15350(this.field_6035, -2.9999999E7, 2.9999999E7);
		if (d != this.field_5987 || e != this.field_6035) {
			this.method_5814(d, this.field_6010, e);
		}

		this.field_6273++;
		class_1799 lv = this.method_6047();
		if (!class_1799.method_7973(this.field_7525, lv)) {
			if (!class_1799.method_7987(this.field_7525, lv)) {
				this.method_7350();
			}

			this.field_7525 = lv.method_7960() ? class_1799.field_8037 : lv.method_7972();
		}

		this.method_7330();
		this.field_7484.method_7903();
		this.method_7318();
	}

	protected boolean method_7295() {
		this.field_7490 = this.method_5744(class_3486.field_15517, true);
		return this.field_7490;
	}

	private void method_7330() {
		class_1799 lv = this.method_6118(class_1304.field_6169);
		if (lv.method_7909() == class_1802.field_8090 && !this.method_5777(class_3486.field_15517)) {
			this.method_6092(new class_1293(class_1294.field_5923, 200, 0, false, false, true));
		}
	}

	protected class_1796 method_7265() {
		return new class_1796();
	}

	private void method_7300() {
		class_2680 lv = this.field_6002.method_8475(this.method_5829().method_1009(0.0, -0.4F, 0.0).method_1011(0.001), class_2246.field_10422);
		if (lv != null) {
			if (!this.field_7517 && !this.field_5953 && lv.method_11614() == class_2246.field_10422 && !this.method_7325()) {
				boolean bl = (Boolean)lv.method_11654(class_2258.field_10680);
				if (bl) {
					this.field_6002.method_8486(this.field_5987, this.field_6010, this.field_6035, class_3417.field_14752, this.method_5634(), 1.0F, 1.0F, false);
				} else {
					this.field_6002.method_8486(this.field_5987, this.field_6010, this.field_6035, class_3417.field_14570, this.method_5634(), 1.0F, 1.0F, false);
				}
			}

			this.field_7517 = true;
		} else {
			this.field_7517 = false;
		}
	}

	private void method_7313() {
		this.field_7524 = this.field_7500;
		this.field_7502 = this.field_7521;
		this.field_7522 = this.field_7499;
		double d = this.field_5987 - this.field_7500;
		double e = this.field_6010 - this.field_7521;
		double f = this.field_6035 - this.field_7499;
		double g = 10.0;
		if (d > 10.0) {
			this.field_7500 = this.field_5987;
			this.field_7524 = this.field_7500;
		}

		if (f > 10.0) {
			this.field_7499 = this.field_6035;
			this.field_7522 = this.field_7499;
		}

		if (e > 10.0) {
			this.field_7521 = this.field_6010;
			this.field_7502 = this.field_7521;
		}

		if (d < -10.0) {
			this.field_7500 = this.field_5987;
			this.field_7524 = this.field_7500;
		}

		if (f < -10.0) {
			this.field_7499 = this.field_6035;
			this.field_7522 = this.field_7499;
		}

		if (e < -10.0) {
			this.field_7521 = this.field_6010;
			this.field_7502 = this.field_7521;
		}

		this.field_7500 += d * 0.25;
		this.field_7499 += f * 0.25;
		this.field_7521 += e * 0.25;
	}

	protected void method_7318() {
		float f;
		float g;
		if (this.method_6128()) {
			f = 0.6F;
			g = 0.6F;
		} else if (this.method_6113()) {
			f = 0.2F;
			g = 0.2F;
		} else if (this.method_5681() || this.method_6123()) {
			f = 0.6F;
			g = 0.6F;
		} else if (this.method_5715()) {
			f = 0.6F;
			g = 1.65F;
		} else {
			f = 0.6F;
			g = 1.8F;
		}

		if (f != this.field_5998 || g != this.field_6019) {
			class_238 lv = this.method_5829();
			lv = new class_238(lv.field_1323, lv.field_1322, lv.field_1321, lv.field_1323 + (double)f, lv.field_1322 + (double)g, lv.field_1321 + (double)f);
			if (this.field_6002.method_8587(null, lv)) {
				this.method_5835(f, g);
			}
		}
	}

	@Override
	public int method_5741() {
		return this.field_7503.field_7480 ? 1 : 80;
	}

	@Override
	protected class_3414 method_5737() {
		return class_3417.field_14998;
	}

	@Override
	protected class_3414 method_5625() {
		return class_3417.field_14810;
	}

	@Override
	protected class_3414 method_5672() {
		return class_3417.field_14876;
	}

	@Override
	public int method_5806() {
		return 10;
	}

	@Override
	public void method_5783(class_3414 arg, float f, float g) {
		this.field_6002.method_8465(this, this.field_5987, this.field_6010, this.field_6035, arg, this.method_5634(), f, g);
	}

	public void method_17356(class_3414 arg, class_3419 arg2, float f, float g) {
	}

	@Override
	public class_3419 method_5634() {
		return class_3419.field_15248;
	}

	@Override
	protected int method_5676() {
		return 20;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 9) {
			this.method_6040();
		} else if (b == 23) {
			this.field_7523 = false;
		} else if (b == 22) {
			this.field_7523 = true;
		} else if (b == 43) {
			this.method_16475(class_2398.field_11204);
		} else {
			super.method_5711(b);
		}
	}

	@Environment(EnvType.CLIENT)
	private void method_16475(class_2394 arg) {
		for (int i = 0; i < 5; i++) {
			double d = this.field_5974.nextGaussian() * 0.02;
			double e = this.field_5974.nextGaussian() * 0.02;
			double f = this.field_5974.nextGaussian() * 0.02;
			this.field_6002
				.method_8406(
					arg,
					this.field_5987 + (double)(this.field_5974.nextFloat() * this.field_5998 * 2.0F) - (double)this.field_5998,
					this.field_6010 + 1.0 + (double)(this.field_5974.nextFloat() * this.field_6019),
					this.field_6035 + (double)(this.field_5974.nextFloat() * this.field_5998 * 2.0F) - (double)this.field_5998,
					d,
					e,
					f
				);
		}
	}

	@Override
	protected boolean method_6062() {
		return this.method_6032() <= 0.0F || this.method_6113();
	}

	protected void method_7346() {
		this.field_7512 = this.field_7498;
	}

	@Override
	public void method_5842() {
		if (!this.field_6002.field_9236 && this.method_5715() && this.method_5765()) {
			this.method_5848();
			this.method_5660(false);
		} else {
			double d = this.field_5987;
			double e = this.field_6010;
			double f = this.field_6035;
			float g = this.field_6031;
			float h = this.field_5965;
			super.method_5842();
			this.field_7505 = this.field_7483;
			this.field_7483 = 0.0F;
			this.method_7260(this.field_5987 - d, this.field_6010 - e, this.field_6035 - f);
			if (this.method_5854() instanceof class_1452) {
				this.field_5965 = h;
				this.field_6031 = g;
				this.field_6283 = ((class_1452)this.method_5854()).field_6283;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5823() {
		this.method_5835(0.6F, 1.8F);
		super.method_5823();
		this.method_6033(this.method_6063());
		this.field_6213 = 0;
	}

	@Override
	protected void method_6023() {
		super.method_6023();
		this.method_6119();
		this.field_6241 = this.field_6031;
	}

	@Override
	public void method_6007() {
		if (this.field_7489 > 0) {
			this.field_7489--;
		}

		if (this.field_6002.method_8407() == class_1267.field_5801 && this.field_6002.method_8450().method_8355("naturalRegeneration")) {
			if (this.method_6032() < this.method_6063() && this.field_6012 % 20 == 0) {
				this.method_6025(1.0F);
			}

			if (this.field_7493.method_7587() && this.field_6012 % 10 == 0) {
				this.field_7493.method_7580(this.field_7493.method_7586() + 1);
			}
		}

		this.field_7514.method_7381();
		this.field_7505 = this.field_7483;
		super.method_6007();
		class_1324 lv = this.method_5996(class_1612.field_7357);
		if (!this.field_6002.field_9236) {
			lv.method_6192((double)this.field_7503.method_7253());
		}

		this.field_6281 = 0.02F;
		if (this.method_5624()) {
			this.field_6281 = (float)((double)this.field_6281 + 0.005999999865889549);
		}

		this.method_6125((float)lv.method_6194());
		float f = class_3532.method_15368(this.field_5967 * this.field_5967 + this.field_6006 * this.field_6006);
		float g = (float)(Math.atan(-this.field_5984 * 0.2F) * 15.0);
		if (f > 0.1F) {
			f = 0.1F;
		}

		if (!this.field_5952 || this.method_6032() <= 0.0F || this.method_5681()) {
			f = 0.0F;
		}

		if (this.field_5952 || this.method_6032() <= 0.0F) {
			g = 0.0F;
		}

		this.field_7483 = this.field_7483 + (f - this.field_7483) * 0.4F;
		this.field_6223 = this.field_6223 + (g - this.field_6223) * 0.8F;
		if (this.method_6032() > 0.0F && !this.method_7325()) {
			class_238 lv2;
			if (this.method_5765() && !this.method_5854().field_5988) {
				lv2 = this.method_5829().method_991(this.method_5854().method_5829()).method_1009(1.0, 0.0, 1.0);
			} else {
				lv2 = this.method_5829().method_1009(1.0, 0.5, 1.0);
			}

			List<class_1297> list = this.field_6002.method_8335(this, lv2);

			for (int i = 0; i < list.size(); i++) {
				class_1297 lv3 = (class_1297)list.get(i);
				if (!lv3.field_5988) {
					this.method_7341(lv3);
				}
			}
		}

		this.method_7267(this.method_7356());
		this.method_7267(this.method_7308());
		if (!this.field_6002.field_9236 && (this.field_6017 > 0.5F || this.method_5799() || this.method_5765()) || this.field_7503.field_7479) {
			this.method_7262();
		}
	}

	private void method_7267(@Nullable class_2487 arg) {
		if (arg != null && !arg.method_10545("Silent") || !arg.method_10577("Silent")) {
			String string = arg.method_10558("id");
			if (class_1299.method_5898(string) == class_1299.field_6104) {
				class_1453.method_6589(this.field_6002, this);
			}
		}
	}

	private void method_7341(class_1297 arg) {
		arg.method_5694(this);
	}

	public int method_7272() {
		return this.field_6011.method_12789(field_7511);
	}

	public void method_7320(int i) {
		this.field_6011.method_12778(field_7511, i);
	}

	public void method_7285(int i) {
		int j = this.method_7272();
		this.field_6011.method_12778(field_7511, j + i);
	}

	@Override
	public void method_6078(class_1282 arg) {
		super.method_6078(arg);
		this.method_5835(0.2F, 0.2F);
		this.method_5814(this.field_5987, this.field_6010, this.field_6035);
		this.field_5984 = 0.1F;
		if ("Notch".equals(this.method_5477().getString())) {
			this.method_7329(new class_1799(class_1802.field_8279), true, false);
		}

		if (!this.method_7325()) {
			this.method_16080(arg);
		}

		if (arg != null) {
			this.field_5967 = (double)(-class_3532.method_15362((this.field_6271 + this.field_6031) * (float) (Math.PI / 180.0)) * 0.1F);
			this.field_6006 = (double)(-class_3532.method_15374((this.field_6271 + this.field_6031) * (float) (Math.PI / 180.0)) * 0.1F);
		} else {
			this.field_5967 = 0.0;
			this.field_6006 = 0.0;
		}

		this.method_7281(class_3468.field_15421);
		this.method_7266(class_3468.field_15419.method_14956(class_3468.field_15400));
		this.method_7266(class_3468.field_15419.method_14956(class_3468.field_15429));
		this.method_5646();
		this.method_5729(0, false);
	}

	@Override
	protected void method_16078() {
		super.method_16078();
		if (!this.field_6002.method_8450().method_8355("keepInventory")) {
			this.method_7293();
			this.field_7514.method_7388();
		}
	}

	protected void method_7293() {
		for (int i = 0; i < this.field_7514.method_5439(); i++) {
			class_1799 lv = this.field_7514.method_5438(i);
			if (!lv.method_7960() && class_1890.method_8221(lv)) {
				this.field_7514.method_5441(i);
			}
		}
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		if (arg == class_1282.field_5854) {
			return class_3417.field_14623;
		} else {
			return arg == class_1282.field_5859 ? class_3417.field_15205 : class_3417.field_15115;
		}
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_14904;
	}

	@Nullable
	public class_1542 method_7290(boolean bl) {
		return this.method_7329(
			this.field_7514
				.method_5434(this.field_7514.field_7545, bl && !this.field_7514.method_7391().method_7960() ? this.field_7514.method_7391().method_7947() : 1),
			false,
			true
		);
	}

	@Nullable
	public class_1542 method_7328(class_1799 arg, boolean bl) {
		return this.method_7329(arg, false, bl);
	}

	@Nullable
	public class_1542 method_7329(class_1799 arg, boolean bl, boolean bl2) {
		if (arg.method_7960()) {
			return null;
		} else {
			double d = this.field_6010 - 0.3F + (double)this.method_5751();
			class_1542 lv = new class_1542(this.field_6002, this.field_5987, d, this.field_6035, arg);
			lv.method_6982(40);
			if (bl2) {
				lv.method_6981(this.method_5667());
			}

			if (bl) {
				float f = this.field_5974.nextFloat() * 0.5F;
				float g = this.field_5974.nextFloat() * (float) (Math.PI * 2);
				lv.field_5967 = (double)(-class_3532.method_15374(g) * f);
				lv.field_6006 = (double)(class_3532.method_15362(g) * f);
				lv.field_5984 = 0.2F;
			} else {
				float f = 0.3F;
				lv.field_5967 = (double)(
					-class_3532.method_15374(this.field_6031 * (float) (Math.PI / 180.0)) * class_3532.method_15362(this.field_5965 * (float) (Math.PI / 180.0)) * f
				);
				lv.field_6006 = (double)(
					class_3532.method_15362(this.field_6031 * (float) (Math.PI / 180.0)) * class_3532.method_15362(this.field_5965 * (float) (Math.PI / 180.0)) * f
				);
				lv.field_5984 = (double)(-class_3532.method_15374(this.field_5965 * (float) (Math.PI / 180.0)) * f + 0.1F);
				float g = this.field_5974.nextFloat() * (float) (Math.PI * 2);
				f = 0.02F * this.field_5974.nextFloat();
				lv.field_5967 = lv.field_5967 + Math.cos((double)g) * (double)f;
				lv.field_5984 = lv.field_5984 + (double)((this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.1F);
				lv.field_6006 = lv.field_6006 + Math.sin((double)g) * (double)f;
			}

			class_1799 lv2 = this.method_7314(lv);
			if (bl2) {
				if (!lv2.method_7960()) {
					this.method_7342(class_3468.field_15405.method_14956(lv2.method_7909()), arg.method_7947());
				}

				this.method_7281(class_3468.field_15406);
			}

			return lv;
		}
	}

	protected class_1799 method_7314(class_1542 arg) {
		this.field_6002.method_8649(arg);
		return arg.method_6983();
	}

	public float method_7351(class_2680 arg) {
		float f = this.field_7514.method_7370(arg);
		if (f > 1.0F) {
			int i = class_1890.method_8234(this);
			class_1799 lv = this.method_6047();
			if (i > 0 && !lv.method_7960()) {
				f += (float)(i * i + 1);
			}
		}

		if (class_1292.method_5576(this)) {
			f *= 1.0F + (float)(class_1292.method_5575(this) + 1) * 0.2F;
		}

		if (this.method_6059(class_1294.field_5901)) {
			float g;
			switch (this.method_6112(class_1294.field_5901).method_5578()) {
				case 0:
					g = 0.3F;
					break;
				case 1:
					g = 0.09F;
					break;
				case 2:
					g = 0.0027F;
					break;
				case 3:
				default:
					g = 8.1E-4F;
			}

			f *= g;
		}

		if (this.method_5777(class_3486.field_15517) && !class_1890.method_8200(this)) {
			f /= 5.0F;
		}

		if (!this.field_5952) {
			f /= 5.0F;
		}

		return f;
	}

	public boolean method_7305(class_2680 arg) {
		return arg.method_11620().method_15805() || this.field_7514.method_7383(arg);
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.method_5826(method_7271(this.field_7507));
		class_2499 lv = arg.method_10554("Inventory", 10);
		this.field_7514.method_7397(lv);
		this.field_7514.field_7545 = arg.method_10550("SelectedItemSlot");
		this.field_7492 = arg.method_10577("Sleeping");
		this.field_7487 = arg.method_10568("SleepTimer");
		this.field_7510 = arg.method_10583("XpP");
		this.field_7520 = arg.method_10550("XpLevel");
		this.field_7495 = arg.method_10550("XpTotal");
		this.field_7494 = arg.method_10550("XpSeed");
		if (this.field_7494 == 0) {
			this.field_7494 = this.field_5974.nextInt();
		}

		this.method_7320(arg.method_10550("Score"));
		if (this.field_7492) {
			this.field_7519 = new class_2338(this);
			this.method_7358(true, true, false);
		}

		if (arg.method_10573("SpawnX", 99) && arg.method_10573("SpawnY", 99) && arg.method_10573("SpawnZ", 99)) {
			this.field_7501 = new class_2338(arg.method_10550("SpawnX"), arg.method_10550("SpawnY"), arg.method_10550("SpawnZ"));
			this.field_7515 = arg.method_10577("SpawnForced");
		}

		this.field_7493.method_7584(arg);
		this.field_7503.method_7249(arg);
		if (arg.method_10573("EnderItems", 9)) {
			this.field_7486.method_7659(arg.method_10554("EnderItems", 10));
		}

		if (arg.method_10573("ShoulderEntityLeft", 10)) {
			this.method_7273(arg.method_10562("ShoulderEntityLeft"));
		}

		if (arg.method_10573("ShoulderEntityRight", 10)) {
			this.method_7345(arg.method_10562("ShoulderEntityRight"));
		}

		if (!this.field_6002.field_9236 && this.field_6002.method_16542() != null && this.method_6088().keySet().contains(class_1294.field_16595)) {
			this.field_6002.method_16542().method_16538(this);
		}
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("DataVersion", class_155.method_16673().getWorldVersion());
		arg.method_10566("Inventory", this.field_7514.method_7384(new class_2499()));
		arg.method_10569("SelectedItemSlot", this.field_7514.field_7545);
		arg.method_10556("Sleeping", this.field_7492);
		arg.method_10575("SleepTimer", (short)this.field_7487);
		arg.method_10548("XpP", this.field_7510);
		arg.method_10569("XpLevel", this.field_7520);
		arg.method_10569("XpTotal", this.field_7495);
		arg.method_10569("XpSeed", this.field_7494);
		arg.method_10569("Score", this.method_7272());
		if (this.field_7501 != null) {
			arg.method_10569("SpawnX", this.field_7501.method_10263());
			arg.method_10569("SpawnY", this.field_7501.method_10264());
			arg.method_10569("SpawnZ", this.field_7501.method_10260());
			arg.method_10556("SpawnForced", this.field_7515);
		}

		this.field_7493.method_7582(arg);
		this.field_7503.method_7251(arg);
		arg.method_10566("EnderItems", this.field_7486.method_7660());
		if (!this.method_7356().isEmpty()) {
			arg.method_10566("ShoulderEntityLeft", this.method_7356());
		}

		if (!this.method_7308().isEmpty()) {
			arg.method_10566("ShoulderEntityRight", this.method_7308());
		}
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		if (this.method_5679(arg)) {
			return false;
		} else if (this.field_7503.field_7480 && !arg.method_5538()) {
			return false;
		} else {
			this.field_6278 = 0;
			if (this.method_6032() <= 0.0F) {
				return false;
			} else {
				if (this.method_6113() && !this.field_6002.field_9236) {
					this.method_7358(true, true, false);
				}

				this.method_7262();
				if (arg.method_5514()) {
					if (this.field_6002.method_8407() == class_1267.field_5801) {
						f = 0.0F;
					}

					if (this.field_6002.method_8407() == class_1267.field_5805) {
						f = Math.min(f / 2.0F + 1.0F, f);
					}

					if (this.field_6002.method_8407() == class_1267.field_5807) {
						f = f * 3.0F / 2.0F;
					}
				}

				return f == 0.0F ? false : super.method_5643(arg, f);
			}
		}
	}

	@Override
	protected void method_6090(class_1309 arg) {
		super.method_6090(arg);
		if (arg.method_6047().method_7909() instanceof class_1743) {
			this.method_7284(true);
		}
	}

	public boolean method_7256(class_1657 arg) {
		class_270 lv = this.method_5781();
		class_270 lv2 = arg.method_5781();
		if (lv == null) {
			return true;
		} else {
			return !lv.method_1206(lv2) ? true : lv.method_1205();
		}
	}

	@Override
	protected void method_6105(float f) {
		this.field_7514.method_7375(f);
	}

	@Override
	protected void method_6056(float f) {
		if (f >= 3.0F && this.field_6277.method_7909() == class_1802.field_8255) {
			int i = 1 + class_3532.method_15375(f);
			this.field_6277.method_7956(i, this);
			if (this.field_6277.method_7960()) {
				class_1268 lv = this.method_6058();
				if (lv == class_1268.field_5808) {
					this.method_5673(class_1304.field_6173, class_1799.field_8037);
				} else {
					this.method_5673(class_1304.field_6171, class_1799.field_8037);
				}

				this.field_6277 = class_1799.field_8037;
				this.method_5783(class_3417.field_15239, 0.8F, 0.8F + this.field_6002.field_9229.nextFloat() * 0.4F);
			}
		}
	}

	public float method_7309() {
		int i = 0;

		for (class_1799 lv : this.field_7514.field_7548) {
			if (!lv.method_7960()) {
				i++;
			}
		}

		return (float)i / (float)this.field_7514.field_7548.size();
	}

	@Override
	protected void method_6074(class_1282 arg, float f) {
		if (!this.method_5679(arg)) {
			f = this.method_6132(arg, f);
			f = this.method_6036(arg, f);
			float var8 = Math.max(f - this.method_6067(), 0.0F);
			this.method_6073(this.method_6067() - (f - var8));
			float h = f - var8;
			if (h > 0.0F && h < 3.4028235E37F) {
				this.method_7339(class_3468.field_15365, Math.round(h * 10.0F));
			}

			if (var8 != 0.0F) {
				this.method_7322(arg.method_5528());
				float i = this.method_6032();
				this.method_6033(this.method_6032() - var8);
				this.method_6066().method_5547(arg, i, var8);
				if (var8 < 3.4028235E37F) {
					this.method_7339(class_3468.field_15388, Math.round(var8 * 10.0F));
				}
			}
		}
	}

	public void method_7311(class_2625 arg) {
	}

	public void method_7257(class_1918 arg) {
	}

	public void method_7323(class_2593 arg) {
	}

	public void method_7303(class_2633 arg) {
	}

	public void method_16354(class_3751 arg) {
	}

	public void method_7291(class_1496 arg, class_1263 arg2) {
	}

	public OptionalInt method_17355(@Nullable class_3908 arg) {
		return OptionalInt.empty();
	}

	public void method_17354(int i, class_1916 arg) {
	}

	public void method_7315(class_1799 arg, class_1268 arg2) {
	}

	public class_1269 method_7287(class_1297 arg, class_1268 arg2) {
		if (this.method_7325()) {
			if (arg instanceof class_3908) {
				this.method_17355((class_3908)arg);
			}

			return class_1269.field_5811;
		} else {
			class_1799 lv = this.method_5998(arg2);
			class_1799 lv2 = lv.method_7960() ? class_1799.field_8037 : lv.method_7972();
			if (arg.method_5688(this, arg2)) {
				if (this.field_7503.field_7477 && lv == this.method_5998(arg2) && lv.method_7947() < lv2.method_7947()) {
					lv.method_7939(lv2.method_7947());
				}

				return class_1269.field_5812;
			} else {
				if (!lv.method_7960() && arg instanceof class_1309) {
					if (this.field_7503.field_7477) {
						lv = lv2;
					}

					if (lv.method_7920(this, (class_1309)arg, arg2)) {
						if (lv.method_7960() && !this.field_7503.field_7477) {
							this.method_6122(arg2, class_1799.field_8037);
						}

						return class_1269.field_5812;
					}
				}

				return class_1269.field_5811;
			}
		}
	}

	@Override
	public double method_5678() {
		return -0.35;
	}

	@Override
	public void method_5848() {
		super.method_5848();
		this.field_5951 = 0;
	}

	public void method_7324(class_1297 arg) {
		if (arg.method_5732()) {
			if (!arg.method_5698(this)) {
				float f = (float)this.method_5996(class_1612.field_7363).method_6194();
				float g;
				if (arg instanceof class_1309) {
					g = class_1890.method_8218(this.method_6047(), ((class_1309)arg).method_6046());
				} else {
					g = class_1890.method_8218(this.method_6047(), class_1310.field_6290);
				}

				float h = this.method_7261(0.5F);
				f *= 0.2F + h * h * 0.8F;
				g *= h;
				this.method_7350();
				if (f > 0.0F || g > 0.0F) {
					boolean bl = h > 0.9F;
					boolean bl2 = false;
					int i = 0;
					i += class_1890.method_8205(this);
					if (this.method_5624() && bl) {
						this.field_6002.method_8465(null, this.field_5987, this.field_6010, this.field_6035, class_3417.field_14999, this.method_5634(), 1.0F, 1.0F);
						i++;
						bl2 = true;
					}

					boolean bl3 = bl
						&& this.field_6017 > 0.0F
						&& !this.field_5952
						&& !this.method_6101()
						&& !this.method_5799()
						&& !this.method_6059(class_1294.field_5919)
						&& !this.method_5765()
						&& arg instanceof class_1309;
					bl3 = bl3 && !this.method_5624();
					if (bl3) {
						f *= 1.5F;
					}

					f += g;
					boolean bl4 = false;
					double d = (double)(this.field_5973 - this.field_6039);
					if (bl && !bl3 && !bl2 && this.field_5952 && d < (double)this.method_6029()) {
						class_1799 lv = this.method_5998(class_1268.field_5808);
						if (lv.method_7909() instanceof class_1829) {
							bl4 = true;
						}
					}

					float j = 0.0F;
					boolean bl5 = false;
					int k = class_1890.method_8199(this);
					if (arg instanceof class_1309) {
						j = ((class_1309)arg).method_6032();
						if (k > 0 && !arg.method_5809()) {
							bl5 = true;
							arg.method_5639(1);
						}
					}

					double e = arg.field_5967;
					double l = arg.field_5984;
					double m = arg.field_6006;
					boolean bl6 = arg.method_5643(class_1282.method_5532(this), f);
					if (bl6) {
						if (i > 0) {
							if (arg instanceof class_1309) {
								((class_1309)arg)
									.method_6005(
										this,
										(float)i * 0.5F,
										(double)class_3532.method_15374(this.field_6031 * (float) (Math.PI / 180.0)),
										(double)(-class_3532.method_15362(this.field_6031 * (float) (Math.PI / 180.0)))
									);
							} else {
								arg.method_5762(
									(double)(-class_3532.method_15374(this.field_6031 * (float) (Math.PI / 180.0)) * (float)i * 0.5F),
									0.1,
									(double)(class_3532.method_15362(this.field_6031 * (float) (Math.PI / 180.0)) * (float)i * 0.5F)
								);
							}

							this.field_5967 *= 0.6;
							this.field_6006 *= 0.6;
							this.method_5728(false);
						}

						if (bl4) {
							float n = 1.0F + class_1890.method_8217(this) * f;

							for (class_1309 lv2 : this.field_6002.method_8403(class_1309.class, arg.method_5829().method_1009(1.0, 0.25, 1.0))) {
								if (lv2 != this
									&& lv2 != arg
									&& !this.method_5722(lv2)
									&& (!(lv2 instanceof class_1531) || !((class_1531)lv2).method_6912())
									&& this.method_5858(lv2) < 9.0) {
									lv2.method_6005(
										this,
										0.4F,
										(double)class_3532.method_15374(this.field_6031 * (float) (Math.PI / 180.0)),
										(double)(-class_3532.method_15362(this.field_6031 * (float) (Math.PI / 180.0)))
									);
									lv2.method_5643(class_1282.method_5532(this), n);
								}
							}

							this.field_6002.method_8465(null, this.field_5987, this.field_6010, this.field_6035, class_3417.field_14706, this.method_5634(), 1.0F, 1.0F);
							this.method_7263();
						}

						if (arg instanceof class_3222 && arg.field_6037) {
							((class_3222)arg).field_13987.method_14364(new class_2743(arg));
							arg.field_6037 = false;
							arg.field_5967 = e;
							arg.field_5984 = l;
							arg.field_6006 = m;
						}

						if (bl3) {
							this.field_6002.method_8465(null, this.field_5987, this.field_6010, this.field_6035, class_3417.field_15016, this.method_5634(), 1.0F, 1.0F);
							this.method_7277(arg);
						}

						if (!bl3 && !bl4) {
							if (bl) {
								this.field_6002.method_8465(null, this.field_5987, this.field_6010, this.field_6035, class_3417.field_14840, this.method_5634(), 1.0F, 1.0F);
							} else {
								this.field_6002.method_8465(null, this.field_5987, this.field_6010, this.field_6035, class_3417.field_14625, this.method_5634(), 1.0F, 1.0F);
							}
						}

						if (g > 0.0F) {
							this.method_7304(arg);
						}

						this.method_6114(arg);
						if (arg instanceof class_1309) {
							class_1890.method_8210((class_1309)arg, this);
						}

						class_1890.method_8213(this, arg);
						class_1799 lv3 = this.method_6047();
						class_1297 lv4 = arg;
						if (arg instanceof class_1508) {
							class_1509 lv5 = ((class_1508)arg).field_7007;
							if (lv5 instanceof class_1309) {
								lv4 = (class_1309)lv5;
							}
						}

						if (!lv3.method_7960() && lv4 instanceof class_1309) {
							lv3.method_7979((class_1309)lv4, this);
							if (lv3.method_7960()) {
								this.method_6122(class_1268.field_5808, class_1799.field_8037);
							}
						}

						if (arg instanceof class_1309) {
							float o = j - ((class_1309)arg).method_6032();
							this.method_7339(class_3468.field_15399, Math.round(o * 10.0F));
							if (k > 0) {
								arg.method_5639(k * 4);
							}

							if (this.field_6002 instanceof class_3218 && o > 2.0F) {
								int p = (int)((double)o * 0.5);
								((class_3218)this.field_6002)
									.method_14199(class_2398.field_11209, arg.field_5987, arg.field_6010 + (double)(arg.field_6019 * 0.5F), arg.field_6035, p, 0.1, 0.0, 0.1, 0.2);
							}
						}

						this.method_7322(0.1F);
					} else {
						this.field_6002.method_8465(null, this.field_5987, this.field_6010, this.field_6035, class_3417.field_14914, this.method_5634(), 1.0F, 1.0F);
						if (bl5) {
							arg.method_5646();
						}
					}
				}
			}
		}
	}

	@Override
	protected void method_5997(class_1309 arg) {
		this.method_7324(arg);
	}

	public void method_7284(boolean bl) {
		float f = 0.25F + (float)class_1890.method_8234(this) * 0.05F;
		if (bl) {
			f += 0.75F;
		}

		if (this.field_5974.nextFloat() < f) {
			this.method_7357().method_7906(class_1802.field_8255, 100);
			this.method_6021();
			this.field_6002.method_8421(this, (byte)30);
		}
	}

	public void method_7277(class_1297 arg) {
	}

	public void method_7304(class_1297 arg) {
	}

	public void method_7263() {
		double d = (double)(-class_3532.method_15374(this.field_6031 * (float) (Math.PI / 180.0)));
		double e = (double)class_3532.method_15362(this.field_6031 * (float) (Math.PI / 180.0));
		if (this.field_6002 instanceof class_3218) {
			((class_3218)this.field_6002)
				.method_14199(class_2398.field_11227, this.field_5987 + d, this.field_6010 + (double)this.field_6019 * 0.5, this.field_6035 + e, 0, d, 0.0, e, 0.0);
		}
	}

	@Environment(EnvType.CLIENT)
	public void method_7331() {
	}

	@Override
	public void method_5650() {
		super.method_5650();
		this.field_7498.method_7595(this);
		if (this.field_7512 != null) {
			this.field_7512.method_7595(this);
		}
	}

	@Override
	public boolean method_5757() {
		return !this.field_7492 && super.method_5757();
	}

	public boolean method_7340() {
		return false;
	}

	public GameProfile method_7334() {
		return this.field_7507;
	}

	public class_1657.class_1658 method_7269(class_2338 arg) {
		class_2350 lv = this.field_6002.method_8320(arg).method_11654(class_2383.field_11177);
		if (!this.field_6002.field_9236) {
			if (this.method_6113() || !this.method_5805()) {
				return class_1657.class_1658.field_7531;
			}

			if (!this.field_6002.field_9247.method_12462()) {
				return class_1657.class_1658.field_7528;
			}

			if (this.field_6002.method_8530()) {
				return class_1657.class_1658.field_7529;
			}

			if (!this.method_7264(arg, lv)) {
				return class_1657.class_1658.field_7530;
			}

			if (!this.method_7337()) {
				double d = 8.0;
				double e = 5.0;
				List<class_1588> list = this.field_6002
					.method_8390(
						class_1588.class,
						new class_238(
							(double)arg.method_10263() - 8.0,
							(double)arg.method_10264() - 5.0,
							(double)arg.method_10260() - 8.0,
							(double)arg.method_10263() + 8.0,
							(double)arg.method_10264() + 5.0,
							(double)arg.method_10260() + 8.0
						),
						new class_1657.class_1660(this)
					);
				if (!list.isEmpty()) {
					return class_1657.class_1658.field_7532;
				}
			}
		}

		if (this.method_5765()) {
			this.method_5848();
		}

		this.method_7262();
		this.method_7266(class_3468.field_15419.method_14956(class_3468.field_15429));
		this.method_5835(0.2F, 0.2F);
		if (this.field_6002.method_8591(arg)) {
			float f = 0.5F + (float)lv.method_10148() * 0.4F;
			float g = 0.5F + (float)lv.method_10165() * 0.4F;
			this.method_7312(lv);
			this.method_5814((double)((float)arg.method_10263() + f), (double)((float)arg.method_10264() + 0.6875F), (double)((float)arg.method_10260() + g));
		} else {
			this.method_5814((double)((float)arg.method_10263() + 0.5F), (double)((float)arg.method_10264() + 0.6875F), (double)((float)arg.method_10260() + 0.5F));
		}

		this.field_7492 = true;
		this.field_7487 = 0;
		this.field_7519 = arg;
		this.field_5967 = 0.0;
		this.field_5984 = 0.0;
		this.field_6006 = 0.0;
		if (!this.field_6002.field_9236) {
			this.field_6002.method_8448();
		}

		return class_1657.class_1658.field_7527;
	}

	private boolean method_7264(class_2338 arg, class_2350 arg2) {
		if (Math.abs(this.field_5987 - (double)arg.method_10263()) <= 3.0
			&& Math.abs(this.field_6010 - (double)arg.method_10264()) <= 2.0
			&& Math.abs(this.field_6035 - (double)arg.method_10260()) <= 3.0) {
			return true;
		} else {
			class_2338 lv = arg.method_10093(arg2.method_10153());
			return Math.abs(this.field_5987 - (double)lv.method_10263()) <= 3.0
				&& Math.abs(this.field_6010 - (double)lv.method_10264()) <= 2.0
				&& Math.abs(this.field_6035 - (double)lv.method_10260()) <= 3.0;
		}
	}

	private void method_7312(class_2350 arg) {
		this.field_7516 = -1.8F * (float)arg.method_10148();
		this.field_7497 = -1.8F * (float)arg.method_10165();
	}

	public void method_7358(boolean bl, boolean bl2, boolean bl3) {
		this.method_5835(0.6F, 1.8F);
		class_2680 lv = this.field_6002.method_8320(this.field_7519);
		if (this.field_7519 != null && lv.method_11614() instanceof class_2244) {
			this.field_6002.method_8652(this.field_7519, lv.method_11657(class_2244.field_9968, Boolean.valueOf(false)), 4);
			class_2338 lv2 = class_2244.method_9484(this.field_6002, this.field_7519, 0);
			if (lv2 == null) {
				lv2 = this.field_7519.method_10084();
			}

			this.method_5814((double)((float)lv2.method_10263() + 0.5F), (double)((float)lv2.method_10264() + 0.1F), (double)((float)lv2.method_10260() + 0.5F));
		}

		this.field_7492 = false;
		if (!this.field_6002.field_9236 && bl2) {
			this.field_6002.method_8448();
		}

		this.field_7487 = bl ? 0 : 100;
		if (bl3) {
			this.method_7289(this.field_7519, false);
		}
	}

	private boolean method_7275() {
		return this.field_6002.method_8320(this.field_7519).method_11614() instanceof class_2244;
	}

	@Nullable
	public static class_2338 method_7288(class_1922 arg, class_2338 arg2, boolean bl) {
		class_2248 lv = arg.method_8320(arg2).method_11614();
		if (!(lv instanceof class_2244)) {
			if (!bl) {
				return null;
			} else {
				boolean bl2 = lv.method_9538();
				boolean bl3 = arg.method_8320(arg2.method_10084()).method_11614().method_9538();
				return bl2 && bl3 ? arg2 : null;
			}
		} else {
			return class_2244.method_9484(arg, arg2, 0);
		}
	}

	@Environment(EnvType.CLIENT)
	public float method_7319() {
		if (this.field_7519 != null) {
			class_2350 lv = this.field_6002.method_8320(this.field_7519).method_11654(class_2383.field_11177);
			switch (lv) {
				case field_11035:
					return 90.0F;
				case field_11039:
					return 0.0F;
				case field_11043:
					return 270.0F;
				case field_11034:
					return 180.0F;
			}
		}

		return 0.0F;
	}

	@Override
	public boolean method_6113() {
		return this.field_7492;
	}

	public boolean method_7276() {
		return this.field_7492 && this.field_7487 >= 100;
	}

	public int method_7297() {
		return this.field_7487;
	}

	public void method_7353(class_2561 arg, boolean bl) {
	}

	public class_2338 method_7280() {
		return this.field_7501;
	}

	public boolean method_7258() {
		return this.field_7515;
	}

	public void method_7289(class_2338 arg, boolean bl) {
		if (arg != null) {
			this.field_7501 = arg;
			this.field_7515 = bl;
		} else {
			this.field_7501 = null;
			this.field_7515 = false;
		}
	}

	public void method_7281(class_2960 arg) {
		this.method_7259(class_3468.field_15419.method_14956(arg));
	}

	public void method_7339(class_2960 arg, int i) {
		this.method_7342(class_3468.field_15419.method_14956(arg), i);
	}

	public void method_7259(class_3445<?> arg) {
		this.method_7342(arg, 1);
	}

	public void method_7342(class_3445<?> arg, int i) {
	}

	public void method_7266(class_3445<?> arg) {
	}

	public int method_7254(Collection<class_1860> collection) {
		return 0;
	}

	public void method_7335(class_2960[] args) {
	}

	public int method_7333(Collection<class_1860> collection) {
		return 0;
	}

	@Override
	public void method_6043() {
		super.method_6043();
		this.method_7281(class_3468.field_15428);
		if (this.method_5624()) {
			this.method_7322(0.2F);
		} else {
			this.method_7322(0.05F);
		}
	}

	@Override
	public void method_6091(float f, float g, float h) {
		double d = this.field_5987;
		double e = this.field_6010;
		double i = this.field_6035;
		if (this.method_5681() && !this.method_5765()) {
			double j = this.method_5720().field_1351;
			double k = j < -0.2 ? 0.085 : 0.06;
			if (j <= 0.0
				|| this.field_6282
				|| !this.field_6002.method_8320(new class_2338(this.field_5987, this.field_6010 + 1.0 - 0.1, this.field_6035)).method_11618().method_15769()) {
				this.field_5984 = this.field_5984 + (j - this.field_5984) * k;
			}
		}

		if (this.field_7503.field_7479 && !this.method_5765()) {
			double j = this.field_5984;
			float l = this.field_6281;
			this.field_6281 = this.field_7503.method_7252() * (float)(this.method_5624() ? 2 : 1);
			super.method_6091(f, g, h);
			this.field_5984 = j * 0.6;
			this.field_6281 = l;
			this.field_6017 = 0.0F;
			this.method_5729(7, false);
		} else {
			super.method_6091(f, g, h);
		}

		this.method_7282(this.field_5987 - d, this.field_6010 - e, this.field_6035 - i);
	}

	@Override
	public void method_5790() {
		if (this.field_7503.field_7479) {
			this.method_5796(false);
		} else {
			super.method_5790();
		}
	}

	@Environment(EnvType.CLIENT)
	protected boolean method_7352(class_2338 arg) {
		class_2338 lv = arg.method_10084();
		return this.method_7326(arg) && !this.field_6002.method_8320(lv).method_11621(this.field_6002, lv);
	}

	@Environment(EnvType.CLIENT)
	protected boolean method_7326(class_2338 arg) {
		return !this.field_6002.method_8320(arg).method_11621(this.field_6002, arg);
	}

	@Override
	public float method_6029() {
		return (float)this.method_5996(class_1612.field_7357).method_6194();
	}

	public void method_7282(double d, double e, double f) {
		if (!this.method_5765()) {
			if (this.method_5681()) {
				int i = Math.round(class_3532.method_15368(d * d + e * e + f * f) * 100.0F);
				if (i > 0) {
					this.method_7339(class_3468.field_15423, i);
					this.method_7322(0.01F * (float)i * 0.01F);
				}
			} else if (this.method_5744(class_3486.field_15517, true)) {
				int i = Math.round(class_3532.method_15368(d * d + e * e + f * f) * 100.0F);
				if (i > 0) {
					this.method_7339(class_3468.field_15401, i);
					this.method_7322(0.01F * (float)i * 0.01F);
				}
			} else if (this.method_5799()) {
				int i = Math.round(class_3532.method_15368(d * d + f * f) * 100.0F);
				if (i > 0) {
					this.method_7339(class_3468.field_15394, i);
					this.method_7322(0.01F * (float)i * 0.01F);
				}
			} else if (this.method_6101()) {
				if (e > 0.0) {
					this.method_7339(class_3468.field_15413, (int)Math.round(e * 100.0));
				}
			} else if (this.field_5952) {
				int i = Math.round(class_3532.method_15368(d * d + f * f) * 100.0F);
				if (i > 0) {
					if (this.method_5624()) {
						this.method_7339(class_3468.field_15364, i);
						this.method_7322(0.1F * (float)i * 0.01F);
					} else if (this.method_5715()) {
						this.method_7339(class_3468.field_15376, i);
						this.method_7322(0.0F * (float)i * 0.01F);
					} else {
						this.method_7339(class_3468.field_15377, i);
						this.method_7322(0.0F * (float)i * 0.01F);
					}
				}
			} else if (this.method_6128()) {
				int i = Math.round(class_3532.method_15368(d * d + e * e + f * f) * 100.0F);
				this.method_7339(class_3468.field_15374, i);
			} else {
				int i = Math.round(class_3532.method_15368(d * d + f * f) * 100.0F);
				if (i > 25) {
					this.method_7339(class_3468.field_15426, i);
				}
			}
		}
	}

	private void method_7260(double d, double e, double f) {
		if (this.method_5765()) {
			int i = Math.round(class_3532.method_15368(d * d + e * e + f * f) * 100.0F);
			if (i > 0) {
				if (this.method_5854() instanceof class_1688) {
					this.method_7339(class_3468.field_15409, i);
				} else if (this.method_5854() instanceof class_1690) {
					this.method_7339(class_3468.field_15415, i);
				} else if (this.method_5854() instanceof class_1452) {
					this.method_7339(class_3468.field_15387, i);
				} else if (this.method_5854() instanceof class_1496) {
					this.method_7339(class_3468.field_15396, i);
				}
			}
		}
	}

	@Override
	public void method_5747(float f, float g) {
		if (!this.field_7503.field_7478) {
			if (f >= 2.0F) {
				this.method_7339(class_3468.field_15386, (int)Math.round((double)f * 100.0));
			}

			super.method_5747(f, g);
		}
	}

	@Override
	protected void method_5746() {
		if (!this.method_7325()) {
			super.method_5746();
		}
	}

	@Override
	protected class_3414 method_6041(int i) {
		return i > 4 ? class_3417.field_14794 : class_3417.field_14778;
	}

	@Override
	public void method_5874(class_1309 arg) {
		this.method_7259(class_3468.field_15403.method_14956(arg.method_5864()));
	}

	@Override
	public void method_5844(class_2680 arg, class_243 arg2) {
		if (!this.field_7503.field_7479) {
			super.method_5844(arg, arg2);
		}
	}

	public void method_7255(int i) {
		this.method_7285(i);
		this.field_7510 = this.field_7510 + (float)i / (float)this.method_7349();
		this.field_7495 = class_3532.method_15340(this.field_7495 + i, 0, Integer.MAX_VALUE);

		while (this.field_7510 < 0.0F) {
			float f = this.field_7510 * (float)this.method_7349();
			if (this.field_7520 > 0) {
				this.method_7316(-1);
				this.field_7510 = 1.0F + f / (float)this.method_7349();
			} else {
				this.method_7316(-1);
				this.field_7510 = 0.0F;
			}
		}

		while (this.field_7510 >= 1.0F) {
			this.field_7510 = (this.field_7510 - 1.0F) * (float)this.method_7349();
			this.method_7316(1);
			this.field_7510 = this.field_7510 / (float)this.method_7349();
		}
	}

	public int method_7278() {
		return this.field_7494;
	}

	public void method_7286(class_1799 arg, int i) {
		this.field_7520 -= i;
		if (this.field_7520 < 0) {
			this.field_7520 = 0;
			this.field_7510 = 0.0F;
			this.field_7495 = 0;
		}

		this.field_7494 = this.field_5974.nextInt();
	}

	public void method_7316(int i) {
		this.field_7520 += i;
		if (this.field_7520 < 0) {
			this.field_7520 = 0;
			this.field_7510 = 0.0F;
			this.field_7495 = 0;
		}

		if (i > 0 && this.field_7520 % 5 == 0 && (float)this.field_7508 < (float)this.field_6012 - 100.0F) {
			float f = this.field_7520 > 30 ? 1.0F : (float)this.field_7520 / 30.0F;
			this.field_6002.method_8465(null, this.field_5987, this.field_6010, this.field_6035, class_3417.field_14709, this.method_5634(), f * 0.75F, 1.0F);
			this.field_7508 = this.field_6012;
		}
	}

	public int method_7349() {
		if (this.field_7520 >= 30) {
			return 112 + (this.field_7520 - 30) * 9;
		} else {
			return this.field_7520 >= 15 ? 37 + (this.field_7520 - 15) * 5 : 7 + this.field_7520 * 2;
		}
	}

	public void method_7322(float f) {
		if (!this.field_7503.field_7480) {
			if (!this.field_6002.field_9236) {
				this.field_7493.method_7583(f);
			}
		}
	}

	public class_1702 method_7344() {
		return this.field_7493;
	}

	public boolean method_7332(boolean bl) {
		return !this.field_7503.field_7480 && (bl || this.field_7493.method_7587());
	}

	public boolean method_7317() {
		return this.method_6032() > 0.0F && this.method_6032() < this.method_6063();
	}

	public boolean method_7294() {
		return this.field_7503.field_7476;
	}

	public boolean method_7343(class_2338 arg, class_2350 arg2, class_1799 arg3) {
		if (this.field_7503.field_7476) {
			return true;
		} else {
			class_2338 lv = arg.method_10093(arg2.method_10153());
			class_2694 lv2 = new class_2694(this.field_6002, lv, false);
			return arg3.method_7944(this.field_6002.method_8514(), lv2);
		}
	}

	@Override
	protected int method_6110(class_1657 arg) {
		if (!this.field_6002.method_8450().method_8355("keepInventory") && !this.method_7325()) {
			int i = this.field_7520 * 7;
			return i > 100 ? 100 : i;
		} else {
			return 0;
		}
	}

	@Override
	protected boolean method_6071() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_5733() {
		return true;
	}

	@Override
	protected boolean method_5658() {
		return !this.field_7503.field_7479;
	}

	public void method_7355() {
	}

	public void method_7336(class_1934 arg) {
	}

	@Override
	public class_2561 method_5477() {
		return new class_2585(this.field_7507.getName());
	}

	public class_1730 method_7274() {
		return this.field_7486;
	}

	@Override
	public class_1799 method_6118(class_1304 arg) {
		if (arg == class_1304.field_6173) {
			return this.field_7514.method_7391();
		} else if (arg == class_1304.field_6171) {
			return this.field_7514.field_7544.get(0);
		} else {
			return arg.method_5925() == class_1304.class_1305.field_6178 ? this.field_7514.field_7548.get(arg.method_5927()) : class_1799.field_8037;
		}
	}

	@Override
	public void method_5673(class_1304 arg, class_1799 arg2) {
		if (arg == class_1304.field_6173) {
			this.method_6116(arg2);
			this.field_7514.field_7547.set(this.field_7514.field_7545, arg2);
		} else if (arg == class_1304.field_6171) {
			this.method_6116(arg2);
			this.field_7514.field_7544.set(0, arg2);
		} else if (arg.method_5925() == class_1304.class_1305.field_6178) {
			this.method_6116(arg2);
			this.field_7514.field_7548.set(arg.method_5927(), arg2);
		}
	}

	public boolean method_7270(class_1799 arg) {
		this.method_6116(arg);
		return this.field_7514.method_7394(arg);
	}

	@Override
	public Iterable<class_1799> method_5877() {
		return Lists.<class_1799>newArrayList(this.method_6047(), this.method_6079());
	}

	@Override
	public Iterable<class_1799> method_5661() {
		return this.field_7514.field_7548;
	}

	public boolean method_7298(class_2487 arg) {
		if (this.method_5765() || !this.field_5952 || this.method_5799()) {
			return false;
		} else if (this.method_7356().isEmpty()) {
			this.method_7273(arg);
			return true;
		} else if (this.method_7308().isEmpty()) {
			this.method_7345(arg);
			return true;
		} else {
			return false;
		}
	}

	protected void method_7262() {
		this.method_7296(this.method_7356());
		this.method_7273(new class_2487());
		this.method_7296(this.method_7308());
		this.method_7345(new class_2487());
	}

	private void method_7296(@Nullable class_2487 arg) {
		if (!this.field_6002.field_9236 && !arg.isEmpty()) {
			class_1297 lv = class_1299.method_5892(arg, this.field_6002);
			if (lv instanceof class_1321) {
				((class_1321)lv).method_6174(this.field_6021);
			}

			lv.method_5814(this.field_5987, this.field_6010 + 0.7F, this.field_6035);
			this.field_6002.method_8649(lv);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_5756(class_1657 arg) {
		if (!this.method_5767()) {
			return false;
		} else if (arg.method_7325()) {
			return false;
		} else {
			class_270 lv = this.method_5781();
			return lv == null || arg == null || arg.method_5781() != lv || !lv.method_1199();
		}
	}

	public abstract boolean method_7325();

	@Override
	public boolean method_5681() {
		return !this.field_7503.field_7479 && !this.method_7325() && super.method_5681();
	}

	public abstract boolean method_7337();

	@Override
	public boolean method_5675() {
		return !this.field_7503.field_7479;
	}

	public class_269 method_7327() {
		return this.field_6002.method_8428();
	}

	@Override
	public class_2561 method_5476() {
		class_2561 lv = class_268.method_1142(this.method_5781(), this.method_5477());
		return this.method_7299(lv);
	}

	public class_2561 method_7306() {
		return new class_2585("").method_10852(this.method_5477()).method_10864(" (").method_10864(this.field_7507.getId().toString()).method_10864(")");
	}

	private class_2561 method_7299(class_2561 arg) {
		String string = this.method_7334().getName();
		return arg.method_10859(
			argx -> argx.method_10958(new class_2558(class_2558.class_2559.field_11745, "/tell " + string + " ")).method_10949(this.method_5769()).method_10975(string)
		);
	}

	@Override
	public String method_5820() {
		return this.method_7334().getName();
	}

	@Override
	public float method_5751() {
		float f = 1.62F;
		if (this.method_6113()) {
			f = 0.2F;
		} else if (this.method_5681() || this.method_6128() || this.field_6019 == 0.6F) {
			f = 0.4F;
		} else if (this.method_5715() || this.field_6019 == 1.65F) {
			f -= 0.08F;
		}

		return f;
	}

	@Override
	public void method_6073(float f) {
		if (f < 0.0F) {
			f = 0.0F;
		}

		this.method_5841().method_12778(field_7491, f);
	}

	@Override
	public float method_6067() {
		return this.method_5841().method_12789(field_7491);
	}

	public static UUID method_7271(GameProfile gameProfile) {
		UUID uUID = gameProfile.getId();
		if (uUID == null) {
			uUID = method_7310(gameProfile.getName());
		}

		return uUID;
	}

	public static UUID method_7310(String string) {
		return UUID.nameUUIDFromBytes(("OfflinePlayer:" + string).getBytes(StandardCharsets.UTF_8));
	}

	@Environment(EnvType.CLIENT)
	public boolean method_7348(class_1664 arg) {
		return (this.method_5841().method_12789(field_7518) & arg.method_7430()) == arg.method_7430();
	}

	@Override
	public boolean method_5758(int i, class_1799 arg) {
		if (i >= 0 && i < this.field_7514.field_7547.size()) {
			this.field_7514.method_5447(i, arg);
			return true;
		} else {
			class_1304 lv;
			if (i == 100 + class_1304.field_6169.method_5927()) {
				lv = class_1304.field_6169;
			} else if (i == 100 + class_1304.field_6174.method_5927()) {
				lv = class_1304.field_6174;
			} else if (i == 100 + class_1304.field_6172.method_5927()) {
				lv = class_1304.field_6172;
			} else if (i == 100 + class_1304.field_6166.method_5927()) {
				lv = class_1304.field_6166;
			} else {
				lv = null;
			}

			if (i == 98) {
				this.method_5673(class_1304.field_6173, arg);
				return true;
			} else if (i == 99) {
				this.method_5673(class_1304.field_6171, arg);
				return true;
			} else if (lv == null) {
				int j = i - 200;
				if (j >= 0 && j < this.field_7486.method_5439()) {
					this.field_7486.method_5447(j, arg);
					return true;
				} else {
					return false;
				}
			} else {
				if (!arg.method_7960()) {
					if (!(arg.method_7909() instanceof class_1738) && !(arg.method_7909() instanceof class_1770)) {
						if (lv != class_1304.field_6169) {
							return false;
						}
					} else if (class_1308.method_5953(arg) != lv) {
						return false;
					}
				}

				this.field_7514.method_5447(lv.method_5927() + this.field_7514.field_7547.size(), arg);
				return true;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public boolean method_7302() {
		return this.field_7523;
	}

	@Environment(EnvType.CLIENT)
	public void method_7268(boolean bl) {
		this.field_7523 = bl;
	}

	@Override
	public class_1306 method_6068() {
		return this.field_6011.method_12789(field_7488) == 0 ? class_1306.field_6182 : class_1306.field_6183;
	}

	public void method_7283(class_1306 arg) {
		this.field_6011.method_12778(field_7488, (byte)(arg == class_1306.field_6182 ? 0 : 1));
	}

	public class_2487 method_7356() {
		return this.field_6011.method_12789(field_7496);
	}

	protected void method_7273(class_2487 arg) {
		this.field_6011.method_12778(field_7496, arg);
	}

	public class_2487 method_7308() {
		return this.field_6011.method_12789(field_7506);
	}

	protected void method_7345(class_2487 arg) {
		this.field_6011.method_12778(field_7506, arg);
	}

	public float method_7279() {
		return (float)(1.0 / this.method_5996(class_1612.field_7356).method_6194() * 20.0);
	}

	public float method_7261(float f) {
		return class_3532.method_15363(((float)this.field_6273 + f) / this.method_7279(), 0.0F, 1.0F);
	}

	public void method_7350() {
		this.field_6273 = 0;
	}

	public class_1796 method_7357() {
		return this.field_7484;
	}

	@Override
	public void method_5697(class_1297 arg) {
		if (!this.method_6113()) {
			super.method_5697(arg);
		}
	}

	public float method_7292() {
		return (float)this.method_5996(class_1612.field_7362).method_6194();
	}

	public boolean method_7338() {
		return this.field_7503.field_7477 && this.method_5691() >= 2;
	}

	@Override
	protected void method_6020(class_1293 arg) {
		super.method_6020(arg);
		if (!this.field_6002.field_9236 && this.field_6002.method_16542() != null && arg.method_5579() == class_1294.field_16595) {
			this.field_6002.method_16542().method_16538(this);
		}
	}

	@Override
	protected void method_6129(class_1293 arg) {
		super.method_6129(arg);
		if (!this.field_6002.field_9236 && this.field_6002.method_16542() != null && arg.method_5579() == class_1294.field_16595) {
			this.field_6002.method_16542().method_16536(this);
		}
	}

	public static enum class_1658 {
		field_7527,
		field_7528,
		field_7529,
		field_7530,
		field_7531,
		field_7532;
	}

	public static enum class_1659 {
		field_7538(0, "options.chat.visibility.full"),
		field_7539(1, "options.chat.visibility.system"),
		field_7536(2, "options.chat.visibility.hidden");

		private static final class_1657.class_1659[] field_7534 = (class_1657.class_1659[])Arrays.stream(values())
			.sorted(Comparator.comparingInt(class_1657.class_1659::method_7362))
			.toArray(class_1657.class_1659[]::new);
		private final int field_7535;
		private final String field_7540;

		private class_1659(int j, String string2) {
			this.field_7535 = j;
			this.field_7540 = string2;
		}

		public int method_7362() {
			return this.field_7535;
		}

		@Environment(EnvType.CLIENT)
		public static class_1657.class_1659 method_7360(int i) {
			return field_7534[i % field_7534.length];
		}

		@Environment(EnvType.CLIENT)
		public String method_7359() {
			return this.field_7540;
		}
	}

	static class class_1660 implements Predicate<class_1588> {
		private final class_1657 field_7541;

		private class_1660(class_1657 arg) {
			this.field_7541 = arg;
		}

		public boolean method_7363(@Nullable class_1588 arg) {
			return arg.method_7076(this.field_7541);
		}
	}
}
