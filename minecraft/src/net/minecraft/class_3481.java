package net.minecraft;

import java.util.Collection;

public class class_3481 {
	private static class_3503<class_2248> field_15484 = new class_3503<>(arg -> false, arg -> null, "", false, "");
	private static int field_15473;
	public static final class_3494<class_2248> field_15481 = method_15069("wool");
	public static final class_3494<class_2248> field_15471 = method_15069("planks");
	public static final class_3494<class_2248> field_15465 = method_15069("stone_bricks");
	public static final class_3494<class_2248> field_15499 = method_15069("wooden_buttons");
	public static final class_3494<class_2248> field_15493 = method_15069("buttons");
	public static final class_3494<class_2248> field_15479 = method_15069("carpets");
	public static final class_3494<class_2248> field_15494 = method_15069("wooden_doors");
	public static final class_3494<class_2248> field_15502 = method_15069("wooden_stairs");
	public static final class_3494<class_2248> field_15468 = method_15069("wooden_slabs");
	public static final class_3494<class_2248> field_15477 = method_15069("wooden_pressure_plates");
	public static final class_3494<class_2248> field_15491 = method_15069("wooden_trapdoors");
	public static final class_3494<class_2248> field_15495 = method_15069("doors");
	public static final class_3494<class_2248> field_15462 = method_15069("saplings");
	public static final class_3494<class_2248> field_15475 = method_15069("logs");
	public static final class_3494<class_2248> field_15485 = method_15069("dark_oak_logs");
	public static final class_3494<class_2248> field_15482 = method_15069("oak_logs");
	public static final class_3494<class_2248> field_15498 = method_15069("birch_logs");
	public static final class_3494<class_2248> field_15458 = method_15069("acacia_logs");
	public static final class_3494<class_2248> field_15474 = method_15069("jungle_logs");
	public static final class_3494<class_2248> field_15489 = method_15069("spruce_logs");
	public static final class_3494<class_2248> field_15501 = method_15069("banners");
	public static final class_3494<class_2248> field_15466 = method_15069("sand");
	public static final class_3494<class_2248> field_15459 = method_15069("stairs");
	public static final class_3494<class_2248> field_15469 = method_15069("slabs");
	public static final class_3494<class_2248> field_15504 = method_15069("walls");
	public static final class_3494<class_2248> field_15486 = method_15069("anvil");
	public static final class_3494<class_2248> field_15463 = method_15069("rails");
	public static final class_3494<class_2248> field_15503 = method_15069("leaves");
	public static final class_3494<class_2248> field_15487 = method_15069("trapdoors");
	public static final class_3494<class_2248> field_15480 = method_15069("small_flowers");
	public static final class_3494<class_2248> field_16443 = method_15069("beds");
	public static final class_3494<class_2248> field_16584 = method_15069("fences");
	public static final class_3494<class_2248> field_15470 = method_15069("flower_pots");
	public static final class_3494<class_2248> field_15460 = method_15069("enderman_holdable");
	public static final class_3494<class_2248> field_15467 = method_15069("ice");
	public static final class_3494<class_2248> field_15478 = method_15069("valid_spawn");
	public static final class_3494<class_2248> field_15490 = method_15069("impermeable");
	public static final class_3494<class_2248> field_15496 = method_15069("underwater_bonemeals");
	public static final class_3494<class_2248> field_15461 = method_15069("coral_blocks");
	public static final class_3494<class_2248> field_15476 = method_15069("wall_corals");
	public static final class_3494<class_2248> field_15483 = method_15069("coral_plants");
	public static final class_3494<class_2248> field_15488 = method_15069("corals");
	public static final class_3494<class_2248> field_15497 = method_15069("bamboo_plantable_on");
	public static final class_3494<class_2248> field_15464 = method_15069("dirt_like");
	public static final class_3494<class_2248> field_15472 = method_15069("standing_signs");
	public static final class_3494<class_2248> field_15492 = method_15069("wall_signs");
	public static final class_3494<class_2248> field_15500 = method_15069("signs");

	public static void method_15070(class_3503<class_2248> arg) {
		field_15484 = arg;
		field_15473++;
	}

	public static class_3503<class_2248> method_15073() {
		return field_15484;
	}

	private static class_3494<class_2248> method_15069(String string) {
		return new class_3481.class_3482(new class_2960(string));
	}

	static class class_3482 extends class_3494<class_2248> {
		private int field_15506 = -1;
		private class_3494<class_2248> field_15505;

		public class_3482(class_2960 arg) {
			super(arg);
		}

		public boolean method_15076(class_2248 arg) {
			if (this.field_15506 != class_3481.field_15473) {
				this.field_15505 = class_3481.field_15484.method_15188(this.method_15143());
				this.field_15506 = class_3481.field_15473;
			}

			return this.field_15505.method_15141(arg);
		}

		@Override
		public Collection<class_2248> method_15138() {
			if (this.field_15506 != class_3481.field_15473) {
				this.field_15505 = class_3481.field_15484.method_15188(this.method_15143());
				this.field_15506 = class_3481.field_15473;
			}

			return this.field_15505.method_15138();
		}

		@Override
		public Collection<class_3494.class_3496<class_2248>> method_15139() {
			if (this.field_15506 != class_3481.field_15473) {
				this.field_15505 = class_3481.field_15484.method_15188(this.method_15143());
				this.field_15506 = class_3481.field_15473;
			}

			return this.field_15505.method_15139();
		}
	}
}
