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
import javax.annotation.Nullable;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class BredAnimalsCriterion implements Criterion<BredAnimalsCriterion.Conditions> {
	private static final Identifier field_1271 = new Identifier("bred_animals");
	private final Map<PlayerAdvancementTracker, BredAnimalsCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, BredAnimalsCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return field_1271;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<BredAnimalsCriterion.Conditions> conditionsContainer
	) {
		BredAnimalsCriterion.Handler handler = (BredAnimalsCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler == null) {
			handler = new BredAnimalsCriterion.Handler(playerAdvancementTracker);
			this.handlers.put(playerAdvancementTracker, handler);
		}

		handler.method_856(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<BredAnimalsCriterion.Conditions> conditionsContainer
	) {
		BredAnimalsCriterion.Handler handler = (BredAnimalsCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler != null) {
			handler.method_859(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(playerAdvancementTracker);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
		this.handlers.remove(playerAdvancementTracker);
	}

	public BredAnimalsCriterion.Conditions method_854(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityPredicate entityPredicate = EntityPredicate.deserialize(jsonObject.get("parent"));
		EntityPredicate entityPredicate2 = EntityPredicate.deserialize(jsonObject.get("partner"));
		EntityPredicate entityPredicate3 = EntityPredicate.deserialize(jsonObject.get("child"));
		return new BredAnimalsCriterion.Conditions(entityPredicate, entityPredicate2, entityPredicate3);
	}

	public void method_855(
		ServerPlayerEntity serverPlayerEntity, AnimalEntity animalEntity, @Nullable AnimalEntity animalEntity2, @Nullable PassiveEntity passiveEntity
	) {
		BredAnimalsCriterion.Handler handler = (BredAnimalsCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.method_858(serverPlayerEntity, animalEntity, animalEntity2, passiveEntity);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate field_1276;
		private final EntityPredicate field_1277;
		private final EntityPredicate field_1275;

		public Conditions(EntityPredicate entityPredicate, EntityPredicate entityPredicate2, EntityPredicate entityPredicate3) {
			super(BredAnimalsCriterion.field_1271);
			this.field_1276 = entityPredicate;
			this.field_1277 = entityPredicate2;
			this.field_1275 = entityPredicate3;
		}

		public static BredAnimalsCriterion.Conditions any() {
			return new BredAnimalsCriterion.Conditions(EntityPredicate.ANY, EntityPredicate.ANY, EntityPredicate.ANY);
		}

		public static BredAnimalsCriterion.Conditions method_861(EntityPredicate.Builder builder) {
			return new BredAnimalsCriterion.Conditions(builder.build(), EntityPredicate.ANY, EntityPredicate.ANY);
		}

		public boolean method_862(
			ServerPlayerEntity serverPlayerEntity, AnimalEntity animalEntity, @Nullable AnimalEntity animalEntity2, @Nullable PassiveEntity passiveEntity
		) {
			return !this.field_1275.method_8914(serverPlayerEntity, passiveEntity)
				? false
				: this.field_1276.method_8914(serverPlayerEntity, animalEntity) && this.field_1277.method_8914(serverPlayerEntity, animalEntity2)
					|| this.field_1276.method_8914(serverPlayerEntity, animalEntity2) && this.field_1277.method_8914(serverPlayerEntity, animalEntity);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("parent", this.field_1276.serialize());
			jsonObject.add("partner", this.field_1277.serialize());
			jsonObject.add("child", this.field_1275.serialize());
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker field_1274;
		private final Set<Criterion.ConditionsContainer<BredAnimalsCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<BredAnimalsCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.field_1274 = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void method_856(Criterion.ConditionsContainer<BredAnimalsCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void method_859(Criterion.ConditionsContainer<BredAnimalsCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void method_858(
			ServerPlayerEntity serverPlayerEntity, AnimalEntity animalEntity, @Nullable AnimalEntity animalEntity2, @Nullable PassiveEntity passiveEntity
		) {
			List<Criterion.ConditionsContainer<BredAnimalsCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<BredAnimalsCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.method_797().method_862(serverPlayerEntity, animalEntity, animalEntity2, passiveEntity)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<BredAnimalsCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<BredAnimalsCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.field_1274);
				}
			}
		}
	}
}
