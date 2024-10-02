package net.minecraft.data.server.tag.winterdrop;

import java.util.concurrent.CompletableFuture;
import net.minecraft.block.Block;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.ItemTagProvider;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;

public class WinterDropItemTagProvider extends ItemTagProvider {
	public WinterDropItemTagProvider(
		DataOutput dataOutput,
		CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture,
		CompletableFuture<TagProvider.TagLookup<Item>> completableFuture2,
		CompletableFuture<TagProvider.TagLookup<Block>> completableFuture3
	) {
		super(dataOutput, completableFuture, completableFuture2, completableFuture3);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup registries) {
		this.copy(BlockTags.PLANKS, ItemTags.PLANKS);
		this.copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
		this.copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
		this.copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
		this.copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
		this.copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
		this.copy(BlockTags.FENCE_GATES, ItemTags.FENCE_GATES);
		this.copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
		this.copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
		this.copy(BlockTags.PALE_OAK_LOGS, ItemTags.PALE_OAK_LOGS);
		this.copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
		this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
		this.copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
		this.copy(BlockTags.DIRT, ItemTags.DIRT);
		this.getOrCreateTagBuilder(ItemTags.BOATS).add(Items.PALE_OAK_BOAT);
		this.getOrCreateTagBuilder(ItemTags.CHEST_BOATS).add(Items.PALE_OAK_BOAT);
		this.copy(BlockTags.STANDING_SIGNS, ItemTags.SIGNS);
		this.copy(BlockTags.CEILING_HANGING_SIGNS, ItemTags.HANGING_SIGNS);
	}
}
