package net.minecraft.data.server.tag;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.data.DataOutput;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagBuilder;
import net.minecraft.registry.tag.TagKey;

public abstract class ItemTagProvider extends ValueLookupTagProvider<Item> {
	private final Function<TagKey<Block>, TagBuilder> blockTags;

	public ItemTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture, TagProvider<Block> blockTagProvider) {
		super(output, RegistryKeys.ITEM, registryLookupFuture, item -> item.getRegistryEntry().registryKey());
		this.blockTags = blockTagProvider::getTagBuilder;
	}

	protected void copy(TagKey<Block> blockTag, TagKey<Item> itemTag) {
		TagBuilder tagBuilder = this.getTagBuilder(itemTag);
		TagBuilder tagBuilder2 = (TagBuilder)this.blockTags.apply(blockTag);
		tagBuilder2.build().forEach(tagBuilder::add);
	}
}
