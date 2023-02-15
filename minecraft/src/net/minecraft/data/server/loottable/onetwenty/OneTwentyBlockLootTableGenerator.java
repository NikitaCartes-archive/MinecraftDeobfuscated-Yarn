package net.minecraft.data.server.loottable.onetwenty;

import java.util.Set;
import net.minecraft.block.Blocks;
import net.minecraft.block.TorchflowerBlock;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;

public class OneTwentyBlockLootTableGenerator extends BlockLootTableGenerator {
	protected OneTwentyBlockLootTableGenerator() {
		super(Set.of(), FeatureSet.of(FeatureFlags.UPDATE_1_20));
	}

	@Override
	protected void generate() {
		this.addDrop(Blocks.BAMBOO_BLOCK);
		this.addDrop(Blocks.STRIPPED_BAMBOO_BLOCK);
		this.addDrop(Blocks.BAMBOO_PLANKS);
		this.addDrop(Blocks.BAMBOO_MOSAIC);
		this.addDrop(Blocks.BAMBOO_STAIRS);
		this.addDrop(Blocks.BAMBOO_MOSAIC_STAIRS);
		this.addDrop(Blocks.BAMBOO_SIGN);
		this.addDrop(Blocks.OAK_HANGING_SIGN);
		this.addDrop(Blocks.SPRUCE_HANGING_SIGN);
		this.addDrop(Blocks.BIRCH_HANGING_SIGN);
		this.addDrop(Blocks.ACACIA_HANGING_SIGN);
		this.addDrop(Blocks.CHERRY_HANGING_SIGN);
		this.addDrop(Blocks.JUNGLE_HANGING_SIGN);
		this.addDrop(Blocks.DARK_OAK_HANGING_SIGN);
		this.addDrop(Blocks.MANGROVE_HANGING_SIGN);
		this.addDrop(Blocks.CRIMSON_HANGING_SIGN);
		this.addDrop(Blocks.WARPED_HANGING_SIGN);
		this.addDrop(Blocks.BAMBOO_HANGING_SIGN);
		this.addDrop(Blocks.BAMBOO_PRESSURE_PLATE);
		this.addDrop(Blocks.BAMBOO_FENCE);
		this.addDrop(Blocks.BAMBOO_TRAPDOOR);
		this.addDrop(Blocks.BAMBOO_FENCE_GATE);
		this.addDrop(Blocks.BAMBOO_BUTTON);
		this.addDrop(Blocks.BAMBOO_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.BAMBOO_MOSAIC_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.BAMBOO_DOOR, block -> this.doorDrops(block));
		this.addDropWithSilkTouch(Blocks.CHISELED_BOOKSHELF);
		this.addDrop(Blocks.DECORATED_POT, dropsNothing());
		this.addDrop(Blocks.PIGLIN_HEAD);
		this.addDrop(Blocks.TORCHFLOWER);
		this.addPottedPlantDrops(Blocks.POTTED_TORCHFLOWER);
		BlockStatePropertyLootCondition.Builder builder = BlockStatePropertyLootCondition.builder(Blocks.TORCHFLOWER_CROP)
			.properties(StatePredicate.Builder.create().exactMatch(TorchflowerBlock.field_42776, 2));
		this.addDrop(
			Blocks.TORCHFLOWER_CROP,
			this.applyExplosionDecay(
				Blocks.TORCHFLOWER_CROP,
				LootTable.builder()
					.pool(LootPool.builder().with(ItemEntry.builder(Items.TORCHFLOWER).conditionally(builder).alternatively(ItemEntry.builder(Items.TORCHFLOWER_SEEDS))))
			)
		);
		this.addDrop(Blocks.CHERRY_PLANKS);
		this.addDrop(Blocks.CHERRY_SAPLING);
		this.addDrop(Blocks.CHERRY_LOG);
		this.addDrop(Blocks.STRIPPED_CHERRY_LOG);
		this.addDrop(Blocks.CHERRY_WOOD);
		this.addDrop(Blocks.STRIPPED_CHERRY_WOOD);
		this.addDrop(Blocks.CHERRY_SIGN);
		this.addDrop(Blocks.CHERRY_PRESSURE_PLATE);
		this.addDrop(Blocks.CHERRY_TRAPDOOR);
		this.addDrop(Blocks.CHERRY_BUTTON);
		this.addDrop(Blocks.CHERRY_STAIRS);
		this.addDrop(Blocks.CHERRY_FENCE_GATE);
		this.addDrop(Blocks.CHERRY_FENCE);
		this.addPottedPlantDrops(Blocks.POTTED_CHERRY_SAPLING);
		this.addDrop(Blocks.CHERRY_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.CHERRY_DOOR, block -> this.doorDrops(block));
		this.addDrop(Blocks.CHERRY_LEAVES, block -> this.leavesDrops(block, Blocks.CHERRY_SAPLING, SAPLING_DROP_CHANCE));
		this.addDrop(Blocks.PINK_PETALS, this.flowerbedDrops(Blocks.PINK_PETALS));
		this.addDrop(Blocks.SUSPICIOUS_SAND, dropsNothing());
	}
}
