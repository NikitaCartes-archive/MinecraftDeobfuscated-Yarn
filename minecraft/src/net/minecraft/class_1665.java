package net.minecraft;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_1665 extends class_1297 implements class_1676 {
	private static final Predicate<class_1297> field_7581 = class_1301.field_6155.and(class_1301.field_6154.and(class_1297::method_5863));
	private static final class_2940<Byte> field_7573 = class_2945.method_12791(class_1665.class, class_2943.field_13319);
	protected static final class_2940<Optional<UUID>> field_7580 = class_2945.method_12791(class_1665.class, class_2943.field_13313);
	private static final class_2940<Byte> field_7589 = class_2945.method_12791(class_1665.class, class_2943.field_13319);
	private int field_7585 = -1;
	private int field_7582 = -1;
	private int field_7583 = -1;
	@Nullable
	private class_2680 field_7586;
	protected boolean field_7588;
	protected int field_7576;
	public class_1665.class_1666 field_7572 = class_1665.class_1666.field_7592;
	public int field_7574;
	public UUID field_7587;
	private int field_7578;
	private int field_7577;
	private double field_7571 = 2.0;
	private int field_7575;
	private class_3414 field_7584;
	private IntOpenHashSet field_7590;
	private List<class_1297> field_7579;

	protected class_1665(class_1299<?> arg, class_1937 arg2) {
		super(arg, arg2);
		this.method_5835(0.5F, 0.5F);
		this.field_7584 = class_3417.field_15151;
	}

	protected class_1665(class_1299<?> arg, double d, double e, double f, class_1937 arg2) {
		this(arg, arg2);
		this.method_5814(d, e, f);
	}

	protected class_1665(class_1299<?> arg, class_1309 arg2, class_1937 arg3) {
		this(arg, arg2.field_5987, arg2.field_6010 + (double)arg2.method_5751() - 0.1F, arg2.field_6035, arg3);
		this.method_7432(arg2);
		if (arg2 instanceof class_1657) {
			this.field_7572 = class_1665.class_1666.field_7593;
		}
	}

	public void method_7444(class_3414 arg) {
		this.field_7584 = arg;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_5640(double d) {
		double e = this.method_5829().method_995() * 10.0;
		if (Double.isNaN(e)) {
			e = 1.0;
		}

		e *= 64.0 * method_5824();
		return d < e * e;
	}

	@Override
	protected void method_5693() {
		this.field_6011.method_12784(field_7573, (byte)0);
		this.field_6011.method_12784(field_7580, Optional.empty());
		this.field_6011.method_12784(field_7589, (byte)0);
	}

	public void method_7437(class_1297 arg, float f, float g, float h, float i, float j) {
		float k = -class_3532.method_15374(g * (float) (Math.PI / 180.0)) * class_3532.method_15362(f * (float) (Math.PI / 180.0));
		float l = -class_3532.method_15374(f * (float) (Math.PI / 180.0));
		float m = class_3532.method_15362(g * (float) (Math.PI / 180.0)) * class_3532.method_15362(f * (float) (Math.PI / 180.0));
		this.method_7485((double)k, (double)l, (double)m, i, j);
		this.field_5967 = this.field_5967 + arg.field_5967;
		this.field_6006 = this.field_6006 + arg.field_6006;
		if (!arg.field_5952) {
			this.field_5984 = this.field_5984 + arg.field_5984;
		}
	}

	@Override
	public void method_7485(double d, double e, double f, float g, float h) {
		float i = class_3532.method_15368(d * d + e * e + f * f);
		d /= (double)i;
		e /= (double)i;
		f /= (double)i;
		d += this.field_5974.nextGaussian() * 0.0075F * (double)h;
		e += this.field_5974.nextGaussian() * 0.0075F * (double)h;
		f += this.field_5974.nextGaussian() * 0.0075F * (double)h;
		d *= (double)g;
		e *= (double)g;
		f *= (double)g;
		this.field_5967 = d;
		this.field_5984 = e;
		this.field_6006 = f;
		float j = class_3532.method_15368(d * d + f * f);
		this.field_6031 = (float)(class_3532.method_15349(d, f) * 180.0F / (float)Math.PI);
		this.field_5965 = (float)(class_3532.method_15349(e, (double)j) * 180.0F / (float)Math.PI);
		this.field_5982 = this.field_6031;
		this.field_6004 = this.field_5965;
		this.field_7578 = 0;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5759(double d, double e, double f, float g, float h, int i, boolean bl) {
		this.method_5814(d, e, f);
		this.method_5710(g, h);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5750(double d, double e, double f) {
		this.field_5967 = d;
		this.field_5984 = e;
		this.field_6006 = f;
		if (this.field_6004 == 0.0F && this.field_5982 == 0.0F) {
			float g = class_3532.method_15368(d * d + f * f);
			this.field_5965 = (float)(class_3532.method_15349(e, (double)g) * 180.0F / (float)Math.PI);
			this.field_6031 = (float)(class_3532.method_15349(d, f) * 180.0F / (float)Math.PI);
			this.field_6004 = this.field_5965;
			this.field_5982 = this.field_6031;
			this.method_5808(this.field_5987, this.field_6010, this.field_6035, this.field_6031, this.field_5965);
			this.field_7578 = 0;
		}
	}

	@Override
	public void method_5773() {
		super.method_5773();
		boolean bl = this.method_7441();
		if (this.field_6004 == 0.0F && this.field_5982 == 0.0F) {
			float f = class_3532.method_15368(this.field_5967 * this.field_5967 + this.field_6006 * this.field_6006);
			this.field_6031 = (float)(class_3532.method_15349(this.field_5967, this.field_6006) * 180.0F / (float)Math.PI);
			this.field_5965 = (float)(class_3532.method_15349(this.field_5984, (double)f) * 180.0F / (float)Math.PI);
			this.field_5982 = this.field_6031;
			this.field_6004 = this.field_5965;
		}

		class_2338 lv = new class_2338(this.field_7585, this.field_7582, this.field_7583);
		class_2680 lv2 = this.field_6002.method_8320(lv);
		if (!lv2.method_11588() && !bl) {
			class_265 lv3 = lv2.method_11628(this.field_6002, lv);
			if (!lv3.method_1110()) {
				for (class_238 lv4 : lv3.method_1090()) {
					if (lv4.method_996(lv).method_1006(new class_243(this.field_5987, this.field_6010, this.field_6035))) {
						this.field_7588 = true;
						break;
					}
				}
			}
		}

		if (this.field_7574 > 0) {
			this.field_7574--;
		}

		if (this.method_5721()) {
			this.method_5646();
		}

		if (this.field_7588 && !bl) {
			if (this.field_7586 != lv2 && this.field_6002.method_8587(null, this.method_5829().method_1014(0.05))) {
				this.field_7588 = false;
				this.field_5967 = this.field_5967 * (double)(this.field_5974.nextFloat() * 0.2F);
				this.field_5984 = this.field_5984 * (double)(this.field_5974.nextFloat() * 0.2F);
				this.field_6006 = this.field_6006 * (double)(this.field_5974.nextFloat() * 0.2F);
				this.field_7578 = 0;
				this.field_7577 = 0;
			} else {
				this.method_7446();
			}

			this.field_7576++;
		} else {
			this.field_7576 = 0;
			this.field_7577++;
			class_243 lv5 = new class_243(this.field_5987, this.field_6010, this.field_6035);
			class_243 lv6 = new class_243(this.field_5987 + this.field_5967, this.field_6010 + this.field_5984, this.field_6035 + this.field_6006);
			class_239 lv7 = this.field_6002.method_8531(lv5, lv6, class_242.field_1348, true, false);
			lv5 = new class_243(this.field_5987, this.field_6010, this.field_6035);
			lv6 = new class_243(this.field_5987 + this.field_5967, this.field_6010 + this.field_5984, this.field_6035 + this.field_6006);
			if (lv7 != null) {
				lv6 = new class_243(lv7.field_1329.field_1352, lv7.field_1329.field_1351, lv7.field_1329.field_1350);
			}

			while (!this.field_5988) {
				class_1297 lv8 = this.method_7434(lv5, lv6);
				if (lv8 != null) {
					lv7 = new class_239(lv8);
				}

				if (lv7 != null && lv7.field_1326 instanceof class_1657) {
					class_1657 lv9 = (class_1657)lv7.field_1326;
					class_1297 lv10 = this.method_7452();
					if (lv10 instanceof class_1657 && !((class_1657)lv10).method_7256(lv9)) {
						lv7 = null;
					}
				}

				if (lv7 != null && !bl) {
					this.method_7457(lv7);
					this.field_6007 = true;
				}

				if (lv8 == null || this.method_7447() <= 0) {
					break;
				}

				lv7 = null;
			}

			if (this.method_7443()) {
				for (int i = 0; i < 4; i++) {
					this.field_6002
						.method_8406(
							class_2398.field_11205,
							this.field_5987 + this.field_5967 * (double)i / 4.0,
							this.field_6010 + this.field_5984 * (double)i / 4.0,
							this.field_6035 + this.field_6006 * (double)i / 4.0,
							-this.field_5967,
							-this.field_5984 + 0.2,
							-this.field_6006
						);
				}
			}

			this.field_5987 = this.field_5987 + this.field_5967;
			this.field_6010 = this.field_6010 + this.field_5984;
			this.field_6035 = this.field_6035 + this.field_6006;
			float g = class_3532.method_15368(this.field_5967 * this.field_5967 + this.field_6006 * this.field_6006);
			if (bl) {
				this.field_6031 = (float)(class_3532.method_15349(-this.field_5967, -this.field_6006) * 180.0F / (float)Math.PI);
			} else {
				this.field_6031 = (float)(class_3532.method_15349(this.field_5967, this.field_6006) * 180.0F / (float)Math.PI);
			}

			this.field_5965 = (float)(class_3532.method_15349(this.field_5984, (double)g) * 180.0F / (float)Math.PI);

			while (this.field_5965 - this.field_6004 < -180.0F) {
				this.field_6004 -= 360.0F;
			}

			while (this.field_5965 - this.field_6004 >= 180.0F) {
				this.field_6004 += 360.0F;
			}

			while (this.field_6031 - this.field_5982 < -180.0F) {
				this.field_5982 -= 360.0F;
			}

			while (this.field_6031 - this.field_5982 >= 180.0F) {
				this.field_5982 += 360.0F;
			}

			this.field_5965 = class_3532.method_16439(0.2F, this.field_6004, this.field_5965);
			this.field_6031 = class_3532.method_16439(0.2F, this.field_5982, this.field_6031);
			float h = 0.99F;
			float j = 0.05F;
			if (this.method_5799()) {
				for (int k = 0; k < 4; k++) {
					float l = 0.25F;
					this.field_6002
						.method_8406(
							class_2398.field_11247,
							this.field_5987 - this.field_5967 * 0.25,
							this.field_6010 - this.field_5984 * 0.25,
							this.field_6035 - this.field_6006 * 0.25,
							this.field_5967,
							this.field_5984,
							this.field_6006
						);
				}

				h = this.method_7436();
			}

			this.field_5967 *= (double)h;
			this.field_5984 *= (double)h;
			this.field_6006 *= (double)h;
			if (!this.method_5740() && !bl) {
				this.field_5984 -= 0.05F;
			}

			this.method_5814(this.field_5987, this.field_6010, this.field_6035);
			this.method_5852();
		}
	}

	protected void method_7446() {
		this.field_7578++;
		if (this.field_7578 >= 1200) {
			this.method_5650();
		}
	}

	protected void method_7457(class_239 arg) {
		if (arg.field_1326 != null) {
			this.method_7454(arg);
		} else {
			class_2338 lv = arg.method_1015();
			this.field_7585 = lv.method_10263();
			this.field_7582 = lv.method_10264();
			this.field_7583 = lv.method_10260();
			class_2680 lv2 = this.field_6002.method_8320(lv);
			this.field_7586 = lv2;
			this.field_5967 = (double)((float)(arg.field_1329.field_1352 - this.field_5987));
			this.field_5984 = (double)((float)(arg.field_1329.field_1351 - this.field_6010));
			this.field_6006 = (double)((float)(arg.field_1329.field_1350 - this.field_6035));
			float f = class_3532.method_15368(this.field_5967 * this.field_5967 + this.field_5984 * this.field_5984 + this.field_6006 * this.field_6006) * 20.0F;
			this.field_5987 = this.field_5987 - this.field_5967 / (double)f;
			this.field_6010 = this.field_6010 - this.field_5984 / (double)f;
			this.field_6035 = this.field_6035 - this.field_6006 / (double)f;
			this.method_5783(this.method_7440(), 1.0F, 1.2F / (this.field_5974.nextFloat() * 0.2F + 0.9F));
			this.field_7588 = true;
			this.field_7574 = 7;
			this.method_7439(false);
			this.method_7451((byte)0);
			this.method_7444(class_3417.field_15151);
			this.method_7442(false);
			this.method_7453();
			if (!lv2.method_11588()) {
				this.field_7586.method_11613(this.field_6002, lv, this);
			}
		}
	}

	private void method_7453() {
		if (this.field_7579 != null) {
			this.field_7579.clear();
		}

		if (this.field_7590 != null) {
			this.field_7590.clear();
		}
	}

	protected void method_7454(class_239 arg) {
		class_1297 lv = arg.field_1326;
		float f = class_3532.method_15368(this.field_5967 * this.field_5967 + this.field_5984 * this.field_5984 + this.field_6006 * this.field_6006);
		int i = class_3532.method_15384((double)f * this.field_7571);
		if (this.method_7447() > 0) {
			if (this.field_7590 == null) {
				this.field_7590 = new IntOpenHashSet(5);
			}

			if (this.field_7579 == null) {
				this.field_7579 = Lists.<class_1297>newArrayListWithCapacity(5);
			}

			if (this.field_7590.size() >= this.method_7447() + 1) {
				this.method_5650();
				return;
			}

			this.field_7590.add(lv.method_5628());
		}

		if (this.method_7443()) {
			i += this.field_5974.nextInt(i / 2 + 2);
		}

		class_1297 lv2 = this.method_7452();
		class_1282 lv3;
		if (lv2 == null) {
			lv3 = class_1282.method_5522(this, this);
		} else {
			lv3 = class_1282.method_5522(this, lv2);
		}

		if (this.method_5809() && !(lv instanceof class_1560)) {
			lv.method_5639(5);
		}

		if (lv.method_5643(lv3, (float)i)) {
			if (lv instanceof class_1309) {
				class_1309 lv4 = (class_1309)lv;
				if (!this.field_6002.field_9236 && this.method_7447() <= 0) {
					lv4.method_6097(lv4.method_6022() + 1);
				}

				if (this.field_7575 > 0) {
					float g = class_3532.method_15368(this.field_5967 * this.field_5967 + this.field_6006 * this.field_6006);
					if (g > 0.0F) {
						lv4.method_5762(this.field_5967 * (double)this.field_7575 * 0.6F / (double)g, 0.1, this.field_6006 * (double)this.field_7575 * 0.6F / (double)g);
					}
				}

				if (lv2 instanceof class_1309) {
					class_1890.method_8210(lv4, lv2);
					class_1890.method_8213((class_1309)lv2, lv4);
				}

				this.method_7450(lv4);
				if (lv2 != null && lv4 != lv2 && lv4 instanceof class_1657 && lv2 instanceof class_3222) {
					((class_3222)lv2).field_13987.method_14364(new class_2668(6, 0.0F));
				}

				if (!lv.method_5805() && this.field_7579 != null) {
					this.field_7579.add(lv4);
				}

				if (!this.field_6002.field_9236 && this.field_7579 != null && lv2 instanceof class_3222) {
					int j = this.field_7579.size();
					class_3222 lv5 = (class_3222)lv2;
					class_174.field_1197.method_8980(lv5, this.field_7579, j);
				}
			}

			this.method_5783(this.field_7584, 1.0F, 1.2F / (this.field_5974.nextFloat() * 0.2F + 0.9F));
			if (this.method_7447() <= 0 && !(lv instanceof class_1560)) {
				this.method_5650();
			}
		} else {
			this.field_5967 *= -0.1F;
			this.field_5984 *= -0.1F;
			this.field_6006 *= -0.1F;
			this.field_6031 += 180.0F;
			this.field_5982 += 180.0F;
			this.field_7577 = 0;
			if (!this.field_6002.field_9236 && this.field_5967 * this.field_5967 + this.field_5984 * this.field_5984 + this.field_6006 * this.field_6006 < 0.001F) {
				if (this.field_7572 == class_1665.class_1666.field_7593) {
					this.method_5699(this.method_7445(), 0.1F);
				}

				this.method_5650();
			}
		}
	}

	protected class_3414 method_7440() {
		return this.field_7584;
	}

	@Override
	public void method_5784(class_1313 arg, double d, double e, double f) {
		super.method_5784(arg, d, e, f);
		if (this.field_7588) {
			this.field_7585 = class_3532.method_15357(this.field_5987);
			this.field_7582 = class_3532.method_15357(this.field_6010);
			this.field_7583 = class_3532.method_15357(this.field_6035);
		}
	}

	protected void method_7450(class_1309 arg) {
	}

	@Nullable
	protected class_1297 method_7434(class_243 arg, class_243 arg2) {
		class_1297 lv = null;
		List<class_1297> list = this.field_6002
			.method_8333(this, this.method_5829().method_1012(this.field_5967, this.field_5984, this.field_6006).method_1014(1.0), field_7581);
		double d = 0.0;

		for (int i = 0; i < list.size(); i++) {
			class_1297 lv2 = (class_1297)list.get(i);
			if ((lv2 != this.method_7452() || this.field_7577 >= 5) && (this.field_7590 == null || !this.field_7590.contains(lv2.method_5628()))) {
				class_238 lv3 = lv2.method_5829().method_1014(0.3F);
				class_239 lv4 = lv3.method_1004(arg, arg2);
				if (lv4 != null) {
					double e = arg.method_1025(lv4.field_1329);
					if (e < d || d == 0.0) {
						lv = lv2;
						d = e;
					}
				}
			}
		}

		return lv;
	}

	@Override
	public void method_5652(class_2487 arg) {
		arg.method_10569("xTile", this.field_7585);
		arg.method_10569("yTile", this.field_7582);
		arg.method_10569("zTile", this.field_7583);
		arg.method_10575("life", (short)this.field_7578);
		if (this.field_7586 != null) {
			arg.method_10566("inBlockState", class_2512.method_10686(this.field_7586));
		}

		arg.method_10567("shake", (byte)this.field_7574);
		arg.method_10567("inGround", (byte)(this.field_7588 ? 1 : 0));
		arg.method_10567("pickup", (byte)this.field_7572.ordinal());
		arg.method_10549("damage", this.field_7571);
		arg.method_10556("crit", this.method_7443());
		arg.method_10567("PierceLevel", this.method_7447());
		if (this.field_7587 != null) {
			arg.method_10560("OwnerUUID", this.field_7587);
		}

		arg.method_10582("SoundEvent", class_2378.field_11156.method_10221(this.field_7584).toString());
		arg.method_10556("ShotFromCrossbow", this.method_7456());
	}

	@Override
	public void method_5749(class_2487 arg) {
		this.field_7585 = arg.method_10550("xTile");
		this.field_7582 = arg.method_10550("yTile");
		this.field_7583 = arg.method_10550("zTile");
		this.field_7578 = arg.method_10568("life");
		if (arg.method_10573("inBlockState", 10)) {
			this.field_7586 = class_2512.method_10681(arg.method_10562("inBlockState"));
		}

		this.field_7574 = arg.method_10571("shake") & 255;
		this.field_7588 = arg.method_10571("inGround") == 1;
		if (arg.method_10573("damage", 99)) {
			this.field_7571 = arg.method_10574("damage");
		}

		if (arg.method_10573("pickup", 99)) {
			this.field_7572 = class_1665.class_1666.method_7458(arg.method_10571("pickup"));
		} else if (arg.method_10573("player", 99)) {
			this.field_7572 = arg.method_10577("player") ? class_1665.class_1666.field_7593 : class_1665.class_1666.field_7592;
		}

		this.method_7439(arg.method_10577("crit"));
		this.method_7451(arg.method_10571("PierceLevel"));
		if (arg.method_10576("OwnerUUID")) {
			this.field_7587 = arg.method_10584("OwnerUUID");
		}

		if (arg.method_10573("SoundEvent", 8)) {
			this.field_7584 = class_2378.field_11156.method_10223(new class_2960(arg.method_10558("SoundEvent")));
		}

		this.method_7442(arg.method_10577("ShotFromCrossbow"));
	}

	public void method_7432(@Nullable class_1297 arg) {
		this.field_7587 = arg == null ? null : arg.method_5667();
	}

	@Nullable
	public class_1297 method_7452() {
		return this.field_7587 != null && this.field_6002 instanceof class_3218 ? this.field_6002.method_14190(this.field_7587) : null;
	}

	@Override
	public void method_5694(class_1657 arg) {
		if (!this.field_6002.field_9236 && (this.field_7588 || this.method_7441()) && this.field_7574 <= 0) {
			boolean bl = this.field_7572 == class_1665.class_1666.field_7593
				|| this.field_7572 == class_1665.class_1666.field_7594 && arg.field_7503.field_7477
				|| this.method_7441() && this.method_7452().method_5667() == arg.method_5667();
			if (this.field_7572 == class_1665.class_1666.field_7593 && !arg.field_7514.method_7394(this.method_7445())) {
				bl = false;
			}

			if (bl) {
				arg.method_6103(this, 1);
				this.method_5650();
			}
		}
	}

	protected abstract class_1799 method_7445();

	@Override
	protected boolean method_5658() {
		return false;
	}

	public void method_7438(double d) {
		this.field_7571 = d;
	}

	public double method_7448() {
		return this.field_7571;
	}

	public void method_7449(int i) {
		this.field_7575 = i;
	}

	@Override
	public boolean method_5732() {
		return false;
	}

	@Override
	public float method_5751() {
		return 0.0F;
	}

	public void method_7439(boolean bl) {
		this.method_7455(1, bl);
	}

	public void method_7451(byte b) {
		this.field_6011.method_12778(field_7589, b);
	}

	private void method_7455(int i, boolean bl) {
		byte b = this.field_6011.method_12789(field_7573);
		if (bl) {
			this.field_6011.method_12778(field_7573, (byte)(b | i));
		} else {
			this.field_6011.method_12778(field_7573, (byte)(b & ~i));
		}
	}

	public boolean method_7443() {
		byte b = this.field_6011.method_12789(field_7573);
		return (b & 1) != 0;
	}

	public boolean method_7456() {
		byte b = this.field_6011.method_12789(field_7573);
		return (b & 4) != 0;
	}

	public byte method_7447() {
		return this.field_6011.method_12789(field_7589);
	}

	public void method_7435(class_1309 arg, float f) {
		int i = class_1890.method_8203(class_1893.field_9103, arg);
		int j = class_1890.method_8203(class_1893.field_9116, arg);
		this.method_7438((double)(f * 2.0F) + this.field_5974.nextGaussian() * 0.25 + (double)((float)this.field_6002.method_8407().method_5461() * 0.11F));
		if (i > 0) {
			this.method_7438(this.method_7448() + (double)i * 0.5 + 0.5);
		}

		if (j > 0) {
			this.method_7449(j);
		}

		if (class_1890.method_8203(class_1893.field_9126, arg) > 0) {
			this.method_5639(100);
		}
	}

	protected float method_7436() {
		return 0.6F;
	}

	public void method_7433(boolean bl) {
		this.field_5960 = bl;
		this.method_7455(2, bl);
	}

	public boolean method_7441() {
		return !this.field_6002.field_9236 ? this.field_5960 : (this.field_6011.method_12789(field_7573) & 2) != 0;
	}

	public void method_7442(boolean bl) {
		this.method_7455(4, bl);
	}

	public static enum class_1666 {
		field_7592,
		field_7593,
		field_7594;

		public static class_1665.class_1666 method_7458(int i) {
			if (i < 0 || i > values().length) {
				i = 0;
			}

			return values()[i];
		}
	}
}
