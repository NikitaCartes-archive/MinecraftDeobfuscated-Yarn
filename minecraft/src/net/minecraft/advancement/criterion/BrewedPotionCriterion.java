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
import net.minecraft.advancement.ServerAdvancementManager;
import net.minecraft.potion.Potion;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class BrewedPotionCriterion implements Criterion<BrewedPotionCriterion.Conditions> {
	private static final Identifier ID = new Identifier("brewed_potion");
	private final Map<ServerAdvancementManager, BrewedPotionCriterion.Handler> handlers = Maps.<ServerAdvancementManager, BrewedPotionCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void addCondition(
		ServerAdvancementManager serverAdvancementManager, Criterion.ConditionsContainer<BrewedPotionCriterion.Conditions> conditionsContainer
	) {
		BrewedPotionCriterion.Handler handler = (BrewedPotionCriterion.Handler)this.handlers.get(serverAdvancementManager);
		if (handler == null) {
			handler = new BrewedPotionCriterion.Handler(serverAdvancementManager);
			this.handlers.put(serverAdvancementManager, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void removeCondition(
		ServerAdvancementManager serverAdvancementManager, Criterion.ConditionsContainer<BrewedPotionCriterion.Conditions> conditionsContainer
	) {
		BrewedPotionCriterion.Handler handler = (BrewedPotionCriterion.Handler)this.handlers.get(serverAdvancementManager);
		if (handler != null) {
			handler.removeCondition(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(serverAdvancementManager);
			}
		}
	}

	@Override
	public void removePlayer(ServerAdvancementManager serverAdvancementManager) {
		this.handlers.remove(serverAdvancementManager);
	}

	public BrewedPotionCriterion.Conditions deserializeConditions(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		Potion potion = null;
		if (jsonObject.has("potion")) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "potion"));
			if (!Registry.POTION.contains(identifier)) {
				throw new JsonSyntaxException("Unknown potion '" + identifier + "'");
			}

			potion = Registry.POTION.get(identifier);
		}

		return new BrewedPotionCriterion.Conditions(potion);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity, Potion potion) {
		BrewedPotionCriterion.Handler handler = (BrewedPotionCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.handle(potion);
		}
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
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			if (this.potion != null) {
				jsonObject.addProperty("potion", Registry.POTION.getId(this.potion).toString());
			}

			return jsonObject;
		}
	}

	static class Handler {
		private final ServerAdvancementManager manager;
		private final Set<Criterion.ConditionsContainer<BrewedPotionCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<BrewedPotionCriterion.Conditions>>newHashSet();

		public Handler(ServerAdvancementManager serverAdvancementManager) {
			this.manager = serverAdvancementManager;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void addCondition(Criterion.ConditionsContainer<BrewedPotionCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void removeCondition(Criterion.ConditionsContainer<BrewedPotionCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void handle(Potion potion) {
			List<Criterion.ConditionsContainer<BrewedPotionCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<BrewedPotionCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(potion)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<BrewedPotionCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<BrewedPotionCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.manager);
				}
			}
		}
	}
}
