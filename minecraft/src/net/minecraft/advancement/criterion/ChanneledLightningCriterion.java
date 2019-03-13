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
	private static final Identifier field_9499 = new Identifier("channeled_lightning");
	private final Map<PlayerAdvancementTracker, ChanneledLightningCriterion.class_2003> field_9500 = Maps.<PlayerAdvancementTracker, ChanneledLightningCriterion.class_2003>newHashMap();

	@Override
	public Identifier getId() {
		return field_9499;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<ChanneledLightningCriterion.Conditions> conditionsContainer
	) {
		ChanneledLightningCriterion.class_2003 lv = (ChanneledLightningCriterion.class_2003)this.field_9500.get(playerAdvancementTracker);
		if (lv == null) {
			lv = new ChanneledLightningCriterion.class_2003(playerAdvancementTracker);
			this.field_9500.put(playerAdvancementTracker, lv);
		}

		lv.method_8804(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<ChanneledLightningCriterion.Conditions> conditionsContainer
	) {
		ChanneledLightningCriterion.class_2003 lv = (ChanneledLightningCriterion.class_2003)this.field_9500.get(playerAdvancementTracker);
		if (lv != null) {
			lv.method_8807(conditionsContainer);
			if (lv.method_8805()) {
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

	public void method_8803(ServerPlayerEntity serverPlayerEntity, Collection<? extends Entity> collection) {
		ChanneledLightningCriterion.class_2003 lv = (ChanneledLightningCriterion.class_2003)this.field_9500.get(serverPlayerEntity.getAdvancementManager());
		if (lv != null) {
			lv.method_8806(serverPlayerEntity, collection);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate[] field_9503;

		public Conditions(EntityPredicate[] entityPredicates) {
			super(ChanneledLightningCriterion.field_9499);
			this.field_9503 = entityPredicates;
		}

		public static ChanneledLightningCriterion.Conditions method_8809(EntityPredicate... entityPredicates) {
			return new ChanneledLightningCriterion.Conditions(entityPredicates);
		}

		public boolean method_8808(ServerPlayerEntity serverPlayerEntity, Collection<? extends Entity> collection) {
			for (EntityPredicate entityPredicate : this.field_9503) {
				boolean bl = false;

				for (Entity entity : collection) {
					if (entityPredicate.method_8914(serverPlayerEntity, entity)) {
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
			jsonObject.add("victims", EntityPredicate.serializeAll(this.field_9503));
			return jsonObject;
		}
	}

	static class class_2003 {
		private final PlayerAdvancementTracker field_9502;
		private final Set<Criterion.ConditionsContainer<ChanneledLightningCriterion.Conditions>> field_9501 = Sets.<Criterion.ConditionsContainer<ChanneledLightningCriterion.Conditions>>newHashSet();

		public class_2003(PlayerAdvancementTracker playerAdvancementTracker) {
			this.field_9502 = playerAdvancementTracker;
		}

		public boolean method_8805() {
			return this.field_9501.isEmpty();
		}

		public void method_8804(Criterion.ConditionsContainer<ChanneledLightningCriterion.Conditions> conditionsContainer) {
			this.field_9501.add(conditionsContainer);
		}

		public void method_8807(Criterion.ConditionsContainer<ChanneledLightningCriterion.Conditions> conditionsContainer) {
			this.field_9501.remove(conditionsContainer);
		}

		public void method_8806(ServerPlayerEntity serverPlayerEntity, Collection<? extends Entity> collection) {
			List<Criterion.ConditionsContainer<ChanneledLightningCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<ChanneledLightningCriterion.Conditions> conditionsContainer : this.field_9501) {
				if (conditionsContainer.method_797().method_8808(serverPlayerEntity, collection)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<ChanneledLightningCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<ChanneledLightningCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.field_9502);
				}
			}
		}
	}
}
