package net.minecraft.tag;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

public class BlockTags {
	private static TagContainer<Block> field_15484 = new TagContainer<>(identifier -> Optional.empty(), "", false, "");
	private static int containerChanges;
	public static final Tag<Block> field_15481 = method_15069("wool");
	public static final Tag<Block> field_15471 = method_15069("planks");
	public static final Tag<Block> field_15465 = method_15069("stone_bricks");
	public static final Tag<Block> field_15499 = method_15069("wooden_buttons");
	public static final Tag<Block> field_15493 = method_15069("buttons");
	public static final Tag<Block> field_15479 = method_15069("carpets");
	public static final Tag<Block> field_15494 = method_15069("wooden_doors");
	public static final Tag<Block> field_15502 = method_15069("wooden_stairs");
	public static final Tag<Block> field_15468 = method_15069("wooden_slabs");
	public static final Tag<Block> field_17619 = method_15069("wooden_fences");
	public static final Tag<Block> field_15477 = method_15069("wooden_pressure_plates");
	public static final Tag<Block> field_15491 = method_15069("wooden_trapdoors");
	public static final Tag<Block> field_15495 = method_15069("doors");
	public static final Tag<Block> field_15462 = method_15069("saplings");
	public static final Tag<Block> field_15475 = method_15069("logs");
	public static final Tag<Block> field_15485 = method_15069("dark_oak_logs");
	public static final Tag<Block> field_15482 = method_15069("oak_logs");
	public static final Tag<Block> field_15498 = method_15069("birch_logs");
	public static final Tag<Block> field_15458 = method_15069("acacia_logs");
	public static final Tag<Block> field_15474 = method_15069("jungle_logs");
	public static final Tag<Block> field_15489 = method_15069("spruce_logs");
	public static final Tag<Block> field_15501 = method_15069("banners");
	public static final Tag<Block> field_15466 = method_15069("sand");
	public static final Tag<Block> field_15459 = method_15069("stairs");
	public static final Tag<Block> field_15469 = method_15069("slabs");
	public static final Tag<Block> field_15504 = method_15069("walls");
	public static final Tag<Block> field_15486 = method_15069("anvil");
	public static final Tag<Block> field_15463 = method_15069("rails");
	public static final Tag<Block> field_15503 = method_15069("leaves");
	public static final Tag<Block> field_15487 = method_15069("trapdoors");
	public static final Tag<Block> field_15480 = method_15069("small_flowers");
	public static final Tag<Block> field_16443 = method_15069("beds");
	public static final Tag<Block> field_16584 = method_15069("fences");
	public static final Tag<Block> field_15470 = method_15069("flower_pots");
	public static final Tag<Block> field_15460 = method_15069("enderman_holdable");
	public static final Tag<Block> field_15467 = method_15069("ice");
	public static final Tag<Block> field_15478 = method_15069("valid_spawn");
	public static final Tag<Block> field_15490 = method_15069("impermeable");
	public static final Tag<Block> field_15496 = method_15069("underwater_bonemeals");
	public static final Tag<Block> field_15461 = method_15069("coral_blocks");
	public static final Tag<Block> field_15476 = method_15069("wall_corals");
	public static final Tag<Block> field_15483 = method_15069("coral_plants");
	public static final Tag<Block> field_15488 = method_15069("corals");
	public static final Tag<Block> field_15497 = method_15069("bamboo_plantable_on");
	public static final Tag<Block> field_15464 = method_15069("dirt_like");
	public static final Tag<Block> field_15472 = method_15069("standing_signs");
	public static final Tag<Block> field_15492 = method_15069("wall_signs");
	public static final Tag<Block> field_15500 = method_15069("signs");
	public static final Tag<Block> field_17753 = method_15069("dragon_immune");
	public static final Tag<Block> field_17754 = method_15069("wither_immune");
	public static final Tag<Block> field_18830 = method_15069("unemployed_poi");
	public static final Tag<Block> field_18831 = method_15069("armorer_poi");
	public static final Tag<Block> field_18832 = method_15069("butcher_poi");
	public static final Tag<Block> field_18833 = method_15069("cartographer_poi");
	public static final Tag<Block> field_18834 = method_15069("cleric_poi");
	public static final Tag<Block> field_18835 = method_15069("farmer_poi");
	public static final Tag<Block> field_18836 = method_15069("fisherman_poi");
	public static final Tag<Block> field_18837 = method_15069("fletcher_poi");
	public static final Tag<Block> field_18838 = method_15069("leatherworker_poi");
	public static final Tag<Block> field_18839 = method_15069("librarian_poi");
	public static final Tag<Block> field_18840 = method_15069("mason_poi");
	public static final Tag<Block> field_18841 = method_15069("nitwit_poi");
	public static final Tag<Block> field_18842 = method_15069("shepherd_poi");
	public static final Tag<Block> field_18843 = method_15069("toolsmith_poi");
	public static final Tag<Block> field_18844 = method_15069("weaponsmith_poi");
	public static final Tag<Block> field_18845 = method_15069("job_site_poi");
	public static final Tag<Block> field_18846 = method_15069("meeting_site_poi");
	public static final Tag<Block> field_18847 = method_15069("points_of_interest");

	public static void method_15070(TagContainer<Block> tagContainer) {
		field_15484 = tagContainer;
		containerChanges++;
	}

	public static TagContainer<Block> method_15073() {
		return field_15484;
	}

	private static Tag<Block> method_15069(String string) {
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
				this.field_15505 = BlockTags.field_15484.getOrCreate(this.getId());
				this.field_15506 = BlockTags.containerChanges;
			}

			return this.field_15505.contains(block);
		}

		@Override
		public Collection<Block> values() {
			if (this.field_15506 != BlockTags.containerChanges) {
				this.field_15505 = BlockTags.field_15484.getOrCreate(this.getId());
				this.field_15506 = BlockTags.containerChanges;
			}

			return this.field_15505.values();
		}

		@Override
		public Collection<Tag.Entry<Block>> entries() {
			if (this.field_15506 != BlockTags.containerChanges) {
				this.field_15505 = BlockTags.field_15484.getOrCreate(this.getId());
				this.field_15506 = BlockTags.containerChanges;
			}

			return this.field_15505.entries();
		}
	}
}
