package net.minecraft.data.server.tag;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataOutput;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.registry.Registry;

public class OneTwentyBlockTagProvider extends AbstractTagProvider<Block> {
	public OneTwentyBlockTagProvider(DataOutput output) {
		super(output, Registry.BLOCK);
	}

	@Override
	protected void configure() {
		this.getOrCreateTagBuilder(BlockTags.PLANKS).add(Blocks.BAMBOO_PLANKS);
		this.getOrCreateTagBuilder(BlockTags.WOODEN_BUTTONS).add(Blocks.BAMBOO_BUTTON);
		this.getOrCreateTagBuilder(BlockTags.WOODEN_DOORS).add(Blocks.BAMBOO_DOOR);
		this.getOrCreateTagBuilder(BlockTags.WOODEN_STAIRS).add(Blocks.BAMBOO_STAIRS, Blocks.BAMBOO_MOSAIC_STAIRS);
		this.getOrCreateTagBuilder(BlockTags.WOODEN_SLABS).add(Blocks.BAMBOO_SLAB, Blocks.BAMBOO_MOSAIC_SLAB);
		this.getOrCreateTagBuilder(BlockTags.WOODEN_FENCES).add(Blocks.BAMBOO_FENCE);
		this.getOrCreateTagBuilder(BlockTags.WOODEN_PRESSURE_PLATES).add(Blocks.BAMBOO_PRESSURE_PLATE);
		this.getOrCreateTagBuilder(BlockTags.WOODEN_TRAPDOORS).add(Blocks.BAMBOO_TRAPDOOR);
		this.getOrCreateTagBuilder(BlockTags.STANDING_SIGNS).add(Blocks.BAMBOO_SIGN);
		this.getOrCreateTagBuilder(BlockTags.WALL_SIGNS).add(Blocks.BAMBOO_WALL_SIGN);
		this.getOrCreateTagBuilder(BlockTags.FENCE_GATES).add(Blocks.BAMBOO_FENCE_GATE);
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
		this.getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).addTag(BlockTags.ALL_HANGING_SIGNS).add(Blocks.BAMBOO_MOSAIC, Blocks.CHISELED_BOOKSHELF);
	}

	@Override
	public String getName() {
		return super.getName() + "@1.20";
	}
}
