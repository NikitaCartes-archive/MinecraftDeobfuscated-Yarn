package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.potion.Potion;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class BrewedPotionCriterion implements Criterion<BrewedPotionCriterion.Conditions> {
	private static final Identifier field_9488 = new Identifier("brewed_potion");
	private final Map<PlayerAdvancementTracker, BrewedPotionCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, BrewedPotionCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return field_9488;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<BrewedPotionCriterion.Conditions> conditionsContainer
	) {
		BrewedPotionCriterion.Handler handler = (BrewedPotionCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler == null) {
			handler = new BrewedPotionCriterion.Handler(playerAdvancementTracker);
			this.handlers.put(playerAdvancementTracker, handler);
		}

		handler.method_8786(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<BrewedPotionCriterion.Conditions> conditionsContainer
	) {
		BrewedPotionCriterion.Handler handler = (BrewedPotionCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler != null) {
			handler.method_8788(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(playerAdvancementTracker);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
		this.handlers.remove(playerAdvancementTracker);
	}

	public BrewedPotionCriterion.Conditions method_8785(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		Potion potion = null;
		if (jsonObject.has("potion")) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "potion"));
			potion = (Potion)Registry.POTION.method_17966(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown potion '" + identifier + "'"));
		}

		return new BrewedPotionCriterion.Conditions(potion);
	}

	public void method_8784(ServerPlayerEntity serverPlayerEntity, Potion potion) {
		BrewedPotionCriterion.Handler handler = (BrewedPotionCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.method_8789(potion);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Potion field_9492;

		public Conditions(@Nullable Potion potion) {
			super(BrewedPotionCriterion.field_9488);
			this.field_9492 = potion;
		}

		public static BrewedPotionCriterion.Conditions any() {
			return new BrewedPotionCriterion.Conditions(null);
		}

		public boolean method_8790(Potion potion) {
			return this.field_9492 == null || this.field_9492 == potion;
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			if (this.field_9492 != null) {
				jsonObject.addProperty("potion", Registry.POTION.method_10221(this.field_9492).toString());
			}

			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker field_9491;
		private final Set<Criterion.ConditionsContainer<BrewedPotionCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<BrewedPotionCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.field_9491 = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void method_8786(Criterion.ConditionsContainer<BrewedPotionCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void method_8788(Criterion.ConditionsContainer<BrewedPotionCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void method_8789(Potion potion) {
			List<Criterion.ConditionsContainer<BrewedPotionCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<BrewedPotionCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.method_797().method_8790(potion)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<BrewedPotionCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<BrewedPotionCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.field_9491);
				}
			}
		}
	}
}
