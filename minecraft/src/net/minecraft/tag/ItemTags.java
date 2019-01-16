package net.minecraft.tag;

import java.util.Collection;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class ItemTags {
	private static TagContainer<Item> container = new TagContainer<>(identifier -> false, identifier -> null, "", false, "");
	private static int containerChanges;
	public static final Tag<Item> field_15544 = register("wool");
	public static final Tag<Item> field_15537 = register("planks");
	public static final Tag<Item> field_15531 = register("stone_bricks");
	public static final Tag<Item> field_15555 = register("wooden_buttons");
	public static final Tag<Item> field_15551 = register("buttons");
	public static final Tag<Item> field_15542 = register("carpets");
	public static final Tag<Item> field_15552 = register("wooden_doors");
	public static final Tag<Item> field_15557 = register("wooden_stairs");
	public static final Tag<Item> field_15534 = register("wooden_slabs");
	public static final Tag<Item> field_17620 = register("wooden_fences");
	public static final Tag<Item> field_15540 = register("wooden_pressure_plates");
	public static final Tag<Item> field_15550 = register("wooden_trapdoors");
	public static final Tag<Item> field_15553 = register("doors");
	public static final Tag<Item> field_15528 = register("saplings");
	public static final Tag<Item> field_15539 = register("logs");
	public static final Tag<Item> field_15546 = register("dark_oak_logs");
	public static final Tag<Item> field_15545 = register("oak_logs");
	public static final Tag<Item> field_15554 = register("birch_logs");
	public static final Tag<Item> field_15525 = register("acacia_logs");
	public static final Tag<Item> field_15538 = register("jungle_logs");
	public static final Tag<Item> field_15549 = register("spruce_logs");
	public static final Tag<Item> field_15556 = register("banners");
	public static final Tag<Item> field_15532 = register("sand");
	public static final Tag<Item> field_15526 = register("stairs");
	public static final Tag<Item> field_15535 = register("slabs");
	public static final Tag<Item> field_15560 = register("walls");
	public static final Tag<Item> field_15547 = register("anvil");
	public static final Tag<Item> field_15529 = register("rails");
	public static final Tag<Item> field_15558 = register("leaves");
	public static final Tag<Item> field_15548 = register("trapdoors");
	public static final Tag<Item> field_15543 = register("small_flowers");
	public static final Tag<Item> field_16444 = register("beds");
	public static final Tag<Item> field_16585 = register("fences");
	public static final Tag<Item> field_15536 = register("boats");
	public static final Tag<Item> field_15527 = register("fishes");
	public static final Tag<Item> field_15533 = register("signs");
	public static final Tag<Item> field_15541 = register("music_discs");
	public static final Tag<Item> field_17487 = register("coals");

	public static void setContainer(TagContainer<Item> tagContainer) {
		container = tagContainer;
		containerChanges++;
	}

	public static TagContainer<Item> getContainer() {
		return container;
	}

	private static Tag<Item> register(String string) {
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
				this.field_15561 = ItemTags.container.getOrCreate(this.getId());
				this.field_15562 = ItemTags.containerChanges;
			}

			return this.field_15561.contains(item);
		}

		@Override
		public Collection<Item> values() {
			if (this.field_15562 != ItemTags.containerChanges) {
				this.field_15561 = ItemTags.container.getOrCreate(this.getId());
				this.field_15562 = ItemTags.containerChanges;
			}

			return this.field_15561.values();
		}

		@Override
		public Collection<Tag.Entry<Item>> entries() {
			if (this.field_15562 != ItemTags.containerChanges) {
				this.field_15561 = ItemTags.container.getOrCreate(this.getId());
				this.field_15562 = ItemTags.containerChanges;
			}

			return this.field_15561.entries();
		}
	}
}
