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

	public void trigger(ServerPlayerEntity player, DimensionType from, DimensionType to) {
		this.test(player.getAdvancementManager(), conditions -> conditions.matches(from, to));
	}

	public static class Conditions extends AbstractCriterionConditions {
		@Nullable
		private final DimensionType from;
		@Nullable
		private final DimensionType to;

		public Conditions(@Nullable DimensionType from, @Nullable DimensionType to) {
			super(ChangedDimensionCriterion.ID);
			this.from = from;
			this.to = to;
		}

		public static ChangedDimensionCriterion.Conditions to(DimensionType to) {
			return new ChangedDimensionCriterion.Conditions(null, to);
		}

		public boolean matches(DimensionType from, DimensionType to) {
			return this.from != null && this.from != from ? false : this.to == null || this.to == to;
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
