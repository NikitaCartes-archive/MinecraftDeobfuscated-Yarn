package net.minecraft.enchantment.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.effect.EnchantmentEntityEffectType;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record ReplaceBlockEnchantmentEffectType(Vec3i offset, Optional<BlockPredicate> predicate, BlockStateProvider blockState)
	implements EnchantmentEntityEffectType {
	public static final MapCodec<ReplaceBlockEnchantmentEffectType> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Vec3i.CODEC.optionalFieldOf("offset", Vec3i.ZERO).forGetter(ReplaceBlockEnchantmentEffectType::offset),
					BlockPredicate.BASE_CODEC.optionalFieldOf("predicate").forGetter(ReplaceBlockEnchantmentEffectType::predicate),
					BlockStateProvider.TYPE_CODEC.fieldOf("block_state").forGetter(ReplaceBlockEnchantmentEffectType::blockState)
				)
				.apply(instance, ReplaceBlockEnchantmentEffectType::new)
	);

	@Override
	public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
		BlockPos blockPos = BlockPos.ofFloored(pos).add(this.offset);
		if ((Boolean)this.predicate.map(blockPredicate -> blockPredicate.test(world, blockPos)).orElse(true)) {
			world.setBlockState(blockPos, this.blockState.get(user.getRandom(), blockPos));
		}
	}

	@Override
	public MapCodec<ReplaceBlockEnchantmentEffectType> getCodec() {
		return CODEC;
	}
}
