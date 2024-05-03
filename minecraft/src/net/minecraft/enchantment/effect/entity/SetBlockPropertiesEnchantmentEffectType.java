package net.minecraft.enchantment.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.effect.EnchantmentEntityEffectType;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public record SetBlockPropertiesEnchantmentEffectType(BlockStateComponent properties, Vec3i offset) implements EnchantmentEntityEffectType {
	public static final MapCodec<SetBlockPropertiesEnchantmentEffectType> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					BlockStateComponent.CODEC.fieldOf("properties").forGetter(SetBlockPropertiesEnchantmentEffectType::properties),
					Vec3i.CODEC.optionalFieldOf("offset", Vec3i.ZERO).forGetter(SetBlockPropertiesEnchantmentEffectType::offset)
				)
				.apply(instance, SetBlockPropertiesEnchantmentEffectType::new)
	);

	public SetBlockPropertiesEnchantmentEffectType(BlockStateComponent properties) {
		this(properties, Vec3i.ZERO);
	}

	@Override
	public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
		BlockPos blockPos = BlockPos.ofFloored(pos).add(this.offset);
		BlockState blockState = user.getWorld().getBlockState(blockPos);
		BlockState blockState2 = this.properties.applyToState(blockState);
		if (!blockState.equals(blockState2)) {
			user.getWorld().setBlockState(blockPos, blockState2, Block.NOTIFY_ALL);
		}
	}

	@Override
	public MapCodec<SetBlockPropertiesEnchantmentEffectType> getCodec() {
		return CODEC;
	}
}
