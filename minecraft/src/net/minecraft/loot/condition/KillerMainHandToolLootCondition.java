package net.minecraft.loot.condition;

import com.mojang.serialization.Codec;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemConvertible;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.item.ItemPredicate;

public record KillerMainHandToolLootCondition(ItemPredicate predicate) implements LootCondition {
	public static final Codec<KillerMainHandToolLootCondition> CODEC = ItemPredicate.CODEC
		.xmap(KillerMainHandToolLootCondition::new, KillerMainHandToolLootCondition::predicate);

	@Override
	public LootConditionType getType() {
		return LootConditionTypes.KILLER_MAIN_HAND_TOOL;
	}

	public boolean test(LootContext lootContext) {
		DamageSource damageSource = lootContext.get(LootContextParameters.DAMAGE_SOURCE);
		if (damageSource != null) {
			Entity entity = damageSource.getAttacker();
			return entity != null && entity instanceof LivingEntity livingEntity ? this.predicate.test(livingEntity.getMainHandStack()) : false;
		} else {
			return false;
		}
	}

	public static LootCondition.Builder builder(ItemConvertible item) {
		return () -> new KillerMainHandToolLootCondition(ItemPredicate.Builder.create().items(item).build());
	}
}
