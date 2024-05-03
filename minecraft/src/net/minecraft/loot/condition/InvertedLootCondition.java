package net.minecraft.loot.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;

public record InvertedLootCondition(LootCondition term) implements LootCondition {
	public static final MapCodec<InvertedLootCondition> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(LootCondition.CODEC.fieldOf("term").forGetter(InvertedLootCondition::term)).apply(instance, InvertedLootCondition::new)
	);

	@Override
	public LootConditionType getType() {
		return LootConditionTypes.INVERTED;
	}

	public boolean test(LootContext lootContext) {
		return !this.term.test(lootContext);
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return this.term.getRequiredParameters();
	}

	@Override
	public void validate(LootTableReporter reporter) {
		LootCondition.super.validate(reporter);
		this.term.validate(reporter);
	}

	public static LootCondition.Builder builder(LootCondition.Builder term) {
		InvertedLootCondition invertedLootCondition = new InvertedLootCondition(term.build());
		return () -> invertedLootCondition;
	}
}
