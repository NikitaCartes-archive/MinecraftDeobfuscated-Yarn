package net.minecraft;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;

public abstract class class_3732 extends class_1588 {
	private class_2338 field_16478;
	private boolean field_16479;
	private boolean field_16477;

	protected class_3732(class_1299<? extends class_3732> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	protected void method_5959() {
		super.method_5959();
		this.field_6201.method_6277(4, new class_3732.class_3733<>(this, 0.7, 0.595));
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		if (this.field_16478 != null) {
			arg.method_10566("PatrolTarget", class_2512.method_10692(this.field_16478));
		}

		arg.method_10556("PatrolLeader", this.field_16479);
		arg.method_10556("Patrolling", this.field_16477);
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		if (arg.method_10545("PatrolTarget")) {
			this.field_16478 = class_2512.method_10691(arg.method_10562("PatrolTarget"));
		}

		this.field_16479 = arg.method_10577("PatrolLeader");
		this.field_16477 = arg.method_10577("Patrolling");
	}

	@Override
	public double method_5678() {
		return -0.45;
	}

	public boolean method_16485() {
		return true;
	}

	@Nullable
	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		if (arg3 != class_3730.field_16527
			&& arg3 != class_3730.field_16467
			&& arg3 != class_3730.field_16474
			&& this.field_5974.nextFloat() < 0.06F
			&& this.method_16485()) {
			this.field_16479 = true;
		}

		if (this.method_16219()) {
			this.method_5673(class_1304.field_6169, class_3765.method_16515());
			this.method_5946(class_1304.field_6169, 2.0F);
		}

		if (arg3 == class_3730.field_16527) {
			this.field_16477 = true;
		}

		return super.method_5943(arg, arg2, arg3, arg4, arg5);
	}

	public static boolean method_20739(class_1299<? extends class_3732> arg, class_1936 arg2, class_3730 arg3, class_2338 arg4, Random random) {
		return arg2.method_8314(class_1944.field_9282, arg4) > 8 ? false : method_20681(arg, arg2, arg3, arg4, random);
	}

	@Override
	public boolean method_5974(double d) {
		return !this.field_16477 || d > 16384.0;
	}

	public void method_16216(class_2338 arg) {
		this.field_16478 = arg;
		this.field_16477 = true;
	}

	public class_2338 method_16215() {
		return this.field_16478;
	}

	public boolean method_16220() {
		return this.field_16478 != null;
	}

	public void method_16217(boolean bl) {
		this.field_16479 = bl;
		this.field_16477 = true;
	}

	public boolean method_16219() {
		return this.field_16479;
	}

	public boolean method_16472() {
		return true;
	}

	public void method_16218() {
		this.field_16478 = new class_2338(this).method_10069(-500 + this.field_5974.nextInt(1000), 0, -500 + this.field_5974.nextInt(1000));
		this.field_16477 = true;
	}

	protected boolean method_16915() {
		return this.field_16477;
	}

	public static class class_3733<T extends class_3732> extends class_1352 {
		private final T field_16481;
		private final double field_16480;
		private final double field_16535;

		public class_3733(T arg, double d, double e) {
			this.field_16481 = arg;
			this.field_16480 = d;
			this.field_16535 = e;
			this.method_6265(EnumSet.of(class_1352.class_4134.field_18405));
		}

		@Override
		public boolean method_6264() {
			return this.field_16481.method_16915() && this.field_16481.method_5968() == null && !this.field_16481.method_5782() && this.field_16481.method_16220();
		}

		@Override
		public void method_6269() {
		}

		@Override
		public void method_6270() {
		}

		@Override
		public void method_6268() {
			boolean bl = this.field_16481.method_16219();
			class_1408 lv = this.field_16481.method_5942();
			if (lv.method_6357()) {
				if (bl && this.field_16481.method_16215().method_19769(this.field_16481.method_19538(), 10.0)) {
					this.field_16481.method_16218();
				} else {
					class_243 lv2 = new class_243(this.field_16481.method_16215());
					class_243 lv3 = new class_243(this.field_16481.field_5987, this.field_16481.field_6010, this.field_16481.field_6035);
					class_243 lv4 = lv3.method_1020(lv2);
					lv2 = lv4.method_1024(90.0F).method_1021(0.4).method_1019(lv2);
					class_243 lv5 = lv2.method_1020(lv3).method_1029().method_1021(10.0).method_1019(lv3);
					class_2338 lv6 = new class_2338(lv5);
					lv6 = this.field_16481.field_6002.method_8598(class_2902.class_2903.field_13203, lv6);
					if (!lv.method_6337((double)lv6.method_10263(), (double)lv6.method_10264(), (double)lv6.method_10260(), bl ? this.field_16535 : this.field_16480)) {
						this.method_16222();
					} else if (bl) {
						for (class_3732 lv7 : this.field_16481
							.field_6002
							.method_8390(class_3732.class, this.field_16481.method_5829().method_1014(16.0), arg -> !arg.method_16219() && arg.method_16472())) {
							lv7.method_16216(lv6);
						}
					}
				}
			}
		}

		private void method_16222() {
			Random random = this.field_16481.method_6051();
			class_2338 lv = this.field_16481
				.field_6002
				.method_8598(class_2902.class_2903.field_13203, new class_2338(this.field_16481).method_10069(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
			this.field_16481.method_5942().method_6337((double)lv.method_10263(), (double)lv.method_10264(), (double)lv.method_10260(), this.field_16480);
		}
	}
}
