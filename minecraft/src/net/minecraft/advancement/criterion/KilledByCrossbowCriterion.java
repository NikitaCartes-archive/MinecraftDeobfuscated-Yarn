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
	private final Map<PlayerAdvancementTracker, KilledByCrossbowCriterion.class_2077> field_9656 = Maps.<PlayerAdvancementTracker, KilledByCrossbowCriterion.class_2077>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<KilledByCrossbowCriterion.Conditions> conditionsContainer
	) {
		KilledByCrossbowCriterion.class_2077 lv = (KilledByCrossbowCriterion.class_2077)this.field_9656.get(playerAdvancementTracker);
		if (lv == null) {
			lv = new KilledByCrossbowCriterion.class_2077(playerAdvancementTracker);
			this.field_9656.put(playerAdvancementTracker, lv);
		}

		lv.method_8982(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<KilledByCrossbowCriterion.Conditions> conditionsContainer
	) {
		KilledByCrossbowCriterion.class_2077 lv = (KilledByCrossbowCriterion.class_2077)this.field_9656.get(playerAdvancementTracker);
		if (lv != null) {
			lv.method_8985(conditionsContainer);
			if (lv.method_8984()) {
				this.field_9656.remove(playerAdvancementTracker);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
		this.field_9656.remove(playerAdvancementTracker);
	}

	public KilledByCrossbowCriterion.Conditions method_8979(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityPredicate[] entityPredicates = EntityPredicate.deserializeAll(jsonObject.get("victims"));
		NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("unique_entity_types"));
		return new KilledByCrossbowCriterion.Conditions(entityPredicates, intRange);
	}

	public void method_8980(ServerPlayerEntity serverPlayerEntity, Collection<Entity> collection, int i) {
		KilledByCrossbowCriterion.class_2077 lv = (KilledByCrossbowCriterion.class_2077)this.field_9656.get(serverPlayerEntity.getAdvancementManager());
		if (lv != null) {
			lv.method_8983(serverPlayerEntity, collection, i);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate[] victims;
		private final NumberRange.IntRange field_9659;

		public Conditions(EntityPredicate[] entityPredicates, NumberRange.IntRange intRange) {
			super(KilledByCrossbowCriterion.ID);
			this.victims = entityPredicates;
			this.field_9659 = intRange;
		}

		public static KilledByCrossbowCriterion.Conditions method_8986(EntityPredicate.Builder... builders) {
			EntityPredicate[] entityPredicates = new EntityPredicate[builders.length];

			for (int i = 0; i < builders.length; i++) {
				EntityPredicate.Builder builder = builders[i];
				entityPredicates[i] = builder.build();
			}

			return new KilledByCrossbowCriterion.Conditions(entityPredicates, NumberRange.IntRange.ANY);
		}

		public static KilledByCrossbowCriterion.Conditions method_8987(NumberRange.IntRange intRange) {
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

			if (this.field_9659 == NumberRange.IntRange.ANY) {
				return true;
			} else {
				Set<EntityType<?>> set = Sets.<EntityType<?>>newHashSet();

				for (Entity entity2 : collection) {
					set.add(entity2.getType());
				}

				return this.field_9659.test(set.size()) && this.field_9659.test(i);
			}
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("victims", EntityPredicate.serializeAll(this.victims));
			jsonObject.add("unique_entity_types", this.field_9659.serialize());
			return jsonObject;
		}
	}

	static class class_2077 {
		private final PlayerAdvancementTracker field_9658;
		private final Set<Criterion.ConditionsContainer<KilledByCrossbowCriterion.Conditions>> field_9657 = Sets.<Criterion.ConditionsContainer<KilledByCrossbowCriterion.Conditions>>newHashSet();

		public class_2077(PlayerAdvancementTracker playerAdvancementTracker) {
			this.field_9658 = playerAdvancementTracker;
		}

		public boolean method_8984() {
			return this.field_9657.isEmpty();
		}

		public void method_8982(Criterion.ConditionsContainer<KilledByCrossbowCriterion.Conditions> conditionsContainer) {
			this.field_9657.add(conditionsContainer);
		}

		public void method_8985(Criterion.ConditionsContainer<KilledByCrossbowCriterion.Conditions> conditionsContainer) {
			this.field_9657.remove(conditionsContainer);
		}

		public void method_8983(ServerPlayerEntity serverPlayerEntity, Collection<Entity> collection, int i) {
			List<Criterion.ConditionsContainer<KilledByCrossbowCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<KilledByCrossbowCriterion.Conditions> conditionsContainer : this.field_9657) {
				if (conditionsContainer.getConditions().matches(serverPlayerEntity, collection, i)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<KilledByCrossbowCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<KilledByCrossbowCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.field_9658);
				}
			}
		}
	}
}
