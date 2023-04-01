package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.entity.PlayerPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;

public class TickCriterion extends AbstractCriterion<TickCriterion.Conditions> {
	final Identifier id;

	public TickCriterion(Identifier id) {
		this.id = id;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	public TickCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		return new TickCriterion.Conditions(this.id, extended);
	}

	public void trigger(ServerPlayerEntity player) {
		this.trigger(player, conditions -> true);
	}

	public static class Conditions extends AbstractCriterionConditions {
		public Conditions(Identifier identifier, EntityPredicate.Extended extended) {
			super(identifier, extended);
		}

		public static TickCriterion.Conditions createLocation(LocationPredicate location) {
			return new TickCriterion.Conditions(Criteria.LOCATION.id, EntityPredicate.Extended.ofLegacy(EntityPredicate.Builder.create().location(location).build()));
		}

		public static TickCriterion.Conditions createLocation(EntityPredicate entity) {
			return new TickCriterion.Conditions(Criteria.LOCATION.id, EntityPredicate.Extended.ofLegacy(entity));
		}

		public static TickCriterion.Conditions createSleptInBed() {
			return new TickCriterion.Conditions(Criteria.SLEPT_IN_BED.id, EntityPredicate.Extended.EMPTY);
		}

		public static TickCriterion.Conditions createHeroOfTheVillage() {
			return new TickCriterion.Conditions(Criteria.HERO_OF_THE_VILLAGE.id, EntityPredicate.Extended.EMPTY);
		}

		public static TickCriterion.Conditions createAvoidVibration() {
			return new TickCriterion.Conditions(Criteria.AVOID_VIBRATION.id, EntityPredicate.Extended.EMPTY);
		}

		public static TickCriterion.Conditions createTick() {
			return new TickCriterion.Conditions(Criteria.TICK.id, EntityPredicate.Extended.EMPTY);
		}

		public static TickCriterion.Conditions createVoted(NumberRange.IntRange voteCount) {
			return new TickCriterion.Conditions(
				Criteria.VOTED.id,
				EntityPredicate.Extended.ofLegacy(
					EntityPredicate.Builder.create().typeSpecific(PlayerPredicate.Builder.create().stat(Stats.CUSTOM.getOrCreateStat(Stats.VOTES), voteCount).build()).build()
				)
			);
		}

		public static TickCriterion.Conditions createLocation(Block block, Item item) {
			return createLocation(
				EntityPredicate.Builder.create()
					.equipment(EntityEquipmentPredicate.Builder.create().feet(ItemPredicate.Builder.create().items(item).build()).build())
					.steppingOn(LocationPredicate.Builder.create().block(BlockPredicate.Builder.create().blocks(block).build()).build())
					.build()
			);
		}
	}
}
