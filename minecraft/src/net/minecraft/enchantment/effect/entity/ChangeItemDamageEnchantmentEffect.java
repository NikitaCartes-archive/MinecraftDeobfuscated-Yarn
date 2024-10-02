package net.minecraft.enchantment.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public record ChangeItemDamageEnchantmentEffect(EnchantmentLevelBasedValue amount) implements EnchantmentEntityEffect {
	public static final MapCodec<ChangeItemDamageEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					EnchantmentLevelBasedValue.CODEC.fieldOf("amount").forGetter(changeItemDamageEnchantmentEffect -> changeItemDamageEnchantmentEffect.amount)
				)
				.apply(instance, ChangeItemDamageEnchantmentEffect::new)
	);

	@Override
	public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
		ItemStack itemStack = context.stack();
		if (itemStack.contains(DataComponentTypes.MAX_DAMAGE) && itemStack.contains(DataComponentTypes.DAMAGE)) {
			ServerPlayerEntity serverPlayerEntity2 = context.owner() instanceof ServerPlayerEntity serverPlayerEntity ? serverPlayerEntity : null;
			int i = (int)this.amount.getValue(level);
			itemStack.damage(i, world, serverPlayerEntity2, context.breakCallback());
		}
	}

	@Override
	public MapCodec<ChangeItemDamageEnchantmentEffect> getCodec() {
		return CODEC;
	}
}
