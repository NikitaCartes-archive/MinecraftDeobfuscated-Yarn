package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class OnKilledCriterion extends AbstractCriterion<OnKilledCriterion.Conditions> {
	private final Identifier id;

	public OnKilledCriterion(Identifier id) {
		this.id = id;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	public OnKilledCriterion.Conditions method_8989(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		return new OnKilledCriterion.Conditions(
			this.id, EntityPredicate.fromJson(jsonObject.get("entity")), DamageSourcePredicate.deserialize(jsonObject.get("killing_blow"))
		);
	}

	public void trigger(ServerPlayerEntity player, Entity entity, DamageSource source) {
		this.test(player.getAdvancementTracker(), conditions -> conditions.test(player, entity, source));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate entity;
		private final DamageSourcePredicate killingBlow;

		public Conditions(Identifier id, EntityPredicate entity, DamageSourcePredicate killingBlow) {
			super(id);
			this.entity = entity;
			this.killingBlow = killingBlow;
		}

		public static OnKilledCriterion.Conditions createPlayerKilledEntity(EntityPredicate.Builder builder) {
			return new OnKilledCriterion.Conditions(Criterions.PLAYER_KILLED_ENTITY.id, builder.build(), DamageSourcePredicate.EMPTY);
		}

		public static OnKilledCriterion.Conditions createPlayerKilledEntity() {
			return new OnKilledCriterion.Conditions(Criterions.PLAYER_KILLED_ENTITY.id, EntityPredicate.ANY, DamageSourcePredicate.EMPTY);
		}

		public static OnKilledCriterion.Conditions createPlayerKilledEntity(EntityPredicate.Builder builder, DamageSourcePredicate.Builder builder2) {
			return new OnKilledCriterion.Conditions(Criterions.PLAYER_KILLED_ENTITY.id, builder.build(), builder2.build());
		}

		public static OnKilledCriterion.Conditions createEntityKilledPlayer() {
			return new OnKilledCriterion.Conditions(Criterions.ENTITY_KILLED_PLAYER.id, EntityPredicate.ANY, DamageSourcePredicate.EMPTY);
		}

		public boolean test(ServerPlayerEntity player, Entity entity, DamageSource killingBlow) {
			return !this.killingBlow.test(player, killingBlow) ? false : this.entity.test(player, entity);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("entity", this.entity.serialize());
			jsonObject.add("killing_blow", this.killingBlow.serialize());
			return jsonObject;
		}
	}
}
