package net.minecraft.data.server.loottable.onetwentyone;

import net.minecraft.data.server.loottable.EntityLootTableGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;

public class OneTwentyOneEntityLootTableGenerator extends EntityLootTableGenerator {
	protected OneTwentyOneEntityLootTableGenerator() {
		super(FeatureSet.of(FeatureFlags.UPDATE_1_21));
	}

	@Override
	public void generate() {
		this.register(
			EntityType.BREEZE,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.WIND_CHARGE).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 6.0F))))
						.conditionally(KilledByPlayerLootCondition.builder())
				)
		);
	}
}
