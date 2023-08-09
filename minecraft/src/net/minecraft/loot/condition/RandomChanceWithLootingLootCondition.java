package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;

public record RandomChanceWithLootingLootCondition(float chance, float lootingMultiplier) implements LootCondition {
	public static final Codec<RandomChanceWithLootingLootCondition> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.FLOAT.fieldOf("chance").forGetter(RandomChanceWithLootingLootCondition::chance),
					Codec.FLOAT.fieldOf("looting_multiplier").forGetter(RandomChanceWithLootingLootCondition::lootingMultiplier)
				)
				.apply(instance, RandomChanceWithLootingLootCondition::new)
	);

	@Override
	public LootConditionType getType() {
		return LootConditionTypes.RANDOM_CHANCE_WITH_LOOTING;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.KILLER_ENTITY);
	}

	public boolean test(LootContext lootContext) {
		Entity entity = lootContext.get(LootContextParameters.KILLER_ENTITY);
		int i = 0;
		if (entity instanceof LivingEntity) {
			i = EnchantmentHelper.getLooting((LivingEntity)entity);
		}

		return lootContext.getRandom().nextFloat() < this.chance + (float)i * this.lootingMultiplier;
	}

	public static LootCondition.Builder builder(float chance, float lootingMultiplier) {
		return () -> new RandomChanceWithLootingLootCondition(chance, lootingMultiplier);
	}
}
