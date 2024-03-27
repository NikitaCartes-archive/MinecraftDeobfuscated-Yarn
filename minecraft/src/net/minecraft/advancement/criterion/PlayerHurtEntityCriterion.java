package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerHurtEntityCriterion extends AbstractCriterion<PlayerHurtEntityCriterion.Conditions> {
	@Override
	public Codec<PlayerHurtEntityCriterion.Conditions> getConditionsCodec() {
		return PlayerHurtEntityCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, Entity entity, DamageSource damage, float dealt, float taken, boolean blocked) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, entity);
		this.trigger(player, conditions -> conditions.matches(player, lootContext, damage, dealt, taken, blocked));
	}

	public static record Conditions(Optional<LootContextPredicate> player, Optional<DamagePredicate> damage, Optional<LootContextPredicate> entity)
		implements AbstractCriterion.Conditions {
		public static final Codec<PlayerHurtEntityCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(PlayerHurtEntityCriterion.Conditions::player),
						DamagePredicate.CODEC.optionalFieldOf("damage").forGetter(PlayerHurtEntityCriterion.Conditions::damage),
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("entity").forGetter(PlayerHurtEntityCriterion.Conditions::entity)
					)
					.apply(instance, PlayerHurtEntityCriterion.Conditions::new)
		);

		public static AdvancementCriterion<PlayerHurtEntityCriterion.Conditions> create() {
			return Criteria.PLAYER_HURT_ENTITY.create(new PlayerHurtEntityCriterion.Conditions(Optional.empty(), Optional.empty(), Optional.empty()));
		}

		public static AdvancementCriterion<PlayerHurtEntityCriterion.Conditions> createDamage(Optional<DamagePredicate> damage) {
			return Criteria.PLAYER_HURT_ENTITY.create(new PlayerHurtEntityCriterion.Conditions(Optional.empty(), damage, Optional.empty()));
		}

		public static AdvancementCriterion<PlayerHurtEntityCriterion.Conditions> create(DamagePredicate.Builder damage) {
			return Criteria.PLAYER_HURT_ENTITY.create(new PlayerHurtEntityCriterion.Conditions(Optional.empty(), Optional.of(damage.build()), Optional.empty()));
		}

		public static AdvancementCriterion<PlayerHurtEntityCriterion.Conditions> createEntity(Optional<EntityPredicate> entity) {
			return Criteria.PLAYER_HURT_ENTITY
				.create(new PlayerHurtEntityCriterion.Conditions(Optional.empty(), Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(entity)));
		}

		public static AdvancementCriterion<PlayerHurtEntityCriterion.Conditions> create(Optional<DamagePredicate> damage, Optional<EntityPredicate> entity) {
			return Criteria.PLAYER_HURT_ENTITY
				.create(new PlayerHurtEntityCriterion.Conditions(Optional.empty(), damage, EntityPredicate.contextPredicateFromEntityPredicate(entity)));
		}

		public static AdvancementCriterion<PlayerHurtEntityCriterion.Conditions> create(DamagePredicate.Builder damage, Optional<EntityPredicate> entity) {
			return Criteria.PLAYER_HURT_ENTITY
				.create(
					new PlayerHurtEntityCriterion.Conditions(Optional.empty(), Optional.of(damage.build()), EntityPredicate.contextPredicateFromEntityPredicate(entity))
				);
		}

		public boolean matches(ServerPlayerEntity player, LootContext entity, DamageSource damageSource, float dealt, float taken, boolean blocked) {
			return this.damage.isPresent() && !((DamagePredicate)this.damage.get()).test(player, damageSource, dealt, taken, blocked)
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
