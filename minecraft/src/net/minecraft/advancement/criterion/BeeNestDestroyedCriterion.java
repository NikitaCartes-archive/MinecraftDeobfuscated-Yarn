package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;

public class BeeNestDestroyedCriterion extends AbstractCriterion<BeeNestDestroyedCriterion.Conditions> {
	@Override
	public Codec<BeeNestDestroyedCriterion.Conditions> getConditionsCodec() {
		return BeeNestDestroyedCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, BlockState state, ItemStack stack, int beeCount) {
		this.trigger(player, conditions -> conditions.test(state, stack, beeCount));
	}

	public static record Conditions(
		Optional<LootContextPredicate> player, Optional<RegistryEntry<Block>> block, Optional<ItemPredicate> item, NumberRange.IntRange beesInside
	) implements AbstractCriterion.Conditions {
		public static final Codec<BeeNestDestroyedCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(BeeNestDestroyedCriterion.Conditions::player),
						Registries.BLOCK.getEntryCodec().optionalFieldOf("block").forGetter(BeeNestDestroyedCriterion.Conditions::block),
						ItemPredicate.CODEC.optionalFieldOf("item").forGetter(BeeNestDestroyedCriterion.Conditions::item),
						NumberRange.IntRange.CODEC.optionalFieldOf("num_bees_inside", NumberRange.IntRange.ANY).forGetter(BeeNestDestroyedCriterion.Conditions::beesInside)
					)
					.apply(instance, BeeNestDestroyedCriterion.Conditions::new)
		);

		public static AdvancementCriterion<BeeNestDestroyedCriterion.Conditions> create(
			Block block, ItemPredicate.Builder itemPredicateBuilder, NumberRange.IntRange beeCountRange
		) {
			return Criteria.BEE_NEST_DESTROYED
				.create(
					new BeeNestDestroyedCriterion.Conditions(Optional.empty(), Optional.of(block.getRegistryEntry()), Optional.of(itemPredicateBuilder.build()), beeCountRange)
				);
		}

		public boolean test(BlockState state, ItemStack stack, int count) {
			if (this.block.isPresent() && !state.isOf((RegistryEntry<Block>)this.block.get())) {
				return false;
			} else {
				return this.item.isPresent() && !((ItemPredicate)this.item.get()).test(stack) ? false : this.beesInside.test(count);
			}
		}
	}
}
