package net.minecraft.data.server.tag.bundle;

import java.util.concurrent.CompletableFuture;
import net.minecraft.block.Block;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.ItemTagProvider;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

public class BundleItemTagProvider extends ItemTagProvider {
	public BundleItemTagProvider(
		DataOutput dataOutput,
		CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture,
		CompletableFuture<TagProvider.TagLookup<Item>> completableFuture2,
		CompletableFuture<TagProvider.TagLookup<Block>> completableFuture3
	) {
		super(dataOutput, completableFuture, completableFuture2, completableFuture3);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup registries) {
		this.getOrCreateTagBuilder(ItemTags.BUNDLES)
			.add(
				Items.BUNDLE,
				Items.BLACK_BUNDLE,
				Items.BLUE_BUNDLE,
				Items.BROWN_BUNDLE,
				Items.CYAN_BUNDLE,
				Items.GRAY_BUNDLE,
				Items.GREEN_BUNDLE,
				Items.LIGHT_BLUE_BUNDLE,
				Items.LIGHT_GRAY_BUNDLE,
				Items.LIME_BUNDLE,
				Items.MAGENTA_BUNDLE,
				Items.ORANGE_BUNDLE,
				Items.PINK_BUNDLE,
				Items.PURPLE_BUNDLE,
				Items.RED_BUNDLE,
				Items.YELLOW_BUNDLE,
				Items.WHITE_BUNDLE
			);
	}
}
