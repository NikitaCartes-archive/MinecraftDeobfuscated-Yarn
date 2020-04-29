package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
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

	public ChangedDimensionCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		DimensionType dimensionType = jsonObject.has("from") ? DimensionType.byId(new Identifier(JsonHelper.getString(jsonObject, "from"))) : null;
		DimensionType dimensionType2 = jsonObject.has("to") ? DimensionType.byId(new Identifier(JsonHelper.getString(jsonObject, "to"))) : null;
		return new ChangedDimensionCriterion.Conditions(extended, dimensionType, dimensionType2);
	}

	public void trigger(ServerPlayerEntity player, DimensionType from, DimensionType to) {
		this.test(player, conditions -> conditions.matches(from, to));
	}

	public static class Conditions extends AbstractCriterionConditions {
		@Nullable
		private final DimensionType from;
		@Nullable
		private final DimensionType to;

		public Conditions(EntityPredicate.Extended player, @Nullable DimensionType from, @Nullable DimensionType to) {
			super(ChangedDimensionCriterion.ID, player);
			this.from = from;
			this.to = to;
		}

		public static ChangedDimensionCriterion.Conditions to(DimensionType to) {
			return new ChangedDimensionCriterion.Conditions(EntityPredicate.Extended.EMPTY, null, to);
		}

		public boolean matches(DimensionType from, DimensionType to) {
			return this.from != null && this.from != from ? false : this.to == null || this.to == to;
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
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
