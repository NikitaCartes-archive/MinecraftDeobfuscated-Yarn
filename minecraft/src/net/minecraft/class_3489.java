package net.minecraft;

import java.util.Collection;
import java.util.Optional;

public class class_3489 {
	private static class_3503<class_1792> field_15530 = new class_3503<>(arg -> Optional.empty(), "", false, "");
	private static int field_15559;
	public static final class_3494<class_1792> field_15544 = method_15102("wool");
	public static final class_3494<class_1792> field_15537 = method_15102("planks");
	public static final class_3494<class_1792> field_15531 = method_15102("stone_bricks");
	public static final class_3494<class_1792> field_15555 = method_15102("wooden_buttons");
	public static final class_3494<class_1792> field_15551 = method_15102("buttons");
	public static final class_3494<class_1792> field_15542 = method_15102("carpets");
	public static final class_3494<class_1792> field_15552 = method_15102("wooden_doors");
	public static final class_3494<class_1792> field_15557 = method_15102("wooden_stairs");
	public static final class_3494<class_1792> field_15534 = method_15102("wooden_slabs");
	public static final class_3494<class_1792> field_17620 = method_15102("wooden_fences");
	public static final class_3494<class_1792> field_15540 = method_15102("wooden_pressure_plates");
	public static final class_3494<class_1792> field_15550 = method_15102("wooden_trapdoors");
	public static final class_3494<class_1792> field_15553 = method_15102("doors");
	public static final class_3494<class_1792> field_15528 = method_15102("saplings");
	public static final class_3494<class_1792> field_15539 = method_15102("logs");
	public static final class_3494<class_1792> field_15546 = method_15102("dark_oak_logs");
	public static final class_3494<class_1792> field_15545 = method_15102("oak_logs");
	public static final class_3494<class_1792> field_15554 = method_15102("birch_logs");
	public static final class_3494<class_1792> field_15525 = method_15102("acacia_logs");
	public static final class_3494<class_1792> field_15538 = method_15102("jungle_logs");
	public static final class_3494<class_1792> field_15549 = method_15102("spruce_logs");
	public static final class_3494<class_1792> field_15556 = method_15102("banners");
	public static final class_3494<class_1792> field_15532 = method_15102("sand");
	public static final class_3494<class_1792> field_15526 = method_15102("stairs");
	public static final class_3494<class_1792> field_15535 = method_15102("slabs");
	public static final class_3494<class_1792> field_15560 = method_15102("walls");
	public static final class_3494<class_1792> field_15547 = method_15102("anvil");
	public static final class_3494<class_1792> field_15529 = method_15102("rails");
	public static final class_3494<class_1792> field_15558 = method_15102("leaves");
	public static final class_3494<class_1792> field_15548 = method_15102("trapdoors");
	public static final class_3494<class_1792> field_15543 = method_15102("small_flowers");
	public static final class_3494<class_1792> field_16444 = method_15102("beds");
	public static final class_3494<class_1792> field_16585 = method_15102("fences");
	public static final class_3494<class_1792> field_15536 = method_15102("boats");
	public static final class_3494<class_1792> field_15527 = method_15102("fishes");
	public static final class_3494<class_1792> field_15533 = method_15102("signs");
	public static final class_3494<class_1792> field_15541 = method_15102("music_discs");
	public static final class_3494<class_1792> field_17487 = method_15102("coals");
	public static final class_3494<class_1792> field_18317 = method_15102("arrows");

	public static void method_15103(class_3503<class_1792> arg) {
		field_15530 = arg;
		field_15559++;
	}

	public static class_3503<class_1792> method_15106() {
		return field_15530;
	}

	private static class_3494<class_1792> method_15102(String string) {
		return new class_3489.class_3490(new class_2960(string));
	}

	public static class class_3490 extends class_3494<class_1792> {
		private int field_15562 = -1;
		private class_3494<class_1792> field_15561;

		public class_3490(class_2960 arg) {
			super(arg);
		}

		public boolean method_15109(class_1792 arg) {
			if (this.field_15562 != class_3489.field_15559) {
				this.field_15561 = class_3489.field_15530.method_15188(this.method_15143());
				this.field_15562 = class_3489.field_15559;
			}

			return this.field_15561.method_15141(arg);
		}

		@Override
		public Collection<class_1792> method_15138() {
			if (this.field_15562 != class_3489.field_15559) {
				this.field_15561 = class_3489.field_15530.method_15188(this.method_15143());
				this.field_15562 = class_3489.field_15559;
			}

			return this.field_15561.method_15138();
		}

		@Override
		public Collection<class_3494.class_3496<class_1792>> method_15139() {
			if (this.field_15562 != class_3489.field_15559) {
				this.field_15561 = class_3489.field_15530.method_15188(this.method_15143());
				this.field_15562 = class_3489.field_15559;
			}

			return this.field_15561.method_15139();
		}
	}
}
