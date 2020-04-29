package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ConstructBeaconCriterion extends AbstractCriterion<ConstructBeaconCriterion.Conditions> {
	private static final Identifier ID = new Identifier("construct_beacon");

	@Override
	public Identifier getId() {
		return ID;
	}

	public ConstructBeaconCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("level"));
		return new ConstructBeaconCriterion.Conditions(extended, intRange);
	}

	public void trigger(ServerPlayerEntity player, BeaconBlockEntity beacon) {
		this.test(player, conditions -> conditions.matches(beacon));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final NumberRange.IntRange level;

		public Conditions(EntityPredicate.Extended player, NumberRange.IntRange level) {
			super(ConstructBeaconCriterion.ID, player);
			this.level = level;
		}

		public static ConstructBeaconCriterion.Conditions level(NumberRange.IntRange level) {
			return new ConstructBeaconCriterion.Conditions(EntityPredicate.Extended.EMPTY, level);
		}

		public boolean matches(BeaconBlockEntity beacon) {
			return this.level.test(beacon.getLevel());
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("level", this.level.toJson());
			return jsonObject;
		}
	}
}
