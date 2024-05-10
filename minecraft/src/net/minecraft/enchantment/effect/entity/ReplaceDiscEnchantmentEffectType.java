package net.minecraft.enchantment.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentLevelBasedValueType;
import net.minecraft.enchantment.effect.EnchantmentEntityEffectType;
import net.minecraft.entity.Entity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record ReplaceDiscEnchantmentEffectType(
	EnchantmentLevelBasedValueType radius,
	EnchantmentLevelBasedValueType height,
	Vec3i offset,
	Optional<BlockPredicate> predicate,
	BlockStateProvider blockState,
	Optional<RegistryEntry<GameEvent>> triggerGameEvent
) implements EnchantmentEntityEffectType {
	public static final MapCodec<ReplaceDiscEnchantmentEffectType> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					EnchantmentLevelBasedValueType.CODEC.fieldOf("radius").forGetter(ReplaceDiscEnchantmentEffectType::radius),
					EnchantmentLevelBasedValueType.CODEC.fieldOf("height").forGetter(ReplaceDiscEnchantmentEffectType::height),
					Vec3i.CODEC.optionalFieldOf("offset", Vec3i.ZERO).forGetter(ReplaceDiscEnchantmentEffectType::offset),
					BlockPredicate.BASE_CODEC.optionalFieldOf("predicate").forGetter(ReplaceDiscEnchantmentEffectType::predicate),
					BlockStateProvider.TYPE_CODEC.fieldOf("block_state").forGetter(ReplaceDiscEnchantmentEffectType::blockState),
					GameEvent.field_51910.optionalFieldOf("trigger_game_event").forGetter(ReplaceDiscEnchantmentEffectType::triggerGameEvent)
				)
				.apply(instance, ReplaceDiscEnchantmentEffectType::new)
	);

	@Override
	public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
		BlockPos blockPos = BlockPos.ofFloored(pos).add(this.offset);
		Random random = user.getRandom();
		int i = (int)this.radius.getValue(level);
		int j = (int)this.height.getValue(level);

		for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-i, 0, -i), blockPos.add(i, Math.min(j - 1, 0), i))) {
			if (blockPos2.getSquaredDistanceFromCenter(pos.getX(), (double)blockPos2.getY() + 0.5, pos.getZ()) < (double)MathHelper.square(i)
				&& (Boolean)this.predicate.map(predicate -> predicate.test(world, blockPos2)).orElse(true)
				&& world.setBlockState(blockPos2, this.blockState.get(random, blockPos2))) {
				this.triggerGameEvent.ifPresent(registryEntry -> world.emitGameEvent(user, registryEntry, blockPos2));
			}
		}
	}

	@Override
	public MapCodec<ReplaceDiscEnchantmentEffectType> getCodec() {
		return CODEC;
	}
}
