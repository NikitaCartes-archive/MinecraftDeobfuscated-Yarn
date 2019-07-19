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
	private final Map<PlayerAdvancementTracker, ChanneledLightningCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, ChanneledLightningCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void beginTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<ChanneledLightningCriterion.Conditions> conditionsContainer) {
		ChanneledLightningCriterion.Handler handler = (ChanneledLightningCriterion.Handler)this.handlers.get(manager);
		if (handler == null) {
			handler = new ChanneledLightningCriterion.Handler(manager);
			this.handlers.put(manager, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<ChanneledLightningCriterion.Conditions> conditionsContainer) {
		ChanneledLightningCriterion.Handler handler = (ChanneledLightningCriterion.Handler)this.handlers.get(manager);
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

	public ChanneledLightningCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityPredicate[] entityPredicates = EntityPredicate.fromJsonArray(jsonObject.get("victims"));
		return new ChanneledLightningCriterion.Conditions(entityPredicates);
	}

	public void trigger(ServerPlayerEntity player, Collection<? extends Entity> victims) {
		ChanneledLightningCriterion.Handler handler = (ChanneledLightningCriterion.Handler)this.handlers.get(player.getAdvancementTracker());
		if (handler != null) {
			handler.handle(player, victims);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate[] victims;

		public Conditions(EntityPredicate[] victims) {
			super(ChanneledLightningCriterion.ID);
			this.victims = victims;
		}

		public static ChanneledLightningCriterion.Conditions create(EntityPredicate... victims) {
			return new ChanneledLightningCriterion.Conditions(victims);
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

		public Handler(PlayerAdvancementTracker manager) {
			this.manager = manager;
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
