package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class UsingItemCriterion extends AbstractCriterion<UsingItemCriterion.Conditions> {
	@Override
	public Codec<UsingItemCriterion.Conditions> getConditionsCodec() {
		return UsingItemCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack) {
		this.trigger(player, conditions -> conditions.test(stack));
	}

	public static record Conditions(Optional<LootContextPredicate> player, Optional<ItemPredicate> item) implements AbstractCriterion.Conditions {
		public static final Codec<UsingItemCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(UsingItemCriterion.Conditions::player),
						ItemPredicate.CODEC.optionalFieldOf("item").forGetter(UsingItemCriterion.Conditions::item)
					)
					.apply(instance, UsingItemCriterion.Conditions::new)
		);

		public static AdvancementCriterion<UsingItemCriterion.Conditions> create(EntityPredicate.Builder player, ItemPredicate.Builder item) {
			return Criteria.USING_ITEM
				.create(new UsingItemCriterion.Conditions(Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(player)), Optional.of(item.build())));
		}

		public boolean test(ItemStack stack) {
			return !this.item.isPresent() || ((ItemPredicate)this.item.get()).test(stack);
		}
	}
}
