package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;

public class class_1382 extends class_1367 {
	private final class_2248 field_6587;
	private final class_1308 field_6589;
	private int field_6588;

	public class_1382(class_2248 arg, class_1314 arg2, double d, int i) {
		super(arg2, d, 24, i);
		this.field_6587 = arg;
		this.field_6589 = arg2;
	}

	@Override
	public boolean method_6264() {
		if (!this.field_6589.field_6002.method_8450().method_8355("mobGriefing")) {
			return false;
		} else {
			return this.field_6589.method_6051().nextInt(20) != 0 ? false : super.method_6264();
		}
	}

	@Override
	protected int method_6293(class_1314 arg) {
		return 0;
	}

	@Override
	public boolean method_6266() {
		return super.method_6266();
	}

	@Override
	public void method_6270() {
		super.method_6270();
		this.field_6589.field_6017 = 1.0F;
	}

	@Override
	public void method_6269() {
		super.method_6269();
		this.field_6588 = 0;
	}

	public void method_6307(class_1936 arg, class_2338 arg2) {
	}

	public void method_6309(class_1937 arg, class_2338 arg2) {
	}

	@Override
	public void method_6268() {
		super.method_6268();
		class_1937 lv = this.field_6589.field_6002;
		class_2338 lv2 = new class_2338(this.field_6589);
		class_2338 lv3 = this.method_6308(lv2, lv);
		Random random = this.field_6589.method_6051();
		if (this.method_6295() && lv3 != null) {
			if (this.field_6588 > 0) {
				this.field_6589.field_5984 = 0.3;
				if (!lv.field_9236) {
					double d = 0.08;
					((class_3218)lv)
						.method_14199(
							new class_2392(class_2398.field_11218, new class_1799(class_1802.field_8803)),
							(double)lv3.method_10263() + 0.5,
							(double)lv3.method_10264() + 0.7,
							(double)lv3.method_10260() + 0.5,
							3,
							((double)random.nextFloat() - 0.5) * 0.08,
							((double)random.nextFloat() - 0.5) * 0.08,
							((double)random.nextFloat() - 0.5) * 0.08,
							0.15F
						);
				}
			}

			if (this.field_6588 % 2 == 0) {
				this.field_6589.field_5984 = -0.3;
				if (this.field_6588 % 6 == 0) {
					this.method_6307(lv, this.field_6512);
				}
			}

			if (this.field_6588 > 60) {
				lv.method_8650(lv3);
				if (!lv.field_9236) {
					for (int i = 0; i < 20; i++) {
						double e = random.nextGaussian() * 0.02;
						double f = random.nextGaussian() * 0.02;
						double g = random.nextGaussian() * 0.02;
						((class_3218)lv)
							.method_14199(class_2398.field_11203, (double)lv3.method_10263() + 0.5, (double)lv3.method_10264(), (double)lv3.method_10260() + 0.5, 1, e, f, g, 0.15F);
					}

					this.method_6309(lv, this.field_6512);
				}
			}

			this.field_6588++;
		}
	}

	@Nullable
	private class_2338 method_6308(class_2338 arg, class_1922 arg2) {
		if (arg2.method_8320(arg).method_11614() == this.field_6587) {
			return arg;
		} else {
			class_2338[] lvs = new class_2338[]{
				arg.method_10074(), arg.method_10067(), arg.method_10078(), arg.method_10095(), arg.method_10072(), arg.method_10074().method_10074()
			};

			for (class_2338 lv : lvs) {
				if (arg2.method_8320(lv).method_11614() == this.field_6587) {
					return lv;
				}
			}

			return null;
		}
	}

	@Override
	protected boolean method_6296(class_1941 arg, class_2338 arg2) {
		class_2248 lv = arg.method_8320(arg2).method_11614();
		return lv == this.field_6587 && arg.method_8320(arg2.method_10084()).method_11588() && arg.method_8320(arg2.method_10086(2)).method_11588();
	}
}
