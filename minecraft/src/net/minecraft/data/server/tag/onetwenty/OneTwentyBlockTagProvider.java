package net.minecraft.data.server.tag.onetwenty;

import java.util.concurrent.CompletableFuture;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.ValueLookupTagProvider;
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
				Blocks.CHERRY_HANGING_SIGN,
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
				Blocks.CHERRY_WALL_HANGING_SIGN,
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
		this.getOrCreateTagBuilder(BlockTags.SNIFFER_DIGGABLE_BLOCK)
			.add(Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.PODZOL, Blocks.COARSE_DIRT, Blocks.ROOTED_DIRT, Blocks.MOSS_BLOCK, Blocks.MUD, Blocks.MUDDY_MANGROVE_ROOTS);
		this.getOrCreateTagBuilder(BlockTags.CHERRY_LOGS).add(Blocks.CHERRY_LOG, Blocks.CHERRY_WOOD, Blocks.STRIPPED_CHERRY_LOG, Blocks.STRIPPED_CHERRY_WOOD);
		this.getOrCreateTagBuilder(BlockTags.PLANKS).add(Blocks.CHERRY_PLANKS);
		this.getOrCreateTagBuilder(BlockTags.WOODEN_BUTTONS).add(Blocks.CHERRY_BUTTON);
		this.getOrCreateTagBuilder(BlockTags.WOODEN_DOORS).add(Blocks.CHERRY_DOOR);
		this.getOrCreateTagBuilder(BlockTags.WOODEN_STAIRS).add(Blocks.CHERRY_STAIRS);
		this.getOrCreateTagBuilder(BlockTags.WOODEN_SLABS).add(Blocks.CHERRY_SLAB);
		this.getOrCreateTagBuilder(BlockTags.WOODEN_FENCES).add(Blocks.CHERRY_FENCE);
		this.getOrCreateTagBuilder(BlockTags.SAPLINGS).add(Blocks.CHERRY_SAPLING);
		this.getOrCreateTagBuilder(BlockTags.LOGS_THAT_BURN).addTag(BlockTags.CHERRY_LOGS);
		this.getOrCreateTagBuilder(BlockTags.OVERWORLD_NATURAL_LOGS).add(Blocks.CHERRY_LOG);
		this.getOrCreateTagBuilder(BlockTags.FLOWER_POTS).add(Blocks.FLOWER_POT);
		this.getOrCreateTagBuilder(BlockTags.WOODEN_PRESSURE_PLATES).add(Blocks.CHERRY_PRESSURE_PLATE);
		this.getOrCreateTagBuilder(BlockTags.LEAVES).add(Blocks.CHERRY_LEAVES);
		this.getOrCreateTagBuilder(BlockTags.WOODEN_TRAPDOORS).add(Blocks.CHERRY_TRAPDOOR);
		this.getOrCreateTagBuilder(BlockTags.STANDING_SIGNS).add(Blocks.CHERRY_SIGN);
		this.getOrCreateTagBuilder(BlockTags.WALL_SIGNS).add(Blocks.CHERRY_WALL_SIGN);
		this.getOrCreateTagBuilder(BlockTags.FENCE_GATES).add(Blocks.CHERRY_FENCE_GATE);
		this.getOrCreateTagBuilder(BlockTags.HOE_MINEABLE).add(Blocks.CHERRY_LEAVES);
		this.getOrCreateTagBuilder(BlockTags.FLOWERS).add(Blocks.CHERRY_LEAVES, Blocks.PINK_PETALS);
		this.getOrCreateTagBuilder(BlockTags.INSIDE_STEP_SOUND_BLOCKS).add(Blocks.PINK_PETALS);
		this.getOrCreateTagBuilder(BlockTags.HOE_MINEABLE).add(Blocks.PINK_PETALS);
		this.getOrCreateTagBuilder(BlockTags.SAND).add(Blocks.SUSPICIOUS_SAND);
	}
}
