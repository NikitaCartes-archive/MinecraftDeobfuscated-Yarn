package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class KilledByCrossbowCriterion extends AbstractCriterion<KilledByCrossbowCriterion.Conditions> {
	public KilledByCrossbowCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		List<LootContextPredicate> list = EntityPredicate.contextPredicateArrayFromJson(jsonObject, "victims", advancementEntityPredicateDeserializer);
		NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("unique_entity_types"));
		return new KilledByCrossbowCriterion.Conditions(optional, list, intRange);
	}

	public void trigger(ServerPlayerEntity player, Collection<Entity> piercingKilledEntities) {
		List<LootContext> list = Lists.<LootContext>newArrayList();
		Set<EntityType<?>> set = Sets.<EntityType<?>>newHashSet();

		for (Entity entity : piercingKilledEntities) {
			set.add(entity.getType());
			list.add(EntityPredicate.createAdvancementEntityLootContext(player, entity));
		}

		this.trigger(player, conditions -> conditions.matches(list, set.size()));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final List<LootContextPredicate> victims;
		private final NumberRange.IntRange uniqueEntityTypes;

		public Conditions(Optional<LootContextPredicate> playerPredicate, List<LootContextPredicate> victims, NumberRange.IntRange uniqueEntityTypes) {
			super(playerPredicate);
			this.victims = victims;
			this.uniqueEntityTypes = uniqueEntityTypes;
		}

		public static AdvancementCriterion<KilledByCrossbowCriterion.Conditions> create(EntityPredicate.Builder... victimPredicates) {
			return Criteria.KILLED_BY_CROSSBOW
				.create(
					new KilledByCrossbowCriterion.Conditions(
						Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicates(victimPredicates), NumberRange.IntRange.ANY
					)
				);
		}

		public static AdvancementCriterion<KilledByCrossbowCriterion.Conditions> create(NumberRange.IntRange uniqueEntityTypes) {
			return Criteria.KILLED_BY_CROSSBOW.create(new KilledByCrossbowCriterion.Conditions(Optional.empty(), List.of(), uniqueEntityTypes));
		}

		public boolean matches(Collection<LootContext> victimContexts, int uniqueEntityTypeCount) {
			if (!this.victims.isEmpty()) {
				List<LootContext> list = Lists.<LootContext>newArrayList(victimContexts);

				for (LootContextPredicate lootContextPredicate : this.victims) {
					boolean bl = false;
					Iterator<LootContext> iterator = list.iterator();

					while (iterator.hasNext()) {
						LootContext lootContext = (LootContext)iterator.next();
						if (lootContextPredicate.test(lootContext)) {
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

			return this.uniqueEntityTypes.test(uniqueEntityTypeCount);
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			jsonObject.add("victims", LootContextPredicate.toPredicatesJsonArray(this.victims));
			jsonObject.add("unique_entity_types", this.uniqueEntityTypes.toJson());
			return jsonObject;
		}
	}
}
