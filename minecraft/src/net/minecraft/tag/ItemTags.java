package net.minecraft.tag;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class ItemTags {
	private static TagContainer<Item> container = new TagContainer<>(identifier -> Optional.empty(), "", false, "");
	private static int latestVersion;
	public static final Tag<Item> WOOL = register("wool");
	public static final Tag<Item> PLANKS = register("planks");
	public static final Tag<Item> STONE_BRICKS = register("stone_bricks");
	public static final Tag<Item> WOODEN_BUTTONS = register("wooden_buttons");
	public static final Tag<Item> BUTTONS = register("buttons");
	public static final Tag<Item> CARPETS = register("carpets");
	public static final Tag<Item> WOODEN_DOORS = register("wooden_doors");
	public static final Tag<Item> WOODEN_STAIRS = register("wooden_stairs");
	public static final Tag<Item> WOODEN_SLABS = register("wooden_slabs");
	public static final Tag<Item> WOODEN_FENCES = register("wooden_fences");
	public static final Tag<Item> WOODEN_PRESSURE_PLATES = register("wooden_pressure_plates");
	public static final Tag<Item> WOODEN_TRAPDOORS = register("wooden_trapdoors");
	public static final Tag<Item> DOORS = register("doors");
	public static final Tag<Item> SAPLINGS = register("saplings");
	public static final Tag<Item> LOGS = register("logs");
	public static final Tag<Item> DARK_OAK_LOGS = register("dark_oak_logs");
	public static final Tag<Item> OAK_LOGS = register("oak_logs");
	public static final Tag<Item> BIRCH_LOGS = register("birch_logs");
	public static final Tag<Item> ACACIA_LOGS = register("acacia_logs");
	public static final Tag<Item> JUNGLE_LOGS = register("jungle_logs");
	public static final Tag<Item> SPRUCE_LOGS = register("spruce_logs");
	public static final Tag<Item> BANNERS = register("banners");
	public static final Tag<Item> SAND = register("sand");
	public static final Tag<Item> STAIRS = register("stairs");
	public static final Tag<Item> SLABS = register("slabs");
	public static final Tag<Item> WALLS = register("walls");
	public static final Tag<Item> ANVIL = register("anvil");
	public static final Tag<Item> RAILS = register("rails");
	public static final Tag<Item> LEAVES = register("leaves");
	public static final Tag<Item> TRAPDOORS = register("trapdoors");
	public static final Tag<Item> SMALL_FLOWERS = register("small_flowers");
	public static final Tag<Item> BEDS = register("beds");
	public static final Tag<Item> FENCES = register("fences");
	public static final Tag<Item> TALL_FLOWERS = register("tall_flowers");
	public static final Tag<Item> FLOWERS = register("flowers");
	public static final Tag<Item> BOATS = register("boats");
	public static final Tag<Item> FISHES = register("fishes");
	public static final Tag<Item> SIGNS = register("signs");
	public static final Tag<Item> MUSIC_DISCS = register("music_discs");
	public static final Tag<Item> COALS = register("coals");
	public static final Tag<Item> ARROWS = register("arrows");
	public static final Tag<Item> LECTERN_BOOKS = register("lectern_books");

	public static void setContainer(TagContainer<Item> container) {
		ItemTags.container = container;
		latestVersion++;
	}

	public static TagContainer<Item> getContainer() {
		return container;
	}

	private static Tag<Item> register(String id) {
		return new ItemTags.CachingTag(new Identifier(id));
	}

	public static class CachingTag extends Tag<Item> {
		private int version = -1;
		private Tag<Item> delegate;

		public CachingTag(Identifier identifier) {
			super(identifier);
		}

		public boolean method_15109(Item item) {
			if (this.version != ItemTags.latestVersion) {
				this.delegate = ItemTags.container.getOrCreate(this.getId());
				this.version = ItemTags.latestVersion;
			}

			return this.delegate.contains(item);
		}

		@Override
		public Collection<Item> values() {
			if (this.version != ItemTags.latestVersion) {
				this.delegate = ItemTags.container.getOrCreate(this.getId());
				this.version = ItemTags.latestVersion;
			}

			return this.delegate.values();
		}

		@Override
		public Collection<Tag.Entry<Item>> entries() {
			if (this.version != ItemTags.latestVersion) {
				this.delegate = ItemTags.container.getOrCreate(this.getId());
				this.version = ItemTags.latestVersion;
			}

			return this.delegate.entries();
		}
	}
}
