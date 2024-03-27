package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerInteractedWithEntityCriterion extends AbstractCriterion<PlayerInteractedWithEntityCriterion.Conditions> {
	@Override
	public Codec<PlayerInteractedWithEntityCriterion.Conditions> getConditionsCodec() {
		return PlayerInteractedWithEntityCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack, Entity entity) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, entity);
		this.trigger(player, conditions -> conditions.test(stack, lootContext));
	}

	public static record Conditions(Optional<LootContextPredicate> player, Optional<ItemPredicate> item, Optional<LootContextPredicate> entity)
		implements AbstractCriterion.Conditions {
		public static final Codec<PlayerInteractedWithEntityCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(PlayerInteractedWithEntityCriterion.Conditions::player),
						ItemPredicate.CODEC.optionalFieldOf("item").forGetter(PlayerInteractedWithEntityCriterion.Conditions::item),
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("entity").forGetter(PlayerInteractedWithEntityCriterion.Conditions::entity)
					)
					.apply(instance, PlayerInteractedWithEntityCriterion.Conditions::new)
		);

		public static AdvancementCriterion<PlayerInteractedWithEntityCriterion.Conditions> create(
			Optional<LootContextPredicate> playerPredicate, ItemPredicate.Builder item, Optional<LootContextPredicate> entity
		) {
			return Criteria.PLAYER_INTERACTED_WITH_ENTITY.create(new PlayerInteractedWithEntityCriterion.Conditions(playerPredicate, Optional.of(item.build()), entity));
		}

		public static AdvancementCriterion<PlayerInteractedWithEntityCriterion.Conditions> create(ItemPredicate.Builder item, Optional<LootContextPredicate> entity) {
			return create(Optional.empty(), item, entity);
		}

		public boolean test(ItemStack stack, LootContext entity) {
			return this.item.isPresent() && !((ItemPredicate)this.item.get()).test(stack)
				? false
				: this.entity.isEmpty() || ((LootContextPredicate)this.entity.get()).test(entity);
		}

		@Override
		public void validate(LootContextPredicateValidator validator) {
			AbstractCriterion.Conditions.super.validate(validator);
			validator.validateEntityPredicate(this.entity, ".entity");
		}
	}
}
