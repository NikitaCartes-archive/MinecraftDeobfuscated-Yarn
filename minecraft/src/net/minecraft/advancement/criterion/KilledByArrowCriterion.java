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
import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.server.network.ServerPlayerEntity;

public class KilledByArrowCriterion extends AbstractCriterion<KilledByArrowCriterion.Conditions> {
	@Override
	public Codec<KilledByArrowCriterion.Conditions> getConditionsCodec() {
		return KilledByArrowCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, Collection<Entity> piercingKilledEntities, @Nullable ItemStack weapon) {
		List<LootContext> list = Lists.<LootContext>newArrayList();
		Set<EntityType<?>> set = Sets.<EntityType<?>>newHashSet();

		for (Entity entity : piercingKilledEntities) {
			set.add(entity.getType());
			list.add(EntityPredicate.createAdvancementEntityLootContext(player, entity));
		}

		this.trigger(player, conditions -> conditions.matches(list, set.size(), weapon));
	}

	public static record Conditions(
		Optional<LootContextPredicate> player, List<LootContextPredicate> victims, NumberRange.IntRange uniqueEntityTypes, Optional<ItemPredicate> firedFromWeapon
	) implements AbstractCriterion.Conditions {
		public static final Codec<KilledByArrowCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(KilledByArrowCriterion.Conditions::player),
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.listOf().optionalFieldOf("victims", List.of()).forGetter(KilledByArrowCriterion.Conditions::victims),
						NumberRange.IntRange.CODEC
							.optionalFieldOf("unique_entity_types", NumberRange.IntRange.ANY)
							.forGetter(KilledByArrowCriterion.Conditions::uniqueEntityTypes),
						ItemPredicate.CODEC.optionalFieldOf("fired_from_weapon").forGetter(KilledByArrowCriterion.Conditions::firedFromWeapon)
					)
					.apply(instance, KilledByArrowCriterion.Conditions::new)
		);

		public static AdvancementCriterion<KilledByArrowCriterion.Conditions> createCrossbow(
			RegistryEntryLookup<Item> itemRegistry, EntityPredicate.Builder... victims
		) {
			return Criteria.KILLED_BY_ARROW
				.create(
					new KilledByArrowCriterion.Conditions(
						Optional.empty(),
						EntityPredicate.contextPredicateFromEntityPredicates(victims),
						NumberRange.IntRange.ANY,
						Optional.of(ItemPredicate.Builder.create().items(itemRegistry, Items.CROSSBOW).build())
					)
				);
		}

		public static AdvancementCriterion<KilledByArrowCriterion.Conditions> createCrossbow(
			RegistryEntryLookup<Item> itemRegistry, NumberRange.IntRange uniqueEntityTypeCount
		) {
			return Criteria.KILLED_BY_ARROW
				.create(
					new KilledByArrowCriterion.Conditions(
						Optional.empty(), List.of(), uniqueEntityTypeCount, Optional.of(ItemPredicate.Builder.create().items(itemRegistry, Items.CROSSBOW).build())
					)
				);
		}

		public boolean matches(Collection<LootContext> victimContexts, int uniqueEntityTypeCount, @Nullable ItemStack weapon) {
			if (!this.firedFromWeapon.isPresent() || weapon != null && ((ItemPredicate)this.firedFromWeapon.get()).test(weapon)) {
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
			} else {
				return false;
			}
		}

		@Override
		public void validate(LootContextPredicateValidator validator) {
			AbstractCriterion.Conditions.super.validate(validator);
			validator.validateEntityPredicates(this.victims, ".victims");
		}
	}
}
