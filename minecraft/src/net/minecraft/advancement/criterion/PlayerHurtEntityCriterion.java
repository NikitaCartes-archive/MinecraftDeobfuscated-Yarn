package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class PlayerHurtEntityCriterion extends AbstractCriterion<PlayerHurtEntityCriterion.Conditions> {
	private static final Identifier ID = new Identifier("player_hurt_entity");

	@Override
	public Identifier getId() {
		return ID;
	}

	public PlayerHurtEntityCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		DamagePredicate damagePredicate = DamagePredicate.deserialize(jsonObject.get("damage"));
		EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("entity"));
		return new PlayerHurtEntityCriterion.Conditions(damagePredicate, entityPredicate);
	}

	public void trigger(ServerPlayerEntity player, Entity entity, DamageSource source, float dealt, float taken, boolean blocked) {
		this.test(player.getAdvancementTracker(), conditions -> conditions.matches(player, entity, source, dealt, taken, blocked));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final DamagePredicate damage;
		private final EntityPredicate entity;

		public Conditions(DamagePredicate damage, EntityPredicate entity) {
			super(PlayerHurtEntityCriterion.ID);
			this.damage = damage;
			this.entity = entity;
		}

		public static PlayerHurtEntityCriterion.Conditions create(DamagePredicate.Builder builder) {
			return new PlayerHurtEntityCriterion.Conditions(builder.build(), EntityPredicate.ANY);
		}

		public boolean matches(ServerPlayerEntity player, Entity entity, DamageSource source, float dealt, float taken, boolean blocked) {
			return !this.damage.test(player, source, dealt, taken, blocked) ? false : this.entity.test(player, entity);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("damage", this.damage.serialize());
			jsonObject.add("entity", this.entity.serialize());
			return jsonObject;
		}
	}
}
