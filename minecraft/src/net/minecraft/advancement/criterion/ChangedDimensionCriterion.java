package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
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

	public ChangedDimensionCriterion.Conditions method_8793(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		DimensionType dimensionType = jsonObject.has("from") ? DimensionType.byId(new Identifier(JsonHelper.getString(jsonObject, "from"))) : null;
		DimensionType dimensionType2 = jsonObject.has("to") ? DimensionType.byId(new Identifier(JsonHelper.getString(jsonObject, "to"))) : null;
		return new ChangedDimensionCriterion.Conditions(dimensionType, dimensionType2);
	}

	public void trigger(ServerPlayerEntity serverPlayerEntity, DimensionType dimensionType, DimensionType dimensionType2) {
		this.test(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(dimensionType, dimensionType2));
	}

	public static class Conditions extends AbstractCriterionConditions {
		@Nullable
		private final DimensionType from;
		@Nullable
		private final DimensionType to;

		public Conditions(@Nullable DimensionType dimensionType, @Nullable DimensionType dimensionType2) {
			super(ChangedDimensionCriterion.ID);
			this.from = dimensionType;
			this.to = dimensionType2;
		}

		public static ChangedDimensionCriterion.Conditions to(DimensionType dimensionType) {
			return new ChangedDimensionCriterion.Conditions(null, dimensionType);
		}

		public boolean matches(DimensionType dimensionType, DimensionType dimensionType2) {
			return this.from != null && this.from != dimensionType ? false : this.to == null || this.to == dimensionType2;
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

			return jsonObject;
		}
	}
}
