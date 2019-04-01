package net.minecraft;

import java.util.List;
import java.util.Random;

public class class_3415 {
	private static final class_2338 field_14536 = new class_2338(4, 0, 15);
	private static final class_2960[] field_14534 = new class_2960[]{
		new class_2960("shipwreck/with_mast"),
		new class_2960("shipwreck/sideways_full"),
		new class_2960("shipwreck/sideways_fronthalf"),
		new class_2960("shipwreck/sideways_backhalf"),
		new class_2960("shipwreck/rightsideup_full"),
		new class_2960("shipwreck/rightsideup_fronthalf"),
		new class_2960("shipwreck/rightsideup_backhalf"),
		new class_2960("shipwreck/with_mast_degraded"),
		new class_2960("shipwreck/rightsideup_full_degraded"),
		new class_2960("shipwreck/rightsideup_fronthalf_degraded"),
		new class_2960("shipwreck/rightsideup_backhalf_degraded")
	};
	private static final class_2960[] field_14535 = new class_2960[]{
		new class_2960("shipwreck/with_mast"),
		new class_2960("shipwreck/upsidedown_full"),
		new class_2960("shipwreck/upsidedown_fronthalf"),
		new class_2960("shipwreck/upsidedown_backhalf"),
		new class_2960("shipwreck/sideways_full"),
		new class_2960("shipwreck/sideways_fronthalf"),
		new class_2960("shipwreck/sideways_backhalf"),
		new class_2960("shipwreck/rightsideup_full"),
		new class_2960("shipwreck/rightsideup_fronthalf"),
		new class_2960("shipwreck/rightsideup_backhalf"),
		new class_2960("shipwreck/with_mast_degraded"),
		new class_2960("shipwreck/upsidedown_full_degraded"),
		new class_2960("shipwreck/upsidedown_fronthalf_degraded"),
		new class_2960("shipwreck/upsidedown_backhalf_degraded"),
		new class_2960("shipwreck/sideways_full_degraded"),
		new class_2960("shipwreck/sideways_fronthalf_degraded"),
		new class_2960("shipwreck/sideways_backhalf_degraded"),
		new class_2960("shipwreck/rightsideup_full_degraded"),
		new class_2960("shipwreck/rightsideup_fronthalf_degraded"),
		new class_2960("shipwreck/rightsideup_backhalf_degraded")
	};

	public static void method_14834(class_3485 arg, class_2338 arg2, class_2470 arg3, List<class_3443> list, Random random, class_3172 arg4) {
		class_2960 lv = arg4.field_13803 ? field_14534[random.nextInt(field_14534.length)] : field_14535[random.nextInt(field_14535.length)];
		list.add(new class_3415.class_3416(arg, lv, arg2, arg3, arg4.field_13803));
	}

	public static class class_3416 extends class_3470 {
		private final class_2470 field_14539;
		private final class_2960 field_14537;
		private final boolean field_14538;

		public class_3416(class_3485 arg, class_2960 arg2, class_2338 arg3, class_2470 arg4, boolean bl) {
			super(class_3773.field_16935, 0);
			this.field_15432 = arg3;
			this.field_14539 = arg4;
			this.field_14537 = arg2;
			this.field_14538 = bl;
			this.method_14837(arg);
		}

		public class_3416(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16935, arg2);
			this.field_14537 = new class_2960(arg2.method_10558("Template"));
			this.field_14538 = arg2.method_10577("isBeached");
			this.field_14539 = class_2470.valueOf(arg2.method_10558("Rot"));
			this.method_14837(arg);
		}

		@Override
		protected void method_14943(class_2487 arg) {
			super.method_14943(arg);
			arg.method_10582("Template", this.field_14537.toString());
			arg.method_10556("isBeached", this.field_14538);
			arg.method_10582("Rot", this.field_14539.name());
		}

		private void method_14837(class_3485 arg) {
			class_3499 lv = arg.method_15091(this.field_14537);
			class_3492 lv2 = new class_3492()
				.method_15123(this.field_14539)
				.method_15125(class_2415.field_11302)
				.method_15119(class_3415.field_14536)
				.method_16184(class_3793.field_16721);
			this.method_15027(lv, this.field_15432, lv2);
		}

		@Override
		protected void method_15026(String string, class_2338 arg, class_1936 arg2, Random random, class_3341 arg3) {
			if ("map_chest".equals(string)) {
				class_2621.method_11287(arg2, random, arg.method_10074(), class_39.field_841);
			} else if ("treasure_chest".equals(string)) {
				class_2621.method_11287(arg2, random, arg.method_10074(), class_39.field_665);
			} else if ("supply_chest".equals(string)) {
				class_2621.method_11287(arg2, random, arg.method_10074(), class_39.field_880);
			}
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			int i = 256;
			int j = 0;
			class_2338 lv = this.field_15432.method_10069(this.field_15433.method_15160().method_10263() - 1, 0, this.field_15433.method_15160().method_10260() - 1);

			for (class_2338 lv2 : class_2338.method_10097(this.field_15432, lv)) {
				int k = arg.method_8589(this.field_14538 ? class_2902.class_2903.field_13194 : class_2902.class_2903.field_13195, lv2.method_10263(), lv2.method_10260());
				j += k;
				i = Math.min(i, k);
			}

			j /= this.field_15433.method_15160().method_10263() * this.field_15433.method_15160().method_10260();
			int l = this.field_14538 ? i - this.field_15433.method_15160().method_10264() / 2 - random.nextInt(3) : j;
			this.field_15432 = new class_2338(this.field_15432.method_10263(), l, this.field_15432.method_10260());
			return super.method_14931(arg, random, arg2, arg3);
		}
	}
}
