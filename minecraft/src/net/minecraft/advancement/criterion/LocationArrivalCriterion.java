package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class LocationArrivalCriterion extends AbstractCriterion<LocationArrivalCriterion.Conditions> {
	final Identifier id;

	public LocationArrivalCriterion(Identifier id) {
		this.id = id;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	public LocationArrivalCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "location", jsonObject);
		LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject2);
		return new LocationArrivalCriterion.Conditions(this.id, extended, locationPredicate);
	}

	public void trigger(ServerPlayerEntity player) {
		this.test(player, conditions -> conditions.matches(player.getServerWorld(), player.getX(), player.getY(), player.getZ()));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final LocationPredicate location;

		public Conditions(Identifier id, EntityPredicate.Extended player, LocationPredicate location) {
			super(id, player);
			this.location = location;
		}

		public static LocationArrivalCriterion.Conditions create(LocationPredicate location) {
			return new LocationArrivalCriterion.Conditions(Criteria.LOCATION.id, EntityPredicate.Extended.EMPTY, location);
		}

		public static LocationArrivalCriterion.Conditions create(EntityPredicate entity) {
			return new LocationArrivalCriterion.Conditions(Criteria.LOCATION.id, EntityPredicate.Extended.ofLegacy(entity), LocationPredicate.ANY);
		}

		public static LocationArrivalCriterion.Conditions createSleptInBed() {
			return new LocationArrivalCriterion.Conditions(Criteria.SLEPT_IN_BED.id, EntityPredicate.Extended.EMPTY, LocationPredicate.ANY);
		}

		public static LocationArrivalCriterion.Conditions createHeroOfTheVillage() {
			return new LocationArrivalCriterion.Conditions(Criteria.HERO_OF_THE_VILLAGE.id, EntityPredicate.Extended.EMPTY, LocationPredicate.ANY);
		}

		public static LocationArrivalCriterion.Conditions createSteppingOnWithBoots(Block block, Item boots) {
			return create(
				EntityPredicate.Builder.create()
					.equipment(EntityEquipmentPredicate.Builder.create().feet(ItemPredicate.Builder.create().items(boots).build()).build())
					.steppingOn(LocationPredicate.Builder.create().block(BlockPredicate.Builder.create().blocks(block).build()).build())
					.build()
			);
		}

		public boolean matches(ServerWorld world, double x, double y, double z) {
			return this.location.test(world, x, y, z);
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("location", this.location.toJson());
			return jsonObject;
		}
	}
}
