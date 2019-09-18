package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.potion.Potion;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class BrewedPotionCriterion extends AbstractCriterion<BrewedPotionCriterion.Conditions> {
	private static final Identifier ID = new Identifier("brewed_potion");

	@Override
	public Identifier getId() {
		return ID;
	}

	public BrewedPotionCriterion.Conditions method_8785(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		Potion potion = null;
		if (jsonObject.has("potion")) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "potion"));
			potion = (Potion)Registry.POTION.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown potion '" + identifier + "'"));
		}

		return new BrewedPotionCriterion.Conditions(potion);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity, Potion potion) {
		this.test(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(potion));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Potion potion;

		public Conditions(@Nullable Potion potion) {
			super(BrewedPotionCriterion.ID);
			this.potion = potion;
		}

		public static BrewedPotionCriterion.Conditions any() {
			return new BrewedPotionCriterion.Conditions(null);
		}

		public boolean matches(Potion potion) {
			return this.potion == null || this.potion == potion;
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			if (this.potion != null) {
				jsonObject.addProperty("potion", Registry.POTION.getId(this.potion).toString());
			}

			return jsonObject;
		}
	}
}
