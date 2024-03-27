package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;

public class KilledByCrossbowCriterion extends AbstractCriterion<KilledByCrossbowCriterion.Conditions> {
	@Override
	public Codec<KilledByCrossbowCriterion.Conditions> getConditionsCodec() {
		return KilledByCrossbowCriterion.Conditions.CODEC;
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

	public static record Conditions(Optional<LootContextPredicate> player, List<LootContextPredicate> victims, NumberRange.IntRange uniqueEntityTypes)
		implements AbstractCriterion.Conditions {
		public static final Codec<KilledByCrossbowCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(KilledByCrossbowCriterion.Conditions::player),
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.listOf().optionalFieldOf("victims", List.of()).forGetter(KilledByCrossbowCriterion.Conditions::victims),
						NumberRange.IntRange.CODEC
							.optionalFieldOf("unique_entity_types", NumberRange.IntRange.ANY)
							.forGetter(KilledByCrossbowCriterion.Conditions::uniqueEntityTypes)
					)
					.apply(instance, KilledByCrossbowCriterion.Conditions::new)
		);

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
		public void validate(LootContextPredicateValidator validator) {
			AbstractCriterion.Conditions.super.validate(validator);
			validator.validateEntityPredicates(this.victims, ".victims");
		}
	}
}
