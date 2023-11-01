package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class BredAnimalsCriterion extends AbstractCriterion<BredAnimalsCriterion.Conditions> {
	public BredAnimalsCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Optional<LootContextPredicate> optional2 = EntityPredicate.contextPredicateFromJson(jsonObject, "parent", advancementEntityPredicateDeserializer);
		Optional<LootContextPredicate> optional3 = EntityPredicate.contextPredicateFromJson(jsonObject, "partner", advancementEntityPredicateDeserializer);
		Optional<LootContextPredicate> optional4 = EntityPredicate.contextPredicateFromJson(jsonObject, "child", advancementEntityPredicateDeserializer);
		return new BredAnimalsCriterion.Conditions(optional, optional2, optional3, optional4);
	}

	public void trigger(ServerPlayerEntity player, AnimalEntity parent, AnimalEntity partner, @Nullable PassiveEntity child) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, parent);
		LootContext lootContext2 = EntityPredicate.createAdvancementEntityLootContext(player, partner);
		LootContext lootContext3 = child != null ? EntityPredicate.createAdvancementEntityLootContext(player, child) : null;
		this.trigger(player, conditions -> conditions.matches(lootContext, lootContext2, lootContext3));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Optional<LootContextPredicate> parent;
		private final Optional<LootContextPredicate> partner;
		private final Optional<LootContextPredicate> child;

		public Conditions(
			Optional<LootContextPredicate> playerPredicate,
			Optional<LootContextPredicate> parentPredicate,
			Optional<LootContextPredicate> partnerPredicate,
			Optional<LootContextPredicate> childPredicate
		) {
			super(playerPredicate);
			this.parent = parentPredicate;
			this.partner = partnerPredicate;
			this.child = childPredicate;
		}

		public static AdvancementCriterion<BredAnimalsCriterion.Conditions> any() {
			return Criteria.BRED_ANIMALS.create(new BredAnimalsCriterion.Conditions(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
		}

		public static AdvancementCriterion<BredAnimalsCriterion.Conditions> create(EntityPredicate.Builder child) {
			return Criteria.BRED_ANIMALS
				.create(
					new BredAnimalsCriterion.Conditions(
						Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(child))
					)
				);
		}

		public static AdvancementCriterion<BredAnimalsCriterion.Conditions> create(
			Optional<EntityPredicate> parent, Optional<EntityPredicate> partner, Optional<EntityPredicate> child
		) {
			return Criteria.BRED_ANIMALS
				.create(
					new BredAnimalsCriterion.Conditions(
						Optional.empty(),
						EntityPredicate.contextPredicateFromEntityPredicate(parent),
						EntityPredicate.contextPredicateFromEntityPredicate(partner),
						EntityPredicate.contextPredicateFromEntityPredicate(child)
					)
				);
		}

		public boolean matches(LootContext parentContext, LootContext partnerContext, @Nullable LootContext childContext) {
			return !this.child.isPresent() || childContext != null && ((LootContextPredicate)this.child.get()).test(childContext)
				? parentMatches(this.parent, parentContext) && parentMatches(this.partner, partnerContext)
					|| parentMatches(this.parent, partnerContext) && parentMatches(this.partner, parentContext)
				: false;
		}

		private static boolean parentMatches(Optional<LootContextPredicate> parent, LootContext parentContext) {
			return parent.isEmpty() || ((LootContextPredicate)parent.get()).test(parentContext);
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			this.parent.ifPresent(parent -> jsonObject.add("parent", parent.toJson()));
			this.partner.ifPresent(partner -> jsonObject.add("partner", partner.toJson()));
			this.child.ifPresent(child -> jsonObject.add("child", child.toJson()));
			return jsonObject;
		}
	}
}
