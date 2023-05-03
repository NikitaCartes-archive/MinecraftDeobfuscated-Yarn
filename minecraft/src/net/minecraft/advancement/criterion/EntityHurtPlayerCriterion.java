package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
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
		JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		DamagePredicate damagePredicate = DamagePredicate.fromJson(jsonObject.get("damage"));
		return new EntityHurtPlayerCriterion.Conditions(lootContextPredicate, damagePredicate);
	}

	public void trigger(ServerPlayerEntity player, DamageSource source, float dealt, float taken, boolean blocked) {
		this.trigger(player, conditions -> conditions.matches(player, source, dealt, taken, blocked));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final DamagePredicate damage;

		public Conditions(LootContextPredicate player, DamagePredicate damage) {
			super(EntityHurtPlayerCriterion.ID, player);
			this.damage = damage;
		}

		public static EntityHurtPlayerCriterion.Conditions create() {
			return new EntityHurtPlayerCriterion.Conditions(LootContextPredicate.EMPTY, DamagePredicate.ANY);
		}

		public static EntityHurtPlayerCriterion.Conditions create(DamagePredicate predicate) {
			return new EntityHurtPlayerCriterion.Conditions(LootContextPredicate.EMPTY, predicate);
		}

		public static EntityHurtPlayerCriterion.Conditions create(DamagePredicate.Builder damageBuilder) {
			return new EntityHurtPlayerCriterion.Conditions(LootContextPredicate.EMPTY, damageBuilder.build());
		}

		public boolean matches(ServerPlayerEntity player, DamageSource source, float dealt, float taken, boolean blocked) {
			return this.damage.test(player, source, dealt, taken, blocked);
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("damage", this.damage.toJson());
			return jsonObject;
		}
	}
}
