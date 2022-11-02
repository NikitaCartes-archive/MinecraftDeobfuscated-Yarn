package net.minecraft.data.server.tag;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.data.DataOutput;
import net.minecraft.item.Item;
import net.minecraft.tag.TagBuilder;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryWrapper;

public abstract class AbstractItemTagProvider extends ValueLookupTagProvider<Item> {
	private final Function<TagKey<Block>, TagBuilder> blockTags;

	public AbstractItemTagProvider(
		DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture, AbstractTagProvider<Block> blockTagProvider
	) {
		super(output, Registry.ITEM_KEY, registryLookupFuture, item -> item.getRegistryEntry().registryKey());
		this.blockTags = blockTagProvider::getTagBuilder;
	}

	protected void copy(TagKey<Block> blockTag, TagKey<Item> itemTag) {
		TagBuilder tagBuilder = this.getTagBuilder(itemTag);
		TagBuilder tagBuilder2 = (TagBuilder)this.blockTags.apply(blockTag);
		tagBuilder2.build().forEach(tagBuilder::add);
	}
}
