package net.minecraft.data.server.loottable.vanilla;

import java.util.function.BiConsumer;
import net.minecraft.data.server.loottable.EntityLootTableGenerator;
import net.minecraft.data.server.loottable.LootTableData;
import net.minecraft.data.server.loottable.LootTableGenerator;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.EntitySubPredicateTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;

public record VanillaShearingLootTableGenerator(RegistryWrapper.WrapperLookup registries) implements LootTableGenerator {
	@Override
	public void accept(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> lootTableBiConsumer) {
		lootTableBiConsumer.accept(
			LootTables.BOGGED_SHEARING,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(2.0F))
						.with(ItemEntry.builder(Items.BROWN_MUSHROOM).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
						.with(ItemEntry.builder(Items.RED_MUSHROOM).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
				)
		);
		LootTableData.WOOL_FROM_DYE_COLOR
			.forEach(
				(color, wool) -> lootTableBiConsumer.accept(
						(RegistryKey)LootTables.SHEEP_SHEARING_FROM_DYE_COLOR.get(color),
						LootTable.builder().pool(LootPool.builder().rolls(UniformLootNumberProvider.create(1.0F, 3.0F)).with(ItemEntry.builder(wool)))
					)
			);
		lootTableBiConsumer.accept(
			LootTables.SHEEP_SHEARING, LootTable.builder().pool(EntityLootTableGenerator.createForSheep(LootTables.SHEEP_SHEARING_FROM_DYE_COLOR))
		);
		lootTableBiConsumer.accept(
			LootTables.MOOSHROOM_SHEARING,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.with(
							AlternativeEntry.builder(
								LootTableEntry.builder(LootTables.MOOSHROOM_RED_SHEARING)
									.conditionally(
										EntityPropertiesLootCondition.builder(
											LootContext.EntityTarget.THIS,
											EntityPredicate.Builder.create().typeSpecific(EntitySubPredicateTypes.MOOSHROOM.createPredicate(MooshroomEntity.Type.RED))
										)
									),
								LootTableEntry.builder(LootTables.MOOSHROOM_BROWN_SHEARING)
									.conditionally(
										EntityPropertiesLootCondition.builder(
											LootContext.EntityTarget.THIS,
											EntityPredicate.Builder.create().typeSpecific(EntitySubPredicateTypes.MOOSHROOM.createPredicate(MooshroomEntity.Type.BROWN))
										)
									)
							)
						)
				)
		);
		lootTableBiConsumer.accept(
			LootTables.MOOSHROOM_RED_SHEARING,
			LootTable.builder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(5.0F)).with(ItemEntry.builder(Items.RED_MUSHROOM)))
		);
		lootTableBiConsumer.accept(
			LootTables.MOOSHROOM_BROWN_SHEARING,
			LootTable.builder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(5.0F)).with(ItemEntry.builder(Items.BROWN_MUSHROOM)))
		);
		lootTableBiConsumer.accept(
			LootTables.SNOW_GOLEM_SHEARING,
			LootTable.builder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(Items.CARVED_PUMPKIN)))
		);
	}
}
