package net.minecraft.tag;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

public class BlockTags {
	private static TagContainer<Block> container = new TagContainer<>(identifier -> Optional.empty(), "", false, "");
	private static int latestVersion;
	public static final Tag<Block> WOOL = register("wool");
	public static final Tag<Block> PLANKS = register("planks");
	public static final Tag<Block> STONE_BRICKS = register("stone_bricks");
	public static final Tag<Block> WOODEN_BUTTONS = register("wooden_buttons");
	public static final Tag<Block> BUTTONS = register("buttons");
	public static final Tag<Block> CARPETS = register("carpets");
	public static final Tag<Block> WOODEN_DOORS = register("wooden_doors");
	public static final Tag<Block> WOODEN_STAIRS = register("wooden_stairs");
	public static final Tag<Block> WOODEN_SLABS = register("wooden_slabs");
	public static final Tag<Block> WOODEN_FENCES = register("wooden_fences");
	public static final Tag<Block> WOODEN_PRESSURE_PLATES = register("wooden_pressure_plates");
	public static final Tag<Block> WOODEN_TRAPDOORS = register("wooden_trapdoors");
	public static final Tag<Block> DOORS = register("doors");
	public static final Tag<Block> SAPLINGS = register("saplings");
	public static final Tag<Block> LOGS = register("logs");
	public static final Tag<Block> DARK_OAK_LOGS = register("dark_oak_logs");
	public static final Tag<Block> OAK_LOGS = register("oak_logs");
	public static final Tag<Block> BIRCH_LOGS = register("birch_logs");
	public static final Tag<Block> ACACIA_LOGS = register("acacia_logs");
	public static final Tag<Block> JUNGLE_LOGS = register("jungle_logs");
	public static final Tag<Block> SPRUCE_LOGS = register("spruce_logs");
	public static final Tag<Block> BANNERS = register("banners");
	public static final Tag<Block> SAND = register("sand");
	public static final Tag<Block> STAIRS = register("stairs");
	public static final Tag<Block> SLABS = register("slabs");
	public static final Tag<Block> WALLS = register("walls");
	public static final Tag<Block> ANVIL = register("anvil");
	public static final Tag<Block> RAILS = register("rails");
	public static final Tag<Block> LEAVES = register("leaves");
	public static final Tag<Block> TRAPDOORS = register("trapdoors");
	public static final Tag<Block> SMALL_FLOWERS = register("small_flowers");
	public static final Tag<Block> BEDS = register("beds");
	public static final Tag<Block> FENCES = register("fences");
	public static final Tag<Block> TALL_FLOWERS = register("tall_flowers");
	public static final Tag<Block> FLOWERS = register("flowers");
	public static final Tag<Block> SHULKER_BOXES = register("shulker_boxes");
	public static final Tag<Block> FLOWER_POTS = register("flower_pots");
	public static final Tag<Block> ENDERMAN_HOLDABLE = register("enderman_holdable");
	public static final Tag<Block> ICE = register("ice");
	public static final Tag<Block> VALID_SPAWN = register("valid_spawn");
	public static final Tag<Block> IMPERMEABLE = register("impermeable");
	public static final Tag<Block> UNDERWATER_BONEMEALS = register("underwater_bonemeals");
	public static final Tag<Block> CORAL_BLOCKS = register("coral_blocks");
	public static final Tag<Block> WALL_CORALS = register("wall_corals");
	public static final Tag<Block> CORAL_PLANTS = register("coral_plants");
	public static final Tag<Block> CORALS = register("corals");
	public static final Tag<Block> BAMBOO_PLANTABLE_ON = register("bamboo_plantable_on");
	public static final Tag<Block> STANDING_SIGNS = register("standing_signs");
	public static final Tag<Block> WALL_SIGNS = register("wall_signs");
	public static final Tag<Block> SIGNS = register("signs");
	public static final Tag<Block> DRAGON_IMMUNE = register("dragon_immune");
	public static final Tag<Block> WITHER_IMMUNE = register("wither_immune");
	public static final Tag<Block> BEEHIVES = register("beehives");
	public static final Tag<Block> CROPS = register("crops");
	public static final Tag<Block> BEE_GROWABLES = register("bee_growables");
	public static final Tag<Block> PORTALS = register("portals");

	public static void setContainer(TagContainer<Block> container) {
		BlockTags.container = container;
		latestVersion++;
	}

	public static TagContainer<Block> getContainer() {
		return container;
	}

	private static Tag<Block> register(String id) {
		return new BlockTags.CachingTag(new Identifier(id));
	}

	static class CachingTag extends Tag<Block> {
		private int version = -1;
		private Tag<Block> delegate;

		public CachingTag(Identifier identifier) {
			super(identifier);
		}

		public boolean contains(Block block) {
			if (this.version != BlockTags.latestVersion) {
				this.delegate = BlockTags.container.getOrCreate(this.getId());
				this.version = BlockTags.latestVersion;
			}

			return this.delegate.contains(block);
		}

		@Override
		public Collection<Block> values() {
			if (this.version != BlockTags.latestVersion) {
				this.delegate = BlockTags.container.getOrCreate(this.getId());
				this.version = BlockTags.latestVersion;
			}

			return this.delegate.values();
		}

		@Override
		public Collection<Tag.Entry<Block>> entries() {
			if (this.version != BlockTags.latestVersion) {
				this.delegate = BlockTags.container.getOrCreate(this.getId());
				this.version = BlockTags.latestVersion;
			}

			return this.delegate.entries();
		}
	}
}
