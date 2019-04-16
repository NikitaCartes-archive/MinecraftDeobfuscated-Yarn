package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ChanneledLightningCriterion implements Criterion<ChanneledLightningCriterion.Conditions> {
	private static final Identifier ID = new Identifier("channeled_lightning");
	private final Map<PlayerAdvancementTracker, ChanneledLightningCriterion.Handler> field_9500 = Maps.<PlayerAdvancementTracker, ChanneledLightningCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<ChanneledLightningCriterion.Conditions> conditionsContainer
	) {
		ChanneledLightningCriterion.Handler handler = (ChanneledLightningCriterion.Handler)this.field_9500.get(playerAdvancementTracker);
		if (handler == null) {
			handler = new ChanneledLightningCriterion.Handler(playerAdvancementTracker);
			this.field_9500.put(playerAdvancementTracker, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<ChanneledLightningCriterion.Conditions> conditionsContainer
	) {
		ChanneledLightningCriterion.Handler handler = (ChanneledLightningCriterion.Handler)this.field_9500.get(playerAdvancementTracker);
		if (handler != null) {
			handler.removeCondition(conditionsContainer);
			if (handler.isEmpty()) {
				this.field_9500.remove(playerAdvancementTracker);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
		this.field_9500.remove(playerAdvancementTracker);
	}

	public ChanneledLightningCriterion.Conditions method_8801(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityPredicate[] entityPredicates = EntityPredicate.deserializeAll(jsonObject.get("victims"));
		return new ChanneledLightningCriterion.Conditions(entityPredicates);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity, Collection<? extends Entity> collection) {
		ChanneledLightningCriterion.Handler handler = (ChanneledLightningCriterion.Handler)this.field_9500.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.handle(serverPlayerEntity, collection);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate[] victims;

		public Conditions(EntityPredicate[] entityPredicates) {
			super(ChanneledLightningCriterion.ID);
			this.victims = entityPredicates;
		}

		public static ChanneledLightningCriterion.Conditions create(EntityPredicate... entityPredicates) {
			return new ChanneledLightningCriterion.Conditions(entityPredicates);
		}

		public boolean matches(ServerPlayerEntity serverPlayerEntity, Collection<? extends Entity> collection) {
			for (EntityPredicate entityPredicate : this.victims) {
				boolean bl = false;

				for (Entity entity : collection) {
					if (entityPredicate.test(serverPlayerEntity, entity)) {
						bl = true;
						break;
					}
				}

				if (!bl) {
					return false;
				}
			}

			return true;
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("victims", EntityPredicate.serializeAll(this.victims));
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker manager;
		private final Set<Criterion.ConditionsContainer<ChanneledLightningCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<ChanneledLightningCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.manager = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void addCondition(Criterion.ConditionsContainer<ChanneledLightningCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void removeCondition(Criterion.ConditionsContainer<ChanneledLightningCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void handle(ServerPlayerEntity serverPlayerEntity, Collection<? extends Entity> collection) {
			List<Criterion.ConditionsContainer<ChanneledLightningCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<ChanneledLightningCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(serverPlayerEntity, collection)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<ChanneledLightningCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<ChanneledLightningCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.manager);
				}
			}
		}
	}
}
