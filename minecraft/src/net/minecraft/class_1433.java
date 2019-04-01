package net.minecraft;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1433 extends class_1480 {
	private static final class_2940<class_2338> field_6747 = class_2945.method_12791(class_1433.class, class_2943.field_13324);
	private static final class_2940<Boolean> field_6750 = class_2945.method_12791(class_1433.class, class_2943.field_13323);
	private static final class_2940<Integer> field_6749 = class_2945.method_12791(class_1433.class, class_2943.field_13327);
	private static final class_4051 field_18101 = new class_4051().method_18418(10.0).method_18421().method_18417();
	public static final Predicate<class_1542> field_6748 = arg -> !arg.method_6977() && arg.method_5805() && arg.method_5799();

	public class_1433(class_1299<? extends class_1433> arg, class_1937 arg2) {
		super(arg, arg2);
		this.field_6207 = new class_1433.class_1434(this);
		this.field_6206 = new class_1332(this, 10);
		this.method_5952(true);
	}

	@Nullable
	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		this.method_5855(this.method_5748());
		this.field_5965 = 0.0F;
		return super.method_5943(arg, arg2, arg3, arg4, arg5);
	}

	@Override
	public boolean method_6094() {
		return false;
	}

	@Override
	protected void method_6673(int i) {
	}

	public void method_6493(class_2338 arg) {
		this.field_6011.method_12778(field_6747, arg);
	}

	public class_2338 method_6494() {
		return this.field_6011.method_12789(field_6747);
	}

	public boolean method_6487() {
		return this.field_6011.method_12789(field_6750);
	}

	public void method_6486(boolean bl) {
		this.field_6011.method_12778(field_6750, bl);
	}

	public int method_6491() {
		return this.field_6011.method_12789(field_6749);
	}

	public void method_6489(int i) {
		this.field_6011.method_12778(field_6749, i);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_6747, class_2338.field_10980);
		this.field_6011.method_12784(field_6750, false);
		this.field_6011.method_12784(field_6749, 2400);
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("TreasurePosX", this.method_6494().method_10263());
		arg.method_10569("TreasurePosY", this.method_6494().method_10264());
		arg.method_10569("TreasurePosZ", this.method_6494().method_10260());
		arg.method_10556("GotFish", this.method_6487());
		arg.method_10569("Moistness", this.method_6491());
	}

	@Override
	public void method_5749(class_2487 arg) {
		int i = arg.method_10550("TreasurePosX");
		int j = arg.method_10550("TreasurePosY");
		int k = arg.method_10550("TreasurePosZ");
		this.method_6493(new class_2338(i, j, k));
		super.method_5749(arg);
		this.method_6486(arg.method_10577("GotFish"));
		this.method_6489(arg.method_10550("Moistness"));
	}

	@Override
	protected void method_5959() {
		this.field_6201.method_6277(0, new class_1342(this));
		this.field_6201.method_6277(0, new class_1393(this));
		this.field_6201.method_6277(1, new class_1433.class_1435(this));
		this.field_6201.method_6277(2, new class_1433.class_1436(this, 4.0));
		this.field_6201.method_6277(4, new class_1378(this, 1.0, 10));
		this.field_6201.method_6277(4, new class_1376(this));
		this.field_6201.method_6277(5, new class_1361(this, class_1657.class, 6.0F));
		this.field_6201.method_6277(5, new class_1357(this, 10));
		this.field_6201.method_6277(6, new class_1366(this, 1.2F, true));
		this.field_6201.method_6277(8, new class_1433.class_1437());
		this.field_6201.method_6277(8, new class_1346(this));
		this.field_6201.method_6277(9, new class_1338(this, class_1577.class, 8.0F, 1.0, 1.0));
		this.field_6185.method_6277(1, new class_1399(this, class_1577.class).method_6318());
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192(10.0);
		this.method_5996(class_1612.field_7357).method_6192(1.2F);
		this.method_6127().method_6208(class_1612.field_7363);
		this.method_5996(class_1612.field_7363).method_6192(3.0);
	}

	@Override
	protected class_1408 method_5965(class_1937 arg) {
		return new class_1412(this, arg);
	}

	@Override
	public boolean method_6121(class_1297 arg) {
		boolean bl = arg.method_5643(class_1282.method_5511(this), (float)((int)this.method_5996(class_1612.field_7363).method_6194()));
		if (bl) {
			this.method_5723(this, arg);
			this.method_5783(class_3417.field_14992, 1.0F, 1.0F);
		}

		return bl;
	}

	@Override
	public int method_5748() {
		return 4800;
	}

	@Override
	protected int method_6064(int i) {
		return this.method_5748();
	}

	@Override
	protected float method_18394(class_4050 arg, class_4048 arg2) {
		return 0.3F;
	}

	@Override
	public int method_5978() {
		return 1;
	}

	@Override
	public int method_5986() {
		return 1;
	}

	@Override
	protected boolean method_5860(class_1297 arg) {
		return true;
	}

	@Override
	public boolean method_18397(class_1799 arg) {
		class_1304 lv = class_1308.method_5953(arg);
		return !this.method_6118(lv).method_7960() ? false : lv == class_1304.field_6173 && super.method_18397(arg);
	}

	@Override
	protected void method_5949(class_1542 arg) {
		if (this.method_6118(class_1304.field_6173).method_7960()) {
			class_1799 lv = arg.method_6983();
			if (this.method_5939(lv)) {
				this.method_5673(class_1304.field_6173, lv);
				this.field_6187[class_1304.field_6173.method_5927()] = 2.0F;
				this.method_6103(arg, lv.method_7947());
				arg.method_5650();
			}
		}
	}

	@Override
	public void method_5773() {
		super.method_5773();
		if (!this.method_5987()) {
			if (this.method_5637()) {
				this.method_6489(2400);
			} else {
				this.method_6489(this.method_6491() - 1);
				if (this.method_6491() <= 0) {
					this.method_5643(class_1282.field_5842, 1.0F);
				}

				if (this.field_5952) {
					this.method_18799(
						this.method_18798()
							.method_1031((double)((this.field_5974.nextFloat() * 2.0F - 1.0F) * 0.2F), 0.5, (double)((this.field_5974.nextFloat() * 2.0F - 1.0F) * 0.2F))
					);
					this.field_6031 = this.field_5974.nextFloat() * 360.0F;
					this.field_5952 = false;
					this.field_6007 = true;
				}
			}

			if (this.field_6002.field_9236 && this.method_5799() && this.method_18798().method_1027() > 0.03) {
				class_243 lv = this.method_5828(0.0F);
				float f = class_3532.method_15362(this.field_6031 * (float) (Math.PI / 180.0)) * 0.3F;
				float g = class_3532.method_15374(this.field_6031 * (float) (Math.PI / 180.0)) * 0.3F;
				float h = 1.2F - this.field_5974.nextFloat() * 0.7F;

				for (int i = 0; i < 2; i++) {
					this.field_6002
						.method_8406(
							class_2398.field_11222,
							this.field_5987 - lv.field_1352 * (double)h + (double)f,
							this.field_6010 - lv.field_1351,
							this.field_6035 - lv.field_1350 * (double)h + (double)g,
							0.0,
							0.0,
							0.0
						);
					this.field_6002
						.method_8406(
							class_2398.field_11222,
							this.field_5987 - lv.field_1352 * (double)h - (double)f,
							this.field_6010 - lv.field_1351,
							this.field_6035 - lv.field_1350 * (double)h - (double)g,
							0.0,
							0.0,
							0.0
						);
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 38) {
			this.method_6492(class_2398.field_11211);
		} else {
			super.method_5711(b);
		}
	}

	@Environment(EnvType.CLIENT)
	private void method_6492(class_2394 arg) {
		for (int i = 0; i < 7; i++) {
			double d = this.field_5974.nextGaussian() * 0.01;
			double e = this.field_5974.nextGaussian() * 0.01;
			double f = this.field_5974.nextGaussian() * 0.01;
			this.field_6002
				.method_8406(
					arg,
					this.field_5987 + (double)(this.field_5974.nextFloat() * this.method_17681() * 2.0F) - (double)this.method_17681(),
					this.field_6010 + 0.2F + (double)(this.field_5974.nextFloat() * this.method_17682()),
					this.field_6035 + (double)(this.field_5974.nextFloat() * this.method_17681() * 2.0F) - (double)this.method_17681(),
					d,
					e,
					f
				);
		}
	}

	@Override
	protected boolean method_5992(class_1657 arg, class_1268 arg2) {
		class_1799 lv = arg.method_5998(arg2);
		if (!lv.method_7960() && lv.method_7909().method_7855(class_3489.field_15527)) {
			if (!this.field_6002.field_9236) {
				this.method_5783(class_3417.field_14590, 1.0F, 1.0F);
			}

			this.method_6486(true);
			if (!arg.field_7503.field_7477) {
				lv.method_7934(1);
			}

			return true;
		} else {
			return super.method_5992(arg, arg2);
		}
	}

	@Override
	public boolean method_5979(class_1936 arg, class_3730 arg2) {
		return this.field_6010 > 45.0 && this.field_6010 < (double)arg.method_8615() && arg.method_8310(new class_2338(this)) != class_1972.field_9423
			|| arg.method_8310(new class_2338(this)) != class_1972.field_9446 && super.method_5979(arg, arg2);
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_15216;
	}

	@Nullable
	@Override
	protected class_3414 method_6002() {
		return class_3417.field_15101;
	}

	@Nullable
	@Override
	protected class_3414 method_5994() {
		return this.method_5799() ? class_3417.field_14881 : class_3417.field_14799;
	}

	@Override
	protected class_3414 method_5625() {
		return class_3417.field_14887;
	}

	@Override
	protected class_3414 method_5737() {
		return class_3417.field_15172;
	}

	protected boolean method_6484() {
		class_2338 lv = this.method_5942().method_6355();
		return lv != null ? lv.method_19769(this.method_19538(), 12.0) : false;
	}

	@Override
	public void method_6091(class_243 arg) {
		if (this.method_6034() && this.method_5799()) {
			this.method_5724(this.method_6029(), arg);
			this.method_5784(class_1313.field_6308, this.method_18798());
			this.method_18799(this.method_18798().method_1021(0.9));
			if (this.method_5968() == null) {
				this.method_18799(this.method_18798().method_1031(0.0, -0.005, 0.0));
			}
		} else {
			super.method_6091(arg);
		}
	}

	@Override
	public boolean method_5931(class_1657 arg) {
		return true;
	}

	static class class_1434 extends class_1335 {
		private final class_1433 field_6751;

		public class_1434(class_1433 arg) {
			super(arg);
			this.field_6751 = arg;
		}

		@Override
		public void method_6240() {
			if (this.field_6751.method_5799()) {
				this.field_6751.method_18799(this.field_6751.method_18798().method_1031(0.0, 0.005, 0.0));
			}

			if (this.field_6374 == class_1335.class_1336.field_6378 && !this.field_6751.method_5942().method_6357()) {
				double d = this.field_6370 - this.field_6751.field_5987;
				double e = this.field_6369 - this.field_6751.field_6010;
				double f = this.field_6367 - this.field_6751.field_6035;
				double g = d * d + e * e + f * f;
				if (g < 2.5000003E-7F) {
					this.field_6371.method_5930(0.0F);
				} else {
					float h = (float)(class_3532.method_15349(f, d) * 180.0F / (float)Math.PI) - 90.0F;
					this.field_6751.field_6031 = this.method_6238(this.field_6751.field_6031, h, 10.0F);
					this.field_6751.field_6283 = this.field_6751.field_6031;
					this.field_6751.field_6241 = this.field_6751.field_6031;
					float i = (float)(this.field_6372 * this.field_6751.method_5996(class_1612.field_7357).method_6194());
					if (this.field_6751.method_5799()) {
						this.field_6751.method_6125(i * 0.02F);
						float j = -((float)(class_3532.method_15349(e, (double)class_3532.method_15368(d * d + f * f)) * 180.0F / (float)Math.PI));
						j = class_3532.method_15363(class_3532.method_15393(j), -85.0F, 85.0F);
						this.field_6751.field_5965 = this.method_6238(this.field_6751.field_5965, j, 5.0F);
						float k = class_3532.method_15362(this.field_6751.field_5965 * (float) (Math.PI / 180.0));
						float l = class_3532.method_15374(this.field_6751.field_5965 * (float) (Math.PI / 180.0));
						this.field_6751.field_6250 = k * i;
						this.field_6751.field_6227 = -l * i;
					} else {
						this.field_6751.method_6125(i * 0.1F);
					}
				}
			} else {
				this.field_6751.method_6125(0.0F);
				this.field_6751.method_5938(0.0F);
				this.field_6751.method_5976(0.0F);
				this.field_6751.method_5930(0.0F);
			}
		}
	}

	static class class_1435 extends class_1352 {
		private final class_1433 field_6752;
		private boolean field_6753;

		class_1435(class_1433 arg) {
			this.field_6752 = arg;
			this.method_6265(EnumSet.of(class_1352.class_4134.field_18405, class_1352.class_4134.field_18406));
		}

		@Override
		public boolean method_6267() {
			return false;
		}

		@Override
		public boolean method_6264() {
			return this.field_6752.method_6487() && this.field_6752.method_5669() >= 100;
		}

		@Override
		public boolean method_6266() {
			class_2338 lv = this.field_6752.method_6494();
			return !new class_2338((double)lv.method_10263(), this.field_6752.field_6010, (double)lv.method_10260()).method_19769(this.field_6752.method_19538(), 4.0)
				&& !this.field_6753
				&& this.field_6752.method_5669() >= 100;
		}

		@Override
		public void method_6269() {
			this.field_6753 = false;
			this.field_6752.method_5942().method_6340();
			class_1937 lv = this.field_6752.field_6002;
			class_2338 lv2 = new class_2338(this.field_6752);
			String string = (double)lv.field_9229.nextFloat() >= 0.5 ? "Ocean_Ruin" : "Shipwreck";
			class_2338 lv3 = lv.method_8487(string, lv2, 50, false);
			if (lv3 == null) {
				class_2338 lv4 = lv.method_8487(string.equals("Ocean_Ruin") ? "Shipwreck" : "Ocean_Ruin", lv2, 50, false);
				if (lv4 == null) {
					this.field_6753 = true;
					return;
				}

				this.field_6752.method_6493(lv4);
			} else {
				this.field_6752.method_6493(lv3);
			}

			lv.method_8421(this.field_6752, (byte)38);
		}

		@Override
		public void method_6270() {
			class_2338 lv = this.field_6752.method_6494();
			if (new class_2338((double)lv.method_10263(), this.field_6752.field_6010, (double)lv.method_10260()).method_19769(this.field_6752.method_19538(), 4.0)
				|| this.field_6753) {
				this.field_6752.method_6486(false);
			}
		}

		@Override
		public void method_6268() {
			class_2338 lv = this.field_6752.method_6494();
			class_1937 lv2 = this.field_6752.field_6002;
			if (this.field_6752.method_6484() || this.field_6752.method_5942().method_6357()) {
				class_243 lv3 = class_1414.method_6377(
					this.field_6752, 16, 1, new class_243((double)lv.method_10263(), (double)lv.method_10264(), (double)lv.method_10260()), (float) (Math.PI / 8)
				);
				if (lv3 == null) {
					lv3 = class_1414.method_6373(this.field_6752, 8, 4, new class_243((double)lv.method_10263(), (double)lv.method_10264(), (double)lv.method_10260()));
				}

				if (lv3 != null) {
					class_2338 lv4 = new class_2338(lv3);
					if (!lv2.method_8316(lv4).method_15767(class_3486.field_15517) || !lv2.method_8320(lv4).method_11609(lv2, lv4, class_10.field_48)) {
						lv3 = class_1414.method_6373(this.field_6752, 8, 5, new class_243((double)lv.method_10263(), (double)lv.method_10264(), (double)lv.method_10260()));
					}
				}

				if (lv3 == null) {
					this.field_6753 = true;
					return;
				}

				this.field_6752
					.method_5988()
					.method_6230(lv3.field_1352, lv3.field_1351, lv3.field_1350, (float)(this.field_6752.method_5986() + 20), (float)this.field_6752.method_5978());
				this.field_6752.method_5942().method_6337(lv3.field_1352, lv3.field_1351, lv3.field_1350, 1.3);
				if (lv2.field_9229.nextInt(80) == 0) {
					lv2.method_8421(this.field_6752, (byte)38);
				}
			}
		}
	}

	static class class_1436 extends class_1352 {
		private final class_1433 field_6755;
		private final double field_6754;
		private class_1657 field_6756;

		class_1436(class_1433 arg, double d) {
			this.field_6755 = arg;
			this.field_6754 = d;
			this.method_6265(EnumSet.of(class_1352.class_4134.field_18405, class_1352.class_4134.field_18406));
		}

		@Override
		public boolean method_6264() {
			this.field_6756 = this.field_6755.field_6002.method_18462(class_1433.field_18101, this.field_6755);
			return this.field_6756 == null ? false : this.field_6756.method_5681();
		}

		@Override
		public boolean method_6266() {
			return this.field_6756 != null && this.field_6756.method_5681() && this.field_6755.method_5858(this.field_6756) < 256.0;
		}

		@Override
		public void method_6269() {
			this.field_6756.method_6092(new class_1293(class_1294.field_5900, 100));
		}

		@Override
		public void method_6270() {
			this.field_6756 = null;
			this.field_6755.method_5942().method_6340();
		}

		@Override
		public void method_6268() {
			this.field_6755.method_5988().method_6226(this.field_6756, (float)(this.field_6755.method_5986() + 20), (float)this.field_6755.method_5978());
			if (this.field_6755.method_5858(this.field_6756) < 6.25) {
				this.field_6755.method_5942().method_6340();
			} else {
				this.field_6755.method_5942().method_6335(this.field_6756, this.field_6754);
			}

			if (this.field_6756.method_5681() && this.field_6756.field_6002.field_9229.nextInt(6) == 0) {
				this.field_6756.method_6092(new class_1293(class_1294.field_5900, 100));
			}
		}
	}

	class class_1437 extends class_1352 {
		private int field_6758;

		private class_1437() {
		}

		@Override
		public boolean method_6264() {
			if (this.field_6758 > class_1433.this.field_6012) {
				return false;
			} else {
				List<class_1542> list = class_1433.this.field_6002
					.method_8390(class_1542.class, class_1433.this.method_5829().method_1009(8.0, 8.0, 8.0), class_1433.field_6748);
				return !list.isEmpty() || !class_1433.this.method_6118(class_1304.field_6173).method_7960();
			}
		}

		@Override
		public void method_6269() {
			List<class_1542> list = class_1433.this.field_6002
				.method_8390(class_1542.class, class_1433.this.method_5829().method_1009(8.0, 8.0, 8.0), class_1433.field_6748);
			if (!list.isEmpty()) {
				class_1433.this.method_5942().method_6335((class_1297)list.get(0), 1.2F);
				class_1433.this.method_5783(class_3417.field_14972, 1.0F, 1.0F);
			}

			this.field_6758 = 0;
		}

		@Override
		public void method_6270() {
			class_1799 lv = class_1433.this.method_6118(class_1304.field_6173);
			if (!lv.method_7960()) {
				this.method_18056(lv);
				class_1433.this.method_5673(class_1304.field_6173, class_1799.field_8037);
				this.field_6758 = class_1433.this.field_6012 + class_1433.this.field_5974.nextInt(100);
			}
		}

		@Override
		public void method_6268() {
			List<class_1542> list = class_1433.this.field_6002
				.method_8390(class_1542.class, class_1433.this.method_5829().method_1009(8.0, 8.0, 8.0), class_1433.field_6748);
			class_1799 lv = class_1433.this.method_6118(class_1304.field_6173);
			if (!lv.method_7960()) {
				this.method_18056(lv);
				class_1433.this.method_5673(class_1304.field_6173, class_1799.field_8037);
			} else if (!list.isEmpty()) {
				class_1433.this.method_5942().method_6335((class_1297)list.get(0), 1.2F);
			}
		}

		private void method_18056(class_1799 arg) {
			if (!arg.method_7960()) {
				double d = class_1433.this.field_6010 - 0.3F + (double)class_1433.this.method_5751();
				class_1542 lv = new class_1542(class_1433.this.field_6002, class_1433.this.field_5987, d, class_1433.this.field_6035, arg);
				lv.method_6982(40);
				lv.method_6981(class_1433.this.method_5667());
				float f = 0.3F;
				float g = class_1433.this.field_5974.nextFloat() * (float) (Math.PI * 2);
				float h = 0.02F * class_1433.this.field_5974.nextFloat();
				lv.method_18800(
					(double)(
						0.3F
								* -class_3532.method_15374(class_1433.this.field_6031 * (float) (Math.PI / 180.0))
								* class_3532.method_15362(class_1433.this.field_5965 * (float) (Math.PI / 180.0))
							+ class_3532.method_15362(g) * h
					),
					(double)(0.3F * class_3532.method_15374(class_1433.this.field_5965 * (float) (Math.PI / 180.0)) * 1.5F),
					(double)(
						0.3F
								* class_3532.method_15362(class_1433.this.field_6031 * (float) (Math.PI / 180.0))
								* class_3532.method_15362(class_1433.this.field_5965 * (float) (Math.PI / 180.0))
							+ class_3532.method_15374(g) * h
					)
				);
				class_1433.this.field_6002.method_8649(lv);
			}
		}
	}
}
