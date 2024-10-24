package net.minecraft.data.server.loottable.vanilla;

import java.util.function.BiConsumer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.data.server.loottable.LootTableGenerator;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.item.equipment.trim.ArmorTrimPattern;
import net.minecraft.item.equipment.trim.ArmorTrimPatterns;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.function.SetComponentsLootFunction;
import net.minecraft.loot.function.SetEnchantmentsLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

public record VanillaEquipmentLootTableGenerator(RegistryWrapper.WrapperLookup registries) implements LootTableGenerator {
	@Override
	public void accept(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> lootTableBiConsumer) {
		RegistryWrapper.Impl<ArmorTrimPattern> impl = this.registries.getOrThrow(RegistryKeys.TRIM_PATTERN);
		RegistryWrapper.Impl<ArmorTrimMaterial> impl2 = this.registries.getOrThrow(RegistryKeys.TRIM_MATERIAL);
		RegistryWrapper.Impl<Enchantment> impl3 = this.registries.getOrThrow(RegistryKeys.ENCHANTMENT);
		ArmorTrim armorTrim = new ArmorTrim(impl2.getOrThrow(ArmorTrimMaterials.COPPER), impl.getOrThrow(ArmorTrimPatterns.FLOW));
		ArmorTrim armorTrim2 = new ArmorTrim(impl2.getOrThrow(ArmorTrimMaterials.COPPER), impl.getOrThrow(ArmorTrimPatterns.BOLT));
		lootTableBiConsumer.accept(
			LootTables.TRIAL_CHAMBER_EQUIPMENT,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(LootTableEntry.builder(createEquipmentTableBuilder(Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, armorTrim2, impl3).build()).weight(4))
						.with(LootTableEntry.builder(createEquipmentTableBuilder(Items.IRON_HELMET, Items.IRON_CHESTPLATE, armorTrim, impl3).build()).weight(2))
						.with(LootTableEntry.builder(createEquipmentTableBuilder(Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, armorTrim, impl3).build()).weight(1))
				)
		);
		lootTableBiConsumer.accept(
			LootTables.TRIAL_CHAMBER_MELEE_EQUIPMENT,
			LootTable.builder()
				.pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(LootTableEntry.builder(LootTables.TRIAL_CHAMBER_EQUIPMENT)))
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.IRON_SWORD).weight(4))
						.with(
							ItemEntry.builder(Items.IRON_SWORD)
								.apply(new SetEnchantmentsLootFunction.Builder().enchantment(impl3.getOrThrow(Enchantments.SHARPNESS), ConstantLootNumberProvider.create(1.0F)))
						)
						.with(
							ItemEntry.builder(Items.IRON_SWORD)
								.apply(new SetEnchantmentsLootFunction.Builder().enchantment(impl3.getOrThrow(Enchantments.KNOCKBACK), ConstantLootNumberProvider.create(1.0F)))
						)
						.with(ItemEntry.builder(Items.DIAMOND_SWORD))
				)
		);
		lootTableBiConsumer.accept(
			LootTables.TRIAL_CHAMBER_RANGED_EQUIPMENT,
			LootTable.builder()
				.pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(LootTableEntry.builder(LootTables.TRIAL_CHAMBER_EQUIPMENT)))
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.BOW).weight(2))
						.with(
							ItemEntry.builder(Items.BOW)
								.apply(new SetEnchantmentsLootFunction.Builder().enchantment(impl3.getOrThrow(Enchantments.POWER), ConstantLootNumberProvider.create(1.0F)))
						)
						.with(
							ItemEntry.builder(Items.BOW)
								.apply(new SetEnchantmentsLootFunction.Builder().enchantment(impl3.getOrThrow(Enchantments.PUNCH), ConstantLootNumberProvider.create(1.0F)))
						)
				)
		);
	}

	public static LootTable.Builder createEquipmentTableBuilder(
		Item helmet, Item chestplate, ArmorTrim trim, RegistryWrapper.Impl<Enchantment> enchantmentRegistryWrapper
	) {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.conditionally(RandomChanceLootCondition.builder(0.5F))
					.with(
						ItemEntry.builder(helmet)
							.apply(SetComponentsLootFunction.builder(DataComponentTypes.TRIM, trim))
							.apply(
								new SetEnchantmentsLootFunction.Builder()
									.enchantment(enchantmentRegistryWrapper.getOrThrow(Enchantments.PROTECTION), ConstantLootNumberProvider.create(4.0F))
									.enchantment(enchantmentRegistryWrapper.getOrThrow(Enchantments.PROJECTILE_PROTECTION), ConstantLootNumberProvider.create(4.0F))
									.enchantment(enchantmentRegistryWrapper.getOrThrow(Enchantments.FIRE_PROTECTION), ConstantLootNumberProvider.create(4.0F))
							)
					)
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.conditionally(RandomChanceLootCondition.builder(0.5F))
					.with(
						ItemEntry.builder(chestplate)
							.apply(SetComponentsLootFunction.builder(DataComponentTypes.TRIM, trim))
							.apply(
								new SetEnchantmentsLootFunction.Builder()
									.enchantment(enchantmentRegistryWrapper.getOrThrow(Enchantments.PROTECTION), ConstantLootNumberProvider.create(4.0F))
									.enchantment(enchantmentRegistryWrapper.getOrThrow(Enchantments.PROJECTILE_PROTECTION), ConstantLootNumberProvider.create(4.0F))
									.enchantment(enchantmentRegistryWrapper.getOrThrow(Enchantments.FIRE_PROTECTION), ConstantLootNumberProvider.create(4.0F))
							)
					)
			);
	}
}
