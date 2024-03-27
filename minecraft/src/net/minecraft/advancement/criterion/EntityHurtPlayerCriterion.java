package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class EntityHurtPlayerCriterion extends AbstractCriterion<EntityHurtPlayerCriterion.Conditions> {
	@Override
	public Codec<EntityHurtPlayerCriterion.Conditions> getConditionsCodec() {
		return EntityHurtPlayerCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, DamageSource source, float dealt, float taken, boolean blocked) {
		this.trigger(player, conditions -> conditions.matches(player, source, dealt, taken, blocked));
	}

	public static record Conditions(Optional<LootContextPredicate> player, Optional<DamagePredicate> damage) implements AbstractCriterion.Conditions {
		public static final Codec<EntityHurtPlayerCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(EntityHurtPlayerCriterion.Conditions::player),
						DamagePredicate.CODEC.optionalFieldOf("damage").forGetter(EntityHurtPlayerCriterion.Conditions::damage)
					)
					.apply(instance, EntityHurtPlayerCriterion.Conditions::new)
		);

		public static AdvancementCriterion<EntityHurtPlayerCriterion.Conditions> create() {
			return Criteria.ENTITY_HURT_PLAYER.create(new EntityHurtPlayerCriterion.Conditions(Optional.empty(), Optional.empty()));
		}

		public static AdvancementCriterion<EntityHurtPlayerCriterion.Conditions> create(DamagePredicate predicate) {
			return Criteria.ENTITY_HURT_PLAYER.create(new EntityHurtPlayerCriterion.Conditions(Optional.empty(), Optional.of(predicate)));
		}

		public static AdvancementCriterion<EntityHurtPlayerCriterion.Conditions> create(DamagePredicate.Builder damageBuilder) {
			return Criteria.ENTITY_HURT_PLAYER.create(new EntityHurtPlayerCriterion.Conditions(Optional.empty(), Optional.of(damageBuilder.build())));
		}

		public boolean matches(ServerPlayerEntity player, DamageSource damageSource, float dealt, float taken, boolean blocked) {
			return !this.damage.isPresent() || ((DamagePredicate)this.damage.get()).test(player, damageSource, dealt, taken, blocked);
		}
	}
}
