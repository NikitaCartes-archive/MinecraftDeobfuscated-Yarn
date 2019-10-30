package net.minecraft.item;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public interface ItemPropertyGetter {
	@Environment(EnvType.CLIENT)
	float call(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity);
}
