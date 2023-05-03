package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class KilledByCrossbowCriterion extends AbstractCriterion<KilledByCrossbowCriterion.Conditions> {
	static final Identifier ID = new Identifier("killed_by_crossbow");

	@Override
	public Identifier getId() {
		return ID;
	}

	public KilledByCrossbowCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		LootContextPredicate[] lootContextPredicates = EntityPredicate.contextPredicateArrayFromJson(jsonObject, "victims", advancementEntityPredicateDeserializer);
		NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("unique_entity_types"));
		return new KilledByCrossbowCriterion.Conditions(lootContextPredicate, lootContextPredicates, intRange);
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
		private final LootContextPredicate[] victims;
		private final NumberRange.IntRange uniqueEntityTypes;

		public Conditions(LootContextPredicate player, LootContextPredicate[] victims, NumberRange.IntRange uniqueEntityTypes) {
			super(KilledByCrossbowCriterion.ID, player);
			this.victims = victims;
			this.uniqueEntityTypes = uniqueEntityTypes;
		}

		public static KilledByCrossbowCriterion.Conditions create(EntityPredicate.Builder... victimPredicates) {
			LootContextPredicate[] lootContextPredicates = new LootContextPredicate[victimPredicates.length];

			for (int i = 0; i < victimPredicates.length; i++) {
				EntityPredicate.Builder builder = victimPredicates[i];
				lootContextPredicates[i] = EntityPredicate.asLootContextPredicate(builder.build());
			}

			return new KilledByCrossbowCriterion.Conditions(LootContextPredicate.EMPTY, lootContextPredicates, NumberRange.IntRange.ANY);
		}

		public static KilledByCrossbowCriterion.Conditions create(NumberRange.IntRange uniqueEntityTypes) {
			LootContextPredicate[] lootContextPredicates = new LootContextPredicate[0];
			return new KilledByCrossbowCriterion.Conditions(LootContextPredicate.EMPTY, lootContextPredicates, uniqueEntityTypes);
		}

		public boolean matches(Collection<LootContext> victimContexts, int uniqueEntityTypeCount) {
			if (this.victims.length > 0) {
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
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("victims", LootContextPredicate.toPredicatesJsonArray(this.victims, predicateSerializer));
			jsonObject.add("unique_entity_types", this.uniqueEntityTypes.toJson());
			return jsonObject;
		}
	}
}
