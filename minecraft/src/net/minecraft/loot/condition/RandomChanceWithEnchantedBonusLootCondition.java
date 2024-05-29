package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;

public record RandomChanceWithEnchantedBonusLootCondition(
	float unenchantedChance, EnchantmentLevelBasedValue enchantedChance, RegistryEntry<Enchantment> enchantment
) implements LootCondition {
	public static final MapCodec<RandomChanceWithEnchantedBonusLootCondition> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codec.floatRange(0.0F, 1.0F).fieldOf("unenchanted_chance").forGetter(RandomChanceWithEnchantedBonusLootCondition::unenchantedChance),
					EnchantmentLevelBasedValue.CODEC.fieldOf("enchanted_chance").forGetter(RandomChanceWithEnchantedBonusLootCondition::enchantedChance),
					Enchantment.ENTRY_CODEC.fieldOf("enchantment").forGetter(RandomChanceWithEnchantedBonusLootCondition::enchantment)
				)
				.apply(instance, RandomChanceWithEnchantedBonusLootCondition::new)
	);

	@Override
	public LootConditionType getType() {
		return LootConditionTypes.RANDOM_CHANCE_WITH_ENCHANTED_BONUS;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.ATTACKING_ENTITY);
	}

	public boolean test(LootContext lootContext) {
		Entity entity = lootContext.get(LootContextParameters.ATTACKING_ENTITY);
		int i = entity instanceof LivingEntity livingEntity ? EnchantmentHelper.getEquipmentLevel(this.enchantment, livingEntity) : 0;
		float f = i > 0 ? this.enchantedChance.getValue(i) : this.unenchantedChance;
		return lootContext.getRandom().nextFloat() < f;
	}

	public static LootCondition.Builder builder(RegistryWrapper.WrapperLookup registryLookup, float base, float perLevelAboveFirst) {
		RegistryWrapper.Impl<Enchantment> impl = registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
		return () -> new RandomChanceWithEnchantedBonusLootCondition(
				base, new EnchantmentLevelBasedValue.Linear(base + perLevelAboveFirst, perLevelAboveFirst), impl.getOrThrow(Enchantments.LOOTING)
			);
	}
}
