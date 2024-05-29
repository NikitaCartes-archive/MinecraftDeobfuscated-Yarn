package net.minecraft.data.server.tag;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.block.Block;
import net.minecraft.data.DataOutput;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagBuilder;
import net.minecraft.registry.tag.TagKey;

public abstract class ItemTagProvider extends ValueLookupTagProvider<Item> {
	private final CompletableFuture<TagProvider.TagLookup<Block>> blockTags;
	private final Map<TagKey<Block>, TagKey<Item>> blockTagsToCopy = new HashMap();

	public ItemTagProvider(
		DataOutput output,
		CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture,
		CompletableFuture<TagProvider.TagLookup<Block>> blockTagLookupFuture
	) {
		super(output, RegistryKeys.ITEM, registryLookupFuture, item -> item.getRegistryEntry().registryKey());
		this.blockTags = blockTagLookupFuture;
	}

	public ItemTagProvider(
		DataOutput output,
		CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture,
		CompletableFuture<TagProvider.TagLookup<Item>> parentTagLookupFuture,
		CompletableFuture<TagProvider.TagLookup<Block>> blockTagLookupFuture
	) {
		super(output, RegistryKeys.ITEM, registryLookupFuture, parentTagLookupFuture, item -> item.getRegistryEntry().registryKey());
		this.blockTags = blockTagLookupFuture;
	}

	protected void copy(TagKey<Block> blockTag, TagKey<Item> itemTag) {
		this.blockTagsToCopy.put(blockTag, itemTag);
	}

	@Override
	protected CompletableFuture<RegistryWrapper.WrapperLookup> getRegistryLookupFuture() {
		return super.getRegistryLookupFuture().thenCombine(this.blockTags, (lookup, blockTags) -> {
			this.blockTagsToCopy.forEach((blockTag, itemTag) -> {
				TagBuilder tagBuilder = this.getTagBuilder(itemTag);
				Optional<TagBuilder> optional = (Optional<TagBuilder>)blockTags.apply(blockTag);
				((TagBuilder)optional.orElseThrow(() -> new IllegalStateException("Missing block tag " + itemTag.id()))).build().forEach(tagBuilder::add);
			});
			return lookup;
		});
	}
}
