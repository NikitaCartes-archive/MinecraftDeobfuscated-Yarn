package net.minecraft.data.server.tag.onetwenty;

import java.util.concurrent.CompletableFuture;
import net.minecraft.block.Block;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.ItemTagProvider;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;

public class OneTwentyItemTagProvider extends ItemTagProvider {
	public OneTwentyItemTagProvider(DataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture, TagProvider<Block> tagProvider) {
		super(dataOutput, completableFuture, tagProvider);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup lookup) {
		this.copy(BlockTags.PLANKS, ItemTags.PLANKS);
		this.copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
		this.copy(BlockTags.BUTTONS, ItemTags.BUTTONS);
		this.copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
		this.copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
		this.copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
		this.copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
		this.copy(BlockTags.FENCE_GATES, ItemTags.FENCE_GATES);
		this.copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
		this.copy(BlockTags.DOORS, ItemTags.DOORS);
		this.copy(BlockTags.SLABS, ItemTags.SLABS);
		this.copy(BlockTags.STAIRS, ItemTags.STAIRS);
		this.copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
		this.copy(BlockTags.TRAPDOORS, ItemTags.TRAPDOORS);
		this.copy(BlockTags.FENCES, ItemTags.FENCES);
		this.copy(BlockTags.STANDING_SIGNS, ItemTags.SIGNS);
		this.copy(BlockTags.BAMBOO_BLOCKS, ItemTags.BAMBOO_BLOCKS);
		this.copy(BlockTags.CEILING_HANGING_SIGNS, ItemTags.HANGING_SIGNS);
		this.getOrCreateTagBuilder(ItemTags.CHEST_BOATS).add(Items.BAMBOO_CHEST_RAFT);
		this.getOrCreateTagBuilder(ItemTags.BOATS).add(Items.BAMBOO_RAFT);
		this.getOrCreateTagBuilder(ItemTags.BOOKSHELF_BOOKS).add(Items.BOOK, Items.WRITTEN_BOOK, Items.ENCHANTED_BOOK, Items.WRITABLE_BOOK);
		this.getOrCreateTagBuilder(ItemTags.NON_FLAMMABLE_WOOD).add(Items.WARPED_HANGING_SIGN, Items.CRIMSON_HANGING_SIGN);
		this.getOrCreateTagBuilder(ItemTags.NOTEBLOCK_TOP_INSTRUMENTS)
			.add(Items.ZOMBIE_HEAD, Items.SKELETON_SKULL, Items.CREEPER_HEAD, Items.DRAGON_HEAD, Items.WITHER_SKELETON_SKULL, Items.PIGLIN_HEAD, Items.PLAYER_HEAD);
		this.getOrCreateTagBuilder(ItemTags.TRIMMABLE_ARMOR)
			.add(Items.NETHERITE_HELMET)
			.add(Items.NETHERITE_CHESTPLATE)
			.add(Items.NETHERITE_LEGGINGS)
			.add(Items.NETHERITE_BOOTS)
			.add(Items.DIAMOND_HELMET)
			.add(Items.DIAMOND_CHESTPLATE)
			.add(Items.DIAMOND_LEGGINGS)
			.add(Items.DIAMOND_BOOTS)
			.add(Items.GOLDEN_HELMET)
			.add(Items.GOLDEN_CHESTPLATE)
			.add(Items.GOLDEN_LEGGINGS)
			.add(Items.GOLDEN_BOOTS)
			.add(Items.IRON_HELMET)
			.add(Items.IRON_CHESTPLATE)
			.add(Items.IRON_LEGGINGS)
			.add(Items.IRON_BOOTS)
			.add(Items.CHAINMAIL_HELMET)
			.add(Items.CHAINMAIL_CHESTPLATE)
			.add(Items.CHAINMAIL_LEGGINGS)
			.add(Items.CHAINMAIL_BOOTS)
			.add(Items.TURTLE_HELMET);
		this.getOrCreateTagBuilder(ItemTags.TRIM_MATERIALS)
			.add(Items.IRON_INGOT)
			.add(Items.COPPER_INGOT)
			.add(Items.GOLD_INGOT)
			.add(Items.LAPIS_LAZULI)
			.add(Items.EMERALD)
			.add(Items.DIAMOND)
			.add(Items.NETHERITE_INGOT)
			.add(Items.REDSTONE)
			.add(Items.QUARTZ)
			.add(Items.AMETHYST_SHARD);
		this.getOrCreateTagBuilder(ItemTags.TRIM_TEMPLATES)
			.add(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE)
			.add(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE)
			.add(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE)
			.add(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE)
			.add(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE)
			.add(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE)
			.add(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE)
			.add(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE)
			.add(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE)
			.add(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE)
			.add(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE);
	}
}
