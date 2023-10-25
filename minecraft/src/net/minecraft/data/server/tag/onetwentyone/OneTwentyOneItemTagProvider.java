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
	}
}
