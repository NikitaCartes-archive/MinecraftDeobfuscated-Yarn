package net.minecraft.tag;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class ItemTags {
	private static TagContainer<Item> field_15530 = new TagContainer<>(identifier -> Optional.empty(), "", false, "");
	private static int containerChanges;
	public static final Tag<Item> field_15544 = method_15102("wool");
	public static final Tag<Item> field_15537 = method_15102("planks");
	public static final Tag<Item> field_15531 = method_15102("stone_bricks");
	public static final Tag<Item> field_15555 = method_15102("wooden_buttons");
	public static final Tag<Item> field_15551 = method_15102("buttons");
	public static final Tag<Item> field_15542 = method_15102("carpets");
	public static final Tag<Item> field_15552 = method_15102("wooden_doors");
	public static final Tag<Item> field_15557 = method_15102("wooden_stairs");
	public static final Tag<Item> field_15534 = method_15102("wooden_slabs");
	public static final Tag<Item> field_17620 = method_15102("wooden_fences");
	public static final Tag<Item> field_15540 = method_15102("wooden_pressure_plates");
	public static final Tag<Item> field_15550 = method_15102("wooden_trapdoors");
	public static final Tag<Item> field_15553 = method_15102("doors");
	public static final Tag<Item> field_15528 = method_15102("saplings");
	public static final Tag<Item> field_15539 = method_15102("logs");
	public static final Tag<Item> field_15546 = method_15102("dark_oak_logs");
	public static final Tag<Item> field_15545 = method_15102("oak_logs");
	public static final Tag<Item> field_15554 = method_15102("birch_logs");
	public static final Tag<Item> field_15525 = method_15102("acacia_logs");
	public static final Tag<Item> field_15538 = method_15102("jungle_logs");
	public static final Tag<Item> field_15549 = method_15102("spruce_logs");
	public static final Tag<Item> field_15556 = method_15102("banners");
	public static final Tag<Item> field_15532 = method_15102("sand");
	public static final Tag<Item> field_15526 = method_15102("stairs");
	public static final Tag<Item> field_15535 = method_15102("slabs");
	public static final Tag<Item> field_15560 = method_15102("walls");
	public static final Tag<Item> field_15547 = method_15102("anvil");
	public static final Tag<Item> field_15529 = method_15102("rails");
	public static final Tag<Item> field_15558 = method_15102("leaves");
	public static final Tag<Item> field_15548 = method_15102("trapdoors");
	public static final Tag<Item> field_15543 = method_15102("small_flowers");
	public static final Tag<Item> field_16444 = method_15102("beds");
	public static final Tag<Item> field_16585 = method_15102("fences");
	public static final Tag<Item> field_15536 = method_15102("boats");
	public static final Tag<Item> field_15527 = method_15102("fishes");
	public static final Tag<Item> field_15533 = method_15102("signs");
	public static final Tag<Item> field_15541 = method_15102("music_discs");
	public static final Tag<Item> field_17487 = method_15102("coals");
	public static final Tag<Item> field_18317 = method_15102("arrows");

	public static void method_15103(TagContainer<Item> tagContainer) {
		field_15530 = tagContainer;
		containerChanges++;
	}

	public static TagContainer<Item> method_15106() {
		return field_15530;
	}

	private static Tag<Item> method_15102(String string) {
		return new ItemTags.class_3490(new Identifier(string));
	}

	public static class class_3490 extends Tag<Item> {
		private int field_15562 = -1;
		private Tag<Item> field_15561;

		public class_3490(Identifier identifier) {
			super(identifier);
		}

		public boolean method_15109(Item item) {
			if (this.field_15562 != ItemTags.containerChanges) {
				this.field_15561 = ItemTags.field_15530.getOrCreate(this.getId());
				this.field_15562 = ItemTags.containerChanges;
			}

			return this.field_15561.contains(item);
		}

		@Override
		public Collection<Item> values() {
			if (this.field_15562 != ItemTags.containerChanges) {
				this.field_15561 = ItemTags.field_15530.getOrCreate(this.getId());
				this.field_15562 = ItemTags.containerChanges;
			}

			return this.field_15561.values();
		}

		@Override
		public Collection<Tag.Entry<Item>> entries() {
			if (this.field_15562 != ItemTags.containerChanges) {
				this.field_15561 = ItemTags.field_15530.getOrCreate(this.getId());
				this.field_15562 = ItemTags.containerChanges;
			}

			return this.field_15561.entries();
		}
	}
}
