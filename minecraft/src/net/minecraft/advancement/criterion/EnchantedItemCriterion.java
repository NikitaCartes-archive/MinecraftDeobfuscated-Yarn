package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class EnchantedItemCriterion extends AbstractCriterion<EnchantedItemCriterion.Conditions> {
	@Override
	public Codec<EnchantedItemCriterion.Conditions> getConditionsCodec() {
		return EnchantedItemCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack, int levels) {
		this.trigger(player, conditions -> conditions.matches(stack, levels));
	}

	public static record Conditions(Optional<LootContextPredicate> player, Optional<ItemPredicate> item, NumberRange.IntRange levels)
		implements AbstractCriterion.Conditions {
		public static final Codec<EnchantedItemCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(EnchantedItemCriterion.Conditions::player),
						ItemPredicate.CODEC.optionalFieldOf("item").forGetter(EnchantedItemCriterion.Conditions::item),
						NumberRange.IntRange.CODEC.optionalFieldOf("levels", NumberRange.IntRange.ANY).forGetter(EnchantedItemCriterion.Conditions::levels)
					)
					.apply(instance, EnchantedItemCriterion.Conditions::new)
		);

		public static AdvancementCriterion<EnchantedItemCriterion.Conditions> any() {
			return Criteria.ENCHANTED_ITEM.create(new EnchantedItemCriterion.Conditions(Optional.empty(), Optional.empty(), NumberRange.IntRange.ANY));
		}

		public boolean matches(ItemStack stack, int levels) {
			return this.item.isPresent() && !((ItemPredicate)this.item.get()).test(stack) ? false : this.levels.test(levels);
		}
	}
}
