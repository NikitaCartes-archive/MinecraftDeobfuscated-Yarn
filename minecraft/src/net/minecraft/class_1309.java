package net.minecraft;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_1309 extends class_1297 {
	private static final Logger field_6216 = LogManager.getLogger();
	private static final UUID field_6237 = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
	private static final class_1322 field_6231 = new class_1322(field_6237, "Sprinting speed boost", 0.3F, class_1322.class_1323.field_6331).method_6187(false);
	protected static final class_2940<Byte> field_6257 = class_2945.method_12791(class_1309.class, class_2943.field_13319);
	private static final class_2940<Float> field_6247 = class_2945.method_12791(class_1309.class, class_2943.field_13320);
	private static final class_2940<Integer> field_6240 = class_2945.method_12791(class_1309.class, class_2943.field_13327);
	private static final class_2940<Boolean> field_6214 = class_2945.method_12791(class_1309.class, class_2943.field_13323);
	private static final class_2940<Integer> field_6219 = class_2945.method_12791(class_1309.class, class_2943.field_13327);
	private class_1325 field_6260;
	private final class_1283 field_6256 = new class_1283(this);
	private final Map<class_1291, class_1293> field_6280 = Maps.<class_1291, class_1293>newHashMap();
	private final class_2371<class_1799> field_6234 = class_2371.method_10213(2, class_1799.field_8037);
	private final class_2371<class_1799> field_6248 = class_2371.method_10213(4, class_1799.field_8037);
	public boolean field_6252;
	public class_1268 field_6266;
	public int field_6279;
	public int field_6218;
	public int field_6235;
	public int field_6254;
	public float field_6271;
	public int field_6213;
	public float field_6229;
	public float field_6251;
	protected int field_6273;
	public float field_6211;
	public float field_6225;
	public float field_6249;
	public final int field_6269 = 20;
	public float field_6286;
	public float field_6223;
	public final float field_6244;
	public final float field_6262;
	public float field_6283;
	public float field_6220;
	public float field_6241;
	public float field_6259;
	public float field_6281 = 0.02F;
	protected class_1657 field_6258;
	protected int field_6238;
	protected boolean field_6272;
	protected int field_6278;
	protected float field_6217;
	protected float field_6233;
	protected float field_6255;
	protected float field_6275;
	protected float field_6215;
	protected int field_6232;
	protected float field_6253;
	protected boolean field_6282;
	public float field_6212;
	public float field_6227;
	public float field_6250;
	public float field_6267;
	protected int field_6210;
	protected double field_6224;
	protected double field_6245;
	protected double field_6263;
	protected double field_6284;
	protected double field_6221;
	protected double field_6242;
	protected int field_6265;
	private boolean field_6285 = true;
	private class_1309 field_6274;
	private int field_6230;
	private class_1309 field_6236;
	private int field_6270;
	private float field_6287;
	private int field_6228;
	private float field_6246;
	protected class_1799 field_6277 = class_1799.field_8037;
	protected int field_6222;
	protected int field_6239;
	private class_2338 field_6268;
	private class_1282 field_6276;
	private long field_6226;
	protected int field_6261;
	private float field_6243;
	private float field_6264;

	protected class_1309(class_1299<?> arg, class_1937 arg2) {
		super(arg, arg2);
		this.method_6001();
		this.method_6033(this.method_6063());
		this.field_6033 = true;
		this.field_6262 = (float)((Math.random() + 1.0) * 0.01F);
		this.method_5814(this.field_5987, this.field_6010, this.field_6035);
		this.field_6244 = (float)Math.random() * 12398.0F;
		this.field_6031 = (float)(Math.random() * (float) (Math.PI * 2));
		this.field_6241 = this.field_6031;
		this.field_6013 = 0.6F;
	}

	@Override
	public void method_5768() {
		this.method_5643(class_1282.field_5849, Float.MAX_VALUE);
	}

	@Override
	protected void method_5693() {
		this.field_6011.method_12784(field_6257, (byte)0);
		this.field_6011.method_12784(field_6240, 0);
		this.field_6011.method_12784(field_6214, false);
		this.field_6011.method_12784(field_6219, 0);
		this.field_6011.method_12784(field_6247, 1.0F);
	}

	protected void method_6001() {
		this.method_6127().method_6208(class_1612.field_7359);
		this.method_6127().method_6208(class_1612.field_7360);
		this.method_6127().method_6208(class_1612.field_7357);
		this.method_6127().method_6208(class_1612.field_7358);
		this.method_6127().method_6208(class_1612.field_7364);
	}

	@Override
	protected void method_5623(double d, boolean bl, class_2680 arg, class_2338 arg2) {
		if (!this.method_5799()) {
			this.method_5713();
		}

		if (!this.field_6002.field_9236 && this.field_6017 > 3.0F && bl) {
			float f = (float)class_3532.method_15386(this.field_6017 - 3.0F);
			if (!arg.method_11588()) {
				double e = Math.min((double)(0.2F + f / 15.0F), 2.5);
				int i = (int)(150.0 * e);
				((class_3218)this.field_6002)
					.method_14199(new class_2388(class_2398.field_11217, arg), this.field_5987, this.field_6010, this.field_6035, i, 0.0, 0.0, 0.0, 0.15F);
			}
		}

		super.method_5623(d, bl, arg, arg2);
	}

	public boolean method_6094() {
		return this.method_6046() == class_1310.field_6289;
	}

	@Environment(EnvType.CLIENT)
	public float method_6024(float f) {
		return class_3532.method_16439(f, this.field_6264, this.field_6243);
	}

	@Override
	public void method_5670() {
		this.field_6229 = this.field_6251;
		super.method_5670();
		this.field_6002.method_16107().method_15396("livingEntityBaseTick");
		boolean bl = this instanceof class_1657;
		if (this.method_5805()) {
			if (this.method_5757()) {
				this.method_5643(class_1282.field_5855, 1.0F);
			} else if (bl && !this.field_6002.method_8621().method_11966(this.method_5829())) {
				double d = this.field_6002.method_8621().method_11979(this) + this.field_6002.method_8621().method_11971();
				if (d < 0.0) {
					double e = this.field_6002.method_8621().method_11953();
					if (e > 0.0) {
						this.method_5643(class_1282.field_5855, (float)Math.max(1, class_3532.method_15357(-d * e)));
					}
				}
			}
		}

		if (this.method_5753() || this.field_6002.field_9236) {
			this.method_5646();
		}

		boolean bl2 = bl && ((class_1657)this).field_7503.field_7480;
		if (this.method_5805()) {
			if (this.method_5777(class_3486.field_15517)
				&& this.field_6002.method_8320(new class_2338(this.field_5987, this.field_6010 + (double)this.method_5751(), this.field_6035)).method_11614()
					!= class_2246.field_10422) {
				if (!this.method_6094() && !class_1292.method_5574(this) && !bl2) {
					this.method_5855(this.method_6130(this.method_5669()));
					if (this.method_5669() == -20) {
						this.method_5855(0);

						for (int i = 0; i < 8; i++) {
							float f = this.field_5974.nextFloat() - this.field_5974.nextFloat();
							float g = this.field_5974.nextFloat() - this.field_5974.nextFloat();
							float h = this.field_5974.nextFloat() - this.field_5974.nextFloat();
							this.field_6002
								.method_8406(
									class_2398.field_11247,
									this.field_5987 + (double)f,
									this.field_6010 + (double)g,
									this.field_6035 + (double)h,
									this.field_5967,
									this.field_5984,
									this.field_6006
								);
						}

						this.method_5643(class_1282.field_5859, 2.0F);
					}
				}

				if (!this.field_6002.field_9236 && this.method_5765() && this.method_5854() != null && !this.method_5854().method_5788()) {
					this.method_5848();
				}
			} else if (this.method_5669() < this.method_5748()) {
				this.method_5855(this.method_6064(this.method_5669()));
			}

			if (!this.field_6002.field_9236) {
				class_2338 lv = new class_2338(this);
				if (!Objects.equal(this.field_6268, lv)) {
					this.field_6268 = lv;
					this.method_6126(lv);
				}
			}
		}

		if (this.method_5805() && this.method_5637()) {
			this.method_5646();
		}

		this.field_6286 = this.field_6223;
		if (this.field_6235 > 0) {
			this.field_6235--;
		}

		if (this.field_6008 > 0 && !(this instanceof class_3222)) {
			this.field_6008--;
		}

		if (this.method_6032() <= 0.0F) {
			this.method_6108();
		}

		if (this.field_6238 > 0) {
			this.field_6238--;
		} else {
			this.field_6258 = null;
		}

		if (this.field_6236 != null && !this.field_6236.method_5805()) {
			this.field_6236 = null;
		}

		if (this.field_6274 != null) {
			if (!this.field_6274.method_5805()) {
				this.method_6015(null);
			} else if (this.field_6012 - this.field_6230 > 100) {
				this.method_6015(null);
			}
		}

		this.method_6050();
		this.field_6275 = this.field_6255;
		this.field_6220 = this.field_6283;
		this.field_6259 = this.field_6241;
		this.field_5982 = this.field_6031;
		this.field_6004 = this.field_5965;
		this.field_6002.method_16107().method_15407();
	}

	protected void method_6126(class_2338 arg) {
		int i = class_1890.method_8203(class_1893.field_9122, this);
		if (i > 0) {
			class_1894.method_8236(this, this.field_6002, arg, i);
		}
	}

	public boolean method_6109() {
		return false;
	}

	@Override
	public boolean method_5788() {
		return false;
	}

	protected void method_6108() {
		this.field_6213++;
		if (this.field_6213 == 20) {
			if (!this.field_6002.field_9236
				&& (this.method_6071() || this.field_6238 > 0 && this.method_6054() && this.field_6002.method_8450().method_8355("doMobLoot"))) {
				int i = this.method_6110(this.field_6258);

				while (i > 0) {
					int j = class_1303.method_5918(i);
					i -= j;
					this.field_6002.method_8649(new class_1303(this.field_6002, this.field_5987, this.field_6010, this.field_6035, j));
				}
			}

			this.method_5650();

			for (int i = 0; i < 20; i++) {
				double d = this.field_5974.nextGaussian() * 0.02;
				double e = this.field_5974.nextGaussian() * 0.02;
				double f = this.field_5974.nextGaussian() * 0.02;
				this.field_6002
					.method_8406(
						class_2398.field_11203,
						this.field_5987 + (double)(this.field_5974.nextFloat() * this.field_5998 * 2.0F) - (double)this.field_5998,
						this.field_6010 + (double)(this.field_5974.nextFloat() * this.field_6019),
						this.field_6035 + (double)(this.field_5974.nextFloat() * this.field_5998 * 2.0F) - (double)this.field_5998,
						d,
						e,
						f
					);
			}
		}
	}

	protected boolean method_6054() {
		return !this.method_6109();
	}

	protected int method_6130(int i) {
		int j = class_1890.method_8211(this);
		return j > 0 && this.field_5974.nextInt(j + 1) > 0 ? i : i - 1;
	}

	protected int method_6064(int i) {
		return Math.min(i + 4, this.method_5748());
	}

	protected int method_6110(class_1657 arg) {
		return 0;
	}

	protected boolean method_6071() {
		return false;
	}

	public Random method_6051() {
		return this.field_5974;
	}

	@Nullable
	public class_1309 method_6065() {
		return this.field_6274;
	}

	public int method_6117() {
		return this.field_6230;
	}

	public void method_6015(@Nullable class_1309 arg) {
		this.field_6274 = arg;
		this.field_6230 = this.field_6012;
	}

	public class_1309 method_6052() {
		return this.field_6236;
	}

	public int method_6083() {
		return this.field_6270;
	}

	public void method_6114(class_1297 arg) {
		if (arg instanceof class_1309) {
			this.field_6236 = (class_1309)arg;
		} else {
			this.field_6236 = null;
		}

		this.field_6270 = this.field_6012;
	}

	public int method_6131() {
		return this.field_6278;
	}

	public void method_16826(int i) {
		this.field_6278 = i;
	}

	protected void method_6116(class_1799 arg) {
		if (!arg.method_7960()) {
			class_3414 lv = class_3417.field_14883;
			class_1792 lv2 = arg.method_7909();
			if (lv2 instanceof class_1738) {
				lv = ((class_1738)lv2).method_7686().method_7698();
			} else if (lv2 == class_1802.field_8833) {
				lv = class_3417.field_14966;
			}

			this.method_5783(lv, 1.0F, 1.0F);
		}
	}

	@Override
	public void method_5652(class_2487 arg) {
		arg.method_10548("Health", this.method_6032());
		arg.method_10575("HurtTime", (short)this.field_6235);
		arg.method_10569("HurtByTimestamp", this.field_6230);
		arg.method_10575("DeathTime", (short)this.field_6213);
		arg.method_10548("AbsorptionAmount", this.method_6067());

		for (class_1304 lv : class_1304.values()) {
			class_1799 lv2 = this.method_6118(lv);
			if (!lv2.method_7960()) {
				this.method_6127().method_6209(lv2.method_7926(lv));
			}
		}

		arg.method_10566("Attributes", class_1612.method_7134(this.method_6127()));

		for (class_1304 lvx : class_1304.values()) {
			class_1799 lv2 = this.method_6118(lvx);
			if (!lv2.method_7960()) {
				this.method_6127().method_6210(lv2.method_7926(lvx));
			}
		}

		if (!this.field_6280.isEmpty()) {
			class_2499 lv3 = new class_2499();

			for (class_1293 lv4 : this.field_6280.values()) {
				lv3.method_10606(lv4.method_5582(new class_2487()));
			}

			arg.method_10566("ActiveEffects", lv3);
		}

		arg.method_10556("FallFlying", this.method_6128());
	}

	@Override
	public void method_5749(class_2487 arg) {
		this.method_6073(arg.method_10583("AbsorptionAmount"));
		if (arg.method_10573("Attributes", 9) && this.field_6002 != null && !this.field_6002.field_9236) {
			class_1612.method_7131(this.method_6127(), arg.method_10554("Attributes", 10));
		}

		if (arg.method_10573("ActiveEffects", 9)) {
			class_2499 lv = arg.method_10554("ActiveEffects", 10);

			for (int i = 0; i < lv.size(); i++) {
				class_2487 lv2 = lv.method_10602(i);
				class_1293 lv3 = class_1293.method_5583(lv2);
				if (lv3 != null) {
					this.field_6280.put(lv3.method_5579(), lv3);
				}
			}
		}

		if (arg.method_10573("Health", 99)) {
			this.method_6033(arg.method_10583("Health"));
		}

		this.field_6235 = arg.method_10568("HurtTime");
		this.field_6213 = arg.method_10568("DeathTime");
		this.field_6230 = arg.method_10550("HurtByTimestamp");
		if (arg.method_10573("Team", 8)) {
			String string = arg.method_10558("Team");
			class_268 lv4 = this.field_6002.method_8428().method_1153(string);
			boolean bl = lv4 != null && this.field_6002.method_8428().method_1172(this.method_5845(), lv4);
			if (!bl) {
				field_6216.warn("Unable to add mob to team \"{}\" (that team probably doesn't exist)", string);
			}
		}

		if (arg.method_10577("FallFlying")) {
			this.method_5729(7, true);
		}
	}

	protected void method_6050() {
		Iterator<class_1291> iterator = this.field_6280.keySet().iterator();

		try {
			while (iterator.hasNext()) {
				class_1291 lv = (class_1291)iterator.next();
				class_1293 lv2 = (class_1293)this.field_6280.get(lv);
				if (!lv2.method_5585(this)) {
					if (!this.field_6002.field_9236) {
						iterator.remove();
						this.method_6129(lv2);
					}
				} else if (lv2.method_5584() % 600 == 0) {
					this.method_6009(lv2, false);
				}
			}
		} catch (ConcurrentModificationException var11) {
		}

		if (this.field_6285) {
			if (!this.field_6002.field_9236) {
				this.method_6027();
			}

			this.field_6285 = false;
		}

		int i = this.field_6011.method_12789(field_6240);
		boolean bl = this.field_6011.method_12789(field_6214);
		if (i > 0) {
			boolean bl2;
			if (this.method_5767()) {
				bl2 = this.field_5974.nextInt(15) == 0;
			} else {
				bl2 = this.field_5974.nextBoolean();
			}

			if (bl) {
				bl2 &= this.field_5974.nextInt(5) == 0;
			}

			if (bl2 && i > 0) {
				double d = (double)(i >> 16 & 0xFF) / 255.0;
				double e = (double)(i >> 8 & 0xFF) / 255.0;
				double f = (double)(i >> 0 & 0xFF) / 255.0;
				this.field_6002
					.method_8406(
						bl ? class_2398.field_11225 : class_2398.field_11226,
						this.field_5987 + (this.field_5974.nextDouble() - 0.5) * (double)this.field_5998,
						this.field_6010 + this.field_5974.nextDouble() * (double)this.field_6019,
						this.field_6035 + (this.field_5974.nextDouble() - 0.5) * (double)this.field_5998,
						d,
						e,
						f
					);
			}
		}
	}

	protected void method_6027() {
		if (this.field_6280.isEmpty()) {
			this.method_6069();
			this.method_5648(false);
		} else {
			Collection<class_1293> collection = this.field_6280.values();
			this.field_6011.method_12778(field_6214, method_6089(collection));
			this.field_6011.method_12778(field_6240, class_1844.method_8055(collection));
			this.method_5648(this.method_6059(class_1294.field_5905));
		}
	}

	public static boolean method_6089(Collection<class_1293> collection) {
		for (class_1293 lv : collection) {
			if (!lv.method_5591()) {
				return false;
			}
		}

		return true;
	}

	protected void method_6069() {
		this.field_6011.method_12778(field_6214, false);
		this.field_6011.method_12778(field_6240, 0);
	}

	public boolean method_6012() {
		if (this.field_6002.field_9236) {
			return false;
		} else {
			Iterator<class_1293> iterator = this.field_6280.values().iterator();

			boolean bl;
			for (bl = false; iterator.hasNext(); bl = true) {
				this.method_6129((class_1293)iterator.next());
				iterator.remove();
			}

			return bl;
		}
	}

	public Collection<class_1293> method_6026() {
		return this.field_6280.values();
	}

	public Map<class_1291, class_1293> method_6088() {
		return this.field_6280;
	}

	public boolean method_6059(class_1291 arg) {
		return this.field_6280.containsKey(arg);
	}

	@Nullable
	public class_1293 method_6112(class_1291 arg) {
		return (class_1293)this.field_6280.get(arg);
	}

	public boolean method_6092(class_1293 arg) {
		if (!this.method_6049(arg)) {
			return false;
		} else {
			class_1293 lv = (class_1293)this.field_6280.get(arg.method_5579());
			if (lv == null) {
				this.field_6280.put(arg.method_5579(), arg);
				this.method_6020(arg);
				return true;
			} else if (lv.method_5590(arg)) {
				this.method_6009(lv, true);
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean method_6049(class_1293 arg) {
		if (this.method_6046() == class_1310.field_6289) {
			class_1291 lv = arg.method_5579();
			if (lv == class_1294.field_5924 || lv == class_1294.field_5899) {
				return false;
			}
		}

		return true;
	}

	public boolean method_5999() {
		return this.method_6046() == class_1310.field_6289;
	}

	@Nullable
	public class_1293 method_6111(@Nullable class_1291 arg) {
		return (class_1293)this.field_6280.remove(arg);
	}

	public boolean method_6016(class_1291 arg) {
		class_1293 lv = this.method_6111(arg);
		if (lv != null) {
			this.method_6129(lv);
			return true;
		} else {
			return false;
		}
	}

	protected void method_6020(class_1293 arg) {
		this.field_6285 = true;
		if (!this.field_6002.field_9236) {
			arg.method_5579().method_5555(this, this.method_6127(), arg.method_5578());
		}
	}

	protected void method_6009(class_1293 arg, boolean bl) {
		this.field_6285 = true;
		if (bl && !this.field_6002.field_9236) {
			class_1291 lv = arg.method_5579();
			lv.method_5562(this, this.method_6127(), arg.method_5578());
			lv.method_5555(this, this.method_6127(), arg.method_5578());
		}
	}

	protected void method_6129(class_1293 arg) {
		this.field_6285 = true;
		if (!this.field_6002.field_9236) {
			arg.method_5579().method_5562(this, this.method_6127(), arg.method_5578());
		}
	}

	public void method_6025(float f) {
		float g = this.method_6032();
		if (g > 0.0F) {
			this.method_6033(g + f);
		}
	}

	public float method_6032() {
		return this.field_6011.method_12789(field_6247);
	}

	public void method_6033(float f) {
		this.field_6011.method_12778(field_6247, class_3532.method_15363(f, 0.0F, this.method_6063()));
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		if (this.method_5679(arg)) {
			return false;
		} else if (this.field_6002.field_9236) {
			return false;
		} else if (this.method_6032() <= 0.0F) {
			return false;
		} else if (arg.method_5534() && this.method_6059(class_1294.field_5918)) {
			return false;
		} else {
			this.field_6278 = 0;
			float g = f;
			if ((arg == class_1282.field_5865 || arg == class_1282.field_5847) && !this.method_6118(class_1304.field_6169).method_7960()) {
				this.method_6118(class_1304.field_6169).method_7956((int)(f * 4.0F + this.field_5974.nextFloat() * f * 2.0F), this);
				f *= 0.75F;
			}

			boolean bl = false;
			float h = 0.0F;
			if (f > 0.0F && this.method_6061(arg)) {
				this.method_6056(f);
				h = f;
				f = 0.0F;
				if (!arg.method_5533()) {
					class_1297 lv = arg.method_5526();
					if (lv instanceof class_1309) {
						this.method_6090((class_1309)lv);
					}
				}

				bl = true;
			}

			this.field_6225 = 1.5F;
			boolean bl2 = true;
			if ((float)this.field_6008 > 10.0F) {
				if (f <= this.field_6253) {
					return false;
				}

				this.method_6074(arg, f - this.field_6253);
				this.field_6253 = f;
				bl2 = false;
			} else {
				this.field_6253 = f;
				this.field_6008 = 20;
				this.method_6074(arg, f);
				this.field_6254 = 10;
				this.field_6235 = this.field_6254;
			}

			this.field_6271 = 0.0F;
			class_1297 lv2 = arg.method_5529();
			if (lv2 != null) {
				if (lv2 instanceof class_1309) {
					this.method_6015((class_1309)lv2);
				}

				if (lv2 instanceof class_1657) {
					this.field_6238 = 100;
					this.field_6258 = (class_1657)lv2;
				} else if (lv2 instanceof class_1493) {
					class_1493 lv3 = (class_1493)lv2;
					if (lv3.method_6181()) {
						this.field_6238 = 100;
						this.field_6258 = null;
					}
				}
			}

			if (bl2) {
				if (bl) {
					this.field_6002.method_8421(this, (byte)29);
				} else if (arg instanceof class_1285 && ((class_1285)arg).method_5549()) {
					this.field_6002.method_8421(this, (byte)33);
				} else {
					byte b;
					if (arg == class_1282.field_5859) {
						b = 36;
					} else if (arg.method_5534()) {
						b = 37;
					} else {
						b = 2;
					}

					this.field_6002.method_8421(this, b);
				}

				if (arg != class_1282.field_5859 && (!bl || f > 0.0F)) {
					this.method_5785();
				}

				if (lv2 != null) {
					double d = lv2.field_5987 - this.field_5987;

					double e;
					for (e = lv2.field_6035 - this.field_6035; d * d + e * e < 1.0E-4; e = (Math.random() - Math.random()) * 0.01) {
						d = (Math.random() - Math.random()) * 0.01;
					}

					this.field_6271 = (float)(class_3532.method_15349(e, d) * 180.0F / (float)Math.PI - (double)this.field_6031);
					this.method_6005(lv2, 0.4F, d, e);
				} else {
					this.field_6271 = (float)((int)(Math.random() * 2.0) * 180);
				}
			}

			if (this.method_6032() <= 0.0F) {
				if (!this.method_6095(arg)) {
					class_3414 lv4 = this.method_6002();
					if (bl2 && lv4 != null) {
						this.method_5783(lv4, this.method_6107(), this.method_6017());
					}

					this.method_6078(arg);
				}
			} else if (bl2) {
				this.method_6013(arg);
			}

			boolean bl3 = !bl || f > 0.0F;
			if (bl3) {
				this.field_6276 = arg;
				this.field_6226 = this.field_6002.method_8510();
			}

			if (this instanceof class_3222) {
				class_174.field_1209.method_8901((class_3222)this, arg, g, f, bl);
				if (h > 0.0F && h < 3.4028235E37F) {
					((class_3222)this).method_7339(class_3468.field_15380, Math.round(h * 10.0F));
				}
			}

			if (lv2 instanceof class_3222) {
				class_174.field_1199.method_9097((class_3222)lv2, this, arg, g, f, bl);
			}

			return bl3;
		}
	}

	protected void method_6090(class_1309 arg) {
		arg.method_6060(this);
	}

	protected void method_6060(class_1309 arg) {
		arg.method_6005(this, 0.5F, arg.field_5987 - this.field_5987, arg.field_6035 - this.field_6035);
	}

	private boolean method_6095(class_1282 arg) {
		if (arg.method_5538()) {
			return false;
		} else {
			class_1799 lv = null;

			for (class_1268 lv2 : class_1268.values()) {
				class_1799 lv3 = this.method_5998(lv2);
				if (lv3.method_7909() == class_1802.field_8288) {
					lv = lv3.method_7972();
					lv3.method_7934(1);
					break;
				}
			}

			if (lv != null) {
				if (this instanceof class_3222) {
					class_3222 lv4 = (class_3222)this;
					lv4.method_7259(class_3468.field_15372.method_14956(class_1802.field_8288));
					class_174.field_1204.method_9165(lv4, lv);
				}

				this.method_6033(1.0F);
				this.method_6012();
				this.method_6092(new class_1293(class_1294.field_5924, 900, 1));
				this.method_6092(new class_1293(class_1294.field_5898, 100, 1));
				this.field_6002.method_8421(this, (byte)35);
			}

			return lv != null;
		}
	}

	@Nullable
	public class_1282 method_6081() {
		if (this.field_6002.method_8510() - this.field_6226 > 40L) {
			this.field_6276 = null;
		}

		return this.field_6276;
	}

	protected void method_6013(class_1282 arg) {
		class_3414 lv = this.method_6011(arg);
		if (lv != null) {
			this.method_5783(lv, this.method_6107(), this.method_6017());
		}
	}

	private boolean method_6061(class_1282 arg) {
		class_1297 lv = arg.method_5526();
		boolean bl = false;
		if (lv instanceof class_1665) {
			class_1665 lv2 = (class_1665)lv;
			if (lv2.method_7447() > 0) {
				bl = true;
			}
		}

		if (!arg.method_5537() && this.method_6039() && !bl) {
			class_243 lv3 = arg.method_5510();
			if (lv3 != null) {
				class_243 lv4 = this.method_5828(1.0F);
				class_243 lv5 = lv3.method_1035(new class_243(this.field_5987, this.field_6010, this.field_6035)).method_1029();
				lv5 = new class_243(lv5.field_1352, 0.0, lv5.field_1350);
				if (lv5.method_1026(lv4) < 0.0) {
					return true;
				}
			}
		}

		return false;
	}

	public void method_6045(class_1799 arg) {
		super.method_5783(class_3417.field_15075, 0.8F, 0.8F + this.field_6002.field_9229.nextFloat() * 0.4F);
		this.method_6037(arg, 5);
	}

	public void method_6078(class_1282 arg) {
		if (!this.field_6272) {
			class_1297 lv = arg.method_5529();
			class_1309 lv2 = this.method_6124();
			if (this.field_6232 >= 0 && lv2 != null) {
				lv2.method_5716(this, this.field_6232, arg);
			}

			if (lv != null) {
				lv.method_5874(this);
			}

			this.field_6272 = true;
			this.method_6066().method_5539();
			if (!this.field_6002.field_9236) {
				this.method_16080(arg);
				if (lv2 instanceof class_1528 && this.field_6002.method_8450().method_8355("mobGriefing")) {
					class_2338 lv3 = new class_2338(this.field_5987, this.field_6010, this.field_6035);
					class_2680 lv4 = class_2246.field_10606.method_9564();
					if (this.field_6002.method_8320(lv3).method_11588() && lv4.method_11591(this.field_6002, lv3)) {
						this.field_6002.method_8652(lv3, lv4, 3);
					}
				}
			}

			this.field_6002.method_8421(this, (byte)3);
		}
	}

	protected void method_16080(class_1282 arg) {
		class_1297 lv = arg.method_5529();
		int i;
		if (lv instanceof class_1657) {
			i = class_1890.method_8226((class_1309)lv);
		} else {
			i = 0;
		}

		boolean bl = this.field_6238 > 0;
		if (this.method_6054() && this.field_6002.method_8450().method_8355("doMobLoot")) {
			this.method_16077(arg, bl);
			this.method_6099(arg, i, bl);
		}

		this.method_16078();
	}

	protected void method_16078() {
	}

	protected void method_6099(class_1282 arg, int i, boolean bl) {
	}

	public class_2960 method_5989() {
		return this.method_5864().method_16351();
	}

	protected void method_16077(class_1282 arg, boolean bl) {
		class_2960 lv = this.method_5989();
		class_52 lv2 = this.field_6002.method_8503().method_3857().method_367(lv);
		class_47.class_48 lv3 = this.method_16079(bl, arg);
		lv2.method_320(lv3.method_309(class_173.field_1173), this::method_5775);
	}

	protected class_47.class_48 method_16079(boolean bl, class_1282 arg) {
		class_47.class_48 lv = new class_47.class_48((class_3218)this.field_6002)
			.method_311(this.field_5974)
			.method_312(class_181.field_1226, this)
			.method_312(class_181.field_1232, new class_2338(this))
			.method_312(class_181.field_1231, arg)
			.method_306(class_181.field_1230, arg.method_5529())
			.method_306(class_181.field_1227, arg.method_5526());
		if (bl && this.field_6258 != null) {
			lv = lv.method_312(class_181.field_1233, this.field_6258).method_303(this.field_6258.method_7292());
		}

		return lv;
	}

	public void method_6005(class_1297 arg, float f, double d, double e) {
		if (!(this.field_5974.nextDouble() < this.method_5996(class_1612.field_7360).method_6194())) {
			this.field_6007 = true;
			float g = class_3532.method_15368(d * d + e * e);
			this.field_5967 /= 2.0;
			this.field_6006 /= 2.0;
			this.field_5967 -= d / (double)g * (double)f;
			this.field_6006 -= e / (double)g * (double)f;
			if (this.field_5952) {
				this.field_5984 /= 2.0;
				this.field_5984 += (double)f;
				if (this.field_5984 > 0.4F) {
					this.field_5984 = 0.4F;
				}
			}
		}
	}

	@Nullable
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_14940;
	}

	@Nullable
	protected class_3414 method_6002() {
		return class_3417.field_14732;
	}

	protected class_3414 method_6041(int i) {
		return i > 4 ? class_3417.field_14928 : class_3417.field_15018;
	}

	public boolean method_6101() {
		if (this instanceof class_1657 && ((class_1657)this).method_7325()) {
			return false;
		} else {
			class_2680 lv = this.method_16212();
			class_2248 lv2 = lv.method_11614();
			return lv2 != class_2246.field_9983 && lv2 != class_2246.field_10597 && lv2 != class_2246.field_16492
				? lv2 instanceof class_2533 && this.method_6077(new class_2338(this), lv)
				: true;
		}
	}

	public class_2680 method_16212() {
		return this.field_6002.method_8320(new class_2338(this));
	}

	private boolean method_6077(class_2338 arg, class_2680 arg2) {
		if ((Boolean)arg2.method_11654(class_2533.field_11631)) {
			class_2680 lv = this.field_6002.method_8320(arg.method_10074());
			if (lv.method_11614() == class_2246.field_9983 && lv.method_11654(class_2399.field_11253) == arg2.method_11654(class_2533.field_11177)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean method_5805() {
		return !this.field_5988 && this.method_6032() > 0.0F;
	}

	@Override
	public void method_5747(float f, float g) {
		super.method_5747(f, g);
		class_1293 lv = this.method_6112(class_1294.field_5913);
		float h = lv == null ? 0.0F : (float)(lv.method_5578() + 1);
		int i = class_3532.method_15386((f - 3.0F - h) * g);
		if (i > 0) {
			this.method_5783(this.method_6041(i), 1.0F, 1.0F);
			this.method_5643(class_1282.field_5868, (float)i);
			int j = class_3532.method_15357(this.field_5987);
			int k = class_3532.method_15357(this.field_6010 - 0.2F);
			int l = class_3532.method_15357(this.field_6035);
			class_2680 lv2 = this.field_6002.method_8320(new class_2338(j, k, l));
			if (!lv2.method_11588()) {
				class_2498 lv3 = lv2.method_11638();
				this.method_5783(lv3.method_10593(), lv3.method_10597() * 0.5F, lv3.method_10599() * 0.75F);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5879() {
		this.field_6254 = 10;
		this.field_6235 = this.field_6254;
		this.field_6271 = 0.0F;
	}

	public int method_6096() {
		class_1324 lv = this.method_5996(class_1612.field_7358);
		return class_3532.method_15357(lv.method_6194());
	}

	protected void method_6105(float f) {
	}

	protected void method_6056(float f) {
	}

	protected float method_6132(class_1282 arg, float f) {
		if (!arg.method_5537()) {
			this.method_6105(f);
			f = class_1280.method_5496(f, (float)this.method_6096(), (float)this.method_5996(class_1612.field_7364).method_6194());
		}

		return f;
	}

	protected float method_6036(class_1282 arg, float f) {
		if (arg.method_5504()) {
			return f;
		} else {
			if (this.method_6059(class_1294.field_5907) && arg != class_1282.field_5849) {
				int i = (this.method_6112(class_1294.field_5907).method_5578() + 1) * 5;
				int j = 25 - i;
				float g = f * (float)j;
				float h = f;
				f = Math.max(g / 25.0F, 0.0F);
				float k = h - f;
				if (k > 0.0F && k < 3.4028235E37F) {
					if (this instanceof class_3222) {
						((class_3222)this).method_7339(class_3468.field_15425, Math.round(k * 10.0F));
					} else if (arg.method_5529() instanceof class_3222) {
						((class_3222)arg.method_5529()).method_7339(class_3468.field_15397, Math.round(k * 10.0F));
					}
				}
			}

			if (f <= 0.0F) {
				return 0.0F;
			} else {
				int i = class_1890.method_8219(this.method_5661(), arg);
				if (i > 0) {
					f = class_1280.method_5497(f, (float)i);
				}

				return f;
			}
		}
	}

	protected void method_6074(class_1282 arg, float f) {
		if (!this.method_5679(arg)) {
			f = this.method_6132(arg, f);
			f = this.method_6036(arg, f);
			float var8 = Math.max(f - this.method_6067(), 0.0F);
			this.method_6073(this.method_6067() - (f - var8));
			float h = f - var8;
			if (h > 0.0F && h < 3.4028235E37F && arg.method_5529() instanceof class_3222) {
				((class_3222)arg.method_5529()).method_7339(class_3468.field_15408, Math.round(h * 10.0F));
			}

			if (var8 != 0.0F) {
				float i = this.method_6032();
				this.method_6033(i - var8);
				this.method_6066().method_5547(arg, i, var8);
				this.method_6073(this.method_6067() - var8);
			}
		}
	}

	public class_1283 method_6066() {
		return this.field_6256;
	}

	@Nullable
	public class_1309 method_6124() {
		if (this.field_6256.method_5541() != null) {
			return this.field_6256.method_5541();
		} else if (this.field_6258 != null) {
			return this.field_6258;
		} else {
			return this.field_6274 != null ? this.field_6274 : null;
		}
	}

	public final float method_6063() {
		return (float)this.method_5996(class_1612.field_7359).method_6194();
	}

	public final int method_6022() {
		return this.field_6011.method_12789(field_6219);
	}

	public final void method_6097(int i) {
		this.field_6011.method_12778(field_6219, i);
	}

	private int method_6028() {
		if (class_1292.method_5576(this)) {
			return 6 - (1 + class_1292.method_5575(this));
		} else {
			return this.method_6059(class_1294.field_5901) ? 6 + (1 + this.method_6112(class_1294.field_5901).method_5578()) * 2 : 6;
		}
	}

	public void method_6104(class_1268 arg) {
		if (!this.field_6252 || this.field_6279 >= this.method_6028() / 2 || this.field_6279 < 0) {
			this.field_6279 = -1;
			this.field_6252 = true;
			this.field_6266 = arg;
			if (this.field_6002 instanceof class_3218) {
				((class_3218)this.field_6002).method_14180().method_14079(this, new class_2616(this, arg == class_1268.field_5808 ? 0 : 3));
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		boolean bl = b == 33;
		boolean bl2 = b == 36;
		boolean bl3 = b == 37;
		if (b == 2 || bl || bl2 || bl3) {
			this.field_6225 = 1.5F;
			this.field_6008 = 20;
			this.field_6254 = 10;
			this.field_6235 = this.field_6254;
			this.field_6271 = 0.0F;
			if (bl) {
				this.method_5783(class_3417.field_14663, this.method_6107(), (this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.2F + 1.0F);
			}

			class_1282 lv;
			if (bl3) {
				lv = class_1282.field_5854;
			} else if (bl2) {
				lv = class_1282.field_5859;
			} else {
				lv = class_1282.field_5869;
			}

			class_3414 lv2 = this.method_6011(lv);
			if (lv2 != null) {
				this.method_5783(lv2, this.method_6107(), (this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.2F + 1.0F);
			}

			this.method_5643(class_1282.field_5869, 0.0F);
		} else if (b == 3) {
			class_3414 lv3 = this.method_6002();
			if (lv3 != null) {
				this.method_5783(lv3, this.method_6107(), (this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.2F + 1.0F);
			}

			this.method_6033(0.0F);
			this.method_6078(class_1282.field_5869);
		} else if (b == 30) {
			this.method_5783(class_3417.field_15239, 0.8F, 0.8F + this.field_6002.field_9229.nextFloat() * 0.4F);
		} else if (b == 29) {
			this.method_5783(class_3417.field_15150, 1.0F, 0.8F + this.field_6002.field_9229.nextFloat() * 0.4F);
		} else {
			super.method_5711(b);
		}
	}

	@Override
	protected void method_5825() {
		this.method_5643(class_1282.field_5849, 4.0F);
	}

	protected void method_6119() {
		int i = this.method_6028();
		if (this.field_6252) {
			this.field_6279++;
			if (this.field_6279 >= i) {
				this.field_6279 = 0;
				this.field_6252 = false;
			}
		} else {
			this.field_6279 = 0;
		}

		this.field_6251 = (float)this.field_6279 / (float)i;
	}

	public class_1324 method_5996(class_1320 arg) {
		return this.method_6127().method_6205(arg);
	}

	public class_1325 method_6127() {
		if (this.field_6260 == null) {
			this.field_6260 = new class_1327();
		}

		return this.field_6260;
	}

	public class_1310 method_6046() {
		return class_1310.field_6290;
	}

	public class_1799 method_6047() {
		return this.method_6118(class_1304.field_6173);
	}

	public class_1799 method_6079() {
		return this.method_6118(class_1304.field_6171);
	}

	public class_1799 method_5998(class_1268 arg) {
		if (arg == class_1268.field_5808) {
			return this.method_6118(class_1304.field_6173);
		} else if (arg == class_1268.field_5810) {
			return this.method_6118(class_1304.field_6171);
		} else {
			throw new IllegalArgumentException("Invalid hand " + arg);
		}
	}

	public void method_6122(class_1268 arg, class_1799 arg2) {
		if (arg == class_1268.field_5808) {
			this.method_5673(class_1304.field_6173, arg2);
		} else {
			if (arg != class_1268.field_5810) {
				throw new IllegalArgumentException("Invalid hand " + arg);
			}

			this.method_5673(class_1304.field_6171, arg2);
		}
	}

	public boolean method_6084(class_1304 arg) {
		return !this.method_6118(arg).method_7960();
	}

	@Override
	public abstract Iterable<class_1799> method_5661();

	public abstract class_1799 method_6118(class_1304 arg);

	@Override
	public abstract void method_5673(class_1304 arg, class_1799 arg2);

	@Override
	public void method_5728(boolean bl) {
		super.method_5728(bl);
		class_1324 lv = this.method_5996(class_1612.field_7357);
		if (lv.method_6199(field_6237) != null) {
			lv.method_6202(field_6231);
		}

		if (bl) {
			lv.method_6197(field_6231);
		}
	}

	protected float method_6107() {
		return 1.0F;
	}

	protected float method_6017() {
		return this.method_6109()
			? (this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.2F + 1.5F
			: (this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.2F + 1.0F;
	}

	protected boolean method_6062() {
		return this.method_6032() <= 0.0F;
	}

	public void method_6038(class_1297 arg) {
		if (!(arg instanceof class_1690) && !(arg instanceof class_1496)) {
			double k = arg.field_5987;
			double l = arg.method_5829().field_1322 + (double)arg.field_6019;
			double e = arg.field_6035;
			class_2350 lv = arg.method_5755();
			if (lv != null) {
				class_2350 lv2 = lv.method_10170();
				int[][] is = new int[][]{{0, 1}, {0, -1}, {-1, 1}, {-1, -1}, {1, 1}, {1, -1}, {-1, 0}, {1, 0}, {0, 1}};
				double m = Math.floor(this.field_5987) + 0.5;
				double n = Math.floor(this.field_6035) + 0.5;
				double o = this.method_5829().field_1320 - this.method_5829().field_1323;
				double p = this.method_5829().field_1324 - this.method_5829().field_1321;
				class_238 lv3 = new class_238(
					m - o / 2.0, arg.method_5829().field_1322, n - p / 2.0, m + o / 2.0, Math.floor(arg.method_5829().field_1322) + (double)this.field_6019, n + p / 2.0
				);

				for (int[] js : is) {
					double q = (double)(lv.method_10148() * js[0] + lv2.method_10148() * js[1]);
					double r = (double)(lv.method_10165() * js[0] + lv2.method_10165() * js[1]);
					double s = m + q;
					double t = n + r;
					class_238 lv4 = lv3.method_989(q, 0.0, r);
					if (this.field_6002.method_8587(this, lv4)) {
						class_2338 lv5 = new class_2338(s, this.field_6010, t);
						if (this.field_6002.method_8320(lv5).method_11631(this.field_6002, lv5)) {
							this.method_5859(s, this.field_6010 + 1.0, t);
							return;
						}

						class_2338 lv6 = new class_2338(s, this.field_6010 - 1.0, t);
						if (this.field_6002.method_8320(lv6).method_11631(this.field_6002, lv6) || this.field_6002.method_8316(lv6).method_15767(class_3486.field_15517)) {
							k = s;
							l = this.field_6010 + 1.0;
							e = t;
						}
					} else {
						class_2338 lv5x = new class_2338(s, this.field_6010 + 1.0, t);
						if (this.field_6002.method_8587(this, lv4.method_989(0.0, 1.0, 0.0)) && this.field_6002.method_8320(lv5x).method_11631(this.field_6002, lv5x)) {
							k = s;
							l = this.field_6010 + 2.0;
							e = t;
						}
					}
				}
			}

			this.method_5859(k, l, e);
		} else {
			double d = (double)(this.field_5998 / 2.0F + arg.field_5998 / 2.0F) + 0.4;
			float f;
			if (arg instanceof class_1690) {
				f = 0.0F;
			} else {
				f = (float) (Math.PI / 2) * (float)(this.method_6068() == class_1306.field_6183 ? -1 : 1);
			}

			float g = -class_3532.method_15374(-this.field_6031 * (float) (Math.PI / 180.0) - (float) Math.PI + f);
			float h = -class_3532.method_15362(-this.field_6031 * (float) (Math.PI / 180.0) - (float) Math.PI + f);
			double e = Math.abs(g) > Math.abs(h) ? d / (double)Math.abs(g) : d / (double)Math.abs(h);
			double i = this.field_5987 + (double)g * e;
			double j = this.field_6035 + (double)h * e;
			this.method_5814(i, arg.field_6010 + (double)arg.field_6019 + 0.001, j);
			if (!this.field_6002.method_8587(this, this.method_5829().method_991(arg.method_5829()))) {
				this.method_5814(i, arg.field_6010 + (double)arg.field_6019 + 1.001, j);
				if (!this.field_6002.method_8587(this, this.method_5829().method_991(arg.method_5829()))) {
					this.method_5814(arg.field_5987, arg.field_6010 + (double)this.field_6019 + 0.001, arg.field_6035);
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_5733() {
		return this.method_5807();
	}

	protected float method_6106() {
		return 0.42F;
	}

	protected void method_6043() {
		this.field_5984 = (double)this.method_6106();
		if (this.method_6059(class_1294.field_5913)) {
			this.field_5984 = this.field_5984 + (double)((float)(this.method_6112(class_1294.field_5913).method_5578() + 1) * 0.1F);
		}

		if (this.method_5624()) {
			float f = this.field_6031 * (float) (Math.PI / 180.0);
			this.field_5967 = this.field_5967 - (double)(class_3532.method_15374(f) * 0.2F);
			this.field_6006 = this.field_6006 + (double)(class_3532.method_15362(f) * 0.2F);
		}

		this.field_6007 = true;
	}

	@Environment(EnvType.CLIENT)
	protected void method_6093() {
		this.field_5984 -= 0.04F;
	}

	protected void method_6010(class_3494<class_3611> arg) {
		this.field_5984 += 0.04F;
	}

	protected float method_6120() {
		return 0.8F;
	}

	public void method_6091(float f, float g, float h) {
		if (this.method_6034() || this.method_5787()) {
			double d = 0.08;
			if (this.field_5984 <= 0.0 && this.method_6059(class_1294.field_5906)) {
				d = 0.01;
				this.field_6017 = 0.0F;
			}

			if (!this.method_5799() || this instanceof class_1657 && ((class_1657)this).field_7503.field_7479) {
				if (!this.method_5771() || this instanceof class_1657 && ((class_1657)this).field_7503.field_7479) {
					if (this.method_6128()) {
						if (this.field_5984 > -0.5) {
							this.field_6017 = 1.0F;
						}

						class_243 lv = this.method_5720();
						float l = this.field_5965 * (float) (Math.PI / 180.0);
						double m = Math.sqrt(lv.field_1352 * lv.field_1352 + lv.field_1350 * lv.field_1350);
						double n = Math.sqrt(this.field_5967 * this.field_5967 + this.field_6006 * this.field_6006);
						double o = lv.method_1033();
						float p = class_3532.method_15362(l);
						p = (float)((double)p * (double)p * Math.min(1.0, o / 0.4));
						this.field_5984 += d * (-1.0 + (double)p * 0.75);
						if (this.field_5984 < 0.0 && m > 0.0) {
							double q = this.field_5984 * -0.1 * (double)p;
							this.field_5984 += q;
							this.field_5967 = this.field_5967 + lv.field_1352 * q / m;
							this.field_6006 = this.field_6006 + lv.field_1350 * q / m;
						}

						if (l < 0.0F && m > 0.0) {
							double q = n * (double)(-class_3532.method_15374(l)) * 0.04;
							this.field_5984 += q * 3.2;
							this.field_5967 = this.field_5967 - lv.field_1352 * q / m;
							this.field_6006 = this.field_6006 - lv.field_1350 * q / m;
						}

						if (m > 0.0) {
							this.field_5967 = this.field_5967 + (lv.field_1352 / m * n - this.field_5967) * 0.1;
							this.field_6006 = this.field_6006 + (lv.field_1350 / m * n - this.field_6006) * 0.1;
						}

						this.field_5967 *= 0.99F;
						this.field_5984 *= 0.98F;
						this.field_6006 *= 0.99F;
						this.method_5784(class_1313.field_6308, this.field_5967, this.field_5984, this.field_6006);
						if (this.field_5976 && !this.field_6002.field_9236) {
							double q = Math.sqrt(this.field_5967 * this.field_5967 + this.field_6006 * this.field_6006);
							double r = n - q;
							float s = (float)(r * 10.0 - 3.0);
							if (s > 0.0F) {
								this.method_5783(this.method_6041((int)s), 1.0F, 1.0F);
								this.method_5643(class_1282.field_5843, s);
							}
						}

						if (this.field_5952 && !this.field_6002.field_9236) {
							this.method_5729(7, false);
						}
					} else {
						float t = 0.91F;

						try (class_2338.class_2340 lv2 = class_2338.class_2340.method_10115(this.field_5987, this.method_5829().field_1322 - 1.0, this.field_6035)) {
							if (this.field_5952) {
								t = this.field_6002.method_8320(lv2).method_11614().method_9499() * 0.91F;
							}

							float j = 0.16277137F / (t * t * t);
							float k;
							if (this.field_5952) {
								k = this.method_6029() * j;
							} else {
								k = this.field_6281;
							}

							this.method_5724(f, g, h, k);
							t = 0.91F;
							if (this.field_5952) {
								t = this.field_6002.method_8320(lv2.method_10112(this.field_5987, this.method_5829().field_1322 - 1.0, this.field_6035)).method_11614().method_9499()
									* 0.91F;
							}

							if (this.method_6101()) {
								float u = 0.15F;
								this.field_5967 = class_3532.method_15350(this.field_5967, -0.15F, 0.15F);
								this.field_6006 = class_3532.method_15350(this.field_6006, -0.15F, 0.15F);
								this.field_6017 = 0.0F;
								if (this.field_5984 < -0.15) {
									this.field_5984 = -0.15;
								}

								if (this.method_16212().method_11614() != class_2246.field_16492) {
									boolean bl = this.method_5715() && this instanceof class_1657;
									if (bl && this.field_5984 < 0.0) {
										this.field_5984 = 0.0;
									}
								}
							}

							this.method_5784(class_1313.field_6308, this.field_5967, this.field_5984, this.field_6006);
							if ((this.field_5976 || this.field_6282) && this.method_6101()) {
								this.field_5984 = 0.2;
							}

							if (this.method_6059(class_1294.field_5902)) {
								this.field_5984 = this.field_5984 + (0.05 * (double)(this.method_6112(class_1294.field_5902).method_5578() + 1) - this.field_5984) * 0.2;
								this.field_6017 = 0.0F;
							} else {
								lv2.method_10112(this.field_5987, 0.0, this.field_6035);
								if (!this.field_6002.field_9236 || this.field_6002.method_8591(lv2) && this.field_6002.method_8500(lv2).method_12229()) {
									if (!this.method_5740()) {
										this.field_5984 -= d;
									}
								} else if (this.field_6010 > 0.0) {
									this.field_5984 = -0.1;
								} else {
									this.field_5984 = 0.0;
								}
							}

							this.field_5984 *= 0.98F;
							this.field_5967 *= (double)t;
							this.field_6006 *= (double)t;
						}
					}
				} else {
					double e = this.field_6010;
					this.method_5724(f, g, h, 0.02F);
					this.method_5784(class_1313.field_6308, this.field_5967, this.field_5984, this.field_6006);
					this.field_5967 *= 0.5;
					this.field_5984 *= 0.5;
					this.field_6006 *= 0.5;
					if (!this.method_5740()) {
						this.field_5984 -= d / 4.0;
					}

					if (this.field_5976 && this.method_5654(this.field_5967, this.field_5984 + 0.6F - this.field_6010 + e, this.field_6006)) {
						this.field_5984 = 0.3F;
					}
				}
			} else {
				double ex = this.field_6010;
				float i = this.method_5624() ? 0.9F : this.method_6120();
				float jx = 0.02F;
				float kx = (float)class_1890.method_8232(this);
				if (kx > 3.0F) {
					kx = 3.0F;
				}

				if (!this.field_5952) {
					kx *= 0.5F;
				}

				if (kx > 0.0F) {
					i += (0.54600006F - i) * kx / 3.0F;
					jx += (this.method_6029() - jx) * kx / 3.0F;
				}

				if (this.method_6059(class_1294.field_5900)) {
					i = 0.96F;
				}

				this.method_5724(f, g, h, jx);
				this.method_5784(class_1313.field_6308, this.field_5967, this.field_5984, this.field_6006);
				this.field_5967 *= (double)i;
				this.field_5984 *= 0.8F;
				this.field_6006 *= (double)i;
				if (!this.method_5740() && !this.method_5624()) {
					if (this.field_5984 <= 0.0 && Math.abs(this.field_5984 - 0.005) >= 0.003 && Math.abs(this.field_5984 - d / 16.0) < 0.003) {
						this.field_5984 = -0.003;
					} else {
						this.field_5984 -= d / 16.0;
					}
				}

				if (this.field_5976 && this.method_5654(this.field_5967, this.field_5984 + 0.6F - this.field_6010 + ex, this.field_6006)) {
					this.field_5984 = 0.3F;
				}
			}
		}

		this.field_6211 = this.field_6225;
		double dx = this.field_5987 - this.field_6014;
		double exx = this.field_6035 - this.field_5969;
		double mx = this instanceof class_1432 ? this.field_6010 - this.field_6036 : 0.0;
		float kxx = class_3532.method_15368(dx * dx + mx * mx + exx * exx) * 4.0F;
		if (kxx > 1.0F) {
			kxx = 1.0F;
		}

		this.field_6225 = this.field_6225 + (kxx - this.field_6225) * 0.4F;
		this.field_6249 = this.field_6249 + this.field_6225;
	}

	public float method_6029() {
		return this.field_6287;
	}

	public void method_6125(float f) {
		this.field_6287 = f;
	}

	public boolean method_6121(class_1297 arg) {
		this.method_6114(arg);
		return false;
	}

	public boolean method_6113() {
		return false;
	}

	@Override
	public void method_5773() {
		super.method_5773();
		this.method_6076();
		this.method_6072();
		if (!this.field_6002.field_9236) {
			int i = this.method_6022();
			if (i > 0) {
				if (this.field_6218 <= 0) {
					this.field_6218 = 20 * (30 - i);
				}

				this.field_6218--;
				if (this.field_6218 <= 0) {
					this.method_6097(i - 1);
				}
			}

			for (class_1304 lv : class_1304.values()) {
				class_1799 lv2;
				switch (lv.method_5925()) {
					case field_6177:
						lv2 = this.field_6234.get(lv.method_5927());
						break;
					case field_6178:
						lv2 = this.field_6248.get(lv.method_5927());
						break;
					default:
						continue;
				}

				class_1799 lv3 = this.method_6118(lv);
				if (!class_1799.method_7973(lv3, lv2)) {
					((class_3218)this.field_6002).method_14180().method_14079(this, new class_2744(this.method_5628(), lv, lv3));
					if (!lv2.method_7960()) {
						this.method_6127().method_6209(lv2.method_7926(lv));
					}

					if (!lv3.method_7960()) {
						this.method_6127().method_6210(lv3.method_7926(lv));
					}

					switch (lv.method_5925()) {
						case field_6177:
							this.field_6234.set(lv.method_5927(), lv3.method_7960() ? class_1799.field_8037 : lv3.method_7972());
							break;
						case field_6178:
							this.field_6248.set(lv.method_5927(), lv3.method_7960() ? class_1799.field_8037 : lv3.method_7972());
					}
				}
			}

			if (this.field_6012 % 20 == 0) {
				this.method_6066().method_5539();
			}

			if (!this.field_5958) {
				boolean bl = this.method_6059(class_1294.field_5912);
				if (this.method_5795(6) != bl) {
					this.method_5729(6, bl);
				}
			}
		}

		this.method_6007();
		double d = this.field_5987 - this.field_6014;
		double e = this.field_6035 - this.field_5969;
		float f = (float)(d * d + e * e);
		float g = this.field_6283;
		float h = 0.0F;
		this.field_6217 = this.field_6233;
		float j = 0.0F;
		if (f > 0.0025000002F) {
			j = 1.0F;
			h = (float)Math.sqrt((double)f) * 3.0F;
			float k = (float)class_3532.method_15349(e, d) * (180.0F / (float)Math.PI) - 90.0F;
			float l = class_3532.method_15379(class_3532.method_15393(this.field_6031) - k);
			if (95.0F < l && l < 265.0F) {
				g = k - 180.0F;
			} else {
				g = k;
			}
		}

		if (this.field_6251 > 0.0F) {
			g = this.field_6031;
		}

		if (!this.field_5952) {
			j = 0.0F;
		}

		this.field_6233 = this.field_6233 + (j - this.field_6233) * 0.3F;
		this.field_6002.method_16107().method_15396("headTurn");
		h = this.method_6031(g, h);
		this.field_6002.method_16107().method_15407();
		this.field_6002.method_16107().method_15396("rangeChecks");

		while (this.field_6031 - this.field_5982 < -180.0F) {
			this.field_5982 -= 360.0F;
		}

		while (this.field_6031 - this.field_5982 >= 180.0F) {
			this.field_5982 += 360.0F;
		}

		while (this.field_6283 - this.field_6220 < -180.0F) {
			this.field_6220 -= 360.0F;
		}

		while (this.field_6283 - this.field_6220 >= 180.0F) {
			this.field_6220 += 360.0F;
		}

		while (this.field_5965 - this.field_6004 < -180.0F) {
			this.field_6004 -= 360.0F;
		}

		while (this.field_5965 - this.field_6004 >= 180.0F) {
			this.field_6004 += 360.0F;
		}

		while (this.field_6241 - this.field_6259 < -180.0F) {
			this.field_6259 -= 360.0F;
		}

		while (this.field_6241 - this.field_6259 >= 180.0F) {
			this.field_6259 += 360.0F;
		}

		this.field_6002.method_16107().method_15407();
		this.field_6255 += h;
		if (this.method_6128()) {
			this.field_6239++;
		} else {
			this.field_6239 = 0;
		}
	}

	protected float method_6031(float f, float g) {
		float h = class_3532.method_15393(f - this.field_6283);
		this.field_6283 += h * 0.3F;
		float i = class_3532.method_15393(this.field_6031 - this.field_6283);
		boolean bl = i < -90.0F || i >= 90.0F;
		if (i < -75.0F) {
			i = -75.0F;
		}

		if (i >= 75.0F) {
			i = 75.0F;
		}

		this.field_6283 = this.field_6031 - i;
		if (i * i > 2500.0F) {
			this.field_6283 += i * 0.2F;
		}

		if (bl) {
			g *= -1.0F;
		}

		return g;
	}

	public void method_6007() {
		if (this.field_6228 > 0) {
			this.field_6228--;
		}

		if (this.field_6210 > 0 && !this.method_5787()) {
			double d = this.field_5987 + (this.field_6224 - this.field_5987) / (double)this.field_6210;
			double e = this.field_6010 + (this.field_6245 - this.field_6010) / (double)this.field_6210;
			double f = this.field_6035 + (this.field_6263 - this.field_6035) / (double)this.field_6210;
			double g = class_3532.method_15338(this.field_6284 - (double)this.field_6031);
			this.field_6031 = (float)((double)this.field_6031 + g / (double)this.field_6210);
			this.field_5965 = (float)((double)this.field_5965 + (this.field_6221 - (double)this.field_5965) / (double)this.field_6210);
			this.field_6210--;
			this.method_5814(d, e, f);
			this.method_5710(this.field_6031, this.field_5965);
		} else if (!this.method_6034()) {
			this.field_5967 *= 0.98;
			this.field_5984 *= 0.98;
			this.field_6006 *= 0.98;
		}

		if (this.field_6265 > 0) {
			this.field_6241 = (float)((double)this.field_6241 + class_3532.method_15338(this.field_6242 - (double)this.field_6241) / (double)this.field_6265);
			this.field_6265--;
		}

		if (Math.abs(this.field_5967) < 0.003) {
			this.field_5967 = 0.0;
		}

		if (Math.abs(this.field_5984) < 0.003) {
			this.field_5984 = 0.0;
		}

		if (Math.abs(this.field_6006) < 0.003) {
			this.field_6006 = 0.0;
		}

		this.field_6002.method_16107().method_15396("ai");
		if (this.method_6062()) {
			this.field_6282 = false;
			this.field_6212 = 0.0F;
			this.field_6250 = 0.0F;
			this.field_6267 = 0.0F;
		} else if (this.method_6034()) {
			this.field_6002.method_16107().method_15396("newAi");
			this.method_6023();
			this.field_6002.method_16107().method_15407();
		}

		this.field_6002.method_16107().method_15407();
		this.field_6002.method_16107().method_15396("jump");
		if (this.field_6282) {
			if (!(this.field_5964 > 0.0) || this.field_5952 && !(this.field_5964 > 0.4)) {
				if (this.method_5771()) {
					this.method_6010(class_3486.field_15518);
				} else if ((this.field_5952 || this.field_5964 > 0.0 && this.field_5964 <= 0.4) && this.field_6228 == 0) {
					this.method_6043();
					this.field_6228 = 10;
				}
			} else {
				this.method_6010(class_3486.field_15517);
			}
		} else {
			this.field_6228 = 0;
		}

		this.field_6002.method_16107().method_15407();
		this.field_6002.method_16107().method_15396("travel");
		this.field_6212 *= 0.98F;
		this.field_6250 *= 0.98F;
		this.field_6267 *= 0.9F;
		this.method_6053();
		class_238 lv = this.method_5829();
		this.method_6091(this.field_6212, this.field_6227, this.field_6250);
		this.field_6002.method_16107().method_15407();
		this.field_6002.method_16107().method_15396("push");
		if (this.field_6261 > 0) {
			this.field_6261--;
			this.method_6035(lv, this.method_5829());
		}

		this.method_6070();
		this.field_6002.method_16107().method_15407();
	}

	private void method_6053() {
		boolean bl = this.method_5795(7);
		if (bl && !this.field_5952 && !this.method_5765()) {
			class_1799 lv = this.method_6118(class_1304.field_6174);
			if (lv.method_7909() == class_1802.field_8833 && class_1770.method_7804(lv)) {
				bl = true;
				if (!this.field_6002.field_9236 && (this.field_6239 + 1) % 20 == 0) {
					lv.method_7956(1, this);
				}
			} else {
				bl = false;
			}
		} else {
			bl = false;
		}

		if (!this.field_6002.field_9236) {
			this.method_5729(7, bl);
		}
	}

	protected void method_6023() {
	}

	protected void method_6070() {
		List<class_1297> list = this.field_6002.method_8333(this, this.method_5829(), class_1301.method_5911(this));
		if (!list.isEmpty()) {
			int i = this.field_6002.method_8450().method_8356("maxEntityCramming");
			if (i > 0 && list.size() > i - 1 && this.field_5974.nextInt(4) == 0) {
				int j = 0;

				for (int k = 0; k < list.size(); k++) {
					if (!((class_1297)list.get(k)).method_5765()) {
						j++;
					}
				}

				if (j > i - 1) {
					this.method_5643(class_1282.field_5844, 6.0F);
				}
			}

			for (int j = 0; j < list.size(); j++) {
				class_1297 lv = (class_1297)list.get(j);
				this.method_6087(lv);
			}
		}
	}

	protected void method_6035(class_238 arg, class_238 arg2) {
		class_238 lv = arg.method_991(arg2);
		List<class_1297> list = this.field_6002.method_8335(this, lv);
		if (!list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				class_1297 lv2 = (class_1297)list.get(i);
				if (lv2 instanceof class_1309) {
					this.method_5997((class_1309)lv2);
					this.field_6261 = 0;
					this.field_5967 *= -0.2;
					this.field_5984 *= -0.2;
					this.field_6006 *= -0.2;
					break;
				}
			}
		} else if (this.field_5976) {
			this.field_6261 = 0;
		}

		if (!this.field_6002.field_9236 && this.field_6261 <= 0) {
			this.method_6085(4, false);
		}
	}

	protected void method_6087(class_1297 arg) {
		arg.method_5697(this);
	}

	protected void method_5997(class_1309 arg) {
	}

	public void method_6018(int i) {
		this.field_6261 = i;
		if (!this.field_6002.field_9236) {
			this.method_6085(4, true);
		}
	}

	public boolean method_6123() {
		return (this.field_6011.method_12789(field_6257) & 4) != 0;
	}

	@Override
	public void method_5848() {
		class_1297 lv = this.method_5854();
		super.method_5848();
		if (lv != null && lv != this.method_5854() && !this.field_6002.field_9236) {
			this.method_6038(lv);
		}
	}

	@Override
	public void method_5842() {
		super.method_5842();
		this.field_6217 = this.field_6233;
		this.field_6233 = 0.0F;
		this.field_6017 = 0.0F;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5759(double d, double e, double f, float g, float h, int i, boolean bl) {
		this.field_6224 = d;
		this.field_6245 = e;
		this.field_6263 = f;
		this.field_6284 = (double)g;
		this.field_6221 = (double)h;
		this.field_6210 = i;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5683(float f, int i) {
		this.field_6242 = (double)f;
		this.field_6265 = i;
	}

	public void method_6100(boolean bl) {
		this.field_6282 = bl;
	}

	public void method_6103(class_1297 arg, int i) {
		if (!arg.field_5988 && !this.field_6002.field_9236) {
			class_3208 lv = ((class_3218)this.field_6002).method_14180();
			if (arg instanceof class_1542 || arg instanceof class_1665 || arg instanceof class_1303) {
				lv.method_14079(arg, new class_2775(arg.method_5628(), this.method_5628(), i));
			}
		}
	}

	public boolean method_6057(class_1297 arg) {
		return this.field_6002
				.method_8531(
					new class_243(this.field_5987, this.field_6010 + (double)this.method_5751(), this.field_6035),
					new class_243(arg.field_5987, arg.field_6010 + (double)arg.method_5751(), arg.field_6035),
					class_242.field_1348,
					true,
					false
				)
			== null;
	}

	@Override
	public float method_5705(float f) {
		return f == 1.0F ? this.field_6241 : class_3532.method_16439(f, this.field_6259, this.field_6241);
	}

	@Environment(EnvType.CLIENT)
	public float method_6055(float f) {
		float g = this.field_6251 - this.field_6229;
		if (g < 0.0F) {
			g++;
		}

		return this.field_6229 + g * f;
	}

	public boolean method_6034() {
		return !this.field_6002.field_9236;
	}

	@Override
	public boolean method_5863() {
		return !this.field_5988;
	}

	@Override
	public boolean method_5810() {
		return this.method_5805() && !this.method_6101();
	}

	@Override
	protected void method_5785() {
		this.field_6037 = this.field_5974.nextDouble() >= this.method_5996(class_1612.field_7360).method_6194();
	}

	@Override
	public float method_5791() {
		return this.field_6241;
	}

	@Override
	public void method_5847(float f) {
		this.field_6241 = f;
	}

	@Override
	public void method_5636(float f) {
		this.field_6283 = f;
	}

	public float method_6067() {
		return this.field_6246;
	}

	public void method_6073(float f) {
		if (f < 0.0F) {
			f = 0.0F;
		}

		this.field_6246 = f;
	}

	public void method_6000() {
	}

	public void method_6044() {
	}

	protected void method_6008() {
		this.field_6285 = true;
	}

	public abstract class_1306 method_6068();

	public boolean method_6115() {
		return (this.field_6011.method_12789(field_6257) & 1) > 0;
	}

	public class_1268 method_6058() {
		return (this.field_6011.method_12789(field_6257) & 2) > 0 ? class_1268.field_5810 : class_1268.field_5808;
	}

	protected void method_6076() {
		if (this.method_6115()) {
			if (this.method_5998(this.method_6058()) == this.field_6277) {
				this.field_6277.method_7949(this.field_6002, this, this.method_6014());
				if (this.method_6014() <= 25 && this.method_6014() % 4 == 0) {
					this.method_6098(this.field_6277, 5);
				}

				if (--this.field_6222 == 0 && !this.field_6002.field_9236 && !this.field_6277.method_7967()) {
					this.method_6040();
				}
			} else {
				this.method_6021();
			}
		}
	}

	private void method_6072() {
		this.field_6264 = this.field_6243;
		if (this.method_5681()) {
			this.field_6243 = Math.min(1.0F, this.field_6243 + 0.09F);
		} else {
			this.field_6243 = Math.max(0.0F, this.field_6243 - 0.09F);
		}
	}

	protected void method_6085(int i, boolean bl) {
		int j = this.field_6011.method_12789(field_6257);
		if (bl) {
			j |= i;
		} else {
			j &= ~i;
		}

		this.field_6011.method_12778(field_6257, (byte)j);
	}

	public void method_6019(class_1268 arg) {
		class_1799 lv = this.method_5998(arg);
		if (!lv.method_7960() && !this.method_6115()) {
			this.field_6277 = lv;
			this.field_6222 = lv.method_7935();
			if (!this.field_6002.field_9236) {
				this.method_6085(1, true);
				this.method_6085(2, arg == class_1268.field_5810);
			}
		}
	}

	@Override
	public void method_5674(class_2940<?> arg) {
		super.method_5674(arg);
		if (field_6257.equals(arg) && this.field_6002.field_9236) {
			if (this.method_6115() && this.field_6277.method_7960()) {
				this.field_6277 = this.method_5998(this.method_6058());
				if (!this.field_6277.method_7960()) {
					this.field_6222 = this.field_6277.method_7935();
				}
			} else if (!this.method_6115() && !this.field_6277.method_7960()) {
				this.field_6277 = class_1799.field_8037;
				this.field_6222 = 0;
			}
		}
	}

	@Override
	public void method_5702(class_2183.class_2184 arg, class_243 arg2) {
		super.method_5702(arg, arg2);
		this.field_6259 = this.field_6241;
		this.field_6283 = this.field_6241;
		this.field_6220 = this.field_6283;
	}

	protected void method_6098(class_1799 arg, int i) {
		if (!arg.method_7960() && this.method_6115()) {
			if (arg.method_7976() == class_1839.field_8946) {
				this.method_5783(class_3417.field_14643, 0.5F, this.field_6002.field_9229.nextFloat() * 0.1F + 0.9F);
			}

			if (arg.method_7976() == class_1839.field_8950) {
				this.method_6037(arg, i);
				this.method_5783(
					class_3417.field_14544, 0.5F + 0.5F * (float)this.field_5974.nextInt(2), (this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.2F + 1.0F
				);
			}
		}
	}

	private void method_6037(class_1799 arg, int i) {
		for (int j = 0; j < i; j++) {
			class_243 lv = new class_243(((double)this.field_5974.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
			lv = lv.method_1037(-this.field_5965 * (float) (Math.PI / 180.0));
			lv = lv.method_1024(-this.field_6031 * (float) (Math.PI / 180.0));
			double d = (double)(-this.field_5974.nextFloat()) * 0.6 - 0.3;
			class_243 lv2 = new class_243(((double)this.field_5974.nextFloat() - 0.5) * 0.3, d, 0.6);
			lv2 = lv2.method_1037(-this.field_5965 * (float) (Math.PI / 180.0));
			lv2 = lv2.method_1024(-this.field_6031 * (float) (Math.PI / 180.0));
			lv2 = lv2.method_1031(this.field_5987, this.field_6010 + (double)this.method_5751(), this.field_6035);
			this.field_6002
				.method_8406(
					new class_2392(class_2398.field_11218, arg), lv2.field_1352, lv2.field_1351, lv2.field_1350, lv.field_1352, lv.field_1351 + 0.05, lv.field_1350
				);
		}
	}

	protected void method_6040() {
		if (!this.field_6277.method_7960() && this.method_6115()) {
			this.method_6098(this.field_6277, 16);
			this.method_6122(this.method_6058(), this.field_6277.method_7910(this.field_6002, this));
			this.method_6021();
		}
	}

	public class_1799 method_6030() {
		return this.field_6277;
	}

	public int method_6014() {
		return this.field_6222;
	}

	public int method_6048() {
		return this.method_6115() ? this.field_6277.method_7935() - this.method_6014() : 0;
	}

	public void method_6075() {
		if (!this.field_6277.method_7960()) {
			this.field_6277.method_7930(this.field_6002, this, this.method_6014());
			if (this.field_6277.method_7967()) {
				this.method_6076();
			}
		}

		this.method_6021();
	}

	public void method_6021() {
		if (!this.field_6002.field_9236) {
			this.method_6085(1, false);
		}

		this.field_6277 = class_1799.field_8037;
		this.field_6222 = 0;
	}

	public boolean method_6039() {
		if (this.method_6115() && !this.field_6277.method_7960()) {
			class_1792 lv = this.field_6277.method_7909();
			return lv.method_7853(this.field_6277) != class_1839.field_8949 ? false : lv.method_7881(this.field_6277) - this.field_6222 >= 5;
		} else {
			return false;
		}
	}

	public boolean method_6128() {
		return this.method_5795(7);
	}

	@Environment(EnvType.CLIENT)
	public int method_6003() {
		return this.field_6239;
	}

	public boolean method_6082(double d, double e, double f, boolean bl) {
		double g = this.field_5987;
		double h = this.field_6010;
		double i = this.field_6035;
		this.field_5987 = d;
		this.field_6010 = e;
		this.field_6035 = f;
		boolean bl2 = false;
		class_2338 lv = new class_2338(this);
		class_1936 lv2 = this.field_6002;
		Random random = this.method_6051();
		if (lv2.method_8591(lv)) {
			boolean bl3 = false;

			while (!bl3 && lv.method_10264() > 0) {
				class_2338 lv3 = lv.method_10074();
				class_2680 lv4 = lv2.method_8320(lv3);
				if (lv4.method_11620().method_15801()) {
					bl3 = true;
				} else {
					this.field_6010--;
					lv = lv3;
				}
			}

			if (bl3) {
				this.method_5859(this.field_5987, this.field_6010, this.field_6035);
				if (lv2.method_8587(this, this.method_5829()) && !lv2.method_8599(this.method_5829())) {
					bl2 = true;
				}
			}
		}

		if (!bl2) {
			this.method_5859(g, h, i);
			return false;
		} else {
			if (bl) {
				int j = 128;

				for (int k = 0; k < 128; k++) {
					double l = (double)k / 127.0;
					float m = (random.nextFloat() - 0.5F) * 0.2F;
					float n = (random.nextFloat() - 0.5F) * 0.2F;
					float o = (random.nextFloat() - 0.5F) * 0.2F;
					double p = class_3532.method_16436(l, g, this.field_5987) + (random.nextDouble() - 0.5) * (double)this.field_5998 * 2.0;
					double q = class_3532.method_16436(l, h, this.field_6010) + random.nextDouble() * (double)this.field_6019;
					double r = class_3532.method_16436(l, i, this.field_6035) + (random.nextDouble() - 0.5) * (double)this.field_5998 * 2.0;
					lv2.method_8406(class_2398.field_11214, p, q, r, (double)m, (double)n, (double)o);
				}
			}

			if (this instanceof class_1314) {
				((class_1314)this).method_5942().method_6340();
			}

			return true;
		}
	}

	public boolean method_6086() {
		return true;
	}

	public boolean method_6102() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	public void method_6006(class_2338 arg, boolean bl) {
	}
}
