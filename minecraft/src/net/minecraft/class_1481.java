package net.minecraft;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class class_1481 extends class_1429 {
	private static final class_2940<class_2338> field_6920 = class_2945.method_12791(class_1481.class, class_2943.field_13324);
	private static final class_2940<Boolean> field_6919 = class_2945.method_12791(class_1481.class, class_2943.field_13323);
	private static final class_2940<Boolean> field_6923 = class_2945.method_12791(class_1481.class, class_2943.field_13323);
	private static final class_2940<class_2338> field_6922 = class_2945.method_12791(class_1481.class, class_2943.field_13324);
	private static final class_2940<Boolean> field_6924 = class_2945.method_12791(class_1481.class, class_2943.field_13323);
	private static final class_2940<Boolean> field_6925 = class_2945.method_12791(class_1481.class, class_2943.field_13323);
	private int field_6918;
	public static final Predicate<class_1309> field_6921 = arg -> arg.method_6109() && !arg.method_5799();

	public class_1481(class_1299<? extends class_1481> arg, class_1937 arg2) {
		super(arg, arg2);
		this.field_6207 = new class_1481.class_1486(this);
		this.field_6746 = class_2246.field_10102;
		this.field_6013 = 1.0F;
	}

	public void method_6683(class_2338 arg) {
		this.field_6011.method_12778(field_6920, arg);
	}

	private class_2338 method_6693() {
		return this.field_6011.method_12789(field_6920);
	}

	private void method_6699(class_2338 arg) {
		this.field_6011.method_12778(field_6922, arg);
	}

	private class_2338 method_6687() {
		return this.field_6011.method_12789(field_6922);
	}

	public boolean method_6679() {
		return this.field_6011.method_12789(field_6919);
	}

	private void method_6680(boolean bl) {
		this.field_6011.method_12778(field_6919, bl);
	}

	public boolean method_6695() {
		return this.field_6011.method_12789(field_6923);
	}

	private void method_6676(boolean bl) {
		this.field_6918 = bl ? 1 : 0;
		this.field_6011.method_12778(field_6923, bl);
	}

	private boolean method_6684() {
		return this.field_6011.method_12789(field_6924);
	}

	private void method_6697(boolean bl) {
		this.field_6011.method_12778(field_6924, bl);
	}

	private boolean method_6691() {
		return this.field_6011.method_12789(field_6925);
	}

	private void method_6696(boolean bl) {
		this.field_6011.method_12778(field_6925, bl);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_6920, class_2338.field_10980);
		this.field_6011.method_12784(field_6919, false);
		this.field_6011.method_12784(field_6922, class_2338.field_10980);
		this.field_6011.method_12784(field_6924, false);
		this.field_6011.method_12784(field_6925, false);
		this.field_6011.method_12784(field_6923, false);
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("HomePosX", this.method_6693().method_10263());
		arg.method_10569("HomePosY", this.method_6693().method_10264());
		arg.method_10569("HomePosZ", this.method_6693().method_10260());
		arg.method_10556("HasEgg", this.method_6679());
		arg.method_10569("TravelPosX", this.method_6687().method_10263());
		arg.method_10569("TravelPosY", this.method_6687().method_10264());
		arg.method_10569("TravelPosZ", this.method_6687().method_10260());
	}

	@Override
	public void method_5749(class_2487 arg) {
		int i = arg.method_10550("HomePosX");
		int j = arg.method_10550("HomePosY");
		int k = arg.method_10550("HomePosZ");
		this.method_6683(new class_2338(i, j, k));
		super.method_5749(arg);
		this.method_6680(arg.method_10577("HasEgg"));
		int l = arg.method_10550("TravelPosX");
		int m = arg.method_10550("TravelPosY");
		int n = arg.method_10550("TravelPosZ");
		this.method_6699(new class_2338(l, m, n));
	}

	@Nullable
	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		this.method_6683(new class_2338(this));
		this.method_6699(class_2338.field_10980);
		return super.method_5943(arg, arg2, arg3, arg4, arg5);
	}

	@Override
	public boolean method_5979(class_1936 arg, class_3730 arg2) {
		class_2338 lv = new class_2338(this.field_5987, this.method_5829().field_1322, this.field_6035);
		return lv.method_10264() < arg.method_8615() + 4 && super.method_5979(arg, arg2);
	}

	@Override
	protected void method_5959() {
		this.field_6201.method_6277(0, new class_1481.class_1487(this, 1.2));
		this.field_6201.method_6277(1, new class_1481.class_1482(this, 1.0));
		this.field_6201.method_6277(1, new class_1481.class_1485(this, 1.0));
		this.field_6201.method_6277(2, new class_1481.class_1490(this, 1.1, class_2246.field_10376.method_8389()));
		this.field_6201.method_6277(3, new class_1481.class_1484(this, 1.0));
		this.field_6201.method_6277(4, new class_1481.class_1483(this, 1.0));
		this.field_6201.method_6277(7, new class_1481.class_1491(this, 1.0));
		this.field_6201.method_6277(8, new class_1361(this, class_1657.class, 8.0F));
		this.field_6201.method_6277(9, new class_1481.class_1489(this, 1.0, 100));
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192(30.0);
		this.method_5996(class_1612.field_7357).method_6192(0.25);
	}

	@Override
	public boolean method_5675() {
		return false;
	}

	@Override
	public boolean method_6094() {
		return true;
	}

	@Override
	public class_1310 method_6046() {
		return class_1310.field_6292;
	}

	@Override
	public int method_5970() {
		return 200;
	}

	@Nullable
	@Override
	protected class_3414 method_5994() {
		return !this.method_5799() && this.field_5952 && !this.method_6109() ? class_3417.field_14722 : super.method_5994();
	}

	@Override
	protected void method_5734(float f) {
		super.method_5734(f * 1.5F);
	}

	@Override
	protected class_3414 method_5737() {
		return class_3417.field_14764;
	}

	@Nullable
	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return this.method_6109() ? class_3417.field_15070 : class_3417.field_15183;
	}

	@Nullable
	@Override
	protected class_3414 method_6002() {
		return this.method_6109() ? class_3417.field_14618 : class_3417.field_14856;
	}

	@Override
	protected void method_5712(class_2338 arg, class_2680 arg2) {
		class_3414 lv = this.method_6109() ? class_3417.field_14864 : class_3417.field_14549;
		this.method_5783(lv, 0.15F, 1.0F);
	}

	@Override
	public boolean method_6482() {
		return super.method_6482() && !this.method_6679();
	}

	@Override
	protected float method_5867() {
		return this.field_5994 + 0.15F;
	}

	@Override
	public float method_17825() {
		return this.method_6109() ? 0.3F : 1.0F;
	}

	@Override
	protected class_1408 method_5965(class_1937 arg) {
		return new class_1481.class_1488(this, arg);
	}

	@Nullable
	@Override
	public class_1296 method_5613(class_1296 arg) {
		return class_1299.field_6113.method_5883(this.field_6002);
	}

	@Override
	public boolean method_6481(class_1799 arg) {
		return arg.method_7909() == class_2246.field_10376.method_8389();
	}

	@Override
	public float method_6144(class_2338 arg, class_1941 arg2) {
		return !this.method_6684() && arg2.method_8316(arg).method_15767(class_3486.field_15517) ? 10.0F : super.method_6144(arg, arg2);
	}

	@Override
	public void method_6007() {
		super.method_6007();
		if (this.method_5805() && this.method_6695() && this.field_6918 >= 1 && this.field_6918 % 5 == 0) {
			class_2338 lv = new class_2338(this);
			if (this.field_6002.method_8320(lv.method_10074()).method_11614() == class_2246.field_10102) {
				this.field_6002.method_8535(2001, lv, class_2248.method_9507(class_2246.field_10102.method_9564()));
			}
		}
	}

	@Override
	protected void method_5619() {
		super.method_5619();
		if (!this.method_6109() && this.field_6002.method_8450().method_8355("doMobLoot")) {
			this.method_5870(class_1802.field_8161, 1);
		}
	}

	@Override
	public void method_6091(class_243 arg) {
		if (this.method_6034() && this.method_5799()) {
			this.method_5724(0.1F, arg);
			this.method_5784(class_1313.field_6308, this.method_18798());
			this.method_18799(this.method_18798().method_1021(0.9));
			if (this.method_5968() == null && (!this.method_6684() || !this.method_6693().method_19769(this.method_19538(), 20.0))) {
				this.method_18799(this.method_18798().method_1031(0.0, -0.005, 0.0));
			}
		} else {
			super.method_6091(arg);
		}
	}

	@Override
	public boolean method_5931(class_1657 arg) {
		return false;
	}

	@Override
	public void method_5800(class_1538 arg) {
		this.method_5643(class_1282.field_5861, Float.MAX_VALUE);
	}

	static class class_1482 extends class_1341 {
		private final class_1481 field_6926;

		class_1482(class_1481 arg, double d) {
			super(arg, d);
			this.field_6926 = arg;
		}

		@Override
		public boolean method_6264() {
			return super.method_6264() && !this.field_6926.method_6679();
		}

		@Override
		protected void method_6249() {
			class_3222 lv = this.field_6404.method_6478();
			if (lv == null && this.field_6406.method_6478() != null) {
				lv = this.field_6406.method_6478();
			}

			if (lv != null) {
				lv.method_7281(class_3468.field_15410);
				class_174.field_1190.method_855(lv, this.field_6404, this.field_6406, null);
			}

			this.field_6926.method_6680(true);
			this.field_6404.method_6477();
			this.field_6406.method_6477();
			Random random = this.field_6404.method_6051();
			if (this.field_6405.method_8450().method_8355("doMobLoot")) {
				this.field_6405
					.method_8649(new class_1303(this.field_6405, this.field_6404.field_5987, this.field_6404.field_6010, this.field_6404.field_6035, random.nextInt(7) + 1));
			}
		}
	}

	static class class_1483 extends class_1352 {
		private final class_1481 field_6930;
		private final double field_6927;
		private boolean field_6929;
		private int field_6928;

		class_1483(class_1481 arg, double d) {
			this.field_6930 = arg;
			this.field_6927 = d;
		}

		@Override
		public boolean method_6264() {
			if (this.field_6930.method_6109()) {
				return false;
			} else if (this.field_6930.method_6679()) {
				return true;
			} else {
				return this.field_6930.method_6051().nextInt(700) != 0 ? false : !this.field_6930.method_6693().method_19769(this.field_6930.method_19538(), 64.0);
			}
		}

		@Override
		public void method_6269() {
			this.field_6930.method_6697(true);
			this.field_6929 = false;
			this.field_6928 = 0;
		}

		@Override
		public void method_6270() {
			this.field_6930.method_6697(false);
		}

		@Override
		public boolean method_6266() {
			return !this.field_6930.method_6693().method_19769(this.field_6930.method_19538(), 7.0) && !this.field_6929 && this.field_6928 <= 600;
		}

		@Override
		public void method_6268() {
			class_2338 lv = this.field_6930.method_6693();
			boolean bl = lv.method_19769(this.field_6930.method_19538(), 16.0);
			if (bl) {
				this.field_6928++;
			}

			if (this.field_6930.method_5942().method_6357()) {
				class_243 lv2 = class_1414.method_6377(
					this.field_6930, 16, 3, new class_243((double)lv.method_10263(), (double)lv.method_10264(), (double)lv.method_10260()), (float) (Math.PI / 10)
				);
				if (lv2 == null) {
					lv2 = class_1414.method_6373(this.field_6930, 8, 7, new class_243((double)lv.method_10263(), (double)lv.method_10264(), (double)lv.method_10260()));
				}

				if (lv2 != null && !bl && this.field_6930.field_6002.method_8320(new class_2338(lv2)).method_11614() != class_2246.field_10382) {
					lv2 = class_1414.method_6373(this.field_6930, 16, 5, new class_243((double)lv.method_10263(), (double)lv.method_10264(), (double)lv.method_10260()));
				}

				if (lv2 == null) {
					this.field_6929 = true;
					return;
				}

				this.field_6930.method_5942().method_6337(lv2.field_1352, lv2.field_1351, lv2.field_1350, this.field_6927);
			}
		}
	}

	static class class_1484 extends class_1367 {
		private final class_1481 field_6931;

		private class_1484(class_1481 arg, double d) {
			super(arg, arg.method_6109() ? 2.0 : d, 24);
			this.field_6931 = arg;
			this.field_6515 = -1;
		}

		@Override
		public boolean method_6266() {
			return !this.field_6931.method_5799() && this.field_6517 <= 1200 && this.method_6296(this.field_6931.field_6002, this.field_6512);
		}

		@Override
		public boolean method_6264() {
			if (this.field_6931.method_6109() && !this.field_6931.method_5799()) {
				return super.method_6264();
			} else {
				return !this.field_6931.method_6684() && !this.field_6931.method_5799() && !this.field_6931.method_6679() ? super.method_6264() : false;
			}
		}

		@Override
		public boolean method_6294() {
			return this.field_6517 % 160 == 0;
		}

		@Override
		protected boolean method_6296(class_1941 arg, class_2338 arg2) {
			class_2248 lv = arg.method_8320(arg2).method_11614();
			return lv == class_2246.field_10382;
		}
	}

	static class class_1485 extends class_1367 {
		private final class_1481 field_6932;

		class_1485(class_1481 arg, double d) {
			super(arg, d, 16);
			this.field_6932 = arg;
		}

		@Override
		public boolean method_6264() {
			return this.field_6932.method_6679() && this.field_6932.method_6693().method_19769(this.field_6932.method_19538(), 9.0) ? super.method_6264() : false;
		}

		@Override
		public boolean method_6266() {
			return super.method_6266() && this.field_6932.method_6679() && this.field_6932.method_6693().method_19769(this.field_6932.method_19538(), 9.0);
		}

		@Override
		public void method_6268() {
			super.method_6268();
			class_2338 lv = new class_2338(this.field_6932);
			if (!this.field_6932.method_5799() && this.method_6295()) {
				if (this.field_6932.field_6918 < 1) {
					this.field_6932.method_6676(true);
				} else if (this.field_6932.field_6918 > 200) {
					class_1937 lv2 = this.field_6932.field_6002;
					lv2.method_8396(null, lv, class_3417.field_14634, class_3419.field_15245, 0.3F, 0.9F + lv2.field_9229.nextFloat() * 0.2F);
					lv2.method_8652(
						this.field_6512.method_10084(),
						class_2246.field_10195.method_9564().method_11657(class_2542.field_11710, Integer.valueOf(this.field_6932.field_5974.nextInt(4) + 1)),
						3
					);
					this.field_6932.method_6680(false);
					this.field_6932.method_6676(false);
					this.field_6932.method_6476(600);
				}

				if (this.field_6932.method_6695()) {
					this.field_6932.field_6918++;
				}
			}
		}

		@Override
		protected boolean method_6296(class_1941 arg, class_2338 arg2) {
			if (!arg.method_8623(arg2.method_10084())) {
				return false;
			} else {
				class_2248 lv = arg.method_8320(arg2).method_11614();
				return lv == class_2246.field_10102;
			}
		}
	}

	static class class_1486 extends class_1335 {
		private final class_1481 field_6933;

		class_1486(class_1481 arg) {
			super(arg);
			this.field_6933 = arg;
		}

		private void method_6700() {
			if (this.field_6933.method_5799()) {
				this.field_6933.method_18799(this.field_6933.method_18798().method_1031(0.0, 0.005, 0.0));
				if (!this.field_6933.method_6693().method_19769(this.field_6933.method_19538(), 16.0)) {
					this.field_6933.method_6125(Math.max(this.field_6933.method_6029() / 2.0F, 0.08F));
				}

				if (this.field_6933.method_6109()) {
					this.field_6933.method_6125(Math.max(this.field_6933.method_6029() / 3.0F, 0.06F));
				}
			} else if (this.field_6933.field_5952) {
				this.field_6933.method_6125(Math.max(this.field_6933.method_6029() / 2.0F, 0.06F));
			}
		}

		@Override
		public void method_6240() {
			this.method_6700();
			if (this.field_6374 == class_1335.class_1336.field_6378 && !this.field_6933.method_5942().method_6357()) {
				double d = this.field_6370 - this.field_6933.field_5987;
				double e = this.field_6369 - this.field_6933.field_6010;
				double f = this.field_6367 - this.field_6933.field_6035;
				double g = (double)class_3532.method_15368(d * d + e * e + f * f);
				e /= g;
				float h = (float)(class_3532.method_15349(f, d) * 180.0F / (float)Math.PI) - 90.0F;
				this.field_6933.field_6031 = this.method_6238(this.field_6933.field_6031, h, 90.0F);
				this.field_6933.field_6283 = this.field_6933.field_6031;
				float i = (float)(this.field_6372 * this.field_6933.method_5996(class_1612.field_7357).method_6194());
				this.field_6933.method_6125(class_3532.method_16439(0.125F, this.field_6933.method_6029(), i));
				this.field_6933.method_18799(this.field_6933.method_18798().method_1031(0.0, (double)this.field_6933.method_6029() * e * 0.1, 0.0));
			} else {
				this.field_6933.method_6125(0.0F);
			}
		}
	}

	static class class_1487 extends class_1374 {
		class_1487(class_1481 arg, double d) {
			super(arg, d);
		}

		@Override
		public boolean method_6264() {
			if (this.field_6549.method_6065() == null && !this.field_6549.method_5809()) {
				return false;
			} else {
				class_2338 lv = this.method_6300(this.field_6549.field_6002, this.field_6549, 7, 4);
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
	}

	static class class_1488 extends class_1412 {
		class_1488(class_1481 arg, class_1937 arg2) {
			super(arg, arg2);
		}

		@Override
		protected boolean method_6358() {
			return true;
		}

		@Override
		protected class_13 method_6336(int i) {
			return new class_13(new class_15(), i);
		}

		@Override
		public boolean method_6333(class_2338 arg) {
			if (this.field_6684 instanceof class_1481) {
				class_1481 lv = (class_1481)this.field_6684;
				if (lv.method_6691()) {
					return this.field_6677.method_8320(arg).method_11614() == class_2246.field_10382;
				}
			}

			return !this.field_6677.method_8320(arg.method_10074()).method_11588();
		}
	}

	static class class_1489 extends class_1379 {
		private final class_1481 field_6934;

		private class_1489(class_1481 arg, double d, int i) {
			super(arg, d, i);
			this.field_6934 = arg;
		}

		@Override
		public boolean method_6264() {
			return !this.field_6566.method_5799() && !this.field_6934.method_6684() && !this.field_6934.method_6679() ? super.method_6264() : false;
		}
	}

	static class class_1490 extends class_1352 {
		private static final class_4051 field_18117 = new class_4051().method_18418(10.0).method_18421().method_18417();
		private final class_1481 field_6938;
		private final double field_6935;
		private class_1657 field_6939;
		private int field_6936;
		private final Set<class_1792> field_6937;

		class_1490(class_1481 arg, double d, class_1792 arg2) {
			this.field_6938 = arg;
			this.field_6935 = d;
			this.field_6937 = Sets.<class_1792>newHashSet(arg2);
			this.method_6265(EnumSet.of(class_1352.class_4134.field_18405, class_1352.class_4134.field_18406));
		}

		@Override
		public boolean method_6264() {
			if (this.field_6936 > 0) {
				this.field_6936--;
				return false;
			} else {
				this.field_6939 = this.field_6938.field_6002.method_18462(field_18117, this.field_6938);
				return this.field_6939 == null ? false : this.method_6701(this.field_6939.method_6047()) || this.method_6701(this.field_6939.method_6079());
			}
		}

		private boolean method_6701(class_1799 arg) {
			return this.field_6937.contains(arg.method_7909());
		}

		@Override
		public boolean method_6266() {
			return this.method_6264();
		}

		@Override
		public void method_6270() {
			this.field_6939 = null;
			this.field_6938.method_5942().method_6340();
			this.field_6936 = 100;
		}

		@Override
		public void method_6268() {
			this.field_6938.method_5988().method_6226(this.field_6939, (float)(this.field_6938.method_5986() + 20), (float)this.field_6938.method_5978());
			if (this.field_6938.method_5858(this.field_6939) < 6.25) {
				this.field_6938.method_5942().method_6340();
			} else {
				this.field_6938.method_5942().method_6335(this.field_6939, this.field_6935);
			}
		}
	}

	static class class_1491 extends class_1352 {
		private final class_1481 field_6942;
		private final double field_6940;
		private boolean field_6941;

		class_1491(class_1481 arg, double d) {
			this.field_6942 = arg;
			this.field_6940 = d;
		}

		@Override
		public boolean method_6264() {
			return !this.field_6942.method_6684() && !this.field_6942.method_6679() && this.field_6942.method_5799();
		}

		@Override
		public void method_6269() {
			int i = 512;
			int j = 4;
			Random random = this.field_6942.field_5974;
			int k = random.nextInt(1025) - 512;
			int l = random.nextInt(9) - 4;
			int m = random.nextInt(1025) - 512;
			if ((double)l + this.field_6942.field_6010 > (double)(this.field_6942.field_6002.method_8615() - 1)) {
				l = 0;
			}

			class_2338 lv = new class_2338((double)k + this.field_6942.field_5987, (double)l + this.field_6942.field_6010, (double)m + this.field_6942.field_6035);
			this.field_6942.method_6699(lv);
			this.field_6942.method_6696(true);
			this.field_6941 = false;
		}

		@Override
		public void method_6268() {
			if (this.field_6942.method_5942().method_6357()) {
				class_2338 lv = this.field_6942.method_6687();
				class_243 lv2 = class_1414.method_6377(
					this.field_6942, 16, 3, new class_243((double)lv.method_10263(), (double)lv.method_10264(), (double)lv.method_10260()), (float) (Math.PI / 10)
				);
				if (lv2 == null) {
					lv2 = class_1414.method_6373(this.field_6942, 8, 7, new class_243((double)lv.method_10263(), (double)lv.method_10264(), (double)lv.method_10260()));
				}

				if (lv2 != null) {
					int i = class_3532.method_15357(lv2.field_1352);
					int j = class_3532.method_15357(lv2.field_1350);
					int k = 34;
					if (!this.field_6942.field_6002.method_8627(i - 34, 0, j - 34, i + 34, 0, j + 34)) {
						lv2 = null;
					}
				}

				if (lv2 == null) {
					this.field_6941 = true;
					return;
				}

				this.field_6942.method_5942().method_6337(lv2.field_1352, lv2.field_1351, lv2.field_1350, this.field_6940);
			}
		}

		@Override
		public boolean method_6266() {
			return !this.field_6942.method_5942().method_6357()
				&& !this.field_6941
				&& !this.field_6942.method_6684()
				&& !this.field_6942.method_6479()
				&& !this.field_6942.method_6679();
		}

		@Override
		public void method_6270() {
			this.field_6942.method_6696(false);
			super.method_6270();
		}
	}
}
