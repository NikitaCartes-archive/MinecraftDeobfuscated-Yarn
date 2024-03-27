package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.item.ItemPredicate;

public record MatchToolLootCondition(Optional<ItemPredicate> predicate) implements LootCondition {
	public static final MapCodec<MatchToolLootCondition> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(ItemPredicate.CODEC.optionalFieldOf("predicate").forGetter(MatchToolLootCondition::predicate))
				.apply(instance, MatchToolLootCondition::new)
	);

	@Override
	public LootConditionType getType() {
		return LootConditionTypes.MATCH_TOOL;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.TOOL);
	}

	public boolean test(LootContext lootContext) {
		ItemStack itemStack = lootContext.get(LootContextParameters.TOOL);
		return itemStack != null && (this.predicate.isEmpty() || ((ItemPredicate)this.predicate.get()).test(itemStack));
	}

	public static LootCondition.Builder builder(ItemPredicate.Builder predicate) {
		return () -> new MatchToolLootCondition(Optional.of(predicate.build()));
	}
}
