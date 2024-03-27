package net.minecraft.data.server.tag.onetwentyone;

import java.util.concurrent.CompletableFuture;
import net.minecraft.block.Block;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.ItemTagProvider;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

public class OneTwentyOneItemTagProvider extends ItemTagProvider {
	public OneTwentyOneItemTagProvider(
		DataOutput dataOutput,
		CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture,
		CompletableFuture<TagProvider.TagLookup<Item>> completableFuture2,
		CompletableFuture<TagProvider.TagLookup<Block>> completableFuture3
	) {
		super(dataOutput, completableFuture, completableFuture2, completableFuture3);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup lookup) {
		this.getOrCreateTagBuilder(ItemTags.STAIRS).add(Items.TUFF_STAIRS, Items.POLISHED_TUFF_STAIRS, Items.TUFF_BRICK_STAIRS);
		this.getOrCreateTagBuilder(ItemTags.SLABS).add(Items.TUFF_SLAB, Items.POLISHED_TUFF_SLAB, Items.TUFF_BRICK_SLAB);
		this.getOrCreateTagBuilder(ItemTags.WALLS).add(Items.TUFF_WALL, Items.POLISHED_TUFF_WALL, Items.TUFF_BRICK_WALL);
		this.getOrCreateTagBuilder(ItemTags.DOORS)
			.add(
				Items.COPPER_DOOR,
				Items.EXPOSED_COPPER_DOOR,
				Items.WEATHERED_COPPER_DOOR,
				Items.OXIDIZED_COPPER_DOOR,
				Items.WAXED_COPPER_DOOR,
				Items.WAXED_EXPOSED_COPPER_DOOR,
				Items.WAXED_WEATHERED_COPPER_DOOR,
				Items.WAXED_OXIDIZED_COPPER_DOOR
			);
		this.getOrCreateTagBuilder(ItemTags.TRAPDOORS)
			.add(
				Items.COPPER_TRAPDOOR,
				Items.EXPOSED_COPPER_TRAPDOOR,
				Items.WEATHERED_COPPER_TRAPDOOR,
				Items.OXIDIZED_COPPER_TRAPDOOR,
				Items.WAXED_COPPER_TRAPDOOR,
				Items.WAXED_EXPOSED_COPPER_TRAPDOOR,
				Items.WAXED_WEATHERED_COPPER_TRAPDOOR,
				Items.WAXED_OXIDIZED_COPPER_TRAPDOOR
			);
		this.getOrCreateTagBuilder(ItemTags.MACE_ENCHANTABLE).add(Items.MACE);
		this.getOrCreateTagBuilder(ItemTags.DECORATED_POT_SHERDS).add(Items.FLOW_POTTERY_SHERD, Items.GUSTER_POTTERY_SHERD, Items.SCRAPE_POTTERY_SHERD);
		this.getOrCreateTagBuilder(ItemTags.DECORATED_POT_INGREDIENTS).add(Items.FLOW_POTTERY_SHERD).add(Items.GUSTER_POTTERY_SHERD).add(Items.SCRAPE_POTTERY_SHERD);
		this.getOrCreateTagBuilder(ItemTags.TRIM_TEMPLATES).add(Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE).add(Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE);
		this.getOrCreateTagBuilder(ItemTags.DURABILITY_ENCHANTABLE).add(Items.MACE);
		this.getOrCreateTagBuilder(ItemTags.WEAPON_ENCHANTABLE).add(Items.MACE);
		this.getOrCreateTagBuilder(ItemTags.FIRE_ASPECT_ENCHANTABLE).add(Items.MACE);
		this.getOrCreateTagBuilder(ItemTags.VANISHING_ENCHANTABLE).add(Items.MACE);
	}
}
