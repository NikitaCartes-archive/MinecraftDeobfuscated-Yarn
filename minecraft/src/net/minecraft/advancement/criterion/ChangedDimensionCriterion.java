package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

public class ChangedDimensionCriterion extends AbstractCriterion<ChangedDimensionCriterion.Conditions> {
	static final Identifier ID = new Identifier("changed_dimension");

	@Override
	public Identifier getId() {
		return ID;
	}

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
			super(ChangedDimensionCriterion.ID, playerPredicate);
			this.from = from;
			this.to = to;
		}

		public static ChangedDimensionCriterion.Conditions create() {
			return new ChangedDimensionCriterion.Conditions(Optional.empty(), null, null);
		}

		public static ChangedDimensionCriterion.Conditions create(RegistryKey<World> from, RegistryKey<World> to) {
			return new ChangedDimensionCriterion.Conditions(Optional.empty(), from, to);
		}

		public static ChangedDimensionCriterion.Conditions to(RegistryKey<World> to) {
			return new ChangedDimensionCriterion.Conditions(Optional.empty(), null, to);
		}

		public static ChangedDimensionCriterion.Conditions from(RegistryKey<World> from) {
			return new ChangedDimensionCriterion.Conditions(Optional.empty(), from, null);
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
