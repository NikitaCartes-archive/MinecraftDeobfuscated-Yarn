package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class ItemCriterion extends AbstractCriterion<ItemCriterion.Conditions> {
	@Override
	public Codec<ItemCriterion.Conditions> getConditionsCodec() {
		return ItemCriterion.Conditions.CODEC;
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

	public static record Conditions(Optional<LootContextPredicate> player, Optional<LootContextPredicate> location) implements AbstractCriterion.Conditions {
		public static final Codec<ItemCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(ItemCriterion.Conditions::player),
						LootContextPredicate.CODEC.optionalFieldOf("location").forGetter(ItemCriterion.Conditions::location)
					)
					.apply(instance, ItemCriterion.Conditions::new)
		);

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
		public void validate(LootContextPredicateValidator validator) {
			AbstractCriterion.Conditions.super.validate(validator);
			this.location.ifPresent(location -> validator.validate(location, LootContextTypes.ADVANCEMENT_LOCATION, ".location"));
		}
	}
}
