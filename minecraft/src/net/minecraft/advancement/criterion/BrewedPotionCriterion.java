package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.potion.Potion;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class BrewedPotionCriterion extends AbstractCriterion<BrewedPotionCriterion.Conditions> {
	public BrewedPotionCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Potion potion = null;
		if (jsonObject.has("potion")) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "potion"));
			potion = (Potion)Registries.POTION.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown potion '" + identifier + "'"));
		}

		return new BrewedPotionCriterion.Conditions(optional, potion);
	}

	public void trigger(ServerPlayerEntity player, Potion potion) {
		this.trigger(player, conditions -> conditions.matches(potion));
	}

	public static class Conditions extends AbstractCriterionConditions {
		@Nullable
		private final Potion potion;

		public Conditions(Optional<LootContextPredicate> playerPredicate, @Nullable Potion potion) {
			super(playerPredicate);
			this.potion = potion;
		}

		public static AdvancementCriterion<BrewedPotionCriterion.Conditions> any() {
			return Criteria.BREWED_POTION.create(new BrewedPotionCriterion.Conditions(Optional.empty(), null));
		}

		public boolean matches(Potion potion) {
			return this.potion == null || this.potion == potion;
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			if (this.potion != null) {
				jsonObject.addProperty("potion", Registries.POTION.getId(this.potion).toString());
			}

			return jsonObject;
		}
	}
}
