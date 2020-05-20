package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
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
		RegistryKey<DimensionType> registryKey = jsonObject.has("from")
			? RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier(JsonHelper.getString(jsonObject, "from")))
			: null;
		RegistryKey<DimensionType> registryKey2 = jsonObject.has("to")
			? RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier(JsonHelper.getString(jsonObject, "to")))
			: null;
		return new ChangedDimensionCriterion.Conditions(extended, registryKey, registryKey2);
	}

	public void trigger(ServerPlayerEntity player, RegistryKey<DimensionType> from, RegistryKey<DimensionType> to) {
		this.test(player, conditions -> conditions.matches(from, to));
	}

	public static class Conditions extends AbstractCriterionConditions {
		@Nullable
		private final RegistryKey<DimensionType> from;
		@Nullable
		private final RegistryKey<DimensionType> to;

		public Conditions(EntityPredicate.Extended player, @Nullable RegistryKey<DimensionType> from, @Nullable RegistryKey<DimensionType> to) {
			super(ChangedDimensionCriterion.ID, player);
			this.from = from;
			this.to = to;
		}

		public static ChangedDimensionCriterion.Conditions to(RegistryKey<DimensionType> to) {
			return new ChangedDimensionCriterion.Conditions(EntityPredicate.Extended.EMPTY, null, to);
		}

		public boolean matches(RegistryKey<DimensionType> from, RegistryKey<DimensionType> to) {
			return this.from != null && this.from != from ? false : this.to == null || this.to == to;
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			if (this.from != null) {
				jsonObject.addProperty("from", this.from.getValue().toString());
			}

			if (this.to != null) {
				jsonObject.addProperty("to", this.to.getValue().toString());
			}

			return jsonObject;
		}
	}
}
