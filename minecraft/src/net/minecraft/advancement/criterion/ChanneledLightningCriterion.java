package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ChanneledLightningCriterion extends AbstractCriterion<ChanneledLightningCriterion.Conditions> {
	private static final Identifier ID = new Identifier("channeled_lightning");

	@Override
	public Identifier getId() {
		return ID;
	}

	public ChanneledLightningCriterion.Conditions method_8801(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityPredicate[] entityPredicates = EntityPredicate.fromJsonArray(jsonObject.get("victims"));
		return new ChanneledLightningCriterion.Conditions(entityPredicates);
	}

	public void trigger(ServerPlayerEntity player, Collection<? extends Entity> victims) {
		this.test(player.getAdvancementManager(), conditions -> conditions.matches(player, victims));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate[] victims;

		public Conditions(EntityPredicate[] victims) {
			super(ChanneledLightningCriterion.ID);
			this.victims = victims;
		}

		public static ChanneledLightningCriterion.Conditions create(EntityPredicate... victims) {
			return new ChanneledLightningCriterion.Conditions(victims);
		}

		public boolean matches(ServerPlayerEntity player, Collection<? extends Entity> victims) {
			for (EntityPredicate entityPredicate : this.victims) {
				boolean bl = false;

				for (Entity entity : victims) {
					if (entityPredicate.test(player, entity)) {
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
