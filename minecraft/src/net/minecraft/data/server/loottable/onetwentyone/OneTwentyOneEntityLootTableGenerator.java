package net.minecraft.data.server.loottable.onetwentyone;

import net.minecraft.data.server.loottable.EntityLootTableGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.LootingEnchantLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetOminousBottleAmplifierLootFunction;
import net.minecraft.loot.function.SetPotionLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.potion.Potions;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.RaiderPredicate;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;

public class OneTwentyOneEntityLootTableGenerator extends EntityLootTableGenerator {
	protected OneTwentyOneEntityLootTableGenerator() {
		super(FeatureSet.of(FeatureFlags.VANILLA, FeatureFlags.UPDATE_1_21), FeatureSet.of(FeatureFlags.UPDATE_1_21));
	}

	@Override
	public void generate() {
		this.register(
			EntityType.BREEZE,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.BREEZE_ROD)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F)))
						)
						.conditionally(KilledByPlayerLootCondition.builder())
				)
		);
		this.register(
			EntityType.BOGGED,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.ARROW)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.BONE)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)).withLimit(1))
								.apply(SetPotionLootFunction.builder(Potions.POISON))
						)
						.conditionally(KilledByPlayerLootCondition.builder())
				)
		);
		this.register(
			EntityType.PILLAGER,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.OMINOUS_BOTTLE)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetOminousBottleAmplifierLootFunction.builder(UniformLootNumberProvider.create(0.0F, 4.0F)))
						)
						.conditionally(
							EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, EntityPredicate.Builder.create().typeSpecific(RaiderPredicate.CAPTAIN_WITHOUT_RAID))
						)
				)
		);
	}
}
