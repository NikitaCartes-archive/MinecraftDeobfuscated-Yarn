package net.minecraft.data.server.tag.winterdrop;

import java.util.concurrent.CompletableFuture;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.data.server.tag.ValueLookupTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

public class WinterDropBlockTagProvider extends ValueLookupTagProvider<Block> {
	public WinterDropBlockTagProvider(
		DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture, CompletableFuture<TagProvider.TagLookup<Block>> parentTagLookupFuture
	) {
		super(output, RegistryKeys.BLOCK, registriesFuture, parentTagLookupFuture, block -> block.getRegistryEntry().registryKey());
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup registries) {
		this.getOrCreateTagBuilder(BlockTags.PLANKS).add(Blocks.PALE_OAK_PLANKS);
		this.getOrCreateTagBuilder(BlockTags.WOODEN_BUTTONS).add(Blocks.PALE_OAK_BUTTON);
		this.getOrCreateTagBuilder(BlockTags.WOODEN_DOORS).add(Blocks.PALE_OAK_DOOR);
		this.getOrCreateTagBuilder(BlockTags.WOODEN_STAIRS).add(Blocks.PALE_OAK_STAIRS);
		this.getOrCreateTagBuilder(BlockTags.WOODEN_SLABS).add(Blocks.PALE_OAK_SLAB);
		this.getOrCreateTagBuilder(BlockTags.WOODEN_FENCES).add(Blocks.PALE_OAK_FENCE);
		this.getOrCreateTagBuilder(BlockTags.SAPLINGS).add(Blocks.PALE_OAK_SAPLING);
		this.getOrCreateTagBuilder(BlockTags.PALE_OAK_LOGS)
			.add(Blocks.PALE_OAK_LOG, Blocks.PALE_OAK_WOOD, Blocks.STRIPPED_PALE_OAK_LOG, Blocks.STRIPPED_PALE_OAK_WOOD);
		this.getOrCreateTagBuilder(BlockTags.LOGS_THAT_BURN).addTag(BlockTags.PALE_OAK_LOGS);
		this.getOrCreateTagBuilder(BlockTags.OVERWORLD_NATURAL_LOGS).add(Blocks.PALE_OAK_LOG);
		this.getOrCreateTagBuilder(BlockTags.DIRT).add(Blocks.PALE_MOSS_BLOCK);
		this.getOrCreateTagBuilder(BlockTags.FLOWER_POTS).add(Blocks.POTTED_PALE_OAK_SAPLING);
		this.getOrCreateTagBuilder(BlockTags.WOODEN_PRESSURE_PLATES).add(Blocks.PALE_OAK_PRESSURE_PLATE);
		this.getOrCreateTagBuilder(BlockTags.LEAVES).add(Blocks.PALE_OAK_LEAVES);
		this.getOrCreateTagBuilder(BlockTags.WOODEN_TRAPDOORS).add(Blocks.PALE_OAK_TRAPDOOR);
		this.getOrCreateTagBuilder(BlockTags.STANDING_SIGNS).add(Blocks.PALE_OAK_SIGN);
		this.getOrCreateTagBuilder(BlockTags.WALL_SIGNS).add(Blocks.PALE_OAK_WALL_SIGN);
		this.getOrCreateTagBuilder(BlockTags.CEILING_HANGING_SIGNS).add(Blocks.PALE_OAK_HANGING_SIGN);
		this.getOrCreateTagBuilder(BlockTags.WALL_HANGING_SIGNS).add(Blocks.PALE_OAK_WALL_HANGING_SIGN);
		this.getOrCreateTagBuilder(BlockTags.FENCE_GATES).add(Blocks.PALE_OAK_FENCE_GATE);
		this.getOrCreateTagBuilder(BlockTags.HOE_MINEABLE).add(Blocks.PALE_OAK_LEAVES, Blocks.PALE_MOSS_BLOCK, Blocks.PALE_MOSS_CARPET);
		this.getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(Blocks.CREAKING_HEART);
		this.getOrCreateTagBuilder(BlockTags.SWORD_EFFICIENT).add(Blocks.PALE_MOSS_CARPET);
		this.getOrCreateTagBuilder(BlockTags.COMBINATION_STEP_SOUND_BLOCKS).add(Blocks.PALE_MOSS_CARPET);
	}
}
