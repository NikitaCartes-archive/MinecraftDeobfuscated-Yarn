package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;

public class EnchantedCountIncreaseLootFunction extends ConditionalLootFunction {
	public static final int DEFAULT_LIMIT = 0;
	public static final MapCodec<EnchantedCountIncreaseLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance)
				.<RegistryEntry<Enchantment>, LootNumberProvider, int>and(
					instance.group(
						Enchantment.ENTRY_CODEC.fieldOf("enchantment").forGetter(function -> function.enchantment),
						LootNumberProviderTypes.CODEC.fieldOf("count").forGetter(function -> function.count),
						Codec.INT.optionalFieldOf("limit", Integer.valueOf(0)).forGetter(function -> function.limit)
					)
				)
				.apply(instance, EnchantedCountIncreaseLootFunction::new)
	);
	private final RegistryEntry<Enchantment> enchantment;
	private final LootNumberProvider count;
	private final int limit;

	EnchantedCountIncreaseLootFunction(List<LootCondition> conditions, RegistryEntry<Enchantment> enchantment, LootNumberProvider count, int limit) {
		super(conditions);
		this.enchantment = enchantment;
		this.count = count;
		this.limit = limit;
	}

	@Override
	public LootFunctionType<EnchantedCountIncreaseLootFunction> getType() {
		return LootFunctionTypes.ENCHANTED_COUNT_INCREASE;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return Sets.<LootContextParameter<?>>union(ImmutableSet.of(LootContextParameters.ATTACKING_ENTITY), this.count.getRequiredParameters());
	}

	private boolean hasLimit() {
		return this.limit > 0;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		Entity entity = context.get(LootContextParameters.ATTACKING_ENTITY);
		if (entity instanceof LivingEntity livingEntity) {
			int i = EnchantmentHelper.getEquipmentLevel(this.enchantment, livingEntity);
			if (i == 0) {
				return stack;
			}

			float f = (float)i * this.count.nextFloat(context);
			stack.increment(Math.round(f));
			if (this.hasLimit()) {
				stack.capCount(this.limit);
			}
		}

		return stack;
	}

	public static EnchantedCountIncreaseLootFunction.Builder builder(RegistryWrapper.WrapperLookup registryLookup, LootNumberProvider count) {
		RegistryWrapper.Impl<Enchantment> impl = registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
		return new EnchantedCountIncreaseLootFunction.Builder(impl.getOrThrow(Enchantments.LOOTING), count);
	}

	public static class Builder extends ConditionalLootFunction.Builder<EnchantedCountIncreaseLootFunction.Builder> {
		private final RegistryEntry<Enchantment> enchantment;
		private final LootNumberProvider count;
		private int limit = 0;

		public Builder(RegistryEntry<Enchantment> enchantment, LootNumberProvider count) {
			this.enchantment = enchantment;
			this.count = count;
		}

		protected EnchantedCountIncreaseLootFunction.Builder getThisBuilder() {
			return this;
		}

		public EnchantedCountIncreaseLootFunction.Builder withLimit(int limit) {
			this.limit = limit;
			return this;
		}

		@Override
		public LootFunction build() {
			return new EnchantedCountIncreaseLootFunction(this.getConditions(), this.enchantment, this.count, this.limit);
		}
	}
}
