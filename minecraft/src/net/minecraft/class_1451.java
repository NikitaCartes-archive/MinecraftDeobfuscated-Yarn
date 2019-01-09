package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1451 extends class_1321 {
	private static final class_1856 field_6809 = class_1856.method_8091(class_1802.field_8429, class_1802.field_8209);
	private static final class_2940<Integer> field_6811 = class_2945.method_12791(class_1451.class, class_2943.field_13327);
	private static final class_2940<Boolean> field_16284 = class_2945.method_12791(class_1451.class, class_2943.field_13323);
	private static final class_2940<Boolean> field_16292 = class_2945.method_12791(class_1451.class, class_2943.field_13323);
	private static final class_2940<Integer> field_16285 = class_2945.method_12791(class_1451.class, class_2943.field_13327);
	public static final Map<Integer, class_2960> field_16283 = class_156.method_654(Maps.<Integer, class_2960>newHashMap(), hashMap -> {
		hashMap.put(0, new class_2960("textures/entity/cat/tabby.png"));
		hashMap.put(1, new class_2960("textures/entity/cat/black.png"));
		hashMap.put(2, new class_2960("textures/entity/cat/red.png"));
		hashMap.put(3, new class_2960("textures/entity/cat/siamese.png"));
		hashMap.put(4, new class_2960("textures/entity/cat/british_shorthair.png"));
		hashMap.put(5, new class_2960("textures/entity/cat/calico.png"));
		hashMap.put(6, new class_2960("textures/entity/cat/persian.png"));
		hashMap.put(7, new class_2960("textures/entity/cat/ragdoll.png"));
		hashMap.put(8, new class_2960("textures/entity/cat/white.png"));
		hashMap.put(9, new class_2960("textures/entity/cat/jellie.png"));
		hashMap.put(10, new class_2960("textures/entity/cat/all_black.png"));
	});
	private class_1451.class_3698<class_1657> field_6808;
	private class_1391 field_6810;
	private float field_16290;
	private float field_16291;
	private float field_16288;
	private float field_16289;
	private float field_16286;
	private float field_16287;

	public class_1451(class_1937 arg) {
		super(class_1299.field_16281, arg);
		this.method_5835(0.6F, 0.7F);
	}

	public class_2960 method_16092() {
		return (class_2960)field_16283.get(this.method_6571());
	}

	@Override
	protected void method_5959() {
		this.field_6321 = new class_1386(this);
		this.field_6810 = new class_1451.class_3700(this, 0.6, field_6809, true);
		this.field_6201.method_6277(1, new class_1347(this));
		this.field_6201.method_6277(1, new class_1451.class_3699(this));
		this.field_6201.method_6277(2, this.field_6321);
		this.field_6201.method_6277(3, this.field_6810);
		this.field_6201.method_6277(5, new class_3697(this, 1.1, 8));
		this.field_6201.method_6277(6, new class_1350(this, 1.0, 10.0F, 5.0F));
		this.field_6201.method_6277(7, new class_1373(this, 0.8));
		this.field_6201.method_6277(8, new class_1359(this, 0.3F));
		this.field_6201.method_6277(9, new class_1371(this));
		this.field_6201.method_6277(10, new class_1341(this, 0.8));
		this.field_6201.method_6277(11, new class_1394(this, 0.8, 1.0000001E-5F));
		this.field_6201.method_6277(12, new class_1361(this, class_1657.class, 10.0F));
		this.field_6185.method_6277(1, new class_1404(this, class_1463.class, false, null));
		this.field_6185.method_6277(1, new class_1404(this, class_1481.class, false, class_1481.field_6921));
	}

	public int method_6571() {
		return this.field_6011.method_12789(field_6811);
	}

	public void method_6572(int i) {
		this.field_6011.method_12778(field_6811, i);
	}

	public void method_16088(boolean bl) {
		this.field_6011.method_12778(field_16284, bl);
	}

	public boolean method_16086() {
		return this.field_6011.method_12789(field_16284);
	}

	public void method_16087(boolean bl) {
		this.field_6011.method_12778(field_16292, bl);
	}

	public boolean method_16093() {
		return this.field_6011.method_12789(field_16292);
	}

	public class_1767 method_16096() {
		return class_1767.method_7791(this.field_6011.method_12789(field_16285));
	}

	public void method_16094(class_1767 arg) {
		this.field_6011.method_12778(field_16285, arg.method_7789());
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_6811, 1);
		this.field_6011.method_12784(field_16284, false);
		this.field_6011.method_12784(field_16292, false);
		this.field_6011.method_12784(field_16285, class_1767.field_7964.method_7789());
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("CatType", this.method_6571());
		arg.method_10567("CollarColor", (byte)this.method_16096().method_7789());
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.method_6572(arg.method_10550("CatType"));
		if (arg.method_10573("CollarColor", 99)) {
			this.method_16094(class_1767.method_7791(arg.method_10550("CollarColor")));
		}
	}

	@Override
	public void method_5958() {
		if (this.method_5962().method_6241()) {
			double d = this.method_5962().method_6242();
			if (d == 0.6) {
				this.method_5660(true);
				this.method_5728(false);
			} else if (d == 1.33) {
				this.method_5660(false);
				this.method_5728(true);
			} else {
				this.method_5660(false);
				this.method_5728(false);
			}
		} else {
			this.method_5660(false);
			this.method_5728(false);
		}
	}

	@Nullable
	@Override
	protected class_3414 method_5994() {
		if (this.method_6181()) {
			if (this.method_6479()) {
				return class_3417.field_14741;
			} else {
				return this.field_5974.nextInt(4) == 0 ? class_3417.field_14589 : class_3417.field_15051;
			}
		} else {
			return class_3417.field_16440;
		}
	}

	@Override
	public int method_5970() {
		return 120;
	}

	public void method_16089() {
		this.method_5783(class_3417.field_14938, this.method_6107(), this.method_6017());
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_14867;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_14971;
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192(10.0);
		this.method_5996(class_1612.field_7357).method_6192(0.3F);
	}

	@Override
	public void method_5747(float f, float g) {
	}

	@Override
	protected void method_6475(class_1657 arg, class_1799 arg2) {
		if (this.method_6481(arg2)) {
			this.method_5783(class_3417.field_16439, 1.0F, 1.0F);
		}

		super.method_6475(arg, arg2);
	}

	@Override
	public boolean method_6121(class_1297 arg) {
		return arg.method_5643(class_1282.method_5511(this), 3.0F);
	}

	@Override
	public void method_5773() {
		super.method_5773();
		if (this.field_6810 != null && this.field_6810.method_6313() && !this.method_6181() && this.field_6012 % 100 == 0) {
			this.method_5783(class_3417.field_16438, 1.0F, 1.0F);
		}

		this.method_16085();
	}

	private void method_16085() {
		if ((this.method_16086() || this.method_16093()) && this.field_6012 % 5 == 0) {
			this.method_5783(class_3417.field_14741, 0.5F + this.field_5974.nextFloat() - this.field_5974.nextFloat(), 1.0F);
		}

		this.method_16090();
		this.method_16084();
	}

	private void method_16090() {
		this.field_16291 = this.field_16290;
		this.field_16289 = this.field_16288;
		if (this.method_16086()) {
			this.field_16290 = Math.min(1.0F, this.field_16290 + 0.15F);
			this.field_16288 = Math.min(1.0F, this.field_16288 + 0.08F);
		} else {
			this.field_16290 = Math.max(0.0F, this.field_16290 - 0.22F);
			this.field_16288 = Math.max(0.0F, this.field_16288 - 0.13F);
		}
	}

	private void method_16084() {
		this.field_16287 = this.field_16286;
		if (this.method_16093()) {
			this.field_16286 = Math.min(1.0F, this.field_16286 + 0.1F);
		} else {
			this.field_16286 = Math.max(0.0F, this.field_16286 - 0.13F);
		}
	}

	@Environment(EnvType.CLIENT)
	public float method_16082(float f) {
		return class_3532.method_16439(f, this.field_16291, this.field_16290);
	}

	@Environment(EnvType.CLIENT)
	public float method_16091(float f) {
		return class_3532.method_16439(f, this.field_16289, this.field_16288);
	}

	@Environment(EnvType.CLIENT)
	public float method_16095(float f) {
		return class_3532.method_16439(f, this.field_16287, this.field_16286);
	}

	public class_1451 method_6573(class_1296 arg) {
		class_1451 lv = new class_1451(this.field_6002);
		if (this.method_6181() && arg instanceof class_1451) {
			lv.method_6174(this.method_6139());
			lv.method_6173(true);
			if (this.field_5974.nextBoolean()) {
				lv.method_6572(this.method_6571());
			} else {
				lv.method_6572(((class_1451)arg).method_6571());
			}

			if (this.field_5974.nextBoolean()) {
				lv.method_16094(this.method_16096());
			} else {
				lv.method_16094(((class_1451)arg).method_16096());
			}
		}

		return lv;
	}

	@Override
	public boolean method_6474(class_1429 arg) {
		if (!this.method_6181()) {
			return false;
		} else if (!(arg instanceof class_1451)) {
			return false;
		} else {
			class_1451 lv = (class_1451)arg;
			return lv.method_6181() && super.method_6474(arg);
		}
	}

	@Nullable
	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		arg4 = super.method_5943(arg, arg2, arg3, arg4, arg5);
		if (arg.method_8391() > 0.9F) {
			this.method_6572(this.field_5974.nextInt(11));
		} else {
			this.method_6572(this.field_5974.nextInt(10));
		}

		if (class_3031.field_13520.method_14024(arg, new class_2338(this))) {
			this.method_6572(10);
			this.method_5971();
		}

		return arg4;
	}

	@Override
	public boolean method_5992(class_1657 arg, class_1268 arg2) {
		class_1799 lv = arg.method_5998(arg2);
		class_1792 lv2 = lv.method_7909();
		if (this.method_6181()) {
			if (this.method_6171(arg)) {
				if (lv2 instanceof class_1769) {
					class_1767 lv3 = ((class_1769)lv2).method_7802();
					if (lv3 != this.method_16096()) {
						this.method_16094(lv3);
						if (!arg.field_7503.field_7477) {
							lv.method_7934(1);
						}

						this.method_5971();
						return true;
					}
				} else if (!this.field_6002.field_9236 && !this.method_6481(lv)) {
					this.field_6321.method_6311(!this.method_6172());
				}
			}
		} else if (this.method_6481(lv)) {
			this.method_6475(arg, lv);
			if (!this.field_6002.field_9236) {
				if (this.field_5974.nextInt(3) == 0) {
					this.method_6170(arg);
					this.method_6180(true);
					this.field_6321.method_6311(true);
					this.field_6002.method_8421(this, (byte)7);
				} else {
					this.method_6180(false);
					this.field_6002.method_8421(this, (byte)6);
				}
			}

			this.method_5971();
			return true;
		}

		boolean bl = super.method_5992(arg, arg2);
		if (bl) {
			this.method_5971();
		}

		return bl;
	}

	@Override
	public boolean method_6481(class_1799 arg) {
		return field_6809.method_8093(arg);
	}

	@Override
	public float method_5751() {
		return this.field_6019 * 0.5F;
	}

	@Override
	public boolean method_5974(double d) {
		return !this.method_6181() && this.field_6012 > 2400;
	}

	@Override
	protected void method_6175() {
		if (this.field_6808 == null) {
			this.field_6808 = new class_1451.class_3698<>(this, class_1657.class, 16.0F, 0.8, 1.33);
		}

		this.field_6201.method_6280(this.field_6808);
		if (!this.method_6181()) {
			this.field_6201.method_6277(4, this.field_6808);
		}
	}

	static class class_3698<T extends class_1297> extends class_1338<T> {
		private final class_1451 field_16293;

		public class_3698(class_1451 arg, Class<T> class_, float f, double d, double e) {
			super(arg, class_, f, d, e, class_1301.field_6155);
			this.field_16293 = arg;
		}

		@Override
		public boolean method_6264() {
			return !this.field_16293.method_6181() && super.method_6264();
		}

		@Override
		public boolean method_6266() {
			return !this.field_16293.method_6181() && super.method_6266();
		}
	}

	static class class_3699 extends class_1352 {
		private final class_1451 field_16297;
		private class_1657 field_16295;
		private class_2338 field_16294;
		private int field_16296;

		public class_3699(class_1451 arg) {
			this.field_16297 = arg;
		}

		@Override
		public boolean method_6264() {
			if (!this.field_16297.method_6181()) {
				return false;
			} else if (this.field_16297.method_6172()) {
				return false;
			} else {
				class_1309 lv = this.field_16297.method_6177();
				if (lv instanceof class_1657) {
					this.field_16295 = (class_1657)lv;
					if (!lv.method_6113()) {
						return false;
					}

					if (this.field_16297.method_5858(this.field_16295) > 100.0) {
						return false;
					}

					class_2338 lv2 = new class_2338(this.field_16295);
					class_2680 lv3 = this.field_16297.field_6002.method_8320(lv2);
					if (lv3.method_11614().method_9525(class_3481.field_16443)) {
						class_2350 lv4 = lv3.method_11654(class_2244.field_11177);
						this.field_16294 = new class_2338(lv2.method_10263() - lv4.method_10148(), lv2.method_10264(), lv2.method_10260() - lv4.method_10165());
						return !this.method_16098();
					}
				}

				return false;
			}
		}

		private boolean method_16098() {
			for (class_1451 lv : this.field_16297.field_6002.method_8403(class_1451.class, new class_238(this.field_16294).method_1014(2.0))) {
				if (lv != this.field_16297 && (lv.method_16086() || lv.method_16093())) {
					return true;
				}
			}

			return false;
		}

		@Override
		public boolean method_6266() {
			return this.field_16297.method_6181()
				&& !this.field_16297.method_6172()
				&& this.field_16295 != null
				&& this.field_16295.method_6113()
				&& this.field_16294 != null
				&& !this.method_16098();
		}

		@Override
		public void method_6269() {
			if (this.field_16294 != null) {
				this.field_16297.method_6176().method_6311(false);
				this.field_16297
					.method_5942()
					.method_6337((double)this.field_16294.method_10263(), (double)this.field_16294.method_10264(), (double)this.field_16294.method_10260(), 1.1F);
			}
		}

		@Override
		public void method_6270() {
			this.field_16297.method_16088(false);
			float f = this.field_16297.field_6002.method_8400(1.0F);
			if (this.field_16295.method_7297() >= 100 && (double)f > 0.77 && (double)f < 0.8 && (double)this.field_16297.field_6002.method_8409().nextFloat() < 0.7) {
				this.method_16097();
			}

			this.field_16296 = 0;
			this.field_16297.method_16087(false);
			this.field_16297.method_5942().method_6340();
		}

		private void method_16097() {
			Random random = this.field_16297.method_6051();
			class_2338.class_2339 lv = new class_2338.class_2339();
			lv.method_10105(this.field_16297);
			this.field_16297
				.method_6082(
					(double)(lv.method_10263() + random.nextInt(11) - 5),
					(double)(lv.method_10264() + random.nextInt(5) - 2),
					(double)(lv.method_10260() + random.nextInt(11) - 5),
					false
				);
			lv.method_10105(this.field_16297);
			this.field_16297.method_6176().method_6311(true);
			class_52 lv2 = this.field_16297.field_6002.method_8503().method_3857().method_367(class_39.field_16216);
			class_47.class_48 lv3 = new class_47.class_48((class_3218)this.field_16297.field_6002)
				.method_312(class_181.field_1232, lv)
				.method_312(class_181.field_1226, this.field_16297)
				.method_311(random);

			for (class_1799 lv4 : lv2.method_319(lv3.method_309(class_173.field_16235))) {
				class_1542 lv5 = new class_1542(
					this.field_16297.field_6002,
					(double)((float)lv.method_10263() - class_3532.method_15374(this.field_16297.field_6283 * (float) (Math.PI / 180.0))),
					(double)lv.method_10264(),
					(double)((float)lv.method_10260() + class_3532.method_15362(this.field_16297.field_6283 * (float) (Math.PI / 180.0))),
					lv4
				);
				this.field_16297.field_6002.method_8649(lv5);
			}
		}

		@Override
		public void method_6268() {
			if (this.field_16295 != null && this.field_16294 != null) {
				this.field_16297.method_6176().method_6311(false);
				this.field_16297
					.method_5942()
					.method_6337((double)this.field_16294.method_10263(), (double)this.field_16294.method_10264(), (double)this.field_16294.method_10260(), 1.1F);
				if (this.field_16297.method_5858(this.field_16295) < 2.5) {
					this.field_16296++;
					if (this.field_16296 > 16) {
						this.field_16297.method_16088(true);
						this.field_16297.method_16087(false);
					} else {
						this.field_16297.method_5951(this.field_16295, 45.0F, 45.0F);
						this.field_16297.method_16087(true);
					}
				} else {
					this.field_16297.method_16088(false);
				}
			}
		}
	}

	static class class_3700 extends class_1391 {
		@Nullable
		private class_1657 field_16298;

		public class_3700(class_1314 arg, double d, class_1856 arg2, boolean bl) {
			super(arg, d, arg2, bl);
		}

		@Override
		public void method_6268() {
			super.method_6268();
			if (this.field_16298 == null && this.field_6616.method_6051().nextInt(600) == 0) {
				this.field_16298 = this.field_6617;
			} else if (this.field_6616.method_6051().nextInt(500) == 0) {
				this.field_16298 = null;
			}
		}

		@Override
		protected boolean method_16081() {
			return this.field_16298 != null && this.field_16298.equals(this.field_6617) ? false : super.method_16081();
		}
	}
}
