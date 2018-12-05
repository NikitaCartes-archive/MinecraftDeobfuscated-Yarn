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
import net.minecraft.advancement.ServerAdvancementManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class OnKilledCriterion implements Criterion<OnKilledCriterion.class_2083> {
	private final Map<ServerAdvancementManager, OnKilledCriterion.Handler> handlers = Maps.<ServerAdvancementManager, OnKilledCriterion.Handler>newHashMap();
	private final Identifier id;

	public OnKilledCriterion(Identifier identifier) {
		this.id = identifier;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public void addCondition(ServerAdvancementManager serverAdvancementManager, Criterion.ConditionsContainer<OnKilledCriterion.class_2083> conditionsContainer) {
		OnKilledCriterion.Handler handler = (OnKilledCriterion.Handler)this.handlers.get(serverAdvancementManager);
		if (handler == null) {
			handler = new OnKilledCriterion.Handler(serverAdvancementManager);
			this.handlers.put(serverAdvancementManager, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void removeCondition(ServerAdvancementManager serverAdvancementManager, Criterion.ConditionsContainer<OnKilledCriterion.class_2083> conditionsContainer) {
		OnKilledCriterion.Handler handler = (OnKilledCriterion.Handler)this.handlers.get(serverAdvancementManager);
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

	public OnKilledCriterion.class_2083 deserializeConditions(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		return new OnKilledCriterion.class_2083(
			this.id, EntityPredicate.deserialize(jsonObject.get("entity")), DamageSourcePredicate.deserialize(jsonObject.get("killing_blow"))
		);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity, Entity entity, DamageSource damageSource) {
		OnKilledCriterion.Handler handler = (OnKilledCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.handle(serverPlayerEntity, entity, damageSource);
		}
	}

	static class Handler {
		private final ServerAdvancementManager manager;
		private final Set<Criterion.ConditionsContainer<OnKilledCriterion.class_2083>> conditions = Sets.<Criterion.ConditionsContainer<OnKilledCriterion.class_2083>>newHashSet();

		public Handler(ServerAdvancementManager serverAdvancementManager) {
			this.manager = serverAdvancementManager;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void addCondition(Criterion.ConditionsContainer<OnKilledCriterion.class_2083> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void removeCondition(Criterion.ConditionsContainer<OnKilledCriterion.class_2083> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void handle(ServerPlayerEntity serverPlayerEntity, Entity entity, DamageSource damageSource) {
			List<Criterion.ConditionsContainer<OnKilledCriterion.class_2083>> list = null;

			for (Criterion.ConditionsContainer<OnKilledCriterion.class_2083> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().method_9000(serverPlayerEntity, entity, damageSource)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<OnKilledCriterion.class_2083>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<OnKilledCriterion.class_2083> conditionsContainerx : list) {
					conditionsContainerx.apply(this.manager);
				}
			}
		}
	}

	public static class class_2081 extends AbstractCriterionConditions {
		private final EntityPredicate entity;
		private final DamageSourcePredicate killingBlow;

		public class_2081(Identifier identifier, EntityPredicate entityPredicate, DamageSourcePredicate damageSourcePredicate) {
			super(identifier);
			this.entity = entityPredicate;
			this.killingBlow = damageSourcePredicate;
		}

		public static OnKilledCriterion.class_2081 method_8992(EntityPredicate.Builder builder, DamageSourcePredicate.Builder builder2) {
			return new OnKilledCriterion.class_2081(CriterionCriterions.PLAYER_KILLED_ENTITY.id, builder.build(), builder2.build());
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("entity", this.entity.serialize());
			jsonObject.add("killing_blow", this.killingBlow.serialize());
			return jsonObject;
		}
	}

	public static class class_2083 extends AbstractCriterionConditions {
		private final EntityPredicate entity;
		private final DamageSourcePredicate killingBlow;

		public class_2083(Identifier identifier, EntityPredicate entityPredicate, DamageSourcePredicate damageSourcePredicate) {
			super(identifier);
			this.entity = entityPredicate;
			this.killingBlow = damageSourcePredicate;
		}

		public static OnKilledCriterion.class_2083 method_8997(EntityPredicate.Builder builder) {
			return new OnKilledCriterion.class_2083(CriterionCriterions.PLAYER_KILLED_ENTITY.id, builder.build(), DamageSourcePredicate.EMPTY);
		}

		public static OnKilledCriterion.class_2083 method_8999() {
			return new OnKilledCriterion.class_2083(CriterionCriterions.PLAYER_KILLED_ENTITY.id, EntityPredicate.ANY, DamageSourcePredicate.EMPTY);
		}

		public static OnKilledCriterion.class_2083 method_9001(EntityPredicate.Builder builder, DamageSourcePredicate.Builder builder2) {
			return new OnKilledCriterion.class_2083(CriterionCriterions.PLAYER_KILLED_ENTITY.id, builder.build(), builder2.build());
		}

		public static OnKilledCriterion.class_2083 method_8998() {
			return new OnKilledCriterion.class_2083(CriterionCriterions.ENTITY_KILLED_PLAYER.id, EntityPredicate.ANY, DamageSourcePredicate.EMPTY);
		}

		public boolean method_9000(ServerPlayerEntity serverPlayerEntity, Entity entity, DamageSource damageSource) {
			return !this.killingBlow.test(serverPlayerEntity, damageSource) ? false : this.entity.test(serverPlayerEntity, entity);
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("entity", this.entity.serialize());
			jsonObject.add("killing_blow", this.killingBlow.serialize());
			return jsonObject;
		}
	}
}
