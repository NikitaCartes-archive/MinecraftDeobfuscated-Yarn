package net.minecraft.data.server.loottable.onetwenty;

import net.minecraft.data.server.loottable.EntityLootTableGenerator;
import net.minecraft.data.server.loottable.vanilla.VanillaEntityLootTableGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;

public class OneTwentyEntityLootTableGenerator extends EntityLootTableGenerator {
	protected OneTwentyEntityLootTableGenerator() {
		super(FeatureSet.of(FeatureFlags.UPDATE_1_20, FeatureFlags.VANILLA), FeatureSet.of(FeatureFlags.UPDATE_1_20));
	}

	@Override
	public void generate() {
		this.register(EntityType.CAMEL, LootTable.builder());
		this.register(
			EntityType.ELDER_GUARDIAN,
			VanillaEntityLootTableGenerator.createElderGuardianTableBuilder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(EmptyEntry.builder().weight(4))
						.with(ItemEntry.builder(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1))
				)
		);
	}
}
