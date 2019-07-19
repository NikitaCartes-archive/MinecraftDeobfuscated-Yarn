package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class TameAnimalCriterion implements Criterion<TameAnimalCriterion.Conditions> {
	private static final Identifier ID = new Identifier("tame_animal");
	private final Map<PlayerAdvancementTracker, TameAnimalCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, TameAnimalCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void beginTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<TameAnimalCriterion.Conditions> conditionsContainer) {
		TameAnimalCriterion.Handler handler = (TameAnimalCriterion.Handler)this.handlers.get(manager);
		if (handler == null) {
			handler = new TameAnimalCriterion.Handler(manager);
			this.handlers.put(manager, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<TameAnimalCriterion.Conditions> conditionsContainer) {
		TameAnimalCriterion.Handler handler = (TameAnimalCriterion.Handler)this.handlers.get(manager);
		if (handler != null) {
			handler.removeCondition(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(manager);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker tracker) {
		this.handlers.remove(tracker);
	}

	public TameAnimalCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("entity"));
		return new TameAnimalCriterion.Conditions(entityPredicate);
	}

	public void trigger(ServerPlayerEntity player, AnimalEntity entity) {
		TameAnimalCriterion.Handler handler = (TameAnimalCriterion.Handler)this.handlers.get(player.getAdvancementTracker());
		if (handler != null) {
			handler.handle(player, entity);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate entity;

		public Conditions(EntityPredicate entityPredicate) {
			super(TameAnimalCriterion.ID);
			this.entity = entityPredicate;
		}

		public static TameAnimalCriterion.Conditions any() {
			return new TameAnimalCriterion.Conditions(EntityPredicate.ANY);
		}

		public static TameAnimalCriterion.Conditions create(EntityPredicate entityPredicate) {
			return new TameAnimalCriterion.Conditions(entityPredicate);
		}

		public boolean matches(ServerPlayerEntity serverPlayerEntity, AnimalEntity animalEntity) {
			return this.entity.test(serverPlayerEntity, animalEntity);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("entity", this.entity.serialize());
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker manager;
		private final Set<Criterion.ConditionsContainer<TameAnimalCriterion.Conditions>> Conditions = Sets.<Criterion.ConditionsContainer<TameAnimalCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.manager = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.Conditions.isEmpty();
		}

		public void addCondition(Criterion.ConditionsContainer<TameAnimalCriterion.Conditions> conditionsContainer) {
			this.Conditions.add(conditionsContainer);
		}

		public void removeCondition(Criterion.ConditionsContainer<TameAnimalCriterion.Conditions> conditionsContainer) {
			this.Conditions.remove(conditionsContainer);
		}

		public void handle(ServerPlayerEntity entity, AnimalEntity animalEntity) {
			List<Criterion.ConditionsContainer<TameAnimalCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<TameAnimalCriterion.Conditions> conditionsContainer : this.Conditions) {
				if (conditionsContainer.getConditions().matches(entity, animalEntity)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<TameAnimalCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<TameAnimalCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.manager);
				}
			}
		}
	}
}
