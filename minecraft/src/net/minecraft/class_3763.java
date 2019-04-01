package net.minecraft;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_3763 extends class_3732 {
	protected static final class_2940<Boolean> field_19032 = class_2945.method_12791(class_3763.class, class_2943.field_13323);
	private static final Predicate<class_1542> field_16600 = arg -> !arg.method_6977()
			&& arg.method_5805()
			&& class_1799.method_7973(arg.method_6983(), class_3765.field_16609);
	@Nullable
	protected class_3765 field_16599;
	private int field_16601;
	private boolean field_16602;
	private int field_16997;

	protected class_3763(class_1299<? extends class_3763> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	protected void method_5959() {
		super.method_5959();
		this.field_6201.method_6277(1, new class_3763.class_3764<>(this));
		this.field_6201.method_6277(3, new class_3759<>(this));
		this.field_6201.method_6277(4, new class_3763.class_4261(this, 1.05F, 1));
		this.field_6201.method_6277(5, new class_3763.class_4260(this));
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_19032, false);
	}

	public abstract void method_16484(int i, boolean bl);

	public boolean method_16481() {
		return this.field_16602;
	}

	public void method_16480(boolean bl) {
		this.field_16602 = bl;
	}

	@Override
	public void method_6007() {
		if (this.field_6002 instanceof class_3218 && this.method_5805()) {
			class_3765 lv = this.method_16478();
			if (this.method_16481()) {
				if (lv == null) {
					if (this.field_6002.method_8510() % 20L == 0L) {
						class_3765 lv2 = ((class_3218)this.field_6002).method_19502(new class_2338(this));
						if (lv2 != null && class_3767.method_16838(this, lv2)) {
							lv2.method_16516(lv2.method_16490(), this, null, true);
						}
					}
				} else {
					class_1309 lv3 = this.method_5968();
					if (lv3 != null && (lv3.method_5864() == class_1299.field_6097 || lv3.method_5864() == class_1299.field_6147)) {
						this.field_6278 = 0;
					}
				}
			}
		}

		super.method_6007();
	}

	@Override
	protected void method_16827() {
		this.field_6278 += 2;
	}

	@Override
	public void method_6078(class_1282 arg) {
		if (this.field_6002 instanceof class_3218) {
			class_1297 lv = arg.method_5529();
			if (this.method_16478() != null) {
				if (this.method_16219()) {
					this.method_16478().method_16500(this.method_16486());
				}

				if (lv != null && lv.method_5864() == class_1299.field_6097) {
					this.method_16478().method_20017(lv);
				}

				this.method_16478().method_16510(this, false);
			}

			if (this.method_16219() && this.method_16478() == null && ((class_3218)this.field_6002).method_19502(new class_2338(this)) == null) {
				class_1799 lv2 = this.method_6118(class_1304.field_6169);
				class_1657 lv3 = null;
				if (lv instanceof class_1657) {
					lv3 = (class_1657)lv;
				} else if (lv instanceof class_1493) {
					class_1493 lv5 = (class_1493)lv;
					class_1309 lv6 = lv5.method_6177();
					if (lv5.method_6181() && lv6 instanceof class_1657) {
						lv3 = (class_1657)lv6;
					}
				}

				if (!lv2.method_7960() && class_1799.method_7973(lv2, class_3765.field_16609) && lv3 != null) {
					class_1293 lv7 = lv3.method_6112(class_1294.field_16595);
					int i = 1;
					if (lv7 != null) {
						i += lv7.method_5578();
						lv3.method_6111(class_1294.field_16595);
					} else {
						i--;
					}

					i = class_3532.method_15340(i, 0, 5);
					class_1293 lv8 = new class_1293(class_1294.field_16595, 120000, i, false, false, true);
					lv3.method_6092(lv8);
				}
			}
		}

		super.method_6078(arg);
	}

	@Override
	public boolean method_16472() {
		return !this.method_16482();
	}

	public void method_16476(@Nullable class_3765 arg) {
		this.field_16599 = arg;
	}

	@Nullable
	public class_3765 method_16478() {
		return this.field_16599;
	}

	public boolean method_16482() {
		return this.method_16478() != null && this.method_16478().method_16504();
	}

	public void method_16477(int i) {
		this.field_16601 = i;
	}

	public int method_16486() {
		return this.field_16601;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_20034() {
		return this.field_6011.method_12789(field_19032);
	}

	public void method_20036(boolean bl) {
		this.field_6011.method_12778(field_19032, bl);
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("Wave", this.field_16601);
		arg.method_10556("CanJoinRaid", this.field_16602);
		if (this.field_16599 != null) {
			arg.method_10569("RaidId", this.field_16599.method_16494());
		}
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.field_16601 = arg.method_10550("Wave");
		this.field_16602 = arg.method_10577("CanJoinRaid");
		if (arg.method_10573("RaidId", 3)) {
			if (this.field_6002 instanceof class_3218) {
				this.field_16599 = ((class_3218)this.field_6002).method_19495().method_16541(arg.method_10550("RaidId"));
			}

			if (this.field_16599 != null) {
				this.field_16599.method_16487(this.field_16601, this, false);
				if (this.method_16219()) {
					this.field_16599.method_16491(this.field_16601, this);
				}
			}
		}
	}

	@Override
	protected void method_5949(class_1542 arg) {
		class_1799 lv = arg.method_6983();
		boolean bl = this.method_16482() && this.method_16478().method_16496(this.method_16486()) != null;
		if (this.method_16482() && !bl && class_1799.method_7973(lv, class_3765.field_16609)) {
			class_1304 lv2 = class_1304.field_6169;
			class_1799 lv3 = this.method_6118(lv2);
			double d = (double)this.method_5929(lv2);
			if (!lv3.method_7960() && (double)(this.field_5974.nextFloat() - 0.1F) < d) {
				this.method_5775(lv3);
			}

			this.method_5673(lv2, lv);
			this.method_6103(arg, lv.method_7947());
			arg.method_5650();
			this.method_16478().method_16491(this.method_16486(), this);
			this.method_16217(true);
		} else {
			super.method_5949(arg);
		}
	}

	@Override
	public boolean method_5974(double d) {
		return this.method_16478() == null ? super.method_5974(d) : false;
	}

	@Override
	public boolean method_17326() {
		return this.method_16478() != null;
	}

	public int method_16836() {
		return this.field_16997;
	}

	public void method_16835(int i) {
		this.field_16997 = i;
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		if (this.method_16482()) {
			this.method_16478().method_16523();
		}

		return super.method_5643(arg, f);
	}

	public abstract class_3414 method_20033();

	public class class_3764<T extends class_3763> extends class_1352 {
		private final T field_16603;

		public class_3764(T arg2) {
			this.field_16603 = arg2;
			this.method_6265(EnumSet.of(class_1352.class_4134.field_18405));
		}

		@Override
		public boolean method_6264() {
			class_3765 lv = this.field_16603.method_16478();
			if (this.field_16603.method_16482()
				&& !this.field_16603.method_16478().method_16832()
				&& this.field_16603.method_16485()
				&& !class_1799.method_7973(this.field_16603.method_6118(class_1304.field_6169), class_3765.field_16609)) {
				class_3763 lv2 = lv.method_16496(this.field_16603.method_16486());
				if (lv2 == null || !lv2.method_5805()) {
					List<class_1542> list = this.field_16603
						.field_6002
						.method_8390(class_1542.class, this.field_16603.method_5829().method_1009(16.0, 8.0, 16.0), class_3763.field_16600);
					if (!list.isEmpty()) {
						return this.field_16603.method_5942().method_6335((class_1297)list.get(0), 1.15F);
					}
				}

				return false;
			} else {
				return false;
			}
		}

		@Override
		public void method_6268() {
			if (this.field_16603.method_5942().method_6355().method_19769(this.field_16603.method_19538(), 1.414)) {
				List<class_1542> list = this.field_16603
					.field_6002
					.method_8390(class_1542.class, this.field_16603.method_5829().method_1009(4.0, 4.0, 4.0), class_3763.field_16600);
				if (!list.isEmpty()) {
					this.field_16603.method_5949((class_1542)list.get(0));
				}
			}
		}
	}

	public class class_4223 extends class_1352 {
		private final class_3763 field_18883;
		private final float field_18884;
		public final class_4051 field_18881 = new class_4051().method_18418(8.0).method_18423().method_18417().method_18421().method_18422().method_18424();

		public class_4223(class_1543 arg2, float f) {
			this.field_18883 = arg2;
			this.field_18884 = f * f;
			this.method_6265(EnumSet.of(class_1352.class_4134.field_18405, class_1352.class_4134.field_18406));
		}

		@Override
		public boolean method_6264() {
			class_1309 lv = this.field_18883.method_6065();
			return this.field_18883.method_16478() == null
				&& this.field_18883.method_16915()
				&& this.field_18883.method_5968() != null
				&& !this.field_18883.method_6510()
				&& (lv == null || lv.method_5864() != class_1299.field_6097);
		}

		@Override
		public void method_6269() {
			super.method_6269();
			this.field_18883.method_5942().method_6340();

			for (class_3763 lv : this.field_18883
				.field_6002
				.method_18466(class_3763.class, this.field_18881, this.field_18883, this.field_18883.method_5829().method_1009(8.0, 8.0, 8.0))) {
				lv.method_5980(this.field_18883.method_5968());
			}
		}

		@Override
		public void method_6270() {
			super.method_6270();
			class_1309 lv = this.field_18883.method_5968();
			if (lv != null) {
				for (class_3763 lv2 : this.field_18883
					.field_6002
					.method_18466(class_3763.class, this.field_18881, this.field_18883, this.field_18883.method_5829().method_1009(8.0, 8.0, 8.0))) {
					lv2.method_5980(lv);
					lv2.method_19540(true);
				}

				this.field_18883.method_19540(true);
			}
		}

		@Override
		public void method_6268() {
			class_1309 lv = this.field_18883.method_5968();
			if (lv != null) {
				if (this.field_18883.method_5858(lv) > (double)this.field_18884) {
					this.field_18883.method_5988().method_6226(lv, 30.0F, 30.0F);
					if (this.field_18883.field_5974.nextInt(50) == 0) {
						this.field_18883.method_5966();
					}
				} else {
					this.field_18883.method_19540(true);
				}

				super.method_6268();
			}
		}
	}

	public class class_4260 extends class_1352 {
		private final class_3763 field_19034;

		class_4260(class_3763 arg2) {
			this.field_19034 = arg2;
			this.method_6265(EnumSet.of(class_1352.class_4134.field_18405));
		}

		@Override
		public boolean method_6264() {
			class_3765 lv = this.field_19034.method_16478();
			return this.field_19034.method_5805() && this.field_19034.method_5968() == null && lv != null && lv.method_20024();
		}

		@Override
		public void method_6269() {
			this.field_19034.method_20036(true);
			super.method_6269();
		}

		@Override
		public void method_6270() {
			this.field_19034.method_20036(false);
			super.method_6270();
		}

		@Override
		public void method_6268() {
			if (!this.field_19034.method_5701() && this.field_19034.field_5974.nextInt(100) == 0) {
				class_3763.this.method_5783(class_3763.this.method_20033(), class_3763.this.method_6107(), class_3763.this.method_6017());
			}

			if (!this.field_19034.method_5765() && this.field_19034.field_5974.nextInt(50) == 0) {
				this.field_19034.method_5993().method_6233();
			}

			super.method_6268();
		}
	}

	static class class_4261 extends class_1352 {
		private final class_3763 field_19035;
		private final double field_19036;
		private class_2338 field_19037;
		private final List<class_2338> field_19038 = Lists.<class_2338>newArrayList();
		private final int field_19039;
		private boolean field_19040;

		public class_4261(class_3763 arg, double d, int i) {
			this.field_19035 = arg;
			this.field_19036 = d;
			this.field_19039 = i;
			this.method_6265(EnumSet.of(class_1352.class_4134.field_18405));
		}

		@Override
		public boolean method_6264() {
			this.method_20041();
			return this.method_20039() && this.method_20040();
		}

		private boolean method_20039() {
			return this.field_19035.method_16482() && !this.field_19035.method_16478().method_16832();
		}

		private boolean method_20040() {
			class_3218 lv = (class_3218)this.field_19035.field_6002;
			class_2338 lv2 = new class_2338(this.field_19035);
			Optional<class_2338> optional = lv.method_19494()
				.method_20005(arg -> arg == class_4158.field_18517, this::method_20038, class_4153.class_4155.field_18489, lv2, 48, this.field_19035.field_5974);
			if (!optional.isPresent()) {
				return false;
			} else {
				this.field_19037 = ((class_2338)optional.get()).method_10062();
				return true;
			}
		}

		@Override
		public boolean method_6266() {
			return this.field_19035.method_5942().method_6357()
				? false
				: !this.field_19037.method_19769(this.field_19035.method_19538(), (double)(this.field_19035.method_17681() + (float)this.field_19039)) && !this.field_19040;
		}

		@Override
		public void method_6270() {
			if (this.field_19037.method_19769(this.field_19035.method_19538(), (double)this.field_19039)) {
				this.field_19038.add(this.field_19037);
			}
		}

		@Override
		public void method_6269() {
			super.method_6269();
			this.field_19035.method_16826(0);
			this.field_19035
				.method_5942()
				.method_6337((double)this.field_19037.method_10263(), (double)this.field_19037.method_10264(), (double)this.field_19037.method_10260(), this.field_19036);
			this.field_19040 = false;
		}

		@Override
		public void method_6268() {
			if (this.field_19035.method_5942().method_6357()) {
				int i = this.field_19037.method_10263();
				int j = this.field_19037.method_10264();
				int k = this.field_19037.method_10260();
				class_243 lv = class_1414.method_6377(this.field_19035, 16, 7, new class_243((double)i, (double)j, (double)k), (float) (Math.PI / 10));
				if (lv == null) {
					lv = class_1414.method_6373(this.field_19035, 8, 7, new class_243((double)i, (double)j, (double)k));
				}

				if (lv == null) {
					this.field_19040 = true;
					return;
				}

				this.field_19035.method_5942().method_6337(lv.field_1352, lv.field_1351, lv.field_1350, this.field_19036);
			}
		}

		private boolean method_20038(class_2338 arg) {
			for (class_2338 lv : this.field_19038) {
				if (Objects.equals(arg, lv)) {
					return false;
				}
			}

			return true;
		}

		private void method_20041() {
			if (this.field_19038.size() > 2) {
				this.field_19038.remove(0);
			}
		}
	}
}
