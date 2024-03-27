package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class ThrownItemPickedUpByEntityCriterion extends AbstractCriterion<ThrownItemPickedUpByEntityCriterion.Conditions> {
	@Override
	public Codec<ThrownItemPickedUpByEntityCriterion.Conditions> getConditionsCodec() {
		return ThrownItemPickedUpByEntityCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack, @Nullable Entity entity) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, entity);
		this.trigger(player, conditions -> conditions.test(player, stack, lootContext));
	}

	public static record Conditions(Optional<LootContextPredicate> player, Optional<ItemPredicate> item, Optional<LootContextPredicate> entity)
		implements AbstractCriterion.Conditions {
		public static final Codec<ThrownItemPickedUpByEntityCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(ThrownItemPickedUpByEntityCriterion.Conditions::player),
						ItemPredicate.CODEC.optionalFieldOf("item").forGetter(ThrownItemPickedUpByEntityCriterion.Conditions::item),
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("entity").forGetter(ThrownItemPickedUpByEntityCriterion.Conditions::entity)
					)
					.apply(instance, ThrownItemPickedUpByEntityCriterion.Conditions::new)
		);

		public static AdvancementCriterion<ThrownItemPickedUpByEntityCriterion.Conditions> createThrownItemPickedUpByEntity(
			LootContextPredicate player, Optional<ItemPredicate> item, Optional<LootContextPredicate> entity
		) {
			return Criteria.THROWN_ITEM_PICKED_UP_BY_ENTITY.create(new ThrownItemPickedUpByEntityCriterion.Conditions(Optional.of(player), item, entity));
		}

		public static AdvancementCriterion<ThrownItemPickedUpByEntityCriterion.Conditions> createThrownItemPickedUpByPlayer(
			Optional<LootContextPredicate> playerPredicate, Optional<ItemPredicate> item, Optional<LootContextPredicate> entity
		) {
			return Criteria.THROWN_ITEM_PICKED_UP_BY_PLAYER.create(new ThrownItemPickedUpByEntityCriterion.Conditions(playerPredicate, item, entity));
		}

		public boolean test(ServerPlayerEntity player, ItemStack stack, LootContext entity) {
			return this.item.isPresent() && !((ItemPredicate)this.item.get()).test(stack)
				? false
				: !this.entity.isPresent() || ((LootContextPredicate)this.entity.get()).test(entity);
		}

		@Override
		public void validate(LootContextPredicateValidator validator) {
			AbstractCriterion.Conditions.super.validate(validator);
			validator.validateEntityPredicate(this.entity, ".entity");
		}
	}
}
