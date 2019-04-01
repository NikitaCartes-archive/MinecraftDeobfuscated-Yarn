package net.minecraft;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;

public class class_3924 extends class_2586 implements class_3829, class_3000 {
	private final class_2371<class_1799> field_17383 = class_2371.method_10213(4, class_1799.field_8037);
	private final int[] field_17384 = new int[4];
	private final int[] field_17385 = new int[4];

	public class_3924() {
		super(class_2591.field_17380);
	}

	@Override
	public void method_16896() {
		boolean bl = (Boolean)this.method_11010().method_11654(class_3922.field_17352);
		boolean bl2 = this.field_11863.field_9236;
		if (bl2) {
			if (bl) {
				this.method_17509();
			}
		} else {
			if (bl) {
				this.method_17508();
			} else {
				for (int i = 0; i < this.field_17383.size(); i++) {
					if (this.field_17384[i] > 0) {
						this.field_17384[i] = class_3532.method_15340(this.field_17384[i] - 2, 0, this.field_17385[i]);
					}
				}
			}
		}
	}

	private void method_17508() {
		for (int i = 0; i < this.field_17383.size(); i++) {
			class_1799 lv = this.field_17383.get(i);
			if (!lv.method_7960()) {
				this.field_17384[i]++;
				if (this.field_17384[i] >= this.field_17385[i]) {
					class_1263 lv2 = new class_1277(lv);
					class_1799 lv3 = (class_1799)this.field_11863
						.method_8433()
						.method_8132(class_3956.field_17549, lv2, this.field_11863)
						.map(arg2 -> arg2.method_8116(lv2))
						.orElse(lv);
					class_2338 lv4 = this.method_11016();
					class_1264.method_5449(this.field_11863, (double)lv4.method_10263(), (double)lv4.method_10264(), (double)lv4.method_10260(), lv3);
					this.field_17383.set(i, class_1799.field_8037);
					this.method_17510();
				}
			}
		}
	}

	private void method_17509() {
		class_1937 lv = this.method_10997();
		if (lv != null) {
			class_2338 lv2 = this.method_11016();
			Random random = lv.field_9229;
			if (random.nextFloat() < 0.11F) {
				for (int i = 0; i < random.nextInt(2) + 2; i++) {
					class_3922.method_17455(lv, lv2, (Boolean)this.method_11010().method_11654(class_3922.field_17353), false);
				}
			}

			int i = ((class_2350)this.method_11010().method_11654(class_3922.field_17564)).method_10161();

			for (int j = 0; j < this.field_17383.size(); j++) {
				if (!this.field_17383.get(j).method_7960() && random.nextFloat() < 0.2F) {
					class_2350 lv3 = class_2350.method_10139(Math.floorMod(j + i, 4));
					float f = 0.3125F;
					double d = (double)lv2.method_10263() + 0.5 - (double)((float)lv3.method_10148() * 0.3125F) + (double)((float)lv3.method_10170().method_10148() * 0.3125F);
					double e = (double)lv2.method_10264() + 0.5;
					double g = (double)lv2.method_10260() + 0.5 - (double)((float)lv3.method_10165() * 0.3125F) + (double)((float)lv3.method_10170().method_10165() * 0.3125F);

					for (int k = 0; k < 4; k++) {
						lv.method_8406(class_2398.field_11251, d, e, g, 0.0, 5.0E-4, 0.0);
					}
				}
			}
		}
	}

	public class_2371<class_1799> method_17505() {
		return this.field_17383;
	}

	@Override
	public void method_11014(class_2487 arg) {
		super.method_11014(arg);
		this.field_17383.clear();
		class_1262.method_5429(arg, this.field_17383);
		if (arg.method_10573("CookingTimes", 11)) {
			int[] is = arg.method_10561("CookingTimes");
			System.arraycopy(is, 0, this.field_17384, 0, Math.min(this.field_17385.length, is.length));
		}

		if (arg.method_10573("CookingTotalTimes", 11)) {
			int[] is = arg.method_10561("CookingTotalTimes");
			System.arraycopy(is, 0, this.field_17385, 0, Math.min(this.field_17385.length, is.length));
		}
	}

	@Override
	public class_2487 method_11007(class_2487 arg) {
		this.method_17507(arg);
		arg.method_10539("CookingTimes", this.field_17384);
		arg.method_10539("CookingTotalTimes", this.field_17385);
		return arg;
	}

	private class_2487 method_17507(class_2487 arg) {
		super.method_11007(arg);
		class_1262.method_5427(arg, this.field_17383, true);
		return arg;
	}

	@Nullable
	@Override
	public class_2622 method_16886() {
		return new class_2622(this.field_11867, 13, this.method_16887());
	}

	@Override
	public class_2487 method_16887() {
		return this.method_17507(new class_2487());
	}

	public Optional<class_3920> method_17502(class_1799 arg) {
		return this.field_17383.stream().noneMatch(class_1799::method_7960)
			? Optional.empty()
			: this.field_11863.method_8433().method_8132(class_3956.field_17549, new class_1277(arg), this.field_11863);
	}

	public boolean method_17503(class_1799 arg, int i) {
		for (int j = 0; j < this.field_17383.size(); j++) {
			class_1799 lv = this.field_17383.get(j);
			if (lv.method_7960()) {
				this.field_17385[j] = i;
				this.field_17384[j] = 0;
				this.field_17383.set(j, arg.method_7971(1));
				this.method_17510();
				return true;
			}
		}

		return false;
	}

	private void method_17510() {
		this.method_5431();
		this.method_10997().method_8413(this.method_11016(), this.method_11010(), this.method_11010(), 3);
	}

	@Override
	public void method_5448() {
		this.field_17383.clear();
	}

	public void method_17506() {
		if (!this.method_10997().field_9236) {
			class_1264.method_17349(this.method_10997(), this.method_11016(), this.method_17505());
		}

		this.method_17510();
	}
}
