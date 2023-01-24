/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server.tag.onetwenty;

import java.util.concurrent.CompletableFuture;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.ValueLookupTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

public class OneTwentyBlockTagProvider
extends ValueLookupTagProvider<Block> {
    public OneTwentyBlockTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        super(output, RegistryKeys.BLOCK, registryLookupFuture, block -> block.getRegistryEntry().registryKey());
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup lookup) {
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)BlockTags.PLANKS)).add(Blocks.BAMBOO_PLANKS);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)BlockTags.WOODEN_BUTTONS)).add(Blocks.BAMBOO_BUTTON);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)BlockTags.WOODEN_DOORS)).add(Blocks.BAMBOO_DOOR);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)BlockTags.WOODEN_STAIRS)).add(Blocks.BAMBOO_STAIRS);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)BlockTags.WOODEN_SLABS)).add(Blocks.BAMBOO_SLAB);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)BlockTags.WOODEN_FENCES)).add(Blocks.BAMBOO_FENCE);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)BlockTags.WOODEN_PRESSURE_PLATES)).add(Blocks.BAMBOO_PRESSURE_PLATE);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)BlockTags.WOODEN_TRAPDOORS)).add(Blocks.BAMBOO_TRAPDOOR);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)BlockTags.STANDING_SIGNS)).add(Blocks.BAMBOO_SIGN);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)BlockTags.WALL_SIGNS)).add(Blocks.BAMBOO_WALL_SIGN);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)BlockTags.FENCE_GATES)).add(Blocks.BAMBOO_FENCE_GATE);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)BlockTags.BAMBOO_BLOCKS)).add(Blocks.BAMBOO_BLOCK, Blocks.STRIPPED_BAMBOO_BLOCK);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)BlockTags.CEILING_HANGING_SIGNS)).add(Blocks.OAK_HANGING_SIGN, Blocks.SPRUCE_HANGING_SIGN, Blocks.BIRCH_HANGING_SIGN, Blocks.ACACIA_HANGING_SIGN, Blocks.JUNGLE_HANGING_SIGN, Blocks.DARK_OAK_HANGING_SIGN, Blocks.CRIMSON_HANGING_SIGN, Blocks.WARPED_HANGING_SIGN, Blocks.MANGROVE_HANGING_SIGN, Blocks.BAMBOO_HANGING_SIGN);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)BlockTags.WALL_HANGING_SIGNS)).add(Blocks.OAK_WALL_HANGING_SIGN, Blocks.SPRUCE_WALL_HANGING_SIGN, Blocks.BIRCH_WALL_HANGING_SIGN, Blocks.ACACIA_WALL_HANGING_SIGN, Blocks.JUNGLE_WALL_HANGING_SIGN, Blocks.DARK_OAK_WALL_HANGING_SIGN, Blocks.CRIMSON_WALL_HANGING_SIGN, Blocks.WARPED_WALL_HANGING_SIGN, Blocks.MANGROVE_WALL_HANGING_SIGN, Blocks.BAMBOO_WALL_HANGING_SIGN);
        ((ValueLookupTagProvider.ObjectBuilder)((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)BlockTags.ALL_HANGING_SIGNS)).addTag((TagKey)BlockTags.CEILING_HANGING_SIGNS)).addTag((TagKey)BlockTags.WALL_HANGING_SIGNS);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)BlockTags.ALL_SIGNS)).addTag((TagKey)BlockTags.ALL_HANGING_SIGNS);
        ((ValueLookupTagProvider.ObjectBuilder)((ValueLookupTagProvider.ObjectBuilder)((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)BlockTags.AXE_MINEABLE)).addTag((TagKey)BlockTags.ALL_HANGING_SIGNS)).add(Blocks.BAMBOO_MOSAIC, Blocks.BAMBOO_MOSAIC_SLAB, Blocks.BAMBOO_MOSAIC_STAIRS).addTag((TagKey)BlockTags.BAMBOO_BLOCKS)).add(Blocks.CHISELED_BOOKSHELF);
    }
}

