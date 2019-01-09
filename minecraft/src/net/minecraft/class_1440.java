package net.minecraft;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1440 extends class_1429 {
	private static final class_2940<Integer> field_6764 = class_2945.method_12791(class_1440.class, class_2943.field_13327);
	private static final class_2940<Integer> field_6771 = class_2945.method_12791(class_1440.class, class_2943.field_13327);
	private static final class_2940<Integer> field_6780 = class_2945.method_12791(class_1440.class, class_2943.field_13327);
	private static final class_2940<Byte> field_6766 = class_2945.method_12791(class_1440.class, class_2943.field_13319);
	private static final class_2940<Byte> field_6781 = class_2945.method_12791(class_1440.class, class_2943.field_13319);
	private static final class_2940<Byte> field_6768 = class_2945.method_12791(class_1440.class, class_2943.field_13319);
	private boolean field_6769;
	private boolean field_6770;
	public int field_6767;
	private double field_6776;
	private double field_6778;
	private float field_6777;
	private float field_6779;
	private float field_6774;
	private float field_6775;
	private float field_6772;
	private float field_6773;
	private static final Predicate<class_1542> field_6765 = arg -> {
		class_1792 lv = arg.method_6983().method_7909();
		return (lv == class_2246.field_10211.method_8389() || lv == class_2246.field_10183.method_8389()) && arg.method_5805() && !arg.method_6977();
	};

	public class_1440(class_1937 arg) {
		super(class_1299.field_6146, arg);
		this.method_5835(1.3F, 1.25F);
		this.field_6207 = new class_1440.class_1446(this);
		if (!this.method_6109()) {
			this.method_5952(true);
		}
	}

	public int method_6521() {
		return this.field_6011.method_12789(field_6764);
	}

	public void method_6517(int i) {
		this.field_6011.method_12778(field_6764, i);
	}

	public boolean method_6545() {
		return this.method_6533(2);
	}

	public boolean method_6535() {
		return this.method_6533(8);
	}

	public void method_6513(boolean bl) {
		this.method_6557(8, bl);
	}

	public boolean method_6514() {
		return this.method_6533(16);
	}

	public void method_6505(boolean bl) {
		this.method_6557(16, bl);
	}

	public boolean method_6527() {
		return this.field_6011.method_12789(field_6780) > 0;
	}

	public void method_6552(boolean bl) {
		this.field_6011.method_12778(field_6780, bl ? 1 : 0);
	}

	private int method_6528() {
		return this.field_6011.method_12789(field_6780);
	}

	private void method_6558(int i) {
		this.field_6011.method_12778(field_6780, i);
	}

	public void method_6546(boolean bl) {
		this.method_6557(2, bl);
		if (!bl) {
			this.method_6539(0);
		}
	}

	public int method_6532() {
		return this.field_6011.method_12789(field_6771);
	}

	public void method_6539(int i) {
		this.field_6011.method_12778(field_6771, i);
	}

	public class_1440.class_1443 method_6525() {
		return class_1440.class_1443.method_6566(this.field_6011.method_12789(field_6766));
	}

	public void method_6529(class_1440.class_1443 arg) {
		if (arg.method_6564() > 6) {
			arg = this.method_6543();
		}

		this.field_6011.method_12778(field_6766, (byte)arg.method_6564());
	}

	public class_1440.class_1443 method_6508() {
		return class_1440.class_1443.method_6566(this.field_6011.method_12789(field_6781));
	}

	public void method_6547(class_1440.class_1443 arg) {
		if (arg.method_6564() > 6) {
			arg = this.method_6543();
		}

		this.field_6011.method_12778(field_6781, (byte)arg.method_6564());
	}

	public boolean method_6526() {
		return this.method_6533(4);
	}

	public void method_6541(boolean bl) {
		this.method_6557(4, bl);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_6764, 0);
		this.field_6011.method_12784(field_6771, 0);
		this.field_6011.method_12784(field_6766, (byte)0);
		this.field_6011.method_12784(field_6781, (byte)0);
		this.field_6011.method_12784(field_6768, (byte)0);
		this.field_6011.method_12784(field_6780, 0);
	}

	private boolean method_6533(int i) {
		return (this.field_6011.method_12789(field_6768) & i) != 0;
	}

	private void method_6557(int i, boolean bl) {
		byte b = this.field_6011.method_12789(field_6768);
		if (bl) {
			this.field_6011.method_12778(field_6768, (byte)(b | i));
		} else {
			this.field_6011.method_12778(field_6768, (byte)(b & ~i));
		}
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10582("MainGene", this.method_6525().method_6565());
		arg.method_10582("HiddenGene", this.method_6508().method_6565());
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.method_6529(class_1440.class_1443.method_6567(arg.method_10558("MainGene")));
		this.method_6547(class_1440.class_1443.method_6567(arg.method_10558("HiddenGene")));
	}

	@Nullable
	@Override
	public class_1296 method_5613(class_1296 arg) {
		class_1440 lv = new class_1440(this.field_6002);
		if (arg instanceof class_1440) {
			lv.method_6515(this, (class_1440)arg);
		}

		lv.method_6538();
		return lv;
	}

	@Override
	protected void method_5959() {
		this.field_6201.method_6277(0, new class_1347(this));
		this.field_6201.method_6277(2, new class_1440.class_1447(this, 2.0));
		this.field_6201.method_6277(2, new class_1440.class_1442(this, 1.0));
		this.field_6201.method_6277(3, new class_1366(this, 1.2F, true));
		this.field_6201.method_6277(4, new class_1391(this, 1.0, class_1856.method_8091(class_2246.field_10211.method_8389()), false));
		this.field_6201.method_6277(6, new class_1440.class_1441(this, class_1657.class, 8.0F, 2.0, 2.0));
		this.field_6201.method_6277(6, new class_1440.class_1441(this, class_1588.class, 4.0F, 2.0, 2.0));
		this.field_6201.method_6277(7, new class_1440.class_1449());
		this.field_6201.method_6277(8, new class_1440.class_1445(this));
		this.field_6201.method_6277(8, new class_1440.class_1450(this));
		this.field_6201.method_6277(9, new class_1361(this, class_1657.class, 6.0F));
		this.field_6201.method_6277(10, new class_1376(this));
		this.field_6201.method_6277(12, new class_1440.class_1448(this));
		this.field_6201.method_6277(13, new class_1353(this, 1.25));
		this.field_6201.method_6277(14, new class_1394(this, 1.0));
		this.field_6185.method_6277(1, new class_1440.class_1444(this).method_6318(new Class[0]));
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7357).method_6192(0.15F);
		this.method_6127().method_6208(class_1612.field_7363).method_6192(6.0);
	}

	public class_1440.class_1443 method_6554() {
		return class_1440.class_1443.method_6569(this.method_6525(), this.method_6508());
	}

	public boolean method_6549() {
		return this.method_6554() == class_1440.class_1443.field_6794;
	}

	public boolean method_6509() {
		return this.method_6554() == class_1440.class_1443.field_6795;
	}

	public boolean method_6522() {
		return this.method_6554() == class_1440.class_1443.field_6791;
	}

	public boolean method_6550() {
		return this.method_6554() == class_1440.class_1443.field_6793;
	}

	public boolean method_6510() {
		return this.method_6554() == class_1440.class_1443.field_6789;
	}

	private class_1440.class_1443 method_6543() {
		int i = this.field_5974.nextInt(16);
		if (i == 0) {
			return class_1440.class_1443.field_6794;
		} else if (i == 1) {
			return class_1440.class_1443.field_6795;
		} else if (i == 2) {
			return class_1440.class_1443.field_6791;
		} else if (i == 4) {
			return class_1440.class_1443.field_6789;
		} else if (i < 9) {
			return class_1440.class_1443.field_6793;
		} else {
			return i < 11 ? class_1440.class_1443.field_6792 : class_1440.class_1443.field_6788;
		}
	}

	@Override
	public boolean method_5931(class_1657 arg) {
		return false;
	}

	@Override
	public boolean method_6121(class_1297 arg) {
		this.method_5783(class_3417.field_14552, 1.0F, 1.0F);
		if (!this.method_6510()) {
			this.field_6770 = true;
		}

		return super.method_6121(arg);
	}

	@Override
	public void method_5773() {
		super.method_5773();
		if (this.method_6509()) {
			if (this.field_6002.method_8546()) {
				this.method_6513(true);
				this.method_6552(false);
			} else if (!this.method_6527()) {
				this.method_6513(false);
			}
		}

		if (this.method_5968() == null) {
			this.field_6769 = false;
			this.field_6770 = false;
		}

		if (this.method_6521() > 0) {
			if (this.method_5968() != null) {
				this.method_5951(this.method_5968(), 90.0F, 90.0F);
			}

			if (this.method_6521() == 29 || this.method_6521() == 14) {
				this.method_5783(class_3417.field_14936, 1.0F, 1.0F);
			}

			this.method_6517(this.method_6521() - 1);
		}

		if (this.method_6545()) {
			this.method_6539(this.method_6532() + 1);
			if (this.method_6532() > 20) {
				this.method_6546(false);
				this.method_6548();
			} else if (this.method_6532() == 1) {
				this.method_5783(class_3417.field_14997, 1.0F, 1.0F);
			}
		}

		if (this.method_6526()) {
			this.method_6537();
		} else {
			this.field_6767 = 0;
		}

		if (this.method_6535()) {
			this.field_5965 = 0.0F;
		}

		this.method_6544();
		this.method_6536();
		this.method_6503();
		this.method_6523();
	}

	public boolean method_6524() {
		return this.method_6509() && this.field_6002.method_8546();
	}

	private void method_6536() {
		if (!this.method_6527()
			&& this.method_6535()
			&& !this.method_6524()
			&& !this.method_6118(class_1304.field_6173).method_7960()
			&& this.field_5974.nextInt(80) == 1) {
			this.method_6552(true);
		} else if (this.method_6118(class_1304.field_6173).method_7960() || !this.method_6535()) {
			this.method_6552(false);
		}

		if (this.method_6527()) {
			this.method_6512();
			if (!this.field_6002.field_9236 && this.method_6528() > 80 && this.field_5974.nextInt(20) == 1) {
				if (this.method_6528() > 100 && this.method_16106(this.method_6118(class_1304.field_6173))) {
					if (!this.field_6002.field_9236) {
						this.method_5673(class_1304.field_6173, class_1799.field_8037);
					}

					this.method_6513(false);
				}

				this.method_6552(false);
				return;
			}

			this.method_6558(this.method_6528() + 1);
		}
	}

	private void method_6512() {
		if (this.method_6528() % 5 == 0) {
			this.method_5783(
				class_3417.field_15106, 0.5F + 0.5F * (float)this.field_5974.nextInt(2), (this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.2F + 1.0F
			);

			for (int i = 0; i < 6; i++) {
				class_243 lv = new class_243(
					((double)this.field_5974.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, ((double)this.field_5974.nextFloat() - 0.5) * 0.1
				);
				lv = lv.method_1037(-this.field_5965 * (float) (Math.PI / 180.0));
				lv = lv.method_1024(-this.field_6031 * (float) (Math.PI / 180.0));
				double d = (double)(-this.field_5974.nextFloat()) * 0.6 - 0.3;
				class_243 lv2 = new class_243(((double)this.field_5974.nextFloat() - 0.5) * 0.8, d, 1.0 + ((double)this.field_5974.nextFloat() - 0.5) * 0.4);
				lv2 = lv2.method_1024(-this.field_6283 * (float) (Math.PI / 180.0));
				lv2 = lv2.method_1031(this.field_5987, this.field_6010 + (double)this.method_5751() + 1.0, this.field_6035);
				this.field_6002
					.method_8406(
						new class_2392(class_2398.field_11218, this.method_6118(class_1304.field_6173)),
						lv2.field_1352,
						lv2.field_1351,
						lv2.field_1350,
						lv.field_1352,
						lv.field_1351 + 0.05,
						lv.field_1350
					);
			}
		}
	}

	private void method_6544() {
		this.field_6779 = this.field_6777;
		if (this.method_6535()) {
			this.field_6777 = Math.min(1.0F, this.field_6777 + 0.15F);
		} else {
			this.field_6777 = Math.max(0.0F, this.field_6777 - 0.19F);
		}
	}

	private void method_6503() {
		this.field_6775 = this.field_6774;
		if (this.method_6514()) {
			this.field_6774 = Math.min(1.0F, this.field_6774 + 0.15F);
		} else {
			this.field_6774 = Math.max(0.0F, this.field_6774 - 0.19F);
		}
	}

	private void method_6523() {
		this.field_6773 = this.field_6772;
		if (this.method_6526()) {
			this.field_6772 = Math.min(1.0F, this.field_6772 + 0.15F);
		} else {
			this.field_6772 = Math.max(0.0F, this.field_6772 - 0.19F);
		}
	}

	@Environment(EnvType.CLIENT)
	public float method_6534(float f) {
		return class_3532.method_16439(f, this.field_6779, this.field_6777);
	}

	@Environment(EnvType.CLIENT)
	public float method_6555(float f) {
		return class_3532.method_16439(f, this.field_6775, this.field_6774);
	}

	@Environment(EnvType.CLIENT)
	public float method_6560(float f) {
		return class_3532.method_16439(f, this.field_6773, this.field_6772);
	}

	private void method_6537() {
		this.field_6767++;
		if (this.field_6767 > 32) {
			this.method_6541(false);
		} else {
			if (!this.field_6002.field_9236) {
				if (this.field_6767 == 1) {
					this.field_5984 = 0.27F;
					float f = this.field_6031 * (float) (Math.PI / 180.0);
					float g = this.method_6109() ? 0.1F : 0.2F;
					this.field_5967 = this.field_5967 - (double)(class_3532.method_15374(f) * g);
					this.field_6006 = this.field_6006 + (double)(class_3532.method_15362(f) * g);
					this.field_6776 = this.field_5967;
					this.field_6778 = this.field_6006;
				} else if ((float)this.field_6767 != 7.0F && (float)this.field_6767 != 15.0F && (float)this.field_6767 != 23.0F) {
					this.field_5967 = this.field_6776;
					this.field_6006 = this.field_6778;
				} else {
					if (this.field_5952) {
						this.field_5984 = 0.27F;
					}

					this.field_5967 = 0.0;
					this.field_6006 = 0.0;
				}
			}
		}
	}

	private void method_6548() {
		this.field_6002
			.method_8406(
				class_2398.field_11234,
				this.field_5987 - (double)(this.field_5998 + 1.0F) * 0.5 * (double)class_3532.method_15374(this.field_6283 * (float) (Math.PI / 180.0)),
				this.field_6010 + (double)this.method_5751() - 0.1F,
				this.field_6035 + (double)(this.field_5998 + 1.0F) * 0.5 * (double)class_3532.method_15362(this.field_6283 * (float) (Math.PI / 180.0)),
				this.field_5967,
				0.0,
				this.field_6006
			);
		this.method_5783(class_3417.field_15076, 1.0F, 1.0F);

		for (class_1440 lv : this.field_6002.method_8403(class_1440.class, this.method_5829().method_1014(10.0))) {
			if (!lv.method_6109() && lv.field_5952 && !lv.method_5799() && !lv.method_6514()) {
				lv.method_6043();
			}
		}

		if (this.field_5974.nextInt(700) == 0 && this.field_6002.method_8450().method_8355("doMobLoot")) {
			this.method_5706(class_1802.field_8777);
		}
	}

	@Override
	protected void method_5949(class_1542 arg) {
		if (this.method_6118(class_1304.field_6173).method_7960() && field_6765.test(arg)) {
			class_1799 lv = arg.method_6983();
			this.method_5673(class_1304.field_6173, lv);
			this.field_6187[class_1304.field_6173.method_5927()] = 2.0F;
			this.method_6103(arg, lv.method_7947());
			arg.method_5650();
		}
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		this.method_6513(false);
		return super.method_5643(arg, f);
	}

	@Override
	public void method_6015(@Nullable class_1309 arg) {
		super.method_6015(arg);
		if (arg instanceof class_1657) {
			List<class_1646> list = this.field_6002.method_8403(class_1646.class, this.method_5829().method_1014(10.0));
			boolean bl = false;

			for (class_1646 lv : list) {
				if (lv.method_5805()) {
					this.field_6002.method_8421(lv, (byte)13);
					if (!bl) {
						class_1415 lv2 = lv.method_7232();
						if (lv2 != null) {
							bl = true;
							lv2.method_6404(arg);
							int i = -1;
							if (this.method_6109()) {
								i = -3;
							}

							lv2.method_6393(((class_1657)arg).method_7334().getName(), i);
						}
					}
				}
			}
		}
	}

	@Nullable
	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		this.method_6529(this.method_6543());
		this.method_6547(this.method_6543());
		this.method_6538();
		return super.method_5943(arg, arg2, arg3, arg4, arg5);
	}

	public void method_6515(class_1440 arg, @Nullable class_1440 arg2) {
		if (arg2 == null) {
			if (this.field_5974.nextBoolean()) {
				this.method_6529(arg.method_6519());
				this.method_6547(this.method_6543());
			} else {
				this.method_6529(this.method_6543());
				this.method_6547(arg.method_6519());
			}
		} else if (this.field_5974.nextBoolean()) {
			this.method_6529(arg.method_6519());
			this.method_6547(arg2.method_6519());
		} else {
			this.method_6529(arg2.method_6519());
			this.method_6547(arg.method_6519());
		}

		if (this.field_5974.nextInt(32) == 0) {
			this.method_6529(this.method_6543());
		}

		if (this.field_5974.nextInt(32) == 0) {
			this.method_6547(this.method_6543());
		}
	}

	private class_1440.class_1443 method_6519() {
		return this.field_5974.nextBoolean() ? this.method_6525() : this.method_6508();
	}

	public void method_6538() {
		if (this.method_6550()) {
			this.method_5996(class_1612.field_7359).method_6192(10.0);
		}

		if (this.method_6549()) {
			this.method_5996(class_1612.field_7357).method_6192(0.07F);
		}
	}

	@Override
	public boolean method_5992(class_1657 arg, class_1268 arg2) {
		class_1799 lv = arg.method_5998(arg2);
		if (lv.method_7909() instanceof class_1826) {
			return super.method_5992(arg, arg2);
		} else if (this.method_6524()) {
			return false;
		} else if (this.method_6514()) {
			this.method_6505(false);
			return true;
		} else if (this.method_6481(lv)) {
			if (this.method_5968() != null) {
				this.field_6769 = true;
			}

			if (this.method_6109()) {
				this.method_6475(arg, lv);
				this.method_5620((int)((float)(-this.method_5618() / 20) * 0.1F), true);
			} else if (!this.field_6002.field_9236 && this.method_5618() == 0 && this.method_6482()) {
				this.method_6475(arg, lv);
				this.method_6480(arg);
			} else {
				if (this.field_6002.field_9236 || this.method_6535()) {
					return false;
				}

				this.method_6513(true);
				this.method_6552(true);
				class_1799 lv2 = this.method_6118(class_1304.field_6173);
				if (!lv2.method_7960() && !arg.field_7503.field_7477) {
					this.method_5775(lv2);
				}

				this.method_5673(class_1304.field_6173, new class_1799(lv.method_7909(), 1));
				this.method_6475(arg, lv);
			}

			return true;
		} else {
			return false;
		}
	}

	@Nullable
	@Override
	protected class_3414 method_5994() {
		if (this.method_6510()) {
			return class_3417.field_14801;
		} else {
			return this.method_6509() ? class_3417.field_14715 : class_3417.field_14604;
		}
	}

	@Override
	protected void method_5712(class_2338 arg, class_2680 arg2) {
		this.method_5783(class_3417.field_15035, 0.15F, 1.0F);
	}

	@Override
	public boolean method_6481(class_1799 arg) {
		return arg.method_7909() == class_2246.field_10211.method_8389();
	}

	private boolean method_16106(class_1799 arg) {
		return this.method_6481(arg) || arg.method_7909() == class_2246.field_10183.method_8389();
	}

	@Nullable
	@Override
	protected class_3414 method_6002() {
		return class_3417.field_15208;
	}

	@Nullable
	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_14668;
	}

	static class class_1441<T extends class_1297> extends class_1338<T> {
		private final class_1440 field_6782;

		public class_1441(class_1440 arg, Class<T> class_, float f, double d, double e) {
			super(arg, class_, f, d, e, class_1301.field_6155);
			this.field_6782 = arg;
		}

		@Override
		public boolean method_6264() {
			return this.field_6782.method_6509() && super.method_6264();
		}
	}

	static class class_1442 extends class_1341 {
		private final class_1440 field_6784;
		private int field_6783;

		public class_1442(class_1440 arg, double d) {
			super(arg, d);
			this.field_6784 = arg;
		}

		@Override
		public boolean method_6264() {
			if (!super.method_6264() || this.field_6784.method_6521() != 0) {
				return false;
			} else if (!this.method_6561()) {
				if (this.field_6783 <= this.field_6784.field_6012) {
					this.field_6784.method_6517(32);
					this.field_6783 = this.field_6784.field_6012 + 600;
					if (this.field_6784.method_6034()) {
						class_1657 lv = this.field_6405.method_8614(this.field_6784, 8.0);
						this.field_6784.method_5980(lv);
					}
				}

				return false;
			} else {
				return true;
			}
		}

		private boolean method_6561() {
			class_2338 lv = new class_2338(this.field_6784);
			class_2338.class_2339 lv2 = new class_2338.class_2339();

			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 8; j++) {
					for (int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
						for (int l = k < j && k > -j ? j : 0; l <= j; l = l > 0 ? -l : 1 - l) {
							lv2.method_10101(lv).method_10100(k, i, l);
							if (this.field_6405.method_8320(lv2).method_11614() == class_2246.field_10211) {
								return true;
							}
						}
					}
				}
			}

			return false;
		}
	}

	public static enum class_1443 {
		field_6788(0, "normal", false, "textures/entity/panda/panda.png"),
		field_6794(1, "lazy", false, "textures/entity/panda/lazy_panda.png"),
		field_6795(2, "worried", false, "textures/entity/panda/worried_panda.png"),
		field_6791(3, "playful", false, "textures/entity/panda/playful_panda.png"),
		field_6792(4, "brown", true, "textures/entity/panda/brown_panda.png"),
		field_6793(5, "weak", true, "textures/entity/panda/weak_panda.png"),
		field_6789(6, "aggressive", false, "textures/entity/panda/aggressive_panda.png");

		private static final class_1440.class_1443[] field_6786 = (class_1440.class_1443[])Arrays.stream(values())
			.sorted(Comparator.comparingInt(class_1440.class_1443::method_6564))
			.toArray(class_1440.class_1443[]::new);
		private final int field_6785;
		private final String field_6797;
		private final boolean field_6790;
		private final class_2960 field_6787;

		private class_1443(int j, String string2, boolean bl, String string3) {
			this.field_6785 = j;
			this.field_6797 = string2;
			this.field_6790 = bl;
			this.field_6787 = new class_2960(string3);
		}

		public int method_6564() {
			return this.field_6785;
		}

		public String method_6565() {
			return this.field_6797;
		}

		public boolean method_6568() {
			return this.field_6790;
		}

		private static class_1440.class_1443 method_6569(class_1440.class_1443 arg, class_1440.class_1443 arg2) {
			if (arg.method_6568()) {
				return arg == arg2 ? arg : field_6788;
			} else {
				return arg;
			}
		}

		@Environment(EnvType.CLIENT)
		public class_2960 method_6562() {
			return this.field_6787;
		}

		public static class_1440.class_1443 method_6566(int i) {
			if (i < 0 || i >= field_6786.length) {
				i = 0;
			}

			return field_6786[i];
		}

		public static class_1440.class_1443 method_6567(String string) {
			for (class_1440.class_1443 lv : values()) {
				if (lv.field_6797.equals(string)) {
					return lv;
				}
			}

			return field_6788;
		}
	}

	static class class_1444 extends class_1399 {
		private final class_1440 field_6798;

		public class_1444(class_1440 arg, Class<?>... classs) {
			super(arg, classs);
			this.field_6798 = arg;
		}

		@Override
		public boolean method_6266() {
			if (!this.field_6798.field_6769 && !this.field_6798.field_6770) {
				return super.method_6266();
			} else {
				this.field_6798.method_5980(null);
				return false;
			}
		}

		@Override
		protected void method_6319(class_1314 arg, class_1309 arg2) {
			if (arg instanceof class_1440 && ((class_1440)arg).method_6510()) {
				arg.method_5980(arg2);
			}
		}
	}

	static class class_1445 extends class_1352 {
		private final class_1440 field_6800;
		private int field_6799;

		public class_1445(class_1440 arg) {
			this.field_6800 = arg;
		}

		@Override
		public boolean method_6264() {
			return this.field_6799 < this.field_6800.field_6012
				&& this.field_6800.method_6549()
				&& !this.field_6800.method_6535()
				&& !this.field_6800.method_6526()
				&& this.field_6800.field_5974.nextInt(400) == 1;
		}

		@Override
		public boolean method_6266() {
			return !this.field_6800.method_5799() && (this.field_6800.method_6549() || this.field_6800.field_5974.nextInt(600) != 1)
				? this.field_6800.field_5974.nextInt(2000) != 1
				: false;
		}

		@Override
		public void method_6269() {
			this.field_6800.method_6505(true);
			this.field_6799 = 0;
		}

		@Override
		public void method_6270() {
			this.field_6800.method_6505(false);
			this.field_6799 = this.field_6800.field_6012 + 200;
		}
	}

	static class class_1446 extends class_1335 {
		private final class_1440 field_6801;

		public class_1446(class_1440 arg) {
			super(arg);
			this.field_6801 = arg;
		}

		@Override
		public void method_6240() {
			if (!this.field_6801.method_6535() && !this.field_6801.method_6514() && !this.field_6801.method_6524()) {
				super.method_6240();
			}
		}
	}

	static class class_1447 extends class_1374 {
		private final class_1440 field_6802;

		public class_1447(class_1440 arg, double d) {
			super(arg, d);
			this.field_6802 = arg;
		}

		@Override
		public boolean method_6264() {
			if (!this.field_6802.method_5809()) {
				return false;
			} else {
				class_2338 lv = this.method_6300(this.field_6549.field_6002, this.field_6549, 5, 4);
				if (lv != null) {
					this.field_6547 = (double)lv.method_10263();
					this.field_6546 = (double)lv.method_10264();
					this.field_6550 = (double)lv.method_10260();
					return true;
				} else {
					return this.method_6301();
				}
			}
		}

		@Override
		public boolean method_6266() {
			if (this.field_6802.method_6535()) {
				this.field_6802.method_5942().method_6340();
				return false;
			} else {
				return super.method_6266();
			}
		}
	}

	static class class_1448 extends class_1352 {
		private final class_1440 field_6803;

		public class_1448(class_1440 arg) {
			this.field_6803 = arg;
			this.method_6265(7);
		}

		@Override
		public boolean method_6264() {
			if ((this.field_6803.method_6109() || this.field_6803.method_6522())
				&& this.field_6803.field_5952
				&& !this.field_6803.method_6514()
				&& !this.field_6803.method_6545()) {
				float f = this.field_6803.field_6031 * (float) (Math.PI / 180.0);
				int i = 0;
				int j = 0;
				float g = -class_3532.method_15374(f);
				float h = class_3532.method_15362(f);
				if ((double)Math.abs(g) > 0.5) {
					i = (int)((float)i + g / Math.abs(g));
				}

				if ((double)Math.abs(h) > 0.5) {
					j = (int)((float)j + h / Math.abs(h));
				}

				if (this.field_6803.field_6002.method_8320(new class_2338(this.field_6803).method_10069(i, -1, j)).method_11588()) {
					return true;
				} else {
					return this.field_6803.method_6522() && this.field_6803.field_5974.nextInt(60) == 1 ? true : this.field_6803.field_5974.nextInt(500) == 1;
				}
			} else {
				return false;
			}
		}

		@Override
		public boolean method_6266() {
			return false;
		}

		@Override
		public void method_6269() {
			this.field_6803.method_6541(true);
		}

		@Override
		public boolean method_6267() {
			return false;
		}
	}

	class class_1449 extends class_1352 {
		private int field_6804;

		public class_1449() {
			this.method_6265(1);
		}

		@Override
		public boolean method_6264() {
			if (this.field_6804 <= class_1440.this.field_6012
				&& !class_1440.this.method_6109()
				&& !class_1440.this.method_6535()
				&& !class_1440.this.method_5799()
				&& !class_1440.this.method_6514()
				&& class_1440.this.method_6521() <= 0) {
				List<class_1542> list = class_1440.this.field_6002
					.method_8390(class_1542.class, class_1440.this.method_5829().method_1009(6.0, 6.0, 6.0), class_1440.field_6765);
				return !list.isEmpty() || !class_1440.this.method_6118(class_1304.field_6173).method_7960();
			} else {
				return false;
			}
		}

		@Override
		public boolean method_6266() {
			return !class_1440.this.method_5799() && (class_1440.this.method_6549() || class_1440.this.field_5974.nextInt(600) != 1)
				? class_1440.this.field_5974.nextInt(2000) != 1
				: false;
		}

		@Override
		public void method_6268() {
			if (!class_1440.this.method_6535() && !class_1440.this.method_6118(class_1304.field_6173).method_7960()) {
				class_1440.this.method_6513(true);
			}
		}

		@Override
		public void method_6269() {
			List<class_1542> list = class_1440.this.field_6002
				.method_8390(class_1542.class, class_1440.this.method_5829().method_1009(8.0, 8.0, 8.0), class_1440.field_6765);
			if (!list.isEmpty() && class_1440.this.method_6118(class_1304.field_6173).method_7960()) {
				class_1440.this.method_5942().method_6335((class_1297)list.get(0), 1.2F);
			} else if (!class_1440.this.method_6118(class_1304.field_6173).method_7960()) {
				class_1440.this.method_6513(true);
			}

			this.field_6804 = 0;
		}

		@Override
		public void method_6270() {
			class_1799 lv = class_1440.this.method_6118(class_1304.field_6173);
			if (!lv.method_7960()) {
				class_1440.this.method_5775(lv);
				class_1440.this.method_5673(class_1304.field_6173, class_1799.field_8037);
				int i = class_1440.this.method_6549() ? class_1440.this.field_5974.nextInt(50) + 10 : class_1440.this.field_5974.nextInt(150) + 10;
				this.field_6804 = class_1440.this.field_6012 + i * 20;
			}

			class_1440.this.method_6513(false);
		}
	}

	static class class_1450 extends class_1352 {
		private final class_1440 field_6806;

		public class_1450(class_1440 arg) {
			this.field_6806 = arg;
		}

		@Override
		public boolean method_6264() {
			if (this.field_6806.method_6109() && !this.field_6806.method_6526()) {
				return this.field_6806.method_6550() && this.field_6806.field_5974.nextInt(500) == 1 ? true : this.field_6806.field_5974.nextInt(6000) == 1;
			} else {
				return false;
			}
		}

		@Override
		public boolean method_6266() {
			return false;
		}

		@Override
		public void method_6269() {
			this.field_6806.method_6546(true);
		}
	}
}
