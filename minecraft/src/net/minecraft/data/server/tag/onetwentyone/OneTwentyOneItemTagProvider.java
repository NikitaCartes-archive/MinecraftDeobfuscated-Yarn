package net.minecraft.data.server.tag.onetwentyone;

import java.util.concurrent.CompletableFuture;
import net.minecraft.block.Block;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.ItemTagProvider;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;

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
	}
}
