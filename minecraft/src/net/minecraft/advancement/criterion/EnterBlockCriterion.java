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

public class EnterBlockCriterion extends AbstractCriterion<EnterBlockCriterion.Conditions> {
	@Override
	public Codec<EnterBlockCriterion.Conditions> getConditionsCodec() {
		return EnterBlockCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, BlockState state) {
		this.trigger(player, conditions -> conditions.matches(state));
	}

	public static record Conditions(Optional<LootContextPredicate> player, Optional<RegistryEntry<Block>> block, Optional<StatePredicate> state)
		implements AbstractCriterion.Conditions {
		public static final Codec<EnterBlockCriterion.Conditions> CODEC = RecordCodecBuilder.<EnterBlockCriterion.Conditions>create(
				instance -> instance.group(
							EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(EnterBlockCriterion.Conditions::player),
							Registries.BLOCK.getEntryCodec().optionalFieldOf("block").forGetter(EnterBlockCriterion.Conditions::block),
							StatePredicate.CODEC.optionalFieldOf("state").forGetter(EnterBlockCriterion.Conditions::state)
						)
						.apply(instance, EnterBlockCriterion.Conditions::new)
			)
			.validate(EnterBlockCriterion.Conditions::validate);

		private static DataResult<EnterBlockCriterion.Conditions> validate(EnterBlockCriterion.Conditions conditions) {
			return (DataResult<EnterBlockCriterion.Conditions>)conditions.block
				.flatMap(
					block -> conditions.state
							.flatMap(state -> state.findMissing(((Block)block.value()).getStateManager()))
							.map(property -> DataResult.error(() -> "Block" + block + " has no property " + property))
				)
				.orElseGet(() -> DataResult.success(conditions));
		}

		public static AdvancementCriterion<EnterBlockCriterion.Conditions> block(Block block) {
			return Criteria.ENTER_BLOCK.create(new EnterBlockCriterion.Conditions(Optional.empty(), Optional.of(block.getRegistryEntry()), Optional.empty()));
		}

		public boolean matches(BlockState state) {
			return this.block.isPresent() && !state.isOf((RegistryEntry<Block>)this.block.get())
				? false
				: !this.state.isPresent() || ((StatePredicate)this.state.get()).test(state);
		}
	}
}
