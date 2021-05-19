package net.minecraft.client.item;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public interface UnclampedModelPredicateProvider extends ModelPredicateProvider {
	@Deprecated
	@Override
	default float call(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i) {
		return MathHelper.clamp(this.unclampedCall(itemStack, clientWorld, livingEntity, i), 0.0F, 1.0F);
	}

	float unclampedCall(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed);
}
