package net.minecraft.data.server.tag;

import java.util.concurrent.CompletableFuture;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataOutput;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

public class OneTwentyBlockTagProvider extends ValueLookupTagProvider<Block> {
	public OneTwentyBlockTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		super(output, RegistryKeys.BLOCK, registryLookupFuture, block -> block.getRegistryEntry().registryKey());
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup lookup) {
		this.getOrCreateTagBuilder(BlockTags.PLANKS).add(Blocks.BAMBOO_PLANKS);
		this.getOrCreateTagBuilder(BlockTags.WOODEN_BUTTONS).add(Blocks.BAMBOO_BUTTON);
		this.getOrCreateTagBuilder(BlockTags.WOODEN_DOORS).add(Blocks.BAMBOO_DOOR);
		this.getOrCreateTagBuilder(BlockTags.WOODEN_STAIRS).add(Blocks.BAMBOO_STAIRS);
		this.getOrCreateTagBuilder(BlockTags.WOODEN_SLABS).add(Blocks.BAMBOO_SLAB);
		this.getOrCreateTagBuilder(BlockTags.WOODEN_FENCES).add(Blocks.BAMBOO_FENCE);
		this.getOrCreateTagBuilder(BlockTags.WOODEN_PRESSURE_PLATES).add(Blocks.BAMBOO_PRESSURE_PLATE);
		this.getOrCreateTagBuilder(BlockTags.WOODEN_TRAPDOORS).add(Blocks.BAMBOO_TRAPDOOR);
		this.getOrCreateTagBuilder(BlockTags.STANDING_SIGNS).add(Blocks.BAMBOO_SIGN);
		this.getOrCreateTagBuilder(BlockTags.WALL_SIGNS).add(Blocks.BAMBOO_WALL_SIGN);
		this.getOrCreateTagBuilder(BlockTags.FENCE_GATES).add(Blocks.BAMBOO_FENCE_GATE);
		this.getOrCreateTagBuilder(BlockTags.BAMBOO_BLOCKS).add(Blocks.BAMBOO_BLOCK, Blocks.STRIPPED_BAMBOO_BLOCK);
		this.getOrCreateTagBuilder(BlockTags.CEILING_HANGING_SIGNS)
			.add(
				Blocks.OAK_HANGING_SIGN,
				Blocks.SPRUCE_HANGING_SIGN,
				Blocks.BIRCH_HANGING_SIGN,
				Blocks.ACACIA_HANGING_SIGN,
				Blocks.JUNGLE_HANGING_SIGN,
				Blocks.DARK_OAK_HANGING_SIGN,
				Blocks.CRIMSON_HANGING_SIGN,
				Blocks.WARPED_HANGING_SIGN,
				Blocks.MANGROVE_HANGING_SIGN,
				Blocks.BAMBOO_HANGING_SIGN
			);
		this.getOrCreateTagBuilder(BlockTags.WALL_HANGING_SIGNS)
			.add(
				Blocks.OAK_WALL_HANGING_SIGN,
				Blocks.SPRUCE_WALL_HANGING_SIGN,
				Blocks.BIRCH_WALL_HANGING_SIGN,
				Blocks.ACACIA_WALL_HANGING_SIGN,
				Blocks.JUNGLE_WALL_HANGING_SIGN,
				Blocks.DARK_OAK_WALL_HANGING_SIGN,
				Blocks.CRIMSON_WALL_HANGING_SIGN,
				Blocks.WARPED_WALL_HANGING_SIGN,
				Blocks.MANGROVE_WALL_HANGING_SIGN,
				Blocks.BAMBOO_WALL_HANGING_SIGN
			);
		this.getOrCreateTagBuilder(BlockTags.ALL_HANGING_SIGNS).addTag(BlockTags.CEILING_HANGING_SIGNS).addTag(BlockTags.WALL_HANGING_SIGNS);
		this.getOrCreateTagBuilder(BlockTags.ALL_SIGNS).addTag(BlockTags.ALL_HANGING_SIGNS);
		this.getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
			.addTag(BlockTags.ALL_HANGING_SIGNS)
			.add(Blocks.BAMBOO_MOSAIC, Blocks.BAMBOO_MOSAIC_SLAB, Blocks.BAMBOO_MOSAIC_STAIRS)
			.addTag(BlockTags.BAMBOO_BLOCKS)
			.add(Blocks.CHISELED_BOOKSHELF);
	}
}
