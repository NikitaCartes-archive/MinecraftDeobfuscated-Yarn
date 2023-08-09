package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class EntityHurtPlayerCriterion extends AbstractCriterion<EntityHurtPlayerCriterion.Conditions> {
	static final Identifier ID = new Identifier("entity_hurt_player");

	@Override
	public Identifier getId() {
		return ID;
	}

	public EntityHurtPlayerCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Optional<DamagePredicate> optional2 = DamagePredicate.fromJson(jsonObject.get("damage"));
		return new EntityHurtPlayerCriterion.Conditions(optional, optional2);
	}

	public void trigger(ServerPlayerEntity player, DamageSource source, float dealt, float taken, boolean blocked) {
		this.trigger(player, conditions -> conditions.matches(player, source, dealt, taken, blocked));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Optional<DamagePredicate> damage;

		public Conditions(Optional<LootContextPredicate> playerPredicate, Optional<DamagePredicate> damage) {
			super(EntityHurtPlayerCriterion.ID, playerPredicate);
			this.damage = damage;
		}

		public static EntityHurtPlayerCriterion.Conditions create() {
			return new EntityHurtPlayerCriterion.Conditions(Optional.empty(), Optional.empty());
		}

		public static EntityHurtPlayerCriterion.Conditions create(DamagePredicate predicate) {
			return new EntityHurtPlayerCriterion.Conditions(Optional.empty(), Optional.of(predicate));
		}

		public static EntityHurtPlayerCriterion.Conditions create(DamagePredicate.Builder damageBuilder) {
			return new EntityHurtPlayerCriterion.Conditions(Optional.empty(), damageBuilder.build());
		}

		public boolean matches(ServerPlayerEntity player, DamageSource damageSource, float dealt, float taken, boolean blocked) {
			return !this.damage.isPresent() || ((DamagePredicate)this.damage.get()).test(player, damageSource, dealt, taken, blocked);
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			this.damage.ifPresent(damagePredicate -> jsonObject.add("damage", damagePredicate.toJson()));
			return jsonObject;
		}
	}
}
