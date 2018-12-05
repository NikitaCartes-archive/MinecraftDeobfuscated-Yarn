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
import net.minecraft.advancement.ServerAdvancementManager;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ChanneledLightningCriterion implements Criterion<ChanneledLightningCriterion.Conditions> {
	private static final Identifier ID = new Identifier("channeled_lightning");
	private final Map<ServerAdvancementManager, ChanneledLightningCriterion.class_2003> field_9500 = Maps.<ServerAdvancementManager, ChanneledLightningCriterion.class_2003>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void addCondition(
		ServerAdvancementManager serverAdvancementManager, Criterion.ConditionsContainer<ChanneledLightningCriterion.Conditions> conditionsContainer
	) {
		ChanneledLightningCriterion.class_2003 lv = (ChanneledLightningCriterion.class_2003)this.field_9500.get(serverAdvancementManager);
		if (lv == null) {
			lv = new ChanneledLightningCriterion.class_2003(serverAdvancementManager);
			this.field_9500.put(serverAdvancementManager, lv);
		}

		lv.method_8804(conditionsContainer);
	}

	@Override
	public void removeCondition(
		ServerAdvancementManager serverAdvancementManager, Criterion.ConditionsContainer<ChanneledLightningCriterion.Conditions> conditionsContainer
	) {
		ChanneledLightningCriterion.class_2003 lv = (ChanneledLightningCriterion.class_2003)this.field_9500.get(serverAdvancementManager);
		if (lv != null) {
			lv.method_8807(conditionsContainer);
			if (lv.method_8805()) {
				this.field_9500.remove(serverAdvancementManager);
			}
		}
	}

	@Override
	public void removePlayer(ServerAdvancementManager serverAdvancementManager) {
		this.field_9500.remove(serverAdvancementManager);
	}

	public ChanneledLightningCriterion.Conditions deserializeConditions(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
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
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("victims", EntityPredicate.serializeAll(this.victims));
			return jsonObject;
		}
	}

	static class class_2003 {
		private final ServerAdvancementManager field_9502;
		private final Set<Criterion.ConditionsContainer<ChanneledLightningCriterion.Conditions>> field_9501 = Sets.<Criterion.ConditionsContainer<ChanneledLightningCriterion.Conditions>>newHashSet();

		public class_2003(ServerAdvancementManager serverAdvancementManager) {
			this.field_9502 = serverAdvancementManager;
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
				if (conditionsContainer.getConditions().matches(serverPlayerEntity, collection)) {
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
