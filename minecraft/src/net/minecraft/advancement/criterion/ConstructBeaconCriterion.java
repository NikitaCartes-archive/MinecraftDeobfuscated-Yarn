package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ConstructBeaconCriterion extends AbstractCriterion<ConstructBeaconCriterion.Conditions> {
	static final Identifier ID = new Identifier("construct_beacon");

	@Override
	public Identifier getId() {
		return ID;
	}

	public ConstructBeaconCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("level"));
		return new ConstructBeaconCriterion.Conditions(lootContextPredicate, intRange);
	}

	public void trigger(ServerPlayerEntity player, int level) {
		this.trigger(player, conditions -> conditions.matches(level));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final NumberRange.IntRange level;

		public Conditions(LootContextPredicate player, NumberRange.IntRange level) {
			super(ConstructBeaconCriterion.ID, player);
			this.level = level;
		}

		public static ConstructBeaconCriterion.Conditions create() {
			return new ConstructBeaconCriterion.Conditions(LootContextPredicate.EMPTY, NumberRange.IntRange.ANY);
		}

		public static ConstructBeaconCriterion.Conditions level(NumberRange.IntRange level) {
			return new ConstructBeaconCriterion.Conditions(LootContextPredicate.EMPTY, level);
		}

		public boolean matches(int level) {
			return this.level.test(level);
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("level", this.level.toJson());
			return jsonObject;
		}
	}
}
