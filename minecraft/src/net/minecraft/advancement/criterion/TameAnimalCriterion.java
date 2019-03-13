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
	private static final Identifier field_9753 = new Identifier("tame_animal");
	private final Map<PlayerAdvancementTracker, TameAnimalCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, TameAnimalCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return field_9753;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<TameAnimalCriterion.Conditions> conditionsContainer
	) {
		TameAnimalCriterion.Handler handler = (TameAnimalCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler == null) {
			handler = new TameAnimalCriterion.Handler(playerAdvancementTracker);
			this.handlers.put(playerAdvancementTracker, handler);
		}

		handler.method_9134(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<TameAnimalCriterion.Conditions> conditionsContainer
	) {
		TameAnimalCriterion.Handler handler = (TameAnimalCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler != null) {
			handler.method_9137(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(playerAdvancementTracker);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
		this.handlers.remove(playerAdvancementTracker);
	}

	public TameAnimalCriterion.Conditions method_9133(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityPredicate entityPredicate = EntityPredicate.deserialize(jsonObject.get("entity"));
		return new TameAnimalCriterion.Conditions(entityPredicate);
	}

	public void method_9132(ServerPlayerEntity serverPlayerEntity, AnimalEntity animalEntity) {
		TameAnimalCriterion.Handler handler = (TameAnimalCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.method_9136(serverPlayerEntity, animalEntity);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate entity;

		public Conditions(EntityPredicate entityPredicate) {
			super(TameAnimalCriterion.field_9753);
			this.entity = entityPredicate;
		}

		public static TameAnimalCriterion.Conditions any() {
			return new TameAnimalCriterion.Conditions(EntityPredicate.ANY);
		}

		public static TameAnimalCriterion.Conditions method_16114(EntityPredicate entityPredicate) {
			return new TameAnimalCriterion.Conditions(entityPredicate);
		}

		public boolean method_9139(ServerPlayerEntity serverPlayerEntity, AnimalEntity animalEntity) {
			return this.entity.method_8914(serverPlayerEntity, animalEntity);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("entity", this.entity.serialize());
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker field_9756;
		private final Set<Criterion.ConditionsContainer<TameAnimalCriterion.Conditions>> Conditions = Sets.<Criterion.ConditionsContainer<TameAnimalCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.field_9756 = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.Conditions.isEmpty();
		}

		public void method_9134(Criterion.ConditionsContainer<TameAnimalCriterion.Conditions> conditionsContainer) {
			this.Conditions.add(conditionsContainer);
		}

		public void method_9137(Criterion.ConditionsContainer<TameAnimalCriterion.Conditions> conditionsContainer) {
			this.Conditions.remove(conditionsContainer);
		}

		public void method_9136(ServerPlayerEntity serverPlayerEntity, AnimalEntity animalEntity) {
			List<Criterion.ConditionsContainer<TameAnimalCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<TameAnimalCriterion.Conditions> conditionsContainer : this.Conditions) {
				if (conditionsContainer.method_797().method_9139(serverPlayerEntity, animalEntity)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<TameAnimalCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<TameAnimalCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.field_9756);
				}
			}
		}
	}
}
