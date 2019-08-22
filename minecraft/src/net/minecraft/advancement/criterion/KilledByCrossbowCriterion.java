package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.NumberRange;

public class KilledByCrossbowCriterion implements Criterion<KilledByCrossbowCriterion.Conditions> {
	private static final Identifier ID = new Identifier("killed_by_crossbow");
	private final Map<PlayerAdvancementTracker, KilledByCrossbowCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, KilledByCrossbowCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<KilledByCrossbowCriterion.Conditions> conditionsContainer
	) {
		KilledByCrossbowCriterion.Handler handler = (KilledByCrossbowCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler == null) {
			handler = new KilledByCrossbowCriterion.Handler(playerAdvancementTracker);
			this.handlers.put(playerAdvancementTracker, handler);
		}

		handler.add(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<KilledByCrossbowCriterion.Conditions> conditionsContainer
	) {
		KilledByCrossbowCriterion.Handler handler = (KilledByCrossbowCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler != null) {
			handler.remove(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(playerAdvancementTracker);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
		this.handlers.remove(playerAdvancementTracker);
	}

	public KilledByCrossbowCriterion.Conditions method_8979(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityPredicate[] entityPredicates = EntityPredicate.deserializeAll(jsonObject.get("victims"));
		NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("unique_entity_types"));
		return new KilledByCrossbowCriterion.Conditions(entityPredicates, intRange);
	}

	public void trigger(ServerPlayerEntity serverPlayerEntity, Collection<Entity> collection, int i) {
		KilledByCrossbowCriterion.Handler handler = (KilledByCrossbowCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.trigger(serverPlayerEntity, collection, i);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate[] victims;
		private final NumberRange.IntRange uniqueEntityTypes;

		public Conditions(EntityPredicate[] entityPredicates, NumberRange.IntRange intRange) {
			super(KilledByCrossbowCriterion.ID);
			this.victims = entityPredicates;
			this.uniqueEntityTypes = intRange;
		}

		public static KilledByCrossbowCriterion.Conditions create(EntityPredicate.Builder... builders) {
			EntityPredicate[] entityPredicates = new EntityPredicate[builders.length];

			for (int i = 0; i < builders.length; i++) {
				EntityPredicate.Builder builder = builders[i];
				entityPredicates[i] = builder.build();
			}

			return new KilledByCrossbowCriterion.Conditions(entityPredicates, NumberRange.IntRange.ANY);
		}

		public static KilledByCrossbowCriterion.Conditions create(NumberRange.IntRange intRange) {
			EntityPredicate[] entityPredicates = new EntityPredicate[0];
			return new KilledByCrossbowCriterion.Conditions(entityPredicates, intRange);
		}

		public boolean matches(ServerPlayerEntity serverPlayerEntity, Collection<Entity> collection, int i) {
			if (this.victims.length > 0) {
				List<Entity> list = Lists.<Entity>newArrayList(collection);

				for (EntityPredicate entityPredicate : this.victims) {
					boolean bl = false;
					Iterator<Entity> iterator = list.iterator();

					while (iterator.hasNext()) {
						Entity entity = (Entity)iterator.next();
						if (entityPredicate.test(serverPlayerEntity, entity)) {
							iterator.remove();
							bl = true;
							break;
						}
					}

					if (!bl) {
						return false;
					}
				}
			}

			if (this.uniqueEntityTypes == NumberRange.IntRange.ANY) {
				return true;
			} else {
				Set<EntityType<?>> set = Sets.<EntityType<?>>newHashSet();

				for (Entity entity2 : collection) {
					set.add(entity2.getType());
				}

				return this.uniqueEntityTypes.test(set.size()) && this.uniqueEntityTypes.test(i);
			}
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("victims", EntityPredicate.serializeAll(this.victims));
			jsonObject.add("unique_entity_types", this.uniqueEntityTypes.serialize());
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker manager;
		private final Set<Criterion.ConditionsContainer<KilledByCrossbowCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<KilledByCrossbowCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.manager = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void add(Criterion.ConditionsContainer<KilledByCrossbowCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void remove(Criterion.ConditionsContainer<KilledByCrossbowCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void trigger(ServerPlayerEntity serverPlayerEntity, Collection<Entity> collection, int i) {
			List<Criterion.ConditionsContainer<KilledByCrossbowCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<KilledByCrossbowCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(serverPlayerEntity, collection, i)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<KilledByCrossbowCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<KilledByCrossbowCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.manager);
				}
			}
		}
	}
}
