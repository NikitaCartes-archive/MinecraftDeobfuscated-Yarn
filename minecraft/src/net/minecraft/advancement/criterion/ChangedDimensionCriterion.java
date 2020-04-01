package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.dimension.DimensionType;

public class ChangedDimensionCriterion extends AbstractCriterion<ChangedDimensionCriterion.Conditions> {
	private static final Identifier ID = new Identifier("changed_dimension");

	@Override
	public Identifier getId() {
		return ID;
	}

	public ChangedDimensionCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("player"));
		DimensionType dimensionType = jsonObject.has("from") ? DimensionType.byId(new Identifier(JsonHelper.getString(jsonObject, "from"))) : null;
		DimensionType dimensionType2 = jsonObject.has("to") ? DimensionType.byId(new Identifier(JsonHelper.getString(jsonObject, "to"))) : null;
		return new ChangedDimensionCriterion.Conditions(dimensionType, dimensionType2, entityPredicate);
	}

	public void trigger(ServerPlayerEntity player, DimensionType from, DimensionType to) {
		this.test(player.getAdvancementTracker(), conditions -> conditions.matches(player, from, to));
	}

	public static class Conditions extends AbstractCriterionConditions {
		@Nullable
		private final DimensionType from;
		@Nullable
		private final DimensionType to;
		private final EntityPredicate field_23407;

		public Conditions(@Nullable DimensionType from, @Nullable DimensionType to, EntityPredicate entityPredicate) {
			super(ChangedDimensionCriterion.ID);
			this.from = from;
			this.to = to;
			this.field_23407 = entityPredicate;
		}

		public static ChangedDimensionCriterion.Conditions method_26440(EntityPredicate entityPredicate) {
			return new ChangedDimensionCriterion.Conditions(null, null, entityPredicate);
		}

		public static ChangedDimensionCriterion.Conditions to(DimensionType to) {
			return new ChangedDimensionCriterion.Conditions(null, to, EntityPredicate.ANY);
		}

		public boolean matches(ServerPlayerEntity serverPlayerEntity, DimensionType dimensionType, DimensionType dimensionType2) {
			if (!this.field_23407.test(serverPlayerEntity, serverPlayerEntity)) {
				return false;
			} else {
				return this.from != null && this.from != dimensionType ? false : this.to == null || this.to == dimensionType2;
			}
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			if (this.from != null) {
				jsonObject.addProperty("from", DimensionType.getId(this.from).toString());
			}

			if (this.to != null) {
				jsonObject.addProperty("to", DimensionType.getId(this.to).toString());
			}

			jsonObject.add("player", this.field_23407.toJson());
			return jsonObject;
		}
	}
}
