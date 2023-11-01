package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Arrays;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
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
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class ItemCriterion extends AbstractCriterion<ItemCriterion.Conditions> {
	public ItemCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Optional<Optional<LootContextPredicate>> optional2 = LootContextPredicate.fromJson(
			"location", advancementEntityPredicateDeserializer, jsonObject.get("location"), LootContextTypes.ADVANCEMENT_LOCATION
		);
		if (optional2.isEmpty()) {
			throw new JsonParseException("Failed to parse 'location' field");
		} else {
			return new ItemCriterion.Conditions(optional, (Optional<LootContextPredicate>)optional2.get());
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
		LootContext lootContext = new LootContext.Builder(lootContextParameterSet).build(Optional.empty());
		this.trigger(player, conditions -> conditions.test(lootContext));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Optional<LootContextPredicate> location;

		public Conditions(Optional<LootContextPredicate> playerPredicate, Optional<LootContextPredicate> location) {
			super(playerPredicate);
			this.location = location;
		}

		public static AdvancementCriterion<ItemCriterion.Conditions> createPlacedBlock(Block block) {
			LootContextPredicate lootContextPredicate = LootContextPredicate.create(BlockStatePropertyLootCondition.builder(block).build());
			return Criteria.PLACED_BLOCK.create(new ItemCriterion.Conditions(Optional.empty(), Optional.of(lootContextPredicate)));
		}

		public static AdvancementCriterion<ItemCriterion.Conditions> createPlacedBlock(LootCondition.Builder... locationConditions) {
			LootContextPredicate lootContextPredicate = LootContextPredicate.create(
				(LootCondition[])Arrays.stream(locationConditions).map(LootCondition.Builder::build).toArray(LootCondition[]::new)
			);
			return Criteria.PLACED_BLOCK.create(new ItemCriterion.Conditions(Optional.empty(), Optional.of(lootContextPredicate)));
		}

		private static ItemCriterion.Conditions create(LocationPredicate.Builder location, ItemPredicate.Builder item) {
			LootContextPredicate lootContextPredicate = LootContextPredicate.create(
				LocationCheckLootCondition.builder(location).build(), MatchToolLootCondition.builder(item).build()
			);
			return new ItemCriterion.Conditions(Optional.empty(), Optional.of(lootContextPredicate));
		}

		public static AdvancementCriterion<ItemCriterion.Conditions> createItemUsedOnBlock(LocationPredicate.Builder location, ItemPredicate.Builder item) {
			return Criteria.ITEM_USED_ON_BLOCK.create(create(location, item));
		}

		public static AdvancementCriterion<ItemCriterion.Conditions> createAllayDropItemOnBlock(LocationPredicate.Builder location, ItemPredicate.Builder item) {
			return Criteria.ALLAY_DROP_ITEM_ON_BLOCK.create(create(location, item));
		}

		public boolean test(LootContext location) {
			return this.location.isEmpty() || ((LootContextPredicate)this.location.get()).test(location);
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			this.location.ifPresent(location -> jsonObject.add("location", location.toJson()));
			return jsonObject;
		}
	}
}
