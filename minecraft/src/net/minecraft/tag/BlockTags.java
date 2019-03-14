package net.minecraft.tag;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

public class BlockTags {
	private static TagContainer<Block> container = new TagContainer<>(identifier -> Optional.empty(), "", false, "");
	private static int containerChanges;
	public static final Tag<Block> field_15481 = register("wool");
	public static final Tag<Block> field_15471 = register("planks");
	public static final Tag<Block> field_15465 = register("stone_bricks");
	public static final Tag<Block> field_15499 = register("wooden_buttons");
	public static final Tag<Block> field_15493 = register("buttons");
	public static final Tag<Block> field_15479 = register("carpets");
	public static final Tag<Block> field_15494 = register("wooden_doors");
	public static final Tag<Block> field_15502 = register("wooden_stairs");
	public static final Tag<Block> field_15468 = register("wooden_slabs");
	public static final Tag<Block> field_17619 = register("wooden_fences");
	public static final Tag<Block> field_15477 = register("wooden_pressure_plates");
	public static final Tag<Block> field_15491 = register("wooden_trapdoors");
	public static final Tag<Block> field_15495 = register("doors");
	public static final Tag<Block> field_15462 = register("saplings");
	public static final Tag<Block> field_15475 = register("logs");
	public static final Tag<Block> field_15485 = register("dark_oak_logs");
	public static final Tag<Block> field_15482 = register("oak_logs");
	public static final Tag<Block> field_15498 = register("birch_logs");
	public static final Tag<Block> field_15458 = register("acacia_logs");
	public static final Tag<Block> field_15474 = register("jungle_logs");
	public static final Tag<Block> field_15489 = register("spruce_logs");
	public static final Tag<Block> field_15501 = register("banners");
	public static final Tag<Block> field_15466 = register("sand");
	public static final Tag<Block> field_15459 = register("stairs");
	public static final Tag<Block> field_15469 = register("slabs");
	public static final Tag<Block> field_15504 = register("walls");
	public static final Tag<Block> field_15486 = register("anvil");
	public static final Tag<Block> field_15463 = register("rails");
	public static final Tag<Block> field_15503 = register("leaves");
	public static final Tag<Block> field_15487 = register("trapdoors");
	public static final Tag<Block> field_15480 = register("small_flowers");
	public static final Tag<Block> field_16443 = register("beds");
	public static final Tag<Block> field_16584 = register("fences");
	public static final Tag<Block> field_15470 = register("flower_pots");
	public static final Tag<Block> field_15460 = register("enderman_holdable");
	public static final Tag<Block> field_15467 = register("ice");
	public static final Tag<Block> field_15478 = register("valid_spawn");
	public static final Tag<Block> field_15490 = register("impermeable");
	public static final Tag<Block> field_15496 = register("underwater_bonemeals");
	public static final Tag<Block> field_15461 = register("coral_blocks");
	public static final Tag<Block> field_15476 = register("wall_corals");
	public static final Tag<Block> field_15483 = register("coral_plants");
	public static final Tag<Block> field_15488 = register("corals");
	public static final Tag<Block> field_15497 = register("bamboo_plantable_on");
	public static final Tag<Block> field_15464 = register("dirt_like");
	public static final Tag<Block> field_15472 = register("standing_signs");
	public static final Tag<Block> field_15492 = register("wall_signs");
	public static final Tag<Block> field_15500 = register("signs");
	public static final Tag<Block> field_17753 = register("dragon_immune");
	public static final Tag<Block> field_17754 = register("wither_immune");
	public static final Tag<Block> field_18830 = register("unemployed_poi");
	public static final Tag<Block> field_18831 = register("armorer_poi");
	public static final Tag<Block> field_18832 = register("butcher_poi");
	public static final Tag<Block> field_18833 = register("cartographer_poi");
	public static final Tag<Block> field_18834 = register("cleric_poi");
	public static final Tag<Block> field_18835 = register("farmer_poi");
	public static final Tag<Block> field_18836 = register("fisherman_poi");
	public static final Tag<Block> field_18837 = register("fletcher_poi");
	public static final Tag<Block> field_18838 = register("leatherworker_poi");
	public static final Tag<Block> field_18839 = register("librarian_poi");
	public static final Tag<Block> field_18840 = register("mason_poi");
	public static final Tag<Block> field_18841 = register("nitwit_poi");
	public static final Tag<Block> field_18842 = register("shepherd_poi");
	public static final Tag<Block> field_18843 = register("toolsmith_poi");
	public static final Tag<Block> field_18844 = register("weaponsmith_poi");
	public static final Tag<Block> field_18845 = register("job_site_poi");
	public static final Tag<Block> field_18846 = register("meeting_site_poi");
	public static final Tag<Block> field_18847 = register("points_of_interest");

	public static void setContainer(TagContainer<Block> tagContainer) {
		container = tagContainer;
		containerChanges++;
	}

	public static TagContainer<Block> getContainer() {
		return container;
	}

	private static Tag<Block> register(String string) {
		return new BlockTags.class_3482(new Identifier(string));
	}

	static class class_3482 extends Tag<Block> {
		private int field_15506 = -1;
		private Tag<Block> field_15505;

		public class_3482(Identifier identifier) {
			super(identifier);
		}

		public boolean method_15076(Block block) {
			if (this.field_15506 != BlockTags.containerChanges) {
				this.field_15505 = BlockTags.container.getOrCreate(this.getId());
				this.field_15506 = BlockTags.containerChanges;
			}

			return this.field_15505.contains(block);
		}

		@Override
		public Collection<Block> values() {
			if (this.field_15506 != BlockTags.containerChanges) {
				this.field_15505 = BlockTags.container.getOrCreate(this.getId());
				this.field_15506 = BlockTags.containerChanges;
			}

			return this.field_15505.values();
		}

		@Override
		public Collection<Tag.Entry<Block>> entries() {
			if (this.field_15506 != BlockTags.containerChanges) {
				this.field_15505 = BlockTags.container.getOrCreate(this.getId());
				this.field_15506 = BlockTags.containerChanges;
			}

			return this.field_15505.entries();
		}
	}
}
