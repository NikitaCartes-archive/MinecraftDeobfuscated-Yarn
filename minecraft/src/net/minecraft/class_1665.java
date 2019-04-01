package net.minecraft;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_1665 extends class_1297 implements class_1676 {
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
	private class_3414 field_7584 = this.method_7440();
	private IntOpenHashSet field_7590;
	private List<class_1297> field_7579;

	protected class_1665(class_1299<? extends class_1665> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	protected class_1665(class_1299<? extends class_1665> arg, double d, double e, double f, class_1937 arg2) {
		this(arg, arg2);
		this.method_5814(d, e, f);
	}

	protected class_1665(class_1299<? extends class_1665> arg, class_1309 arg2, class_1937 arg3) {
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

	public void method_7474(class_1297 arg, float f, float g, float h, float i, float j) {
		float k = -class_3532.method_15374(g * (float) (Math.PI / 180.0)) * class_3532.method_15362(f * (float) (Math.PI / 180.0));
		float l = -class_3532.method_15374(f * (float) (Math.PI / 180.0));
		float m = class_3532.method_15362(g * (float) (Math.PI / 180.0)) * class_3532.method_15362(f * (float) (Math.PI / 180.0));
		this.method_7485((double)k, (double)l, (double)m, i, j);
		this.method_18799(
			this.method_18798().method_1031(arg.method_18798().field_1352, arg.field_5952 ? 0.0 : arg.method_18798().field_1351, arg.method_18798().field_1350)
		);
	}

	@Override
	public void method_7485(double d, double e, double f, float g, float h) {
		class_243 lv = new class_243(d, e, f)
			.method_1029()
			.method_1031(
				this.field_5974.nextGaussian() * 0.0075F * (double)h,
				this.field_5974.nextGaussian() * 0.0075F * (double)h,
				this.field_5974.nextGaussian() * 0.0075F * (double)h
			)
			.method_1021((double)g);
		this.method_18799(lv);
		float i = class_3532.method_15368(method_17996(lv));
		this.field_6031 = (float)(class_3532.method_15349(lv.field_1352, lv.field_1350) * 180.0F / (float)Math.PI);
		this.field_5965 = (float)(class_3532.method_15349(lv.field_1351, (double)i) * 180.0F / (float)Math.PI);
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
		this.method_18800(d, e, f);
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
		class_243 lv = this.method_18798();
		if (this.field_6004 == 0.0F && this.field_5982 == 0.0F) {
			float f = class_3532.method_15368(method_17996(lv));
			this.field_6031 = (float)(class_3532.method_15349(lv.field_1352, lv.field_1350) * 180.0F / (float)Math.PI);
			this.field_5965 = (float)(class_3532.method_15349(lv.field_1351, (double)f) * 180.0F / (float)Math.PI);
			this.field_5982 = this.field_6031;
			this.field_6004 = this.field_5965;
		}

		class_2338 lv2 = new class_2338(this.field_7585, this.field_7582, this.field_7583);
		class_2680 lv3 = this.field_6002.method_8320(lv2);
		if (!lv3.method_11588() && !bl) {
			class_265 lv4 = lv3.method_11628(this.field_6002, lv2);
			if (!lv4.method_1110()) {
				for (class_238 lv5 : lv4.method_1090()) {
					if (lv5.method_996(lv2).method_1006(new class_243(this.field_5987, this.field_6010, this.field_6035))) {
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
			if (this.field_7586 != lv3 && this.field_6002.method_18026(this.method_5829().method_1014(0.05))) {
				this.field_7588 = false;
				this.method_18799(
					lv.method_18805((double)(this.field_5974.nextFloat() * 0.2F), (double)(this.field_5974.nextFloat() * 0.2F), (double)(this.field_5974.nextFloat() * 0.2F))
				);
				this.field_7578 = 0;
				this.field_7577 = 0;
			} else {
				this.method_7446();
			}

			this.field_7576++;
		} else {
			this.field_7576 = 0;
			this.field_7577++;
			class_243 lv6 = new class_243(this.field_5987, this.field_6010, this.field_6035);
			class_243 lv7 = lv6.method_1019(lv);
			class_239 lv8 = this.field_6002.method_17742(new class_3959(lv6, lv7, class_3959.class_3960.field_17558, class_3959.class_242.field_1348, this));
			if (lv8.method_17783() != class_239.class_240.field_1333) {
				lv7 = lv8.method_17784();
			}

			while (!this.field_5988) {
				class_3966 lv9 = this.method_7434(lv6, lv7);
				if (lv9 != null) {
					lv8 = lv9;
				}

				if (lv8 != null && lv8.method_17783() == class_239.class_240.field_1331) {
					class_1297 lv10 = ((class_3966)lv8).method_17782();
					class_1297 lv11 = this.method_7452();
					if (lv10 instanceof class_1657 && lv11 instanceof class_1657 && !((class_1657)lv11).method_7256((class_1657)lv10)) {
						lv8 = null;
					}
				}

				if (lv8 != null && !bl) {
					this.method_7457(lv8);
					this.field_6007 = true;
				}

				if (lv9 == null || this.method_7447() <= 0) {
					break;
				}

				lv8 = null;
			}

			lv = this.method_18798();
			double d = lv.field_1352;
			double e = lv.field_1351;
			double g = lv.field_1350;
			if (this.method_7443()) {
				for (int i = 0; i < 4; i++) {
					this.field_6002
						.method_8406(
							class_2398.field_11205,
							this.field_5987 + d * (double)i / 4.0,
							this.field_6010 + e * (double)i / 4.0,
							this.field_6035 + g * (double)i / 4.0,
							-d,
							-e + 0.2,
							-g
						);
				}
			}

			this.field_5987 += d;
			this.field_6010 += e;
			this.field_6035 += g;
			float h = class_3532.method_15368(method_17996(lv));
			if (bl) {
				this.field_6031 = (float)(class_3532.method_15349(-d, -g) * 180.0F / (float)Math.PI);
			} else {
				this.field_6031 = (float)(class_3532.method_15349(d, g) * 180.0F / (float)Math.PI);
			}

			this.field_5965 = (float)(class_3532.method_15349(e, (double)h) * 180.0F / (float)Math.PI);

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
			float j = 0.99F;
			float k = 0.05F;
			if (this.method_5799()) {
				for (int l = 0; l < 4; l++) {
					float m = 0.25F;
					this.field_6002.method_8406(class_2398.field_11247, this.field_5987 - d * 0.25, this.field_6010 - e * 0.25, this.field_6035 - g * 0.25, d, e, g);
				}

				j = this.method_7436();
			}

			this.method_18799(lv.method_1021((double)j));
			if (!this.method_5740() && !bl) {
				class_243 lv12 = this.method_18798();
				this.method_18800(lv12.field_1352, lv12.field_1351 - 0.05F, lv12.field_1350);
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
		class_239.class_240 lv = arg.method_17783();
		if (lv == class_239.class_240.field_1331) {
			this.method_7454((class_3966)arg);
		} else if (lv == class_239.class_240.field_1332) {
			class_3965 lv2 = (class_3965)arg;
			class_2338 lv3 = lv2.method_17777();
			this.field_7585 = lv3.method_10263();
			this.field_7582 = lv3.method_10264();
			this.field_7583 = lv3.method_10260();
			class_2680 lv4 = this.field_6002.method_8320(lv3);
			this.field_7586 = lv4;
			class_243 lv5 = lv2.method_17784().method_1023(this.field_5987, this.field_6010, this.field_6035);
			this.method_18799(lv5);
			class_243 lv6 = lv5.method_1029().method_1021(0.05F);
			this.field_5987 = this.field_5987 - lv6.field_1352;
			this.field_6010 = this.field_6010 - lv6.field_1351;
			this.field_6035 = this.field_6035 - lv6.field_1350;
			this.method_5783(this.method_20011(), 1.0F, 1.2F / (this.field_5974.nextFloat() * 0.2F + 0.9F));
			this.field_7588 = true;
			this.field_7574 = 7;
			this.method_7439(false);
			this.method_7451((byte)0);
			this.method_7444(class_3417.field_15151);
			this.method_7442(false);
			this.method_7453();
			lv4.method_19287(this.field_6002, lv4, lv2, this);
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

	protected void method_7454(class_3966 arg) {
		class_1297 lv = arg.method_17782();
		float f = (float)this.method_18798().method_1033();
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
			if (lv2 instanceof class_1309) {
				((class_1309)lv2).method_6114(lv);
			}
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
					class_243 lv5 = this.method_18798().method_18805(1.0, 0.0, 1.0).method_1029().method_1021((double)this.field_7575 * 0.6);
					if (lv5.method_1027() > 0.0) {
						lv4.method_5762(lv5.field_1352, 0.1, lv5.field_1350);
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

				if (!this.field_6002.field_9236 && lv2 instanceof class_3222) {
					class_3222 lv6 = (class_3222)lv2;
					if (this.field_7579 != null && this.method_7456()) {
						class_174.field_1197.method_8980(lv6, this.field_7579, this.field_7579.size());
					} else if (!lv.method_5805() && this.method_7456()) {
						class_174.field_1197.method_8980(lv6, Arrays.asList(lv), 0);
					}
				}
			}

			this.method_5783(this.field_7584, 1.0F, 1.2F / (this.field_5974.nextFloat() * 0.2F + 0.9F));
			if (this.method_7447() <= 0 && !(lv instanceof class_1560)) {
				this.method_5650();
			}
		} else {
			this.method_18799(this.method_18798().method_1021(-0.1));
			this.field_6031 += 180.0F;
			this.field_5982 += 180.0F;
			this.field_7577 = 0;
			if (!this.field_6002.field_9236 && this.method_18798().method_1027() < 1.0E-7) {
				if (this.field_7572 == class_1665.class_1666.field_7593) {
					this.method_5699(this.method_7445(), 0.1F);
				}

				this.method_5650();
			}
		}
	}

	protected class_3414 method_7440() {
		return class_3417.field_15151;
	}

	protected final class_3414 method_20011() {
		return this.field_7584;
	}

	@Override
	public void method_5784(class_1313 arg, class_243 arg2) {
		super.method_5784(arg, arg2);
		if (this.field_7588) {
			this.field_7585 = class_3532.method_15357(this.field_5987);
			this.field_7582 = class_3532.method_15357(this.field_6010);
			this.field_7583 = class_3532.method_15357(this.field_6035);
		}
	}

	protected void method_7450(class_1309 arg) {
	}

	@Nullable
	protected class_3966 method_7434(class_243 arg, class_243 arg2) {
		return class_1675.method_18077(
			this.field_6002,
			this,
			arg,
			arg2,
			this.method_5829().method_18804(this.method_18798()).method_1014(1.0),
			argx -> !argx.method_7325()
					&& argx.method_5805()
					&& argx.method_5863()
					&& (argx != this.method_7452() || this.field_7577 >= 5)
					&& (this.field_7590 == null || !this.field_7590.contains(argx.method_5628()))
		);
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
			this.field_7584 = (class_3414)class_2378.field_11156.method_17966(new class_2960(arg.method_10558("SoundEvent"))).orElse(this.method_7440());
		}

		this.method_7442(arg.method_10577("ShotFromCrossbow"));
	}

	public void method_7432(@Nullable class_1297 arg) {
		this.field_7587 = arg == null ? null : arg.method_5667();
		if (arg instanceof class_1657) {
			this.field_7572 = ((class_1657)arg).field_7503.field_7477 ? class_1665.class_1666.field_7594 : class_1665.class_1666.field_7593;
		}
	}

	@Nullable
	public class_1297 method_7452() {
		return this.field_7587 != null && this.field_6002 instanceof class_3218 ? ((class_3218)this.field_6002).method_14190(this.field_7587) : null;
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
	protected float method_18378(class_4050 arg, class_4048 arg2) {
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

	@Override
	public class_2596<?> method_18002() {
		class_1297 lv = this.method_7452();
		return new class_2604(this, lv == null ? 0 : lv.method_5628());
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
