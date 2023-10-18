package net.minecraft.data.server.tag.onetwentyone;

import java.util.concurrent.CompletableFuture;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.data.server.tag.ValueLookupTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

public class OneTwentyOneBlockTagProvider extends ValueLookupTagProvider<Block> {
	public OneTwentyOneBlockTagProvider(
		DataOutput output,
		CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture,
		CompletableFuture<TagProvider.TagLookup<Block>> blockTagLookupFuture
	) {
		super(output, RegistryKeys.BLOCK, registryLookupFuture, blockTagLookupFuture, block -> block.getRegistryEntry().registryKey());
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup lookup) {
		this.getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(Blocks.CRAFTER);
		this.getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL).add(Blocks.CRAFTER);
	}
}
