package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class TickCriterion extends AbstractCriterion<TickCriterion.Conditions> {
	public TickCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		return new TickCriterion.Conditions(optional);
	}

	public void trigger(ServerPlayerEntity player) {
		this.trigger(player, conditions -> true);
	}

	public static class Conditions extends AbstractCriterionConditions {
		public Conditions(Optional<LootContextPredicate> optional) {
			super(optional);
		}

		public static AdvancementCriterion<TickCriterion.Conditions> createLocation(LocationPredicate.Builder location) {
			return Criteria.LOCATION
				.create(new TickCriterion.Conditions(Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(EntityPredicate.Builder.create().location(location)))));
		}

		public static AdvancementCriterion<TickCriterion.Conditions> method_53788(EntityPredicate.Builder builder) {
			return Criteria.LOCATION.create(new TickCriterion.Conditions(Optional.of(EntityPredicate.asLootContextPredicate(builder.build()))));
		}

		public static AdvancementCriterion<TickCriterion.Conditions> createLocation(Optional<EntityPredicate> entity) {
			return Criteria.LOCATION.create(new TickCriterion.Conditions(EntityPredicate.contextPredicateFromEntityPredicate(entity)));
		}

		public static AdvancementCriterion<TickCriterion.Conditions> createSleptInBed() {
			return Criteria.SLEPT_IN_BED.create(new TickCriterion.Conditions(Optional.empty()));
		}

		public static AdvancementCriterion<TickCriterion.Conditions> createHeroOfTheVillage() {
			return Criteria.HERO_OF_THE_VILLAGE.create(new TickCriterion.Conditions(Optional.empty()));
		}

		public static AdvancementCriterion<TickCriterion.Conditions> createAvoidVibration() {
			return Criteria.AVOID_VIBRATION.create(new TickCriterion.Conditions(Optional.empty()));
		}

		public static AdvancementCriterion<TickCriterion.Conditions> createTick() {
			return Criteria.TICK.create(new TickCriterion.Conditions(Optional.empty()));
		}

		public static AdvancementCriterion<TickCriterion.Conditions> createLocation(Block block, Item item) {
			return method_53788(
				EntityPredicate.Builder.create()
					.equipment(EntityEquipmentPredicate.Builder.create().feet(ItemPredicate.Builder.create().items(item)))
					.steppingOn(LocationPredicate.Builder.create().block(BlockPredicate.Builder.create().blocks(block)))
			);
		}
	}
}
