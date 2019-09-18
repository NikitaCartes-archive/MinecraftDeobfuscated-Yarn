package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import net.minecraft.class_4558;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ChanneledLightningCriterion extends class_4558<ChanneledLightningCriterion.Conditions> {
	private static final Identifier ID = new Identifier("channeled_lightning");

	@Override
	public Identifier getId() {
		return ID;
	}

	public ChanneledLightningCriterion.Conditions method_8801(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityPredicate[] entityPredicates = EntityPredicate.deserializeAll(jsonObject.get("victims"));
		return new ChanneledLightningCriterion.Conditions(entityPredicates);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity, Collection<? extends Entity> collection) {
		this.method_22510(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(serverPlayerEntity, collection));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate[] victims;

		public Conditions(EntityPredicate[] entityPredicates) {
			super(ChanneledLightningCriterion.ID);
			this.victims = entityPredicates;
		}

		public static ChanneledLightningCriterion.Conditions create(EntityPredicate... entityPredicates) {
			return new ChanneledLightningCriterion.Conditions(entityPredicates);
		}

		public boolean matches(ServerPlayerEntity serverPlayerEntity, Collection<? extends Entity> collection) {
			for (EntityPredicate entityPredicate : this.victims) {
				boolean bl = false;

				for (Entity entity : collection) {
					if (entityPredicate.test(serverPlayerEntity, entity)) {
						bl = true;
						break;
					}
				}

				if (!bl) {
					return false;
				}
			}

			return true;
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("victims", EntityPredicate.serializeAll(this.victims));
			return jsonObject;
		}
	}
}
