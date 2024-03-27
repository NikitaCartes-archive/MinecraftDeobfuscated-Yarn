package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class DefaultBlockUseCriterion extends AbstractCriterion<DefaultBlockUseCriterion.Conditions> {
	@Override
	public Codec<DefaultBlockUseCriterion.Conditions> getConditionsCodec() {
		return DefaultBlockUseCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, BlockPos pos) {
		ServerWorld serverWorld = player.getServerWorld();
		BlockState blockState = serverWorld.getBlockState(pos);
		LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(serverWorld)
			.add(LootContextParameters.ORIGIN, pos.toCenterPos())
			.add(LootContextParameters.THIS_ENTITY, player)
			.add(LootContextParameters.BLOCK_STATE, blockState)
			.build(LootContextTypes.BLOCK_USE);
		LootContext lootContext = new LootContext.Builder(lootContextParameterSet).build(Optional.empty());
		this.trigger(player, conditions -> conditions.test(lootContext));
	}

	public static record Conditions(Optional<LootContextPredicate> player, Optional<LootContextPredicate> location) implements AbstractCriterion.Conditions {
		public static final Codec<DefaultBlockUseCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(DefaultBlockUseCriterion.Conditions::player),
						LootContextPredicate.CODEC.optionalFieldOf("location").forGetter(DefaultBlockUseCriterion.Conditions::location)
					)
					.apply(instance, DefaultBlockUseCriterion.Conditions::new)
		);

		public boolean test(LootContext location) {
			return this.location.isEmpty() || ((LootContextPredicate)this.location.get()).test(location);
		}

		@Override
		public void validate(LootContextPredicateValidator validator) {
			AbstractCriterion.Conditions.super.validate(validator);
			this.location.ifPresent(location -> validator.validate(location, LootContextTypes.BLOCK_USE, ".location"));
		}
	}
}
