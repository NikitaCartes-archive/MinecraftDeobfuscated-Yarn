package net.minecraft.loot.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.loot.context.LootContext;

public record RandomChanceLootCondition(float chance) implements LootCondition {
	public static final Codec<RandomChanceLootCondition> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(Codec.FLOAT.fieldOf("chance").forGetter(RandomChanceLootCondition::chance)).apply(instance, RandomChanceLootCondition::new)
	);

	@Override
	public LootConditionType getType() {
		return LootConditionTypes.RANDOM_CHANCE;
	}

	public boolean test(LootContext lootContext) {
		return lootContext.getRandom().nextFloat() < this.chance;
	}

	public static LootCondition.Builder builder(float chance) {
		return () -> new RandomChanceLootCondition(chance);
	}
}
