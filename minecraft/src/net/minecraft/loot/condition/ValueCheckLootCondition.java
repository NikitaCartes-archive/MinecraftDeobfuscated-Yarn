package net.minecraft.loot.condition;

import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;

public record ValueCheckLootCondition(LootNumberProvider value, BoundedIntUnaryOperator range) implements LootCondition {
	public static final MapCodec<ValueCheckLootCondition> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					LootNumberProviderTypes.CODEC.fieldOf("value").forGetter(ValueCheckLootCondition::value),
					BoundedIntUnaryOperator.CODEC.fieldOf("range").forGetter(ValueCheckLootCondition::range)
				)
				.apply(instance, ValueCheckLootCondition::new)
	);

	@Override
	public LootConditionType getType() {
		return LootConditionTypes.VALUE_CHECK;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return Sets.<LootContextParameter<?>>union(this.value.getRequiredParameters(), this.range.getRequiredParameters());
	}

	public boolean test(LootContext lootContext) {
		return this.range.test(lootContext, this.value.nextInt(lootContext));
	}

	public static LootCondition.Builder builder(LootNumberProvider value, BoundedIntUnaryOperator range) {
		return () -> new ValueCheckLootCondition(value, range);
	}
}
