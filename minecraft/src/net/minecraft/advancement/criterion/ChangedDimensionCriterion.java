package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

public class ChangedDimensionCriterion extends AbstractCriterion<ChangedDimensionCriterion.Conditions> {
	public ChangedDimensionCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		RegistryKey<World> registryKey = jsonObject.has("from") ? RegistryKey.of(RegistryKeys.WORLD, new Identifier(JsonHelper.getString(jsonObject, "from"))) : null;
		RegistryKey<World> registryKey2 = jsonObject.has("to") ? RegistryKey.of(RegistryKeys.WORLD, new Identifier(JsonHelper.getString(jsonObject, "to"))) : null;
		return new ChangedDimensionCriterion.Conditions(optional, registryKey, registryKey2);
	}

	public void trigger(ServerPlayerEntity player, RegistryKey<World> from, RegistryKey<World> to) {
		this.trigger(player, conditions -> conditions.matches(from, to));
	}

	public static class Conditions extends AbstractCriterionConditions {
		@Nullable
		private final RegistryKey<World> from;
		@Nullable
		private final RegistryKey<World> to;

		public Conditions(Optional<LootContextPredicate> playerPredicate, @Nullable RegistryKey<World> from, @Nullable RegistryKey<World> to) {
			super(playerPredicate);
			this.from = from;
			this.to = to;
		}

		public static AdvancementCriterion<ChangedDimensionCriterion.Conditions> create() {
			return Criteria.CHANGED_DIMENSION.create(new ChangedDimensionCriterion.Conditions(Optional.empty(), null, null));
		}

		public static AdvancementCriterion<ChangedDimensionCriterion.Conditions> create(RegistryKey<World> from, RegistryKey<World> to) {
			return Criteria.CHANGED_DIMENSION.create(new ChangedDimensionCriterion.Conditions(Optional.empty(), from, to));
		}

		public static AdvancementCriterion<ChangedDimensionCriterion.Conditions> to(RegistryKey<World> to) {
			return Criteria.CHANGED_DIMENSION.create(new ChangedDimensionCriterion.Conditions(Optional.empty(), null, to));
		}

		public static AdvancementCriterion<ChangedDimensionCriterion.Conditions> from(RegistryKey<World> from) {
			return Criteria.CHANGED_DIMENSION.create(new ChangedDimensionCriterion.Conditions(Optional.empty(), from, null));
		}

		public boolean matches(RegistryKey<World> from, RegistryKey<World> to) {
			return this.from != null && this.from != from ? false : this.to == null || this.to == to;
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
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
