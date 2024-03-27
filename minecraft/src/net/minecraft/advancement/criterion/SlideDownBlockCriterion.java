package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;

public class SlideDownBlockCriterion extends AbstractCriterion<SlideDownBlockCriterion.Conditions> {
	@Override
	public Codec<SlideDownBlockCriterion.Conditions> getConditionsCodec() {
		return SlideDownBlockCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, BlockState state) {
		this.trigger(player, conditions -> conditions.test(state));
	}

	public static record Conditions(Optional<LootContextPredicate> player, Optional<RegistryEntry<Block>> block, Optional<StatePredicate> state)
		implements AbstractCriterion.Conditions {
		public static final Codec<SlideDownBlockCriterion.Conditions> CODEC = RecordCodecBuilder.<SlideDownBlockCriterion.Conditions>create(
				instance -> instance.group(
							EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(SlideDownBlockCriterion.Conditions::player),
							Registries.BLOCK.getEntryCodec().optionalFieldOf("block").forGetter(SlideDownBlockCriterion.Conditions::block),
							StatePredicate.CODEC.optionalFieldOf("state").forGetter(SlideDownBlockCriterion.Conditions::state)
						)
						.apply(instance, SlideDownBlockCriterion.Conditions::new)
			)
			.validate(SlideDownBlockCriterion.Conditions::validate);

		private static DataResult<SlideDownBlockCriterion.Conditions> validate(SlideDownBlockCriterion.Conditions conditions) {
			return (DataResult<SlideDownBlockCriterion.Conditions>)conditions.block
				.flatMap(
					block -> conditions.state
							.flatMap(state -> state.findMissing(((Block)block.value()).getStateManager()))
							.map(property -> DataResult.error(() -> "Block" + block + " has no property " + property))
				)
				.orElseGet(() -> DataResult.success(conditions));
		}

		public static AdvancementCriterion<SlideDownBlockCriterion.Conditions> create(Block block) {
			return Criteria.SLIDE_DOWN_BLOCK.create(new SlideDownBlockCriterion.Conditions(Optional.empty(), Optional.of(block.getRegistryEntry()), Optional.empty()));
		}

		public boolean test(BlockState state) {
			return this.block.isPresent() && !state.isOf((RegistryEntry<Block>)this.block.get())
				? false
				: !this.state.isPresent() || ((StatePredicate)this.state.get()).test(state);
		}
	}
}
