package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.util.dynamic.Codecs;

public class LootingEnchantLootFunction extends ConditionalLootFunction {
	public static final int DEFAULT_LIMIT = 0;
	public static final Codec<LootingEnchantLootFunction> CODEC = RecordCodecBuilder.create(
		instance -> addConditionsField(instance)
				.<LootNumberProvider, int>and(
					instance.group(
						LootNumberProviderTypes.CODEC.fieldOf("count").forGetter(function -> function.countRange),
						Codecs.createStrictOptionalFieldCodec(Codec.INT, "limit", 0).forGetter(function -> function.limit)
					)
				)
				.apply(instance, LootingEnchantLootFunction::new)
	);
	private final LootNumberProvider countRange;
	private final int limit;

	LootingEnchantLootFunction(List<LootCondition> conditions, LootNumberProvider countRange, int limit) {
		super(conditions);
		this.countRange = countRange;
		this.limit = limit;
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.LOOTING_ENCHANT;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return Sets.<LootContextParameter<?>>union(ImmutableSet.of(LootContextParameters.KILLER_ENTITY), this.countRange.getRequiredParameters());
	}

	private boolean hasLimit() {
		return this.limit > 0;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		Entity entity = context.get(LootContextParameters.KILLER_ENTITY);
		if (entity instanceof LivingEntity) {
			int i = EnchantmentHelper.getLooting((LivingEntity)entity);
			if (i == 0) {
				return stack;
			}

			float f = (float)i * this.countRange.nextFloat(context);
			stack.increment(Math.round(f));
			if (this.hasLimit() && stack.getCount() > this.limit) {
				stack.setCount(this.limit);
			}
		}

		return stack;
	}

	public static LootingEnchantLootFunction.Builder builder(LootNumberProvider countRange) {
		return new LootingEnchantLootFunction.Builder(countRange);
	}

	public static class Builder extends ConditionalLootFunction.Builder<LootingEnchantLootFunction.Builder> {
		private final LootNumberProvider countRange;
		private int limit = 0;

		public Builder(LootNumberProvider countRange) {
			this.countRange = countRange;
		}

		protected LootingEnchantLootFunction.Builder getThisBuilder() {
			return this;
		}

		public LootingEnchantLootFunction.Builder withLimit(int limit) {
			this.limit = limit;
			return this;
		}

		@Override
		public LootFunction build() {
			return new LootingEnchantLootFunction(this.getConditions(), this.countRange, this.limit);
		}
	}
}
