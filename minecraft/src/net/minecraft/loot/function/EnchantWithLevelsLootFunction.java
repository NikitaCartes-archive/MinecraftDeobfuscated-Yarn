package net.minecraft.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.util.math.random.Random;

public class EnchantWithLevelsLootFunction extends ConditionalLootFunction {
	public static final MapCodec<EnchantWithLevelsLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance)
				.<LootNumberProvider, Optional<RegistryEntryList<Enchantment>>>and(
					instance.group(
						LootNumberProviderTypes.CODEC.fieldOf("levels").forGetter(function -> function.levels),
						RegistryCodecs.entryList(RegistryKeys.ENCHANTMENT).optionalFieldOf("options").forGetter(function -> function.options)
					)
				)
				.apply(instance, EnchantWithLevelsLootFunction::new)
	);
	private final LootNumberProvider levels;
	private final Optional<RegistryEntryList<Enchantment>> options;

	EnchantWithLevelsLootFunction(List<LootCondition> conditions, LootNumberProvider levels, Optional<RegistryEntryList<Enchantment>> options) {
		super(conditions);
		this.levels = levels;
		this.options = options;
	}

	@Override
	public LootFunctionType<EnchantWithLevelsLootFunction> getType() {
		return LootFunctionTypes.ENCHANT_WITH_LEVELS;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return this.levels.getRequiredParameters();
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		Random random = context.getRandom();
		DynamicRegistryManager dynamicRegistryManager = context.getWorld().getRegistryManager();
		return EnchantmentHelper.enchant(random, stack, this.levels.nextInt(context), dynamicRegistryManager, this.options);
	}

	public static EnchantWithLevelsLootFunction.Builder builder(RegistryWrapper.WrapperLookup registryLookup, LootNumberProvider levels) {
		return new EnchantWithLevelsLootFunction.Builder(levels)
			.options(registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(EnchantmentTags.ON_RANDOM_LOOT));
	}

	public static class Builder extends ConditionalLootFunction.Builder<EnchantWithLevelsLootFunction.Builder> {
		private final LootNumberProvider levels;
		private Optional<RegistryEntryList<Enchantment>> options = Optional.empty();

		public Builder(LootNumberProvider levels) {
			this.levels = levels;
		}

		protected EnchantWithLevelsLootFunction.Builder getThisBuilder() {
			return this;
		}

		public EnchantWithLevelsLootFunction.Builder options(RegistryEntryList<Enchantment> options) {
			this.options = Optional.of(options);
			return this;
		}

		@Override
		public LootFunction build() {
			return new EnchantWithLevelsLootFunction(this.getConditions(), this.levels, this.options);
		}
	}
}
