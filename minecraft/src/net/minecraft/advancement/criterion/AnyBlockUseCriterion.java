package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
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

public class AnyBlockUseCriterion extends AbstractCriterion<AnyBlockUseCriterion.Conditions> {
	@Override
	public Codec<AnyBlockUseCriterion.Conditions> getConditionsCodec() {
		return AnyBlockUseCriterion.Conditions.CODEC;
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
		public static final Codec<AnyBlockUseCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(AnyBlockUseCriterion.Conditions::player),
						LootContextPredicate.CODEC.optionalFieldOf("location").forGetter(AnyBlockUseCriterion.Conditions::location)
					)
					.apply(instance, AnyBlockUseCriterion.Conditions::new)
		);

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
