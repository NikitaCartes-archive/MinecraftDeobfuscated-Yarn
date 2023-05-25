package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.LocationCheckLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ItemCriterion extends AbstractCriterion<ItemCriterion.Conditions> {
	final Identifier id;

	public ItemCriterion(Identifier id) {
		this.id = id;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	public ItemCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		LootContextPredicate lootContextPredicate2 = LootContextPredicate.fromJson(
			"location", advancementEntityPredicateDeserializer, jsonObject.get("location"), LootContextTypes.ADVANCEMENT_LOCATION
		);
		if (lootContextPredicate2 == null) {
			throw new JsonParseException("Failed to parse 'location' field");
		} else {
			return new ItemCriterion.Conditions(this.id, lootContextPredicate, lootContextPredicate2);
		}
	}

	public void trigger(ServerPlayerEntity player, BlockPos pos, ItemStack stack) {
		ServerWorld serverWorld = player.getServerWorld();
		BlockState blockState = serverWorld.getBlockState(pos);
		LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(serverWorld)
			.add(LootContextParameters.ORIGIN, pos.toCenterPos())
			.add(LootContextParameters.THIS_ENTITY, player)
			.add(LootContextParameters.BLOCK_STATE, blockState)
			.add(LootContextParameters.TOOL, stack)
			.build(LootContextTypes.ADVANCEMENT_LOCATION);
		LootContext lootContext = new LootContext.Builder(lootContextParameterSet).build(null);
		this.trigger(player, conditions -> conditions.test(lootContext));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final LootContextPredicate location;

		public Conditions(Identifier id, LootContextPredicate entity, LootContextPredicate location) {
			super(id, entity);
			this.location = location;
		}

		public static ItemCriterion.Conditions createPlacedBlock(Block block) {
			LootContextPredicate lootContextPredicate = LootContextPredicate.create(BlockStatePropertyLootCondition.builder(block).build());
			return new ItemCriterion.Conditions(Criteria.PLACED_BLOCK.id, LootContextPredicate.EMPTY, lootContextPredicate);
		}

		public static ItemCriterion.Conditions createPlacedBlock(LootCondition.Builder... locationConditions) {
			LootContextPredicate lootContextPredicate = LootContextPredicate.create(
				(LootCondition[])Arrays.stream(locationConditions).map(LootCondition.Builder::build).toArray(LootCondition[]::new)
			);
			return new ItemCriterion.Conditions(Criteria.PLACED_BLOCK.id, LootContextPredicate.EMPTY, lootContextPredicate);
		}

		private static ItemCriterion.Conditions create(LocationPredicate.Builder location, ItemPredicate.Builder item, Identifier id) {
			LootContextPredicate lootContextPredicate = LootContextPredicate.create(
				LocationCheckLootCondition.builder(location).build(), MatchToolLootCondition.builder(item).build()
			);
			return new ItemCriterion.Conditions(id, LootContextPredicate.EMPTY, lootContextPredicate);
		}

		public static ItemCriterion.Conditions createItemUsedOnBlock(LocationPredicate.Builder location, ItemPredicate.Builder item) {
			return create(location, item, Criteria.ITEM_USED_ON_BLOCK.id);
		}

		public static ItemCriterion.Conditions createAllayDropItemOnBlock(LocationPredicate.Builder location, ItemPredicate.Builder item) {
			return create(location, item, Criteria.ALLAY_DROP_ITEM_ON_BLOCK.id);
		}

		public boolean test(LootContext context) {
			return this.location.test(context);
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("location", this.location.toJson(predicateSerializer));
			return jsonObject;
		}
	}
}
